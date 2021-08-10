package org.zstack.core.ratelimit;

import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.SQL;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
public class TokenBucket {

    @Autowired
    private DatabaseFacade dbf;

    private String apiName;

    private int maxFlowRate;

    private int avgFlowRate;

    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private volatile boolean isStart = false;

    private ReentrantLock lock = new ReentrantLock(true);

    public TokenBucket() {

    }

    public TokenBucket(int maxFlowRate, int avgFlowRate) {
        this.maxFlowRate = maxFlowRate;
        this.avgFlowRate = avgFlowRate;
    }

    public TokenBucket(int maxFlowRate, int avgFlowRate, String apiName) {
        this.maxFlowRate = maxFlowRate;
        this.avgFlowRate = avgFlowRate;
        this.apiName = apiName;
    }

    public void addTokens(Integer tokenNum) {
        for (int i = 0; i < tokenNum; i++) {
            Integer token = SQL.New("select token from APIRateLimitVO where api = :apiName", Integer.class).param("apiName", apiName).find();
            if (token < maxFlowRate) {
                SQL.New("update APIRateLimitVO apirl set apirl.token = apirl.token + 1 where apirl.api = :apiName").param("apiName", apiName).execute();
            } else {
                break;
            }
        }
    }

    public TokenBucket build() {
        start();
        return this;
    }

    public boolean getTokens() {
        Preconditions.checkArgument(isStart, "please invoke start method first !");

        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Integer token = SQL.New("select token from APIRateLimitVO where api = :apiName", Integer.class).param("apiName", apiName).find();

            if (token == 0) {
                return false;
            }

            SQL.New("update APIRateLimitVO apirl set apirl.token = apirl.token - 1 where apirl.api = :apiName").param("apiName", apiName).execute();

            return true;
        } finally {
            lock.unlock();
        }
    }

    public void start() {

        if (maxFlowRate != 0) {
            APIRateLimitVO apirl = new APIRateLimitVO();
            apirl.setApi(apiName);
            apirl.setToken(maxFlowRate);
            dbf.persist(apirl);
        }

        TokenProducer tokenProducer = new TokenProducer(avgFlowRate, this);
        scheduledExecutorService.scheduleAtFixedRate(tokenProducer, 0, 1, TimeUnit.SECONDS);
        isStart = true;
    }

    public void stop() {
        isStart = false;
        scheduledExecutorService.shutdown();
    }

    public boolean isStarted() {
        return isStart;
    }

    class TokenProducer implements Runnable {

        private int avgFlowRate;
        private TokenBucket tokenBucket;

        public TokenProducer(int avgFlowRate, TokenBucket tokenBucket) {
            this.avgFlowRate = avgFlowRate;
            this.tokenBucket = tokenBucket;
        }

        @Override
        public void run() {
            tokenBucket.addTokens(avgFlowRate);
        }
    }

    public TokenBucket maxFlowRate(int maxFlowRate) {
        this.maxFlowRate = maxFlowRate;
        return this;
    }

    public TokenBucket avgFlowRate(int avgFlowRate) {
        this.avgFlowRate = avgFlowRate;
        return this;
    }

    public TokenBucket apiName(String apiName) {
        this.apiName = apiName;
        return this;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public int getMaxFlowRate() {
        return maxFlowRate;
    }

    public TokenBucket setMaxFlowRate(int maxFlowRate) {
        this.maxFlowRate = maxFlowRate;
        return this;
    }

    public int getAvgFlowRate() {
        return avgFlowRate;
    }

    public TokenBucket setAvgFlowRate(int avgFlowRate) {
        this.avgFlowRate = avgFlowRate;
        return this;
    }
}
