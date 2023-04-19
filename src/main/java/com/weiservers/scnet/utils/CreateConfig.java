package com.weiservers.scnet.utils;

import com.weiservers.scnet.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class CreateConfig {
    private final static Logger logger = LoggerFactory.getLogger(CreateConfig.class);

    public void Config(String fileName) {
        try {
            File file = new File("./Config" + fileName);
            if (!file.exists()) {
                try (InputStream is = Main.class.getResourceAsStream("/" + fileName)) {
                    File fp = new File(file.getParent());// 输出文件的父目录
                    if (!fp.exists()) {// 父目录不存在时先创建
                        if (fp.mkdirs()) {
                            logger.error("尝试创建配置文件夹失败");
                            System.exit(0);
                        }
                    }
                    if (!file.exists()) {
                        if (!file.createNewFile()) {
                            logger.error("尝试创建配置文件失败：{}", fileName);
                            System.exit(0);
                        }
                    }
                    OutputStream os = new FileOutputStream(file);// 创建输出流
                    int index;
                    byte[] bytes = new byte[4096];
                    if (is != null) {
                        while ((index = is.read(bytes)) != -1) {
                            os.write(bytes, 0, index);
                        }
                    } else {
                        logger.error("尝试创建配置文件失败：{}", fileName);
                        System.exit(0);
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
}
