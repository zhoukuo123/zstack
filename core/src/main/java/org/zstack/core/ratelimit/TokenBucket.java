package org.zstack.core.ratelimit;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class TokenBucket {
    private String apiName;
    private int maxFlowRate;
    private int avgFlowRate;

    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    
    public TokenBucket(Class clz) {
        apiName = clz.getSimpleName();
    }

    public TokenBucket(int maxFlowRate, int avgFlowRate, String apiName) {
        this.maxFlowRate = maxFlowRate;
        this.avgFlowRate = avgFlowRate;
        this.apiName = apiName;
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
            TokenBucketFacadeImpl.addTokens(avgFlowRate, tokenBucket);
        }
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

    public void setMaxFlowRate(int maxFlowRate) {
        this.maxFlowRate = maxFlowRate;
    }

    public int getAvgFlowRate() {
        return avgFlowRate;
    }

    public void setAvgFlowRate(int avgFlowRate) {
        this.avgFlowRate = avgFlowRate;
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    public void setScheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
    }
}
