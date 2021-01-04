package com.netbucket.studymate.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.netbucket.studymate.R;
import com.netbucket.studymate.activities.ViewUserProfileActivity;
import com.netbucket.studymate.model.FacultyMember;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class SuspendedFacultyMemberAdapter extends FirestoreRecyclerAdapter<FacultyMember, SuspendedFacultyMemberAdapter.SuspendedFacultyMemberHolder> {
    private final FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    public SuspendedFacultyMemberAdapter(@NonNull FirestoreRecyclerOptions<FacultyMember> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull SuspendedFacultyMemberHolder holder, int position, @NonNull FacultyMember model) {
        holder.textViewName.setText(model.getFullName());
        holder.textViewCourse.setText(model.getCourse());
        holder.textViewFacultyId.setText(model.getId());

        if (!model.getProfileImageUri().equals("null")) {
            Glide.with(holder.imageViewProfileImage.getContext())
                    .load(model.getProfileImageUri())
                    .placeholder(R.drawable.avatar)
                    .into(holder.imageViewProfileImage);
        } else {
            Glide.with(holder.imageViewProfileImage.getContext())
                    .load(R.drawable.avatar)
                    .placeholder(R.drawable.avatar)
                    .into(holder.imageViewProfileImage);
        }


        holder.constraintLayoutItem.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(holder.constraintLayoutItem.getContext());
            View bottomSheetView = LayoutInflater.from(holder.constraintLayoutItem.getContext()).inflate(R.layout.bottom_sheet_suspended_member_options, holder.itemView.findViewById(R.id.linearLayout_bottom_sheet_container));

            bottomSheetView.findViewById(R.id.linearLayout_view_profile).setOnClickListener(v1 -> {
                Intent intent1 = new Intent(v1.getContext(), ViewUserProfileActivity.class);
                intent1.putExtra("fullName", model.getFullName());
                intent1.putExtra("email", model.getEmail());
                intent1.putExtra("phoneNumber", model.getPhoneNumber());
                intent1.putExtra("username", model.getUsername());
                intent1.putExtra("about", model.getAbout());
                intent1.putExtra("birthday", model.getBirthday());
                intent1.putExtra("gender", model.getGender());
                intent1.putExtra("institute", model.getInstitute());
                intent1.putExtra("role", model.getRole());
                intent1.putExtra("course", model.getCourse());
                intent1.putExtra("id", model.getId());
                intent1.putExtra("termOrYear", model.getSemOrYear());
                intent1.putExtra("profileImageUri", model.getProfileImageUri());
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                bottomSheetDialog.dismiss();

                v1.getContext().startActivity(intent1);
            });

            bottomSheetView.findViewById(R.id.linearLayout_revoke_suspension).setOnClickListener(v1 -> {
                Map<String, Object> userStatus = new HashMap<>();
                userStatus.put("userStatus", "allowed");

                bottomSheetDialog.dismiss();

                mStore.collection("users")
                        .document(getSnapshots().getSnapshot(holder.getAdapterPosition()).getId())
                        .update(userStatus)
                        .addOnSuccessListener(aVoid -> {

                            getSnapshots()
                                    .getSnapshot(holder.getAdapterPosition())
                                    .getReference()
                                    .update(userStatus);

                            Toasty.success(v.getContext(), "User approved!", Toast.LENGTH_SHORT, true).show();
                        })
                        .addOnFailureListener(e -> Toasty.error(v.getContext(), "Failed to approve user! Try again.", Toast.LENGTH_SHORT, true).show());
            });

            bottomSheetView.findViewById(R.id.linearLayout_delete_member).setOnClickListener(v1 -> {
                Map<String, Object> userStatus = new HashMap<>();
                userStatus.put("userStatus", "disallowed");

                bottomSheetDialog.dismiss();

                mStore.collection("users")
                        .document(getSnapshots().getSnapshot(holder.getAdapterPosition()).getId())
                        .update(userStatus)
                        .addOnSuccessListener(aVoid -> {

                            getSnapshots().
                                    getSnapshot(holder.getAdapterPosition())
                                    .getReference()
                                    .update(userStatus);

                            Toasty.success(v.getContext(), "User rejected!", Toast.LENGTH_SHORT, true).show();
                        })
                        .addOnFailureListener(e -> Toasty.error(v.getContext(), "Failed to reject user! Try again.", Toast.LENGTH_SHORT, true).show());
            });

            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();
        });
    }

    @NonNull
    @Override
    public SuspendedFacultyMemberHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_faculty_member, parent, false);
        return new SuspendedFacultyMemberHolder(view);
    }

    static class SuspendedFacultyMemberHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewFacultyId;
        TextView textViewCourse;
        CircleImageView imageViewProfileImage;
        ConstraintLayout constraintLayoutItem;

        public SuspendedFacultyMemberHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textView_full_name);
            textViewFacultyId = itemView.findViewById(R.id.textView_faculty_id);
            textViewCourse = itemView.findViewById(R.id.textView_course);
            imageViewProfileImage = itemView.findViewById(R.id.imageView_profile_image);
            constraintLayoutItem = itemView.findViewById(R.id.constraintLayout_item);
        }
    }
}
