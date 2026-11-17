package com.example.grafanapocket;

import android.app.*;import android.appwidget.*;import android.content.*;import android.graphics.Bitmap;import android.view.View;import android.widget.RemoteViews;

public class GrafanaWidgetProvider extends AppWidgetProvider {
    static final String ACTION_REFRESH="com.example.grafanapocket.WIDGET_REFRESH";
    @Override public void onEnabled(Context c){WidgetUpdateJobService.schedule(c);}
    @Override public void onUpdate(Context c,AppWidgetManager m,int[] ids){WidgetUpdateJobService.schedule(c);for(int id:ids)refreshAsync(c,m,id,null);}
    @Override public void onReceive(Context c,Intent intent){if(ACTION_REFRESH.equals(intent.getAction())){PendingResult pending=goAsync();int id=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,-1);showRefreshing(c,AppWidgetManager.getInstance(c),id);refreshAsync(c,AppWidgetManager.getInstance(c),id,pending);return;}super.onReceive(c,intent);}
    @Override public void onDeleted(Context c,int[] ids){android.content.SharedPreferences.Editor e=c.getSharedPreferences("widgets",Context.MODE_PRIVATE).edit();for(int id:ids){e.remove("connection_"+id).remove("path_"+id).remove("panel_"+id).remove("from_"+id).remove("to_"+id).remove("updated_"+id);WidgetCache.delete(c,id);}e.apply();}
    static void update(Context c,AppWidgetManager m,int id){showCached(c,m,id,"正在后台获取数据…");refreshAsync(c,m,id,null);}
    static void refreshAsync(Context c,AppWidgetManager m,int id,PendingResult pending){new Thread(()->{refreshNow(c,m,id);if(pending!=null)pending.finish();}).start();}
    static void refreshNow(Context c,AppWidgetManager m,int id){try{NativeWidgetUpdater.update(c,id);showCached(c,m,id,null);}catch(Exception e){showCached(c,m,id,"刷新失败："+e.getMessage());}}
    static void showRefreshing(Context c,AppWidgetManager manager,int id){RemoteViews rv=base(c,id);Bitmap bitmap=WidgetCache.load(c,id);if(bitmap!=null)rv.setImageViewBitmap(R.id.widget_image,bitmap);rv.setViewVisibility(R.id.widget_status,View.VISIBLE);rv.setTextViewText(R.id.widget_status,"正在刷新…");rv.setFloat(R.id.widget_refresh,"setAlpha",0.42f);rv.setBoolean(R.id.widget_refresh,"setEnabled",false);manager.updateAppWidget(id,rv);}
    static RemoteViews base(Context c,int id){RemoteViews rv=new RemoteViews(c.getPackageName(),R.layout.widget_grafana);rv.setFloat(R.id.widget_refresh,"setAlpha",0.86f);rv.setBoolean(R.id.widget_refresh,"setEnabled",true);Intent refresh=new Intent(c,GrafanaWidgetProvider.class).setAction(ACTION_REFRESH).putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,id);rv.setOnClickPendingIntent(R.id.widget_refresh,PendingIntent.getBroadcast(c,100000+id,refresh,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE));return rv;}
    static void showCached(Context c,AppWidgetManager manager,int id,String message){if(id<0)return;RemoteViews rv=base(c,id);Bitmap bitmap=WidgetCache.load(c,id);if(bitmap!=null){rv.setImageViewBitmap(R.id.widget_image,bitmap);rv.setViewVisibility(R.id.widget_status,message==null?View.GONE:View.VISIBLE);if(message!=null)rv.setTextViewText(R.id.widget_status,message);}else{rv.setViewVisibility(R.id.widget_status,View.VISIBLE);rv.setTextViewText(R.id.widget_status,message==null?"等待后台刷新":message);}
        android.content.SharedPreferences p=c.getSharedPreferences("widgets",Context.MODE_PRIVATE);Connection x=new SecureStore(c).get(p.getString("connection_"+id,""));if(x!=null){Intent open=new Intent(c,DashboardActivity.class).putExtra("id",x.id).putExtra("path",p.getString("path_"+id,x.dashboard));rv.setOnClickPendingIntent(R.id.widget_image,PendingIntent.getActivity(c,id,open,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE));}
        manager.updateAppWidget(id,rv);}
}
