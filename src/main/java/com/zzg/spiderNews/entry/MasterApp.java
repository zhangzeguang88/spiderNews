package com.zzg.spiderNews.entry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.zzg.spiderNews.cache.InitConfig;
import com.zzg.spiderNews.cache.JedisUtil;
import com.zzg.spiderNews.parse.LinkFilter;

public class MasterApp {

	public static void main(String args[]){
		ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"spring/spring-service.xml","spring/spring-dao.xml"});
		final LinkFilter linkFilter = (LinkFilter)context.getBean("linkFilter");
		
		List<String> seed = readSeed(InitConfig.SEED_LOCATION);
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
						System.out.println("队列为空");
						continue;
					}
					try {
						System.out.println("解析页面数量="+number);
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
			        System.out.println("找不到指定的文件");
			    }
	    } catch (Exception e) {
	        System.out.println("读取文件内容出错");
	        e.printStackTrace();
	    }
	    return seed;
	}
}

