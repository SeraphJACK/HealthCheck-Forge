package top.seraphjack.healthcheck.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.seraphjack.healthcheck.TPSMonitor;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

public final class Report {

    static final HttpClient client = HttpClient
            .newBuilder()
            .build();
    static Logger logger = LogManager.getLogger();

    public static void onServerStart() {
        try {
            client.send(Requests.serverStartRequest(), HttpResponse.BodyHandlers.discarding());
        } catch (IOException | InterruptedException e) {
            logger.error("Failed to report server start", e);
        }
    }

    public static void onServerStop() {
        try {
            client.send(Requests.serverStopRequest(), HttpResponse.BodyHandlers.discarding());
        } catch (IOException | InterruptedException e) {
            logger.error("Failed to report server stop", e);
        }
    }

    public static void reportTPS(TPSMonitor.Result result) {
        reportTPS(result, 3);
    }

    public static void reportTPS(TPSMonitor.Result result, int retryCount) {
        try {
            client.send(Requests.tpsRequest(result), HttpResponse.BodyHandlers.discarding());
        } catch (IOException | InterruptedException e) {
            if (retryCount > 0) {
                reportTPS(result, retryCount - 1);
            } else {
                logger.error("Failed to report tps", e);
            }
        }
    }
}
