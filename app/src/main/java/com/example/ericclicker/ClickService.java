package com.example.ericclicker;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.view.accessibility.AccessibilityEvent;

public class ClickService extends AccessibilityService {
    public static ClickService instance;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instance = this; // 服务启动时保存引用
    }

    // 核心连点功能：传入X, Y坐标
    public void autoClick(int x, int y) {
        Path path = new Path();
        path.moveTo(x, y);
        GestureDescription.Builder builder = new GestureDescription.Builder();
        // 模拟点击：按下0毫秒开始，持续50毫秒
        builder.addStroke(new GestureDescription.StrokeDescription(path, 0, 50));
        dispatchGesture(builder.build(), null, null);
    }

    @Override public void onAccessibilityEvent(AccessibilityEvent event) {}
    @Override public void onInterrupt() {}
}