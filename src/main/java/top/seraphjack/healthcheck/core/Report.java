package top.seraphjack.healthcheck.core;

import top.seraphjack.healthcheck.Constants;
import top.seraphjack.healthcheck.TPSMonitor;

public final class Report {

    public static void onServerStart() {
        System.out.println(Constants.SERVER_NAME + " started.");
    }

    public static void onServerStop() {
        System.out.println(Constants.SERVER_NAME + " stopped.");
    }

    public static void reportTPS(TPSMonitor.Result result) {
        System.out.println(Constants.SERVER_NAME + " TPS report:");
        System.out.printf("Last 1m, 5m, 10m: %.2f, %.2f, %.2f\n", result.last1m, result.last5m, result.last10m);
    }
}
