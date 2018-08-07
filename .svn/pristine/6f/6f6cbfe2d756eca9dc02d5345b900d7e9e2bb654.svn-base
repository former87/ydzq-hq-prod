package com.ydzq.hq.prod;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import com.ydzq.hq.prod.global.AppConstrants;
import com.ydzq.hq.prod.netty.handler.thread.ElectionRunnable;

@Configuration
public class Entrance {
	private Logger logger = LoggerFactory.getLogger(Entrance.class);

	@Resource(name = AppConstrants.SOCKET.HK.PRODUCENAME)
	private ElectionRunnable electionRunable;

	@PostConstruct
	private void init() {
		try {

			if (electionRunable != null) {
				Thread thread = new Thread(electionRunable);
				thread.setName(AppConstrants.SOCKET.HK.PRODUCENAME);
				thread.start();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Scheduled(cron = "0 0 9 ? * *")
	public void Clear() {

		if (electionRunable != null) {
			logger.info("=================socket:[" + electionRunable.getSocketVo() + "] is reset=================");
			electionRunable.getHqClientHandler().reset();
		}
	}

	@Scheduled(cron = "0 0/5 * ? * *")
	public void updateStockInfo() {
		if (electionRunable != null) {
			electionRunable.getBaseService().loadStk();
		}
	}

}
