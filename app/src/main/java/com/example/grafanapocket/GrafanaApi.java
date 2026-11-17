package com.example.grafanapocket;

import android.util.Base64;import android.webkit.CookieManager;import org.json.JSONObject;
import java.io.*;import java.net.*;import java.nio.charset.StandardCharsets;import java.util.*;import java.util.concurrent.ConcurrentHashMap;

final class GrafanaApi {
    private static final Map<String,String> nativeSessions=new ConcurrentHashMap<>();

    static void ensureLogin(Connection c)throws Exception {if("grafana_login".equals(c.auth))login(c);}

    private static void login(Connection c)throws Exception {
        HttpURLConnection h=open(c,c.url+"/login");h.setRequestMethod("POST");h.setDoOutput(true);h.setInstanceFollowRedirects(false);h.setRequestProperty("Content-Type","application/json");
        byte[] body=new JSONObject().put("user",c.user).put("password",c.secret).toString().getBytes(StandardCharsets.UTF_8);h.setFixedLengthStreamingMode(body.length);try(OutputStream o=h.getOutputStream()){o.write(body);}int code=h.getResponseCode();
        List<String> cookies=new ArrayList<>();for(Map.Entry<String,List<String>> e:h.getHeaderFields().entrySet())if(e.getKey()!=null&&"Set-Cookie".equalsIgnoreCase(e.getKey()))for(String raw:e.getValue()){CookieManager.getInstance().setCookie(c.url,raw);String pair=raw.split(";",2)[0].trim();if(!pair.isEmpty())cookies.add(pair);}
        CookieManager.getInstance().flush();if(!cookies.isEmpty())nativeSessions.put(c.url,String.join("; ",cookies));
        if(code/100!=2&&code/100!=3)throw new IOException("Grafana 登录失败（HTTP "+code+"）");if(cookies.isEmpty())throw new IOException("Grafana 登录响应没有会话 Cookie");
    }

    static String get(Connection c,String path)throws Exception {
        ensureLogin(c);HttpURLConnection h=request(c,path);int code=h.getResponseCode();
        if(code==401&&(hasPassword(c))){h.disconnect();login(c);h=request(c,path);code=h.getResponseCode();}
        if(code/100!=2)throw new IOException("读取 Dashboard 失败（HTTP "+code+"）");try(InputStream in=h.getInputStream()){return new String(in.readAllBytes(),StandardCharsets.UTF_8);}
    }
    static String post(Connection c,String path,JSONObject body)throws Exception {ensureLogin(c);HttpURLConnection h=open(c,c.url+path);h.setRequestMethod("POST");h.setDoOutput(true);h.setRequestProperty("Content-Type","application/json");h.setRequestProperty("Accept","application/json");applyAuth(h,c);byte[] bytes=body.toString().getBytes(StandardCharsets.UTF_8);h.setFixedLengthStreamingMode(bytes.length);try(OutputStream out=h.getOutputStream()){out.write(bytes);}int code=h.getResponseCode();if(code==401&&hasPassword(c)){h.disconnect();login(c);h=open(c,c.url+path);h.setRequestMethod("POST");h.setDoOutput(true);h.setRequestProperty("Content-Type","application/json");applyAuth(h,c);h.setFixedLengthStreamingMode(bytes.length);try(OutputStream out=h.getOutputStream()){out.write(bytes);}code=h.getResponseCode();}if(code/100!=2)throw new IOException("查询 Panel 数据失败（HTTP "+code+"）");try(InputStream in=h.getInputStream()){return new String(in.readAllBytes(),StandardCharsets.UTF_8);}}
    private static HttpURLConnection request(Connection c,String path)throws Exception {HttpURLConnection h=open(c,c.url+path);h.setRequestProperty("Accept","application/json");applyAuth(h,c);return h;}
    private static HttpURLConnection open(Connection c,String url)throws Exception {HttpURLConnection h=(HttpURLConnection)new URL(url).openConnection();h.setConnectTimeout(15000);h.setReadTimeout(20000);return h;}
    private static boolean hasPassword(Connection c){return c.user!=null&&!c.user.isEmpty()&&c.secret!=null&&!c.secret.isEmpty()&&!"token".equals(c.auth)&&!"anonymous".equals(c.auth);}

    static void applyAuth(HttpURLConnection h,Connection c){
        if("basic".equals(c.auth))h.setRequestProperty("Authorization","Basic "+Base64.encodeToString((c.user+":"+c.secret).getBytes(StandardCharsets.UTF_8),Base64.NO_WRAP));else if("token".equals(c.auth))h.setRequestProperty("Authorization","Bearer "+c.secret);
        String nativeCookie=nativeSessions.get(c.url),webCookie=CookieManager.getInstance().getCookie(c.url);String cookie=nativeCookie!=null?nativeCookie:webCookie;if(cookie!=null&&!cookie.isEmpty())h.setRequestProperty("Cookie",cookie);
    }
    static String uid(String dashboardPath){try{String p=URI.create(dashboardPath).getPath();String[] parts=p.split("/");for(int i=0;i<parts.length-1;i++)if("d".equals(parts[i])||"d-solo".equals(parts[i]))return parts[i+1];}catch(Exception ignored){}return "";}
}
