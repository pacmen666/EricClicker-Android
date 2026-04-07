# Eric Clicker (v1.1)
一款专为 Android 设计的隐私保护型自动化连点工具。

## 项目背景
本项目旨在探索 Android **Accessibility Service (无障碍服务)** 的底层应用，解决在非 Root 环境下进行跨应用模拟点击的挑战。目前已成功适配 **小米澎湃 OS (HyperOS)** 及其安全保护机制。

## 核心功能
- **自动化点击**：通过坐标映射实现高频模拟点击。
- **反检测机制**：内置随机坐标偏移与变频点击算法，模拟真人操作。
- **隐私至上**：零网络权限申请，物理隔绝数据泄露风险。

## 技术栈
- **开发语言**：Java
- **环境适配**：Android SDK (API 24+), MacBook Pro M2 编译
- **核心组件**：AccessibilityService, Handler, Git 版本控制

## 如何使用 (小米/红米设备)
1. 下载 APK 并安装。
2. 在“应用信息”中开启“允许受限设置”。
3. 在“无障碍”设置中激活 Eric Clicker。
4. (可选) 开启开发者选项中的“USB调试（安全设置）”以穿透系统保护。

# Eric Clicker (v1.1) - Now Available! 🚀

The v1.1 update brings a more intuitive and powerful automation experience.

### 🌟 New Features
- **Visual Feedback**: A real-time red indicator circle now marks exactly where each click occurs, providing instant visual confirmation.
- **Multi-Point Scheduling**: Supports multiple click targets with independent, high-precision timing (e.g., 13s, 15s, and 18s intervals).
- **Smart Shutdown**: Enhanced security with the "Screen-Off-to-Stop" feature. Simply lock your phone to instantly terminate all running tasks.

### 🛠 Improvements
- Optimized coordinate mapping for high-resolution displays.
- Lightweight overlay implementation for minimal battery impact.
