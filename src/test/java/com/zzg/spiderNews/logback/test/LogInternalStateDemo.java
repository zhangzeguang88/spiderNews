package com.zzg.spiderNews.logback.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

public class LogInternalStateDemo {

    private static final Logger logger = LoggerFactory.getLogger(LogInternalStateDemo.class);
    
    public static void main(String[] args) {
//    	int i = 0;
//    		logger.info("{}满世界的跑跑",i++);
//    		logger.debug("{}满世界的跑跑",i++);
//    		logger.warn("{}满世界的跑跑",i++);
        
        
        /*//打印 Logback 内部状态
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(lc);*/
		LogInternalStateDemo ins = new LogInternalStateDemo();
		ins.ma();
        
    }
    
    public synchronized void ma(){
    	System.out.println("ma");
    	mb();
    }
    
    public synchronized void mb(){
    	System.out.println("mb");
    }
}
