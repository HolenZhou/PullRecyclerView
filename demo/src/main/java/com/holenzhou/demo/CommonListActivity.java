package com.holenzhou.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.holenzhou.pullrecyclerview.BaseRecyclerAdapter;
import com.holenzhou.pullrecyclerview.PullRecyclerView;
import com.holenzhou.pullrecyclerview.layoutmanager.XLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by holenzhou on 2016/12/26.
 */

public class CommonListActivity extends BaseActivity {

    private PullRecyclerView mPullRecyclerView;
    private List<CheesesItem> mDataList = new ArrayList<>();
    private BaseRecyclerAdapter<CheesesItem> mAdapter;
    private int pageSize = 2;

    @Override
    public int getContentView() {
        return R.layout.activity_common_list;
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
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.shape_simple_item_decoration));
        mPullRecyclerView.addItemDecoration(itemDecoration);

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
                        // 或者直接使用BaseRecyclerAdapter中封装的方法
                        //mAdapter.replaceAll(mDataList);
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
                        // 或者直接使用BaseRecyclerAdapter中封装的方法
                        //mAdapter.addAll(mDataList);

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
        toolbar.setTitle("CommonList");
        toolbar.inflateMenu(R.menu.menu_common_list);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.add_header) {
                    if (item.getTitle().equals(getString(R.string.add_header))) {
                        mPullRecyclerView.addHeaderView(R.layout.layout_list_header);
                        mAdapter.notifyDataSetChanged();
                        item.setTitle(R.string.remove_header);
                    } else {
                        mPullRecyclerView.removeHeaderView();
                        mAdapter.notifyDataSetChanged();
                        item.setTitle(R.string.add_header);
                    }
                }
                return true;
            }
        });
    }
}
