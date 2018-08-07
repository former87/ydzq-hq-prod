package com.ydzq.hq.prod.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RDeque;
import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import com.ydzq.core.utils.ObjectParser;
import com.ydzq.hq.common.HkPgeniusSql;
import com.ydzq.hq.enums.E_CollectionName;
import com.ydzq.hq.enums.E_HqMarket;
import com.ydzq.hq.hk.dao.BaseDao;
import com.ydzq.hq.prod.global.AppConstrants;
import com.ydzq.hq.vo.VoBase;

@Component
public class CacheHkService extends BaseDao implements BaseService {
	Logger log = LoggerFactory.getLogger(CacheHkService.class);
	private RDeque<String> r_hk;

	public static VoBase getVoBase(String sc) throws Exception {
		Map<String, VoBase> map = AppConstrants.hkStockBaseMap;
		return map.get(sc);
	}

	@Override
	public void init() throws Exception {
		baseInit();
		r_hk = redisson_common.getDeque(com.ydzq.hq.global.AppConstrants.REDIS.HK.PRODUCE);
	}

	public void loadStk() {
		int count = 0;
		List<VoBase> list = getHkStock();
		if (list != null) {

			for (VoBase strv : list) {
				AppConstrants.hkStockBaseMap.put(strv.getSc(), strv);
				count++;
			}
			log.info("stock count:" + count);
		}
	}

	@Override
	public void setProduceFirst(String produce) {
		r_hk.offerFirst(produce);
	}

	@Override
	public void setProduceLast(String produce) {
		if (r_hk != null && !r_hk.contains(produce)) {
			r_hk.addLast(produce);
		}
	}

	@Override
	public String getProduce() {
		return r_hk.peekFirst();
	}

	@Override
	public boolean removeProduce() {
		return r_hk.pollFirst() != null;
	}

	@Override
	public boolean isNullProduce() {
		try {
			return r_hk == null || r_hk.peekFirst() == null;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 获取锁
	 * 
	 * @param lockKey
	 * @return
	 */
	protected boolean isExists(String lockKey) {

		try {
			return getLock(lockKey).isExists();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获取锁
	 * 
	 * @param lockKey
	 * @return
	 */
	@Override
	public RLock tryLock(String lockKey) {
		try {
			RLock lock = getLock(lockKey);
			if (lock.tryLock(AppConstrants.R_Lock_waitTime, AppConstrants.R_Lock_leaseTime, TimeUnit.MILLISECONDS))
				return lock;
			else
				return null;

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
		}
		return null;
	}

	/**
	 * 释放锁
	 * 
	 * @param lockKey
	 */
	public boolean unLock(String lockKey) {
		try {
			getLock(lockKey).unlock();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获取锁
	 * 
	 * @param lockKey
	 */
	protected RLock getLock(String lockKey) {
		try {
			return redisson_common.getLock(lockKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<VoBase> getHkStock() {
		List<VoBase> item = getHkStockByRedis();
		if (item != null)
			return item;
		else
			return getHkStockBySql();
	}

	private List<VoBase> getHkStockByRedis() {

		List<VoBase> item = null;
		try {
			E_HqMarket mkt = E_HqMarket.HK;
			Map<String, Map<String, Object>> maps = new HashMap<String, Map<String, Object>>(this.r_StkInfo);

			for (String i : maps.keySet()) {
				if (item == null)
					item = new ArrayList<VoBase>();
				Map<String, Object> m = maps.get(i);

				VoBase info = new VoBase();
				info.setCu(m.get("cu").toString());
				info.setId(m.get("id").toString());
				info.setMkt(mkt.getIndex());
				info.setOd(ObjectParser.parseInt(m.get("od")));
				info.setSc(m.get("sc").toString());
				info.setSg(ObjectParser.parseInt(m.get("sg")));
				info.setSn(m.get("sn").toString());
				info.setSt(ObjectParser.parseInt(m.get("st")));
				info.setPartition(ObjectParser.parseInt(info.getId()) % AppConstrants.KAFKAPARTITION);
				item.add(info);
			}
			// map.putAll(list); 
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return item;
	}

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * 行情接收程序用
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private List<VoBase> getHkStockBySql() {

		List<VoBase> item = null;
		try {
			E_HqMarket mkt = E_HqMarket.HK;
			List<Map> list = new ArrayList<Map>();
			list.addAll(execSql(mkt, E_CollectionName.HkStockInfo_stk));
			list.addAll(execSql(mkt, E_CollectionName.HkStockInfo_Wrt));
			list.addAll(execSql(mkt, E_CollectionName.HkStockInfo_Fund));

			for (Map i : list) {
				if (item == null)
					item = new ArrayList<VoBase>();

				VoBase info = new VoBase();
				info.setCu(i.get("cu").toString());
				info.setId(i.get("id").toString());
				info.setMkt(mkt.getIndex());
				info.setOd(ObjectParser.parseInt(i.get("od")));
				info.setSc(i.get("sc").toString());
				info.setSg(ObjectParser.parseInt(i.get("sg")));
				info.setSn(i.get("sn").toString());
				info.setSt(ObjectParser.parseInt(i.get("st")));
				info.setPartition(ObjectParser.parseInt(info.getId()) % AppConstrants.KAFKAPARTITION);
				item.add(info);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return item;
	}

	private List<Map<String, Object>> execSql(E_HqMarket mkt, E_CollectionName type) {
		List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
		try {
			String sql = HkPgeniusSql.getPgeniusSql(E_CollectionName.HkStockInfo_stk, null);
			if (!ObjectParser.isNullorEmpty(sql)) {
				maps = jdbcTemplate.queryForList(sql);
			}
		} catch (Exception e) {

		}
		return maps;
	}

}
