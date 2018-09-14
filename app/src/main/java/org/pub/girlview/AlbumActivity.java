package org.pub.girlview;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.NotNull;
import org.pub.girlview.base.BaseActivity;
import org.pub.girlview.base.BaseFragment;
import org.pub.girlview.domain.Girl;
import org.pub.girlview.domain.Item;
import org.pub.girlview.scanner.ShowScanner;
import org.pub.girlview.tools.Widget$;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import me.wangyuwei.flipshare.FlipShareView;
import me.wangyuwei.flipshare.ShareItem;
import uk.co.senab.photoview.PhotoView;

public class AlbumActivity extends BaseActivity {

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

    // Ad
    private static InterstitialAd interstitialAd;
    private static AdRequest request;

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
            new AlbumAsyncTask(this).execute(item);
        }

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-5817384692483918/1189939075");
        request = new AdRequest.Builder().build();

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position % 13 == 0) {
                    interstitialAd.loadAd(request);
                    if (interstitialAd.isLoaded()) {
                        interstitialAd.show();
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


    private static class AlbumAsyncTask extends AsyncTask<Item, Void, List<String>> {

        ProgressDialog pd;
        private AlbumActivity activity;

        public AlbumAsyncTask(AlbumActivity activity) {
            this.activity = activity;
        }

        @Override
        protected List<String> doInBackground(Item... items) {
            ShowScanner scanner = new ShowScanner(items[0].getHref());
            return scanner.getAllImages();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(activity, "", activity.getString(R.string.loading), true, true);
        }

        @Override
        protected void onPostExecute(List<String> items) {
            super.onPostExecute(items);
            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            activity.mSectionsPagerAdapter = new SectionsPagerAdapter(activity.getSupportFragmentManager(), items);
            activity.mViewPager.setAdapter(activity.mSectionsPagerAdapter);
            pd.dismiss();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_album, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_girl_info) {
            new GirlInfoAsyncTask(this).execute(AlbumActivity.this.item.getHref());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private static class GirlInfoAsyncTask extends AsyncTask<String, Void, Girl> {

        ProgressDialog pd;
        private AlbumActivity activity;

        public GirlInfoAsyncTask(AlbumActivity activity) {
            this.activity = activity;
        }

        @Override
        protected Girl doInBackground(String... strings) {
            ShowScanner scanner = null;
            try {
                scanner = new ShowScanner(activity.item.getHref(), 1);
                return scanner.getCurrentGirl();
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(activity, "", activity.getString(R.string.loading), true, false);
        }

        @Override
        protected void onPostExecute(Girl girl) {
            super.onPostExecute(girl);
            pd.dismiss();
            if (girl == null) {
                Widget$.showFailedDialog(activity, "出错了", "加载失败，服务暂时不可用。");
                return;
            }
            DetailActivity.startAction(activity, girl);
        }
    }




    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends BaseFragment {
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

        private View rootView;
        private PhotoView albumImage;
        private String src;

        @Override
        public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_album, container, false);
            albumImage = rootView.findViewById(R.id.albumImage);
//            albumImage.setOnCreateContextMenuListener(PlaceholderFragment.this);
            albumImage.setOnLongClickListener(v -> {
//                v.showContextMenu();
                FlipShareView shareView = new FlipShareView.Builder(getActivity(), albumImage)
                        .addItem(new ShareItem("设为壁纸"))
                        .addItem(new ShareItem("保存"))
                        .create();
                shareView.setOnFlipClickListener(new FlipShareView.OnFlipClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Widget$.showShortToast(getContext(), position + "");
                        if (position == 0) {
                            setWallPaper();
                        } else if (position == 1) {
                            saveImage();
                        }
                    }
                    @Override
                    public void dismiss() {
                    }
                });
                return false;
            });
            src = getArguments().getString(ARG_SECTION_ITEM);
            Picasso.get().load(src).into(albumImage);
            System.err.println(src);

            return rootView;
        }

        @Override
        protected void onFragmentVisibleChange(boolean isVisible) {
            if (isVisible) {
                Widget$.showShortToast(getContext(), "正在加载...");
            } else {
                Widget$.showShortToast(getContext(), "加载关闭...");
            }
        }

        //==============Context Menu=============//
//        private static final String TITLE_SET_TO_WALLPAPER = "设为壁纸";
//        private static final String TITLE_SAVE = "保存";
//
//        @Override
//        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//            super.onCreateContextMenu(menu, v, menuInfo);
//            menu.add(TITLE_SET_TO_WALLPAPER);
//            menu.add(TITLE_SAVE);
//        }
//
//        @Override
//        public boolean onContextItemSelected(MenuItem item) {
//            String title = item.getTitle().toString();
//            if (getUserVisibleHint()) {
//                switch (title) {
//                    case TITLE_SET_TO_WALLPAPER:
//                        setWallPaper();
//                        break;
//                    case TITLE_SAVE:
//                        saveImage();
//                        break;
//                }
//            }
//            return false;
//        }

        private void setWallPaper() {
            WallpaperManager manager = (WallpaperManager) getActivity().getSystemService(WALLPAPER_SERVICE);
            if (manager != null) {
                try {
                    manager.setBitmap(albumImage.getVisibleRectangleBitmap());
                    Widget$.showShortToast(getContext(), "设置壁纸成功");
                    if (interstitialAd.isLoaded()) {
                        interstitialAd.show();
                    }
                } catch (IOException e) {
                    Widget$.showShortToast(getContext(), "设置壁纸失败");
                }
            } else {
                Widget$.showShortToast(getContext(), "设置壁纸失败");
            }
        }


        private Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String imageName = System.currentTimeMillis() + ".png";
                File file = new File(dir.getAbsolutePath() + File.separator + imageName);
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();
                    Widget$.showShortToast(getContext(), "保存成功");
                    if (interstitialAd.isLoaded()) {
                        interstitialAd.show();
                    }
                } catch (FileNotFoundException e) {
                    Widget$.showFailedDialog(getContext(), "保存失败", "请检查文件读取权限");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Widget$.showShortToast(getContext(), "下载图片失败");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Widget$.showShortToast(getContext(), getString(R.string.saving));
            }
        };



        private void saveImage() {
            String src1 = src.replace("/s/", "/");
            Picasso.get().load(src1).into(target);
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public static class SectionsPagerAdapter extends FragmentPagerAdapter {

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
