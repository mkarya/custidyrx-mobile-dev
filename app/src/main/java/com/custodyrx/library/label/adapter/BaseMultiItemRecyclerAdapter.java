package com.custodyrx.library.label.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.recyclerview.widget.RecyclerView;


import com.custodyrx.library.label.util.XLog;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author 一叶丶枫凌
 * Created on 2021-06-25 12:32:13
 */
public abstract class BaseMultiItemRecyclerAdapter<T> extends RecyclerView.Adapter<BaseViewHolder<T>> {
    private final List<T> mDataList = new ArrayList<>();

    public Context mContext;
    private int mBaseLayoutResId;

    public BaseMultiItemRecyclerAdapter(Context context, int baseLayoutResId) {
        mContext = context;
        mBaseLayoutResId = baseLayoutResId;
    }

    abstract protected Class<? extends BaseViewHolder<T>> onBindViewTypeToViewHolder(int viewType);

    final public void setData(List<T> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        mDataList.clear();
        mDataList.addAll(list);
        notifyDataSetChanged();
    }

    final public void addData(T list) {
        int position = mDataList.size();
        mDataList.add(list);
        notifyItemInserted(position);
        notifyItemRangeInserted(position, 1);
    }

    final public void addData(int position, T list) {
        mDataList.add(position, list);
        notifyItemInserted(position);
        notifyItemRangeInserted(position, mDataList.size() - position);
    }

    final public List<T> getData() {
        return mDataList;
    }

    public T getData(int position) {
        if (position < 0) {
            XLog.w("getItem--> position < 0");
            return null;
        }

        int size = mDataList.size();
        if (position >= size) {
            XLog.w("getItem--> position:" + position + " >= mDataList.size():" + size);
            return null;
        }
        return mDataList.get(position);
    }

    final public void clear() {
        mDataList.clear();
        notifyDataSetChanged();
    }

    @Override
    final public int getItemCount() {
        return mDataList.size();
    }

    @Override
    final public BaseViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
        Class<? extends BaseViewHolder<T>> clazz = onBindViewTypeToViewHolder(viewType);
        if (clazz == null) {
            XLog.w("未绑定的viewType：" + viewType);
            return null;
        }
        View view = null;
        if (mBaseLayoutResId > 0) {
            try {
                view = LayoutInflater.from(parent.getContext()).inflate(mBaseLayoutResId, parent, false);
            } catch (Throwable e) {
                XLog.w("catch info->" + Log.getStackTraceString(e));
            }
        }
        if (view == null) {
            XLog.w("未知的LayoutResId：" + mBaseLayoutResId);
            return null;
        }
        try {
            BaseViewHolder<T> baseViewHolder;
            Constructor<? extends BaseViewHolder<T>> declaredConstructor = clazz.getDeclaredConstructor(View.class);

            declaredConstructor.setAccessible(true);

            baseViewHolder = declaredConstructor.newInstance(view);

            baseViewHolder.onCreate(mContext, this);

            return baseViewHolder;
        } catch (Throwable e) {
            XLog.w("catch info->" + Log.getStackTraceString(e));
//            如果这里异常了，可以换成对应的ViewHolder进行测试
//            baseViewHolder = new ViewHolder(view, mContext, this);
            return null;
        }
    }

    @Override
    final public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.onBindData(position);
    }

    @Override
    final public void onViewRecycled(BaseViewHolder holder) {
        holder.onViewRecycled();
    }

    @Override
    final public void onViewDetachedFromWindow(BaseViewHolder holder) {
        holder.onViewDetachedFromWindow();
    }

    @CallSuper
    public void onDestroy() {
        mDataList.clear();
        mContext = null;
    }
}