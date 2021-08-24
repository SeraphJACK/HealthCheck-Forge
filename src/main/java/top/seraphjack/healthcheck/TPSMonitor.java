package top.seraphjack.healthcheck;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.minecraft.server.MinecraftServer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class TPSMonitor {
    private long previousTickCount, previousTime;
    private MinecraftServer server;
    private final double[] result = new double[10];
    private int minute = 0;

    private final ScheduledExecutorService executor;

    public TPSMonitor(MinecraftServer server) {
        this.server = server;
        this.previousTickCount = server.getTickCount();
        this.previousTime = System.currentTimeMillis();

        this.executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder()
                .setNameFormat("TPS-Monitor-Worker-%d").build());

        this.executor.scheduleAtFixedRate(this::harvest, 1, 1, TimeUnit.MINUTES);
    }

    public void shutdown() {
        this.executor.shutdown();
        this.server = null;
    }

    public Result fetch() {
        double a, b = 0, c = 0;
        int min = minute - 1;
        a = result[min];
        if (min >= 5) {
            for (int i = 0; i < 5; ++i) {
                b += result[(min - i) % 10];
            }
        } else {
            b = Double.NaN;
        }
        if (min >= 10) {
            for (int i = 0; i < 10; ++i) {
                c += result[(min - i) % 10];
            }
        } else {
            c = Double.NaN;
        }
        return new Result(a, b / 5, c / 10);
    }

    // should be called every minute
    private void harvest() {
        result[minute++ % 10] = getAverageTPS();
    }

    private double getAverageTPS() {
        long dt = System.currentTimeMillis() - previousTime;
        long dc = server.getTickCount() - previousTickCount;

        previousTime = System.currentTimeMillis();
        previousTickCount = server.getTickCount();

        return 1.0 * dc / (dt / 1000.0);
    }

    public static class Result {
        public final double last1m, last5m, last10m;

        Result(double a, double b, double c) {
            last1m = a;
            if (Double.isNaN(b)) b = a;
            if (Double.isNaN(c)) c = b;
            last5m = b;
            last10m = c;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "last1m=" + last1m +
                    ", last5m=" + last5m +
                    ", last10m=" + last10m +
                    '}';
        }
    }
}
