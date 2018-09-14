package org.pub.girlview.tools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 工具类
 */
public class $ {

    public static boolean isNetWorkConnected(@NotNull Context c) {
        ConnectivityManager manager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null) {
            return info.isAvailable() && info.isConnectedOrConnecting();
        } else {
            return false;
        }
    }


    public static boolean isMobileNetWorkConnected(@NotNull Context c) {
        ConnectivityManager manager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (info != null) {
            return info.isAvailable() && info.isConnectedOrConnecting();
        } else {
            return false;
        }
    }


    public static boolean isWifiConnected(@NotNull Context c) {
        ConnectivityManager manager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (info != null) {
            return info.isAvailable() && info.isConnectedOrConnecting();
        } else {
            return false;
        }
    }


    /**
     * Get new Version
     * @param c
     * @return
     */
    public static String getNewVersion(Context c) {
        FutureTask<String> task = new FutureTask<>(() -> {
            double curV = Double.parseDouble(getVersionName(c));
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://www.quanye.xyz/android/getVersion")
                    .build();
            Response response = null;
            double newV = 0;
            try {
                response = client.newCall(request).execute();
                newV = Double.parseDouble(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (newV > curV) {
                return "http://www.quanye.xyz/android/file/new-version" + newV + ".apk";
            } else {
                return null;
            }
        });

        try {
            new Thread(task).start();
            return task.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Get this app version-code
     *
     * @param c context
     * @return version code
     */
    private static String getVersionName(Context c) {
        PackageManager packageManager = c.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(c.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packInfo != null ? packInfo.versionName : null;
    }
}
