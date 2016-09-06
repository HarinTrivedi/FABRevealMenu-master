package com.hlab.fabrevealmenu.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

public class DynamicGridLayoutManager extends GridLayoutManager {
    private int minItemWidth;
    private int totalItems;

    public DynamicGridLayoutManager(Context context, int minItemWidth, int totalItems) {
        super(context, 1);
        this.minItemWidth = minItemWidth;
        this.totalItems = totalItems;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler,
                                 RecyclerView.State state) {
        updateSpanCount();
        super.onLayoutChildren(recycler, state);
    }

    private void updateSpanCount() {
        int spanCount = 1;
        if (minItemWidth != 0) {
            spanCount = Math.min((getWidth() / minItemWidth), totalItems);
            if (spanCount < 1)
                spanCount = 1;
        }
        this.setSpanCount(spanCount);
    }
}