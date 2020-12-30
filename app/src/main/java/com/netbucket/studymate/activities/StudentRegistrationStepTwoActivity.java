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

public class StudentRegistrationStepTwoActivity extends AppCompatActivity {

    TextInputLayout mInstituteLayout;
    TextInputLayout mCourseLayout;
    TextInputLayout mSemOrYearLayout;
    TextInputLayout mIdLayout;
    TextInputLayout mBirthdayLayout;
    AutoCompleteTextView mInstituteField;
    AutoCompleteTextView mCourseField;
    AutoCompleteTextView mSemOrYearField;
    TextInputEditText mIdField;
    TextInputEditText mBirthdayField;
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
    private String mSemOrYear;
    private String mId;
    private String mBirthday;
    private ArrayList<String> mInstituteList;
    private ArrayList<String> mCourseList;
    private ArrayList<String> mSemOrYearList;
    private FirebaseFirestore mStore;

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_student_registration_step_two);

        Intent intent = getIntent();
        mFullName = intent.getStringExtra("fullName");
        mEmail = intent.getStringExtra("email");
        mPassword = intent.getStringExtra("password");
        mRole = intent.getStringExtra("role");
        mGender = intent.getStringExtra("gender");

        mInstituteLayout = findViewById(R.id.textInputLayout_select_institute);
        mCourseLayout = findViewById(R.id.textInputLayout_select_course);
        mSemOrYearLayout = findViewById(R.id.textInputLayout_sem_or_year);
        mIdLayout = findViewById(R.id.textInputLayout_roll_no);
        mBirthdayLayout = findViewById(R.id.textInputLayout_birthday);
        mInstituteField = findViewById(R.id.autoCompleteTextView_select_institute);
        mCourseField = findViewById(R.id.autoCompleteTextView_select_course);
        mSemOrYearField = findViewById(R.id.autoCompleteTextView_sem_or_year);
        mIdField = findViewById(R.id.textInputEditText_roll_no);
        mBirthdayField = findViewById(R.id.textInputEditText_birthday);
        mBackButton = findViewById(R.id.imageView_back);
        mRefreshButton = findViewById(R.id.imageView_refresh);
        mNextButton = findViewById(R.id.button_next);
        mLoginActivityLink = findViewById(R.id.textView_login);

        mStore = FirebaseFirestore.getInstance();

        mInstituteList = new ArrayList<>();
        fetchInstitutes();
        ArrayAdapter<String> collegeNameAdapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_popup_item, mInstituteList);
        mInstituteField.setAdapter(collegeNameAdapter);

        mCourseList = new ArrayList<>();
        ArrayAdapter<String> courseAdapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_popup_item, mCourseList);
        mCourseField.setAdapter(courseAdapter);

        mSemOrYearList = new ArrayList<>();
        ArrayAdapter<String> semOrYearAdapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_popup_item, mSemOrYearList);
        mSemOrYearField.setAdapter(semOrYearAdapter);

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
        mSemOrYearField.addTextChangedListener(new ValidationWatcher(mSemOrYearField));
        mIdField.addTextChangedListener(new ValidationWatcher(mIdField));
        mBirthdayField.addTextChangedListener(new ValidationWatcher(mBirthdayField));

        mRefreshButton.setOnClickListener(view -> {
            if (mInstituteList.isEmpty()) {
                fetchInstitutes();
            }
        });

        mBackButton.setOnClickListener(view -> onBackPressed());

        mInstituteLayout.setOnClickListener(view -> mInstituteField.showDropDown());

        mCourseLayout.setOnClickListener(view -> mCourseField.showDropDown());

        mSemOrYearLayout.setOnClickListener(view -> mSemOrYearField.showDropDown());

        mBirthdayField.setOnClickListener(view -> materialDatePicker.show(getSupportFragmentManager(), "Material Date Picker"));

        materialDatePicker.addOnPositiveButtonClickListener(selection -> mBirthdayField.setText(materialDatePicker.getHeaderText()));

        mNextButton.setOnClickListener(view -> {
            if ((validInstitute() != null) && (validCourse() != null) && (validSemOrYear() != null) && (validId() != null) && (validBirthday() != null)) {
                if (new NetworkInfoUtility(getApplicationContext()).isConnectedToInternet()) {
                    mProgressDialog = new MaterialDialog.Builder(StudentRegistrationStepTwoActivity.this)
                            .typeface(getResources().getFont(R.font.sf_ui_display_medium), getResources().getFont(R.font.sf_ui_display_regular))
                            .progress(true, 0)
                            .canceledOnTouchOutside(false)
                            .content(getResources().getString(R.string.content_dialog_please_wait))
                            .cancelable(false)
                            .canceledOnTouchOutside(false)
                            .build();
                    mProgressDialog.show();

                    Intent intent1 = new Intent(StudentRegistrationStepTwoActivity.this, StudentRegistrationStepThreeActivity.class);
                    intent1.putExtra("fullName", mFullName);
                    intent1.putExtra("email", mEmail);
                    intent1.putExtra("role", mRole);
                    intent1.putExtra("gender", mGender);
                    intent1.putExtra("password", mPassword);
                    intent1.putExtra("institute", mInstitute);
                    intent1.putExtra("course", mCourse);
                    intent1.putExtra("semOrYear", mSemOrYear);
                    intent1.putExtra("id", mId);
                    intent1.putExtra("birthday", mBirthday);
                    mProgressDialog.dismiss();
                    startActivity(intent1);
                } else {
                    mProgressDialog.dismiss();
                    new MaterialDialog.Builder(StudentRegistrationStepTwoActivity.this)
                            .typeface(getResources().getFont(R.font.sf_ui_display_medium), getResources().getFont(R.font.sf_ui_display_regular))
                            .title(R.string.title_dialog_no_internet)
                            .content(R.string.content_dialog_no_internet)
                            .icon(Objects.requireNonNull(getDrawable(R.drawable.ic_baseline_signal_wifi_off_24)))
                            .positiveText(R.string.positive_text_dialog_no_internet)
                            .negativeText(R.string.negative_text_dialog_no_internet)
                            .canceledOnTouchOutside(false)
                            .cancelable(false)
                            .onPositive((dialog, which) -> {
                                Intent intent1 = new Intent(getApplicationContext(), CommonRegistrationStepOneActivity.class);
                                startActivity(intent1);
                            })
                            .onNegative((dialog, which) -> {
                            })
                            .show();
                }
            }
        });

        mLoginActivityLink.setOnClickListener(view -> {
            Intent intent12 = new Intent(StudentRegistrationStepTwoActivity.this, LoginActivity.class);
            intent12.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(StudentRegistrationStepTwoActivity.this, CommonRegistrationStepOneActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fetchInstitutes() {
        mProgressDialog = new MaterialDialog.Builder(StudentRegistrationStepTwoActivity.this)
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
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fetchCourses() {

//        mProgressDialog = new MaterialDialog.Builder(StudentRegistrationStepTwoActivity.this)
//                .typeface(getResources().getFont(R.font.sf_ui_display_medium), getResources().getFont(R.font.sf_ui_display_regular))
//                .progress(true, 0)
//                .canceledOnTouchOutside(false)
//                .content(getResources().getString(R.string.content_dialog_listing_courses))
//                .cancelable(false)
//                .canceledOnTouchOutside(false)
//                .build();
//       mProgressDialog.show();

        mStore.collection("/institutes/" + mInstitute + "/courses")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot item : queryDocumentSnapshots) {
                        mCourseList.add(item.getId());
//                            mCollegeList = (ArrayList<String>) queryDocumentSnapshots.toObjects(String.class);
//                            mCollegeList.addAll(Collections.singleton(item.getId()));
                        Log.i("Courses list", item.getId());
                    }
                })
                .addOnFailureListener(e -> Log.e("Failed to get courses:", Objects.requireNonNull(e.getMessage())));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fetchSemOrYear() {
        mProgressDialog = new MaterialDialog.Builder(StudentRegistrationStepTwoActivity.this)
                .typeface(getResources().getFont(R.font.sf_ui_display_medium), getResources().getFont(R.font.sf_ui_display_regular))
                .progress(true, 0)
                .canceledOnTouchOutside(false)
                .content(getResources().getString(R.string.content_dialog_retrieving_session_data))
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .build();
        mProgressDialog.show();

        mStore.collection("colleges/" + mInstitute + "/courses/" + mCourse + "/semOrYear")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot item : queryDocumentSnapshots) {
                        mSemOrYearList.add(item.getId());
                        Log.i("Sem/Year list:", item.getId());
                    }
                    mProgressDialog.dismiss();
                })
                .addOnFailureListener(e -> {
                    Log.e("Failed to get sem/year:", Objects.requireNonNull(e.getMessage()));

                    mProgressDialog.dismiss();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
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

                if (mCourseList.contains(mCourse)) {
                    mSemOrYearList.clear();
                    fetchSemOrYear();
                }
                return course;
            } else {
                mCourseLayout.setError(getString(R.string.error_invalid_course));
                requestFocus(mCourseField);
                return null;
            }
        }
    }

    private String validSemOrYear() {
        String semOrYear = Objects.requireNonNull(mSemOrYearLayout.getEditText()).getText().toString().trim();
        Pattern semOrYearPattern = Pattern.compile("^\\p{L}+[\\p{L}\\p{Z}\\p{P}\\p{N}]{0,20}");
        Matcher semOrYearMatcher = semOrYearPattern.matcher(semOrYear);

        if (semOrYear.isEmpty()) {
            mSemOrYearLayout.setError(getString(R.string.error_empty_sem_or_year));
            requestFocus(mSemOrYearField);
            return null;
        } else {
            if (semOrYearMatcher.matches() && mSemOrYearList.contains(semOrYear)) {
                mSemOrYearLayout.setErrorEnabled(false);
                mSemOrYear = semOrYear;
                return semOrYear;
            } else {
                mSemOrYearLayout.setError(getString(R.string.error_invalid_sem_or_year));
                requestFocus(mSemOrYearField);
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
                case R.id.autoCompleteTextView_sem_or_year:
                    validSemOrYear();
                    break;
                case R.id.textInputEditText_birthday:
                    validBirthday();
                    break;
            }
        }
    }
}