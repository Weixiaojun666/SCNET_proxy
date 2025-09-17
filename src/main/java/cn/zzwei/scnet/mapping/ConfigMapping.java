package cn.zzwei.scnet.mapping;

import lombok.Data;
import lombok.Setter;


@Data
@Setter
public class ConfigMapping {
    public static Integer ioAcceptThreadNumber;
    public static Integer ioWorkThreadNumber;
    public static Integer ioMaxBacklog;
    public static Integer timeout;
    public static Integer connectTimeout;
    public static Boolean openLoggingHandler;
    public static Integer corePoolSize;
    public static Long keepAliveTime;
}
