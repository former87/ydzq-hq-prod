package com.ydzq.hq.prod.config.mkt;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ydzq.hq.enums.E_HqMarket; 
import com.ydzq.hq.prod.config.ServerConfig;
import com.ydzq.hq.prod.global.AppConstrants;
import com.ydzq.hq.prod.netty.handler.mkt.HkClientWorkHandler;
import com.ydzq.hq.prod.netty.handler.thread.ElectionRunnable;
import com.ydzq.hq.prod.netty.handler.thread.HqDealHandlerRunnable;
import com.ydzq.hq.prod.netty.handler.thread.HqJjsHandlerRunnable;
import com.ydzq.hq.prod.netty.handler.thread.HqMinHandlerRunnable;
import com.ydzq.hq.prod.netty.handler.thread.HqSnapHandlerRunnable;
import com.ydzq.hq.prod.netty.handler.thread.HqTenHandlerRunnable;
import com.ydzq.hq.prod.netty.protocol.SocketVo;
import com.ydzq.hq.prod.service.CacheHkService;

@Configuration
@ConfigurationProperties(prefix = "netty.address")
public class HkThreadConfig {

	private String hk;

	public String getHk() {
		return hk;
	}

	public void setHk(String hk) {
		this.hk = hk;
	}

	@Resource
	private KafkaProducer<LongSerializer, ByteArraySerializer> kafkaProducer;
	@Resource
	private HkClientWorkHandler hkClientWorkHandler;
	@Resource
	private CacheHkService cacheHkService;
	@Resource
	private ServerConfig serverConfig;

	@Bean(name = AppConstrants.SOCKET.HK.SNAP)
	public HqSnapHandlerRunnable createSnap() throws Exception {
		return new HqSnapHandlerRunnable(kafkaProducer, AppConstrants.SOCKET.HK.SNAP);
	}

	@Bean(name = AppConstrants.SOCKET.HK.TEN)
	public HqTenHandlerRunnable createTen() throws Exception {
		return new HqTenHandlerRunnable(kafkaProducer, AppConstrants.SOCKET.HK.TEN);
	}

	@Bean(name = AppConstrants.SOCKET.HK.MIN)
	public HqMinHandlerRunnable createMin() throws Exception {
		return new HqMinHandlerRunnable(kafkaProducer, AppConstrants.SOCKET.HK.MIN);
	}

	@Bean(name = AppConstrants.SOCKET.HK.DEAL)
	public HqDealHandlerRunnable createDeal() throws Exception {
		return new HqDealHandlerRunnable(kafkaProducer, AppConstrants.SOCKET.HK.DEAL);
	}

	@Bean(name = AppConstrants.SOCKET.HK.JJS)
	public HqJjsHandlerRunnable createJjs() throws Exception {
		return new HqJjsHandlerRunnable(kafkaProducer, AppConstrants.SOCKET.HK.JJS);
	}

	@Bean(name = AppConstrants.SOCKET.HK.PRODUCENAME)
	public ElectionRunnable createElection() throws Exception {
		if (StringUtils.isNotBlank(hk)) {
			Logger logger = LoggerFactory.getLogger(HkThreadConfig.class);
			E_HqMarket mkt = E_HqMarket.HK;
			SocketVo socket = new SocketVo(mkt, hk);
			return new ElectionRunnable(mkt, socket, cacheHkService, hkClientWorkHandler, serverConfig, logger);
		} else
			return null;
	}

}
