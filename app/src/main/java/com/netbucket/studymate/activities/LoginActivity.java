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
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.netbucket.studymate.R;
import com.netbucket.studymate.utils.NetworkInfoUtility;
import com.netbucket.studymate.utils.SessionManager;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.mateware.snacky.Snacky;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout mEmailLayout;
    TextInputEditText mEmailField;
    TextInputLayout mPasswordLayout;
    TextInputEditText mPasswordField;
    Button mLoginButton;
    TextView mSignUpActivityLink;
    MaterialDialog mProgressDialog;
    FirebaseFirestore mStore;
    FirebaseAuth mAuth;
    String mUserReference;
    String mIsApproved = "false";
    private String mFullName;
    private String mRole;
    private String mGender;
    private String mCollegeName;
    private String mCourse;
    private String mId;
    private String mBirthday;
    private String mPhoneNumber;
    private String mUsername;
    private String mAbout;
    private String mSemOrYear;
    private String mProfileImageUri;

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailLayout = findViewById(R.id.textInputLayout_email);
        mPasswordLayout = findViewById(R.id.textInputLayout_create_password);
        mEmailField = findViewById(R.id.textInputEditText_email);
        mPasswordField = findViewById(R.id.textInputEditText_password);
        mLoginButton = findViewById(R.id.button_login);
        mSignUpActivityLink = findViewById(R.id.textView_register);

        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mEmailField.addTextChangedListener(new ValidationWatcher(mEmailField));
        mPasswordField.addTextChangedListener(new ValidationWatcher(mPasswordField));

        mLoginButton.setOnClickListener(v -> signIn());

        mSignUpActivityLink.setOnClickListener(view -> {
            if (new NetworkInfoUtility(getApplicationContext()).isConnectedToInternet()) {
                Intent intent = new Intent(getApplicationContext(), CommonRegistrationStepOneActivity.class);
                startActivity(intent);
                finish();
            } else {
                new MaterialDialog.Builder(LoginActivity.this)
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
                            finish();
                        })
                        .onNegative((dialog, which) -> {
                        })
                        .show();
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void signIn() {
        if (validEmail() != null && validPassword() != null) {
            if (new NetworkInfoUtility(getApplicationContext()).isConnectedToInternet()) {
                mProgressDialog = new MaterialDialog.Builder(LoginActivity.this)
                        .typeface(getResources().getFont(R.font.sf_ui_display_medium), getResources().getFont(R.font.sf_ui_display_regular))
                        .progress(true, 0)
                        .canceledOnTouchOutside(false)
                        .content(getResources().getString(R.string.content_dialog_signing_in))
                        .cancelable(false)
                        .canceledOnTouchOutside(false)
                        .build();
                mProgressDialog.show();

                mAuth.signInWithEmailAndPassword(Objects.requireNonNull(validEmail()), Objects.requireNonNull(validPassword()))
                        .addOnSuccessListener(authResult -> {
                            Log.i("Login successful:", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

                            mStore.collection("users")
                                    .document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                                    .get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        Log.i("Fetching reference: ", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

                                        mIsApproved = documentSnapshot.getString("isApproved");
                                        mUserReference = documentSnapshot.getString("reference");
                                        mProfileImageUri = documentSnapshot.getString("profileImage");


                                        Log.i("User location:", Objects.requireNonNull(mUserReference));
                                        Log.i("Approval Status:", String.valueOf(mIsApproved));

                                        mStore.document(mUserReference)
                                                .get()
                                                .addOnSuccessListener(documentSnapshot1 -> {
                                                    Log.i("Fetching details of: ", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

                                                    mFullName = documentSnapshot1.getString("fullName");
                                                    mRole = documentSnapshot1.getString("role");
                                                    mGender = documentSnapshot1.getString("gender");
                                                    mCollegeName = documentSnapshot1.getString("collegeName");
                                                    mCourse = documentSnapshot1.getString("course");
                                                    mId = documentSnapshot1.getString("id");
                                                    mBirthday = documentSnapshot1.getString("birthday");
                                                    mPhoneNumber = documentSnapshot1.getString("phoneNumber");
                                                    mUsername = documentSnapshot1.getString("username");
                                                    mAbout = documentSnapshot1.getString("about");
                                                    mSemOrYear = documentSnapshot1.getString("semOrYear");
                                                    SessionManager sessionManager = new SessionManager(LoginActivity.this, SessionManager.SESSION_USER_SESSION);
                                                    sessionManager.createUserSession(mAuth.getCurrentUser().getUid(), mUserReference, mIsApproved, mFullName, validEmail(), mRole, mGender, validPassword(), mCollegeName, mCourse, mId, mBirthday, mPhoneNumber, mUsername, mAbout, mSemOrYear, mProfileImageUri);

                                                    if (mIsApproved.equals("true")) {
                                                        switch (mRole) {
                                                            case "Student":
                                                                Intent studentDashboardIntent = new Intent(LoginActivity.this, StudentDashboardActivity.class);
                                                                mProgressDialog.dismiss();
                                                                studentDashboardIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                startActivity(studentDashboardIntent);
                                                                finish();
                                                                break;

                                                            case "Faculty":
                                                                Intent facultyDashboardIntent = new Intent(LoginActivity.this, FacultyDashboardActivity.class);
                                                                mProgressDialog.dismiss();
                                                                facultyDashboardIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                startActivity(facultyDashboardIntent);
                                                                finish();
                                                                break;

                                                            case "Admin/HOD":
                                                                Intent adminDashboardIntent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                                                                mProgressDialog.dismiss();
                                                                adminDashboardIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                startActivity(adminDashboardIntent);
                                                                finish();
                                                                break;

                                                            default:
                                                                break;
                                                        }
                                                    } else {
                                                        Intent intent = new Intent(LoginActivity.this, UserApprovalPendingActivity.class);
                                                        mProgressDialog.dismiss();
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(intent);
                                                        finish();
                                                    }

                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("Details query failed:", Objects.requireNonNull(e.getMessage()));

                                                    mProgressDialog.dismiss();

                                                    Snacky.builder()
                                                            .setActivity(LoginActivity.this)
                                                            .setBackgroundColor(getResources().getColor(R.color.snackBar, getTheme()))
                                                            .setText(R.string.content_snackBar_login_failed)
                                                            .setTextColor(getResources().getColor(R.color.snackBarText, getTheme()))
                                                            .setTextTypeface(getResources().getFont(R.font.sf_ui_display_regular))
                                                            .setIcon(R.drawable.ic_outline_warning_24)
                                                            .setTextSize(16)
                                                            .setDuration(Snacky.LENGTH_LONG)
                                                            .build()
                                                            .show();
                                                });
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Reference query failed:", Objects.requireNonNull(e.getMessage()));

                                        mProgressDialog.dismiss();

                                        Snacky.builder()
                                                .setActivity(LoginActivity.this)
                                                .setBackgroundColor(getResources().getColor(R.color.snackBar, getTheme()))
                                                .setText(R.string.content_snackBar_login_failed)
                                                .setTextColor(getResources().getColor(R.color.snackBarText, getTheme()))
                                                .setTextTypeface(getResources().getFont(R.font.sf_ui_display_regular))
                                                .setIcon(R.drawable.ic_outline_warning_24)
                                                .setTextSize(16)
                                                .setDuration(Snacky.LENGTH_LONG)
                                                .build()
                                                .show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Log.e("Login failed:", Objects.requireNonNull(e.getMessage()));

                            if (e instanceof FirebaseAuthInvalidUserException) {
                                mProgressDialog.dismiss();

                                Snacky.builder()
                                        .setActivity(LoginActivity.this)
                                        .setBackgroundColor(getResources().getColor(R.color.snackBar, getTheme()))
                                        .setText(R.string.content_snackBar_email_not_registered)
                                        .setTextColor(getResources().getColor(R.color.snackBarText, getTheme()))
                                        .setTextTypeface(getResources().getFont(R.font.sf_ui_display_regular))
                                        .setIcon(R.drawable.ic_outline_warning_24)
                                        .setTextSize(16)
                                        .setDuration(Snacky.LENGTH_LONG)
                                        .build()
                                        .show();
                            } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                mProgressDialog.dismiss();

                                Snacky.builder()
                                        .setActivity(LoginActivity.this)
                                        .setBackgroundColor(getResources().getColor(R.color.snackBar, getTheme()))
                                        .setText(R.string.content_snackBar_login_failed_wrong_credentials)
                                        .setTextColor(getResources().getColor(R.color.snackBarText, getTheme()))
                                        .setTextTypeface(getResources().getFont(R.font.sf_ui_display_regular))
                                        .setIcon(R.drawable.ic_outline_warning_24)
                                        .setTextSize(16)
                                        .setDuration(Snacky.LENGTH_LONG)
                                        .build()
                                        .show();
                            } else {
                                mProgressDialog.dismiss();

                                Snacky.builder()
                                        .setActivity(LoginActivity.this)
                                        .setBackgroundColor(getResources().getColor(R.color.snackBar, getTheme()))
                                        .setText(R.string.content_snackBar_login_failed)
                                        .setTextColor(getResources().getColor(R.color.snackBarText, getTheme()))
                                        .setTextTypeface(getResources().getFont(R.font.sf_ui_display_regular))
                                        .setIcon(R.drawable.ic_outline_warning_24)
                                        .setTextSize(16)
                                        .setDuration(Snacky.LENGTH_LONG)
                                        .build()
                                        .show();
                            }
                            if (!new NetworkInfoUtility(getApplicationContext()).isConnectedToInternet()) {
                                mProgressDialog.dismiss();
                                new MaterialDialog.Builder(LoginActivity.this)
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
                        });
            } else {
                new MaterialDialog.Builder(LoginActivity.this)
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    private String validEmail() {
        String email = Objects.requireNonNull(mEmailLayout.getEditText()).getText().toString();
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
        } else {
            if (emailMatcher.matches()) {
                mEmailLayout.setErrorEnabled(false);
                return email;
            } else {
                mEmailLayout.setError(getString(R.string.error_invalid_email));
                requestFocus(mEmailField);
            }
        }
        return null;
    }

    private String validPassword() {
        String password = Objects.requireNonNull(mPasswordLayout.getEditText()).getText().toString().trim();
        Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[(@#$%^&+=])(?=\\S+$).{8,20}$");
        Matcher passwordMatcher = passwordPattern.matcher(password);

        if (password.isEmpty()) {
            mPasswordLayout.setError(getResources().getString(R.string.error_empty_password));
            requestFocus(mPasswordField);
        } else {
            if (passwordMatcher.matches()) {
                mPasswordLayout.setErrorEnabled(false);
                return password;
            } else {
                mPasswordLayout.setError(getResources().getString(R.string.error_invalid_password));
                requestFocus(mPasswordField);
            }
        }
        return null;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void forgotPassword(View view) {
        if (new NetworkInfoUtility(getApplicationContext()).isConnectedToInternet()) {
            startActivity(new Intent(this, PasswordResetActivity.class));
            setResult(RESULT_CANCELED);
            finish();
        } else {
            new MaterialDialog.Builder(LoginActivity.this)
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
                        setResult(RESULT_OK);
                    })
                    .onNegative((dialog, which) -> {
                    })
                    .show();
        }
    }

    public class ValidationWatcher implements TextWatcher {

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
                case R.id.textInputEditText_email:
                    validEmail();
                    break;
                case R.id.textInputEditText_password:
                    validPassword();
                    break;
            }
        }
    }
}