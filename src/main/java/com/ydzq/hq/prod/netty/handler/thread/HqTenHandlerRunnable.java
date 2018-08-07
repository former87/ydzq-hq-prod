package com.ydzq.hq.prod.netty.handler.thread;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ydzq.hq.enums.E_HqMarket;
import com.ydzq.hq.vo.hq.HqTenSubVo;
import com.ydzq.hq.vo.hq.HqTenVo;
import com.ydzq.hq.vo.VoBase;

import io.netty.buffer.ByteBuf;

/**
 * 盘口
 * 
 * @author f.w
 *
 */
public class HqTenHandlerRunnable extends HqBaseHanlerRunnable<HqTenVo> {

	public HqTenHandlerRunnable(KafkaProducer<LongSerializer, ByteArraySerializer> kafkaProducer, String topic)
			throws Exception {
		super(new ObjectMapper(), kafkaProducer, topic, LoggerFactory.getLogger(topic));
	}

	@Override
	protected HqTenVo decoder(ByteBuf buffer) throws Exception {
		HqTenVo o = new HqTenVo();
		String code = parserString(buffer, 12);
		E_HqMarket mkt = E_HqMarket.getValue(buffer.readInt());// 市场
		VoBase relation = parseCode(code, o, mkt);
		// 股票以外不保存
		if (relation == null) {
			this.log("[HqTenVo]-" + mkt + " this code is not exist:" + code);
			return null;
		}

		Date time = parseTime(buffer.readInt(), mkt);
		int nside = buffer.readInt();// 买卖类型1买2卖
		List<HqTenSubVo> items = null;

		if (nside == 1) {
			// 买1-买10
			items = new ArrayList<HqTenSubVo>();
			for (int i = 10; i >= 1; i--) {

				double npx = parseDouble(buffer.readInt());// 买卖档报价
				int ncount = buffer.readInt();// 经纪商数量
				long llvolume = buffer.readLong();// 买卖档对应的股票数量

				HqTenSubVo htsv = new HqTenSubVo();
				htsv.setDw(i);
				htsv.setnPx(npx);
				htsv.setnCount(ncount);
				htsv.setLlVolume(llvolume);
				items.add(htsv);
			}

		} else if (nside == 2) {
			// 卖1-卖10
			items = new ArrayList<HqTenSubVo>();
			for (int i = 1; i <= 10; i++) {

				double npx = parseDouble(buffer.readInt());// 买卖档报价
				int ncount = buffer.readInt();// 经纪商数量
				long llvolume = buffer.readLong();// 买卖档对应的股票数量

				HqTenSubVo htsv = new HqTenSubVo();
				htsv.setDw(i);
				htsv.setnPx(npx);
				htsv.setnCount(ncount);
				htsv.setLlVolume(llvolume);
				items.add(htsv);
			}
		}

		if (items == null)
			return null;

		o.setnSide(nside);
		o.setList(items);

		o.setHt(time);// 行情时间
		o.setPt(Calendar.getInstance().getTime());

		return o;
	}
}
