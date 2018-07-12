package com.hr.videosplayertv.net.entry.request;

import com.hr.videosplayertv.net.base.BaseDataRequest;

/**
 * lv
 */
public class WhatCom extends BaseDataRequest {

    public WhatCom() {

    }
    public WhatCom(
            String token,
            String CID,
            String UID,
            String GID,
            String sign,
            String expire,
            String size,
            String page
    ) {
        super(token, CID, UID, GID, sign, expire);
        Size = size;
        Page = page;
    }
    public WhatCom(
            String token,
            String CID,
            String UID,
            String GID,
            String sign,
            String expire,
            String size,
            String page,
            String tags
    ) {
        super(token, CID, UID, GID, sign, expire);
        Size = size;
        Page = page;
        Tags = tags;
    }

    /**
     * Size : 20
     * Page : 1
     */


    private String Size;
    private String Page;
    private String Tags;

    public String getSize() {
        return Size;
    }

    public void setSize(String Size) {
        this.Size = Size;
    }

    public String getPage() {
        return Page;
    }

    public void setPage(String Page) {
        this.Page = Page;
    }

    public String getTags() {
        return Tags;
    }

    public void setTags(String tags) {
        Tags = tags;
    }
}
