package com.netbucket.studymate.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.netbucket.studymate.R;

public class BottomSheetImagePicker extends BottomSheetDialogFragment {
    private BottomSheetListener mBottomSheetListener;
    LinearLayout mCameraButton;
    LinearLayout mGalleryButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_member_options, container, false);

        mCameraButton = view.findViewById(R.id.linearLayout_button_pick_from_gallery);
        mGalleryButton = view.findViewById(R.id.linearLayout_remove_photo);

        mCameraButton.setOnClickListener(v -> dismiss());

        mGalleryButton.setOnClickListener(v -> dismiss());
        return view;
    }

    public interface BottomSheetListener {
        void onButtonClicked(String text);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mBottomSheetListener =(BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+" must implement BottomSheetListener");
        }
    }
}
