package com.example.grafanapocket;

import android.app.Activity;import android.content.Intent;import android.os.Bundle;import android.view.*;import android.widget.*;import java.util.*;

public class MainActivity extends Activity {
    LinearLayout list;int page=0;final UpdateChecker updates=new UpdateChecker();
    @Override public void onCreate(Bundle b){super.onCreate(b);render();showBackgroundAdvice();}
    @Override protected void onResume(){super.onResume();render();updates.check(this,false);}
    void render(){
        LinearLayout root=Ui.column(this);Ui.fill(root,Ui.title(this,"Grafana Pocket"));
        Ui.fill(root,Ui.text(this,"随手掌握你的监控 · "+UpdateChecker.version(this),13,Ui.MUTED));
        LinearLayout nav=new LinearLayout(this);String[] tabs={"我的面板","偏好设置","使用帮助"};
        for(int i=0;i<tabs.length;i++){final int index=i;Button b=Ui.button(this,tabs[i]);b.setTextColor(page==i?Ui.BG:Ui.ACCENT);if(page==i)b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Ui.ACCENT));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-2,1);lp.setMargins(Ui.dp(this,2),Ui.dp(this,8),Ui.dp(this,2),Ui.dp(this,8));nav.addView(b,lp);b.setOnClickListener(v->{page=index;render();});}Ui.fill(root,nav);
        ScrollView scroll=new ScrollView(this);list=new LinearLayout(this);list.setOrientation(LinearLayout.VERTICAL);scroll.addView(list);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        if(page==0){
            List<Connection> items=new SecureStore(this).all();
            LinearLayout overview=Ui.card(list,"我的 Grafana",items.size()+" 个已保存连接 · 凭据仅加密保存在本机");
            action(overview,"＋ 添加连接",()->startActivity(new Intent(this,EditConnectionActivity.class)));
            if(items.isEmpty())Ui.fill(overview,Ui.text(this,"连接已有 Grafana，即可浏览面板并选择关注的数据窗口。",15,Ui.MUTED));
            for(Connection c:items){LinearLayout card=Ui.card(list,c.name,c.url);action(card,"打开面板 →",()->startActivity(new Intent(this,DashboardActivity.class).putExtra("id",c.id)));action(card,"编辑连接",()->startActivity(new Intent(this,EditConnectionActivity.class).putExtra("id",c.id)));}
        }else if(page==1){
            LinearLayout update=Ui.card(list,"应用更新","当前版本 "+UpdateChecker.version(this)+" · GitHub Releases");
            Switch automatic=new Switch(this);automatic.setText("自动检查新版本");automatic.setTextColor(Ui.TEXT);automatic.setMinHeight(Ui.dp(this,48));automatic.setChecked(getSharedPreferences("updates",0).getBoolean("automatic",true));automatic.setOnCheckedChangeListener((b,on)->getSharedPreferences("updates",0).edit().putBoolean("automatic",on).apply());Ui.fill(update,automatic);
            Ui.fill(update,Ui.text(this,"打开首页时检查，每 24 小时最多一次；仅检查正式版本。下载与安装需要你确认。",13,Ui.MUTED));action(update,"立即检查更新",()->updates.check(this,true));
            LinearLayout battery=Ui.card(list,"后台刷新","系统可能因省电策略推迟桌面小组件更新。");action(battery,"打开电池设置",()->{try{startActivity(new Intent(android.provider.Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS));}catch(Exception e){startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,android.net.Uri.parse("package:"+getPackageName())));}});
        }else{
            LinearLayout guide=Ui.card(list,"从面板到桌面","连接 → 选择窗口 → 添加小组件");
            Ui.fill(guide,Ui.text(this,"1. 添加已有 Grafana 地址和登录信息。\n2. 打开面板，通过“选择窗口”选择关注的 Panel。\n3. 在系统桌面的小组件菜单添加 Grafana Pocket，并选择连接与 Panel。",15,Ui.TEXT));
            LinearLayout about=Ui.card(list,"关于本项目","已有 Grafana 的第三方移动端扩展");
            Ui.fill(about,Ui.text(this,"需要已部署的 Grafana、数据源及访问权限。与 Grafana Labs 无隶属关系。\n既有功能仅在三星 One UI 8.5 上测试通过；本次新版界面仍需实机验证。",14,Ui.MUTED));action(about,"GitHub 项目与反馈",()->UpdateChecker.open(this,UpdateChecker.REPO));
        }
        setContentView(root);
    }
    void action(LinearLayout parent,String label,Runnable run){Button button=Ui.button(this,label);button.setOnClickListener(v->run.run());Ui.fill(parent,button);}
    void showBackgroundAdvice(){android.content.SharedPreferences p=getSharedPreferences("app",MODE_PRIVATE);if(p.getBoolean("background_advice_shown",false))return;p.edit().putBoolean("background_advice_shown",true).apply();new android.app.AlertDialog.Builder(this).setTitle("允许后台运行").setMessage("为了减少系统清理 App 后小组件无法及时更新，可在系统设置中将 Grafana Pocket 设为“不优化”或允许后台运行。不同品牌手机还可能需要在系统管家中允许自启动。即使授权，Android 仍不保证精确定时刷新。").setNegativeButton("稍后",null).setPositiveButton("前往电池设置",(d,w)->{try{startActivity(new android.content.Intent(android.provider.Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS));}catch(Exception ignored){startActivity(new android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,android.net.Uri.parse("package:"+getPackageName())));}}).show();}
}
