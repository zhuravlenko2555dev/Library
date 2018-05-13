package com.zhuravlenko2555dev.library.ui;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration {
    private Context context;
    private int space;

    public RecyclerViewItemDecoration(Context context) {
        this.context = context;

        space = (int) (10 * context.getResources().getDisplayMetrics().density);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (parent.getChildAdapterPosition(view) != 0) {
            outRect.set(0, space, 0, 0);
        }
    }
}
