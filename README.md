# Grafana Pocket

一个面向自托管 Grafana 的原生 Android 客户端，让你可以在手机 App 和桌面小组件中查看自己的 Dashboard 与 Panel。

> [!IMPORTANT]
> 当前版本仅在三星手机的 **One UI 8.5** 系统上完成实际测试。其他品牌、系统版本和桌面启动器可能存在显示、后台刷新或电池策略差异，欢迎提交 Issue 反馈。

## 功能

- 管理多个远程 Grafana 实例
- 支持 HTTP 与 HTTPS
- 支持 Grafana 账号登录、匿名访问、Basic Auth 和 Service Account Token
- 使用 Android Keystore（AES-GCM）加密本地凭据
- 在 App 内显示完整 Dashboard，兼容 Grafana Web UI 和已安装的面板插件
- 自动读取 Dashboard 中的 Panel，可选择一个或多个窗口单独展示
- 支持 App 内手动刷新和定时刷新
- 支持可缩放桌面小组件，默认 2×2，最小约 2×1
- 点击小组件可跳转至对应 Dashboard
- Stat 小组件可在后台查询 Grafana 数据并原生绘制数值、阈值颜色和趋势线
- 小组件右上角可无界面手动刷新
- 使用 JobScheduler 尝试每 15 分钟自动刷新，实际执行时间由 Android 省电策略决定

## 使用说明

1. 在首页添加 Grafana 连接。
2. 填写服务器地址、认证方式、Dashboard 路径和刷新间隔。
3. Dashboard 路径建议使用 `/d/UID/名称` 格式。
4. 进入 Dashboard 后，可通过“选择窗口”勾选需要在 App 内单独显示的 Panel。
5. 从系统桌面的“小组件”菜单添加 Grafana Pocket 小组件，并选择已保存的远程主机和 Panel。

### 认证方式

- `grafana_login`：使用 Grafana 用户名和密码建立会话，适合普通登录页面。
- `anonymous`：Grafana 已开启匿名只读访问时使用。
- `basic`：用于反向代理提供的 HTTP Basic Auth。
- `token`：使用 Grafana Service Account Token。

## 小组件说明

Stat 类型 Panel 可通过 Grafana `/api/ds/query` 在后台获取数据，再由 Android Canvas 绘制数值、阈值颜色和趋势线。手动刷新不会打开 App。

复杂 Panel、第三方面板插件或暂未支持的转换逻辑无法保证与 Grafana 完全一致；此类 Panel 会保留最后一次成功生成的本地快照。Android 可能因为省电策略延迟后台任务，建议允许应用后台运行，并在部分厂商系统中允许自启动。

## 构建

环境要求：

- Android Studio
- JDK 17
- Android SDK 36
- Gradle 8.9（项目已包含 Gradle Wrapper）

调试构建：

```bash
./gradlew assembleDebug
```

Windows：

```powershell
.\gradlew.bat assembleDebug
```

生产发布前请修改 `applicationId`、配置正式签名，并准备 Google Play 所需的隐私政策、数据安全表单与商店素材。

## 安全说明

- Grafana 密码和 Token 使用 Android Keystore AES-GCM 加密保存。
- 应用不申请存储、定位或通知等与核心功能无关的权限。
- HTTP 会明文传输账号、Token 和监控数据，仅建议在可信内网中使用；公网环境应使用有效证书的 HTTPS。
- 为完成 Dashboard 展示，App 内 WebView 会启用 JavaScript 和 DOM Storage。

## 当前版本

`1.5.1`（versionCode 11）

## 支持项目

如果 Grafana Pocket 恰好帮你节省了配置时间，或让日常监控更顺手，欢迎用下面任意一种方式请作者喝杯咖啡。你的支持会成为继续完善兼容性和小组件体验的动力，量力而行即可，谢谢！

<table>
  <tr>
    <th>支付宝</th>
    <th>微信支付</th>
  </tr>
  <tr>
    <td><img src="docs/assets/alipay.jpg" alt="支付宝收款码" width="320"></td>
    <td><img src="docs/assets/wechat-pay.jpg" alt="微信支付收款码" width="320"></td>
  </tr>
</table>

