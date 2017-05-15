package com.hwx.camera_doul.cameradp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.yokeyword.fragmentation.SupportActivity;

public class ImageLibActivity extends SupportActivity {
    @BindView(R.id.tab_image_lib)
    android.support.design.widget.TabLayout mTabLayout;
    @BindView(R.id.vp_image_lib)
    ViewPager mViewPager;

    String[] tabTitle = new String[]{"已录制的视频","照片"};
    List<Fragment> fragments = new ArrayList<Fragment>();
    ImageLibAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagelib);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        fragments.add(new ImageLib_video_Fragment());
        fragments.add(new ImageLib_pic_Fragment());
        mAdapter = new ImageLibAdapter(getSupportFragmentManager(),fragments);
        mViewPager.setAdapter(mAdapter);

        //TabLayout配合ViewPager有时会出现不显示Tab文字的Bug,需要按如下顺序
        mTabLayout.addTab(mTabLayout.newTab().setText(tabTitle[0]));
        mTabLayout.addTab(mTabLayout.newTab().setText(tabTitle[1]));
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setText(tabTitle[0]);
        mTabLayout.getTabAt(1).setText(tabTitle[1]);
    }

}
