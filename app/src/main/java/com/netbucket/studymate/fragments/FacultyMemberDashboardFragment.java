package com.netbucket.studymate.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netbucket.studymate.R;
import com.netbucket.studymate.activities.LoginActivity;

public class FacultyMemberDashboardFragment extends Fragment {

    RelativeLayout mButtonViewClasses;
    String mFullName;
    TextView mFullNameView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFullName = getArguments().getString("fullName");
        }

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_faculty_member_dashboard, container, false);
        mFullNameView = view.findViewById(R.id.textView_full_name);
        mFullNameView.setText(mFullName);
        mButtonViewClasses = view.findViewById(R.id.button_view_classes);


        mButtonViewClasses.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        });
        return view;
    }
}