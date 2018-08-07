package com.ydzq.hq.prod.netty.handler.thread;

import java.util.Calendar;
import java.util.Date;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ydzq.core.utils.DateUtils;
import com.ydzq.hq.enums.E_HqMarket;
import com.ydzq.hq.vo.hq.HqMinVo;
import com.ydzq.hq.vo.VoBase;

import io.netty.buffer.ByteBuf;

/**
 * 分时
 * 
 * @author f.w
 *
 */
public class HqMinHandlerRunnable extends HqBaseHanlerRunnable<HqMinVo> {

	public HqMinHandlerRunnable(KafkaProducer<LongSerializer, ByteArraySerializer> kafkaProducer, String topic)
			throws Exception {
		super(new ObjectMapper(), kafkaProducer, topic, LoggerFactory.getLogger(topic));
	}

	@Override
	protected HqMinVo decoder(ByteBuf buffer) throws Exception { 
		HqMinVo o = new HqMinVo();
		String code = parserString(buffer, 12);
		E_HqMarket mkt = E_HqMarket.getValue(buffer.readInt());// 市场
		VoBase relation = parseCode(code, o, mkt);

		// 股票以外不保存
		if (relation == null) {
			this.log("[HqMinVo]-"+mkt + " this code is not exist:" + code );
			return null;
		}

		Date time = parseTime(buffer.readInt(), mkt);
		double npreclosepx = parseDouble(buffer.readInt());// 昨收
		double nopenpx = parseDouble(buffer.readInt());// 开盘
		double nhighpx = parseDouble(buffer.readInt());// 最高
		double nlowpx = parseDouble(buffer.readInt());// 最低
		double nlastpx = parseDouble(buffer.readInt());// 最新
		long llvolume = buffer.readLong();// 成交量
		double llvalue = parseDouble(buffer.readLong());// 成交额

		o.setLcp(npreclosepx);
		o.setOp(nopenpx);
		o.setHp(nhighpx);
		o.setLp(nlowpx);
		o.setNp(nlastpx);
		o.setTa(llvolume);
		o.setTm(llvalue);
		o.setTime(DateUtils.format(time, "yyyyMMddHHmm"));

		o.setHt(time);// 行情时间
		o.setPt(Calendar.getInstance().getTime());

		return o;
	}
}
