package com.ydzq.hq.prod.netty.handler.thread;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ydzq.hq.enums.E_HqMarket;
import com.ydzq.hq.vo.hq.HqJjsSubVo;
import com.ydzq.hq.vo.hq.HqJjsVo;
import com.ydzq.hq.vo.VoBase;

import io.netty.buffer.ByteBuf;

/**
 * 经纪商
 * 
 * @author f.w
 *
 */
public class HqJjsHandlerRunnable extends HqBaseHanlerRunnable<HqJjsVo> {

	public HqJjsHandlerRunnable(KafkaProducer<LongSerializer, ByteArraySerializer> kafkaProducer, String topic)
			throws Exception {
		super(new ObjectMapper(), kafkaProducer, topic, LoggerFactory.getLogger(topic));
	}

	@Override
	protected HqJjsVo decoder(ByteBuf buffer) throws Exception { 
		HqJjsVo o = new HqJjsVo();
		String code = parserString(buffer, 12);
		E_HqMarket mkt = E_HqMarket.getValue(buffer.readInt());// 市场
		VoBase relation = parseCode(code, o, mkt);

		// 股票以外不保存
		if (relation == null) {
			this.log("[HqJjsVo]-"+mkt + " this code is not exist:" + code );
			return null;
		}

		int nside = buffer.readInt();// 买卖类型1买2卖
		List<HqJjsSubVo> items = null;

		if (nside == 1) {
			items = new ArrayList<HqJjsSubVo>();
			for (int i = 1; i <= 40; i++) {
				HqJjsSubVo hhjsv = o.newSubInstance();
				hhjsv.setId(i);
				hhjsv.setDw(buffer.readInt());// 档位
				hhjsv.setJjsId(buffer.readInt());// 经纪商
				items.add(hhjsv);
			}
		} else if (nside == 2) {
			items = new ArrayList<HqJjsSubVo>();
			for (int i = 1; i <= 40; i++) {
				HqJjsSubVo hhjsv = o.newSubInstance();
				hhjsv.setId(i);
				hhjsv.setDw(buffer.readInt());// 档位
				hhjsv.setJjsId(buffer.readInt());// 经纪商
				items.add(hhjsv);
			}
		}

		if (items == null)
			return null;

		o.setnSide(nside);
		o.setQueue(items);
		
		o.setHt(Calendar.getInstance().getTime());// 行情时间
		o.setPt(Calendar.getInstance().getTime());

		return o;
	}
}
