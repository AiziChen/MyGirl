package org.pub.girlview;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.pub.girlview.adapter.ItemAdapter;
import org.pub.girlview.domain.Girl;
import org.pub.girlview.domain.GirlDetail;
import org.pub.girlview.domain.Item;
import org.pub.girlview.scanner.AlbumScanner;
import org.pub.girlview.scanner.DetailScanner;

import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private static final String GIRL_DETAIL = "girl_detail";

    public static void startAction(Activity act, Girl girl) {
        Intent intent = new Intent(act, DetailActivity.class);
        intent.putExtra(GIRL_DETAIL, girl);
        act.startActivity(intent);
    }

    private Girl girl;
    private ImageView imgGirl;
    private TextView tvGirlName;
    private TextView tvGirlInfo;
    private RecyclerView rvGirl;
    private Girl[] grils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        if (intent != null) {
            girl = (Girl) intent.getSerializableExtra(GIRL_DETAIL);
        }

        imgGirl = findViewById(R.id.img_girlImage);
        tvGirlName = findViewById(R.id.tv_girlName);
        tvGirlInfo = findViewById(R.id.tv_girlInfo);

        new InfoAsyncTask().execute(girl);

        rvGirl = findViewById(R.id.rv_girls);
        rvGirl.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        rvGirl.setLayoutManager(layoutManager);

        new AlbumAsyncTask().execute(girl);
    }


    private class InfoAsyncTask extends AsyncTask<Girl, Void, GirlDetail> {

        @Override
        protected GirlDetail doInBackground(Girl... girls) {
            DetailScanner scanner = new DetailScanner(girls[0].getHref());
            return scanner.getGirlDetail();
        }

        @Override
        protected void onPostExecute(GirlDetail girlDetail) {
            super.onPostExecute(girlDetail);
            Picasso.get().load(girlDetail.getSrc()).into(imgGirl);
            tvGirlName.setText(girlDetail.getName());
            tvGirlInfo.setText(girlDetail.getInfo());
        }
    }


    private class AlbumAsyncTask extends AsyncTask<Girl, Void, List<Item>> {

        @Override
        protected List<Item> doInBackground(Girl... girls) {
            AlbumScanner scanner = new AlbumScanner(girls[0].getHref());
            List<Item> items = scanner.getGirlAlbumItems();
            return items;
        }

        @Override
        protected void onPostExecute(List<Item> items) {
            super.onPostExecute(items);
            ItemAdapter adapter = new ItemAdapter(items);
            rvGirl.setAdapter(adapter);
            adapter.setOnItemClickListener((view, position) -> {
                new Thread(() -> {
                    AlbumActivity.startAction(DetailActivity.this, items.get(position));
                }).start();
            });
        }
    }

}
