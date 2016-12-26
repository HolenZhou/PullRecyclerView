package com.holenzhou.pullrecyclerview;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by holenzhou on 16/11/21.
 */

public class SimpleItemDecoration extends DividerItemDecoration {

    public SimpleItemDecoration(Context context, int orientation) {
        super(context, orientation);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getAdapter().getItemCount();
        int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        if (itemPosition == childCount - 1) {
            // 最后一个item不需要添加分割线
            outRect.set(0, 0, 0, 0);
        } else {
            super.getItemOffsets(outRect, view, parent, state);
        }
    }
}
