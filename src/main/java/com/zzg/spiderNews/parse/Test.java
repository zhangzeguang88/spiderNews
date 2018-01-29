package com.zzg.spiderNews.parse;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test {
	
	public static void main(String[] args) throws Exception {
		ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"applicationContext.xml"});
		BloomFilter cust = (BloomFilter)context.getBean("bloomFilter");
        System.out.println(cust);
	}
}
