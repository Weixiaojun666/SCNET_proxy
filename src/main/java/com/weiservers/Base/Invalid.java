package com.weiservers.Base;

public class Invalid {
    private int num;
    private final long create_time;
    private long update_time;

    public Invalid(long create_time) {
        this.create_time = create_time;
        this.update_time = create_time;
        this.num = 1;
    }

    public void addNum() {
        this.num += 1;
    }

    public int getNum() {
        return num;
    }

    public long getCreate_time() {
        return create_time;
    }

    public long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }
}
