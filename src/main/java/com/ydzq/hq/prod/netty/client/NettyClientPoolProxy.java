package com.ydzq.hq.prod.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ydzq.hq.prod.global.AppConstrants;
import com.ydzq.hq.prod.netty.client.channel.HqChannelInitializer;
import com.ydzq.hq.prod.netty.handler.HqClientHandler;
import com.ydzq.hq.prod.netty.protocol.SocketVo;

/**
 * netty代理
 * 
 * @author f.w
 *
 */
public class NettyClientPoolProxy {
	private Logger logger = LoggerFactory.getLogger(NettyClientPoolProxy.class);

	private Channel channel;
	private Bootstrap bootstrap;
	AtomicInteger RECONNECT_COUNT = new AtomicInteger(0);

	private SocketVo.Address address;
	private HqClientHandler hqClientHandler;
	private Boolean isLive=null;

	public NettyClientPoolProxy(SocketVo.Address address, HqClientHandler hqClientHandler) {
		this.address = address;
		this.hqClientHandler = hqClientHandler;
	}

	public void createProxy() {
		RECONNECT_COUNT = new AtomicInteger(0);
		try {
			NioEventLoopGroup workGroup = new NioEventLoopGroup();
			bootstrap = new Bootstrap();
			bootstrap.group(workGroup);
			bootstrap.channel(NioSocketChannel.class);
			bootstrap.handler(new HqChannelInitializer(this.hqClientHandler));
			bootstrap.option(ChannelOption.TCP_NODELAY, true);
			bootstrap.option(ChannelOption.SO_REUSEADDR, true);
			bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
			bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000);

			reConnect();

		} catch (Exception e) {
			logger.error("socketconnect[" + address + "]-[" + Thread.currentThread().getName() + "] fail. error:", e);
			close();
			throw new RuntimeException(e);
		}
	}

	public void reConnect() {
		try {//没有切换程序前先单机跑
			if (RECONNECT_COUNT.getAndIncrement() >= AppConstrants.SOCKET.RECONNECT_COUNT ) {
				close();
				logger.info("socketconnect[" + address + "]-[" + Thread.currentThread().getName()
						+ "] fail. The number of retries[" + RECONNECT_COUNT.get() + "] has reached");
				isLive=false;
				return;
			}

			if (channel != null && channel.isActive()) {
				return;
			}

			ChannelFuture future = bootstrap.connect(address.getHost(), address.getPort());

			future.addListener(new ChannelFutureListener() {
				public void operationComplete(ChannelFuture futureListener) throws Exception {

					if (futureListener.isSuccess()) {
						channel = futureListener.channel();
						logger.info("socketconnect[" + address + "]-[" + Thread.currentThread().getName()
								+ "] successfully!");
						isLive=true;
					} else {
						logger.info(
								"socketconnect[" + address + "]-[" + Thread.currentThread().getName() + "] fail. retry "
										+ RECONNECT_COUNT + " after " + RECONNECT_COUNT.get() + " millisecond");

						futureListener.channel().eventLoop().schedule(new Runnable() {
							@Override
							public void run() {
								reConnect();
							}
						}, AppConstrants.SOCKET.RECONNECT_TIME, TimeUnit.SECONDS);
					}
				}
			});

		} catch (Exception e) {
			close();
			logger.error("socketconnect[" + address + "]-[" + Thread.currentThread().getName() + "] fail. error", e);
		}
	}
	
	public Boolean isLive()
	{
		return isLive;
	}

	public boolean isActive() {
		if (this.channel != null) {
			return this.channel.isActive();
		}
		return false;
	}

	public void close() {
		try {
			if (this.channel != null) {
				if (this.channel.isOpen()) {
					this.channel.close();
				}
			}
			logger.info("socketconnect[" + address + "]-[" + Thread.currentThread().getName() + "] close.");
		} catch (Exception e) {
			logger.error("socketconnect close[" + address + "]-[" + Thread.currentThread().getName() + "] fail. error",
					e);
		}
	}

	public void mandatoryClose() {
		this.RECONNECT_COUNT.addAndGet(AppConstrants.SOCKET.RECONNECT_COUNT);
		this.reConnect();
		logger.info("socketconnect[" + address + "]-[" + Thread.currentThread().getName() + "] mandatoryClose.");
		this.channel = null;
		this.bootstrap = null;
		this.hqClientHandler = null;
		this.RECONNECT_COUNT = null;
		this.address = null;
	}
}
