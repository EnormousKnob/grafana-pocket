package com.example.grafanapocket;

import org.json.JSONException;
import org.json.JSONObject;

final class Connection {
    String id, name, url, auth, user, secret, dashboard, panels;
    int refreshSeconds;

    JSONObject toJson() throws JSONException {
        return new JSONObject().put("id", id).put("name", name).put("url", url).put("auth", auth)
                .put("user", user).put("secret", secret).put("dashboard", dashboard).put("panels", panels).put("refresh", refreshSeconds);
    }

    static Connection fromJson(JSONObject j) {
        Connection c = new Connection();
        c.id = j.optString("id"); c.name = j.optString("name"); c.url = j.optString("url");
        c.auth = j.optString("auth", "anonymous"); c.user = j.optString("user"); c.secret = j.optString("secret");
        c.dashboard = j.optString("dashboard", "/"); c.panels = j.optString("panels", ""); c.refreshSeconds = j.optInt("refresh", 30);
        return c;
    }
}
