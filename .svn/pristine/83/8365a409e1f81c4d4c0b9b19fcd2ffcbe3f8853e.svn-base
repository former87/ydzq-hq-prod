package com.ydzq.hq.prod.global;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.ydzq.hq.prod.netty.protocol.SocketVo;
import com.ydzq.hq.vo.VoBase; 

/**
 * 常量
 */
public class AppConstrants {

	public static Map<String, VoBase> hkStockBaseMap = new HashMap<String, VoBase>();
	public static Map<String, VoBase> usStockBaseMap = new HashMap<String, VoBase>();
	public static ConcurrentHashMap<String, SocketVo> socketVo = null;
	public static final int KAFKAPARTITION = 3; // socket包头
	public static final String PRODUCENAME=UUID.randomUUID().toString();
	public static final int R_Lock_waitTime=1000; 
	public static final int R_Lock_leaseTime=5000;
	

	public class SOCKET {
		public static final int WRAPHEADLENTH = 12; // socket包头	
		public static final int RECONNECT_TIME = 3;
		public static final int RECONNECT_COUNT = 10;
		public static final String HEART = "111";
		public static final int HEARTTIMROUT = 20;
		
		
		public class HK {
			public static final String SNAP = "300";
			public static final String TEN = "310";
			public static final String MIN = "321";
			public static final String DEAL = "330";
			public static final String JJS = "371";
			public static final String MONITOR = "100";
			public static final String NOTEXISTS = "notexists";
			public static final String LOCKNAME="hkelection";
			public static final String PRODUCENAME="hkproduce";
		}

		public class US {
			public static final String SNAP = "900";
			public static final String TEN = "910";
			public static final String MIN = "921";
			public static final String DEAL = "930"; 
		}
	}
}
