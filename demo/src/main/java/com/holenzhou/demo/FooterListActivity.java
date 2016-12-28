package com.holenzhou.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.holenzhou.pullrecyclerview.BaseRecyclerAdapter;
import com.holenzhou.pullrecyclerview.PullRecyclerView;
import com.holenzhou.pullrecyclerview.layoutmanager.XLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by holenzhou on 2016/12/28.
 */

public class FooterListActivity extends BaseActivity {

    private PullRecyclerView mPullRecyclerView;
    private List<CheesesItem> mDataList = new ArrayList<>();
    private BaseRecyclerAdapter<CheesesItem> mAdapter;
    @Override
    public int getContentView() {
        return R.layout.activity_footer_list;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDataList.addAll(Cheeses.getRandomSubList(10));

        mPullRecyclerView = (PullRecyclerView) findViewById(R.id.pull_recycler_view);
        mPullRecyclerView.setLayoutManager(new XLinearLayoutManager(this));
        mAdapter = new CommonListAdapter(this, R.layout.list_item, mDataList);
        mPullRecyclerView.setAdapter(mAdapter);
        mPullRecyclerView.enableLoadMore(false);
        mPullRecyclerView.addHeaderView(R.layout.layout_list_header);
        mPullRecyclerView.addFooterView(R.layout.layout_list_footer);
        mPullRecyclerView.setOnRecyclerRefreshListener(new PullRecyclerView.OnRecyclerRefreshListener() {
            @Override
            public void onPullRefresh() {
                mPullRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDataList.clear();
                        mDataList.addAll(Cheeses.getRandomSubList(10));
                        mAdapter.notifyDataSetChanged();
                        mPullRecyclerView.stopRefresh();
                    }
                }, 1000);
            }

            @Override
            public void onLoadMore() {

            }
        });
    }

    @Override
    protected void initToolBar() {
        super.initToolBar();
        toolbar.setTitle("FooterList");
    }
}
