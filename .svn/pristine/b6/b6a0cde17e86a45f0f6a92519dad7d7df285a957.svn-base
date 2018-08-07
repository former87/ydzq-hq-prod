package com.ydzq.hq.prod.netty.handler.thread;
 
import org.redisson.api.RLock;
import org.slf4j.Logger;
import com.ydzq.hq.enums.E_HqMarket;
import com.ydzq.hq.prod.config.ServerConfig;
import com.ydzq.hq.prod.global.AppConstrants;
import com.ydzq.hq.prod.netty.client.NettyClientPool;
import com.ydzq.hq.prod.netty.client.NettyClientPoolProxy;
import com.ydzq.hq.prod.netty.handler.HqClientHandler;
import com.ydzq.hq.prod.netty.protocol.SocketVo;
import com.ydzq.hq.prod.netty.protocol.SocketVo.Address;
import com.ydzq.hq.prod.service.BaseService;

public class ElectionRunnable implements Runnable {
	Logger logger = null;
	E_HqMarket mkt = null;
	BaseService baseService = null;
	HqClientHandler hqClientHandler = null;
	SocketVo socket = null;
	String produce = null;

	public ElectionRunnable(E_HqMarket mkt, SocketVo socket, BaseService baseService, HqClientHandler hqClientHandler,
			ServerConfig serverConfig, Logger logger) {
		this.mkt = mkt;
		this.socket = socket;
		this.baseService = baseService;
		this.hqClientHandler = hqClientHandler;
		this.logger = logger;
		this.produce = serverConfig.getHost() + ":" + serverConfig.getPort();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					logger.info("socket:[" + socket + "] is remove");
					baseService.removeProduce();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	public BaseService getBaseService() {
		return baseService;
	}

	public SocketVo getSocketVo() {
		return socket;
	}

	public HqClientHandler getHqClientHandler() {
		return this.hqClientHandler;
	}

	@Override
	public void run() {
		Boolean isLoad = false;
		try {
			baseService.init();
			baseService.setProduceLast(produce);
			baseService.loadStk();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while (true) {
			RLock lock = null;
			try {

				lock = baseService.tryLock(AppConstrants.SOCKET.HK.LOCKNAME);
				if (lock != null) {

					String name = baseService.getProduce();
					boolean isMaster = produce.equals(name);

					// 判断自己是否是master 是，监控hk连接池状态
					if (isMaster) {

						if (!isLoad) {
							init();
							isLoad = true;
						}

						for (Address i : socket.getSockets()) {
							try {

								NettyClientPoolProxy proxy = NettyClientPool.getPool(i.getHost(), i.getPort());
								if (proxy != null && proxy.isLive() != null && !proxy.isLive()) {
									shutdown();
									isMaster = isLoad = false;
									baseService.removeProduce();
									logger.info("socket:[" + socket + "] is Election");
									break;
								}

							} catch (Exception e) {
								logger.error("[" + i + "] " + mkt.toString(), e);
							}
						}

					} else {
						baseService.setProduceLast(produce);
					}

					logger.info("socket:[" + socket + "] is master:" + isMaster + "");

				} else
					logger.info("socket:[" + socket + "] is not get lock");

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (lock != null) {
					lock.unlock();
					logger.info("socket:[" + socket + "] " + Thread.currentThread().getName() + "unlock sucess");
				}
			}

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	void init() throws Exception {
		logger.info("=================socket:[" + socket + "] is load=================");
		createMktSocket(socket, hqClientHandler);
		// socket共享此handler so，handler单独启动
		hqClientHandler.init();
	}

	void shutdown() throws Exception {
		logger.info("=================socket:[" + socket + "] is begin shutdown=================");
		for (Address i : socket.getSockets()) {
			try {
				NettyClientPool.removePool(i);
			} catch (Exception e) {
				logger.error("[" + i + "] " + socket, e);
			}
		}
		hqClientHandler.shutdown();
		logger.info("=================socket:[" + socket + "] is end shutdown=================");
	}

	void createMktSocket(SocketVo socket, HqClientHandler hqClientHandler) throws Exception {
		if (socket != null) {
			for (SocketVo.Address i : socket.getSockets()) {
				NettyClientPool.createPool(i, hqClientHandler);
				logger.info("socket:[" + i.toString() + "] is register");
			}
		}
	}
}