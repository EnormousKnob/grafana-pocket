# Grafana Pocket

**Grafana Pocket** 是一个面向自托管 Grafana 的原生 Android 客户端与桌面小组件（Home Screen Widget），让你可以在安卓手机上查看 Grafana Dashboard、单独展示 Panel，并在桌面监控关键指标。

Grafana Pocket is an Android Grafana client, dashboard viewer, and home screen widget for self-hosted Grafana instances. It supports multiple servers, panel selection, Stat widgets, background refresh, and secure local credential storage.

[下载 APK](https://github.com/EnormousKnob/grafana-pocket/releases/tag/v1.6.0) · [查看 Releases](https://github.com/EnormousKnob/grafana-pocket/releases) · [提交问题](https://github.com/EnormousKnob/grafana-pocket/issues)

> [!IMPORTANT]
> 当前版本仅在三星手机的 **One UI 8.5** 系统上完成实际测试。其他品牌、系统版本和桌面启动器可能存在显示、后台刷新或电池策略差异，欢迎提交 Issue 反馈。

## 项目定位与使用边界

Grafana Pocket **仅是对用户已有 Grafana 部署的第三方移动端查看与桌面小组件扩展**。使用本 App 前，用户必须已经自行部署并配置好可访问的 Grafana 服务、Dashboard、Panel、数据源及相应访问权限。

- 本项目不包含、不部署、不托管 Grafana 服务端，也不负责创建或维护监控数据源。
- 本项目不具备独立采集、存储或处理监控指标的能力，不能替代 Grafana、Prometheus 或其他可观测性后端。
- App 仅代表用户连接其指定的 Grafana 实例，通过 Grafana Web 界面及 HTTP API 读取并展示用户已有的 Dashboard、Panel 和查询结果；部分内容可能在设备本地形成加密配置或小组件快照缓存。
- 实际可用功能取决于用户的 Grafana 版本、认证方式、数据源、Panel 类型、插件、网络环境和服务器权限。第三方插件或复杂转换不保证能够在桌面小组件中完整复现。
- Grafana Pocket 是独立的社区项目，与 Grafana Labs 无隶属、授权或背书关系；Grafana 名称及相关商标归其权利人所有。

## 实机效果

三星手机 One UI 8.5 桌面上的 Grafana Pocket 小组件，展示已有 Grafana Panel 的数值与趋势线。图中数据为截图时的状态，实际显示取决于所连接的面板与刷新结果。

<p align="center">
  <a href="docs/assets/samsung-one-ui-8-5-widget.jpg">
    <img src="docs/assets/samsung-one-ui-8-5-widget.jpg" alt="Grafana Pocket 在三星 One UI 8.5 桌面显示当前 PC 数量及趋势线的实机截图" width="360">
  </a>
</p>

## Grafana Android 客户端功能

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

## 适用场景

- 在 Android 手机上查看家庭服务器、NAS、软路由或云主机的 Grafana 监控面板
- 将 CPU、内存、磁盘、网络流量、温度等 Grafana Stat 指标放到手机桌面
- 同时管理多个自托管 Grafana 实例
- 从大型 Dashboard 中选择一个或多个关键 Panel 进行移动端展示
- 使用可缩放的 Grafana Android Widget 快速查看实时监控数据

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

## Grafana 桌面小组件

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

## 安全说明

- Grafana 密码和 Token 使用 Android Keystore AES-GCM 加密保存。
- 应用不申请存储、定位或通知等与核心功能无关的权限。
- HTTP 会明文传输账号、Token 和监控数据，仅建议在可信内网中使用；公网环境应使用有效证书的 HTTPS。
- 为完成 Dashboard 展示，App 内 WebView 会启用 JavaScript 和 DOM Storage。

## 当前版本

`1.6.0`（versionCode 12）

## 1.6.0 界面与更新

- 参考 [双卡网络磁贴](https://github.com/EnormousKnob/dual-sim-network-tiles) 的布局语言，采用深蓝背景、圆角卡片、青色强调色，以及面板 / 设置 / 帮助导航。
- 连接编辑页按服务器、身份验证、面板与刷新分组；公共按钮、输入框和间距统一。
- 默认在打开首页时自动检查更新，每 24 小时最多一次，可在「偏好设置」关闭或手动检查。
- 更新源固定为本仓库 GitHub Releases，使用公开 API，仅识别正式版本及三段式版本号（如 v1.6.0），不会上传 Grafana 地址或凭据。
- 发现新版本后展示发布说明，由用户确认后通过浏览器下载 APK，再手动安装；不会静默安装。网络不可用或 GitHub 限流时可稍后重试。
- GitHub APK 延续此前 debug 签名以支持覆盖安装；这不是 Google Play 发布包。后续 Release 请保持签名及附件命名 Grafana-Pocket-v版本号-debug.apk。
- 既有功能的实机测试范围为三星 One UI 8.5；本次 UI 和更新流程尚待真机验收。

## 相关关键词

Grafana Android、Grafana App、Grafana Client、Grafana Dashboard Viewer、Grafana Widget、Android Dashboard、Android Home Screen Widget、自托管 Grafana、服务器监控、NAS 监控、家庭服务器监控、Prometheus Dashboard、Observability、Monitoring Widget。

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

