package com.netbucket.studymate.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.netbucket.studymate.R;
import com.netbucket.studymate.utils.NetworkInfoUtility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FacultyMemberRegistrationStepTwoActivity extends AppCompatActivity {
    TextInputLayout mInstituteLayout;
    TextInputLayout mCourseLayout;
    TextInputLayout mIdLayout;
    TextInputLayout mBirthdayLayout;
    TextInputLayout mPhoneNumberLayout;
    AutoCompleteTextView mInstituteField;
    AutoCompleteTextView mCourseField;
    TextInputEditText mIdField;
    TextInputEditText mBirthdayField;
    TextInputEditText mPhoneNumberField;
    ImageView mBackButton;
    ImageView mRefreshButton;
    Button mNextButton;
    TextView mLoginActivityLink;
    MaterialDialog mProgressDialog;
    private String mFullName;
    private String mEmail;
    private String mRole;
    private String mGender;
    private String mPassword;
    private String mInstitute;
    private String mCourse;
    private String mId;
    private String mBirthday;
    private String mPhoneNumber;
    private ArrayList<String> mInstituteList;
    private ArrayList<String> mCourseList;
    private FirebaseFirestore mStore;

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_member_registration_step_two);

        mStore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        mFullName = intent.getStringExtra("fullName");
        mEmail = intent.getStringExtra("email");
        mRole = intent.getStringExtra("role");
        mGender = intent.getStringExtra("gender");
        mPassword = intent.getStringExtra("password");

        mInstituteLayout = findViewById(R.id.textInputLayout_select_institute);
        mCourseLayout = findViewById(R.id.textInputLayout_select_course);
        mIdLayout = findViewById(R.id.textInputLayout_faculty_id);
        mBirthdayLayout = findViewById(R.id.textInputLayout_birthday);
        mPhoneNumberLayout = findViewById(R.id.textInputLayout_phone_number);
        mInstituteField = findViewById(R.id.autoCompleteTextView_select_institute);
        mCourseField = findViewById(R.id.autoCompleteTextView_select_course);
        mIdField = findViewById(R.id.textInputEditText_faculty_id);
        mBirthdayField = findViewById(R.id.textInputEditText_birthday);
        mPhoneNumberField = findViewById(R.id.textInputEditText_phone_number);
        mBackButton = findViewById(R.id.imageView_back);
        mRefreshButton = findViewById(R.id.imageView_refresh);
        mNextButton = findViewById(R.id.button_next);
        mLoginActivityLink = findViewById(R.id.textView_login);

        mInstituteList = new ArrayList<>();
        fetchInstitutes();
        ArrayAdapter<String> collegeNameAdapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_popup_item, mInstituteList);
        mInstituteField.setAdapter(collegeNameAdapter);

        mCourseList = new ArrayList<>();
        ArrayAdapter<String> courseAdapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_popup_item, mCourseList);
        mCourseField.setAdapter(courseAdapter);

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.YEAR, 1970);
        long startYear = calendar.getTimeInMillis();
//        calendar.set(Calendar.YEAR, 2016);
//        long endYear = calendar.getTimeInMillis();
        long endYear = MaterialDatePicker.todayInUtcMilliseconds();

        CalendarConstraints.Builder calendarConstraintsBuilder = new CalendarConstraints.Builder();
        calendarConstraintsBuilder.setStart(startYear);
        calendarConstraintsBuilder.setEnd(endYear);

        MaterialDatePicker.Builder<Long> materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setTitleText("Select Your Birthday");
        materialDateBuilder.setCalendarConstraints(calendarConstraintsBuilder.build());
        final MaterialDatePicker<Long> materialDatePicker = materialDateBuilder.build();

        mInstituteField.addTextChangedListener(new ValidationWatcher(mInstituteField));
        mCourseField.addTextChangedListener(new ValidationWatcher(mCourseField));
        mIdField.addTextChangedListener(new ValidationWatcher(mIdField));
        mBirthdayField.addTextChangedListener(new ValidationWatcher(mBirthdayField));
        mPhoneNumberField.addTextChangedListener(new ValidationWatcher(mPhoneNumberField));

        mRefreshButton.setOnClickListener(view -> {
            if (mInstituteList.isEmpty()) {
                fetchInstitutes();
            }
        });

        mInstituteLayout.setOnClickListener(view -> mInstituteField.showDropDown());

        mCourseLayout.setOnClickListener(view -> mCourseField.showDropDown());

        mBirthdayField.setOnClickListener(view -> materialDatePicker.show(getSupportFragmentManager(), "Material Date Picker"));

        materialDatePicker.addOnPositiveButtonClickListener(selection -> mBirthdayField.setText(materialDatePicker.getHeaderText()));

        mBackButton.setOnClickListener(view -> onBackPressed());

        mNextButton.setOnClickListener(view -> {
            if ((validInstitute() != null) && (validCourse() != null) && (validId() != null) && (validBirthday() != null) && (validPhoneNo() != null)) {
                if (new NetworkInfoUtility(getApplicationContext()).isConnectedToInternet()) {
                    mProgressDialog = new MaterialDialog.Builder(FacultyMemberRegistrationStepTwoActivity.this)
                            .typeface(getResources().getFont(R.font.sf_ui_display_medium), getResources().getFont(R.font.sf_ui_display_regular))
                            .progress(true, 0)
                            .canceledOnTouchOutside(false)
                            .content(getResources().getString(R.string.content_dialog_please_wait))
                            .cancelable(false)
                            .canceledOnTouchOutside(false)
                            .build();
                    mProgressDialog.show();

                    Intent intent12 = new Intent(FacultyMemberRegistrationStepTwoActivity.this, FacultyMemberRegistrationStepThreeActivity.class);
                    intent12.putExtra("fullName", mFullName);
                    intent12.putExtra("email", mEmail);
                    intent12.putExtra("role", mRole);
                    intent12.putExtra("gender", mGender);
                    intent12.putExtra("password", mPassword);
                    intent12.putExtra("institute", mInstitute);
                    intent12.putExtra("course", mCourse);
                    intent12.putExtra("id", mId);
                    intent12.putExtra("birthday", mBirthday);
                    intent12.putExtra("phoneNumber", mPhoneNumber);
                    mProgressDialog.dismiss();
                    startActivity(intent12);
                } else {
                    mProgressDialog.dismiss();
                    new MaterialDialog.Builder(FacultyMemberRegistrationStepTwoActivity.this)
                            .typeface(getResources().getFont(R.font.sf_ui_display_medium), getResources().getFont(R.font.sf_ui_display_regular))
                            .title(R.string.title_dialog_no_internet)
                            .content(R.string.content_dialog_no_internet)
                            .icon(Objects.requireNonNull(getDrawable(R.drawable.ic_baseline_signal_wifi_off_24)))
                            .positiveText(R.string.positive_text_dialog_no_internet)
                            .negativeText(R.string.negative_text_dialog_no_internet)
                            .canceledOnTouchOutside(false)
                            .cancelable(false)
                            .onPositive((dialog, which) -> {
                                Intent intent12 = new Intent(getApplicationContext(), CommonRegistrationStepOneActivity.class);
                                startActivity(intent12);
                            })
                            .onNegative((dialog, which) -> {
                            })
                            .show();
                }
            }
        });

        mLoginActivityLink.setOnClickListener(view -> {
            Intent intent1 = new Intent(FacultyMemberRegistrationStepTwoActivity.this, LoginActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(FacultyMemberRegistrationStepTwoActivity.this, CommonRegistrationStepOneActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fetchInstitutes() {
        mProgressDialog = new MaterialDialog.Builder(FacultyMemberRegistrationStepTwoActivity.this)
                .typeface(getResources().getFont(R.font.sf_ui_display_medium), getResources().getFont(R.font.sf_ui_display_regular))
                .progress(true, 0)
                .canceledOnTouchOutside(false)
                .content(getResources().getString(R.string.content_dialog_retrieving_college_data))
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .build();
        mProgressDialog.show();

        mStore.collection("colleges")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot item : queryDocumentSnapshots) {
                        mInstituteList.add(item.getId());

                        Log.i("Colleges list:", item.getId());
                    }
                    mProgressDialog.dismiss();
                })
                .addOnFailureListener(e -> {
                    Log.e("Failed to get colleges:", Objects.requireNonNull(e.getMessage()));

                    mProgressDialog.dismiss();
                    fetchInstitutes();
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fetchCourses() {
//        mProgressDialog = new MaterialDialog.Builder(FacultyMemberRegistrationStepTwoActivity.this)
//                .typeface(getResources().getFont(R.font.sf_ui_display_medium), getResources().getFont(R.font.sf_ui_display_regular))
//                .progress(true, 0)
//                .canceledOnTouchOutside(false)
//                .content(getResources().getString(R.string.content_dialog_listing_courses))
//                .cancelable(false)
//                .canceledOnTouchOutside(false)
//                .build();
//        mProgressDialog.show();

        mStore.collection("/institutes/" + mInstitute + "/courses")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot item : queryDocumentSnapshots) {
                        mCourseList.add(item.getId());

                        Log.i("Courses list:", item.getId());
                    }
//                        mProgressDialog.dismiss();
                })
                .addOnFailureListener(e -> {
                    Log.e("Failed to get courses:", Objects.requireNonNull(e.getMessage()));
//                        mProgressDialog.dismiss();
                    fetchCourses();
                });
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String validInstitute() {
        String institute = Objects.requireNonNull(mInstituteLayout.getEditText()).getText().toString().trim();
        Pattern institutePattern = Pattern.compile("^\\p{L}+[\\p{L}\\p{Z}\\p{P}\\p{N}]{0,80}");
        Matcher instituteMatcher = institutePattern.matcher(institute);

        if (institute.isEmpty()) {
            mInstituteLayout.setError(getString(R.string.error_empty_college_name));
            requestFocus(mInstituteField);
            return null;
        } else {
            if (instituteMatcher.matches() && mInstituteList.contains(institute)) {
                mInstituteLayout.setErrorEnabled(false);
                mInstitute = institute;
                if (mInstituteList.contains(mInstitute)) {
                    mCourseList.clear();
                    fetchCourses();
                }
                return institute;
            } else {
                mInstituteLayout.setError(getString(R.string.error_invalid_college_name));
                requestFocus(mInstituteField);
                return null;
            }
        }
    }

    private String validCourse() {
        String course = Objects.requireNonNull(mCourseLayout.getEditText()).getText().toString().trim();
        Pattern coursePattern = Pattern.compile("^\\p{L}+[\\p{L}\\p{Z}\\p{P}\\p{N}]{0,60}");
        Matcher courseMatcher = coursePattern.matcher(course);

        if (course.isEmpty()) {
            mCourseLayout.setError(getString(R.string.error_empty_course));
            requestFocus(mCourseField);
            return null;
        } else {
            if (courseMatcher.matches()) {
                mCourseLayout.setErrorEnabled(false);
                mCourse = course;
                return course;
            } else {
                mCourseLayout.setError(getString(R.string.error_invalid_course));
                requestFocus(mCourseField);
                return null;
            }
        }
    }

    private String validId() {
        String id = Objects.requireNonNull(mIdLayout.getEditText()).getText().toString().trim();
        Pattern idPattern = Pattern.compile("[a-zA-Z0-9._:\\p{Pd}]{1,20}");
        Matcher idMatcher = idPattern.matcher(id);

        if (id.isEmpty()) {
            mIdLayout.setError(getString(R.string.error_empty_roll_no_or_id));
            requestFocus(mIdField);
            return null;
        } else {
            if (idMatcher.matches()) {
                mIdLayout.setErrorEnabled(false);
                mId = id;
                return id;
            } else {
                mIdLayout.setError(getString(R.string.error_invalid_roll_no_or_id));
                requestFocus(mIdField);
                return null;
            }
        }
    }

    private String validBirthday() {
        String birthday = Objects.requireNonNull(mBirthdayLayout.getEditText()).getText().toString().trim();

        if (birthday.isEmpty()) {
            mBirthdayLayout.setError(getString(R.string.error_empty_birthday));
            requestFocus(mBirthdayField);
            return null;
        } else {
            mBirthday = birthday;
            return birthday;
        }
    }

    private String validPhoneNo() {
        String phoneNumber = Objects.requireNonNull(mPhoneNumberLayout.getEditText()).getText().toString().trim();
        Pattern phoneNumberPattern = Pattern.compile("^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"
                + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$"
                + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$");
        Matcher phoneNumberMatcher = phoneNumberPattern.matcher(phoneNumber);

        if (phoneNumber.isEmpty()) {
            mPhoneNumberLayout.setError(getString(R.string.error_empty_phone_number));
            requestFocus(mPhoneNumberField);
            return null;
        } else {
            if (phoneNumberMatcher.matches()) {
                mPhoneNumberLayout.setErrorEnabled(false);
                mPhoneNumber = phoneNumber;
                return phoneNumber;
            } else {
                mPhoneNumberLayout.setError(getString(R.string.error_invalid_phone_number));
                requestFocus(mPhoneNumberField);
                return null;
            }
        }
    }

    private class ValidationWatcher implements TextWatcher {

        private final View view;

        private ValidationWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @SuppressLint("NonConstantResourceId")
        @RequiresApi(api = Build.VERSION_CODES.O)
        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.autoCompleteTextView_select_institute:
                    validInstitute();
                    break;
                case R.id.autoCompleteTextView_select_course:
                    validCourse();
                    break;
                case R.id.textInputEditText_faculty_id:
                    validId();
                    break;
                case R.id.textInputEditText_birthday:
                    validBirthday();
                    break;
                case R.id.textInputEditText_phone_number:
                    validPhoneNo();
                    break;
            }
        }
    }
}
