package me.hgj.jetpackmvvm.demo.app.weight.recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * 自定义Behavior  FAB 上滑显示 下滑隐藏
 */

public class FatScrollAwareFABBehavior extends FloatingActionButton.Behavior {

    public FatScrollAwareFABBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                       @NonNull FloatingActionButton child,
                                       @NonNull View directTargetChild,
                                       @NonNull View target, int axes, int type) {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);
    }


    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
                               @NonNull FloatingActionButton child, @NonNull View target,
                               int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed,
                               int type, @NonNull int[] consumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed);
        if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
            child.setVisibility(View.INVISIBLE);
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            child.show();
        }
    }
}