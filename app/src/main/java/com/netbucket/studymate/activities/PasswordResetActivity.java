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
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.netbucket.studymate.R;
import com.netbucket.studymate.utils.NetworkInfoUtility;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordResetActivity extends AppCompatActivity {

    RelativeLayout mInfoButton;
    TextInputLayout mEmailLayout;
    TextInputEditText mEmailField;
    Button mSendPasswordResetLink;
    ImageView mBackButton;
    MaterialDialog mProgressDialog;
    FirebaseAuth mAuth;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        mInfoButton = findViewById(R.id.relativeLayout_logo);
        mEmailLayout = findViewById(R.id.textInputLayout_registered_email);
        mEmailField = findViewById(R.id.textInputEditText_registered_email);
        mBackButton = findViewById(R.id.imageView_back);
        mSendPasswordResetLink = findViewById(R.id.button_send_link);

        mAuth = FirebaseAuth.getInstance();

        mInfoButton.setOnClickListener(v -> {
            Intent intent = new Intent(PasswordResetActivity.this, InfoActivity.class);
            startActivity(intent);
        });

        mBackButton.setOnClickListener(v -> onBackPressed());

        mEmailField.addTextChangedListener(new ValidationWatcher(mEmailField));
        mSendPasswordResetLink.setOnClickListener(v -> {
            if (validEmail() != null) {
                if (new NetworkInfoUtility(getApplicationContext()).isConnectedToInternet()) {
                    mProgressDialog = new MaterialDialog.Builder(PasswordResetActivity.this)
                            .typeface(getResources().getFont(R.font.sf_ui_display_medium), getResources().getFont(R.font.sf_ui_display_regular))
                            .progress(true, 0)
                            .canceledOnTouchOutside(false)
                            .content(getResources().getString(R.string.content_dialog_sending_password_reset_link))
                            .cancelable(false)
                            .canceledOnTouchOutside(false)
                            .build();
                    mProgressDialog.show();

                    mAuth.sendPasswordResetEmail(Objects.requireNonNull(validEmail()))
                            .addOnSuccessListener(aVoid -> {
                                Log.i("Email sent:", Objects.requireNonNull(validEmail()));

                                mProgressDialog.dismiss();
                                new MaterialDialog.Builder(PasswordResetActivity.this)
                                        .typeface(getResources().getFont(R.font.sf_ui_display_medium), getResources().getFont(R.font.sf_ui_display_regular))
                                        .title(R.string.title_dialog_link_sent)
                                        .content(R.string.content_dialog_link_sent)
                                        .icon(Objects.requireNonNull(getDrawable(R.drawable.ic_baseline_send_24)))
                                        .positiveText(R.string.positive_text_dialog_link_sent)
                                        .canceledOnTouchOutside(false)
                                        .cancelable(false)
                                        .onPositive((dialog, which) -> {
                                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                        })
                                        .show();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("Email not sent:", Objects.requireNonNull(e.getMessage()));

                                if (e instanceof FirebaseAuthInvalidUserException) {
                                    mProgressDialog.dismiss();
                                    new MaterialDialog.Builder(PasswordResetActivity.this)
                                            .typeface(getResources().getFont(R.font.sf_ui_display_medium), getResources().getFont(R.font.sf_ui_display_regular))
                                            .title(R.string.title_dialog_email_not_registered)
                                            .content(R.string.content_dialog_email_not_registered)
                                            .icon(Objects.requireNonNull(getDrawable(R.drawable.ic_baseline_cancel_schedule_send_24)))
                                            .negativeText(getResources().getString(R.string.positive_text_dialog_email_not_registered))
                                            .negativeText(getResources().getString(R.string.negative_text_dialog_email_not_registered))
                                            .canceledOnTouchOutside(false)
                                            .cancelable(false)
                                            .onPositive((dialog, which) -> {
                                            })
                                            .onNegative((dialog, which) -> {
                                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                                finish();
                                            })
                                            .show();
                                } else {
                                    mProgressDialog.dismiss();
                                    new MaterialDialog.Builder(PasswordResetActivity.this)
                                            .typeface(getResources().getFont(R.font.sf_ui_display_medium), getResources().getFont(R.font.sf_ui_display_regular))
                                            .title(R.string.title_dialog_link_not_sent)
                                            .content(R.string.content_dialog_link_not_sent)
                                            .icon(Objects.requireNonNull(getDrawable(R.drawable.ic_baseline_cancel_schedule_send_24)))
                                            .negativeText(getResources().getString(R.string.positive_text_dialog_link_not_sent))
                                            .canceledOnTouchOutside(false)
                                            .cancelable(false)
                                            .onNegative((dialog, which) -> {
                                            })
                                            .show();
                                }
                                if (!new NetworkInfoUtility(getApplicationContext()).isConnectedToInternet()) {
                                    mProgressDialog.dismiss();
                                    new MaterialDialog.Builder(PasswordResetActivity.this)
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
                    new MaterialDialog.Builder(PasswordResetActivity.this)
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(PasswordResetActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
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

    public class ValidationWatcher implements TextWatcher {

        private final View view;

        private ValidationWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        public void afterTextChanged(Editable editable) {
            if (view.getId() == R.id.textInputEditText_email) {
                validEmail();
            }
        }
    }
}