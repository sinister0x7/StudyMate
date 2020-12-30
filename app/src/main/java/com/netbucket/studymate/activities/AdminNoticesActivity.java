package com.netbucket.studymate.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.netbucket.studymate.R;

public class AdminNoticesActivity extends AppCompatActivity {

    FloatingActionButton mAddNoticeButton;
    RecyclerView mRecyclerViewNotices;
    MaterialToolbar mMaterialToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_notices);


        mAddNoticeButton = findViewById(R.id.floatingActionButton_add_notice);
        mRecyclerViewNotices = findViewById(R.id.recyclerView_notices);

        mRecyclerViewNotices.setOnClickListener(v -> addNotice());

        setSupportActionBar(mMaterialToolbar);

        mMaterialToolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void addNotice() {

    }
}