package com.holenzhou.demo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.holenzhou.pullrecyclerview.BaseRecyclerAdapter;
import com.holenzhou.pullrecyclerview.BaseViewHolder;
import com.holenzhou.pullrecyclerview.PullRecyclerView;
import com.holenzhou.pullrecyclerview.layoutmanager.XGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by holenzhou on 2016/12/28.
 */

public class GridListActivity extends BaseActivity {

    private PullRecyclerView mPullRecyclerView;
    private List<CheesesItem> mDataList = new ArrayList<>();
    private BaseRecyclerAdapter<CheesesItem> mAdapter;
    private int pageSize = 2;

    @Override
    public int getContentView() {
        return R.layout.activity_grid_list;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPullRecyclerView = (PullRecyclerView) findViewById(R.id.pull_recycler_view);
        // 初始化PullRecyclerView
        mPullRecyclerView.setLayoutManager(new XGridLayoutManager(this, 3));
        mAdapter = new GridListAdapter(this, R.layout.grid_item, mDataList);
        mPullRecyclerView.setAdapter(mAdapter);

        mPullRecyclerView.setColorSchemeResources(R.color.colorAccent); // 设置下拉刷新的旋转圆圈的颜色
        mPullRecyclerView.enablePullRefresh(true); // 开启下拉刷新，默认即为true，可不用设置
        mPullRecyclerView.enableLoadDoneTip(true, R.string.load_done_tip); // 开启数据全部加载完成时的底部提示，默认为false
        mPullRecyclerView.setOnRecyclerRefreshListener(new PullRecyclerView.OnRecyclerRefreshListener() {
            @Override
            public void onPullRefresh() {
                // 模拟下拉刷新网络请求
                mPullRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDataList.clear();
                        mDataList.addAll(Cheeses.getRandomSubList(30));
                        mAdapter.notifyDataSetChanged();
                        mPullRecyclerView.stopRefresh();
                        mPullRecyclerView.enableLoadMore(pageSize > 0); // 当剩余还有大于0页的数据时，开启上拉加载更多
                    }
                }, 1500);
            }

            @Override
            public void onLoadMore() {
                pageSize--;
                // 模拟上拉加载更多网络请求
                mPullRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDataList.addAll(Cheeses.getRandomSubList(30));
                        mAdapter.notifyDataSetChanged();
                        mPullRecyclerView.stopLoadMore();
                        mPullRecyclerView.enableLoadMore(pageSize > 0);
                    }
                }, 1500);
            }
        });
        mPullRecyclerView.postRefreshing();
    }

    @Override
    protected void initToolBar() {
        super.initToolBar();
        toolbar.setTitle("GridList");
    }

    class GridListAdapter extends BaseRecyclerAdapter<CheesesItem> {

        public GridListAdapter(Context context, int layoutResId, List<CheesesItem> data) {
            super(context, layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder holder, final CheesesItem item) {
            ImageView avatarView = holder.getView(R.id.avatar);
            Glide.with(mContext)
                    .load(item.avatar)
                    .into(avatarView);

            holder.getView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(mPullRecyclerView, item.name, Snackbar.LENGTH_SHORT).show();
                }
            });
        }
    }
}
