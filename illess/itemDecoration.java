package com.example.illess;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class itemDecoration extends RecyclerView.ItemDecoration{
    private int space;

    public void SpaceItemDecoration(int space) {
        this.space = space;
    }
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = 30;
        if (parent.getChildLayoutPosition(view) %2==0) {
            outRect.left = 60;
            outRect.right = 30;
        }else{
            outRect.left = 30;
            outRect.right = 60;
        }
    }
}
