package org.pub.girlview;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.pub.girlview.adapter.GirlAdatper;
import org.pub.girlview.adapter.ItemAdatper;
import org.pub.girlview.domain.Girl;
import org.pub.girlview.domain.Item;
import org.pub.girlview.scanner.IndexScanner;
import org.pub.girlview.tools.$;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout refreshLayout;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = (MenuItem item) -> {
        if (!$.isNetWorkConnected(this)) {
            ProgressDialog.show(this, "无网络连接，请检查然后重试", "", true, true);
            return false;
        }
        RecyclerView.LayoutManager layoutManager;
        switch (item.getItemId()) {
            case R.id.navigation_home:
                MainActivity.this.setTitle(R.string.app_name);
                layoutManager = new GridLayoutManager(this, 3);
                mRecyclerView.setLayoutManager(layoutManager);
                new NewestAsyncTask().execute();
                return true;
            case R.id.navigation_dashboard:
                MainActivity.this.setTitle(R.string.hotest);
                layoutManager = new GridLayoutManager(this, 4);
                mRecyclerView.setLayoutManager(layoutManager);
                new HostestAsyncTask().execute();
                return true;
            case R.id.navigation_notifications:
                MainActivity.this.setTitle(R.string.latest);
                layoutManager = new GridLayoutManager(this, 4);
                mRecyclerView.setLayoutManager(layoutManager);
                new UpdateGirlAsyncTask().execute();
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

        refreshLayout = findViewById(R.id.swipe_refresh);

        mRecyclerView = findViewById(R.id.mRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setRecyclerListener(holder -> {
        });
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
        });

        if (!$.isNetWorkConnected(this)) {
            ProgressDialog.show(this, "无网络连接，请检查然后重试", "", true, true);
            return;
        }

        new NewestAsyncTask().execute();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("此为开发版本");
        builder.setNegativeButton("确定", null);
        builder.show();
    }

    /**
     * Newest Asynctask
     */
    private class NewestAsyncTask extends AsyncTask<Void, Void, Item[]> {

        @Override
        protected Item[] doInBackground(Void... items) {
            List<Item> item = IndexScanner.getGalleryItems();
            return item.toArray(new Item[item.size()]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            refreshLayout.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(Item[] items) {
            super.onPostExecute(items);
            ItemAdatper adapter = new ItemAdatper(items);
            adapter.setOnItemClickListener((View view, int position) -> {
                new Thread(() -> {
//                    ShowScanner scanner = new ShowScanner(href);
//                    ArrayList<String> srcs = scanner.getAllImages();
                    AlbumActivity.startAction(MainActivity.this, items[position]);
                }).start();
            });
            mRecyclerView.setAdapter(adapter);
            refreshLayout.setRefreshing(false);
        }
    }


    /**
     * Hostest Asynctask
     */
    private class HostestAsyncTask extends AsyncTask<Void, Void, Girl[]> {

        @Override
        protected Girl[] doInBackground(Void... items) {
            List<Girl> girls = IndexScanner.getHotestGirls();
            return girls.toArray(new Girl[girls.size()]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            refreshLayout.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(Girl[] items) {
            super.onPostExecute(items);
            GirlAdatper adapter = new GirlAdatper(items);
            adapter.setOnItemClickListener((View view, int position) -> {
                DetailActivity.startAction(MainActivity.this, items[position]);
            });
            mRecyclerView.setAdapter(adapter);
            refreshLayout.setRefreshing(false);
        }
    }


    /**
     * UpdateGirl Asynctask
     */
    private class UpdateGirlAsyncTask extends AsyncTask<Void, Void, Girl[]> {

        @Override
        protected Girl[] doInBackground(Void... items) {
            List<Girl> girls = IndexScanner.getUpdateGirls();
            return girls.toArray(new Girl[girls.size()]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            refreshLayout.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(Girl[] items) {
            super.onPostExecute(items);
            GirlAdatper adapter = new GirlAdatper(items);
            adapter.setOnItemClickListener((View view, int position) -> {
                DetailActivity.startAction(MainActivity.this, items[position]);
            });
            mRecyclerView.setAdapter(adapter);
            refreshLayout.setRefreshing(false);
        }
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
    public void onBackPressed() {
        super.onBackPressed();
    }
}
