package org.pub.girlview.listener;

import android.view.View;

@FunctionalInterface
public interface OnItemClickListener {
    void onItemClick(View view, int position);
}