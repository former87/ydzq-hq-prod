package com.ydzq.hq.prod.service;

 
import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ydzq.hq.enums.E_HqMarket;
import com.ydzq.hq.prod.global.AppConstrants;
import com.ydzq.hq.vo.VoBase;

import java.util.Map;

/**
 * 股票
 * 
 * @author f.w
 *
 */
@Component
public interface BaseService {
	Logger log = LoggerFactory.getLogger(BaseService.class);

	static VoBase getVoBase(E_HqMarket mkt, String sc) throws Exception {
		switch (mkt) {
		case HK: {
			Map<String, VoBase> map = AppConstrants.hkStockBaseMap;
			return map.get(sc);
		}
		case US: {
			Map<String, VoBase> map = AppConstrants.usStockBaseMap;
			return map.get(sc);
		}
		default:
			throw new Exception("[value:" + mkt + ",sc:" + sc + "] isn't exists");
		}

	}

	void init() throws Exception;

	void setProduceFirst(String produce) ;
	
	void setProduceLast(String produce) ;

	String getProduce() throws Exception;

	boolean removeProduce() throws Exception;

	boolean isNullProduce() throws Exception;

	RLock tryLock(String lockKey) throws Exception;
	
    void loadStk();

}
