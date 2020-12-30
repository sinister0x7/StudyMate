package com.netbucket.studymate.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.netbucket.studymate.R;
import com.netbucket.studymate.adapters.FacultyMemberAdapter;
import com.netbucket.studymate.adapters.FacultyMemberPendingRequestsAdapter;
import com.netbucket.studymate.model.FacultyMember;
import com.netbucket.studymate.utils.SessionManager;

import java.util.HashMap;

public class FacultyMemberPendingRequestsFragment extends Fragment {

    RecyclerView recyclerView;
    FacultyMemberPendingRequestsAdapter facultyMemberPendingRequestsAdapter;
    FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    String mInstitute;
    String mCourse;

    public FacultyMemberPendingRequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_faculty_member_pending_requests, container, false);
        recyclerView = view.findViewById(R.id.recyclerView_faculty_member_pending_requests);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setHasFixedSize(true);
        SessionManager sessionManager = new SessionManager(getContext(), SessionManager.SESSION_USER_SESSION);
        HashMap<String, String> userData = sessionManager.getUserDataFromSession();
        mInstitute = userData.get(SessionManager.KEY_INSTITUTE);
        mCourse = userData.get(SessionManager.KEY_COURSE);

        Query query = mStore
                .collection("/institutes/" + mInstitute + "/courses/" + mCourse + "/facultyMembers/")
                .whereEqualTo("userStatus", "disallowed")
                .limit(50);

        FirestoreRecyclerOptions<FacultyMember> options = new FirestoreRecyclerOptions.Builder<FacultyMember>()
                .setQuery(query, FacultyMember.class)
                .build();

        facultyMemberPendingRequestsAdapter = new FacultyMemberPendingRequestsAdapter(options);
        recyclerView.setAdapter(facultyMemberPendingRequestsAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        facultyMemberPendingRequestsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        facultyMemberPendingRequestsAdapter.stopListening();
    }

    public void processSearch(String searchString) {

        Query query = mStore
                .collection("/institutes/" + mInstitute + "/courses/" + mCourse + "/facultyMembers/")
                .whereEqualTo("userStatus", "disallowed")
                .orderBy("fullName")
                .startAt(searchString)
                .endAt(searchString+"\uf8ff")
                .limit(50);

        FirestoreRecyclerOptions<FacultyMember> options = new FirestoreRecyclerOptions.Builder<FacultyMember>()
                .setQuery(query,FacultyMember.class)
                .build();

        facultyMemberPendingRequestsAdapter = new FacultyMemberPendingRequestsAdapter(options);

        recyclerView.setAdapter(facultyMemberPendingRequestsAdapter);
    }
}