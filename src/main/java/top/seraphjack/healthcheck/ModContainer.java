package top.seraphjack.healthcheck;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
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
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST,
                () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (serverVer, isDedicated) -> true));
        if (Constants.HC_ENDPOINT.isEmpty()) {
            LogManager.getLogger().warn("Not enabling health checker since HC_ENDPOINT variable is not set.");
            return;
        }
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStart(FMLServerStartedEvent event) {
        monitor = new TPSMonitor(event.getServer());
        Report.onServerStart();
        executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder()
                .setNameFormat("HealthCheck-Worker-%d").build());
        executor.scheduleAtFixedRate(() -> Report.reportTPS(monitor.fetch()), 1, 1, TimeUnit.MINUTES);
    }

    @SubscribeEvent
    public void onServerStop(FMLServerStoppingEvent event) {
        this.monitor.shutdown();
        this.executor.shutdown();
        Report.onServerStop();
    }
}
