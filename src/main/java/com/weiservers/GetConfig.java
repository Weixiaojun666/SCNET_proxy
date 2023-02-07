package com.weiservers;

import Base.Server;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Map;

public class GetConfig {
    private final static Logger logger = LoggerFactory.getLogger(GetConfig.class);
    private static List<Server> serverlist;
    private static Map<String, Object> setting;

    public GetConfig() {
    }

    public static void Read() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File("./setting.json"));
            setting = objectMapper.readValue(rootNode.toString(), new TypeReference<>() {
            });
            serverlist = objectMapper.readValue(rootNode.get("server_list").toString(), new TypeReference<>() {
            });
        } catch (Exception e) {
            logger.error("{}读取配置文件失败{} {}", "\033[31m", e, "\033[0m");
            System.exit(0);
        }
    }

    public static List<Server> getServerlist() {
        return serverlist;
    }

    public static Map<String, Object> getSetting() {
        return setting;
    }
}
