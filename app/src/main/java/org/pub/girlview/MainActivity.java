package org.pub.girlview;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;

import org.pub.girlview.adapter.GirlAdapter;
import org.pub.girlview.adapter.ItemAdapter;
import org.pub.girlview.base.BaseActivity;
import org.pub.girlview.customview.MyGridLayoutManager;
import org.pub.girlview.domain.Girl;
import org.pub.girlview.domain.Item;
import org.pub.girlview.scanner.IndexScanner;
import org.pub.girlview.tools.$;
import org.pub.girlview.tools.Widget$;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private SwipeToLoadLayout loadLayout;

    private enum Status {
        NEWS, HOTTEST, UPDATE
    }

    private Status status = Status.NEWS;
    private static Integer newsIndex = 1;
    private static Integer hottestIndex = 1;
    private static Integer updateIndex = 1;
    private List<Item> newsItems = new ArrayList<>();
    private List<Girl> hottestItems = new ArrayList<>();
    private List<Girl> updateItems = new ArrayList<>();
    private ItemAdapter newsAdapter;
    private GirlAdapter hottestAdapter;
    private GirlAdapter updateAdapter;
    private boolean newsFirst = true;
    private boolean updateFirst = true;
    private boolean hottestFirst = true;
    private int newsPos = 0;
    private int updatePos = 0;
    private int hottestPos = 0;

    private MyGridLayoutManager layoutManagerNews = new MyGridLayoutManager(this, 3);
    private MyGridLayoutManager layoutManagerHottest = new MyGridLayoutManager(this, 4);
    private MyGridLayoutManager layoutManagerUpdate = new MyGridLayoutManager(this, 4);


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = (MenuItem item) -> {
        if (!$.isNetWorkConnected(this)) {
            ProgressDialog.show(this, "无网络连接，请检查然后重试", "", true, true);
            return false;
        }
        switch (item.getItemId()) {
            case R.id.navigation_news:
                MainActivity.this.setTitle(R.string.app_name);
                if (layoutManagerHottest.findFirstVisibleItemPosition() != -1)
                    hottestPos = layoutManagerHottest.findFirstVisibleItemPosition();
                if (layoutManagerUpdate.findFirstVisibleItemPosition() != -1)
                    updatePos = layoutManagerUpdate.findFirstVisibleItemPosition();
                if (newsFirst) {
                    loadLayout.setRefreshing(true);
                    asyncNews(1);
                    newsFirst = false;
                } else {
                    mRecyclerView.setLayoutManager(layoutManagerNews);
                    mRecyclerView.setAdapter(newsAdapter);
                }
                mRecyclerView.scrollToPosition(newsPos);
                status = Status.NEWS;
                return true;

            case R.id.navigation_hottest:
                MainActivity.this.setTitle(R.string.hottest);
                if (layoutManagerNews.findFirstVisibleItemPosition() != -1)
                    newsPos = layoutManagerNews.findFirstVisibleItemPosition();
                if (layoutManagerUpdate.findFirstVisibleItemPosition() != -1)
                    updatePos = layoutManagerUpdate.findFirstVisibleItemPosition();
                if (hottestFirst) {
                    loadLayout.setRefreshing(true);
                    asyncHottest(1);
                    hottestFirst = false;
                } else {
                    mRecyclerView.setLayoutManager(layoutManagerHottest);
                    mRecyclerView.setAdapter(hottestAdapter);
                }
                mRecyclerView.scrollToPosition(hottestPos);
                status = Status.HOTTEST;
                return true;

            case R.id.navigation_update:
                MainActivity.this.setTitle(R.string.new_girls);
                if (layoutManagerHottest.findFirstVisibleItemPosition() != -1)
                    hottestPos = layoutManagerHottest.findFirstVisibleItemPosition();
                if (layoutManagerNews.findFirstVisibleItemPosition() != -1)
                    newsPos = layoutManagerNews.findFirstVisibleItemPosition();
                if (updateFirst) {
                    loadLayout.setRefreshing(true);
                    asyncUpdate(1);
                    updateFirst = false;
                } else {
                    mRecyclerView.setLayoutManager(layoutManagerUpdate);
                    mRecyclerView.setAdapter(updateAdapter);
                }
                mRecyclerView.scrollToPosition(updatePos);
                status = Status.UPDATE;
                return true;
        }
        return false;
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (!$.isNetWorkConnected(this)) {
            ProgressDialog.show(this, "无网络连接，请检查然后重试", "", true, true);
            return;
        }

        loadLayout = findViewById(R.id.swipe_refresh);
        mRecyclerView = findViewById(R.id.swipe_target);
        mRecyclerView.setHasFixedSize(true);

        initListener();
        initAdapter();

        asyncNews(1);
        newsFirst = false;

        checkNewVersion();
    }

    /**
     * 检测新版本
     */
    private void checkNewVersion() {
        new Thread(() -> {
            String url = $.getNewVersion(this);
            if (url != null) {
                runOnUiThread(() -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("发现新版本");
                    builder.setMessage("功能增加，细节优化\nBug修复等");
                    builder.setPositiveButton("取消", null);
                    builder.setNegativeButton("下载", (dialog, which) -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    });
                    builder.show();
                });
            }
        }).start();
    }


    /**
     * 初始化监听器
     */
    private void initListener() {
        loadLayout.setOnRefreshListener(() -> {
            switch (status) {
                case NEWS:
                    newsItems.clear();
                    asyncNews(newsIndex);
                    break;
                case HOTTEST:
                    hottestItems.clear();
                    asyncHottest(hottestIndex);
                    break;
                case UPDATE:
                    updateItems.clear();
                    asyncUpdate(updateIndex);
                    break;
            }
        });
        loadLayout.setOnLoadMoreListener(() -> {
            switch (status) {
                case NEWS:
                    asyncNews(++newsIndex);
                    break;
                case HOTTEST:
                    asyncHottest(++hottestIndex);
                    break;
                case UPDATE:
                    asyncUpdate(++updateIndex);
                    break;
            }
        });
    }


    /**
     * 初始化适配器
     */
    private void initAdapter() {
        // News
        newsAdapter = new ItemAdapter(newsItems);
        newsAdapter.setOnItemClickListener((View view, int position) -> {
            new Thread(() -> {
                AlbumActivity.startAction(MainActivity.this, newsItems.get(position));
            }).start();
        });

        // Updates
        updateAdapter = new GirlAdapter(updateItems);
        updateAdapter.setOnItemClickListener((View view, int position) -> {
            new Thread(() -> {
                DetailActivity.startAction(MainActivity.this, updateItems.get(position));
            }).start();
        });

        // Hottest
        hottestAdapter = new GirlAdapter(hottestItems);
        hottestAdapter.setOnItemClickListener((View view, int position) -> {
            new Thread(() -> {
                DetailActivity.startAction(MainActivity.this, hottestItems.get(position));
            }).start();
        });
    }


    /**
     * Newest AsyncTask
     */
    private class NewestAsyncTask extends AsyncTask<Integer, Void, List<Item>> {

        @Override
        protected List<Item> doInBackground(Integer... items) {
            try {
                return IndexScanner.getGalleryItems(items[0]);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Item> items) {
            super.onPostExecute(items);
            if (items == null) {
                showServerUnUsedDialog();
                loadLayout.setRefreshing(false);
                loadLayout.setLoadingMore(false);
                return;
            }
            newsItems.addAll(items);
            mRecyclerView.setLayoutManager(layoutManagerNews);
            mRecyclerView.setAdapter(newsAdapter);
            newsAdapter.notifyDataSetChanged();
            mRecyclerView.scrollToPosition(newsItems.size() - items.size() - 1);
            loadLayout.setRefreshing(false);
            loadLayout.setLoadingMore(false);
        }
    }


    /**
     * Hostest Asynctask
     */
    private class HottestAsyncTask extends AsyncTask<Integer, Void, List<Girl>> {

        @Override
        protected List<Girl> doInBackground(Integer... items) {
            try {
                return IndexScanner.getHottestGirls(items[0]);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Girl> items) {
            super.onPostExecute(items);
            if (items == null) {
                showServerUnUsedDialog();
                loadLayout.setRefreshing(false);
                loadLayout.setLoadingMore(false);
                return;
            }
            hottestItems.addAll(items);
            mRecyclerView.setLayoutManager(layoutManagerHottest);
            mRecyclerView.setAdapter(hottestAdapter);
            hottestAdapter.notifyDataSetChanged();
            mRecyclerView.scrollToPosition(hottestItems.size() - items.size() - 1);
            loadLayout.setRefreshing(false);
            loadLayout.setLoadingMore(false);
        }
    }


    /**
     * UpdateGirl Asynctask
     */
    private class UpdateGirlAsyncTask extends AsyncTask<Integer, Void, List<Girl>> {

        @Override
        protected List<Girl> doInBackground(Integer... items) {
            try {
                return IndexScanner.getUpdateGirls(items[0]);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Girl> items) {
            super.onPostExecute(items);
            if (items == null) {
                showServerUnUsedDialog();
                loadLayout.setRefreshing(false);
                loadLayout.setLoadingMore(false);
                return;
            }
            updateItems.addAll(items);
            mRecyclerView.setLayoutManager(layoutManagerUpdate);
            mRecyclerView.setAdapter(updateAdapter);
            updateAdapter.notifyDataSetChanged();
            mRecyclerView.scrollToPosition(updateItems.size() - items.size() - 1);
            loadLayout.setRefreshing(false);
            loadLayout.setLoadingMore(false);
        }
    }


    /**
     * Unused Message Dialog
     */
    private void showServerUnUsedDialog() {
        Widget$.showFailedDialog(MainActivity.this, "出错了", "加载失败，服务暂时不可用。");
    }


    private void asyncNews(Integer index) {
        new NewestAsyncTask().execute(index);
        status = Status.NEWS;
    }

    private void asyncUpdate(Integer index) {
        new UpdateGirlAsyncTask().execute(index);
        status = Status.UPDATE;
    }

    private void asyncHottest(Integer index) {
        new HottestAsyncTask().execute(index);
        status = Status.HOTTEST;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private static long beforeTime = System.currentTimeMillis();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - beforeTime > 2.8 * 1000) {
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            beforeTime = currentTime;
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_bar_search, menu);
        SearchManager manager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView view = (SearchView) menu.findItem(R.id.ab_search).getActionView();
        view.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }
}
