package com.weiservers.Core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiservers.Main;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpClient {
    private final static Logger logger = LoggerFactory.getLogger(HttpClient.class);

    public static String encodeChinese(String str, String charset) throws UnsupportedEncodingException {
        //匹配中文和空格的正则表达式
        String zhPattern = "[\u4e00-\u9fa5]+";
        Pattern p = Pattern.compile(zhPattern);
        Matcher m = p.matcher(str);
        StringBuilder b = new StringBuilder();
        while (m.find()) {
            m.appendReplacement(b, URLEncoder.encode(m.group(0), charset));
        }
        m.appendTail(b);
        return b.toString();
    }

    public static JsonNode HttpClient(String url) {
        return HttpClient(url, true);
    }

    public static JsonNode HttpClient(String url, boolean verify) {
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(1000);
        connManager.setDefaultMaxPerRoute(1000);
        final CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connManager).setConnectionManagerShared(true).build();

        int num = 0;
        while (true) {
            num++;
            if (num > (int) Main.getSetting().get("timeout_retry")) {
                return null;
            }
            try (httpClient) {
                final HttpGet httpGet = new HttpGet(encodeChinese(url, "UTF-8"));
               System.out.println(encodeChinese(url, "UTF-8"));
                httpGet.addHeader("Accept-Charset", "utf-8");
                final String responseBody = httpClient.execute(httpGet, new BasicHttpClientResponseHandler());
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(responseBody);
                if (rootNode != null) {
                    String code = rootNode.at("/code").toString();
                    String msg = rootNode.at("/msg").toString();
                    if (verify) {
                        if (code.equals("200")) {
                            return rootNode;
                        } else {
                            logger.warn("服务器返回值不是预期:{} {}", code, msg);
                            return null;
                        }
                    } else return rootNode;
                }
                return null;
            } catch (Exception ignored) {
            }
        }
    }
}
