package com.oneaix.selection.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** 市场数据源配置：jdbc 样例 / external 真实 API 2026-06-05 */
@ConfigurationProperties(prefix = "selection.market-data")
public class MarketDataProperties {

    /** jdbc-only | external-primary */
    private String mode = "jdbc-only";

    private External external = new External();

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public External getExternal() {
        return external;
    }

    public void setExternal(External external) {
        this.external = external;
    }

    public boolean externalPrimary() {
        return "external-primary".equalsIgnoreCase(mode) && external.isEnabled();
    }

    public static class External {
        private boolean enabled = false;
        private String baseUrl = "";
        private String apiKey = "";
        private String trendsPath = "/market/trends";
        private String competitionPath = "/market/competition";
        private String supplyPath = "/market/supply-demand";
        private int connectTimeoutMs = 5000;
        private int readTimeoutMs = 15000;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getTrendsPath() {
            return trendsPath;
        }

        public void setTrendsPath(String trendsPath) {
            this.trendsPath = trendsPath;
        }

        public String getCompetitionPath() {
            return competitionPath;
        }

        public void setCompetitionPath(String competitionPath) {
            this.competitionPath = competitionPath;
        }

        public String getSupplyPath() {
            return supplyPath;
        }

        public void setSupplyPath(String supplyPath) {
            this.supplyPath = supplyPath;
        }

        public int getConnectTimeoutMs() {
            return connectTimeoutMs;
        }

        public void setConnectTimeoutMs(int connectTimeoutMs) {
            this.connectTimeoutMs = connectTimeoutMs;
        }

        public int getReadTimeoutMs() {
            return readTimeoutMs;
        }

        public void setReadTimeoutMs(int readTimeoutMs) {
            this.readTimeoutMs = readTimeoutMs;
        }
    }
}
