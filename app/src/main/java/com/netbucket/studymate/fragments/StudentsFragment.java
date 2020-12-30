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
import com.netbucket.studymate.adapters.StudentAdapter;
import com.netbucket.studymate.model.Student;
import com.netbucket.studymate.utils.SessionManager;

import java.util.HashMap;

public class StudentsFragment extends Fragment {

    RecyclerView recyclerView;
    StudentAdapter studentAdapter;
    FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    String mInstitute;
    String mCourse;

    public StudentsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_students, container, false);
        recyclerView = view.findViewById(R.id.recyclerView_students);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setHasFixedSize(true);
        SessionManager sessionManager = new SessionManager(getContext(), SessionManager.SESSION_USER_SESSION);
        HashMap<String, String> userData = sessionManager.getUserDataFromSession();
        mInstitute = userData.get(SessionManager.KEY_INSTITUTE);
        mCourse = userData.get(SessionManager.KEY_COURSE);

        Query query = mStore
                .collection("/institutes/" + mInstitute + "/courses/" + mCourse + "/students/")
                .whereEqualTo("userStatus", "allowed")
                .limit(50);

        FirestoreRecyclerOptions<Student> options = new FirestoreRecyclerOptions.Builder<Student>()
                .setQuery(query, Student.class)
                .build();

        studentAdapter = new StudentAdapter(options);
        recyclerView.setAdapter(studentAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        studentAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        studentAdapter.stopListening();
    }
}