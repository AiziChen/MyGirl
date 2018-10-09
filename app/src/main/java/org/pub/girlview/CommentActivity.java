package org.pub.girlview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.pub.girlview.domain.Gesture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {

    public static final String EXTRA_COMMENTS = "Comments";

    private ListView listView;
    private List<Gesture> listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        listView = findViewById(R.id.lv_comment);

        Intent intent = getIntent();
        if (intent != null) {
            listData = intent.getParcelableArrayListExtra(EXTRA_COMMENTS);
        }

        List<Map<String, String>> listItems = new ArrayList<>();
        for (Gesture g : listData) {
            Map<String, String> item = new HashMap<>();
            item.put("name", g.getName());
            item.put("comment", g.getComment());
            item.put("area", g.getArea());
            item.put("time", g.getTime());
            listItems.add(item);
        }
        SimpleAdapter adapter = new SimpleAdapter(this,
                listItems,
                R.layout.comment_item,
                new String[]{"name", "comment", "area", "time"},
                new int[]{R.id.tv_item_name, R.id.tv_item_comment, R.id.tv_item_area, R.id.tv_item_time});

        listView.setAdapter(adapter);
    }


    public static void startAction(Activity activity, ArrayList<Gesture> comments) {
        Intent intent = new Intent(activity, CommentActivity.class);
        intent.putParcelableArrayListExtra(EXTRA_COMMENTS, comments);
        activity.startActivity(intent);
    }
}
