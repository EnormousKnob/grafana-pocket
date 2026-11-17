package com.example.grafanapocket;

import android.app.job.*;import android.appwidget.AppWidgetManager;import android.content.*;import android.content.ComponentName;

public class WidgetUpdateJobService extends JobService {
    static final int JOB_ID=48150;
    static void schedule(Context c){JobInfo info=new JobInfo.Builder(JOB_ID,new ComponentName(c,WidgetUpdateJobService.class)).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).setPeriodic(15*60*1000L).setPersisted(true).build();((JobScheduler)c.getSystemService(JOB_SCHEDULER_SERVICE)).schedule(info);}
    @Override public boolean onStartJob(JobParameters params){new Thread(()->{AppWidgetManager m=AppWidgetManager.getInstance(this);int[] ids=m.getAppWidgetIds(new ComponentName(this,GrafanaWidgetProvider.class));for(int id:ids)GrafanaWidgetProvider.refreshNow(this,m,id);jobFinished(params,false);}).start();return true;}
    @Override public boolean onStopJob(JobParameters params){return true;}
}
