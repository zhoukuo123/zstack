package org.zstack.core.ratelimit;

public class TokenBucketVO {
    private Integer rate;
    private Long time;
    private Double nowSize;

    public TokenBucketVO(Integer rate, Long time, Double nowSize) {
        this.rate = rate;
        this.time = time;
        this.nowSize = nowSize;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Double getNowSize() {
        return nowSize;
    }

    public void setNowSize(Double nowSize) {
        this.nowSize = nowSize;
    }
}
