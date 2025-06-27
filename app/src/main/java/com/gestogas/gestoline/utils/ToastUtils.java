package com.gestogas.gestoline.utils;


import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.gestogas.gestoline.R;

public class ToastUtils {

//    ToastUtils.show(this, "Todo salió bien", ToastUtils.SUCCESS);
//    ToastUtils.show(this, "Hubo un error", ToastUtils.ERROR);
//    ToastUtils.show(this, "Esto es información", ToastUtils.INFO);

    public static final int SUCCESS = 0;
    public static final int ERROR = 1;
    public static final int INFO = 2;
    private static final int TOAST_DURATION = 2000;

    public static void show(Activity activity, String message, int type) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View toastView = inflater.inflate(R.layout.custom_toast, null);

        TextView text = toastView.findViewById(R.id.toast_text);
        ImageView icon = toastView.findViewById(R.id.toast_icon);
        View container = toastView.findViewById(R.id.toast_container);

        text.setText(message);

        int bgColor;
        int iconRes;

        switch (type) {
            case SUCCESS:
                bgColor = Color.parseColor("#03c866");
                iconRes = R.drawable.icon_toast_check_24;
                break;
            case ERROR:
                bgColor = Color.parseColor("#c80303");
                iconRes = R.drawable.icon_toast_error_24;
                break;
            default:
                bgColor = Color.parseColor("#038cc8");
                iconRes = R.drawable.icon_toast_info_24;
                break;
        }

        icon.setImageResource(iconRes);
        ((GradientDrawable) container.getBackground()).setColor(bgColor);

        // Animación de entrada: deslizante + fade in
        AnimationSet enterAnim = new AnimationSet(true);
        enterAnim.addAnimation(new TranslateAnimation(0, 0, 100, 0));
        enterAnim.addAnimation(new AlphaAnimation(0, 1));
        enterAnim.setDuration(400);
        toastView.startAnimation(enterAnim);

        // Agregar al layout raíz
        Window window = activity.getWindow();
        FrameLayout root = (FrameLayout) window.getDecorView();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL
        );
        params.bottomMargin = 200;
        root.addView(toastView, params);

        // Animación de salida y remoción
        new Handler().postDelayed(() -> {
            AnimationSet exitAnim = new AnimationSet(true);
            exitAnim.addAnimation(new TranslateAnimation(0, 0, 0, 100));
            exitAnim.addAnimation(new AlphaAnimation(1, 0));
            exitAnim.setDuration(400);
            toastView.startAnimation(exitAnim);

            new Handler().postDelayed(() -> root.removeView(toastView), 400);
        }, 2500);
    }

    public static void showAndThen(Activity activity, String message, int type, Runnable afterDelay) {
        show(activity, message, type);
        new Handler().postDelayed(afterDelay, TOAST_DURATION);
    }
}
