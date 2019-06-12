package cn.fengwoo.sealsteward.utils;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public class MyLayoutManager extends GridLayoutManager {
    public MyLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
        setAutoMeasureEnabled(false);
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
//        final int width = RecyclerView.LayoutManager.chooseSize(widthSpec,
//                getPaddingLeft() + getPaddingRight(),
//                3);
//        final int height = RecyclerView.LayoutManager.chooseSize(heightSpec,
//                getPaddingTop() + getPaddingBottom(),
//                3);
//        setMeasuredDimension(width, height *3);

//        View view = recycler.getViewForPosition(0);
//        if (view != null) {
//            measureChild(view, widthSpec, heightSpec);
//            //int measuredWidth = View.MeasureSpec.getSize(widthSpec);
//            int measuredHeight = view.getMeasuredHeight();
//            int showHeight = measuredHeight * state.getItemCount();
////            if(state.getItemCount() >= 5){
//                showHeight = measuredHeight * 3;
////            }
//            setMeasuredDimension(widthSpec, showHeight);
//        }

        int count = state.getItemCount();
        if (count > 0) {
            int realWidth = 0;
            int realHeight = 0;
//                    for(int i = 0;i < count; i++){
            View view = recycler.getViewForPosition(0);
            if (view != null) {
                measureChild(view, widthSpec, heightSpec);
                realWidth = View.MeasureSpec.getSize(widthSpec);
                realHeight = View.MeasureSpec.getSize(heightSpec);

//                            realWidth = realWidth > measuredWidth ? realWidth : measureWidth;
//                            realHeight += measureHeight;

            }
            setMeasuredDimension(realHeight, realHeight*3);
//                    }
        } else {
            super.onMeasure(recycler, state, widthSpec, heightSpec);
        }
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }
}
