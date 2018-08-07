package com.ydzq.hq.prod.netty.handler.thread;

import java.util.Calendar;
import java.util.Date;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ydzq.hq.enums.E_HqMarket;
import com.ydzq.hq.vo.hq.HqSnapVo;
import com.ydzq.hq.vo.VoBase;

import io.netty.buffer.ByteBuf;

/**
 * 快照
 * 
 * @author f.w
 *
 */
public class HqSnapHandlerRunnable extends HqBaseHanlerRunnable<HqSnapVo> {

	public HqSnapHandlerRunnable(KafkaProducer<LongSerializer, ByteArraySerializer> kafkaProducer, String topic)
			throws Exception {
		super(new ObjectMapper(), kafkaProducer, topic, LoggerFactory.getLogger(topic));
	}

	@Override
	protected HqSnapVo decoder(ByteBuf buffer) throws Exception {
		HqSnapVo o = new HqSnapVo();
		String code = parserString(buffer, 12);
		E_HqMarket mkt = E_HqMarket.getValue(buffer.readInt());// 市场
		VoBase relation = parseCode(code, o, mkt);
		// 股票以外不保存
		if (relation == null) {
			this.log("[HqSnapVo]-" + mkt + " this code is not exist:" + code);
			return null;
		}

		int ty = buffer.readInt();// 交易时段
		Date time = parseTime(buffer.readInt(), mkt);
		double nPreClosePx = parseDouble(buffer.readInt());
		double nOpenPx = parseDouble(buffer.readInt());
		double nHighPx = parseDouble(buffer.readInt());
		double nLowPx = parseDouble(buffer.readInt());
		double nLastPx = parseDouble(buffer.readInt());
		double llValue = parseDouble(buffer.readLong());
		long llTradeNum = buffer.readLong();
		long llVolume = buffer.readLong();
		long ll5ayMeanVol = buffer.readLong();
		long ll10MeanVol = buffer.readLong();
		double nPxChg = parseDouble(buffer.readInt());
		double nPxChgRatio = parseDouble(buffer.readInt());
		int nTradingStatus = buffer.readInt();

		o.setTy(ty);// 交易时间段美股字段，港股没用
		o.setLcp(nPreClosePx); // 昨收价
		o.setOp(nOpenPx); // 开盘价
		o.setHp(nHighPx); // 最高价
		o.setLp(nLowPx); // 最低价
		o.setNp(nLastPx); // 最新价
		o.setTm(llValue); // 成交额
		o.setTp(llTradeNum); // 成交笔数
		o.setTa(llVolume); // 成交量
		o.setTa5(ll5ayMeanVol); // 成交量
		o.setTa10(ll10MeanVol); // 成交量
		o.setHlpm(nPxChg);// 涨跌额
		o.setHlp(nPxChgRatio);// 涨跌幅
		o.setOd(nTradingStatus);// 停牌状态

		o.setHt(time);// 行情时间
		o.setPt(Calendar.getInstance().getTime());

		return o;
	}
}
