package com.netbucket.studymate.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.netbucket.studymate.R;
import com.netbucket.studymate.fragments.StudentChatsFragment;
import com.netbucket.studymate.fragments.StudentDashboardFragment;
import com.netbucket.studymate.fragments.StudentNotificationsFragment;
import com.netbucket.studymate.utils.SessionManager;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class StudentDashboardActivity extends AppCompatActivity {
    ImageView mSettingsButton;
    RelativeLayout mInfoButton;
    CircleImageView mProfileImageView;
    FragmentContainerView mFragmentContainerView;
    BottomNavigationView mBottomNavigationView;
    String mFullName;
    String mProfileImageUri;
    String mUid;
    String mPath;
//    private FirebaseAuth mAuth;
//    private FirebaseUser mCurrentUser;

    private StudentChatsFragment mStudentChatsFragment;
    private StudentNotificationsFragment mStudentNotificationsFragment;

    private long mBackPressedTime;
    private Toast mExitToast;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mCurrentUser = mAuth.getCurrentUser();
        if (mCurrentUser == null) {
            SessionManager sessionManager = new SessionManager(StudentDashboardActivity.this, SessionManager.SESSION_USER_SESSION);
            sessionManager.invalidateSession();
            Intent loginIntent = new Intent(StudentDashboardActivity.this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);
            finish();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        mFragmentContainerView = findViewById(R.id.bottom_nav_host_fragment_student);
        mBottomNavigationView = findViewById(R.id.bottom_nav_view_student);
        mProfileImageView = findViewById(R.id.imageView_profile_image);
        mSettingsButton = findViewById(R.id.imageView_settings);
        mInfoButton = findViewById(R.id.relativeLayout_logo);
        getDataFromSession();

//        getName();

//        mStudentDashboardFragment = new StudentDashboardFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString("name", mFullName);
//        mStudentDashboardFragment.setArguments(bundle);
        mStudentChatsFragment = new StudentChatsFragment();
        mStudentNotificationsFragment = new StudentNotificationsFragment();

        setFragment(newInstance("fullName", mFullName));

        mBottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_dashboard:
//                        mBottomNavigationView.setItemBackgroundResource();
                    setFragment(newInstance("fullName", mFullName));
                    return true;

                case R.id.navigation_chats:
//                        mBottomNavigationView.setItemBackgroundResource();
                    setFragment(mStudentChatsFragment);
                    return true;

                case R.id.navigation_notifications:
//                        mBottomNavigationView.setItemBackgroundResource();
                    setFragment(mStudentNotificationsFragment);
                    return true;
                default:
                    return false;
            }
        });

        mProfileImageView.setOnClickListener(v -> {
            Intent intent = new Intent(StudentDashboardActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        mSettingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(StudentDashboardActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        mInfoButton.setOnClickListener(v -> {
            Intent intent = new Intent(StudentDashboardActivity.this, InfoActivity.class);
            startActivity(intent);
        });

//        BottomNavigationView navView = findViewById(R.id.nav_view);
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(navView, navController);

//        mAuth = FirebaseAuth.getInstance();
//        mCurrentUser = mAuth.getCurrentUser();

    }

    private void getDataFromSession() {
        SessionManager sessionManager = new SessionManager(StudentDashboardActivity.this, SessionManager.SESSION_USER_SESSION);
        HashMap<String, String> userData = sessionManager.getUserDataFromSession();
        mFullName = userData.get(SessionManager.KEY_FULL_NAME);
        mProfileImageUri = userData.get(SessionManager.KEY_PROFILE_IMAGE_URI);
        mUid = userData.get(SessionManager.KEY_UID);
        mPath = userData.get(SessionManager.KEY_USER_PATH);
        Glide
                .with(StudentDashboardActivity.this)
                .load(mProfileImageUri)
                .centerCrop()
                .placeholder(R.drawable.ic_outline_location_city_24)
                .into(mProfileImageView);
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.bottom_nav_host_fragment_student, fragment);
        fragmentTransaction.commit();
    }

    @SuppressLint("StaticFieldLeak")
    public StudentDashboardFragment newInstance(String key, String value) {
        StudentDashboardFragment mStudentDashboardFragment = new StudentDashboardFragment();
        Bundle args = new Bundle();
        args.putString(key, value);
        mStudentDashboardFragment.setArguments(args);
        return mStudentDashboardFragment;
    }

    @Override
    public void onBackPressed() {
        if (mBackPressedTime + 2000 > System.currentTimeMillis()) {
            mExitToast.cancel();
            super.onBackPressed();
            return;
        } else {
            mExitToast = Toasty.custom(getApplicationContext(), "Press back again to exit", R.drawable.img_logo, R.color.logo_color, Toast.LENGTH_SHORT, true, true);
            mExitToast.show();
        }
        mBackPressedTime = System.currentTimeMillis();
    }
}