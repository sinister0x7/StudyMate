package com.netbucket.studymate.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.netbucket.studymate.R;

public class SliderAdapter extends PagerAdapter {

    private static final int[] IMAGES = {
            R.drawable.img_welcome,
            R.drawable.img_managed_classes,
            R.drawable.img_progress_reports,
            R.drawable.img_organized_assignments,
            R.drawable.img_set_reminder,
            R.drawable.img_discussion_groups,
            R.drawable.img_secure
    };

    private static final int[] HEADINGS = {
            R.string.heading_onBoarding_first_slide,
            R.string.heading_onBoarding_second_slide,
            R.string.heading_onBoarding_third_slide,
            R.string.heading_onBoarding_fourth_slide,
            R.string.heading_onBoarding_fifth_slide,
            R.string.heading_onBoarding_sixth_slide,
            R.string.heading_onBoarding_seventh_slide
    };

    private static final int[] DESCRIPTIONS = {
            R.string.desc_onBoarding_first_slide,
            R.string.desc_onBoarding_second_slide,
            R.string.desc_onBoarding_third_slide,
            R.string.desc_onBoarding_fourth_slide,
            R.string.desc_onBoarding_fifth_slide,
            R.string.desc_onBoarding_sixth_slide,
            R.string.desc_onBoarding_seventh_slide
    };

    Context mContext;
    LayoutInflater mLayoutInflater;

    public SliderAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return HEADINGS.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mLayoutInflater.inflate(R.layout.slider_holder_item, container, false);

        ImageView illustrations = view.findViewById(R.id.imageView_slider);
        TextView titles = view.findViewById(R.id.textView_slider_heading);
        TextView descriptions = view.findViewById(R.id.textView_slider_desc);

        illustrations.setImageResource(IMAGES[position]);
        titles.setText(HEADINGS[position]);
        descriptions.setText(DESCRIPTIONS[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout) object);
    }
}