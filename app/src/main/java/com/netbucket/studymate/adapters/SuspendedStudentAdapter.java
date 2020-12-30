package com.netbucket.studymate.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.netbucket.studymate.R;
import com.netbucket.studymate.model.Student;

import de.hdodenhof.circleimageview.CircleImageView;

public class SuspendedStudentAdapter extends FirestoreRecyclerAdapter<Student, SuspendedStudentAdapter.SuspendedStudentHolder> {
    public SuspendedStudentAdapter(@NonNull FirestoreRecyclerOptions<Student> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull SuspendedStudentHolder holder, int position, @NonNull Student model) {
        holder.textViewName.setText(model.getFullName());
        holder.textViewCourse.setText(model.getCourse());
        holder.textViewId.setText(model.getId());
        holder.textViewSemOrYear.setText(model.getSemOrYear());
        Glide.with(holder.imageViewProfileImage.getContext())
                .load(model.getProfileImageUri())
                .into(holder.imageViewProfileImage);
        holder.constraintLayoutItem.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(holder.constraintLayoutItem.getContext());
            View bottomSheetView = LayoutInflater.from(holder.constraintLayoutItem.getContext()).inflate(R.layout.bottom_sheet_suspended_member_options, holder.itemView.findViewById(R.id.linearLayout_bottom_sheet_container));

            bottomSheetView.findViewById(R.id.linearLayout_view_profile).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            bottomSheetView.findViewById(R.id.linearLayout_revoke_suspension).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            bottomSheetView.findViewById(R.id.linearLayout_delete_member).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();
        });
    }

    @NonNull
    @Override
    public SuspendedStudentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new SuspendedStudentHolder(view);
    }

    static class SuspendedStudentHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewId;
        TextView textViewCourse;
        TextView textViewSemOrYear;
        CircleImageView imageViewProfileImage;
        ConstraintLayout constraintLayoutItem;

        public SuspendedStudentHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textView_full_name);
            textViewId = itemView.findViewById(R.id.textView_id_or_roll_no);
            textViewCourse = itemView.findViewById(R.id.textView_course);
            textViewSemOrYear = itemView.findViewById(R.id.textView_sem_or_year);
            imageViewProfileImage = itemView.findViewById(R.id.imageView_profile_image);
            constraintLayoutItem = itemView.findViewById(R.id.constraintLayout_item);
        }
    }
}
