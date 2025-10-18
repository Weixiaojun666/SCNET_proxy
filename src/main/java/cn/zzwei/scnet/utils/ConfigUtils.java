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
          Toml advanced =toml.getTable("advanced");
            ConfigMapping.ioWorkThreadNumber = advanced.getLong("ioWorkThreadNumber", (long) Runtime.getRuntime().availableProcessors()).intValue();
            ConfigMapping.ioAcceptThreadNumber = advanced.getLong("ioAcceptThreadNumber", (long) Runtime.getRuntime().availableProcessors()).intValue();
            ConfigMapping.connectTimeout = advanced.getLong("connectTimeout", 10000L).intValue();
            ConfigMapping.ioMaxBacklog = advanced.getLong("ioMaxBacklog", 64L).intValue();
            ConfigMapping.openLoggingHandler = advanced.getBoolean("openLoggingHandler", false);
            ConfigMapping.timeout = advanced.getLong("timeout", 10000L).intValue();
            ConfigMapping.corePoolSize = advanced.getLong("corePoolSize", (long) Runtime.getRuntime().availableProcessors()).intValue();
            ConfigMapping.keepAliveTime = (long) advanced.getLong("keepAliveTime", 30L).intValue();
            ConfigMapping.HttpTimeout = advanced.getLong("HttpTimeout", 10000L).intValue();
            ConfigMapping.HttpRetryCount = advanced.getLong("HttpRetryCount", 10000L).intValue();
            ConfigMapping.HttpUrl = advanced.getString("HttpUrl","https://api.sckey.net/");
            ConfigMapping.checkIpInterval = advanced.getLong("checkIpInterval",5L).intValue();

    }
}
