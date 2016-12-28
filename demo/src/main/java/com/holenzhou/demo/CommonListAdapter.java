package com.holenzhou.demo;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.holenzhou.pullrecyclerview.BaseRecyclerAdapter;
import com.holenzhou.pullrecyclerview.BaseViewHolder;

import java.util.List;

/**
 * Created by holenzhou on 2016/12/28.
 */

public class CommonListAdapter extends BaseRecyclerAdapter<CheesesItem> {

    public CommonListAdapter(Context context, int layoutResId, List<CheesesItem> data) {
        super(context, layoutResId, data);
    }

    @Override
    protected void convert(final BaseViewHolder holder, final CheesesItem item) {
        ImageView avatarView = holder.getView(R.id.avatar);
        Glide.with(mContext)
                .load(item.avatar)
                .fitCenter()
                .into(avatarView);
        holder.setText(android.R.id.text1, item.name);
        holder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(holder.getView(), item.name, Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}