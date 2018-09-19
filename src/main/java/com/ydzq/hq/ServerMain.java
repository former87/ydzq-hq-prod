package com.ydzq.hq;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
 
 
/**
 * Hello world!mybranch-change,create-mybranch-1
 *
 */
@SpringBootApplication
@ComponentScan
@EnableScheduling
@EnableAutoConfiguration(exclude={MongoAutoConfiguration.class})
public class ServerMain extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ServerMain.class);
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = null;
		SpringApplication app = null;
		Log log = LogFactory.getLog(ServerMain.class);

		try {
			app = new SpringApplication(ServerMain.class);
			ctx = app.run();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("application start up is false!");
			System.exit(0);
		}
		if (ctx == null || app == null) {
			log.error("application start up is false!");
			System.exit(0);
		}
	}

}