package org.pub.girlview.customview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.aspsine.swipetoloadlayout.SwipeRefreshTrigger;
import com.aspsine.swipetoloadlayout.SwipeTrigger;

public class RefreshHeaderView extends android.support.v7.widget.AppCompatTextView implements SwipeRefreshTrigger, SwipeTrigger {
    public RefreshHeaderView(Context context) {
        super(context);
    }

    public RefreshHeaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshHeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onRefresh() {
        setText("刷新中...");
    }

    @Override
    public void onPrepare() {
        setText("刷新中...");
    }

    @Override
    public void onMove(int i, boolean b, boolean b1) {

    }

    @Override
    public void onRelease() {
        setText("刷新中...");
    }

    @Override
    public void onComplete() {
        setText("刷新完成");
    }

    @Override
    public void onReset() {
        setText("刷新中...");
    }
}
