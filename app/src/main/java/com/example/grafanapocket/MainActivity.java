package com.example.grafanapocket;

import android.app.Activity;import android.content.Intent;import android.os.Bundle;import android.view.*;import android.widget.*;import java.util.*;

public class MainActivity extends Activity {
    LinearLayout list;
    @Override public void onCreate(Bundle b){super.onCreate(b);render();showBackgroundAdvice();}
    @Override protected void onResume(){super.onResume();render();}
    void render(){
        LinearLayout root=Ui.column(this); Ui.fill(root,Ui.title(this,"Grafana Pocket"));
        TextView intro=Ui.title(this,"你的 Grafana");intro.setTextSize(16);Ui.fill(root,intro);
        list=new LinearLayout(this);list.setOrientation(LinearLayout.VERTICAL);Ui.fill(root,list);
        List<Connection> items=new SecureStore(this).all();
        if(items.isEmpty()){TextView empty=Ui.title(this,"尚未添加连接。凭据仅加密保存在本机。");empty.setTextSize(15);Ui.fill(list,empty);}
        for(Connection c:items){Button open=Ui.button(this,c.name+"\n"+c.url);open.setAllCaps(false);open.setOnClickListener(v->startActivity(new Intent(this,DashboardActivity.class).putExtra("id",c.id)));open.setOnLongClickListener(v->{startActivity(new Intent(this,EditConnectionActivity.class).putExtra("id",c.id));return true;});Ui.fill(list,open);}
        Button add=Ui.button(this,"＋ 添加 Grafana");add.setOnClickListener(v->startActivity(new Intent(this,EditConnectionActivity.class)));Ui.fill(root,add);
        TextView help=Ui.title(this,"点击打开；长按编辑。添加桌面小组件请使用系统桌面的“小组件”菜单。");help.setTextSize(13);Ui.fill(root,help);
        ScrollView s=new ScrollView(this);s.addView(root);setContentView(s);
    }
    void showBackgroundAdvice(){android.content.SharedPreferences p=getSharedPreferences("app",MODE_PRIVATE);if(p.getBoolean("background_advice_shown",false))return;p.edit().putBoolean("background_advice_shown",true).apply();new android.app.AlertDialog.Builder(this).setTitle("允许后台运行").setMessage("为了减少系统清理 App 后小组件无法及时更新，可在系统设置中将 Grafana Pocket 设为“不优化”或允许后台运行。不同品牌手机还可能需要在系统管家中允许自启动。即使授权，Android 仍不保证精确定时刷新。").setNegativeButton("稍后",null).setPositiveButton("前往电池设置",(d,w)->{try{startActivity(new android.content.Intent(android.provider.Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS));}catch(Exception ignored){startActivity(new android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,android.net.Uri.parse("package:"+getPackageName())));}}).show();}
}
