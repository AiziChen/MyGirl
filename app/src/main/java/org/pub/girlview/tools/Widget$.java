package org.pub.girlview.tools;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

/**
 * Widget Tools
 * @author Quanyec
 */
public class Widget$ {

    private static Toast toast;

    /**
     * Short Time Toast
     * @param ctx
     * @param msg
     */
    public static void showShortToast(Context ctx, String msg) {
        if (toast == null) {
            toast = Toast.makeText(ctx, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    /**
     * Long Time Toast
     * @param ctx
     * @param msg
     */
    public static void showLongToast(Context ctx, String msg) {
        if (toast == null) {
            toast = Toast.makeText(ctx, msg, Toast.LENGTH_LONG);
        } else {
            toast.setText(msg);
            toast.setDuration(Toast.LENGTH_LONG);
        }
        toast.show();
    }


    /**
     * Show Failed Dialog
     * @param ctx
     * @param title
     * @param msg
     */
    public static void showFailedDialog(Context ctx, String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("问题");
        builder.setMessage(msg);
        builder.setPositiveButton("确定", null);
        builder.show();
    }
}
