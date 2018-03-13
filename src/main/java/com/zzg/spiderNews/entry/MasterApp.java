package com.zzg.spiderNews.entry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.zzg.spiderNews.cache.InitConfig;
import com.zzg.spiderNews.cache.JedisUtil;
import com.zzg.spiderNews.parse.LinkFilter;

public class MasterApp {

	private static final Logger logger = LoggerFactory.getLogger(MasterApp.class);
	
	public static void main(String args[]){
		logger.info("master启动,加载上下文开始");
		ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"spring/spring-service.xml","spring/spring-dao.xml"});
		logger.info("加载上下文结束");
		final LinkFilter linkFilter = (LinkFilter)context.getBean("linkFilter");
		
		logger.info("种子加载开始");
		List<String> seed = readSeed(InitConfig.SEED_LOCATION);
		logger.info("种子加载结束，种子数量={}",seed.size());
		for(int i=0;i<seed.size();i++){
			 JedisUtil.lpush(InitConfig.QUEUE_URL, seed.get(i));
		}
		
		// 启动解析线程
		ExecutorService parsePool = Executors.newSingleThreadExecutor();
		Runnable parseTask = new Runnable(){
			@Override
			public void run() {
				int number = 0;
				while(true){
					String source = (String) JedisUtil.rpop(InitConfig.QUEUE_SOURCE);
					if(source == null){
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						logger.info("QUEUE_SOURCE队列为空，继续");
						continue;
					}
					try {
						logger.info("QUEUE_SOURCE不为空，pull 1,解析页面数量={}",number);
						linkFilter.findLinkByJ(source);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		
		parsePool.submit(parseTask);
		parsePool.shutdown();
	}


	public static List<String> readSeed(String filePath){
		List<String> seed= new ArrayList<String>();
	    try {
	            String encoding="utf-8";
	            File file=new File(filePath);
	            if(file.isFile() && file.exists()){ //判断文件是否存在
	                InputStreamReader read = new InputStreamReader(
	                new FileInputStream(file),encoding);//考虑到编码格式
	                BufferedReader bufferedReader = new BufferedReader(read);
	                String lineTxt = null;
	                while((lineTxt = bufferedReader.readLine()) != null){
	                    seed.add(lineTxt);
	                }
	                read.close();
			    }else{
			        logger.error("读取种子时，找不到指定的文件");
			    }
	    } catch (Exception e) {
	        logger.error("读取种子时，读取文件内容出错");
	        e.printStackTrace();
	    }
	    return seed;
	}
}

