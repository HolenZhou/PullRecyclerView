package com.holenzhou.pullrecyclerview.layoutmanager;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import com.holenzhou.pullrecyclerview.BaseRecyclerAdapter;


/**
 * Created by TangHui on 2016/10/25
 */
public class ScrollSpeedLinearLayoutManger extends LinearLayoutManager implements ILayoutManager {
    private static final float MILLISECONDS_PER_INCH = 10f;

    public ScrollSpeedLinearLayoutManger(Context context) {
        super(context);
    }

    public ScrollSpeedLinearLayoutManger(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public ScrollSpeedLinearLayoutManger(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        LinearSmoothScroller linearSmoothScroller =
                new LinearSmoothScroller(recyclerView.getContext()) {
                    @Override
                    public PointF computeScrollVectorForPosition(int targetPosition) {
                        return ScrollSpeedLinearLayoutManger.this.computeScrollVectorForPosition(targetPosition);
                    }

                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
                    }
                };
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }

    @Override
    public int getFirstVisibleItemPosition() {
        return this.findFirstVisibleItemPosition();
    }

    @Override
    public int getLastVisibleItemPosition() {
        return this.findLastVisibleItemPosition();
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        return this;
    }

    @Override
    public void setRecyclerAdapter(BaseRecyclerAdapter adapter) {

    }

    @Override
    public boolean isScrollToFooter(int itemCount) {
        int position = findLastVisibleItemPosition();
        return position == itemCount - 1;
    }
}
