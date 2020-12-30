package com.netbucket.studymate.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.netbucket.studymate.R;
import com.netbucket.studymate.utils.NetworkInfoUtility;
import com.netbucket.studymate.utils.SessionManager;

import java.util.HashMap;
import java.util.Objects;

import de.mateware.snacky.Snacky;

public class ProfileActivity extends AppCompatActivity {
    MaterialToolbar mToolbar;
    LinearLayout mSemOrYearLayout;
    FloatingActionButton mEditButton;
    MaterialDialog mProgressDialog;
    AppCompatImageView mProfileImage;
    String mFullName;
    String mEmail;
    String mPhoneNumber;
    String mUsername;
    String mAbout;
    String mBirthday;
    String mGender;
    String mInstitute;
    String mRole;
    String mCourse;
    String mIdOrRollNo;
    String mSemOrYear;
    String mProfileImageUri;
    TextView mEmailView;
    TextView mPhoneNumberView;
    TextView mUsernameView;
    TextView mAboutView;
    TextView mBirthdayView;
    TextView mGenderView;
    TextView mInstituteView;
    TextView mRoleView;
    TextView mCourseView;
    TextView mIdOrRollNoView;
    TextView mSemOrYearView;
    Button mLogoutButton;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mToolbar = findViewById(R.id.toolbar_profile);
        mProfileImage = findViewById(R.id.imageView_profile_image);
        mSemOrYearLayout = findViewById(R.id.linearLayout_semester_or_year);
        mEditButton = findViewById(R.id.floatingActionButton_edit_profile);
        mEmailView = findViewById(R.id.textView_email);
        mPhoneNumberView = findViewById(R.id.textView_phone);
        mUsernameView = findViewById(R.id.textView_username);
        mAboutView = findViewById(R.id.textView_about);
        mBirthdayView = findViewById(R.id.textView_birthday);
        mGenderView = findViewById(R.id.textView_gender);
        mInstituteView = findViewById(R.id.textView_institute);
        mRoleView = findViewById(R.id.textView_role);
        mCourseView = findViewById(R.id.textView_course);
        mIdOrRollNoView = findViewById(R.id.textView_id_or_roll_no);
        mSemOrYearView = findViewById(R.id.textView_semester_or_year);
        mLogoutButton = findViewById(R.id.button_logout);

        SessionManager sessionManager = new SessionManager(ProfileActivity.this, SessionManager.SESSION_USER_SESSION);
        HashMap<String, String> userData = sessionManager.getUserDataFromSession();
        mFullName = userData.get(SessionManager.KEY_FULL_NAME);
        mEmail = userData.get(SessionManager.KEY_EMAIL);
        mPhoneNumber = userData.get(SessionManager.KEY_PHONE_NUMBER);
        mUsername = userData.get(SessionManager.KEY_USERNAME);
        mBirthday = userData.get(SessionManager.KEY_BIRTHDAY);
        mGender = userData.get(SessionManager.KEY_GENDER);
        mAbout = userData.get(SessionManager.KEY_ABOUT);
        mRole = userData.get(SessionManager.KEY_ROLE);
        mInstitute = userData.get(SessionManager.KEY_INSTITUTE);
        mCourse = userData.get(SessionManager.KEY_COURSE);
        mIdOrRollNo = userData.get(SessionManager.KEY_ID);
        mSemOrYear = userData.get(SessionManager.KEY_SEM_OR_YEAR);
        mProfileImageUri = userData.get(SessionManager.KEY_PROFILE_IMAGE_URI);
        mToolbar.setTitle(mFullName);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.lead_text_color, getTheme()));

        Glide
                .with(ProfileActivity.this)
                .load(mProfileImageUri)
                .centerCrop()
                .placeholder(R.drawable.ic_outline_location_city_24)
                .into(mProfileImage);

        if (mRole.equals("Student")) {
            mSemOrYearLayout.setVisibility(View.VISIBLE);
        } else {
            mSemOrYearLayout.setVisibility(View.GONE);
        }

        mEmailView.setText(mEmail);
        mPhoneNumberView.setText(mPhoneNumber);
        mUsernameView.setText(mUsername);
        mBirthdayView.setText(mBirthday);
        mGenderView.setText(mGender);
        mAboutView.setText(mAbout);
        mRoleView.setText(mRole);
        mInstituteView.setText(mInstitute);
        mCourseView.setText(mCourse);
        mIdOrRollNoView.setText(mIdOrRollNo);
        mSemOrYearView.setText(mSemOrYear);

        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());

        mLogoutButton.setOnClickListener(v -> {
            if (new NetworkInfoUtility(getApplicationContext()).isConnectedToInternet()) {
                mProgressDialog = new MaterialDialog.Builder(ProfileActivity.this)
                        .typeface(getResources().getFont(R.font.sf_ui_display_medium), getResources().getFont(R.font.sf_ui_display_regular))
                        .progress(true, 0)
                        .canceledOnTouchOutside(false)
                        .content(getResources().getString(R.string.content_dialog_logout))
                        .cancelable(false)
                        .canceledOnTouchOutside(false)
                        .build();
                mProgressDialog.show();

                if (mUser != null) {
                    mAuth.signOut();
                    SessionManager sessionManager1 = new SessionManager(ProfileActivity.this, SessionManager.SESSION_USER_SESSION);
                    sessionManager1.invalidateSession();
                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mProgressDialog.dismiss();
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("Failed to logout:", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

                    mProgressDialog.dismiss();

                    Snacky.builder()
                            .setActivity(ProfileActivity.this)
                            .setBackgroundColor(getResources().getColor(R.color.snackBar, getTheme()))
                            .setText(R.string.content_snackBar_logout_failed)
                            .setTextColor(getResources().getColor(R.color.snackBarText, getTheme()))
                            .setTextTypeface(getResources().getFont(R.font.sf_ui_display_regular))
                            .setIcon(R.drawable.ic_outline_warning_24)
                            .setTextSize(16)
                            .setDuration(Snacky.LENGTH_LONG)
                            .build()
                            .show();
                }
            } else {
                new MaterialDialog.Builder(ProfileActivity.this)
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}