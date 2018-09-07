package com.steven.selectimage.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 状态栏工具类（沉浸式）
 */
public class StatusBarUtil {

    /**设置状态栏透明与字体颜色*/
    public static void setStatusBarTrans(Activity activity, boolean lightStatusBar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        ILightStatusBar IMPL;
        if (MIUILightStatusBarImpl.isMe()) {
            IMPL = new MIUILightStatusBarImpl();
        } else if (MeizuLightStatusBarImpl.isMe()) {
            IMPL = new MeizuLightStatusBarImpl();
        } else {
            IMPL = new ILightStatusBar() {
                @Override
                public void setLightStatusBar(Window window, boolean lightStatusBar) {
                }
            };
        }
        IMPL.setLightStatusBar(activity.getWindow(), lightStatusBar);
    }

    /**小米状态栏设置类*/
    public static class MIUILightStatusBarImpl implements ILightStatusBar {
        static boolean isMe() {
            return "Xiaomi".equals(Build.MANUFACTURER);
        }

        public void setLightStatusBar(Window window, boolean lightStatusBar) {
            Class<? extends Window> clazz = window.getClass();
            try {
                Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                int darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                extraFlagField.invoke(window, lightStatusBar ? darkModeFlag : 0, darkModeFlag);
            } catch (Exception e) {
            }
        }
    }

    /**魅族状态栏设置类*/
    public static class MeizuLightStatusBarImpl implements ILightStatusBar {
        static boolean isMe() {
            final Method method;
            try {
                method = Build.class.getMethod("hasSmartBar");
                return method != null;
            } catch (NoSuchMethodException e) {
            }
            return false;
        }

        public void setLightStatusBar(Window window, boolean lightStatusBar) {
            WindowManager.LayoutParams params = window.getAttributes();
            try {
                Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(params);
                if (lightStatusBar) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(params, value);
                window.setAttributes(params);
                darkFlag.setAccessible(false);
                meizuFlags.setAccessible(false);
            } catch (Exception e) {
            }
        }
    }

    interface ILightStatusBar {

        void setLightStatusBar(Window window, boolean lightStatusBar);
    }

    /**
     * 设置状态栏的颜色
     */
    @TargetApi(19)
    public static void statusBarTintColor(Activity activity, int color) {
        // 代表 5.0 及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(color);
            return;
        }

        // versionCode > 4.4  and versionCode < 5.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 在原来的位置上添加一个状态栏
            View statusBarView = createStatusBarView(activity);
            ViewGroup androidContainer = (ViewGroup ) activity.getWindow().getDecorView();
            androidContainer = (ViewGroup ) androidContainer.getChildAt(0);
            androidContainer.addView(statusBarView, 0);
            statusBarView.setBackgroundColor(color);
        }
    }

    /**
     * 创建一个需要填充statusBarView
     */
    private static View createStatusBarView(Activity activity) {
        View statusBarView = new View(activity);
        ViewGroup.LayoutParams statusBarParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity));
        statusBarView.setLayoutParams(statusBarParams);
        return statusBarView;
    }

    /**
     * 获取状态栏的高度
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    /**
     * 状态栏透明,整个界面全屏
     */
    public static void statusBarTranslucent(Activity activity) {
        // 代表 5.0 及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = activity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            return;
        }
        // versionCode > 4.4  and versionCode < 5.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}
