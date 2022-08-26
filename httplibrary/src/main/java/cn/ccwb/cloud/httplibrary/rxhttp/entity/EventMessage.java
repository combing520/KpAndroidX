package cn.ccwb.cloud.httplibrary.rxhttp.entity;

import android.content.Intent;

import java.util.List;

/**
 * EventBus  message类
 *
 * @author kpinfo
 */
public class EventMessage {
    private String label;
    private String content;
    private String from;
    private int res;
    private String postPath;
    private int pos;
    private List<LiveHostEntity.LiveHostItemEntity> liveChatList;
    private LiveHostEntity.LiveHostItemEntity item;
    private String mapPicPath;
    private String longitude;
    private String latitude;
    private String address;
    private Intent intentData;
    private Html2AppEntity html2AppEntity;
    private String callBackMethod;
    private int minTimeLength;
    private int maxTimeLength;



    public EventMessage(String label, Intent intentData) {
        this.label = label;
        this.intentData = intentData;
    }

    public EventMessage(String label, String content) {
        this.label = label;
        this.content = content;
    }

    public EventMessage(String label, int pos) {
        this.label = label;
        this.pos = pos;
    }

    public EventMessage(String label, String content, String from) {
        this.label = label;
        this.content = content;
        this.from = from;
    }

    public EventMessage(String label, String content, String from, int res) {
        this.label = label;
        this.content = content;
        this.from = from;
        this.res = res;
    }

    public EventMessage(String label, String content, String from, String postPath) {
        this.label = label;
        this.content = content;
        this.from = from;
        this.postPath = postPath;
    }


    public EventMessage(String label, List<LiveHostEntity.LiveHostItemEntity> liveChatList) {
        this.label = label;
        this.liveChatList = liveChatList;
    }
    public EventMessage(String label,Html2AppEntity entity){
        this.label = label;
        this.html2AppEntity = entity;
    }

    public EventMessage(String label, LiveHostEntity.LiveHostItemEntity item) {
        this.label = label;
        this.item = item;
    }


    public EventMessage(String label, String content, String longitude, String latitude, String address, String mapPicPath) {

        this.label = label;
        this.content = content;
        this.longitude = longitude;
        this.latitude = latitude;
        this.address = address;
        this.mapPicPath = mapPicPath;
    }

    public EventMessage(String label, int minTimeLength, int maxTimeLength, String callBackMethod) {
        this.label = label;
        this.minTimeLength = minTimeLength;
        this.maxTimeLength = maxTimeLength;
        this.callBackMethod = callBackMethod;
    }



    public String getLabel() {
        return label;
    }

    public String getContent() {
        return content;
    }

    public String getFrom() {
        return from;
    }

    public int getRes() {
        return res;
    }


    public String getPostPath() {
        return postPath;
    }

    public List<LiveHostEntity.LiveHostItemEntity> getLiveChatList() {
        return liveChatList;
    }

    public LiveHostEntity.LiveHostItemEntity getItem() {
        return item;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getAddress() {
        return address;
    }

    public String getMapPicPath() {
        return mapPicPath;
    }

    public int getPos() {
        return pos;
    }

    public Intent getIntentData() {
        return intentData;
    }

    public Html2AppEntity getHtml2AppEntity() {
        return html2AppEntity;
    }


    public String getCallBackMethod() {
        return callBackMethod;
    }

    public void setCallBackMethod(String callBackMethod) {
        this.callBackMethod = callBackMethod;
    }
    public int getMinTimeLength() {
        return minTimeLength;
    }

    public void setMinTimeLength(int minTimeLength) {
        this.minTimeLength = minTimeLength;
    }
    public int getMaxTimeLength() {
        return maxTimeLength;
    }

    public void setMaxTimeLength(int maxTimeLength) {
        this.maxTimeLength = maxTimeLength;
    }



}
