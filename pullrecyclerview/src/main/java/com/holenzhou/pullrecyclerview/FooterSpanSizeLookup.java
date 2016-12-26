package com.holenzhou.pullrecyclerview;

import android.support.v7.widget.GridLayoutManager;

/**
 * Created by holenzhou on 16/5/11.
 */
public class FooterSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {
    BaseRecyclerAdapter adapter;
    int spanCount;
    public FooterSpanSizeLookup(BaseRecyclerAdapter adapter, int spanCount) {
        this.adapter = adapter;
        this.spanCount = spanCount;
    }

    @Override
    public int getSpanSize(int position) {
        if (adapter.isLoadMoreFooter(position) || adapter.isSectionHeader(position) || adapter.isRecyclerHeaderView(position) || adapter.isLoadDoneTip(position)) {
            return spanCount;
        }
        return 1;
    }
}
