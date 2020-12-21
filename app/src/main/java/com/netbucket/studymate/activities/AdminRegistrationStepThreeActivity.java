package com.netbucket.studymate.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.netbucket.studymate.R;
import com.netbucket.studymate.utils.NetworkInfoUtility;
import com.netbucket.studymate.utils.SessionManager;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import de.mateware.snacky.Snacky;

public class AdminRegistrationStepThreeActivity extends AppCompatActivity {

    final String mSemOrYear = "NA";
    CircleImageView mProfileImage;
    TextInputLayout mUsernameLayout;
    TextInputLayout mAboutLayout;
    TextInputEditText mUsernameField;
    TextInputEditText mAboutField;
    ImageView mBackButton;
    Button mRegisterButton;
    TextView mLoginActivityLink;
    MaterialDialog mProgressDialog;
    String mUserReference;
    String mIsApproved;
    FloatingActionButton mCameraFab;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private StorageReference mStorageRef;
    private String mFullName;
    private String mEmail;
    private String mRole;
    private String mGender;
    private String mPassword;
    private String mCollegeName;
    private String mCourse;
    private String mId;
    private String mBirthday;
    private String mPhoneNumber;
    private String mUsername;
    private String mAbout;
    private Uri mImageUri;
    private Uri mProfileImageUri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = Objects.requireNonNull(result).getUri();
                mProfileImage.setImageURI(mImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception exception = Objects.requireNonNull(result).getError();
                Toast.makeText(this, "Error:" + exception, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_registration_step_three);

        Intent intent = getIntent();
        mFullName = intent.getStringExtra("fullName");
        mEmail = intent.getStringExtra("email");
        mRole = intent.getStringExtra("role");
        mGender = intent.getStringExtra("gender");
        mPassword = intent.getStringExtra("password");
        mCollegeName = intent.getStringExtra("collegeName");
        mCourse = intent.getStringExtra("course");
        mId = intent.getStringExtra("id");
        mBirthday = intent.getStringExtra("birthday");
        mPhoneNumber = intent.getStringExtra("phoneNumber");

        mProfileImage = findViewById(R.id.circleImageView_profile_image);
        mUsernameLayout = findViewById(R.id.textInputLayout_username);
        mAboutLayout = findViewById(R.id.textInputLayout_about);
        mUsernameField = findViewById(R.id.textInputEditText_username);
        mAboutField = findViewById(R.id.textInputEditText_about);
        mBackButton = findViewById(R.id.imageView_back);
        mRegisterButton = findViewById(R.id.button_register);
        mLoginActivityLink = findViewById(R.id.textView_login);
        mCameraFab = findViewById(R.id.floatingActionButton_camera);

        mAboutField.setText("Hey There! I am using StudyMate.");
        mIsApproved = "false";

        mUsernameField.addTextChangedListener(new ValidationWatcher(mUsernameField));
        mAboutField.addTextChangedListener(new ValidationWatcher(mAboutField));

        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseStorage mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mCameraFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(AdminRegistrationStepThreeActivity.this);
            }
        });

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if ((validUsername() != null) && (validAbout() != null)) {
                    registerAdmin();
                }
            }
        });

        mLoginActivityLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminRegistrationStepThreeActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void registerAdmin() {
        if (new NetworkInfoUtility(getApplicationContext()).isConnectedToInternet()) {
            mProgressDialog = new MaterialDialog.Builder(AdminRegistrationStepThreeActivity.this)
                    .typeface(getResources().getFont(R.font.sf_ui_display_medium), getResources().getFont(R.font.sf_ui_display_regular))
                    .progress(true, 0)
                    .canceledOnTouchOutside(false)
                    .content(getResources().getString(R.string.content_dialog_registering))
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .build();
            mProgressDialog.show();

            mAuth.createUserWithEmailAndPassword(mEmail, mPassword)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Log.i("Account account with", mEmail);

                            final StorageReference filePath = mStorageRef.child("profileImages").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid() + ".jpg");
                            filePath.putFile(mImageUri)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                            Log.i("Profile image uploaded", String.valueOf(mImageUri));

                                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                ;

                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    Log.d("Profile image URI", String.valueOf(uri));
                                                    mProfileImageUri = uri;

                                                    Map<String, Object> userData = new HashMap<>();
                                                    mUserReference = "/colleges/" + mCollegeName + "/courses/" + mCourse + "/admin/" + Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                                                    userData.put("reference", mUserReference);
                                                    userData.put("isApproved", mIsApproved);
                                                    userData.put("username", mUsername);
                                                    userData.put("profileImage", String.valueOf(mProfileImageUri));

                                                    mStore.collection("users")
                                                            .document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                                                            .set(userData)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Log.i("Reference created", mUserReference);

                                                                    Map<String, Object> userData = new HashMap<>();
                                                                    userData.put("fullName", mFullName);
                                                                    userData.put("email", mEmail);
                                                                    userData.put("role", mRole);
                                                                    userData.put("gender", mGender);
                                                                    userData.put("password", mPassword);
                                                                    userData.put("collegeName", mCollegeName);
                                                                    userData.put("course", mCourse);
                                                                    userData.put("id", mId);
                                                                    userData.put("birthday", mBirthday);
                                                                    userData.put("phoneNumber", mPhoneNumber);
                                                                    userData.put("username", mUsername);
                                                                    userData.put("about", mAbout);
                                                                    userData.put("isApproved", mIsApproved);
                                                                    userData.put("semOrYear", mSemOrYear);

                                                                    mStore.collection("/colleges/" + mCollegeName + "/courses/" + mCourse + "/admin/")
                                                                            .document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                                                                            .set(userData)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    Log.i("Data stored", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

                                                                                    mAuth.signInWithEmailAndPassword(mEmail, mPassword)
                                                                                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                                                                @Override
                                                                                                public void onSuccess(AuthResult authResult) {
                                                                                                    Log.i("Signed in", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

                                                                                                    SessionManager sessionManager = new SessionManager(AdminRegistrationStepThreeActivity.this, SessionManager.SESSION_USER_SESSION);
                                                                                                    sessionManager.createUserSession(mAuth.getCurrentUser().getUid(), mUserReference, mIsApproved, mFullName, mEmail, mRole, mGender, mPassword, mCollegeName, mCourse, mId, mBirthday, mPhoneNumber, mUsername, mAbout, mSemOrYear, String.valueOf(mProfileImageUri));
                                                                                                    Intent intent = new Intent(getApplicationContext(), AdminDashboardActivity.class);
                                                                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                                                                                    mProgressDialog.dismiss();

                                                                                                    startActivity(intent);
                                                                                                    finish();
                                                                                                }
                                                                                            })
                                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                                @Override
                                                                                                public void onFailure(@NonNull Exception e) {
                                                                                                    Log.e("Failed to logging in", Objects.requireNonNull(e.getMessage()));

                                                                                                    mProgressDialog.dismiss();

                                                                                                    Snacky.builder()
                                                                                                            .setActivity(AdminRegistrationStepThreeActivity.this)
                                                                                                            .setBackgroundColor(getResources().getColor(R.color.snackBar, getTheme()))
                                                                                                            .setText(R.string.content_snackBar_login_failed)
                                                                                                            .setTextColor(getResources().getColor(R.color.snackBarText, getTheme()))
                                                                                                            .setTextTypeface(getResources().getFont(R.font.sf_ui_display_regular))
                                                                                                            .setIcon(R.drawable.ic_outline_warning_24)
                                                                                                            .setTextSize(16)
                                                                                                            .setDuration(Snacky.LENGTH_LONG)
                                                                                                            .build()
                                                                                                            .show();

                                                                                                    if (!new NetworkInfoUtility(getApplicationContext()).isConnectedToInternet()) {
                                                                                                        mProgressDialog.dismiss();

                                                                                                        new MaterialDialog.Builder(AdminRegistrationStepThreeActivity.this)
                                                                                                                .typeface(getResources().getFont(R.font.sf_ui_display_medium), getResources().getFont(R.font.sf_ui_display_regular))
                                                                                                                .title(R.string.title_dialog_no_internet)
                                                                                                                .content(R.string.content_dialog_no_internet)
                                                                                                                .icon(Objects.requireNonNull(getDrawable(R.drawable.ic_baseline_signal_wifi_off_24)))
                                                                                                                .positiveText(R.string.positive_text_dialog_no_internet)
                                                                                                                .negativeText(R.string.negative_text_dialog_no_internet)
                                                                                                                .canceledOnTouchOutside(false)
                                                                                                                .cancelable(false)
                                                                                                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                                                                                    @Override
                                                                                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                                                                        Intent intent = new Intent(getApplicationContext(), CommonRegistrationStepOneActivity.class);
                                                                                                                        startActivity(intent);
                                                                                                                    }
                                                                                                                })
                                                                                                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                                                                                    @Override
                                                                                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                                                                    }
                                                                                                                })
                                                                                                                .show();
                                                                                                    }
                                                                                                }
                                                                                            });
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @SuppressLint("UseCompatLoadingForDrawables")
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    Log.e("Failed to store data", Objects.requireNonNull(e.getMessage()));

                                                                                    mProgressDialog.dismiss();

                                                                                    Snacky.builder()
                                                                                            .setActivity(AdminRegistrationStepThreeActivity.this)
                                                                                            .setBackgroundColor(getResources().getColor(R.color.snackBar, getTheme()))
                                                                                            .setText(R.string.content_snackBar_task_failed)
                                                                                            .setTextColor(getResources().getColor(R.color.snackBarText, getTheme()))
                                                                                            .setTextTypeface(getResources().getFont(R.font.sf_ui_display_regular))
                                                                                            .setIcon(R.drawable.ic_outline_warning_24)
                                                                                            .setTextSize(16)
                                                                                            .setDuration(Snacky.LENGTH_LONG)
                                                                                            .build()
                                                                                            .show();

                                                                                    if (!new NetworkInfoUtility(getApplicationContext()).isConnectedToInternet()) {
                                                                                        mProgressDialog.dismiss();

                                                                                        new MaterialDialog.Builder(AdminRegistrationStepThreeActivity.this)
                                                                                                .typeface(getResources().getFont(R.font.sf_ui_display_medium), getResources().getFont(R.font.sf_ui_display_regular))
                                                                                                .title(R.string.title_dialog_no_internet)
                                                                                                .content(R.string.content_dialog_no_internet)
                                                                                                .icon(Objects.requireNonNull(getDrawable(R.drawable.ic_baseline_signal_wifi_off_24)))
                                                                                                .positiveText(R.string.positive_text_dialog_no_internet)
                                                                                                .negativeText(R.string.negative_text_dialog_no_internet)
                                                                                                .canceledOnTouchOutside(false)
                                                                                                .cancelable(false)
                                                                                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                                                                    @Override
                                                                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                                                        Intent intent = new Intent(getApplicationContext(), CommonRegistrationStepOneActivity.class);
                                                                                                        startActivity(intent);
                                                                                                    }
                                                                                                })
                                                                                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                                                                    @Override
                                                                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                                                    }
                                                                                                })
                                                                                                .show();
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @SuppressLint("UseCompatLoadingForDrawables")
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.e("Reference not created", Objects.requireNonNull(e.getMessage()));

                                                                    mProgressDialog.dismiss();

                                                                    Snacky.builder()
                                                                            .setActivity(AdminRegistrationStepThreeActivity.this)
                                                                            .setBackgroundColor(getResources().getColor(R.color.snackBar, getTheme()))
                                                                            .setText(R.string.content_snackBar_task_failed)
                                                                            .setTextColor(getResources().getColor(R.color.snackBarText, getTheme()))
                                                                            .setTextTypeface(getResources().getFont(R.font.sf_ui_display_regular))
                                                                            .setIcon(R.drawable.ic_outline_warning_24)
                                                                            .setTextSize(16)
                                                                            .setDuration(Snacky.LENGTH_LONG)
                                                                            .build()
                                                                            .show();

                                                                    if (!new NetworkInfoUtility(getApplicationContext()).isConnectedToInternet()) {
                                                                        mProgressDialog.dismiss();

                                                                        new MaterialDialog.Builder(AdminRegistrationStepThreeActivity.this)
                                                                                .typeface(getResources().getFont(R.font.sf_ui_display_medium), getResources().getFont(R.font.sf_ui_display_regular))
                                                                                .title(R.string.title_dialog_no_internet)
                                                                                .content(R.string.content_dialog_no_internet)
                                                                                .icon(Objects.requireNonNull(getDrawable(R.drawable.ic_baseline_signal_wifi_off_24)))
                                                                                .positiveText(R.string.positive_text_dialog_no_internet)
                                                                                .negativeText(R.string.negative_text_dialog_no_internet)
                                                                                .canceledOnTouchOutside(false)
                                                                                .cancelable(false)
                                                                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                                                    @Override
                                                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                                        Intent intent = new Intent(getApplicationContext(), CommonRegistrationStepOneActivity.class);
                                                                                        startActivity(intent);
                                                                                    }
                                                                                })
                                                                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                                                    @Override
                                                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                                    }
                                                                                })
                                                                                .show();
                                                                    }
                                                                }
                                                            });
                                                }
                                            });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("Failed to upload image", Objects.requireNonNull(e.getMessage()));
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Account not created", Objects.requireNonNull(e.getMessage()));

                            if (e instanceof FirebaseAuthUserCollisionException) {
                                mProgressDialog.dismiss();

                                new MaterialDialog.Builder(AdminRegistrationStepThreeActivity.this)
                                        .typeface(getResources().getFont(R.font.sf_ui_display_medium), getResources().getFont(R.font.sf_ui_display_regular))
                                        .title(R.string.title_dialog_email_already_registered)
                                        .content(R.string.content_dialog_email_already_registered)
                                        .icon(Objects.requireNonNull(getDrawable(R.drawable.ic_baseline_warning_24)))
                                        .positiveText(getResources().getString(R.string.positive_text_dialog_email_already_registered))
                                        .negativeText(getResources().getString(R.string.negative_text_dialog_email_already_registered))
                                        .canceledOnTouchOutside(false)
                                        .cancelable(false)
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                Intent intent = new Intent(AdminRegistrationStepThreeActivity.this, CommonRegistrationStepOneActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                Intent intent = new Intent(AdminRegistrationStepThreeActivity.this, LoginActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .show();
                            } else {
                                mProgressDialog.dismiss();

                                Snacky.builder()
                                        .setActivity(AdminRegistrationStepThreeActivity.this)
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
                                new MaterialDialog.Builder(AdminRegistrationStepThreeActivity.this)
                                        .typeface(getResources().getFont(R.font.sf_ui_display_medium), getResources().getFont(R.font.sf_ui_display_regular))
                                        .title(R.string.title_dialog_no_internet)
                                        .content(R.string.content_dialog_no_internet)
                                        .icon(Objects.requireNonNull(getDrawable(R.drawable.ic_baseline_signal_wifi_off_24)))
                                        .positiveText(R.string.positive_text_dialog_no_internet)
                                        .negativeText(R.string.negative_text_dialog_no_internet)
                                        .canceledOnTouchOutside(false)
                                        .cancelable(false)
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                Intent intent = new Intent(getApplicationContext(), CommonRegistrationStepOneActivity.class);
                                                startActivity(intent);
                                            }
                                        })
                                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            }
                                        })
                                        .show();
                            }
                        }
                    });
        } else {
            new MaterialDialog.Builder(AdminRegistrationStepThreeActivity.this)
                    .typeface(getResources().getFont(R.font.sf_ui_display_medium), getResources().getFont(R.font.sf_ui_display_regular))
                    .title(R.string.title_dialog_no_internet)
                    .content(R.string.content_dialog_no_internet)
                    .icon(Objects.requireNonNull(getDrawable(R.drawable.ic_baseline_signal_wifi_off_24)))
                    .positiveText(R.string.positive_text_dialog_no_internet)
                    .negativeText(R.string.negative_text_dialog_no_internet)
                    .canceledOnTouchOutside(false)
                    .cancelable(false)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Intent intent = new Intent(getApplicationContext(), CommonRegistrationStepOneActivity.class);
                            startActivity(intent);
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(AdminRegistrationStepThreeActivity.this, AdminRegistrationStepTwoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    private String validUsername() {
        String username = Objects.requireNonNull(mUsernameLayout.getEditText()).getText().toString().trim();
        Pattern usernamePattern = Pattern.compile("[a-zA-Z0-9._:\\p{Pd}]{1,20}");
        Matcher usernameMatcher = usernamePattern.matcher(username);

        if (username.isEmpty()) {
            mUsernameLayout.setError(getString(R.string.error_empty_roll_no_or_id));
            requestFocus(mUsernameField);
            return null;
        } else {
            if (usernameMatcher.matches()) {
                mUsernameLayout.setErrorEnabled(false);
                mUsername = username;
                return username;
            } else {
                mUsernameLayout.setError(getString(R.string.error_invalid_roll_no_or_id));
                requestFocus(mUsernameField);
                return null;
            }
        }
    }

    private String validAbout() {
        String about = Objects.requireNonNull(mAboutLayout.getEditText()).getText().toString().trim();
        Pattern aboutPattern = Pattern.compile("^.{1,256}$");
        Matcher aboutMatcher = aboutPattern.matcher(about);

        if (about.isEmpty()) {
            mAboutLayout.setError(getString(R.string.error_empty_about));
            requestFocus(mAboutField);
            return null;
        } else {
            if (aboutMatcher.matches()) {
                mAboutLayout.setErrorEnabled(false);
                mAbout = about;
                return about;
            } else {
                mAboutLayout.setError(getString(R.string.error_invalid_about));
                requestFocus(mAboutField);
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
                case R.id.textInputEditText_username:
                    validUsername();
                    break;
                case R.id.textInputEditText_about:
                    validAbout();
                    break;
            }
        }
    }
}