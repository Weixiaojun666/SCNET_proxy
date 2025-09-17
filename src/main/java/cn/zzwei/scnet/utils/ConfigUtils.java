package cn.zzwei.scnet.utils;

import cn.zzwei.scnet.mapping.ConfigMapping;
import cn.zzwei.scnet.mapping.ForwardMapping;
import com.moandjiezana.toml.Toml;

import java.io.File;
import java.util.List;


public class ConfigUtils {

    final static String CONFIG_FILE = "./config.toml";
    static Toml toml;

    public static void readToml() {
        toml = new Toml().read(new File(CONFIG_FILE));
    }

    public static List<ForwardMapping> readForwards() {
        return toml.getTables("forward").stream().map(table -> {
            String name = table.getString("name");
            String type = table.getString("type");
            String localAddress = table.getString("localAddress", "localhost");
            String remoteAddress = table.getString("remoteAddress", "localhost");
            Integer localPort = table.getLong("localPort").intValue();
            Integer remotePort = table.getLong("remotePort").intValue();
            return new ForwardMapping(name, type, localAddress, remoteAddress, localPort, remotePort);
        }).toList();
    }

    public static void readAdvanced() {
        if (toml.getTable("advanced").getBoolean("enable", false)) {
            ConfigMapping.ioWorkThreadNumber = toml.getLong("ioWorkThreadNumber", (long) Runtime.getRuntime().availableProcessors()).intValue();
            ConfigMapping.ioAcceptThreadNumber = toml.getLong("ioAcceptThreadNumber", (long) Runtime.getRuntime().availableProcessors()).intValue();
            ConfigMapping.connectTimeout = toml.getLong("connectTimeout", 10000L).intValue();
            ConfigMapping.ioMaxBacklog = toml.getLong("ioMaxBacklog", 64L).intValue();
            ConfigMapping.openLoggingHandler = toml.getBoolean("openLoggingHandler", false);
            ConfigMapping.timeout = toml.getLong("timeout", 10000L).intValue();
            ConfigMapping.corePoolSize = toml.getLong("corePoolSize", (long) Runtime.getRuntime().availableProcessors()).intValue();
            ConfigMapping.keepAliveTime = (long) toml.getLong("keepAliveTime", 30L).intValue();
            ConfigMapping.HttpTimeout = toml.getLong("HttpTimeout", 10000L).intValue();
            ConfigMapping.HttpRetryCount = toml.getLong("HttpRetryCount", 10000L).intValue();
        } else {
            ConfigMapping.ioWorkThreadNumber = Runtime.getRuntime().availableProcessors();
            ConfigMapping.ioAcceptThreadNumber = Runtime.getRuntime().availableProcessors();
            ConfigMapping.connectTimeout = 10000;
            ConfigMapping.ioMaxBacklog = 64;
            ConfigMapping.openLoggingHandler = false;
            ConfigMapping.timeout = 10000;
            ConfigMapping.corePoolSize = Runtime.getRuntime().availableProcessors();
            ConfigMapping.keepAliveTime = 30L;
            ConfigMapping.HttpTimeout = 5;
            ConfigMapping.HttpRetryCount = 3;
        }
    }
}
