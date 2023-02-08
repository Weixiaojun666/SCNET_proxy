package com.weiservers.Cloud;

import com.weiservers.Base.BanArea;
import com.weiservers.Base.BanIp;
import com.weiservers.Base.BanUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiservers.GetConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.weiservers.Core.Tools.HttpClientW;

public class Cloud {
    private final static Logger logger = LoggerFactory.getLogger(Cloud.class);

    private static String access_token;

    public Cloud() {
    }

    public static void getBanArea(int page) {
        if (access_token == null) {
            logger.warn("请登录后使用此功能");
            return;
        }
        try {
            JsonNode rootNode = HttpClientW("https://api.weiservers.com/scnet/ban/getArea?token=" + access_token + "&page=" + page);
            String code = rootNode.at("/code").toString();
            int count = rootNode.at("/count").asInt();
            String msg = rootNode.at("/msg").toString();
            ObjectMapper objectMapper = new ObjectMapper();
            List<BanArea> BanArealist = objectMapper.readValue(rootNode.at("/data").toString(), new TypeReference<>() {
            });
            if (code.equals("0")) {
                logger.info("======================================================");
                logger.info(String.format("%-16s %-24s %-24s %-16s %-16s", "ID", "信息1", "信息2", "信息3", "运营商"));
                for (BanArea banArea : BanArealist) {
                    logger.info(String.format("%-16s %-24s %-24s %-16s %-16s", banArea.id(), banArea.info1(), banArea.info2(), banArea.info3(), banArea.isp()));
                }
                logger.info("======================================================");
                logger.info("当前第{}页 共{}页 总计{}条", page, (int) Math.ceil(count * 1.0 / 10), count);
            } else {
                logger.warn("操作失败:{}",msg);
            }
        } catch (Exception e) {
            logger.error("操作失败:",e);
        }

    }

    public static void getBanUser(int page) throws JsonProcessingException {
        if (access_token == null) {
            logger.warn("请登录后使用此功能");
            return;
        }
        try {
            JsonNode rootNode = HttpClientW("https://api.weiservers.com/scnet/ban/getUser?token=" + access_token + "&page=" + page);
            String code = rootNode.at("/code").toString();
            int count = rootNode.at("/count").asInt();
            String msg = rootNode.at("/msg").toString();
            ObjectMapper objectMapper = new ObjectMapper();
            List<BanUser> Banuserlist = objectMapper.readValue(rootNode.at("/data").toString(), new TypeReference<>() {
            });
            if (code.equals("0")) {
                logger.info("======================================================");
                logger.info(String.format("%-16s %-24s %-24s", "id", "用户名", "社区ID"));
                for (BanUser banUser : Banuserlist) {
                    logger.info(String.format("%-16s %-24s %-24s", banUser.id(), banUser.username(), banUser.userid()));
                }
                logger.info("======================================================");
                logger.info("当前第{}页 共{}页 总计{}条", page, (int) Math.ceil(count * 1.0 / 10), count);
            } else {
                logger.warn("操作失败:{}",msg);
            }
        } catch (Exception e) {
            logger.error("操作失败:",e);
        }
    }

    public static void getBanIp(int page) {
        if (access_token == null) {
            logger.warn("请登录后使用此功能");
            return;
        }
        try {
            JsonNode rootNode = HttpClientW("https://api.weiservers.com/scnet/ban/getIp?token=" + access_token + "&page=" + page);
            String code = rootNode.at("/code").toString();
            int count = rootNode.at("/count").asInt();
            String msg = rootNode.at("/msg").toString();
            ObjectMapper objectMapper = new ObjectMapper();
            List<BanIp> Baniplist = objectMapper.readValue(rootNode.at("/data").toString(), new TypeReference<>() {
            });
            if (code.equals("0")) {
                logger.info("======================================================");
                logger.info(String.format("%-16s %-24s ", "id", "ip"));
                for (BanIp banIp : Baniplist) {
                    logger.info(String.format("%-16s %-24s ", banIp.id(), banIp.ip()));
                }
                logger.info("======================================================");
                logger.info("当前第{}页 共{}页 总计{}条", page, (int) Math.ceil(count * 1.0 / 10), count);
            } else {
                logger.info("操作失败:",msg);
            }
        } catch (Exception e) {
            logger.info("\033[33m 拉取失败,检查token或者网络连接 {} \033[0m", e);
        }
    }
    public static void banuser(String username,String reason,String time,String state) {
        if (access_token == null) {
            logger.info("\033[33m 请登录后使用此功能  \033[0m");
            return;
        }
        try {
            String url = "state="+state+"&token=" + access_token + "&username=" + username + "&reason=" + reason + "&time=" + time;
            JsonNode rootNode = HttpClientW("https://api.weiservers.com/scnet/ban/saveUser?" + url);
            String code = rootNode.at("/code").toString();
            String msg = rootNode.at("/msg").toString();
            if (code.equals("0")) {
                logger.info("操作成功:{}",msg);
            } else {
                logger.info("操作失败:{}",msg);
            }
        } catch (Exception e) {
            logger.info("操作失败:",e);
        }
    }

    public static void banip(String ip,String reason,String time,String state) {
        if (access_token == null) {
            logger.info("\033[33m 请登录后使用此功能  \033[0m");
            return;
        }
        try {
            String url = "state="+state+"&token=" + access_token + "&ip=" + ip + "&reason=" + reason + "&time=" + time;
            JsonNode rootNode = HttpClientW("https://api.weiservers.com/scnet/ban/saveIp?" + url);
            String code = rootNode.at("/code").toString();
            String msg = rootNode.at("/msg").toString();
            if (code.equals("0")) {
                logger.info("操作成功:{}",msg);
            } else {
                logger.info("操作失败:{}",msg);
            }
        } catch (Exception e) {
            logger.info("操作失败:",e);
        }
    }
    public static void banarea(String ip,String reason,String time,String state) {
        if (access_token == null) {
            logger.info("\033[33m 请登录后使用此功能  \033[0m");
            return;
        }
        try {
            String url = "state="+state+"&token=" + access_token + "&ip=" + ip + "&reason=" + reason + "&time=" + time;
            JsonNode rootNode = HttpClientW("https://api.weiservers.com/scnet/ban/saveAreaByIp?" + url);
            String code = rootNode.at("/code").toString();
            String msg = rootNode.at("/msg").toString();
            if (code.equals("0")) {
                logger.info("操作成功: {}",msg);
            } else {
                logger.info("操作失败: {}",msg);
            }
        } catch (Exception e) {
            logger.info("操作失败:",e);
        }
    }
    public static void postloguser(String id) {
        if (access_token == null) return;
        String url = "token=" + access_token + "&id=" + id;
        HttpClientW("https://api.weiservers.com/scnet/log/user?" + url);
    }

    public static String postloguser(String userid, String username, String ip, String servername, String reason, String state) {
        if (access_token == null) return "0";
        try {
            String url = "token=" + access_token + "&userid=" + userid + "&name=" + username + "&ip=" + ip + "&server=" + servername + "&reason=" + reason + "&state=" + state;
            JsonNode rootNode = HttpClientW("https://api.weiservers.com/scnet/log/user?" + url);
            return rootNode.at("/data/WeiId").toString().replace("\"", "");
        } catch (Exception e) {
            logger.error("尝试上报登录记录时出现错误",e);
        }
        return "0";
    }

    public static void Load() {
        JsonNode rootNode = HttpClientW("https://api.weiservers.com/scnet/Index/login?token=" + GetConfig.getSetting().get("token"));
        if (rootNode == null) {
            logger.warn("无法连接到WeiServers 将只能使用本地防护功能");
            return;
        }
        String code = rootNode.at("/code").toString();
        String username = rootNode.at("/data/username").toString();
        access_token = rootNode.at("/data/access_token").toString().replace("\"", "");
        if (code.equals("0")) {
            logger.info("已成功登录WeiServers 登录用户名{}",username);
        } else {
            logger.warn("无法登录WeiServers,部分功能将不可用");
        }
    }
}
