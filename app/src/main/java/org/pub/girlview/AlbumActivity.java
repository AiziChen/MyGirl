package org.pub.girlview;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.pub.girlview.domain.Item;
import org.pub.girlview.scanner.ShowScanner;

import java.util.List;

public class AlbumActivity extends AppCompatActivity {

    private static final String EXTRA_ITEM = "extra_item";

    private Item item;

    public static void startAction(Activity act, Item item) {
        Intent intent = new Intent(act, AlbumActivity.class);
        intent.putExtra(EXTRA_ITEM, item);
        act.startActivity(intent);
    }

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);

        Intent intent = getIntent();
        if (intent != null) {
            item = intent.getParcelableExtra(EXTRA_ITEM);
            toolbar.setTitle(item.getTitle());
            new AlbumAsyncTask().execute(item);
        }
    }


    private class AlbumAsyncTask extends AsyncTask<Item, Void, List<String>> {

        ProgressDialog pd;

        @Override
        protected List<String> doInBackground(Item... items) {
            ShowScanner scanner = new ShowScanner(items[0].getHref());
            return scanner.getAllImages();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(AlbumActivity.this, "", getString(R.string.loading), true, true);
        }

        @Override
        protected void onPostExecute(List<String> items) {
            super.onPostExecute(items);
            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), items);
            mViewPager.setAdapter(mSectionsPagerAdapter);
            pd.dismiss();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_album, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_saveImage) {
            Toast.makeText(this, "功能未开放", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_SECTION_ITEM = "section_item";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(String src) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putString(ARG_SECTION_ITEM, src);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_album, container, false);
            String src = getArguments().getString(ARG_SECTION_ITEM);
            ImageView albumImage = rootView.findViewById(R.id.albumImage);
            Picasso.get().load(src).into(albumImage);
            return rootView;
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private List<String> items;

        public SectionsPagerAdapter(FragmentManager fm, List<String> items) {
            super(fm);
            this.items = items;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(items.get(position));
        }

        @Override
        public int getCount() {
            return items.size();
        }
    }
}
