package com.holenzhou.pullrecyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by holenzhou on 16/5/11.
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "BaseRecyclerAdapter";

    private static final int VIEW_TYPE_LOAD_MORE = 100;
    private static final int VIEW_TYPE_LOAD_DONE = 104;
    private static final int VIEW_TYPE_HEADER = 101;
    private static final int VIEW_TYPE_FOOTER = 102;
    private static final int VIEW_TYPE_EMPTY_VIEW = 103;
    private static final int VIEW_TYPE_ITEM = 0;
    private boolean isShowLoadMoreFooter = false;
    private boolean isShowLoadDoneTip = false;
    private boolean mHeadAndEmptyEnable = false;
    private boolean mEmptyEnable = false;
    protected int mLayoutResId;
    protected Context mContext;
    protected List<T> mData;
    private LayoutInflater mLayoutInflater;
    private OnRecyclerItemClickListener onRecyclerItemClickListener;
    private OnRecyclerItemLongClickListener onRecyclerItemLongClickListener;
    private View mHeaderView;
    private View mFooterView;
    private OnLoadMoreListener loadMoreListener;
    private View mEmptyView;
    private int loadDoneTip;

    public BaseRecyclerAdapter(Context context, int layoutResId, List<T> data) {
        mData = data;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        if (layoutResId != 0) {
            this.mLayoutResId = layoutResId;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder viewHolder = null;
        switch (viewType) {
            case VIEW_TYPE_LOAD_MORE:
                // 创建footer对应的ViewHolder
                View footerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_load_more_footer, parent, false);
                viewHolder = new BaseViewHolder(footerView);
                break;
            case VIEW_TYPE_LOAD_DONE:
                View tipView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_load_done_tip, parent, false);
                TextView tipTv = (TextView) tipView.findViewById(R.id.done_tip_tv);
                tipTv.setText(loadDoneTip);
                viewHolder = new BaseViewHolder(tipView);
                break;
            case VIEW_TYPE_HEADER:
                viewHolder = new BaseViewHolder(mHeaderView);
                break;
            case VIEW_TYPE_FOOTER:
                viewHolder = new BaseViewHolder(mFooterView);
                break;
            case VIEW_TYPE_EMPTY_VIEW:
                if (mHeadAndEmptyEnable) {
                    ViewGroup.LayoutParams layoutParams = mEmptyView.getLayoutParams();
                    int emptyHeight = parent.getHeight() - mHeaderView.getHeight();
                    layoutParams.height = emptyHeight;
                }
                viewHolder = new BaseViewHolder(mEmptyView);
                break;
            default:
                viewHolder = createDefaultViewHolder(mLayoutResId, parent, viewType);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_LOAD_MORE:
                if (loadMoreListener != null) {
                    loadMoreListener.onLoadMore();
                }
                break;
            case VIEW_TYPE_LOAD_DONE:
                break;
            case VIEW_TYPE_HEADER:
                break;
            case VIEW_TYPE_FOOTER:
                break;
            case VIEW_TYPE_EMPTY_VIEW:
                break;
            default:
                // 默认普通item
                int itemPosition = position - getHeaderViewsCount();
                if (itemPosition < mData.size()) {
                    convert(holder, mData.get(itemPosition), itemPosition);
                    initItemClickListener(holder.convertView, itemPosition);
                }
                break;
        }
    }

    protected void convert(BaseViewHolder holder, T item, int position) {
        convert(holder, item);
    }

    protected abstract void convert(BaseViewHolder holder, T item);

    @Override
    public int getItemCount() {
        int count = getCount() + (isShowLoadMoreFooter ? 1 : 0) + getHeaderViewsCount() + getFooterViewsCount() + (isShowLoadDoneTip ? 1 : 0);
        mEmptyEnable = false;
        if ((mHeadAndEmptyEnable && getHeaderViewsCount() == 1 && count == 1) || count == 0) {
            mEmptyEnable = true;
            count += getEmptyViewsCount();
        }
        return count;
    }

    public int getCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView != null && position == 0) {
            return VIEW_TYPE_HEADER;
        }

        if (isShowLoadMoreFooter && (position == getCount() + getHeaderViewsCount())) {
            return VIEW_TYPE_LOAD_MORE;
        }
        if (isShowLoadDoneTip && (position == getCount() + getHeaderViewsCount())) {
            return VIEW_TYPE_LOAD_DONE;
        }
        if (mFooterView != null && (position == getCount() + getHeaderViewsCount())) {
            return VIEW_TYPE_FOOTER;
        }
        if (mEmptyView != null && getItemCount() == (mHeadAndEmptyEnable ? 2 : 1) && mEmptyEnable) {
            return VIEW_TYPE_EMPTY_VIEW;
        }
        return getItemType(position - getHeaderViewsCount());
    }

    protected int getItemType(int position) {
        return VIEW_TYPE_ITEM;
    }

    public boolean isShowLoadMoreFooter() {
        return isShowLoadMoreFooter;
    }

    public boolean isShowLoadDoneTip() {
        return isShowLoadDoneTip;
    }

    public void showLoadMoreFooter(boolean isShow) {
        isShowLoadMoreFooter = isShow;
//        Timber.d("isShowLoadMoreFooter: " + isShow);
        try {
            if (isShow) {
                notifyItemInserted(getItemCount());
            } else {
                notifyItemRemoved(getItemCount());
            }
        } catch (Exception e) {
            Log.e(TAG, "notify failed");
        }
    }

    public void showLoadDoneTip(boolean isShow) {
        isShowLoadDoneTip = isShow;
//        Timber.d("isShowLoadDoneTip: " + isShow);
        try {
            if (isShow) {
                notifyItemInserted(getItemCount());
            } else {
                notifyItemRemoved(getItemCount());
            }
        } catch (Exception e) {
            Log.e(TAG, "notify failed");
        }
    }

    public boolean isLoadMoreFooter(int position) {
        return getItemViewType(position) == VIEW_TYPE_LOAD_MORE;
    }

    public boolean isLoadDoneTip(int position) {
        return getItemViewType(position) == VIEW_TYPE_LOAD_DONE;
    }

    public boolean isRecyclerHeaderView(int position) {
        return getItemViewType(position) == VIEW_TYPE_HEADER;
    }

    public boolean isSectionHeader(int position) {
        return false;
    }

    public BaseViewHolder createDefaultViewHolder(int layoutResId, ViewGroup parent, int viewType) {
        return new BaseViewHolder(mContext, getItemView(layoutResId, parent));
    }

    protected View getItemView(int layoutResId, ViewGroup parent) {
        return mLayoutInflater.inflate(layoutResId, parent, false);
    }

    private void initItemClickListener(View convertView, final int position) {
        if (onRecyclerItemClickListener != null) {
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRecyclerItemClickListener.onItemClick(v, position);
                }
            });
        }
        if (onRecyclerItemLongClickListener != null) {
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return onRecyclerItemLongClickListener.onItemLongClick(view, position);
                }
            });
        }
    }

    public List<T> getData() {
        return mData;
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    public T getItem(int position) {
        return mData.get(position);
    }

    public void setLoadDoneTip(int loadDoneTip) {
        this.loadDoneTip = loadDoneTip;
    }

    public interface OnRecyclerItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnRecyclerItemLongClickListener {
        boolean onItemLongClick(View view, int position);
    }

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener l) {
        this.onRecyclerItemClickListener = l;
    }

    public void setOnRecyclerItemLongClickListener(OnRecyclerItemLongClickListener l) {
        this.onRecyclerItemLongClickListener = l;
    }

    public void addHeaderView(View header) {
        if (header == null) {
            throw new RuntimeException("header is null");
        }
        mHeaderView = header;
        this.notifyDataSetChanged();
    }

    public void removeHeaderView() {
        if (mHeaderView != null) {
            mHeaderView = null;
            this.notifyDataSetChanged();
        }
    }

    public void addFooterView(View footer) {
        if (footer == null) {
            throw new RuntimeException("footer is null.");
        }
        mFooterView = footer;
        this.notifyDataSetChanged();
    }

    public void removeFooterView() {
        if (mFooterView != null) {
            mFooterView = null;
            this.notifyDataSetChanged();
        }
    }

    public void setEmptyView(View emptyView) {
        setEmptyView(false, emptyView);
    }

    public void setEmptyView(boolean isHeadAndEmpty, View emptyView) {
        mHeadAndEmptyEnable = isHeadAndEmpty;
        mEmptyView = emptyView;
    }

    public void removeEmptyView() {
        mEmptyView = null;
    }

    public View getEmptyView() {
        return mEmptyView;
    }

    public int getHeaderViewsCount() {
        return mHeaderView == null ? 0 : 1;
    }

    public int getEmptyViewsCount() {
        return mEmptyView == null ? 0 : 1;
    }

    public int getFooterViewsCount() {
        return mFooterView == null ? 0 : 1;
    }

    /**
     * 当加入一个数据源时就会被刷新
     *
     * @param elem
     */
    public void add(T elem) {
        int index = mData.indexOf(elem);
        add(index, elem);
    }

    public void add(int location, T elem) {
        mData.add(location, elem);
        notifyItemInserted(location + getHeaderViewsCount());
    }

    public void addAll(List<T> elem) {
        mData.addAll(elem);
        notifyDataSetChanged();
    }

    public void addAll(int position, List<T> elem) {
        mData.addAll(position, elem);
        notifyDataSetChanged();
    }

    public void replaceAll(List<T> elem) {
        mData.clear();
        mData.addAll(elem);
        notifyDataSetChanged();
    }

    /**
     * 把老的数据替换为新的数据，并刷新
     *
     * @param oldElem 老的数据源
     * @param newElem 新的数据源
     */
    public void set(T oldElem, T newElem) {
        set(mData.indexOf(oldElem), newElem);
    }

    /**
     * 替换指定位置的数据源
     *
     * @param index 要指定的位置
     * @param elem  数据源
     */
    public void set(int index, T elem) {
        mData.set(index, elem);
        notifyDataSetChanged();
    }

    public void remove(T elem) {
        int index = mData.indexOf(elem);
        remove(index);
    }

    public void remove(int index) {
        if (index >= 0) {
            mData.remove(index);
            notifyItemRemoved(index + getHeaderViewsCount());
        } else {
            Log.e(TAG, "index < 0");
        }
    }


    public boolean contains(T elem) {
        return mData.contains(elem);
    }

    /**
     * 清空所有数据
     */
    public void clear() {
        mData.clear();
        notifyDataSetChanged();
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
