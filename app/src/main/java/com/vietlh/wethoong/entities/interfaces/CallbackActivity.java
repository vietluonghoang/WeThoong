package com.vietlh.wethoong.entities.interfaces;

public interface CallbackActivity {
    //this method is used for triggering callback action
    //actionCase param will define which case will be triggered (in case callback action is triggered by different threads)
    public void triggerCallbackAction(String actionCase);
}
