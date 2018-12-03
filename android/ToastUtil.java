package edu.scut.luluteam.contextlib.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by JJY on 2016/8/24.
 */
public class ToastUtil {

    private static Handler handler;

    static {
        handler = new Handler(Looper.getMainLooper());
    }

    public static void showShortToast(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_LONG).show();
    }

    /**
     * 支持在 <P>非UI线程<P/> 中显示Toast
     * 非线程安全
     *
     * @param context
     * @param msg
     */
    public static void LOG_TOAST(final Context context, final String msg) {
        String TAG = context.getClass().getSimpleName();
        Log.e(TAG, msg);

        if (handler != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }


    }


}
