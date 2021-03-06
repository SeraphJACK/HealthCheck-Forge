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

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public final class Report {

    static final HttpClient client = HttpClientBuilder.create()
            .setDefaultHeaders(Collections.singleton(new BasicHeader("Authorization", Constants.AUTHORIZE_TOKEN)))
            .setConnectionTimeToLive(0, TimeUnit.SECONDS)
            .disableAutomaticRetries()
            .build();
    static Logger logger = LogManager.getLogger();


    public static void onServerStart() {
        HttpResponse response = null;
        try {
            response = client.execute(Requests.serverStartRequest());
        } catch (Exception e) {
            logger.error("Failed to report server start", e);
        } finally {
            HttpClientUtils.closeQuietly(response);
        }

    }

    public static void onServerStop() {
        HttpResponse response = null;
        try {
            response = client.execute(Requests.serverStopRequest());
        } catch (Exception e) {
            logger.error("Failed to report server stop", e);
        } finally {
            HttpClientUtils.closeQuietly(response);
        }
    }

    public static void reportTPS(TPSMonitor.Result result) {
        reportTPS(result, 3);
    }

    public static void reportTPS(TPSMonitor.Result result, int retryCount) {
        HttpResponse response = null;
        try {
            response = client.execute(Requests.tpsRequest(result));
        } catch (Exception e) {
            if (retryCount > 0) {
                reportTPS(result, retryCount - 1);
            } else {
                logger.error("Failed to report server tps", e);
            }
        } finally {
            try {
                HttpClientUtils.closeQuietly(response);
            } catch (Exception e) {
                logger.error("Failed to close response", e);
            }
        }
    }
}
