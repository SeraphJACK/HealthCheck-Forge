package top.seraphjack.healthcheck;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import top.seraphjack.healthcheck.core.Report;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Mod(ModContainer.MODID)
public class ModContainer {
    public static final String MODID = "healthcheck";
    private ScheduledExecutorService executor;
    public TPSMonitor monitor;

    public ModContainer() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(() -> "ANY", (remote, isServer) -> true));
        if (Constants.HC_ENDPOINT.isEmpty()) {
            LogManager.getLogger().warn("Not enabling health checker since HC_ENDPOINT variable is not set.");
            return;
        }
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStart(ServerStartedEvent event) {
        monitor = new TPSMonitor(event.getServer());
        Report.onServerStart();
        executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder()
                .setNameFormat("HealthCheck-Worker-%d").build());
        executor.scheduleAtFixedRate(() -> Report.reportTPS(monitor.fetch()), 1, 1, TimeUnit.MINUTES);
    }

    @SubscribeEvent
    public void onServerStop(ServerStoppedEvent event) {
        this.monitor.shutdown();
        this.executor.shutdown();
        Report.onServerStop();
    }
}
