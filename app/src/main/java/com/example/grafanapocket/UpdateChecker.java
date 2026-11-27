package com.example.grafanapocket;

import android.app.*;
import android.content.*;
import android.net.Uri;
import android.widget.Toast;
import org.json.*;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

/** Public GitHub metadata only; Grafana credentials never enter this request. */
final class UpdateChecker {
 static final String REPO="https://github.com/EnormousKnob/grafana-pocket";
 private boolean busy;
 static String version(Context c){try{return c.getPackageManager().getPackageInfo(c.getPackageName(),0).versionName;}catch(Exception e){return "0.0.0";}}
 static int compare(String a,String b){
  a=a.replaceFirst("^v","");b=b.replaceFirst("^v","");
  if(!a.matches("[0-9]{1,8}\\.[0-9]{1,8}\\.[0-9]{1,8}")||!b.matches("[0-9]{1,8}\\.[0-9]{1,8}\\.[0-9]{1,8}"))throw new IllegalArgumentException("Invalid version");
  String[] x=a.split("\\."),y=b.split("\\.");
  for(int i=0;i<3;i++){int d=Integer.compare(Integer.parseInt(x[i]),Integer.parseInt(y[i]));if(d!=0)return d;}return 0;
 }
 void check(Activity a,boolean manual){
  SharedPreferences p=a.getSharedPreferences("updates",0);
  if(busy)return;
  long now=System.currentTimeMillis();
  if(!manual&&(!p.getBoolean("automatic",true)||now-p.getLong("last_attempt",0)<86400000L))return;
  busy=true;p.edit().putLong("last_attempt",now).apply();
  if(manual)Toast.makeText(a,"正在检查 GitHub 更新…",Toast.LENGTH_SHORT).show();
  new Thread(()->{
   String error=null;JSONObject release=null;HttpURLConnection conn=null;
   try{
    conn=(HttpURLConnection)new URL("https://api.github.com/repos/EnormousKnob/grafana-pocket/releases/latest").openConnection();
    conn.setConnectTimeout(15000);conn.setReadTimeout(15000);
    conn.setRequestProperty("Accept","application/vnd.github+json");conn.setRequestProperty("User-Agent","GrafanaPocket/"+version(a));
    int code=conn.getResponseCode();
    if(code==404)throw new IOException("尚无正式发布版本");
    if(code==403||code==429)throw new IOException("GitHub 请求受限，请稍后重试");
    if(code!=200)throw new IOException("GitHub 返回 HTTP "+code);
    try(InputStream in=conn.getInputStream();ByteArrayOutputStream out=new ByteArrayOutputStream()){
     byte[] buf=new byte[8192];int n;
     while((n=in.read(buf))!=-1){if(out.size()+n>1048576)throw new IOException("更新信息过大");out.write(buf,0,n);}
     release=new JSONObject(out.toString(StandardCharsets.UTF_8.name()));
    }
    if(release.optBoolean("draft")||release.optBoolean("prerelease"))throw new IOException("暂无正式版本");
    compare(release.getString("tag_name"),version(a));
   }catch(Exception e){error=e instanceof IOException?e.getMessage():"更新信息格式不正确";}
   finally{if(conn!=null)conn.disconnect();}
   final JSONObject result=release;final String failure=error;
   a.runOnUiThread(()->{
    busy=false;if(a.isFinishing()||a.isDestroyed())return;
    if(failure!=null){if(manual) new AlertDialog.Builder(a).setTitle("检查更新失败").setMessage(failure+"。可稍后重试或打开项目发布页。").setPositiveButton("发布页",(d,w)->open(a,REPO+"/releases")).setNegativeButton("关闭",null).show();return;}
    String tag=result.optString("tag_name");
    if(compare(tag,version(a))<=0){if(manual)Toast.makeText(a,"当前已是最新版本",Toast.LENGTH_LONG).show();return;}
    String download=null;JSONArray assets=result.optJSONArray("assets");
    if(assets!=null)for(int i=0;i<assets.length();i++){JSONObject asset=assets.optJSONObject(i);if(asset==null)continue;String u=asset.optString("browser_download_url");if(asset.optString("name").equals("Grafana-Pocket-"+tag+"-debug.apk")&&u.startsWith(REPO+"/releases/download/")&&u.endsWith(".apk")&&"uploaded".equals(asset.optString("state")))download=u;}
    final String target=download==null?REPO+"/releases":download;
    String notes=result.optString("body","");if(notes.length()>2500)notes=notes.substring(0,2500)+"…";
    new AlertDialog.Builder(a).setTitle("发现新版本 "+tag).setMessage("当前版本 "+version(a)+"\n\n"+notes+"\n\n确认后由浏览器打开 GitHub 下载，完成后手动安装。").setNegativeButton("稍后",null).setPositiveButton(download==null?"查看发布页":"下载 APK",(d,w)->open(a,target)).show();
   });
  },"github-update-check").start();
 }
 static void open(Context c,String url){try{c.startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url)));}catch(ActivityNotFoundException e){Toast.makeText(c,"请安装浏览器后重试",Toast.LENGTH_LONG).show();}}
}
