package com.hwx.camera_doul.cameradp;


import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.serenegiant.encoder.MediaMuxerWrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 */
public class ImageLib_pic_Fragment extends BaseFragment {

    @BindView(R.id.rv_daily_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.fab_calender)
    FloatingActionButton fabCalender;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;

    QuickAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_imagelib;
    }

    @Override
    protected void initEventAndData() {
        initAdapter();
    }

    private void initAdapter() {
        adapter = new QuickAdapter(initData(Environment.DIRECTORY_DCIM));
        adapter.openLoadAnimation();
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),4));
        mRecyclerView.setAdapter(adapter);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.setNewData(initData(Environment.DIRECTORY_DCIM));
                swipeRefresh.setRefreshing(false);
            }
        });
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                Item item= (Item) adapter.getData().get(position);
                File file = new File(item.getPath());
                if( file != null && file.isFile() == true){
                    Intent intent = new Intent();
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file), "image/*");
                    getContext().startActivity(intent);   }
            }
        });
    }

    public static List<Item> initData(String type) {
        List<Item> items=new ArrayList<>();
        final File dir = new File(Environment.getExternalStoragePublicDirectory(type), MediaMuxerWrapper.DIR_NAME);
        Log.d("tag", "path=" + dir.toString());
        if (dir.list()==null)
            return items;

        for (File file:dir.listFiles()){
            if (!file.isDirectory()){
                if (file.getPath().endsWith("_1.mp4")){
                    continue;
                }
                items.add(new Item(file.getPath(),file.getName()));
            }
        }
        return items;
    }
}