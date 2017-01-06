package com.holenzhou.pullrecyclerview;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.holenzhou.pullrecyclerview.layoutmanager.ILayoutManager;
import com.holenzhou.pullrecyclerview.layoutmanager.XLinearLayoutManager;

/**
 * Created by holenzhou on 16/5/10.
 * 基于RecyclerView和SwipeRefreshLayout进行封装，提供下拉刷新、上拉加载更多、添加header和footer、
 * 设置空页面等多种功能的组合控件
 */
public class PullRecyclerView extends FrameLayout implements SwipeRefreshLayout.OnRefreshListener {

    protected RecyclerView recyclerView;
    protected SwipeRefreshLayout swipeRefreshLayout;
    private OnRecyclerRefreshListener listener;
    public static final int RECYCLER_ACTION_PULL_REFRESH = 1;
    public static final int RECYCLER_ACTION_LOAD_MORE = 2;
    public static final int RECYCLER_ACTION_IDLE = 0;
    int mCurrentAction = RECYCLER_ACTION_IDLE;
    private boolean isLoadMoreEnable;
    private boolean isPullRefreshEnabled = true;
    private boolean isShowLoadDoneTipEnable;
    private ILayoutManager layoutManager;
    private BaseRecyclerAdapter adapter;
    private RecyclerView.OnScrollListener mScrollListener;

    public PullRecyclerView(Context context) {
        super(context);
        init();
    }

    public PullRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PullRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_pull_recycler, this, true);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (mScrollListener != null) {
                    mScrollListener.onScrollStateChanged(recyclerView, newState);
                }

                if (checkIfScrollToFooter() && newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    checkIfCanLoadMore();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (mScrollListener != null) {
                    mScrollListener.onScrolled(recyclerView, dx, dy);
                }

                if (checkIfScrollToFooter() && dy > 0) {
                    checkIfCanLoadMore();
                }
            }
        });
    }

    private void checkIfCanLoadMore() {
        if (isLoadMoreEnable && !adapter.isShowLoadMoreFooter()) {
            post(new Runnable() {
                @Override
                public void run() {
                    adapter.showLoadMoreFooter(true);
                }
            });
        }
    }

    private boolean checkIfScrollToFooter() {
        return layoutManager.isScrollToFooter(adapter.getItemCount());
    }

    public void setOnScrollListener(RecyclerView.OnScrollListener l) {
        mScrollListener = l;
    }

    public int getLastVisibleItemPosition() {
        return layoutManager.getLastVisibleItemPosition();
    }

    public int getFirstVisibleItemPosition() {
        return layoutManager.getFirstVisibleItemPosition();
    }

    public RecyclerView getRecycler() {
        return recyclerView;
    }

    /**
     * 触发PullRecyclerView的下拉刷新
     */
    public void postRefreshing() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }
        });
    }

    /**
     * 设置adapter，需要使用BaseRecyclerAdapter的子类
     * @param adapter
     */
    public void setAdapter(BaseRecyclerAdapter adapter) {
        this.adapter = adapter;
        recyclerView.setAdapter(adapter);
        if (layoutManager == null) {
            layoutManager = new XLinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager.getLayoutManager());
        }
        layoutManager.setRecyclerAdapter(adapter);
        adapter.setLoadMoreListener(new BaseRecyclerAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (listener != null) {
                    if (mCurrentAction == RECYCLER_ACTION_IDLE) {
                        mCurrentAction = RECYCLER_ACTION_LOAD_MORE;
                        recyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onLoadMore();
                            }
                        });
                    }
                }
            }
        });
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        recyclerView.addItemDecoration(itemDecoration);
    }

    public void setLayoutManager(ILayoutManager layoutManager) {
        this.layoutManager = layoutManager;
        recyclerView.setLayoutManager(layoutManager.getLayoutManager());
    }

    /**
     * 设置下拉刷新和上拉加载事件的监听器
     * @param listener
     */
    public void setOnRecyclerRefreshListener(OnRecyclerRefreshListener listener) {
        this.listener = listener;
    }

    @Override
    public void onRefresh() {
        mCurrentAction = RECYCLER_ACTION_PULL_REFRESH;
        listener.onPullRefresh();
    }

    /**
     * set the pull refresh circle color
     * @param colorResIds
     */
    public void setColorSchemeResources(@ColorRes int... colorResIds) {
        swipeRefreshLayout.setColorSchemeResources(colorResIds);
    }

    /**
     * UI更新，结束下拉刷新
     */
    public void stopRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        mCurrentAction = RECYCLER_ACTION_IDLE;
    }

    /**
     * UI更新，结束上拉加载更多
     */
    public void stopLoadMore() {
        adapter.showLoadMoreFooter(false);
        mCurrentAction = RECYCLER_ACTION_IDLE;
    }

    /**
     * 触发下拉刷新并使列表回到顶部
     */
    public void autoRefresh() {
        setSelection(0);
        postRefreshing();
    }

    /**
     * 设置是否可以上拉加载更多
     * @param isLoadMoreEnable
     */
    public void enableLoadMore(boolean isLoadMoreEnable) {
        this.isLoadMoreEnable = isLoadMoreEnable;
        if (!isLoadMoreEnable && isShowLoadDoneTipEnable) {
            checkIfShowLoadDoneTip();
        }
    }

    private void checkIfShowLoadDoneTip() {
        if (adapter.getData() == null || adapter.getData().size() == 0) {
            return;
        }
        if (adapter.isShowLoadDoneTip()) {
            return;
        }
        adapter.showLoadDoneTip(true);
    }

    /**
     * 设置是否可以下拉刷新
     * @param isPullRefreshEnabled
     */
    public void enablePullRefresh(boolean isPullRefreshEnabled) {
        this.isPullRefreshEnabled = isPullRefreshEnabled;
        swipeRefreshLayout.setEnabled(isPullRefreshEnabled);
    }

    public BaseRecyclerAdapter getAdapter() {
        return adapter;
    }

    /**
     * 当数据全部加载完成时，是否在列表底部展示提示语
     * @param enable
     * @param tip 提示语，默认提示“已全部加载”
     */
    public void enableLoadDoneTip(boolean enable, int tip) {
        isShowLoadDoneTipEnable = enable;
        adapter.setLoadDoneTip(tip);
    }

    /**
     * 添加header
     * @param header header对应的View
     */
    public void addHeaderView(View header) {
        adapter.addHeaderView(header);
    }

    /**
     * 添加header
     * @param headerRes header对应的布局资源文件
     */
    public void addHeaderView(int headerRes) {
        adapter.addHeaderView(LayoutInflater.from(getContext()).inflate(headerRes, this, false));
    }

    /**
     * 移除header
     */
    public void removeHeaderView() {
        adapter.removeHeaderView();
    }

    /**
     * 添加footer
     * @param footer footer对应的View
     */
    public void addFooterView(View footer) {
        isLoadMoreEnable = false;
        adapter.addFooterView(footer);
    }

    /**
     * 添加footer
     * @param footerRes footer对应的布局资源文件
     */
    public void addFooterView(int footerRes) {
        isLoadMoreEnable = false;
        adapter.addFooterView(LayoutInflater.from(getContext()).inflate(footerRes, this, false));
    }

    /**
     * 移除footer
     */
    public void removeFooterView() {
        adapter.removeFooterView();
    }

    /**
     * 设置列表无数据时需要展示的空页面
     * @param emptyView 空页面对应的View
     */
    public void setEmptyView(View emptyView) {
        setEmptyView(false, emptyView);
    }

    /**
     * 设置列表无数据时需要展示的空页面
     * @param emptyViewRes 空页面对应的布局资源文件
     */
    public void setEmptyView(int emptyViewRes) {
        setEmptyView(false, emptyViewRes);
    }

    /**
     * 设置列表无数据时需要展示的空页面
     * @param isHeadAndEmpty 当有header时，是否展示空页面，true则展示，false则不展示
     * @param emptyView 空页面对应的View
     */
    public void setEmptyView(boolean isHeadAndEmpty, View emptyView) {
        adapter.setEmptyView(isHeadAndEmpty, emptyView);
    }

    /**
     * 设置列表无数据时需要展示的空页面
     * @param isHeadAndEmpty 当有header时，是否展示空页面，true则展示，false则不展示
     * @param emptyViewRes 空页面对应的布局资源文件
     */
    public void setEmptyView(boolean isHeadAndEmpty, int emptyViewRes) {
        adapter.setEmptyView(isHeadAndEmpty, LayoutInflater.from(getContext()).inflate(emptyViewRes, this, false));
    }

    public void setSelection(int position) {
        recyclerView.scrollToPosition(position);
    }

    /**
     * 直接刷新，不展示下拉圆圈
     */
    public void refreshNoPull() {
        listener.onPullRefresh();
    }

    public void smoothScrollToPosition(final int position) {
        if (layoutManager.getLayoutManager().getChildAt(0).getTop() < 0) {
            post(new Runnable() {
                @Override
                public void run() {
                    recyclerView.smoothScrollToPosition(position);
                }
            });
        }
    }

    public interface OnRecyclerRefreshListener {
        void onPullRefresh();

        void onLoadMore();
    }

}
