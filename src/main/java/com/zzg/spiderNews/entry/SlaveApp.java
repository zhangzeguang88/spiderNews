package com.zzg.spiderNews.entry;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.zzg.spiderNews.cache.InitConfig;
import com.zzg.spiderNews.cache.JedisUtil;
import com.zzg.spiderNews.fetch.Download;
import com.zzg.spiderNews.parse.BloomFilterUtil;


public class SlaveApp {
	private static final Logger logger = LoggerFactory.getLogger(SlaveApp.class);
	
/*	//并发执行队列
	private Queue<String> localQueue = new ConcurrentLinkedDeque<String>();
	
	public Queue<String> getLocalQueue() {
		return localQueue;
	}

	public void setLocalQueue(Queue<String> localQueue) {
		this.localQueue = localQueue;
	}

	//定时任务 每次拿100个url ,当发现队列数值《20时，再去url队列拉数据
	@Scheduled(fixedRate = 1*60*1000)
	public void doJob() { 
	   if(localQueue.size()<20){
		   localQueue.addAll(RedisUtil.get("queue", 0, 100));
	   }
	}*/
	
	public static void start(String args[]){
		logger.info("slave启动,加载上下文开始");
		ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"spring/spring-service.xml","spring/spring-dao.xml"});
		logger.info("加载上下文结束");
		final Download download = (Download)context.getBean("download");
		
		ExecutorService fetchPool = Executors.newSingleThreadExecutor();
		Runnable fetchTask = new Runnable(){
			@Override
			public void run() {
				while(true){
					String url = (String) JedisUtil.rpop(InitConfig.QUEUE_URL);
					if(url==null){
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						logger.info("QUEUE_URL队列为空，继续");
						continue;
					}
					try {
						logger.info("页面请求url={} method={}",url,"GET");
						download.loadPage(url, "GET", null, null, null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		fetchPool.submit(fetchTask);
		fetchPool.shutdown();
	}
	
	
	
}
