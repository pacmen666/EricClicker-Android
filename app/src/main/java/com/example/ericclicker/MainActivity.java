package com.example.ericclicker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private boolean isRunning = false; // 记录是否正在连点
    private Handler handler = new Handler(); // 处理定时任务的“小秘书”
    private Runnable clickRunnable; // 具体的点击任务内容

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = findViewById(R.id.btn_click);

        btn.setOnClickListener(v -> {
            // 第一步：检查权限。如果没开，先带你去开。
            if (ClickService.instance == null) {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                return;
            }

            // 第二步：切换开关状态
            if (!isRunning) {
                // --- 开始挂机逻辑 ---
                isRunning = true;
                btn.setText("正在挂机（点击停止）");

                // 定义点击任务
                clickRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (isRunning) {
                            // 使用你测得的坐标：x:2354.5 y:1049.5 (取整)
                            // 加上上下 8 像素的随机偏移，防止被系统识破
                            int finalX = 2354 + (int)(Math.random() * 16 - 8);
                            int finalY = 1049 + (int)(Math.random() * 16 - 8);

                            // 执行点击
                            ClickService.instance.autoClick(finalX, finalY);

                            // 设置下一次点击的延迟：400ms 到 700ms 之间随机
                            int nextDelay = 400 + (int)(Math.random() * 300);
                            handler.postDelayed(this, nextDelay);
                        }
                    }
                };
                // 立即启动第一次点击
                handler.post(clickRunnable);

                // 【可选】一点开始，自动把 App 藏到后台，方便你直接进游戏
                moveTaskToBack(true);

            } else {
                // --- 停止挂机逻辑 ---
                isRunning = false;
                btn.setText("开始挂机");
                // 撤销所有的定时任务，停止点击
                handler.removeCallbacks(clickRunnable);
            }
        });
    }

    // 当 App 彻底关掉时，强制停止点击，防止后台乱点
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning = false;
        if (clickRunnable != null) {
            handler.removeCallbacks(clickRunnable);
        }
    }
}