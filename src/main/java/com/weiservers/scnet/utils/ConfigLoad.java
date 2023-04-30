package com.weiservers.scnet.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiservers.scnet.Main;
import com.weiservers.scnet.bean.record.Banned;
import com.weiservers.scnet.bean.record.Selectlist;
import com.weiservers.scnet.bean.record.Setting;
import com.weiservers.scnet.bean.record.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class ConfigLoad {
    private final static Logger logger = LoggerFactory.getLogger(ConfigLoad.class);
    private static Banned Banned;
    private static Whitelist Whitelist;
    private static Selectlist Selectlist;
    private static Setting Setting;

    public static void Load() {
        File folder = new File(".//Config");// 输出文件的父目录
        if (!folder.exists() && !folder.isDirectory()) {// 父目录不存在时先创建
            if (folder.mkdirs()) {
                logger.error("尝试创建配置文件夹失败");
                System.exit(0);
            }
        }
        CheckConfig("banned.json");
        CheckConfig("selectlist.json");
        CheckConfig("setting.json");
        CheckConfig("whitelist.json");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Setting = objectMapper.readValue(new File("./config/setting.json"), Setting.class);
            Whitelist = objectMapper.readValue(new File("./config/whitelist.json"), Whitelist.class);
            //  Banned = objectMapper.readValue(new File("./config/banned.json"), Banned.class);
            // Selectlist = objectMapper.readValue(new File("./config/players-choose.json"), Selectlist.class);

        } catch (Exception e) {
            logger.error("读取配置文件失败", e);
            System.exit(0);
        }
    }

    public static void CheckConfig(String fileName) {
        try {
            File file = new File("./Config/" + fileName);
            if (!file.exists()) {
                try (InputStream is = Main.class.getResourceAsStream("/Config/" + fileName)) {
                    if (!file.exists() && !file.createNewFile()) {
                        logger.error("尝试创建配置文件失败：{}", fileName);
                        System.exit(0);
                    }
                    OutputStream os = new FileOutputStream(file);// 创建输出流
                    int index;
                    byte[] bytes = new byte[1024];
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

    public static Setting getSetting() {
        return Setting;
    }

    public static Whitelist getWhitelist() {
        return Whitelist;
    }

    public static com.weiservers.scnet.bean.record.Banned getBanned() {
        return Banned;
    }

    public static com.weiservers.scnet.bean.record.Selectlist getSelectlist() {
        return Selectlist;
    }

    public static List<Whitelist> readWhitelist() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File("Config/whitelist.json"));
            return objectMapper.readValue(rootNode.toString(), new TypeReference<>() {
            });
        } catch (Exception ignored) {

        }
        return null;
    }

}
