package com.zzg.spiderNews.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateTimeUtil {
	
	/**
	 * 获得当天日期
	 * @param pattern
	 * @return
	 */
	public static String getCurrentday(String pattern){
		Calendar   cal   =   Calendar.getInstance();
		String currentday = new SimpleDateFormat( pattern).format(cal.getTime());
		return currentday;
	}
	
	/**
	 * 获取时间差，返回天数
	 * @param pattern 日期格式
	 * @param startDate 开始日期
	 * @param endDate 结束日期
	 * @return
	 */
	public static long getDiffDate(String pattern, String startDate, String endDate){
		DateFormat df = new SimpleDateFormat(pattern);
		long days = 0l;
		try{
		    Date d1 = df.parse(endDate);
		    Date d2 = df.parse(startDate);
		    long diff = d1.getTime() - d2.getTime();
		    days = diff / (1000 * 60 * 60 * 24);
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return days;
	}
	
	/**
	 * 获得N天之后或之前的日期,今天日期不算在内
	 * @param pattern
	 * @param add 正数时可获得add天之后的日期，负数时获得add天之前的日期
	 * @return
	 */
	public static String getAddDate(String pattern, int add){
		Calendar   cal   =   Calendar.getInstance();
		cal.add(Calendar.DATE,   add);
		String date = new SimpleDateFormat( pattern).format(cal.getTime());
		return date;
	}
	
	/**
	 * 格式化日期
	 * @param date 要格式化的日期
	 * @param pattern 格式
	 * @return
	 */
	public static String formatDate(Date date,String pattern){
		return new SimpleDateFormat(pattern).format(date);
	}

	/**
	 * 日期字符串转换为日期
	 * @param dateStr
	 * @param pattern
	 * @return
	 */
	public static Date parse2Date(String dateStr,String pattern){
		Date date = null;
		try {
			date = new SimpleDateFormat(pattern).parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	/**
	 * 将无间隔符的日期转换成带间隔符的日期
	 * @param date
	 * @param interval 间隔符
	 * @return
	 */
	public static String formatDate(String date, String interval){
		StringBuffer sb = new StringBuffer();
		if(date == null || date.length() != 8){
			return date;
		}
		sb.append(date.substring(0, 4)).append(interval);
		sb.append(date.substring(4, 6)).append(interval);
		sb.append(date.substring(6));
		return sb.toString();
	}
	
	public  static String parseUSADate2Formate(String dateStr){
		String timeStr = null;

		try {
			SimpleDateFormat sfEnd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sfStart = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",java.util.Locale.ENGLISH) ;
			timeStr = sfEnd.format(sfStart.parse(dateStr));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return timeStr;
	}

	/**
<<<<<<< HEAD
	 * 输出前几个月的信息，包括当前月
	 * @param num
	 * @param format
	 * @return
	 */
	public static List<String> getMonth(int num,String format){
		List<String> objectTmp = new ArrayList<String>();
		java.text.DateFormat format2 = new java.text.SimpleDateFormat(
				format);
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, 1);
		for (int i = 0; i < num; i++) {
			c.add(Calendar.MONTH, -1);
			Date date = c.getTime();
			String date2 = format2.format(date);
			//System.out.println(date2);
			objectTmp.add(date2);
		}
		return objectTmp;
	}

	 /* 获得N月之后或之前的日期
	 * @param pattern
	 * @param add 正数时可获得add月之后的日期，负数时获得add月之前的日期
	 * @return
	 */
	public static String getAddMonth(int add,String pattern){
		Calendar   cal   =   Calendar.getInstance();
		cal.add(Calendar.MONTH, add);
		String date = new SimpleDateFormat( pattern).format(cal.getTime());
		return date;
	}
}
