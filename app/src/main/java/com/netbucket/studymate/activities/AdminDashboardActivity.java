package com.netbucket.studymate.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.netbucket.studymate.R;
import com.netbucket.studymate.fragments.AdminChatsFragment;
import com.netbucket.studymate.fragments.AdminDashboardFragment;
import com.netbucket.studymate.fragments.AdminNotificationsFragment;
import com.netbucket.studymate.utils.SessionManager;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class AdminDashboardActivity extends AppCompatActivity {
    ImageView mSettingsButton;
    RelativeLayout mInfoButton;
    CircleImageView mProfileImageView;
    FragmentContainerView mFragmentContainerView;
    BottomNavigationView mBottomNavigationView;
    String mFullName;
    String mProfileImageUri;
    String mUid;
    String mPath;
    private AdminChatsFragment mAdminChatsFragment;
    private AdminNotificationsFragment mAdminNotificationsFragment;
    private long mBackPressedTime;
    private Toast mExitToast;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mCurrentUser = mAuth.getCurrentUser();
        if (mCurrentUser == null) {
            SessionManager sessionManager = new SessionManager(AdminDashboardActivity.this, SessionManager.SESSION_USER_SESSION);
            sessionManager.invalidateSession();
            Intent loginIntent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);
            finish();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        mFragmentContainerView = findViewById(R.id.bottom_nav_host_fragment_admin);
        mBottomNavigationView = findViewById(R.id.bottom_nav_view_admin);
        mProfileImageView = findViewById(R.id.imageView_profile_image);
        mSettingsButton = findViewById(R.id.imageView_settings);
        mInfoButton = findViewById(R.id.relativeLayout_logo);

        getDataFromSession();

        mAdminChatsFragment = new AdminChatsFragment();
        mAdminNotificationsFragment = new AdminNotificationsFragment();

        BadgeDrawable badgeDrawable = mBottomNavigationView.getOrCreateBadge(R.id.navigation_chats);
        badgeDrawable.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        badgeDrawable.setVisible(true);
        badgeDrawable.setNumber(20);
        badgeDrawable.setMaxCharacterCount(3);

        BadgeDrawable badgeDrawable2 = mBottomNavigationView.getOrCreateBadge(R.id.navigation_notifications);
        badgeDrawable2.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        badgeDrawable2.setVisible(true);
        badgeDrawable2.setNumber(67);
        badgeDrawable2.setMaxCharacterCount(3);

        setFragment(newInstance("fullName", mFullName));

        mBottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_dashboard:
                    setFragment(newInstance("fullName", mFullName));
                    return true;

                case R.id.navigation_chats:
                    setFragment(mAdminChatsFragment);
                    return true;

                case R.id.navigation_notifications:
                    setFragment(mAdminNotificationsFragment);
                    return true;

                default:
                    return false;
            }
        });

        mProfileImageView.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        mSettingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        mInfoButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, InfoActivity.class);
            startActivity(intent);
        });
    }

    private void getDataFromSession() {
        SessionManager sessionManager = new SessionManager(AdminDashboardActivity.this, SessionManager.SESSION_USER_SESSION);
        HashMap<String, String> userData = sessionManager.getUserDataFromSession();
        mFullName = userData.get(SessionManager.KEY_FULL_NAME);
        mProfileImageUri = userData.get(SessionManager.KEY_PROFILE_IMAGE_URI);
        mUid = userData.get(SessionManager.KEY_UID);
        mPath = userData.get(SessionManager.KEY_USER_PATH);

        Glide
                .with(AdminDashboardActivity.this)
                .load(mProfileImageUri)
                .centerCrop()
                .placeholder(R.drawable.ic_outline_location_city_24)
                .into(mProfileImageView);
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.bottom_nav_host_fragment_admin, fragment);
        fragmentTransaction.commit();
    }

    @SuppressLint("StaticFieldLeak")
    public AdminDashboardFragment newInstance(String key, String value) {
        AdminDashboardFragment mAdminDashboardFragment = new AdminDashboardFragment();
        Bundle args = new Bundle();
        args.putString(key, value);
        mAdminDashboardFragment.setArguments(args);
        return mAdminDashboardFragment;
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