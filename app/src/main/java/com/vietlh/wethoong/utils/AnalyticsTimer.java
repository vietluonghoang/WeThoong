package com.vietlh.wethoong.utils;

import java.util.HashMap;

public class AnalyticsTimer implements Runnable {
    private long timestamp;
    private int timeout;
    private String eventName;
    private HashMap<String, String> params;
    public boolean isRunning = false;

    public AnalyticsTimer(long timestamp, int timeout, String eventName, HashMap<String, String> params) {
        this.timestamp = timestamp;
        if (timeout <= 0) {
            this.timeout = GeneralSettings.DEFAULT_MIXPANEL_EVENT_SEND_TIMEOUT;
        } else {
            this.timeout = timeout;
        }
        this.eventName = eventName;
        this.params = params;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    public void update(long timestamp, String paramKey, String paramValue) {
        this.timestamp = timestamp;
        if (this.params == null) {
            this.params = new HashMap<>();
        }
        this.params.put(paramKey, paramValue);
    }

    @Override
    public void run() {
        isRunning = true;
        while (System.currentTimeMillis() - timestamp < timeout) {
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        AnalyticsHelper.sendAnalyticEvent(eventName, params);
        isRunning = false;
    }
}
