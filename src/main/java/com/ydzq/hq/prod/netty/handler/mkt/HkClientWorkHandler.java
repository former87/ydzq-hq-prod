package com.ydzq.hq.prod.netty.handler.mkt;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;
import com.ydzq.core.utils.ObjectParser;
import com.ydzq.hq.prod.global.AppConstrants;
import com.ydzq.hq.prod.netty.handler.HqClientHandler;
import com.ydzq.hq.prod.netty.handler.thread.HqBaseHanlerRunnable;
import com.ydzq.hq.prod.netty.handler.thread.HqDealHandlerRunnable;
import com.ydzq.hq.prod.netty.handler.thread.HqJjsHandlerRunnable;
import com.ydzq.hq.prod.netty.handler.thread.HqMinHandlerRunnable;
import com.ydzq.hq.prod.netty.handler.thread.HqMonitorRunnable;
import com.ydzq.hq.prod.netty.handler.thread.HqSnapHandlerRunnable;
import com.ydzq.hq.prod.netty.handler.thread.HqTenHandlerRunnable;

import io.netty.channel.ChannelHandler.Sharable;

@Component
@Sharable
public class HkClientWorkHandler extends HqClientHandler {

	@SuppressWarnings("rawtypes")
	@Override
	public ConcurrentHashMap<Integer, HqBaseHanlerRunnable> initPool() {

		handlers = new ConcurrentHashMap<Integer, HqBaseHanlerRunnable>();
		handlers.put(ObjectParser.parseInt(AppConstrants.SOCKET.HK.DEAL), hqDealDecoder);
		handlers.put(ObjectParser.parseInt(AppConstrants.SOCKET.HK.JJS), hqJjsDecoder);
		handlers.put(ObjectParser.parseInt(AppConstrants.SOCKET.HK.MIN), hqMinDecoder);
		handlers.put(ObjectParser.parseInt(AppConstrants.SOCKET.HK.SNAP), hqSnapDecoder);
		handlers.put(ObjectParser.parseInt(AppConstrants.SOCKET.HK.TEN), hqTenDecoder);
		handlers.put(ObjectParser.parseInt(AppConstrants.SOCKET.HK.MONITOR), new HqMonitorRunnable(
				new ConcurrentHashMap<Integer, HqBaseHanlerRunnable>(handlers), AppConstrants.SOCKET.HK.MONITOR));

		cachedThreadPool = Executors.newFixedThreadPool(handlers.size(), new ThreadFactory() {
			AtomicInteger atomic = new AtomicInteger();

			public Thread newThread(Runnable r) {
				return new Thread(r, "hk-threadpool-" + this.atomic.getAndIncrement());
			}
		});

		return handlers;
	}

	@Resource(name = AppConstrants.SOCKET.HK.SNAP)
	private HqSnapHandlerRunnable hqSnapDecoder;

	@Resource(name = AppConstrants.SOCKET.HK.TEN)
	private HqTenHandlerRunnable hqTenDecoder;

	@Resource(name = AppConstrants.SOCKET.HK.MIN)
	private HqMinHandlerRunnable hqMinDecoder;

	@Resource(name = AppConstrants.SOCKET.HK.DEAL)
	private HqDealHandlerRunnable hqDealDecoder;

	@Resource(name = AppConstrants.SOCKET.HK.JJS)
	private HqJjsHandlerRunnable hqJjsDecoder;

}
