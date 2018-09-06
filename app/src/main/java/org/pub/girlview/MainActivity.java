package org.pub.girlview;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;

import org.pub.girlview.adapter.GirlAdapter;
import org.pub.girlview.adapter.ItemAdapter;
import org.pub.girlview.domain.Girl;
import org.pub.girlview.domain.Item;
import org.pub.girlview.scanner.IndexScanner;
import org.pub.girlview.tools.$;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private SwipeToLoadLayout loadLayout;

    private enum Status {
        NEWS, HOTEST, UPDATE
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

    private GridLayoutManager layoutManagerNews = new GridLayoutManager(this, 3);
    private GridLayoutManager layoutManagerHottest = new GridLayoutManager(this, 4);
    private GridLayoutManager layoutManagerUpdate = new GridLayoutManager(this, 4);


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
                    asyncNews(1);
                    newsFirst = false;
                } else {
                    mRecyclerView.setLayoutManager(layoutManagerNews);
                    mRecyclerView.setAdapter(newsAdapter);
                }
                mRecyclerView.scrollToPosition(newsPos);
                return true;

            case R.id.navigation_hottest:
                MainActivity.this.setTitle(R.string.hotest);
                if (layoutManagerNews.findFirstVisibleItemPosition() != -1)
                    newsPos = layoutManagerNews.findFirstVisibleItemPosition();
                if (layoutManagerUpdate.findFirstVisibleItemPosition() != -1)
                    updatePos = layoutManagerUpdate.findFirstVisibleItemPosition();
                if (hottestFirst) {
                    asyncHottest(1);
                    hottestFirst = false;
                } else {
                    mRecyclerView.setLayoutManager(layoutManagerHottest);
                    mRecyclerView.setAdapter(hottestAdapter);
                }
                mRecyclerView.scrollToPosition(hottestPos);
                return true;

            case R.id.navigation_update:
                MainActivity.this.setTitle(R.string.latest);
                if (layoutManagerHottest.findFirstVisibleItemPosition() != -1)
                    hottestPos = layoutManagerHottest.findFirstVisibleItemPosition();
                if (layoutManagerNews.findFirstVisibleItemPosition() != -1)
                    newsPos = layoutManagerNews.findFirstVisibleItemPosition();
                if (updateFirst) {
                    asyncUpdate(1);
                    updateFirst = false;
                } else {
                    mRecyclerView.setLayoutManager(layoutManagerUpdate);
                    mRecyclerView.setAdapter(updateAdapter);
                }
                mRecyclerView.scrollToPosition(updatePos);
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
    }


    private void initListener() {
        loadLayout.setOnRefreshListener(() -> {
            loadLayout.setRefreshing(false);
        });
        loadLayout.setOnLoadMoreListener(() -> {
            switch (status) {
                case NEWS:
                    asyncNews(++newsIndex);
                    break;
                case HOTEST:
                    asyncHottest(++hottestIndex);
                    break;
                case UPDATE:
                    asyncUpdate(++updateIndex);
                    break;
            }
        });
    }


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
     * Newest Asynctask
     */
    private class NewestAsyncTask extends AsyncTask<Integer, Void, List<Item>> {

        @Override
        protected List<Item> doInBackground(Integer... items) {
            return IndexScanner.getGalleryItems(items[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadLayout.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(List<Item> items) {
            super.onPostExecute(items);
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
            return IndexScanner.getHotestGirls(items[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadLayout.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(List<Girl> items) {
            super.onPostExecute(items);
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
            return IndexScanner.getUpdateGirls(items[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadLayout.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(List<Girl> items) {
            super.onPostExecute(items);
            updateItems.addAll(items);
            mRecyclerView.setLayoutManager(layoutManagerUpdate);
            mRecyclerView.setAdapter(updateAdapter);
            updateAdapter.notifyDataSetChanged();
            mRecyclerView.scrollToPosition(updateItems.size() - items.size() - 1);
            loadLayout.setRefreshing(false);
            loadLayout.setLoadingMore(false);
        }
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
        status = Status.HOTEST;
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
}
