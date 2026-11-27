package com.example.grafanapocket;

import android.app.*;import android.os.*;import android.text.InputType;import android.widget.*;import java.net.URI;import java.util.UUID;

public class EditConnectionActivity extends Activity {
    EditText name,url,user,secret,path,refresh;Spinner auth;String id;
    @Override public void onCreate(Bundle b){super.onCreate(b);LinearLayout root=Ui.column(this);Ui.fill(root,Ui.title(this,"Grafana 连接"));
        name=Ui.input(this,"名称，例如：家庭监控");url=Ui.input(this,"HTTP/HTTPS 地址，例如：http://192.168.1.10:3000");
        auth=new Spinner(this);auth.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,new String[]{"grafana_login","anonymous","basic","token"}));
        user=Ui.input(this,"Grafana 用户名（或 Basic Auth 用户名）");secret=Ui.input(this,"Grafana 密码、Basic 密码或 Service Account Token");secret.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        path=Ui.input(this,"Dashboard 路径，例如：/d/abc123/home?kiosk");refresh=Ui.input(this,"App 自动刷新秒数（0=由 Grafana 控制）");refresh.setInputType(InputType.TYPE_CLASS_NUMBER);
        LinearLayout server=Ui.card(root,"服务器","连接你已经部署的 Grafana");Ui.fill(server,name);Ui.fill(server,url);LinearLayout login=Ui.card(root,"身份验证","选择与服务器对应的认证方式");Ui.fill(login,auth);Ui.fill(login,user);Ui.fill(login,secret);LinearLayout dashboard=Ui.card(root,"面板与刷新","Dashboard 路径可从浏览器地址栏复制");Ui.fill(dashboard,path);Ui.fill(dashboard,refresh);
        id=getIntent().getStringExtra("id");Connection old=id==null?null:new SecureStore(this).get(id);
        if(old!=null){name.setText(old.name);url.setText(old.url);user.setText(old.user);secret.setText(old.secret);path.setText(old.dashboard);refresh.setText(String.valueOf(old.refreshSeconds));auth.setSelection(Math.max(0,java.util.Arrays.asList("grafana_login","anonymous","basic","token").indexOf(old.auth)));}
        Button save=Ui.button(this,"保存并连接");save.setOnClickListener(v->save());Ui.fill(root,save);
        if(old!=null){Button del=Ui.button(this,"删除连接");del.setOnClickListener(v->new AlertDialog.Builder(this).setMessage("删除此连接？").setNegativeButton("取消",null).setPositiveButton("删除",(d,w)->{new SecureStore(this).delete(id);finish();}).show());Ui.fill(root,del);}
        ScrollView s=new ScrollView(this);s.addView(root);setContentView(s);
    }
    void save(){try{URI u=URI.create(url.getText().toString().trim());String scheme=u.getScheme();if(u.getHost()==null||(!"https".equalsIgnoreCase(scheme)&&!"http".equalsIgnoreCase(scheme)))throw new Exception();}catch(Exception e){url.setError("请输入有效的 HTTP 或 HTTPS 地址");return;}
        if(name.getText().toString().trim().isEmpty()){name.setError("请输入名称");return;}Connection old=id==null?null:new SecureStore(this).get(id);Connection c=new Connection();c.id=id==null?UUID.randomUUID().toString():id;c.name=name.getText().toString().trim();c.url=url.getText().toString().trim().replaceAll("/+$","");c.auth=(String)auth.getSelectedItem();c.user=user.getText().toString();c.secret=secret.getText().toString();c.dashboard=path.getText().toString().trim();c.panels=old==null?"":old.panels;if(c.dashboard.isEmpty())c.dashboard="/";try{c.refreshSeconds=Integer.parseInt(refresh.getText().toString());}catch(Exception e){c.refreshSeconds=30;}new SecureStore(this).save(c);startActivity(new android.content.Intent(this,DashboardActivity.class).putExtra("id",c.id));finish();}
}
