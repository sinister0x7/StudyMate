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
import com.netbucket.studymate.activities.MembersActivity;

public class AdminDashboardFragment extends Fragment {

    RelativeLayout mButtonViewClasses;
    String name;
    TextView mName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString("name");
        }

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
        mName = root.findViewById(R.id.textView_name);
        mName.setText(name);
        mButtonViewClasses = root.findViewById(R.id.button_view_requests);


        mButtonViewClasses.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MembersActivity.class);
            startActivity(intent);
        });
        return root;
    }
}