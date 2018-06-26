package me.shouheng.commons.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;

/**
 * @author shouh
 * @version $Id: ThemeUtils, v 0.1 2018/6/7 22:10 shouh Exp$
 */
public class ThemeUtils {

    /**
     * should use colored status bar */
    private static boolean useThemeStatusBarColor = false;

    /**
     * status text and image style, true use dark, false light */
    private static boolean useStatusBarColor = false;

    public static void customStatusBar(Activity activity) {
        // 5.0 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = activity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            if (useThemeStatusBarColor) {
                TypedValue typedValue = new TypedValue();
                activity.getTheme().resolveAttribute(android.R.attr.colorPrimaryDark, typedValue, true);
                activity.getWindow().setStatusBarColor(typedValue.data);
            } else {
                activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 4.4 to 5.0
            WindowManager.LayoutParams localLayoutParams = activity.getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !useStatusBarColor) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarColor(Activity activity, @ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(color);
        }
    }

    public static void hideSystemUI(Activity activity) {
        activity.runOnUiThread(() -> activity.getWindow().getDecorView().setSystemUiVisibility(
                SystemUiVisibilityUtil.getSystemVisibility()));
    }

    public static void setUseThemeStatusBarColor(boolean useThemeStatusBarColor) {
        ThemeUtils.useThemeStatusBarColor = useThemeStatusBarColor;
    }

    public static void setUseStatusBarColor(boolean useStatusBarColor) {
        ThemeUtils.useStatusBarColor = useStatusBarColor;
    }
}
