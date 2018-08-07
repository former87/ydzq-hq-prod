package com.ydzq.hq.prod.netty.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ydzq.hq.prod.netty.handler.HqClientHandler; 
import com.ydzq.hq.prod.netty.protocol.SocketVo;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future; 

/**
 * netty线程池
 * 
 * @author f.w
 *
 */
@Component
public class NettyClientPool {
	private static ExecutorService cachedThreadPool = null;
	private static ConcurrentHashMap<String, NettyClientPoolProxy> clientPoolMap = null;
	private static Logger logger = LoggerFactory.getLogger(NettyClientPoolProxy.class);

	public static NettyClientPoolProxy createPool(SocketVo.Address address, HqClientHandler hqClientHandler)
			throws Exception {

		if (cachedThreadPool == null) {
			cachedThreadPool = Executors.newCachedThreadPool();
		}

		if (clientPoolMap == null)
			clientPoolMap = new ConcurrentHashMap<String, NettyClientPoolProxy>();

		String serverAddress = address.getHost() + ":" + address.getPort();
		if (serverAddress == null || serverAddress.trim().length() == 0) {
			throw new IllegalArgumentException("[" + address + "]  serverAddress is null");
		}

		Future<NettyClientPoolProxy> queue = cachedThreadPool
				.submit(new NettyClientPoolFactory(address, hqClientHandler));
		NettyClientPoolProxy clientPool = queue.get();
		clientPoolMap.put(serverAddress, clientPool);
		logger.info("NettyClientPool[" + address + "]-[" + Thread.currentThread().getName() + "]");
		return clientPool;
	}

	public static NettyClientPoolProxy getPool(String host, int port) throws Exception {

		String serverAddress = host + ":" + port;
		// valid serverAddress
		if (serverAddress == null || serverAddress.trim().length() == 0) {
			throw new IllegalArgumentException("[" + serverAddress + "]  serverAddress is null");
		}

		if (clientPoolMap == null)
			throw new IllegalArgumentException("[" + serverAddress + "] isn't create ");

		// get from pool
		NettyClientPoolProxy clientPool = clientPoolMap.get(serverAddress);
		if (clientPool != null) {
			return clientPool;
		}

		return null;
	}

	public static void removePool(SocketVo.Address address) throws Exception {
		NettyClientPoolProxy proxy = getPool(address.getHost(), address.getPort());
		if (proxy != null) {
			if (proxy.isActive()) {
				proxy.mandatoryClose();
			}
			clientPoolMap.remove(address.toString());
			
			logger.info("NettyClientPool[" + address + "]-[" + Thread.currentThread().getName() + "] is removePool");
		} else
			logger.info("NettyClientPool[" + address + "]-[" + Thread.currentThread().getName() + "] is null");
	}
}
