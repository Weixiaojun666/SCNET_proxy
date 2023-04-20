package com.weiservers.scnet.bean;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.HashSet;
//@Component
@Data
public class Info {
    //正常ip列表
    private final HashSet<InetAddress> normal_ip = new HashSet<>();
    //异常ip列表
    private final HashSet<InetAddress> abnormal_ip = new HashSet<>();
    private final long time;
    //正常请求
    private int normal;
    //异常请求
    private int abnormal;
    //无效数据包
    private int invalid;
    //缓存应答
    private int respond;
    //缓存刷新
    private int refresh;

    public Info(long time) {
        this.time = time;
    }

    public HashSet<InetAddress> getNormal_ip() {
        return normal_ip;
    }

    public HashSet<InetAddress> getAbnormal_ip() {
        return abnormal_ip;
    }

    public int getRespond() {
        return respond;
    }

    public int getRefresh() {
        return refresh;
    }

    public int getAbnormal() {
        return abnormal;
    }

    public int getInvalid() {
        return invalid;
    }

    public int getNormal() {
        return normal;
    }

    public long getTime() {
        return time;
    }

    public void addInvalid() {
        invalid++;
    }

    public void addRespond() {
        respond++;
    }

    public void addRefresh() {
        refresh++;
    }

    public void addNormal() {
        normal++;
    }

    public void addAbnormal() {
        abnormal++;
    }
}
