package com.hr.videosplayertv.net.entry;

import com.hr.videosplayertv.common.ImmobilizationData;

public class ListData {

    private int type = 1;

    private String title;

    private ImmobilizationData.Tags tags;

    private int back;

    public ListData() {
    }

    public ListData(int type) {
        this.type = type;
    }

    public ListData(String title) {
        this.title = title;
    }

    public ListData(int type, String title) {
        this.type = type;
        this.title = title;
    }

    public ListData(int type, String title, ImmobilizationData.Tags tags) {
        this.type = type;
        this.title = title;
        this.tags = tags;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ImmobilizationData.Tags getTags() {
        return tags;
    }

    public void setTags(ImmobilizationData.Tags tags) {
        this.tags = tags;
    }

    public int getBack() {
        return back;
    }

    public ListData setBack(int back) {
        this.back = back;
        return this;
    }
}
