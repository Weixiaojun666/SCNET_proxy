package com.weiservers.scnet.bean;

import lombok.Data;

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
