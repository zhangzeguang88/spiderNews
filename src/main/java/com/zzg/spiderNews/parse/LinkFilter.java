package com.zzg.spiderNews.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.zzg.spiderNews.cache.InitConfig;
import com.zzg.spiderNews.cache.JedisUtil;
import com.zzg.spiderNews.entry.MasterApp;

@Component
public class LinkFilter {
	
	private static final Logger logger = LoggerFactory.getLogger(LinkFilter.class);

	private BloomFilter bloomFilter;
	
	private int i;
	String textOfBody;
	String textOfAnthor;
	String weight;

	public LinkFilter() {
		bloomFilter = BloomFilterUtil.getBloomFilter();
	}

	// 通过JSOUP获取链接
	public void findLinkByJ(String source) throws Exception {
		Document doc = Jsoup.parse(source);
		Elements links = doc.getElementsByTag("a");

		for (Element link : links) {				
			String linkHref = link.attr("abs:href");		
			textOfAnthor = link.text();
			 
			if (linkHref.length() < 4 || getLinkType(linkHref, ".*apk$")
					|| getLinkType(linkHref, ".*exe$")
					|| getLinkType(linkHref, ".*rar$")
					|| getLinkType(linkHref, ".*zip$")
					|| getLinkType(linkHref, ".*pps$")
					|| getLinkType(linkHref, ".*ppt$")) { 
				logger.info("这是无效链接,丢弃={}",linkHref);
				continue;
			} 
			if (!bloomFilter.contains(linkHref)) {
				bloomFilter.add(linkHref);
				i++;
				if (i % 1000000 == 0) {
					BloomFilterUtil.setBloomFilter(bloomFilter);
					logger.info("保存至布隆过滤器,布隆过滤器当前保存链接数={}",i);
				}
				//添加进入redis
				JedisUtil.lpush(InitConfig.QUEUE_URL, linkHref);
			}
		}

	}

	//计算链接权值
	public String computeWeight(String textOfAnchor,String textOfBody){
		
		for(int i=0;i<textOfAnthor.length();i++){
			if(textOfAnchor.codePointAt(i)>57923 && textOfAnchor.codePointAt(i)<58192){
				return "1";								
			}
		}
		for(int i=0;i<textOfAnthor.length();i++){
			if(textOfAnchor.codePointAt(i)>57923&&textOfAnchor.codePointAt(i)<58192){
				return "0.5";			
			}
		}		
	return "0";	
	}
	


	// 正则表达式获取链接
	public List<String> findA(String s) {
		String regex;
		final List<String> list = new ArrayList<String>();
		regex = "<a[^>]*href=(\"([^\"]*)\"|\'([^\']*)\'|([^\\s>]*))[^>]*>(.*?)</a>";
		final Pattern pa = Pattern.compile(regex, Pattern.DOTALL);
		final Matcher ma = pa.matcher(s);
		while (ma.find()) {
			list.add(ma.group());
		}
		return list;
	}

	public static String getCharSet(String content) {
		String regex = "<meta[^>]*?charset=(\\w+)[\\W]*?>";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(content);
		if (matcher.find())
			return matcher.group(1);
		else
			return null;
	}


	// 判断链接的类型
	public static boolean getLinkType(String url, String reg) {
		String regex = reg;
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(url);
		if (matcher.find())
			return true;
		else
			return false;

	}

}
