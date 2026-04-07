package com.example.ericclicker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private boolean isRunning = false;
    private Handler handler = new Handler();

    // 定义三个独立的任务闹钟
    private Runnable task13s, task18s, task15s;
    private ScreenStateReceiver screenReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = findViewById(R.id.btn_click);

        // 1. 注册屏幕监听（为了实现“黑屏即停”的骚操作）
        screenReceiver = new ScreenStateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(screenReceiver, filter);

        btn.setOnClickListener(v -> {
            // A. 检查无障碍权限
            if (ClickService.instance == null) {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                return;
            }

            // B. 检查悬浮窗权限（1.1版本显示红色圆圈必须开启）
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "请开启悬浮窗权限以显示点击圆圈", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                    return;
                }
            }

            // C. 开关逻辑
            if (!isRunning) {
                isRunning = true;
                btn.setText("挂机中·黑屏即停止");
                startAllTasks(); // 开启三个任务
                moveTaskToBack(true); // 自动隐藏 App，直接看游戏
            } else {
                stopAllTasks(); // 停止所有任务
                btn.setText("开始挂机");
            }
        });
    }

    // --- 核心：启动三个独立的点击任务 ---
    private void startAllTasks() {
        // 任务 1：13秒一次，坐标 (1351, 874)
        task13s = new Runnable() {
            @Override
            public void run() {
                if (isRunning && ClickService.instance != null) {
                    ClickService.instance.autoClick(1351, 874);
                    handler.postDelayed(this, 13000);
                }
            }
        };

        // 任务 2：18秒一次，坐标 (1518, 1050)
        task18s = new Runnable() {
            @Override
            public void run() {
                if (isRunning && ClickService.instance != null) {
                    ClickService.instance.autoClick(1518, 1050);
                    handler.postDelayed(this, 18000);
                }
            }
        };

        // 任务 3：15秒一次，坐标 (2311, 1118)
        task15s = new Runnable() {
            @Override
            public void run() {
                if (isRunning && ClickService.instance != null) {
                    ClickService.instance.autoClick(2311, 1118);
                    handler.postDelayed(this, 15000);
                }
            }
        };

        // 启动这三个闹钟
        handler.post(task13s);
        handler.post(task18s);
        handler.post(task15s);
    }

    // --- 核心：一键停止所有闹钟 ---
    private void stopAllTasks() {
        isRunning = false;
        if (task13s != null) handler.removeCallbacks(task13s);
        if (task18s != null) handler.removeCallbacks(task18s);
        if (task15s != null) handler.removeCallbacks(task15s);
    }

    // 屏幕状态监听器
    private class ScreenStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                stopAllTasks(); // 熄屏立刻停止，安全第一
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 每次回到 App 界面都自动重置状态，防止乱点
        stopAllTasks();
        Button btn = findViewById(R.id.btn_click);
        btn.setText("开始挂机");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAllTasks();
        if (screenReceiver != null) {
            unregisterReceiver(screenReceiver);
        }
    }
}