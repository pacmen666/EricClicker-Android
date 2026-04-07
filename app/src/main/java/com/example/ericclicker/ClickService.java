package com.example.ericclicker;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

public class ClickService extends AccessibilityService {
    public static ClickService instance;
    // 新增：悬浮窗管理器 + 点击圆圈视图
    private WindowManager windowManager;
    private View clickIndicatorView;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
        // 初始化悬浮窗（可视化圆圈）
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        createClickCircle();
    }

    // 核心连点功能（你原版代码，完全没改！）
    public void autoClick(int x, int y) {
        // 新增：在点击位置显示红色圆圈
        showClickCircle(x, y);

        // ========== 你原来的点击逻辑，原样保留 ==========
        Path path = new Path();
        path.moveTo(x, y);
        GestureDescription.Builder builder = new GestureDescription.Builder();
        builder.addStroke(new GestureDescription.StrokeDescription(path, 0, 50));
        dispatchGesture(builder.build(), null, null);
    }

    // ===================== 可视化圆圈功能 =====================
    // 创建圆形指示器
    private void createClickCircle() {
        clickIndicatorView = new View(this);
        // 设置圆圈样式（对应我们新建的XML文件）
        clickIndicatorView.setBackgroundResource(R.drawable.circle_indicator);
        clickIndicatorView.setVisibility(View.GONE);
    }

    // 在坐标(X,Y)显示圆圈，1秒后自动消失
    private void showClickCircle(int x, int y) {
        if (windowManager == null || clickIndicatorView == null) return;

        // 1. 获取 10mm 对应的屏幕像素值，用于精准对齐中心
        int sizeInPx = (int) android.util.TypedValue.applyDimension(
                android.util.TypedValue.COMPLEX_UNIT_MM, 10, getResources().getDisplayMetrics());

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                sizeInPx, sizeInPx, // 强制宽高为 1cm
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                        ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                        : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN // 确保坐标系覆盖全屏
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, // 允许画到屏幕外
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP | Gravity.LEFT;

        // 2. 核心算法：坐标减去半径，实现中心点对齐
        params.x = x - (sizeInPx / 2);
        params.y = y - (sizeInPx / 2);

        try {
            if (clickIndicatorView.getParent() == null) {
                windowManager.addView(clickIndicatorView, params);
            } else {
                windowManager.updateViewLayout(clickIndicatorView, params);
            }
            clickIndicatorView.setVisibility(View.VISIBLE);

            // 建议缩短到 300ms，这样连点时视觉效果更清爽，不会有太多重影
            clickIndicatorView.postDelayed(() -> clickIndicatorView.setVisibility(View.GONE), 300);
        } catch (Exception ignored) {}
    }


    // 服务销毁时清理悬浮窗，防止报错
    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
        if (clickIndicatorView != null && clickIndicatorView.getParent() != null) {
            windowManager.removeView(clickIndicatorView);
        }
    }

    // 你原版的空方法，保留不动
    @Override public void onAccessibilityEvent(AccessibilityEvent event) {}
    @Override public void onInterrupt() {}
}