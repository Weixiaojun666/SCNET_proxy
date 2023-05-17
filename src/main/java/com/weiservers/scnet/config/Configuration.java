package com.weiservers.scnet.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiservers.scnet.Main;
import com.weiservers.scnet.bean.record.Banned;
import com.weiservers.scnet.bean.record.SelectList;
import com.weiservers.scnet.bean.record.Setting;
import com.weiservers.scnet.bean.record.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class Configuration {
    private final static Logger logger = LoggerFactory.getLogger(Configuration.class);
    private static Banned Banned;
    private static Whitelist Whitelist;
    private static SelectList Selectlist;
    private static Setting Setting;

    public static void Load() {
        File folder = new File(".//Config");// 输出文件的父目录
        if (!folder.exists() && !folder.isDirectory()) {// 父目录不存在时先创建
            logger.warn("The config folder does not exist, it has been created");
            if (!folder.mkdirs()) {
                logger.error("尝试创建配置文件夹失败");
                System.exit(0);
            }
        }
        CheckConfig("banned.json");
        CheckConfig("selectList.json");
        CheckConfig("setting.json");
        CheckConfig("whitelist.json");

        ReadConfig();
    }

    public static void BackupConfig(String fileName) {
        //保存前备份之前配置 ./config -> ./config/backup
        File folder = new File(".//Config/backup");// 输出文件的父目录
        if (!folder.exists() && !folder.isDirectory()) {// 父目录不存在时先创建
            logger.warn("The config backup folder does not exist, it has been created");
            if (!folder.mkdirs()) {
                logger.error("尝试创建配置文件夹失败");
                System.exit(0);
            }
        }
        //将配置文件复制到backup目录
        try {
            File file = new File("./Config/backup/" + fileName);
            File srcFile = new File("./Config/" + fileName);
            FileInputStream fis = new FileInputStream(srcFile);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] b = new byte[1024];
            int len;
            while ((len = fis.read(b)) != -1) {
                fos.write(b, 0, len);
            }
            fos.close();
            fis.close();
        } catch (Exception e) {
            logger.error("尝试备份配置文件:{}失败：", fileName, e);
            System.exit(0);
        }


    }

    public static void SaveConfig() {
        try {
            BackupConfig("banned.json");
            BackupConfig("selectList.json");
            BackupConfig("setting.json");
            BackupConfig("whitelist.json");

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(new File("./config/setting.json"), Setting);
            objectMapper.writeValue(new File("./config/banned.json"), Banned);
            objectMapper.writeValue(new File("./config/whitelist.json"), Whitelist);
            objectMapper.writeValue(new File("./config/selectList.json"), Selectlist);
        } catch (Exception e) {
            logger.error("保存配置文件失败", e);
        }
    }

    public static void ReadConfig() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Setting = objectMapper.readValue(new File("./config/setting.json"), Setting.class);
            Whitelist = objectMapper.readValue(new File("./config/whitelist.json"), Whitelist.class);
            Banned = objectMapper.readValue(new File("./config/banned.json"), Banned.class);
            Selectlist = objectMapper.readValue(new File("./config/selectList.json"), SelectList.class);
        } catch (Exception e) {
            logger.error("读取配置文件失败", e);
        }
    }

    public static void CheckConfig(String fileName) {
        try {
            File file = new File("./Config/" + fileName);
            if (!file.exists()) {
                try (InputStream is = Main.class.getResourceAsStream("/Config/" + fileName)) {
                    if (!file.exists()) {
                        logger.warn("{} file does not exist, it has been created,", fileName);
                        if (!file.createNewFile()) {
                            logger.error("尝试创建配置文件失败：{}", fileName);
                            System.exit(0);
                        }
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

    public static SelectList getSelectlist() {
        return Selectlist;
    }

}
