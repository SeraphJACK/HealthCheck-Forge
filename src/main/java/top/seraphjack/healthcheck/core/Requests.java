package top.seraphjack.healthcheck.core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import top.seraphjack.healthcheck.Constants;
import top.seraphjack.healthcheck.TPSMonitor;

import java.net.URI;
import java.net.http.HttpRequest;

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

    static HttpRequest serverStartRequest() {
        return HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "lifecycle"))
                .header("Content-Type", "application/json")
                .header("Authorization", Constants.AUTHORIZE_TOKEN)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(SERVER_START)))
                .build();
    }

    static HttpRequest serverStopRequest() {
        return HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "lifecycle"))
                .header("Content-Type", "application/json")
                .header("Authorization", Constants.AUTHORIZE_TOKEN)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(SERVER_STOP)))
                .build();
    }

    static HttpRequest tpsRequest(TPSMonitor.Result result) {
        JsonObject payload = new JsonObject();
        payload.addProperty("name", Constants.SERVER_NAME);
        payload.addProperty("last1m", result.last1m);
        payload.addProperty("last5m", result.last5m);
        payload.addProperty("last10m", result.last10m);
        payload.addProperty("player_count", result.playerCount);
        return HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "status"))
                .header("Content-Type", "application/json")
                .header("Authorization", Constants.AUTHORIZE_TOKEN)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(payload)))
                .build();
    }

    static class LifecycleRequest {
        final String name, type;

        LifecycleRequest(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }
}
