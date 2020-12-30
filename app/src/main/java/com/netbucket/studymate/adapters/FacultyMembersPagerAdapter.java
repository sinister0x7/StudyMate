package com.netbucket.studymate.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.netbucket.studymate.fragments.FacultyMemberPendingRequestsFragment;
import com.netbucket.studymate.fragments.SuspendedFacultyMembersFragment;
import com.netbucket.studymate.fragments.FacultyMembersFragment;

public class FacultyMembersPagerAdapter extends FragmentStateAdapter {
    public FacultyMembersPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FacultyMembersFragment();
            case 1:
                return new FacultyMemberPendingRequestsFragment();
            default:
                return new SuspendedFacultyMembersFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
