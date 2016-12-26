package com.holenzhou.pullrecyclerview.layoutmanager;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.holenzhou.pullrecyclerview.BaseRecyclerAdapter;
import com.holenzhou.pullrecyclerview.FooterSpanSizeLookup;


/**
 * Created by holenzhou on 16/5/11.
 */
public class XGridLayoutManager extends GridLayoutManager implements ILayoutManager {

    public XGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public XGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public XGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public int getFirstVisibleItemPosition() {
        return this.findFirstVisibleItemPosition();
    }

    @Override
    public int getLastVisibleItemPosition() {
        return findLastVisibleItemPosition();
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        return this;
    }

    @Override
    public void setRecyclerAdapter(BaseRecyclerAdapter adapter) {
        setSpanSizeLookup(new FooterSpanSizeLookup(adapter, getSpanCount()));
    }

    @Override
    public boolean isScrollToFooter(int itemCount) {
        int position = findLastVisibleItemPosition();
        return position >= itemCount - getSpanCount();
    }
}
