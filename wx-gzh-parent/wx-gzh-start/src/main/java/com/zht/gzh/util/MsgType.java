package com.zht.gzh.util;

public enum  MsgType {
	Text("text"),  
    Image("image"),  
    Music("music"),  
    Video("video"),  
    Voice("voice"),  
    Location("location"),  
    Link("link"),
    SUBSCRIBE("subscribe"),
    UNSUBSCRIBE("unsubscribe"),
    EVENT("event");
    private String msgType = "";
  
    MsgType(String msgType) {  
        this.msgType = msgType;  
    }  
  
    /** 
     * @return the msgType 
     */  
    @Override  
    public String toString() {  
        return msgType;  
    }  
}