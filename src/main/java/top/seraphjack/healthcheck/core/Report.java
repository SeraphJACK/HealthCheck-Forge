package top.seraphjack.healthcheck.core;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.seraphjack.healthcheck.Constants;
import top.seraphjack.healthcheck.TPSMonitor;

import java.io.IOException;
import java.util.Collections;

public final class Report {

    static final HttpClient client = HttpClientBuilder.create()
            .setDefaultHeaders(Collections.singleton(new BasicHeader("Authorization", Constants.AUTHORIZE_TOKEN)))
            .build();
    static Logger logger = LogManager.getLogger();


    public static void onServerStart() {
        try {
            HttpResponse response = client.execute(Requests.serverStartRequest());
            HttpClientUtils.closeQuietly(response);
        } catch (IOException e) {
            logger.error("Failed to report server start", e);
        }

    }

    public static void onServerStop() {
        try {
            HttpResponse response = client.execute(Requests.serverStopRequest());
            HttpClientUtils.closeQuietly(response);
        } catch (IOException e) {
            logger.error("Failed to report server stop", e);
        }
    }

    public static void reportTPS(TPSMonitor.Result result) {
        try {
            HttpResponse response = client.execute(Requests.tpsRequest(result));
            HttpClientUtils.closeQuietly(response);
        } catch (IOException e) {
            logger.error("Failed to report server tps", e);
        }
    }
}
