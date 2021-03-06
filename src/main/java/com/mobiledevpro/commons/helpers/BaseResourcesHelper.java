package com.mobiledevpro.commons.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

/**
 * Helper class for work with resources
 * <p>
 * Created by Dmitriy V. Chernysh
 * <p>
 * https://instagr.am/mobiledevpro
 * #MobileDevPro
 */

public class BaseResourcesHelper {
    /**
     * Get drawable  (compatible)
     *
     * @param drawableResId Resource Drawable ID
     * @return Drawable
     */
    public static Drawable getDrawableCompatible(Context context, @DrawableRes int drawableResId) {
        if (context == null) {
            throw new RuntimeException("Context can not be NULL");
        }
        if (drawableResId == 0) {
            throw new RuntimeException("Drawable Resource ID can not be 0");
        }

        Resources res = context.getResources();
        return res.getDrawable(drawableResId, context.getTheme());

    }

    public static Drawable getThemeDrawable(Context context, @AttrRes int drawableResId) {
        if (context == null) {
            throw new RuntimeException("Context can not be NULL");
        }
        if (drawableResId == 0) {
            throw new RuntimeException("Drawable Resource ID can not be 0");
        }

        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(drawableResId, value, true);

        return context.getResources().getDrawable(value.resourceId, context.getTheme());
    }

    /**
     * Get color (compatible)
     *
     * @param id Resource Color ID
     * @return Color
     */
    @ColorInt
    public static int getColorCompatible(Context context, @ColorRes int id) {
        if (context == null) {
            throw new RuntimeException("Context can not be NULL");
        }
        if (id == 0) {
            throw new RuntimeException("Color Resource ID can not be 0");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.getColor(context, id);
        } else {
            return context.getResources().getColor(id);
        }
    }

    @ColorInt
    public static int getThemeColorCompatible(Context context, @AttrRes int id) {
        if (context == null) {
            throw new RuntimeException("Context can not be NULL");
        }
        if (id == 0) {
            throw new RuntimeException("Color Resource ID can not be 0");
        }

        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(id, value, true);
        return value.data;
    }

    /**
     * Get VectorDrawable compatible with API < 23
     *
     * @param resId Drawable res id
     * @return Drawable
     */
    public static Drawable getVectorDrawable(Context context, @DrawableRes int resId) {
        if (context == null) {
            throw new RuntimeException("Context can not be NULL");
        }
        if (resId == 0) {
            throw new RuntimeException("Drawable Resource ID can not be 0");
        }
        VectorDrawableCompat d = VectorDrawableCompat.create(
                context.getResources(),
                resId,
                null);
        if (d == null) return null;

        return DrawableCompat.wrap(d);
    }

    /**
     * Get Animated VectorDrawable compatible with API < 23
     *
     * @param resId Drawable res id
     * @return Drawable
     */
    public static Drawable getAnimatedVectorDrawable(Context context, @DrawableRes int resId) {
        if (context == null) {
            throw new RuntimeException("Context can not be NULL");
        }
        if (resId == 0) {
            throw new RuntimeException("Drawable Resource ID can not be 0");
        }
        AnimatedVectorDrawableCompat d = AnimatedVectorDrawableCompat.create(
                context,
                resId
        );
        if (d == null) return null;

        return DrawableCompat.wrap(d);
    }

    /**
     * Change Status Bar background color (API21+)
     *
     * @param activity   Activity
     * @param colorResId Color Resource Id
     */
    public static void setStatusBarColor(Activity activity, @ColorRes int colorResId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(
                    getColorCompatible(activity, colorResId)
            );
        }
    }

    /**
     * Convert Drawable to Bitmap (including Vector Drawable)
     *
     * @param context    Context
     * @param drawableId Drawable Res id
     * @return Bitmap
     */
    public static Bitmap getBitmapFromDrawable(Context context, @DrawableRes int drawableId) throws RuntimeException {
        if (context == null) {
            throw new RuntimeException("Context can not be NULL");
        }
        if (drawableId == 0) {
            throw new RuntimeException("Drawable Resource ID can not be 0");
        }

        Drawable drawable;
        VectorDrawableCompat d;

        if (Build.VERSION.SDK_INT >= 21) {
            try {
                d = VectorDrawableCompat.create(
                        context.getResources(),
                        drawableId,
                        null);
            } catch (Resources.NotFoundException e) {
                d = null;
            }
        } else {
            d = null;
        }

        if (d != null) {
            drawable = DrawableCompat.wrap(d);
        } else {
            drawable = ContextCompat.getDrawable(context, drawableId);
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawableCompat || drawable instanceof LayerDrawable) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            throw new IllegalArgumentException("Unsupported drawable type");
        }
    }

    /**
     * Convert DP to PX
     *
     * @param context   Context
     * @param valueInDp DP
     * @return PX
     */
    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    /**
     * Convert PX to DP
     *
     * @param context   Context
     * @param valueInPx PX
     * @return DP
     */
    public static int pxToDp(Context context, int valueInPx) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(valueInPx / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    /**
     * Get Display size
     *
     * @param activity Activity
     * @return {width, height}
     */
    public static int[] getDisplaySize(Activity activity) {
        Point displaySize = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(displaySize);
        int displayWidth = displaySize.x;
        int displayHeight = displaySize.y;

        return new int[]{displayWidth, displayHeight};
    }

}
