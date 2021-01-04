package com.netbucket.studymate.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.netbucket.studymate.R;

import java.util.Objects;

public class ViewUserProfileActivity extends AppCompatActivity {

    MaterialToolbar mToolbar;
    LinearLayout mSemOrYearLayout;
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
    String mTermOrYear;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_profile);

        Intent intent = getIntent();
        mFullName = intent.getStringExtra("fullName");
        mEmail = intent.getStringExtra("email");
        mPhoneNumber = intent.getStringExtra("phoneNumber");
        mUsername = intent.getStringExtra("username");
        mAbout = intent.getStringExtra("about");
        mBirthday = intent.getStringExtra("birthday");
        mGender = intent.getStringExtra("gender");
        mInstitute = intent.getStringExtra("institute");
        mRole = intent.getStringExtra("role");
        mCourse = intent.getStringExtra("course");
        mIdOrRollNo = intent.getStringExtra("id");
        mTermOrYear = intent.getStringExtra("termOrYear");
        mProfileImageUri = intent.getStringExtra("profileImageUri");

        mToolbar = findViewById(R.id.toolbar_profile);
        mProfileImage = findViewById(R.id.imageView_profile_image);
        mSemOrYearLayout = findViewById(R.id.linearLayout_semester_or_year);
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

        mToolbar.setTitle(mFullName);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.lead_text_color, getTheme()));

        if (!mProfileImageUri.equals("null")) {
            Glide
                    .with(ViewUserProfileActivity.this)
                    .load(mProfileImageUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_outline_location_city_24)
                    .into(mProfileImage);
        } else {
            Glide
                    .with(ViewUserProfileActivity.this)
                    .load(R.drawable.ic_baseline_arrow_back_36)
                    .centerCrop()
                    .placeholder(R.drawable.ic_outline_location_city_24)
                    .into(mProfileImage);
        }

        if (mRole.equals("Student")) {
            mSemOrYearLayout.setVisibility(View.VISIBLE);
        } else {
            mSemOrYearLayout.setVisibility(View.GONE);
        }

        mEmailView.setText(mEmail);
        mPhoneNumberView.setText(mPhoneNumber);
        mUsernameView.setText(mUsername);
        mAboutView.setText(mAbout);
        mBirthdayView.setText(mBirthday);
        mGenderView.setText(mGender);
        mInstituteView.setText(mInstitute);
        mRoleView.setText(mRole);
        mCourseView.setText(mCourse);
        mIdOrRollNoView.setText(mIdOrRollNo);
        mSemOrYearView.setText(mTermOrYear);

        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

}