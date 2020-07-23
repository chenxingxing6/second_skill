package com.lxh.seckill.websocket;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * created by lanxinghua@2dfire.com on 2020/7/21
 */
@ServerEndpoint("/websocket/{sid}")
@Component
public class WebSocketServer {
    private static AtomicInteger onlineNum = new AtomicInteger();
    private static ConcurrentHashMap<String, Session> sessionPools = new ConcurrentHashMap<>();

    public void sendMsg(String msg){
        for (Session value : sessionPools.values()) {
            try {
                sendMessage(value, msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //发送消息
    public void sendMessage(Session session, String message) throws IOException {
        if (session != null) {
            synchronized (session) {
                session.getBasicRemote().sendText(message);
            }
        }
    }

    //建立连接成功调用
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "sid") String userName){
        sessionPools.put(userName, session);
        addOnlineCount();
        System.out.println(userName + "加入webSocket！当前人数为" + onlineNum);
//        try {
//            for (int i = 0; i < 100; i++) {
//                TimeUnit.MILLISECONDS.sleep(500);
//                sendMessage(session, "欢迎" + userName + "加入连接！");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    //关闭连接时调用
    @OnClose
    public void onClose(@PathParam(value = "sid") String userName){
        sessionPools.remove(userName);
        subOnlineCount();
        System.out.println(userName + "断开webSocket连接！当前人数为" + onlineNum);
    }

    //收到客户端信息
    @OnMessage
    public void onMessage(String message) throws IOException {
        message = "客户端：" + message + ",已收到";
        System.out.println(message);
        for (Session session: sessionPools.values()) {
            try {
                sendMessage(session, message);
            } catch(Exception e){
                e.printStackTrace();
                continue;
            }
        }
    }

    //错误时调用
    @OnError
    public void onError(Session session, Throwable throwable){
        System.out.println("发生错误");
        throwable.printStackTrace();
    }

    public static void addOnlineCount(){
        onlineNum.incrementAndGet();
    }

    public static void subOnlineCount() {
        onlineNum.decrementAndGet();
    }
}
