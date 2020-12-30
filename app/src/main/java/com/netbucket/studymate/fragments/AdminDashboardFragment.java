package com.netbucket.studymate.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.netbucket.studymate.R;
import com.netbucket.studymate.activities.FacultyMembersActivity;
import com.netbucket.studymate.activities.StudentsActivity;

public class AdminDashboardFragment extends Fragment {

    RelativeLayout mButtonViewStudents;
    RelativeLayout mButtonViewFacultyMembers;
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

        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
        mFullNameView = view.findViewById(R.id.textView_full_name);
        mFullNameView.setText(mFullName);
        mButtonViewStudents = view.findViewById(R.id.button_view_students);
        mButtonViewFacultyMembers = view.findViewById(R.id.button_view_faculty_members);


        mButtonViewStudents.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), StudentsActivity.class);
            startActivity(intent);
        });
        mButtonViewFacultyMembers.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), FacultyMembersActivity.class);
            startActivity(intent);
        });
        return view;
    }
}