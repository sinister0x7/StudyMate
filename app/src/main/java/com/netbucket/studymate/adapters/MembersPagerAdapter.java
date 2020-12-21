package com.netbucket.studymate.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.netbucket.studymate.fragments.FacultyFragment;
import com.netbucket.studymate.fragments.PendingRequestsFragment;
import com.netbucket.studymate.fragments.StudentsFragment;

public class MembersPagerAdapter extends FragmentStateAdapter {
    public MembersPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new StudentsFragment();
            case 1:
                return new FacultyFragment();
            default:
                return new PendingRequestsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
