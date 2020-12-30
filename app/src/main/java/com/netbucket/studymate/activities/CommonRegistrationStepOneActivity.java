package com.netbucket.studymate.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.netbucket.studymate.R;
import com.netbucket.studymate.utils.NetworkInfoUtility;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonRegistrationStepOneActivity extends AppCompatActivity {

    TextInputLayout mFullNameLayout;
    TextInputLayout mEmailLayout;
    TextInputLayout mRoleLayout;
    TextInputLayout mGenderLayout;
    TextInputLayout mPasswordLayout;
    TextInputEditText mFullNameField;
    TextInputEditText mEmailField;
    AutoCompleteTextView mRoleField;
    AutoCompleteTextView mGenderField;
    TextInputEditText mPasswordField;
    ImageView mBackButton;
    Button mNextButton;
    MaterialDialog mProgressDialog;
    TextView mLoginActivityLink;
    String[] mRolesArray;
    String[] mGenderArray;
    private String mFullName;
    private String mEmail;
    private String mRole;
    private String mGender;
    private String mPassword;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_registration_step_one);

        mFullNameLayout = findViewById(R.id.textInputLayout_full_name);
        mEmailLayout = findViewById(R.id.textInputLayout_email);
        mRoleLayout = findViewById(R.id.textInputLayout_role);
        mGenderLayout = findViewById(R.id.textInputLayout_gender);
        mPasswordLayout = findViewById(R.id.textInputLayout_create_password);
        mFullNameField = findViewById(R.id.textInputEditText_full_name);
        mEmailField = findViewById(R.id.textInputEditText_email);
        mRoleField = findViewById(R.id.autoCompleteTextView_role);
        mGenderField = findViewById(R.id.autoCompleteTextView_gender);
        mPasswordField = findViewById(R.id.textInputEditText_create_password);
        mBackButton = findViewById(R.id.imageView_back);
        mNextButton = findViewById(R.id.button_next);
        mLoginActivityLink = findViewById(R.id.textView_login);

        mRolesArray = getResources().getStringArray(R.array.roles);

        ArrayAdapter<String> roleArrayAdapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_popup_item, mRolesArray);
        mRoleField.setAdapter(roleArrayAdapter);

        mGenderArray = getResources().getStringArray(R.array.genders);

        ArrayAdapter<String> genderArrayAdapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_popup_item, mGenderArray);
        mGenderField.setAdapter(genderArrayAdapter);

        mFullNameField.addTextChangedListener(new ValidationWatcher(mFullNameField));
        mEmailField.addTextChangedListener(new ValidationWatcher(mEmailField));
        mRoleField.addTextChangedListener(new ValidationWatcher(mRoleField));
        mGenderField.addTextChangedListener(new ValidationWatcher(mGenderField));
        mPasswordField.addTextChangedListener(new ValidationWatcher(mPasswordField));

        mRoleLayout.setOnClickListener(view -> mRoleField.showDropDown());

        mBackButton.setOnClickListener(view -> onBackPressed());

        mNextButton.setOnClickListener(view -> {
            if ((validFullName() != null) && (validEmail() != null) && (validRole() != null) && (validGender() != null) && (validPassword() != null)) {
                if (new NetworkInfoUtility(getApplicationContext()).isConnectedToInternet()) {
                    mProgressDialog = new MaterialDialog.Builder(CommonRegistrationStepOneActivity.this)
                            .typeface(getResources().getFont(R.font.sf_ui_display_medium), getResources().getFont(R.font.sf_ui_display_regular))
                            .progress(true, 0)
                            .canceledOnTouchOutside(false)
                            .content(getResources().getString(R.string.content_dialog_please_wait))
                            .cancelable(false)
                            .canceledOnTouchOutside(false)
                            .build();
                    mProgressDialog.show();

                    if (mRole.equals(mRolesArray[0])) {
                        Intent intent = new Intent(CommonRegistrationStepOneActivity.this, AdminRegistrationStepTwoActivity.class);
                        intent.putExtra("fullName", mFullName);
                        intent.putExtra("email", mEmail);
                        intent.putExtra("role", mRole);
                        intent.putExtra("gender", mGender);
                        intent.putExtra("password", mPassword);
                        mProgressDialog.dismiss();
                        startActivity(intent);

                    } else if (mRole.equals(mRolesArray[1])) {
                        Intent intent = new Intent(CommonRegistrationStepOneActivity.this, FacultyMemberRegistrationStepTwoActivity.class);
                        intent.putExtra("fullName", mFullName);
                        intent.putExtra("email", mEmail);
                        intent.putExtra("role", mRole);
                        intent.putExtra("gender", mGender);
                        intent.putExtra("password", mPassword);
                        mProgressDialog.dismiss();
                        startActivity(intent);

                    } else {
                        Intent intent = new Intent(CommonRegistrationStepOneActivity.this, StudentRegistrationStepTwoActivity.class);
                        intent.putExtra("fullName", mFullName);
                        intent.putExtra("email", mEmail);
                        intent.putExtra("role", mRole);
                        intent.putExtra("gender", mGender);
                        intent.putExtra("password", mPassword);
                        mProgressDialog.dismiss();
                        startActivity(intent);

                    }
                } else {
                    new MaterialDialog.Builder(CommonRegistrationStepOneActivity.this)
                            .typeface(getResources().getFont(R.font.sf_ui_display_medium), getResources().getFont(R.font.sf_ui_display_regular))
                            .title(R.string.title_dialog_no_internet)
                            .content(R.string.content_dialog_no_internet)
                            .icon(Objects.requireNonNull(getDrawable(R.drawable.ic_baseline_signal_wifi_off_24)))
                            .positiveText(R.string.positive_text_dialog_no_internet)
                            .negativeText(R.string.negative_text_dialog_no_internet)
                            .canceledOnTouchOutside(false)
                            .cancelable(false)
                            .onPositive((dialog, which) -> {
                                Intent intent = new Intent(getApplicationContext(), CommonRegistrationStepOneActivity.class);
                                startActivity(intent);
                            })
                            .onNegative((dialog, which) -> {
                            })
                            .show();
                }
            }
        });

        mLoginActivityLink.setOnClickListener(view -> {
            Intent intent = new Intent(CommonRegistrationStepOneActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(CommonRegistrationStepOneActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    private String validFullName() {
        String fullName = Objects.requireNonNull(mFullNameLayout.getEditText()).getText().toString().trim();
        Pattern fullNamePattern = Pattern.compile("^\\p{L}+[\\p{L}\\p{Z}\\p{P}]{0,50}");
        Matcher fullNameMatcher = fullNamePattern.matcher(fullName);

        if (fullName.isEmpty()) {
            mFullNameLayout.setError(getString(R.string.error_empty_full_name));
            requestFocus(mFullNameField);
            return null;
        } else {
            if (fullNameMatcher.matches()) {
                mFullNameLayout.setErrorEnabled(false);
                mFullName = fullName;
                return fullName;
            } else {
                mFullNameLayout.setError(getString(R.string.error_invalid_full_name));
                requestFocus(mFullNameField);
                return null;
            }
        }
    }

    private String validEmail() {
        String email = Objects.requireNonNull(mEmailLayout.getEditText()).getText().toString().trim();
        Pattern emailPattern = Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$");
        Matcher emailMatcher = emailPattern.matcher(email);

        if (email.isEmpty()) {
            mEmailLayout.setError(getString(R.string.error_empty_email));
            requestFocus(mEmailField);
            return null;
        } else {
            if (emailMatcher.matches()) {
                mEmailLayout.setErrorEnabled(false);
                mEmail = email;
                return email;
            } else {
                mEmailLayout.setError(getString(R.string.error_invalid_email));
                requestFocus(mEmailField);
                return null;
            }
        }
    }

    private String validRole() {
        String role = Objects.requireNonNull(mRoleLayout.getEditText()).getText().toString().trim();

        if (role.isEmpty()) {
            mRoleLayout.setError(getString(R.string.error_empty_role));
            requestFocus(mRoleField);
            return null;
        } else {
            if (role.contentEquals(getString(R.string.role_admin)) || role.contentEquals(getString(R.string.role_faculty_member)) || role.contentEquals(getString(R.string.role_student))) {
                mRoleLayout.setErrorEnabled(false);
                mRole = role;
                return role;
            } else {
                mRoleLayout.setError(getString(R.string.error_invalid_role));
                requestFocus(mRoleField);
                return null;
            }
        }
    }

    private String validGender() {
        String gender = Objects.requireNonNull(mGenderLayout.getEditText()).getText().toString().trim();

        if (gender.isEmpty()) {
            mGenderLayout.setError(getString(R.string.error_empty_gender));
            requestFocus(mGenderField);
            return null;
        } else {
            if (gender.contentEquals(getString(R.string.gender_male)) || gender.contentEquals(getString(R.string.gender_female)) || gender.contentEquals(getString(R.string.gender_prefer_not_to_say))) {
                mGenderLayout.setErrorEnabled(false);
                mGender = gender;
                return gender;
            } else {
                mGenderLayout.setError(getString(R.string.error_invalid_gender));
                requestFocus(mGenderField);
                return null;
            }
        }
    }

    private String validPassword() {
        String password = Objects.requireNonNull(mPasswordLayout.getEditText()).getText().toString().trim();
        Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[(@#$%^&+=])(?=\\S+$).{4,}$");
        Matcher passwordMatcher = passwordPattern.matcher(password);

        if (password.isEmpty()) {
            mPasswordLayout.setError(getString(R.string.error_empty_password));
            requestFocus(mPasswordField);
            return null;
        } else {
            if (passwordMatcher.matches()) {
                mPasswordLayout.setErrorEnabled(false);
                mPassword = password;
                return password;
            } else {
                mPasswordLayout.setError(getString(R.string.error_invalid_password));
                requestFocus(mPasswordField);
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
        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.textInputEditText_full_name:
                    validFullName();
                    break;
                case R.id.textInputEditText_email:
                    validEmail();
                    break;
                case R.id.autoCompleteTextView_role:
                    validRole();
                    break;
                case R.id.autoCompleteTextView_gender:
                    validGender();
                    break;
                case R.id.textInputEditText_create_password:
                    validPassword();
                    break;
            }
        }
    }
}