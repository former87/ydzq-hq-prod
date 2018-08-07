package com.ydzq.hq.prod.netty.handler.thread;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ydzq.hq.prod.global.AppConstrants;
import com.ydzq.hq.vo.HqBase;

import io.netty.buffer.ByteBuf;

/**
 * 快照
 * 
 * @author f.w
 *
 */
@SuppressWarnings("rawtypes")
public class HqMonitorRunnable extends HqBaseHanlerRunnable {

	private ConcurrentHashMap<Integer, HqBaseHanlerRunnable> handlers;
	private Logger logger = null;

	@SuppressWarnings("unchecked")
	public HqMonitorRunnable(ConcurrentHashMap<Integer, HqBaseHanlerRunnable> handlers, String topic) {
		logger = LoggerFactory.getLogger(topic);
		this.handlers = handlers;
		this.topic = topic;
	}

	@Override
	public void run() {

		while (this.isTrue) {
			logger.info("-------------------------------------");
			for (HqBaseHanlerRunnable r : handlers.values()) {
				// log.info("topic[name:" + r.topic + " quote:" + r.countBb() +
				// "]");
				logger.info(r.countBb());
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				logger.error(AppConstrants.SOCKET.HK.MONITOR + " is stop");
				break;
			}
		}
		logger.info("monitor is close ");
	}

	@Override
	protected HqBase decoder(ByteBuf wrap) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
