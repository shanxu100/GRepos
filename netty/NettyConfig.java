package com.example.boottest.demo.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Component;

/**
 * @author Guan
 * @date Created on 2018/10/25
 */
@Component //将这个JavaBean放入Spring容器
@Configuration
public class NettyConfig {

    @Value("${netty.port}")
    private int port;

    @Value("${netty.boss.count}")
    private int bossCount;

    @Value("${netty.worker.count}")
    private int workerCount;

    @Value("${netty.so.keep-alive}")
    private boolean soKeepAlive;

    @Value("${netty.tcp.no-delay}")
    private boolean tcpNoDelay;

    @Value("${netty.so.backlog}")
    private int backLog;

    @Value("${netty.heartbeat.timeout}")
    private int heartbeatTimeout;

    /**
     * 三个线程池
     */
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;
    private DefaultEventExecutorGroup bizGroup;


    @Bean(name = "nettyPort")
    public int getPort() {
        return port;
    }

    @Bean(name = "bossGroup")
    public NioEventLoopGroup getBossGroup() {
        return bossGroup;
    }

    @Bean(name = "workerGroup")
    public NioEventLoopGroup getWorkerGroup() {
        return workerGroup;
    }

    @Bean(name = "bizGroup")
    public DefaultEventExecutorGroup getBizGroup() {
        return bizGroup;
    }

    @SuppressWarnings("unchecked")
    @Bean(name = "serverBootstrap")
    public ServerBootstrap getBootstrap() {
        ServerBootstrap b = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup(bossCount);
        workerGroup = new NioEventLoopGroup(workerCount);
        bizGroup = new DefaultEventExecutorGroup(4);

        ServerChannelInitializer initializer = new ServerChannelInitializer(bizGroup, heartbeatTimeout);
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(initializer)
                .childOption(ChannelOption.SO_KEEPALIVE, soKeepAlive)
                .childOption(ChannelOption.TCP_NODELAY, tcpNoDelay);
        return b;
    }


    /**
     * Necessary to make the Value annotations work.
     *
     * @return
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}

