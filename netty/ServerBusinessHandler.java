package com.example.boottest.demo.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理建立的连接；
 * 处理 解码器 识别出来的帧。
 *
 * @author Guan
 * @date 2017/10/5
 */
public class ServerBusinessHandler extends ChannelDuplexHandler {

    private static Logger logger = LoggerFactory.getLogger(ServerBusinessHandler.class);


    public ServerBusinessHandler() {

    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        /**
         * 建立连接，回应Client
         */
        String welcomMsg = "Welcome to Server! Client: " + ctx.channel().remoteAddress().toString();
        ByteBuf resp = Unpooled.copiedBuffer(welcomMsg.getBytes());
        logger.info(welcomMsg);
        ctx.writeAndFlush(resp);

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        CustomFrame customFrame = CustomFrame.parseCustomFrame();
//        byte[] responseBytes = frameProcessor.process(ctx.channel(), customFrame);
//        ByteBuf resp = Unpooled.copiedBuffer(responseBytes);
//        ctx.writeAndFlush(resp);
    }

    /**
     * 关闭连接，打印消息
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        String leaveMsg = "Leave Server! Client: " + ctx.channel().remoteAddress().toString();
        logger.info(leaveMsg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //心跳检测
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                //未进行读操作
                logger.error("心跳超时：未收到客户端数据，关闭连接 " + ctx.channel().remoteAddress());
                ctx.close();
            }
        }
    }


    /**
     * 发生异常
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
        //关闭连接成功之后，会调用 channelInactive()
        String closeMsg = "发生异常，关闭连接Client: " + ctx.channel().remoteAddress().toString();
        logger.info(closeMsg);
    }


}
