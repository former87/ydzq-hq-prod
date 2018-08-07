package com.ydzq.hq.prod.netty.client;

import java.util.concurrent.Callable;

import com.ydzq.hq.prod.netty.handler.HqClientHandler;
import com.ydzq.hq.prod.netty.protocol.SocketVo;


/**
 * netty工厂
 * 
 * @author f.w
 *
 */
public class NettyClientPoolFactory implements Callable<NettyClientPoolProxy> {

	private SocketVo.Address address;
	private HqClientHandler hqClientHandler;

	public NettyClientPoolFactory(SocketVo.Address address, HqClientHandler hqClientHandler) {
		this.address = address;
		this.hqClientHandler = hqClientHandler;
	}

	@Override
	public NettyClientPoolProxy call() throws Exception {
		NettyClientPoolProxy proxy = new NettyClientPoolProxy(this.address, this.hqClientHandler);
		proxy.createProxy();
		return proxy;
	}

}
