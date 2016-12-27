package com.holenzhou.pullrecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
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
    private boolean showLoadDoneTip;
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

    public static int resolveColor(Context context, @AttrRes int attr, int fallback) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});
        try {
            return a.getColor(0, fallback);
        } finally {
            a.recycle();
        }
    }

    public void setOnScrollListener(RecyclerView.OnScrollListener l) {
        mScrollListener = l;
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

    public void checkIfShowLoadDoneTip() {
        if (showLoadDoneTip && !adapter.isShowLoadDoneTip()) {
            adapter.showLoadDoneTip(true);
        }
    }

    private boolean checkIfScrollToFooter() {
        return layoutManager.isScrollToFooter(adapter.getItemCount());
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

    public void postRefreshing() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }
        });
    }

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

    public void onRefreshComplete(int action) {
        switch (action) {
            case RECYCLER_ACTION_PULL_REFRESH:
                swipeRefreshLayout.setRefreshing(false);
                break;
            case RECYCLER_ACTION_LOAD_MORE:
                adapter.showLoadMoreFooter(false);
                break;
        }
        mCurrentAction = RECYCLER_ACTION_IDLE;
    }

    public void stopRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        mCurrentAction = RECYCLER_ACTION_IDLE;
    }

    public void autoRefresh() {
        setSelection(0);
        postRefreshing();
    }

    public void stopLoadMore() {
        adapter.showLoadMoreFooter(false);
        mCurrentAction = RECYCLER_ACTION_IDLE;
    }

    public void enableLoadMore(boolean isLoadMoreEnable) {
        this.isLoadMoreEnable = isLoadMoreEnable;
        if (isShowLoadDoneTipEnable) {
            this.showLoadDoneTip = !isLoadMoreEnable;
        }
    }

    public void enablePullRefresh(boolean isPullRefreshEnabled) {
        this.isPullRefreshEnabled = isPullRefreshEnabled;
        swipeRefreshLayout.setEnabled(isPullRefreshEnabled);
    }

    public BaseRecyclerAdapter getAdapter() {
        return adapter;
    }

    public void addHeaderView(View header) {
        adapter.addHeaderView(header);
    }

    public void addHeaderView(int headerRes) {
        adapter.addHeaderView(LayoutInflater.from(getContext()).inflate(headerRes, this, false));
    }

    public void enableLoadDoneTip(boolean enable, int tip) {
        isShowLoadDoneTipEnable = enable;
        adapter.setLoadDoneTip(tip);
    }

    public void removeHeaderView() {
        adapter.removeHeaderView();
    }

    public void addFooterView(View footer) {
        isLoadMoreEnable = false;
        adapter.addFooterView(footer);
    }

    public void addFooterView(int footerRes) {
        isLoadMoreEnable = false;
        adapter.addFooterView(LayoutInflater.from(getContext()).inflate(footerRes, this, false));
    }

    public void removeFooterView() {
        adapter.removeFooterView();
    }

    public void setEmptyView(View emptyView) {
        setEmptyView(false, emptyView);
    }

    public void setEmptyView(int emptyViewRes) {
        setEmptyView(false, emptyViewRes);
    }

    public void setEmptyView(boolean isHeadAndEmpty, View emptyView) {
        adapter.setEmptyView(isHeadAndEmpty, emptyView);
    }

    public void setEmptyView(boolean isHeadAndEmpty, int emptyViewRes) {
        adapter.setEmptyView(isHeadAndEmpty, LayoutInflater.from(getContext()).inflate(emptyViewRes, this, false));
    }

    public void setSelection(int position) {
        recyclerView.scrollToPosition(position);
    }

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
