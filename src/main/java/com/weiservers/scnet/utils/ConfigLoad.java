package com.weiservers.scnet.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiservers.scnet.Main;
import com.weiservers.scnet.bean.Server;
import com.weiservers.scnet.bean.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class ConfigLoad {
    private final static Logger logger = LoggerFactory.getLogger(ConfigLoad.class);
    public static List<Server> serverlist;
    private static Map<String, Object> setting;
    static {
        File folder = new File(".//Config");// 输出文件的父目录
        if (!folder.exists()&& !folder.isDirectory()) {// 父目录不存在时先创建
            if (folder.mkdirs()) {
                logger.error("尝试创建配置文件夹失败");
                System.exit(0);
            }
        }
        CheckConfig("banned-areas.json");
        CheckConfig("banned-ips.json");
        CheckConfig("banned-players.json");
        CheckConfig("players-choose.json");
        CheckConfig("setting.json");
        CheckConfig("whitelist.json");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File("./config/setting.json"));
            setting = objectMapper.readValue(rootNode.toString(), new TypeReference<>() {
            });
            serverlist = objectMapper.readValue(rootNode.get("server_list").toString(), new TypeReference<>() {
            });
        } catch (Exception e) {
            logger.error("读取配置文件失败", e);
            System.exit(0);
        }
    }


    public static void CheckConfig(String fileName){
        try {
            File file = new File("./Config/" + fileName);
            if (!file.exists()) {
                try (InputStream is = Main.class.getResourceAsStream("/Config/" + fileName)) {
                    if (!file.exists() &&!file.createNewFile() ) {
                            logger.error("尝试创建配置文件失败：{}", fileName);
                            System.exit(0);
                    }
                    OutputStream os = new FileOutputStream(file);// 创建输出流
                    int index;
                    byte[] bytes = new byte[4096];
                    if (is != null) {
                        while ((index = is.read(bytes)) != -1) {
                            os.write(bytes, 0, index);
                        }
                    }
                    os.flush();
                    os.close();
                }
            }
        } catch (Exception e) {
            logger.error("尝试创建配置文件:{}失败：", fileName, e);
            System.exit(0);
        }
    }

    public static  List<Server>  getServerlist() {
        return serverlist;
    }
    public static Map<String, Object> getSetting() {
        return setting;
    }

    public static List<Whitelist> readWhitelist() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File("Config/whitelist.json"));
            List<Whitelist> whitelists = objectMapper.readValue(rootNode.toString(), new TypeReference<>() {
            });
            return whitelists;
        } catch (Exception e) {

        }
        return null;
    }

}
