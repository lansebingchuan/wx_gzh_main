package com.zht.gzh.dto;

import lombok.Data;

@Data
public class BaseMessage {

    private String ToUserName;
    private String FromUserName;
    private long CreateTime;
    private String MsgType;
    private long MsgId;

}