package com.weiservers;

import Base.Client;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {

    public static void disconnect(Client client) {
        if (client.getThread() != null) client.getThread().interrupt();
        if (client.getTo_server_socket() != null) client.getTo_server_socket().close();
        client.setTime(0);
    }

    public static String getDatePoor(Long startTime, Long endTime) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endTime - startTime;

        long day = diff / nd;

        long hour = diff % nd / nh;

        long min = diff % nd % nh / nm;
        long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟" + sec + "秒";
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xFF & aByte);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static byte[] hexStringToByteArray(String hexString) {
        hexString = hexString.replaceAll(" ", "");
        int len = hexString.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
        }
        return bytes;
    }

    public static JsonNode HttpClientW(String url) {
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(1000);
        connManager.setDefaultMaxPerRoute(1000);
        final CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connManager).setConnectionManagerShared(true).build();

        int num = 0;
        while (true) {
            num++;
            if (num == 6) {
                return null;
            }
            try (httpClient) {
                final HttpGet httpGet = new HttpGet(encodeChinese(url, "UTF-8"));
                //System.out.println(encodeChinese(url, "UTF-8"));
                httpGet.addHeader("Accept-Charset", "utf-8");
                final String responseBody = httpClient.execute(httpGet, new BasicHttpClientResponseHandler());
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readTree(responseBody);
            } catch (Exception ignored) {
            }
        }
    }

    public static String encodeChinese(String str, String charset) throws UnsupportedEncodingException {
        //匹配中文和空格的正则表达式
        String zhPattern = "[\u4e00-\u9fa5]+";
        Pattern p = Pattern.compile(zhPattern);
        Matcher m = p.matcher(str);
        StringBuffer b = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(b, URLEncoder.encode(m.group(0), charset));
        }
        m.appendTail(b);
        return b.toString();
    }
}
