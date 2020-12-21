package com.netbucket.studymate.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.netbucket.studymate.R;
import com.netbucket.studymate.adapters.SliderAdapter;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

public class OnBoardingActivity extends AppCompatActivity {

    ViewPager mSlider;
    WormDotsIndicator mWormDotsIndicator;
    Button mGetStartedButton;
    TextView mSkipButton;
    TextView mNextButton;
    SliderAdapter mSliderAdapter;
    Animation mGetStartedButtonShowAnimation;
    Animation mSkipButtonHideAnimation;
    Animation mNextButtonHideAnimation;
    int mCurrentSlidePos;

    ViewPager.OnPageChangeListener mChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mCurrentSlidePos = position;

            if (position == 0) {
                mGetStartedButton.setVisibility(View.INVISIBLE);
                mSkipButton.setVisibility(View.VISIBLE);
                mNextButton.setVisibility(View.VISIBLE);
            } else if (position == 1) {
                mGetStartedButton.setVisibility(View.INVISIBLE);
                mSkipButton.setVisibility(View.VISIBLE);
                mNextButton.setVisibility(View.VISIBLE);
            } else if (position == 2) {
                mGetStartedButton.setVisibility(View.INVISIBLE);
                mSkipButton.setVisibility(View.VISIBLE);
                mNextButton.setVisibility(View.VISIBLE);
            } else if (position == 3) {
                mGetStartedButton.setVisibility(View.INVISIBLE);
                mSkipButton.setVisibility(View.VISIBLE);
                mNextButton.setVisibility(View.VISIBLE);
            } else if (position == 4) {
                mGetStartedButton.setVisibility(View.INVISIBLE);
                mSkipButton.setVisibility(View.VISIBLE);
                mNextButton.setVisibility(View.VISIBLE);
            } else if (position == 5) {
                mSkipButton.setVisibility(View.VISIBLE);
                mNextButton.setVisibility(View.VISIBLE);
//                getStartedButtonHideAnimation = AnimationUtils.loadAnimation(OnBoarding.this, R.anim.anim_fade_out_right);
//                getStartedButton.setAnimation(getStartedButtonHideAnimation);
                mGetStartedButton.setVisibility(View.INVISIBLE);
            } else {
                mGetStartedButtonShowAnimation = AnimationUtils.loadAnimation(OnBoardingActivity.this, R.anim.anim_fade_in_left);
                mGetStartedButton.setAnimation(mGetStartedButtonShowAnimation);
                mGetStartedButton.setVisibility(View.VISIBLE);

                mSkipButtonHideAnimation = AnimationUtils.loadAnimation(OnBoardingActivity.this, R.anim.anim_fade_out_left);
                mSkipButton.setAnimation(mSkipButtonHideAnimation);
                mSkipButton.setVisibility(View.INVISIBLE);

                mNextButtonHideAnimation = AnimationUtils.loadAnimation(OnBoardingActivity.this, R.anim.anim_fade_out_right);
                mNextButton.setAnimation(mNextButtonHideAnimation);
                mNextButton.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_on_boarding);

        mSlider = findViewById(R.id.viewPager_on_boarding);
        mWormDotsIndicator = findViewById(R.id.wormDotsIndicator);
        mGetStartedButton = findViewById(R.id.button_get_started);
        mSkipButton = findViewById(R.id.textView_skip);
        mNextButton = findViewById(R.id.textView_next);

        mSliderAdapter = new SliderAdapter(this);
        mSlider.setAdapter(mSliderAdapter);
        mWormDotsIndicator.setViewPager(mSlider);
        mSlider.addOnPageChangeListener(mChangeListener);

        mGetStartedButton.setVisibility(View.INVISIBLE);
    }

    public void next(View view) {
        mSlider.setCurrentItem(mCurrentSlidePos + 1);
    }

    public void skip(View view) {
        mSlider.setCurrentItem(mCurrentSlidePos = 6);
    }

    public void getStarted(View view) {
        Intent intent = new Intent(OnBoardingActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}