package org.zstack.core.ratelimit;


public class TokenBucketVO {
    private Integer id;
    private String apiName;
    private Integer total;
    private Integer rate;
    private Long time;
    private Double nowSize;

    public TokenBucketVO(String apiName, Integer total, Integer rate, Long time, Double nowSize) {
        this.apiName = apiName;
        this.total = total;
        this.rate = rate;
        this.time = time;
        this.nowSize = nowSize;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
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
