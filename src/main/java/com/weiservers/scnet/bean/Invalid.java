package com.weiservers.scnet.bean;

import lombok.Data;


@Data
public class Invalid {
    private final long create_time;
    private int num;
    private long update_time;

    private long check_time;

    public Invalid() {
        long create_time=System.currentTimeMillis();
        this.create_time = create_time;
        this.update_time = create_time;
        this.check_time = create_time;
        this.num = 1;
    }

    public void addNum() {
        this.num += 1;
    }
}
