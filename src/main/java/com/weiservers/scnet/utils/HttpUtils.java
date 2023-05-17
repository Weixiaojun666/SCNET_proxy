package com.weiservers.scnet.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiservers.scnet.config.Configuration;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpUtils {
    private final static Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    private final static PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();

    private final static RequestConfig requestConfig;

    static {
        connManager.setMaxTotal(1000);
        connManager.setDefaultMaxPerRoute(1000);
        requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofSeconds(Configuration.getSetting().basicConfig().httpTimeout()))
                .build();
    }

    public static JsonNode HttpGet(String url) {
        HttpGet httpGet = new HttpGet(encodeUTF_8(url));
        httpGet.setConfig(requestConfig);
        httpGet.addHeader("Accept-Charset", "utf-8");
        return HttpClient(httpGet);
    }

    public static JsonNode HttpPost(String url, String data) {
        HttpPost httpPost = new HttpPost(encodeUTF_8(url));
        httpPost.setConfig(requestConfig);
        httpPost.addHeader("Accept-Charset", "utf-8");
        httpPost.addHeader("Content-Type", "application/json");
        StringEntity entity = new StringEntity(data, ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);
        return HttpClient(httpPost);
    }

    private static JsonNode HttpClient(ClassicHttpRequest request) {
        int num = 0;
        while (num < Configuration.getSetting().basicConfig().httpRetryCount()) {
            try (CloseableHttpClient httpClient = HttpClientBuilder.create().setConnectionManager(connManager).build()) {
                String responseBody = httpClient.execute(request, new BasicHttpClientResponseHandler());
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readTree(responseBody);
            } catch (IOException e) {
                num++;
                logger.error("Http [{}] IO failed: url :[{}]", request.getMethod(), request.getPath(), e);
            } catch (RuntimeException e) {
                logger.error("Http [{}] Runtime failed: url :[{}]", request.getMethod(), request.getPath(), e);
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private static String encodeUTF_8(String str) {
        //匹配中文和空格的正则表达式
        String zhPattern = "[一-龥]+";
        Pattern p = Pattern.compile(zhPattern);
        Matcher m = p.matcher(str);
        StringBuilder b = new StringBuilder();
        while (m.find()) {
            m.appendReplacement(b, URLEncoder.encode(m.group(0), StandardCharsets.UTF_8));
        }
        m.appendTail(b);
        return b.toString();
    }
}

