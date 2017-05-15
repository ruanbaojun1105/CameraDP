package com.hwx.camera_doul.cameradp;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class QuickAdapter extends BaseQuickAdapter<Item, BaseViewHolder> {
    public QuickAdapter(List<Item> items) {
        super(R.layout.fragment_imagelib_item, items);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, Item item) {
        viewHolder.setText(R.id.textView, item.getName())
                .linkify(R.id.textView);
        viewHolder.setVisible(R.id.image_play,item.getPath().endsWith(".mp4"));
        viewHolder.getView(R.id.image_play).bringToFront();
        Glide.with(mContext).load(item.getPath()).centerCrop().crossFade().into((ImageView) viewHolder.getView(R.id.imageView));
    }
}