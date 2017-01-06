package com.holenzhou.pullrecyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.holenzhou.pullrecyclerview.layoutmanager.ILayoutManager;
import com.holenzhou.pullrecyclerview.layoutmanager.XLinearLayoutManager;


/**
 * Created by holenzhou on 16/5/26.
 * 只支持上拉加载更多的LoadMoreRecyclerView
 * 直接继承自RecyclerView，适用于需要将SwipeRefreshLayout和RecyclerView分开使用的场景
 */
public class LoadMoreRecyclerView extends RecyclerView {

    private OnScrollListener scrollListener;
    private ILayoutManager layoutManager;
    private BaseRecyclerAdapter adapter;
    private boolean isLoadMoreEnable;
    private boolean isShowLoadDoneTipEnable;
    OnLoadMoreListener onLoadMoreListener;
    public static final int RECYCLER_ACTION_LOAD_MORE = 2;
    public static final int RECYCLER_ACTION_IDLE = 0;
    int mCurrentAction = RECYCLER_ACTION_IDLE;

    public LoadMoreRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (scrollListener != null) {
                    scrollListener.onScrollStateChanged(recyclerView, newState);
                }

                if (checkIfScrollToFooter() && newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    checkIfCanLoadMore();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (scrollListener != null) {
                    scrollListener.onScrolled(recyclerView, dx, dy);
                }

                if (checkIfScrollToFooter() && dy > 0) {
                    checkIfCanLoadMore();
                }
            }
        });
    }

    public void setScrollListener(OnScrollListener listener) {
        scrollListener = listener;
    }

    private boolean checkIfScrollToFooter() {
        return layoutManager.isScrollToFooter(adapter.getItemCount());
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

    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter instanceof BaseRecyclerAdapter) {
            this.adapter = (BaseRecyclerAdapter) adapter;
            if (layoutManager == null) {
                layoutManager = new XLinearLayoutManager(getContext());
                setLayoutManager(layoutManager.getLayoutManager());
            }
            layoutManager.setRecyclerAdapter(this.adapter);
            this.adapter.setLoadMoreListener(new BaseRecyclerAdapter.OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    if (onLoadMoreListener != null) {
                        if (mCurrentAction == RECYCLER_ACTION_IDLE) {
                            mCurrentAction = RECYCLER_ACTION_LOAD_MORE;
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    onLoadMoreListener.onLoadMore();
                                }
                            });
                        }
                    }
                }
            });
        } else {
            throw new RuntimeException("Please use BaseRecyclerAdapter!");
        }
    }

    public void setXLayoutManager(ILayoutManager layoutManager) {
        this.layoutManager = layoutManager;
        setLayoutManager(layoutManager.getLayoutManager());
    }

    public void stopLoadMore() {
        adapter.showLoadMoreFooter(false);
        mCurrentAction = RECYCLER_ACTION_IDLE;
    }

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

    public void enableLoadDoneTip(boolean enable, int tip) {
        isShowLoadDoneTipEnable = enable;
        adapter.setLoadDoneTip(tip);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.onLoadMoreListener = listener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
