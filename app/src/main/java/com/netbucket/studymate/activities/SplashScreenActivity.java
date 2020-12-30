package com.netbucket.studymate.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.netbucket.studymate.R;
import com.netbucket.studymate.utils.SessionManager;

import java.util.HashMap;
import java.util.Objects;

public class SplashScreenActivity extends AppCompatActivity {

    static final int SPLASH_TIMER = 2000;
    Animation mSlideUp;
    Animation mSlideDown;
    ImageView mBrandLogo;
    TextView mBrandName;
    TextView mTextMadeWith;
    ImageView mLove;
    TextView mTextInIndia;
    String mUserStatus;
    String mRole;
    SharedPreferences onBoardingScreenPrefs;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        mSlideDown = AnimationUtils.loadAnimation(this, R.anim.anim_fade_in_bottom);
        mSlideUp = AnimationUtils.loadAnimation(this, R.anim.aim_fade_in_top);

        mBrandLogo = findViewById(R.id.imageView_logo);
        mBrandName = findViewById(R.id.textView_brand_name);
        mTextMadeWith = findViewById(R.id.textView_made_with);
        mLove = findViewById(R.id.imageView_love);
        mTextInIndia = findViewById(R.id.textView_in_india);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mBrandLogo.setAnimation(mSlideDown);
        mBrandName.setAnimation(mSlideUp);
        mTextMadeWith.setAnimation(mSlideUp);
        mLove.setAnimation(mSlideUp);
        mTextInIndia.setAnimation(mSlideUp);

        new Handler().postDelayed(() -> {
            onBoardingScreenPrefs = getSharedPreferences("onBoardingScreen", MODE_PRIVATE);
            boolean isFirstTime = onBoardingScreenPrefs.getBoolean("firstTime", true);
            SessionManager sessionManager = new SessionManager(SplashScreenActivity.this, SessionManager.SESSION_USER_SESSION);

            if (isFirstTime) {
                SharedPreferences.Editor editor = onBoardingScreenPrefs.edit();
                editor.putBoolean("firstTime", false);
                editor.apply();

                Intent intent = new Intent(getApplicationContext(), OnBoardingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else if ((mUser != null) && sessionManager.checkIfLoggedIn()) {
                HashMap<String, String> userData = sessionManager.getUserDataFromSession();
                mUserStatus = userData.get(SessionManager.KEY_USER_STATUS);
                mRole = userData.get(SessionManager.KEY_ROLE);

                if (mUserStatus.equals("allowed")) {

                    switch (Objects.requireNonNull(mRole)) {
                        case "Student":
                            Intent studentDashboardIntent = new Intent(SplashScreenActivity.this, StudentDashboardActivity.class);
                            studentDashboardIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(studentDashboardIntent);
                            finish();
                            break;

                        case "Faculty Member":
                            Intent facultyDashboardIntent = new Intent(SplashScreenActivity.this, FacultyMemberDashboardActivity.class);
                            facultyDashboardIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(facultyDashboardIntent);
                            finish();
                            break;

                        case "Admin/HOD":
                            Intent adminDashboardIntent = new Intent(SplashScreenActivity.this, AdminDashboardActivity.class);
                            adminDashboardIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(adminDashboardIntent);
                            finish();
                            break;
                        default:
                            break;
                    }
                } else {
                    Intent intent = new Intent(SplashScreenActivity.this, UserApprovalPendingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            } else {
                mAuth.signOut();
                sessionManager.invalidateSession();

                Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIMER);
    }
}