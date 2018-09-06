package org.pub.girlview.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.jetbrains.annotations.NotNull;

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


}
