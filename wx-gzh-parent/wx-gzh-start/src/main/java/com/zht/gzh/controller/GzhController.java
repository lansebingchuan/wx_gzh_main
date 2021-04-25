package com.zht.gzh.controller;

import com.alibaba.fastjson.JSONObject;
import com.thoughtworks.xstream.XStream;
import com.zht.gzh.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * <p> 公众号控制器 </p>
 *
 * @author: ZHT
 * @create: 2021-02-04 16:30
 **/
@RestController
@Api(value = "公众号控制器", description = "请求测试公众号功能")
public class GzhController {

    @Autowired
    private RestTemplate restTemplate;

    private static String token = "123";

    /**
     * 接口调用tocken
     */
    private static String ACCESS_TOKEN = "";

    /**
     * ACCESS_TOKEN 存活时间
     */
    private static Long EXPIRES_IN = 0L;

    @GetMapping("/checkToken")
    @ApiOperation(value = "检验Token信息")
    public String checkToken(String signature, String timestamp, String nonce, String echostr, String ToUserName, HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("signature: " + signature + "    timestamp: " + timestamp + "     nonce: " + nonce + "        echostr: " + echostr + "        ToUserName: " + ToUserName);
        acceptMessage(request, response);
        return echostr;
    }

    private void acceptMessage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 处理接收消息
        ServletInputStream in = request.getInputStream();
        // 将POST流转换为XStream对象
        XStream xs = SerializeXmlUtil.createXstream();
        xs.processAnnotations(InputMessage.class);
        xs.processAnnotations(OutputMessage.class);
        // 将指定节点下的xml节点数据映射为对象
        xs.alias("xml", InputMessage.class);
        // 将流转换为字符串
        StringBuilder xmlMsg = new StringBuilder();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1;) {
            xmlMsg.append(new String(b, 0, n, "UTF-8"));
        }
        // 将xml内容转换为InputMessage对象
        System.out.println("原始记录：" + xmlMsg.toString());
        InputMessage inputMsg = (InputMessage) xs.fromXML(xmlMsg.toString());

        String servername = inputMsg.getToUserName();// 服务端
        String custermname = inputMsg.getFromUserName();// 客户端
        long createTime = inputMsg.getCreateTime();// 接收时间
        Long returnTime = Calendar.getInstance().getTimeInMillis() / 1000;// 返回时间

        // 取得消息类型
        String msgType = inputMsg.getMsgType();
        // 根据消息类型获取对应的消息内容
        if (msgType.equals(MsgType.Text.toString())) {
            // 文本消息
            System.out.println("开发者微信号：" + inputMsg.getToUserName());
            System.out.println("发送方帐号：" + inputMsg.getFromUserName());
            System.out.println("消息创建时间：" + inputMsg.getCreateTime() + new Date(createTime * 1000l));
            System.out.println("消息内容：" + inputMsg.getContent());
            System.out.println("消息Id：" + inputMsg.getMsgId());

            StringBuffer str = new StringBuffer();
            str.append("<xml>");
            str.append("<ToUserName><![CDATA[" + custermname + "]]></ToUserName>");
            str.append("<FromUserName><![CDATA[" + servername + "]]></FromUserName>");
            str.append("<CreateTime>" + returnTime + "</CreateTime>");
            str.append("<MsgType><![CDATA[" + msgType + "]]></MsgType>");
            str.append("<Content><![CDATA[你说的是：" + inputMsg.getContent() + "，吗？]]></Content>");
            str.append("</xml>");
            System.out.println(str.toString());
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(str.toString());
        }
        // 获取并返回多图片消息
        if (msgType.equals(MsgType.Image.toString())) {
            System.out.println("获取多媒体信息");
            System.out.println("多媒体文件id：" + inputMsg.getMediaId());
            System.out.println("图片链接：" + inputMsg.getPicUrl());
            System.out.println("消息id，64位整型：" + inputMsg.getMsgId());

            OutputMessage outputMsg = new OutputMessage();
            outputMsg.setFromUserName(servername);
            outputMsg.setToUserName(custermname);
            outputMsg.setCreateTime(returnTime);
            outputMsg.setMsgType(msgType);
            ImageMessage images = new ImageMessage();
            images.setMediaId(inputMsg.getMediaId());
            outputMsg.setImage(images);
            System.out.println("xml转换：/n" + xs.toXML(outputMsg));
            response.getWriter().write(xs.toXML(outputMsg));

        }
        //订阅消息
        if (msgType.equals(MsgType.EVENT.toString())) {
            String event = inputMsg.getEvent();
            //订阅消息
            if (event.equals(MsgType.SUBSCRIBE.toString())) {
                System.out.println("订阅微信号：" + inputMsg.getToUserName());
                System.out.println("订阅帐号：" + inputMsg.getFromUserName());
                System.out.println("订阅时间：" + inputMsg.getCreateTime() + new Date(createTime * 1000l));
                StringBuffer str = new StringBuffer();
                str.append("<xml>");
                str.append("<ToUserName><![CDATA[" + custermname + "]]></ToUserName>");
                str.append("<FromUserName><![CDATA[" + servername + "]]></FromUserName>");
                str.append("<CreateTime>" + returnTime + "</CreateTime>");
                str.append("<MsgType><![CDATA[" + msgType + "]]></MsgType>");
                str.append("<Content><![CDATA[欢迎您!]]></Content>");
                str.append("</xml>");
                System.out.println(str.toString());
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(str.toString());

            }
            //取消订阅消息
            if (event.equals(MsgType.UNSUBSCRIBE.toString())) {
                System.out.println("取消订阅微信号：" + inputMsg.getToUserName());
                System.out.println("取消订阅帐号：" + inputMsg.getFromUserName());
                System.out.println("取消时间：" + inputMsg.getCreateTime() + new Date(createTime * 1000l));

            }
            //发送模板回调
            if (event.equals("TEMPLATESENDJOBFINISH")) {
                String status = inputMsg.getStatus();
                if ("success".equals(status)){
                    System.out.println("推送模板成功！");
                }
                System.out.println("发送模板回调微信号：" + inputMsg.getToUserName());
                System.out.println("发送模板回调帐号：" + inputMsg.getFromUserName());
                System.out.println("发送模板回调时间：" + inputMsg.getCreateTime() + new Date(createTime * 1000l));

            }
        }

    }

    @GetMapping("/accessToken")
    @ApiOperation(value = "获取access_token信息")
    public String getToken(){
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx22c69b9b1b45d748&secret=986dc2a6417e02e2cc6d41354c742a3b";
        ResponseEntity<JSONObject> forEntity = restTemplate.getForEntity(url, JSONObject.class);
        JSONObject body = forEntity.getBody();
        //访问接口 tocken
        GzhController.ACCESS_TOKEN = body.getString("access_token");
        //存活时间
        GzhController.EXPIRES_IN = body.getLong("expires_in");
        return  body.toJSONString();
    }

    /**
     * 获取所有的模块信息
     *
     * @return 所有的模块信息
     */
    @GetMapping("/getAllPrivateTemplate")
    @ApiOperation(value = "获取所有的模板信息")
    public String getAllPrivateTemplate(){
        String url = "https://api.weixin.qq.com/cgi-bin/template/get_all_private_template?access_token=" + GzhController.ACCESS_TOKEN;
        ResponseEntity<JSONObject> forEntity = restTemplate.getForEntity(url, JSONObject.class);
        JSONObject body = forEntity.getBody();
        return  body.toJSONString();
    }

    /**
     * 发送模板
     *
     * @param templateId 模板id
     * @param touser 接收人
     *
     * @return 发送结果
     */
    @GetMapping("/sendTemplate")
    @ApiOperation(value = "给用户发送模板信息")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "模板id", name = "templateId", paramType = "query", dataType = "String"),
            @ApiImplicitParam(value = "接收人微信号", name = "touser", paramType = "query", dataType = "String")
    })
    public String sendTemplate(@RequestParam("templateId") String templateId, @RequestParam("touser") String touser){
        String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + GzhController.ACCESS_TOKEN;
        JSONObject msg = new JSONObject();
        msg.put("touser", touser);
        msg.put("template_id", templateId);
        msg.put("url", "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx520c15f417810387&redirect_uri=https%3A%2F%2Fchong.qq.com%2Fphp%2Findex.php%3Fd%3D%26c%3DwxAdapter%26m%3DmobileDeal%26showwxpaytitle%3D1%26vb2ctag%3D4_2030_5_1194_60&response_type=code&scope=snsapi_base&state=123#wechat_redirect");
        JSONObject data = new JSONObject();
        msg.put("data", data);

        //第一个值
        JSONObject first = new JSONObject();
        first.put("value", "恭喜你！\r\n");
        first.put("color", "#173177");
        data.put("result", first);

        first = new JSONObject();
        first.put("value", "-100\r\n");
        first.put("color", "#173177");
        data.put("withdrawMoney", first);

        first = new JSONObject();
        first.put("value", "2021-02-04\r\n");
        first.put("color", "#173177");
        data.put("withdrawTime", first);


        first = new JSONObject();
        first.put("value", "中国建设银行\r\n");
        first.put("color", "#173177");
        data.put("cardInfo", first);


        first = new JSONObject();
        first.put("value", "2021-02-05\r\n");
        first.put("color", "#173177");
        data.put("arrivedTime", first);

        System.out.println(msg.toJSONString());
        JSONObject jsonObject = restTemplate.postForObject(url, msg, JSONObject.class);
        return jsonObject.toJSONString();
    }
}
