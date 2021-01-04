package com.netbucket.studymate.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.netbucket.studymate.R;
import com.netbucket.studymate.fragments.FacultyMemberChatsFragment;
import com.netbucket.studymate.fragments.FacultyMemberDashboardFragment;
import com.netbucket.studymate.fragments.FacultyMemberNotificationsFragment;
import com.netbucket.studymate.utils.SessionManager;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class FacultyMemberDashboardActivity extends AppCompatActivity {
    ImageView mSettingsButton;
    RelativeLayout mInfoButton;
    CircleImageView mProfileImageView;
    FragmentContainerView mFragmentContainerView;
    BottomNavigationView mBottomNavigationView;
    String mFullName;
    String mProfileImageUri;
    String mUid;
    String mPath;
    private FacultyMemberDashboardFragment mFacultyMemberDashboardFragment;

    private FacultyMemberChatsFragment mFacultyMemberChatsFragment;
    private FacultyMemberNotificationsFragment mFacultyMemberNotificationsFragment;
    private long mBackPressedTime;
    private Toast mExitToast;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mCurrentUser = mAuth.getCurrentUser();
        if (mCurrentUser == null) {
            SessionManager sessionManager = new SessionManager(FacultyMemberDashboardActivity.this, SessionManager.SESSION_USER_SESSION);
            sessionManager.invalidateSession();
            Intent loginIntent = new Intent(FacultyMemberDashboardActivity.this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_member_dashboard);

        mFragmentContainerView = findViewById(R.id.bottom_nav_host_fragment_faculty_member);
        mBottomNavigationView = findViewById(R.id.bottom_nav_view_faculty_member);
        mProfileImageView = findViewById(R.id.imageView_profile_image);
        mSettingsButton = findViewById(R.id.imageView_settings);
        mInfoButton = findViewById(R.id.relativeLayout_logo);

        getDataFromSession();

        mFacultyMemberChatsFragment = new FacultyMemberChatsFragment();
        mFacultyMemberNotificationsFragment = new FacultyMemberNotificationsFragment();

        setFragment(newInstance("fullName", mFullName));

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_dashboard:
//                        mBottomNavigationView.setItemBackgroundResource();
                        setFragment(newInstance("fullName", mFullName));
                        return true;

                    case R.id.navigation_chats:
//                        mBottomNavigationView.setItemBackgroundResource();
                        setFragment(mFacultyMemberChatsFragment);
                        return true;

                    case R.id.navigation_notifications:
//                        mBottomNavigationView.setItemBackgroundResource();
                        setFragment(mFacultyMemberNotificationsFragment);
                        return true;
                    default:
                        return false;
                }
            }
        });

        mProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FacultyMemberDashboardActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FacultyMemberDashboardActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        mInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FacultyMemberDashboardActivity.this, InfoActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getDataFromSession() {
        SessionManager sessionManager = new SessionManager(FacultyMemberDashboardActivity.this, SessionManager.SESSION_USER_SESSION);
        HashMap<String, String> userData = sessionManager.getUserDataFromSession();
        mFullName = userData.get(SessionManager.KEY_FULL_NAME);
        mProfileImageUri = userData.get(SessionManager.KEY_PROFILE_IMAGE_URI);
        mUid = userData.get(SessionManager.KEY_UID);
        mPath = userData.get(SessionManager.KEY_USER_PATH);
        if (!mProfileImageUri.equals("null")) {
            Glide
                    .with(FacultyMemberDashboardActivity.this)
                    .load(mProfileImageUri)
                    .centerCrop()
                    .placeholder(R.drawable.avatar)
                    .into(mProfileImageView);
        } else {
            Glide
                    .with(FacultyMemberDashboardActivity.this)
                    .load(R.drawable.avatar)
                    .centerCrop()
                    .placeholder(R.drawable.avatar)
                    .into(mProfileImageView);
        }
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.bottom_nav_host_fragment_faculty_member, fragment);
        fragmentTransaction.commit();
    }

    @SuppressLint("StaticFieldLeak")
    public FacultyMemberDashboardFragment newInstance(String key, String value) {
        FacultyMemberDashboardFragment mFacultyMemberDashboardFragment = new FacultyMemberDashboardFragment();
        Bundle args = new Bundle();
        args.putString(key, value);
        mFacultyMemberDashboardFragment.setArguments(args);
        return mFacultyMemberDashboardFragment;
    }

    @Override
    public void onBackPressed() {
        if (mBackPressedTime + 2000 > System.currentTimeMillis()) {
            mExitToast.cancel();
            super.onBackPressed();
            return;
        } else {
            mExitToast = Toasty.custom(getApplicationContext(), "Press back again to exit", R.drawable.img_logo, R.color.logo_color, Toast.LENGTH_SHORT, true, false);
            mExitToast.show();
        }
        mBackPressedTime = System.currentTimeMillis();
    }
}