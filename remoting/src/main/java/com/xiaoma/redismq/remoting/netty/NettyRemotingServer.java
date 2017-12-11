package com.xiaoma.redismq.remoting.netty;

import com.sun.org.apache.xpath.internal.SourceTree;
import com.xiaoma.redismq.common.util.CommonUtil;
import com.xiaoma.redismq.remoting.listen.ChannelEventListener;
import com.xiaoma.redismq.remoting.process.RemotingCommand;
import com.xiaoma.redismq.remoting.serialize.protostuff.ProtostuffCodecUtil;
import com.xiaoma.redismq.remoting.serialize.protostuff.ProtostuffDecoder;
import com.xiaoma.redismq.remoting.serialize.protostuff.ProtostuffEncoder;
import com.xiaoma.redismq.remoting.util.RemotingUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NettyRemotingServer extends NettyRemotingBaseService implements RemotingServer{

    private ServerBootstrap bootstrap;

    private EventLoopGroup boosGroup;

    private EventLoopGroup workGroup;

    private EventExecutorGroup eventGroup;

    private NettyServerConfig nettyConfig;

    public NettyRemotingServer(NettyServerConfig nettyConfig, ChannelEventListener eventListener) {
        this.nettyConfig = nettyConfig;
        this.eventListener = eventListener;
        this.bootstrap = new ServerBootstrap();
        this.boosGroup = new NioEventLoopGroup(1, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("NettyBoss_%d", this.threadIndex.incrementAndGet()));
            }
        });

        //如果在linux平台，并且设置了使用epoll，则netty selector使用epoll
        if (CommonUtil.isLinuxPlatForm() //
                && nettyConfig.isUseEpollNativeSelector()) {
            this.workGroup = new EpollEventLoopGroup(nettyConfig.getServerSelectorThreads(), new ThreadFactory() {
                private AtomicInteger threadIndex = new AtomicInteger(0);
                private int threadTotal = nettyConfig.getServerSelectorThreads();

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, String.format("NettyServerEPOLLSelector_%d_%d", threadTotal, this.threadIndex.incrementAndGet()));
                }
            });
        } else {
            this.workGroup = new NioEventLoopGroup(nettyConfig.getServerSelectorThreads(), new ThreadFactory() {
                private AtomicInteger threadIndex = new AtomicInteger(0);
                private int threadTotal = nettyConfig.getServerSelectorThreads();

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, String.format("NettyServerNIOSelector_%d_%d", threadTotal, this.threadIndex.incrementAndGet()));
                }
            });
        }

        this.eventGroup = new DefaultEventExecutorGroup(
                nettyConfig.getServerWorkerThreads(),
                new ThreadFactory() {

                    private AtomicInteger threadIndex = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "NettyServerCodecThread_" + this.threadIndex.incrementAndGet());
                    }
                });
    }

    @Override
    public void start() {
        bootstrap.group(boosGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                //服务端接受连接的队列长度1024
                .option(ChannelOption.SO_BACKLOG, 1024)
                //地址复用，一般来说，一个端口释放后会等待两分钟之后才能再被使用，SO_REUSEADDR是让端口释放后立即就可以被再次使用
                .option(ChannelOption.SO_REUSEADDR, true)
                //默认心跳
                .option(ChannelOption.SO_KEEPALIVE, false)
                //TCP参数，立即发送数据，默认值为Ture
                .childOption(ChannelOption.TCP_NODELAY, true)
                //TCP数据发送缓冲区大小
                .option(ChannelOption.SO_SNDBUF, nettyConfig.getServerSocketSndBufSize())
                //TCP数据接收缓冲区大小
                .option(ChannelOption.SO_RCVBUF, nettyConfig.getServerSocketRcvBufSize())
                .localAddress(new InetSocketAddress(this.nettyConfig.getListenPort()))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ProtostuffCodecUtil util = new ProtostuffCodecUtil();
                        util.setRpcDirect(true);
                        ch.pipeline().addLast(
                                eventGroup,
                                new ProtostuffEncoder(util),
                                new ProtostuffDecoder(util),
                                new IdleStateHandler(0, 0, nettyConfig.getServerChannelMaxIdleTimeSeconds()),
                                //管理心跳,连接等信息
                                new NettyConnetManageHandler(),
                                new NettyServerHandler()
                        );
                    }
                });
        //ChannelFuture sync = null;
        try {
            ChannelFuture sync = this.bootstrap.bind().sync();
            //InetSocketAddress addr = (InetSocketAddress) sync.channel().localAddress();
            //this.port = addr.getPort();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void registerProcessor(int requestCode, NettyRequestProcessor processor, ExecutorService executor) {

    }

    public void registerProcessor(NettyRequestProcessor processor) {
        this.nettyProcessor = processor;
    }

    @Override
    public void registerDefaultProcessor(NettyRequestProcessor processor, ExecutorService executor) {

    }

    @Override
    public int localListenPort() {
        return 0;
    }

    @Override
    public RemotingCommand invokeSync(Channel channel, RemotingCommand request, long timeoutMillis) throws InterruptedException {
        return this.invokeSyncImpl(channel, request, timeoutMillis);
    }

    @Override
    public void invokeAsync(Channel channel, RemotingCommand request, long timeoutMillis, InvokeCallback invokeCallback) {

    }

    @Override
    public void invokeOneway(Channel channel, RemotingCommand request, long timeoutMillis) {

    }

    class NettyServerHandler extends SimpleChannelInboundHandler<RemotingCommand> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, RemotingCommand msg) throws Exception {
            System.out.println(msg.toString());
            processMessageReceived(ctx, msg);
        }
    }

    class NettyConnetManageHandler extends ChannelDuplexHandler {
        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            //final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
            System.out.println("channelRegistered");
            super.channelRegistered(ctx);
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            //final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
            System.out.println("channelUnregistered");

            super.channelUnregistered(ctx);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("channelActive");

            final String remoteAddress = CommonUtil.parseChannelRemoteAddr(ctx.channel());
            super.channelActive(ctx);

            if (NettyRemotingServer.this.eventListener != null) {
                NettyRemotingServer.this.putNettyEvent(NettyEventType.CONNECT, remoteAddress.toString(), ctx.channel());
            }
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("channelInactive");

            final String remoteAddress = CommonUtil.parseChannelRemoteAddr(ctx.channel());
            super.channelInactive(ctx);

            if (NettyRemotingServer.this.eventListener != null) {
                NettyRemotingServer.this.putNettyEvent(NettyEventType.CLOSE, remoteAddress.toString(), ctx.channel());
            }
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent evnet = (IdleStateEvent) evt;
                if (evnet.state().equals(IdleState.ALL_IDLE)) {
                    final String remoteAddress = CommonUtil.parseChannelRemoteAddr(ctx.channel());
                    RemotingUtil.closeChannel(ctx.channel());
                    if (NettyRemotingServer.this.eventListener != null) {
                        NettyRemotingServer.this
                                .putNettyEvent(NettyEventType.IDLE, remoteAddress.toString(), ctx.channel());
                    }
                }
            }

            ctx.fireUserEventTriggered(evt);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            final String remoteAddress = CommonUtil.parseChannelRemoteAddr(ctx.channel());

            if (NettyRemotingServer.this.eventListener != null) {
                NettyRemotingServer.this.putNettyEvent(NettyEventType.EXCEPTION, remoteAddress.toString(), ctx.channel());
            }

            RemotingUtil.closeChannel(ctx.channel());
        }
    }
}
