package com.netbucket.studymate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.ferfalk.simplesearchview.SimpleSearchView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.netbucket.studymate.R;
import com.netbucket.studymate.adapters.FacultyMemberAdapter;
import com.netbucket.studymate.adapters.FacultyMemberPendingRequestsAdapter;
import com.netbucket.studymate.adapters.FacultyMembersPagerAdapter;
import com.netbucket.studymate.fragments.FacultyMemberPendingRequestsFragment;
import com.netbucket.studymate.fragments.FacultyMembersFragment;
import com.netbucket.studymate.fragments.SuspendedFacultyMembersFragment;
import com.netbucket.studymate.model.FacultyMember;

public class FacultyMembersActivity extends AppCompatActivity {

    SimpleSearchView searchView;
    MaterialToolbar materialToolbar;
    int mPosition=0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (searchView.onActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_members);

        materialToolbar = findViewById(R.id.toolbar_faculty_members);
        searchView = findViewById(R.id.searchView_faculty_members);
        searchView.setTabLayout(findViewById(R.id.tabLayout_faculty_members));
        searchView.closeSearch();
        setSupportActionBar(materialToolbar);

        materialToolbar.setNavigationOnClickListener(v -> onBackPressed());

        ViewPager2 viewPager = findViewById(R.id.viewPager_faculty_members);
        viewPager.setAdapter(new FacultyMembersPagerAdapter(this));
        TabLayout tabLayout = findViewById(R.id.tabLayout_faculty_members);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            mPosition= position;
            switch (position) {
                case 0:
                    tab.setText(R.string.title_tab_faculty_members);
                    tab.setIcon(R.drawable.selector_item_members);
                    break;
                case 1:
                    tab.setText(R.string.title_tab_pending_requests);
                    tab.setIcon(R.drawable.selector_item_pending_requests);
                    BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                    badgeDrawable.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                    badgeDrawable.setVisible(true);
                    badgeDrawable.setNumber(4);
                    badgeDrawable.setMaxCharacterCount(3);
                    break;
                case 2:
                    tab.setText(R.string.title_tab_suspended_faculty_members);
                    tab.setIcon(R.drawable.selector_item_suspended_members);
                    break;
            }
        });

        tabLayoutMediator.attach();

        searchView.setOnQueryTextListener(new SimpleSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                switch (mPosition) {
                    case 0:
                    FacultyMembersFragment facultyMembersFragment = new FacultyMembersFragment();
                    facultyMembersFragment.processSearch(query);
                    break;
                    case 1:
                        FacultyMemberPendingRequestsFragment facultyMemberPendingRequestsFragment = new FacultyMemberPendingRequestsFragment();
                        facultyMemberPendingRequestsFragment.processSearch(query);
                        break;
                    case 2:
                        SuspendedFacultyMembersFragment suspendedFacultyMembersFragment = new SuspendedFacultyMembersFragment();
                        suspendedFacultyMembersFragment.processSearch(query);
                        break;
                    default:
                        searchView.closeSearch();
                        break;
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("SimpleSearchView", "Text changed:" + newText);
                return false;
            }

            @Override
            public boolean onQueryTextCleared() {
                Log.d("SimpleSearchView", "Text cleared");
                searchView.closeSearch();
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (searchView.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_faculty_members, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.search) {
            searchView.setMenuItem(item);
            return true;
        } else if (item.getItemId() == R.id.delete_all) {

        }
        return true;
    }
}