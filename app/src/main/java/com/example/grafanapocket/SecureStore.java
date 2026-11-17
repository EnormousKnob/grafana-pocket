package com.example.grafanapocket;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import org.json.JSONArray;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

final class SecureStore {
    private static final String KEY_ALIAS = "grafana_pocket_credentials";
    private final SharedPreferences prefs;
    SecureStore(Context c) { prefs = c.getSharedPreferences("connections", Context.MODE_PRIVATE); }

    List<Connection> all() {
        List<Connection> out = new ArrayList<>();
        try {
            JSONArray a = new JSONArray(prefs.getString("items", "[]"));
            for (int i=0;i<a.length();i++) { Connection c=Connection.fromJson(a.getJSONObject(i)); c.secret=decrypt(c.secret); out.add(c); }
        } catch (Exception ignored) { }
        return out;
    }
    Connection get(String id) { for (Connection c:all()) if(c.id.equals(id)) return c; return null; }
    void save(Connection value) {
        List<Connection> list=all(); boolean found=false;
        for(int i=0;i<list.size();i++) if(list.get(i).id.equals(value.id)){list.set(i,value);found=true;break;}
        if(!found) list.add(value);
        JSONArray a=new JSONArray();
        try { for(Connection c:list){String plain=c.secret;c.secret=encrypt(plain);a.put(c.toJson());c.secret=plain;} prefs.edit().putString("items",a.toString()).apply(); }
        catch(Exception e){ throw new IllegalStateException("Unable to protect credentials",e); }
    }
    void delete(String id) { List<Connection> l=all(); l.removeIf(c->c.id.equals(id)); JSONArray a=new JSONArray(); try{for(Connection c:l){String p=c.secret;c.secret=encrypt(p);a.put(c.toJson());c.secret=p;}prefs.edit().putString("items",a.toString()).apply();}catch(Exception ignored){} }

    private SecretKey key() throws Exception {
        KeyStore ks=KeyStore.getInstance("AndroidKeyStore");ks.load(null);
        if(ks.containsAlias(KEY_ALIAS)) return ((KeyStore.SecretKeyEntry)ks.getEntry(KEY_ALIAS,null)).getSecretKey();
        KeyGenerator g=KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore");
        g.init(new KeyGenParameterSpec.Builder(KEY_ALIAS,KeyProperties.PURPOSE_ENCRYPT|KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build());
        return g.generateKey();
    }
    private String encrypt(String s)throws Exception { if(s==null||s.isEmpty())return "";Cipher c=Cipher.getInstance("AES/GCM/NoPadding");c.init(Cipher.ENCRYPT_MODE,key());byte[] data=c.doFinal(s.getBytes(StandardCharsets.UTF_8));return Base64.encodeToString(c.getIV(),Base64.NO_WRAP)+":"+Base64.encodeToString(data,Base64.NO_WRAP); }
    private String decrypt(String s)throws Exception { if(s==null||s.isEmpty())return "";String[] p=s.split(":",2);Cipher c=Cipher.getInstance("AES/GCM/NoPadding");c.init(Cipher.DECRYPT_MODE,key(),new GCMParameterSpec(128,Base64.decode(p[0],Base64.NO_WRAP)));return new String(c.doFinal(Base64.decode(p[1],Base64.NO_WRAP)),StandardCharsets.UTF_8); }
}
