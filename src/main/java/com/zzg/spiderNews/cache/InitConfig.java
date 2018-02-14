package com.zzg.spiderNews.cache;

import java.util.ResourceBundle;

public class InitConfig {
	 public static String QUEUE_URL = "";
	 public static String QUEUE_SOURCE = "";
	 public static String SEED_LOCATION = "";
	
	static{
		 ResourceBundle bundle = ResourceBundle.getBundle("fetch");
	        if (bundle == null) {
	            throw new IllegalArgumentException(
	                    "[jedis-pool.properties] is not found!");
	        }

	        QUEUE_URL = bundle.getString("fetch.queue.url.name");
	        QUEUE_SOURCE = bundle.getString("fetch.queue.source.name");
	        SEED_LOCATION = bundle.getString("fetch.seed.location");
	}

}
