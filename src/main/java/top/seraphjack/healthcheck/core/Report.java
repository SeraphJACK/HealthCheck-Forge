package top.seraphjack.healthcheck.core;

import org.apache.http.client.HttpClient;
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
            client.execute(Requests.serverStartRequest());
        } catch (IOException e) {
            logger.error("Failed to report server start", e);
        }

    }

    public static void onServerStop() {
        try {
            client.execute(Requests.serverStopRequest());
        } catch (IOException e) {
            logger.error("Failed to report server stop", e);
        }
    }

    public static void reportTPS(TPSMonitor.Result result) {
        try {
            client.execute(Requests.tpsRequest(result));
        } catch (IOException e) {
            logger.error("Failed to report server tps", e);
        }
    }
}
