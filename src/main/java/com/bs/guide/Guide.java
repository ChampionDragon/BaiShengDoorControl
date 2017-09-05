package com.bs.guide;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bs.R;
import com.bs.account.Login;
import com.bs.adapter.ImageViewAdapter;
import com.bs.base.BaseActivity;
import com.bs.base.BaseApplication;
import com.bs.constant.SpKey;
import com.bs.util.SmallUtil;

import java.util.ArrayList;
import java.util.List;

public class Guide extends BaseActivity {
    private ViewPager vp;
    private Button btn;
    private LinearLayout ll;
    private int[] pics = {R.drawable.ad1, R.drawable.ad2, R.drawable.ad3, R.drawable.ad4};
    private ImageView[] dots;
    private int dotIndex;// 设置当前点的索引
    private List<ImageView> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initView();
    }

    private void initView() {
        vp = (ViewPager) findViewById(R.id.guide_vp);
        btn = (Button) findViewById(R.id.guide_btn);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SmallUtil.getActivity(Guide.this, Login.class);
                BaseApplication.sp.putBoolean(SpKey.isFirst, false);
                finish();
            }
        });
        ll = (LinearLayout) findViewById(R.id.guide_ll);
        initdot();
        initVp();

    }

    private void initVp() {
        vp.addOnPageChangeListener(onPageChangeListener);
        list = new ArrayList<ImageView>();
        for (int i = 0; i < pics.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(pics[i]);
            list.add(imageView);
        }
        vp.setAdapter(new ImageViewAdapter(list));
    }

    /**
     * 初始化viewpager下面的点
     */
    private void initdot() {
        ll = (LinearLayout) findViewById(R.id.guide_ll);
        dots = new ImageView[pics.length];

        LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        // 设置每个小圆点距离左边的间距
        margin.setMargins(15, 0, 15, 0);
        for (int i = 0; i < pics.length; i++) {
            ImageView imageView = new ImageView(this);
            dots[i] = imageView;
            if (i == 0) {
                // 默认选中第一张图片
                dots[i]
                        .setBackgroundResource(R.drawable.dot_press);
            } else {
                // 其他图片都设置未选中状态
                dots[i]
                        .setBackgroundResource(R.drawable.dot);
            }
            ll.addView(dots[i], margin);

        }
    }

    /**
     * 设置当前点的颜色
     */
    private void setCurrentDot(int currentIndex) {
        dots[currentIndex].setImageResource(R.drawable.dot_press);
        dots[dotIndex].setImageResource(R.drawable.dot);
        dotIndex = currentIndex;
    }

    OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int arg0) {
            setCurrentDot(arg0);
            if (arg0 != pics.length - 1) {
                btn.setVisibility(View.GONE);
            } else {
                btn.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };
}
