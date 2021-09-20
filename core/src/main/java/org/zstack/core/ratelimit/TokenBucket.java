package org.zstack.core.ratelimit;

import javax.persistence.*;

@Entity
@Table
public class TokenBucket {
    @Id
    @Column
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String apiName;

    @Column
    private Double total;

    @Column
    private Double rate;

    @Column
    private Long time;

    @Column
    private Double nowSize;

    public TokenBucket() {

    }

    public TokenBucket(String apiName, Double total, Double rate, Long time, Double nowSize) {
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

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
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
