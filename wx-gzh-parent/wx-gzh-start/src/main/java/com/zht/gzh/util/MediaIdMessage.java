package com.zht.gzh.util;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class MediaIdMessage {
    @XStreamAlias("MediaId")
    @XStreamCDATA  
    private String MediaId;  
  
    public String getMediaId() {  
        return MediaId;  
    }  
  
    public void setMediaId(String mediaId) {  
        MediaId = mediaId;  
    }  
  
}  