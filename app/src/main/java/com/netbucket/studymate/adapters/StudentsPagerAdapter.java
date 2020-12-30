package com.netbucket.studymate.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.netbucket.studymate.fragments.StudentPendingRequestsFragment;
import com.netbucket.studymate.fragments.SuspendedStudentsFragment;
import com.netbucket.studymate.fragments.StudentsFragment;

public class StudentsPagerAdapter extends FragmentStateAdapter {
    public StudentsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new StudentsFragment();
            case 1:
                return new StudentPendingRequestsFragment();
            default:
                return new SuspendedStudentsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
