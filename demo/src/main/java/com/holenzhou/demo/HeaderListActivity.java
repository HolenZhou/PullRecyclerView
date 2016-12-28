package com.holenzhou.demo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.holenzhou.pullrecyclerview.BaseRecyclerAdapter;
import com.holenzhou.pullrecyclerview.BaseViewHolder;
import com.holenzhou.pullrecyclerview.PullRecyclerView;
import com.holenzhou.pullrecyclerview.layoutmanager.XLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by holenzhou on 2016/12/26.
 */

public class HeaderListActivity extends AppCompatActivity {

    private PullRecyclerView mPullRecyclerView;
    private List<CheesesItem> mDataList = new ArrayList<>();
    private BaseRecyclerAdapter<CheesesItem> mAdapter;
    private int pageSize = 3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widget_pull_recycler_view);
        mPullRecyclerView = (PullRecyclerView) findViewById(R.id.pull_recycler_view);

        // 初始化PullRecyclerView
        mPullRecyclerView.setLayoutManager(new XLinearLayoutManager(this));
        mAdapter = new CommonListAdapter(this, R.layout.list_item, mDataList);
        mPullRecyclerView.setAdapter(mAdapter);

        mPullRecyclerView.setColorSchemeResources(R.color.colorAccent); // 设置下拉刷新的旋转圆圈的颜色
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.shape_simple_item_decoration));
        mPullRecyclerView.addItemDecoration(itemDecoration);

        mPullRecyclerView.addHeaderView(R.layout.layout_list_header);
        mPullRecyclerView.setEmptyView(true, R.layout.layout_empty_view);

        mPullRecyclerView.setOnRecyclerRefreshListener(new PullRecyclerView.OnRecyclerRefreshListener() {
            @Override
            public void onPullRefresh() {
                // 模拟下拉刷新网络请求
                mPullRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDataList.clear();
//                        mDataList.addAll(Cheeses.getRandomSubList(30));
                        mAdapter.notifyDataSetChanged();
                        // 或者直接使用BaseRecyclerAdapter中封装的方法
                        //mAdapter.replaceAll(mDataList);
                        mPullRecyclerView.stopRefresh();
                        mPullRecyclerView.enableLoadMore(false);
//                        mPullRecyclerView.enableLoadMore(pageSize > 0);
                    }
                }, 1500);
            }

            @Override
            public void onLoadMore() {
//                pageSize--;
//                // 模拟上拉加载更多网络请求
//                mPullRecyclerView.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        mDataList.addAll(Cheeses.getRandomSubList(30));
//                        mAdapter.notifyDataSetChanged();
//                        // 或者直接使用BaseRecyclerAdapter中封装的方法
//                        //mAdapter.addAll(mDataList);
//
//                        mPullRecyclerView.stopLoadMore();
//                        mPullRecyclerView.enableLoadMore(pageSize > 0);
//                        mPullRecyclerView.checkIfShowLoadDoneTip();
//                    }
//                }, 1500);
            }
        });
        mPullRecyclerView.postRefreshing();
    }

    class CommonListAdapter extends BaseRecyclerAdapter<CheesesItem> {

        public CommonListAdapter(Context context, int layoutResId, List<CheesesItem> data) {
            super(context, layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder holder, final CheesesItem item) {
            ImageView avatarView = holder.getView(R.id.avatar);
            Glide.with(mContext)
                    .load(item.avatar)
                    .fitCenter()
                    .into(avatarView);
            holder.setText(android.R.id.text1, item.name);
            holder.getView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(mPullRecyclerView, item.name, Snackbar.LENGTH_SHORT).show();
                }
            });
        }
    }
}
