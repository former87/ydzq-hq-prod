package com.ydzq.hq.prod.netty.handler.thread;

import java.util.Calendar;
import java.util.Date;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ydzq.hq.enums.E_HqMarket;
import com.ydzq.hq.vo.hq.HqDealVo;
import com.ydzq.hq.vo.VoBase;

import io.netty.buffer.ByteBuf;

/**
 * 交易
 * 
 * @author f.w
 *
 */
public class HqDealHandlerRunnable extends HqBaseHanlerRunnable<HqDealVo> {

	public HqDealHandlerRunnable(KafkaProducer<LongSerializer, ByteArraySerializer> kafkaProducer, String topic)
			throws Exception {
		super(new ObjectMapper(), kafkaProducer, topic, LoggerFactory.getLogger(topic));
	}

	@Override
	protected HqDealVo decoder(ByteBuf buffer) throws Exception { 
		
		HqDealVo o = new HqDealVo();
		String code = parserString(buffer, 12);
		E_HqMarket mkt = E_HqMarket.getValue(buffer.readInt());// 市场
		VoBase relation = parseCode(code, o, mkt);

		// 股票以外不保存
		if (relation == null) {
			this.log("[HqDealVo]-"+mkt + " this code is not exist:" + code );
			return null;
		}

		Date time = parseTime(buffer.readInt(), mkt);
		double npx = parseDouble(buffer.readInt());// 成交价
		double nvwap = parseDouble(buffer.readInt());// 加权成交价
		int ntradeuniqueid = buffer.readInt();// 成交编号
		long iisize = buffer.readLong();// 逐笔成交量
		double iivalue = parseDouble(buffer.readLong());// 逐笔成交额
		long iivolume = buffer.readLong();// 当日成交总量
		String ctradetype = parserString(buffer, 1);// 逐笔成交类型B为买，S为卖
		String ctradetrend = parserString(buffer, 8);// 不用
		String ctradetick = parserString(buffer, 1);// 不用

		o.setnPx(npx);
		o.setnVWAp(nvwap);
		o.setnTradeUniqueID(ntradeuniqueid);
		o.setLlSize(iisize);
		o.setLlValue(iivalue);
		o.setLlVolume(iivolume);
		o.setcTradeType(ctradetype);
		o.setcTradeTrend(ctradetrend);
		o.setcTradeTick(ctradetick);

		o.setHt(time);// 行情时间
		o.setPt(Calendar.getInstance().getTime());
		return o;
	}
}
