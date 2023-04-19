package com.weiservers.scnet.base;

public class Invalid {
    private final long create_time;
    private int num;
    private long update_time;

    private long check_time;

    public Invalid(long create_time) {
        this.create_time = create_time;
        this.update_time = create_time;
        this.check_time = create_time;
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

    public long getCheck_time() {
        return check_time;
    }

    public void setCheck_time(long check_time) {
        this.check_time = check_time;
    }
}
