package com.holenzhou.demo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

public class EmptyListActivity extends BaseActivity {

    private PullRecyclerView mPullRecyclerView;
    private List<CheesesItem> mDataList = new ArrayList<>();
    private BaseRecyclerAdapter<CheesesItem> mAdapter;

    @Override
    public int getContentView() {
        return R.layout.activity_empty_list;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPullRecyclerView = (PullRecyclerView) findViewById(R.id.pull_recycler_view);

        // 初始化PullRecyclerView
        mPullRecyclerView.setLayoutManager(new XLinearLayoutManager(this));
        mAdapter = new CommonListAdapter(this, R.layout.list_item, mDataList);
        mPullRecyclerView.setAdapter(mAdapter);

        mPullRecyclerView.setColorSchemeResources(R.color.colorAccent); // 设置下拉刷新的旋转圆圈的颜色

        mPullRecyclerView.setEmptyView(R.layout.layout_empty_view);

        mPullRecyclerView.setOnRecyclerRefreshListener(new PullRecyclerView.OnRecyclerRefreshListener() {
            @Override
            public void onPullRefresh() {
                // 模拟下拉刷新网络请求
                mPullRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        mPullRecyclerView.stopRefresh();
                        mPullRecyclerView.enableLoadMore(false);
                    }
                }, 1500);
            }

            @Override
            public void onLoadMore() {
            }
        });
        mPullRecyclerView.autoRefresh();
    }

    @Override
    protected void initToolBar() {
        super.initToolBar();
        toolbar.setTitle("EmptyList");
        toolbar.inflateMenu(R.menu.menu_empty_list);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.show_empty) {
                    item.setChecked(true);
                    mPullRecyclerView.setEmptyView(R.layout.layout_empty_view);
                    mPullRecyclerView.removeHeaderView();
                } else if (item.getItemId() == R.id.show_header_empty) {
                    item.setChecked(true);
                    mPullRecyclerView.addHeaderView(R.layout.layout_list_header);
                    mPullRecyclerView.setEmptyView(true, R.layout.layout_empty_view);
                    mPullRecyclerView.setAdapter(mAdapter);
                } else if (item.getItemId() == R.id.show_header) {
                    item.setChecked(true);
                    mPullRecyclerView.setEmptyView(R.layout.layout_empty_view);
                    mPullRecyclerView.addHeaderView(R.layout.layout_list_header);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
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
