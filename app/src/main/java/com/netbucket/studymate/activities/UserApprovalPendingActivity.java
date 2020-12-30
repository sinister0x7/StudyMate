package com.netbucket.studymate.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.netbucket.studymate.R;
import com.netbucket.studymate.utils.NetworkInfoUtility;
import com.netbucket.studymate.utils.SessionManager;

import java.util.HashMap;
import java.util.Objects;

import de.mateware.snacky.Snacky;
import es.dmoral.toasty.Toasty;

public class UserApprovalPendingActivity extends AppCompatActivity {

    Button mRefreshButton;
    Button mLogoutButton;
    RelativeLayout mInfoButton;
    ImageView mCloseButton;
    MaterialDialog mProgressDialog;
    String mUserStatus = "disallowed";
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private FirebaseUser mUser;
    private String mUserPath;
    private String mRole;
    private long mBackPressedTime;
    private Toast mExitToast;

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_approval_pending);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();

        mInfoButton = findViewById(R.id.relativeLayout_logo);
        mCloseButton = findViewById(R.id.imageView_close);
        mRefreshButton = findViewById(R.id.button_refresh);
        mLogoutButton = findViewById(R.id.button_logout);

        SessionManager sessionManager = new SessionManager(UserApprovalPendingActivity.this, SessionManager.SESSION_USER_SESSION);
        HashMap<String, String> userData = sessionManager.getUserDataFromSession();
        mRole = userData.get(SessionManager.KEY_ROLE);
        mUserPath = userData.get(SessionManager.KEY_USER_PATH);

        if (mUser == null) {
            sessionManager.invalidateSession();
            mAuth.signOut();
            Intent intent = new Intent(UserApprovalPendingActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mProgressDialog.dismiss();
            startActivity(intent);
            finish();
        }

        checkApprovalStatus();

        mInfoButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserApprovalPendingActivity.this, InfoActivity.class);
            startActivity(intent);
        });

        mRefreshButton.setOnClickListener(v -> checkApprovalStatus());

        mLogoutButton.setOnClickListener(v -> {
            if (new NetworkInfoUtility(getApplicationContext()).isConnectedToInternet()) {
                mProgressDialog = new MaterialDialog.Builder(UserApprovalPendingActivity.this)
                        .typeface(getResources().getFont(R.font.sf_ui_display_medium), getResources().getFont(R.font.sf_ui_display_regular))
                        .progress(true, 0)
                        .canceledOnTouchOutside(false)
                        .content(getResources().getString(R.string.content_dialog_logout))
                        .cancelable(false)
                        .canceledOnTouchOutside(false)
                        .build();
                mProgressDialog.show();

                if (mUser != null) {
                    sessionManager.invalidateSession();
                    mAuth.signOut();
                    Intent intent = new Intent(UserApprovalPendingActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mProgressDialog.dismiss();
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("Failed to logout:", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

                    mProgressDialog.dismiss();

                    Snacky.builder()
                            .setActivity(UserApprovalPendingActivity.this)
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
                new MaterialDialog.Builder(UserApprovalPendingActivity.this)
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("UseCompatLoadingForDrawables")
    public void checkApprovalStatus() {
        if (new NetworkInfoUtility(getApplicationContext()).isConnectedToInternet()) {
            mProgressDialog = new MaterialDialog.Builder(UserApprovalPendingActivity.this)
                    .typeface(getResources().getFont(R.font.sf_ui_display_medium), getResources().getFont(R.font.sf_ui_display_regular))
                    .progress(true, 0)
                    .canceledOnTouchOutside(false)
                    .content(getResources().getString(R.string.content_dialog_please_wait))
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .build();
            mProgressDialog.show();


            Log.i("Role:", Objects.requireNonNull(mRole));

            mStore.document(mUserPath)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        mUserStatus = documentSnapshot.getString("userStatus");
                        if (mUserStatus != null) {
                            Log.i("Approval Status:", mUserStatus);
                            if (mUserStatus.equals("allowed")) {
                                SessionManager sessionManager = new SessionManager(UserApprovalPendingActivity.this, SessionManager.SESSION_USER_SESSION);
                                sessionManager.createUserSession(mUserStatus);
                                switch (mRole) {
                                    case "Student":
                                        Intent studentDashboardIntent = new Intent(UserApprovalPendingActivity.this, StudentDashboardActivity.class);
                                        mProgressDialog.dismiss();
                                        studentDashboardIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(studentDashboardIntent);
                                        finish();
                                        break;

                                    case "Faculty Member":
                                        Intent facultyDashboardIntent = new Intent(UserApprovalPendingActivity.this, FacultyMemberDashboardActivity.class);
                                        mProgressDialog.dismiss();
                                        facultyDashboardIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(facultyDashboardIntent);
                                        finish();
                                        break;

                                    case "Admin/HOD":
                                        Intent adminDashboardIntent = new Intent(UserApprovalPendingActivity.this, AdminDashboardActivity.class);
                                        mProgressDialog.dismiss();
                                        adminDashboardIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(adminDashboardIntent);
                                        finish();
                                        break;
                                    default:
                                        break;
                                }
                            } else {
                                mProgressDialog.dismiss();
                                Log.i("User Not Approved ", Objects.requireNonNull(mAuth.getUid()));
                                Snacky.builder()
                                        .setActivity(UserApprovalPendingActivity.this)
                                        .setBackgroundColor(getResources().getColor(R.color.snackBar, getTheme()))
                                        .setText(R.string.content_snackBar_user_not_allowed)
                                        .setTextColor(getResources().getColor(R.color.snackBarText, getTheme()))
                                        .setTextTypeface(getResources().getFont(R.font.sf_ui_display_regular))
                                        .setIcon(R.drawable.ic_outline_warning_24)
                                        .setTextSize(16)
                                        .setDuration(Snacky.LENGTH_LONG)
                                        .build()
                                        .show();
                            }
                        } else {
                            mProgressDialog.dismiss();
                            Snacky.builder()
                                    .setActivity(UserApprovalPendingActivity.this)
                                    .setBackgroundColor(getResources().getColor(R.color.snackBar, getTheme()))
                                    .setText(R.string.content_snackBar_task_failed)
                                    .setTextColor(getResources().getColor(R.color.snackBarText, getTheme()))
                                    .setTextTypeface(getResources().getFont(R.font.sf_ui_display_regular))
                                    .setIcon(R.drawable.ic_outline_warning_24)
                                    .setTextSize(16)
                                    .setDuration(Snacky.LENGTH_LONG)
                                    .build()
                                    .show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        mProgressDialog.dismiss();
                        Log.e("Redirection Failed", Objects.requireNonNull(e.getMessage()));
                        Snacky.builder()
                                .setActivity(UserApprovalPendingActivity.this)
                                .setBackgroundColor(getResources().getColor(R.color.snackBar, getTheme()))
                                .setText(R.string.content_snackBar_unable_to_check_approval_status)
                                .setTextColor(getResources().getColor(R.color.snackBarText, getTheme()))
                                .setTextTypeface(getResources().getFont(R.font.sf_ui_display_regular))
                                .setIcon(R.drawable.ic_outline_warning_24)
                                .setTextSize(16)
                                .setDuration(Snacky.LENGTH_LONG)
                                .build()
                                .show();
                        if (!new NetworkInfoUtility(getApplicationContext()).isConnectedToInternet()) {
                            mProgressDialog.dismiss();
                            new MaterialDialog.Builder(UserApprovalPendingActivity.this)
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
            new MaterialDialog.Builder(UserApprovalPendingActivity.this)
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