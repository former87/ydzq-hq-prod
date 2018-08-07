package com.ydzq.hq.prod.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ydzq.hq.prod.global.AppConstrants;
import com.ydzq.hq.prod.netty.client.NettyClientPool;
import com.ydzq.hq.prod.netty.client.NettyClientPoolProxy;
import com.ydzq.hq.prod.netty.handler.thread.HqBaseHanlerRunnable;
import com.ydzq.hq.prod.netty.protocol.WrapProtocol;

/**
 * handler中心
 * 
 * @author f.w
 *
 */
public abstract class HqClientHandler extends HqChannelInboundHandler {
	protected Logger log = LoggerFactory.getLogger(HqClientHandler.class);
	protected ExecutorService cachedThreadPool = null;
	@SuppressWarnings("rawtypes")
	protected ConcurrentHashMap<Integer, HqBaseHanlerRunnable> handlers = null;

	@SuppressWarnings("rawtypes")
	protected abstract ConcurrentHashMap<Integer, HqBaseHanlerRunnable> initPool();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void init() {
		handlers = this.initPool();
		if (handlers != null) {
			for (HqBaseHanlerRunnable r : handlers.values()) {
				r.isTrue = true;
				cachedThreadPool.submit(r);
				if (!r.topic.equals(AppConstrants.SOCKET.HK.MONITOR)) {
					log.info(r.countBb() + " is load");
				}
			}
		} else
			log.info("[" + Thread.currentThread().getName() + "] HqClientHandler handlers is null");
	}

	public String pre(ChannelHandlerContext ctx) {
		InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
		String ip = insocket.getHostName();
		int port = insocket.getPort();
		return pre(ip, port);
	}

	private String pre(String ip, int port) {
		return "socketconnect[" + ip + ":" + port + "]-[" + Thread.currentThread().getName() + "] ";
	}

	@SuppressWarnings("rawtypes")
	public ConcurrentHashMap<Integer, HqBaseHanlerRunnable> getHandlers() {
		return handlers;
	}

	@SuppressWarnings("rawtypes")
	public void reset() {
		log.info("ClientWorkHandler is begin reset ");
		if (handlers != null) {
			for (HqBaseHanlerRunnable r : handlers.values()) {
				if (!r.topic.equals(AppConstrants.SOCKET.HK.MONITOR)) {
					log.info("reset " + r.countBb());
					r.reset();
				}
			}
		}
		log.info("ClientWorkHandler is end reset ");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void shutdown() {
		if (cachedThreadPool != null) {
			try {

				for (HqBaseHanlerRunnable r : handlers.values()) {
					r.isTrue = false;
				}

				cachedThreadPool.shutdown();

				// (所有的任务都结束的时候，返回TRUE)
				if (!cachedThreadPool.awaitTermination(0, TimeUnit.MILLISECONDS)) {
					// 超时的时候向线程池中所有的线程发出中断(interrupted)。
					cachedThreadPool.shutdownNow();
				}

			} catch (InterruptedException e) {
				// awaitTermination方法被中断的时候也中止线程池中全部的线程的执行。
				cachedThreadPool.shutdownNow();
			}

			reset();

			if (cachedThreadPool.isShutdown())
				cachedThreadPool = null;

			log.info("shutdown ClientWorkHandler true ");
		} else
			log.info("shutdown cachedThreadPool is null");
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		WrapProtocol wrap = null;
		try {
			wrap = (WrapProtocol) msg;
			wrap.setAddress(this.pre(ctx));
			log.info(wrap.toString());

			if ((wrap.getType() + "").equals(AppConstrants.SOCKET.HEART)) {
				return;
			}

			HqBaseHanlerRunnable handler = handlers.get(wrap.getType());
			if (handler != null) {
				handler.addBb(wrap.getBytebuf());
			}
			// 20171215调代码临时注视
			else
				throw new Exception("wrap[" + wrap + "] isn't type");
		} catch (Exception e) {
			throw e;
		} finally {
			// ReferenceCountUtil.release(msg);
			wrap = null;
			msg = null;
			// log.info(wrap.toString());
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		log.info(pre(ctx).concat("is coming"));
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		log.error(pre(ctx).concat("is exception"), cause);
		NettyClientPoolProxy proxy;
		try {
			InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
			String ip = insocket.getHostName();
			int port = insocket.getPort();
			log.info(pre(ip, port).concat("exception"));

			proxy = NettyClientPool.getPool(ip, port);
			if (proxy != null) {
				proxy.close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
		try {
			if (ctx != null) {
				InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
				String ip = insocket.getHostName();
				int port = insocket.getPort();
				log.info(pre(ip, port).concat("disconnect"));

				NettyClientPoolProxy proxy = NettyClientPool.getPool(ip, port);
				if (proxy != null) {
					proxy.reConnect();
				}
			}
		} catch (Exception e) {
			log.error("channel disconnect is error", e);
		}
	}

	@Override
	protected void handleReaderIdle(ChannelHandlerContext ctx) {
		try {

			InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
			String ip = insocket.getHostName();
			int port = insocket.getPort();
			log.info(pre(ip, port).concat("idle"));

			NettyClientPoolProxy proxy = NettyClientPool.getPool(ip, port);
			if (proxy != null) {
				proxy.close();
			}

		} catch (Exception e) {
			log.error("channel idle is error", e);
		}
	}

	@Override
	protected void channelRead0(ChannelHandlerContext arg0, ByteBuf arg1) throws Exception {
		// TODO Auto-generated method stub

	}

}
