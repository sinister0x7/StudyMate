package com.netbucket.studymate.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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
import com.netbucket.studymate.fragments.FacultyChatsFragment;
import com.netbucket.studymate.fragments.FacultyDashboardFragment;
import com.netbucket.studymate.fragments.FacultyNotificationsFragment;
import com.netbucket.studymate.utils.SessionManager;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class FacultyDashboardActivity extends AppCompatActivity {
    ImageView mSettingsButton;
    RelativeLayout mInfoButton;
    CircleImageView mProfileImageView;
    FragmentContainerView mFragmentContainerView;
    BottomNavigationView mBottomNavigationView;
    String mFullName;
    String mProfileImageUri;
    String mUid;
    String mPath;
    private FacultyDashboardFragment mFacultyDashboardFragment;

    private FacultyChatsFragment mFacultyChatsFragment;
    private FacultyNotificationsFragment mFacultyNotificationsFragment;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mCurrentUser = mAuth.getCurrentUser();
        if (mCurrentUser == null) {
            SessionManager sessionManager = new SessionManager(FacultyDashboardActivity.this, SessionManager.SESSION_USER_SESSION);
            sessionManager.invalidateSession();
            Intent loginIntent = new Intent(FacultyDashboardActivity.this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_dashboard);

        mFragmentContainerView = findViewById(R.id.bottom_nav_host_fragment_faculty);
        mBottomNavigationView = findViewById(R.id.bottom_nav_view_faculty);
        mProfileImageView = findViewById(R.id.circleImageView_profile_image);
        mSettingsButton = findViewById(R.id.imageView_settings);
        mInfoButton = findViewById(R.id.relativeLayout_logo);

        getDataFromSession();

        mFacultyChatsFragment = new FacultyChatsFragment();
        mFacultyNotificationsFragment = new FacultyNotificationsFragment();

        setFragment(newInstance("name", mFullName));

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_dashboard:
//                        mBottomNavigationView.setItemBackgroundResource();
                        setFragment(newInstance("name", mFullName));
                        return true;

                    case R.id.navigation_chats:
//                        mBottomNavigationView.setItemBackgroundResource();
                        setFragment(mFacultyChatsFragment);
                        return true;

                    case R.id.navigation_notifications:
//                        mBottomNavigationView.setItemBackgroundResource();
                        setFragment(mFacultyNotificationsFragment);
                        return true;
                    default:
                        return false;
                }
            }
        });

        mProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FacultyDashboardActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FacultyDashboardActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        mInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FacultyDashboardActivity.this, InfoActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getDataFromSession() {
        SessionManager sessionManager = new SessionManager(FacultyDashboardActivity.this, SessionManager.SESSION_USER_SESSION);
        HashMap<String, String> userData = sessionManager.getUserDataFromSession();
        mFullName = userData.get(SessionManager.KEY_FULL_NAME);
        mProfileImageUri = userData.get(SessionManager.KEY_PROFILE_IMAGE_URI);
        mUid = userData.get(SessionManager.KEY_UID);
        mPath = userData.get(SessionManager.KEY_USER_PATH);
        Glide
                .with(FacultyDashboardActivity.this)
                .load(mProfileImageUri)
                .centerCrop()
                .placeholder(R.drawable.ic_outline_location_city_24)
                .into(mProfileImageView);
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.bottom_nav_host_fragment_faculty, fragment);
        fragmentTransaction.commit();
    }

    @SuppressLint("StaticFieldLeak")
    public FacultyDashboardFragment newInstance(String key, String value) {
        FacultyDashboardFragment mFacultyDashboardFragment = new FacultyDashboardFragment();
        Bundle args = new Bundle();
        args.putString(key, value);
        mFacultyDashboardFragment.setArguments(args);
        return mFacultyDashboardFragment;
    }
}