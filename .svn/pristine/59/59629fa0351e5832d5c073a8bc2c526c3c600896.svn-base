package com.ydzq.hq.prod.netty.handler.thread;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ydzq.hq.enums.E_HqMarket;
import com.ydzq.hq.prod.global.AppConstrants;
import com.ydzq.hq.prod.service.BaseService;
import com.ydzq.hq.vo.HqBase;
import com.ydzq.hq.vo.VoBase;

import io.netty.buffer.ByteBuf;

/**
 * 行情信息解码器
 * 
 * @author f.w
 *
 * @param <T>
 */
public abstract class HqBaseHanlerRunnable<T extends HqBase> implements Runnable {

	private Logger logger = null;
	private ConcurrentLinkedQueue<ByteBuf> collect = null;
	private ObjectMapper objectMapper = null;
	private KafkaProducer<LongSerializer, ByteArraySerializer> kafkaProducer = null;
	public String topic = null;
	public boolean isTrue = true;
	Logger log = LoggerFactory.getLogger(AppConstrants.SOCKET.HK.NOTEXISTS);
	AtomicInteger RECONNECT_COUNT = new AtomicInteger(0);

	public HqBaseHanlerRunnable() {

	}

	public HqBaseHanlerRunnable(ObjectMapper objectMapper,
			KafkaProducer<LongSerializer, ByteArraySerializer> kafkaProducer, String topic, Logger logger)
			throws Exception {
		if (objectMapper == null || kafkaProducer == null || StringUtils.isBlank(topic) || logger == null)
			throw new Exception("HqBaseHanlerRunable initializer is error");
		this.objectMapper = objectMapper;
		this.kafkaProducer = kafkaProducer;
		this.topic = topic;
		this.logger = logger;
	}

	public void addBb(ByteBuf bb) {
		if (collect == null) {
			collect = new ConcurrentLinkedQueue<ByteBuf>();
		}
		collect.offer(bb);
	}

	public void reset() {
		RECONNECT_COUNT = new AtomicInteger(0);
		collect = new ConcurrentLinkedQueue<ByteBuf>();
	}

	public String countBb() {
		int count = 0;
		if (collect != null) {
			count = collect.size();
		}
		String str = "topic[name:" + topic + " quote:" + count + " pollcount:" + RECONNECT_COUNT.get() + "]";
		logger.info(str);
		return str;
	}

	protected void log(String msg) {
		log.info(msg);
	}

	protected abstract T decoder(ByteBuf wrap) throws Exception;

	@Override
	public void run() {
		while (isTrue) {
			ByteBuf wrap = null;
			try {

				if (collect == null || collect.isEmpty()) {
					Thread.sleep(1000);
					continue;
				} else {

					wrap = collect.poll();
					RECONNECT_COUNT.getAndIncrement();
					if (wrap == null) {
						logger.info("++++++++++" + Thread.currentThread().getName() + " " + this.topic + wrap);
					} else {
						T e = this.decoder(wrap);

						if (e != null) {
							logger.info(e.toString());
						    sendKafka(e);
							e = null;
						}
					}
				}
			}
			// 没有直接处理这个异常跳出，是因为不知道是否有其他地方有这个异常
			// catch (InterruptedException e) {
			// logger.error(topic + " is stop");
			// break;
			// }
			catch (Exception e) {
				logger.error(topic + "error:", e);
			} finally {
				if (wrap != null) {
					wrap.release();
					// logger.info(wrap.refCnt()+"");
				}
			}
		}
		logger.info(Thread.currentThread().getName() + " " + topic + " is close");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	void sendKafka(T entity) {

		long time = System.currentTimeMillis();
		try {

			if (objectMapper == null) {
				objectMapper = new ObjectMapper();
			}

			byte[] sendArray = objectMapper.writeValueAsBytes(entity);
			long key = System.currentTimeMillis();
			Future<RecordMetadata> future = kafkaProducer
					.send(new ProducerRecord(topic, entity.getPartition(), key, sendArray));

			if (future != null && RECONNECT_COUNT.get() % 1000 == 0) {
				try {

					RecordMetadata rMetadata = future.get();
					logger.info("topic:" + rMetadata.topic() + " partition:" + rMetadata.partition() + " offset:"
							+ rMetadata.offset() + " time cost:" + (System.currentTimeMillis() - time) + ".");
					logger.debug("key:" + key + "contnet:" + new String(sendArray));

				} catch (InterruptedException e) {
					logger.error("future.get()", e);
				} catch (ExecutionException e) {
					logger.error("future.get()", e);
				}
			}

		} catch (Exception e) {
			logger.error("future.get()", e);
		}
	}

	double parseDouble(int o) {
		return BigDecimal.valueOf(o).divide(BigDecimal.valueOf(10000), 4, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	String parserString(ByteBuf buffer, int size) {
		try {
			byte[] bytes = new byte[size];
			buffer.readBytes(bytes, 0, size);
			return new String(bytes).trim();// 股票代码
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	double parseDouble(long o) {
		return BigDecimal.valueOf(o).divide(BigDecimal.valueOf(10000), 4, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	VoBase parseCode(String code, HqBase o, E_HqMarket market) throws Exception {
		String sc = code;

		VoBase relation = BaseService.getVoBase(market, sc);
		if (relation != null) {
			o.setSc(relation.getSc());
			o.setId(relation.getId());
			o.setSn(relation.getSn());
			o.setSg(relation.getSg());
			o.setSt(relation.getSt());
			o.setCu(relation.getCu());
			o.setMkt(market.getIndex());
			o.setOd(relation.getOd());
			o.setPartition(relation.getPartition());
		}
		return relation;
		// return new VoBase();
	}

	Date parseTime(long time, E_HqMarket market) throws Exception {
		if (market.equals(E_HqMarket.HK)) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(time * 1000);
			return calendar.getTime();
		}
		throw new Exception("mkt[" + market + "] isn't exists");
	}
}
