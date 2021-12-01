package top.seraphjack.healthcheck.core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import top.seraphjack.healthcheck.Constants;
import top.seraphjack.healthcheck.TPSMonitor;

import java.io.UnsupportedEncodingException;

final class Requests {
    private static final String baseUrl;
    private static final Gson gson = new Gson();
    private static final LifecycleRequest SERVER_START = new LifecycleRequest(Constants.SERVER_NAME, "start");
    private static final LifecycleRequest SERVER_STOP = new LifecycleRequest(Constants.SERVER_NAME, "stop");

    static {
        if (Constants.HC_ENDPOINT.endsWith("/")) {
            baseUrl = Constants.HC_ENDPOINT;
        } else {
            baseUrl = Constants.HC_ENDPOINT + "/";
        }
    }

    static HttpPost serverStartRequest() {
        HttpPost r = new HttpPost(baseUrl + "lifecycle");
        try {
            r.setEntity(new StringEntity(gson.toJson(SERVER_START)));
            r.setHeader("Content-Type", "application/json");
        } catch (UnsupportedEncodingException e) {
            // not possible
        }
        return r;
    }

    static HttpPost serverStopRequest() {
        HttpPost r = new HttpPost(baseUrl + "lifecycle");
        try {
            r.setEntity(new StringEntity(gson.toJson(SERVER_STOP)));
            r.setHeader("Content-Type", "application/json");
        } catch (UnsupportedEncodingException e) {
            // not possible
        }
        return r;
    }

    static HttpPost tpsRequest(TPSMonitor.Result result) {
        HttpPost r = new HttpPost(baseUrl + "status");
        JsonObject payload = new JsonObject();
        payload.addProperty("name", Constants.SERVER_NAME);
        payload.addProperty("last1m", result.last1m);
        payload.addProperty("last5m", result.last5m);
        payload.addProperty("last10m", result.last10m);
        payload.addProperty("player_count", result.playerCount);
        try {
            r.setEntity(new StringEntity(gson.toJson(payload)));
        } catch (UnsupportedEncodingException e) {
            // not possible
        }
        return r;
    }

    static class LifecycleRequest {
        final String name, type;

        LifecycleRequest(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }
}
