package com.example.boottest.demo.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;


/**
 * 加载顺序 Constructor >> @Autowired >> @PostConstruct
 *
 * @author Guan
 * @date Created on 2018/10/25
 */


@Component
public class NettyServer {

    @Autowired
    @Qualifier("serverBootstrap")
    private ServerBootstrap serverBootstrap;

    @Autowired
    @Qualifier("nettyPort")
    private int port;

    @Autowired
    @Qualifier("bossGroup")
    private NioEventLoopGroup bossGroup;
    @Autowired
    @Qualifier("workerGroup")
    private NioEventLoopGroup workerGroup;
    @Autowired
    @Qualifier("bizGroup")
    private DefaultEventExecutorGroup bizGroup;


    private static Logger logger = LoggerFactory.getLogger(NettyServer.class);


    @Async
    public void start() {
        try {
            Channel channel = serverBootstrap.bind(port).sync().channel();
            logger.info("Netty 启动成功..." + Thread.currentThread().toString());
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error(logger.getName(), e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            bizGroup.shutdownGracefully();

        }
    }

    @Async
    @PreDestroy//这个有用吗？
    public void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        bizGroup.shutdownGracefully();
        logger.info("stop netty server..." + Thread.currentThread().toString());
    }

}
