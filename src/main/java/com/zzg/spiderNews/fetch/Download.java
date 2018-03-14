package com.zzg.spiderNews.fetch;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebConnection;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.zzg.spiderNews.cache.InitConfig;
import com.zzg.spiderNews.cache.JedisUtil;
import com.zzg.spiderNews.entity.CrawlHtml;
import com.zzg.spiderNews.parse.BloomFilterUtil;
import com.zzg.spiderNews.service.CrawHtmlServiceImpl;

@Component
public class Download {
	
	private static final Logger logger = LoggerFactory.getLogger(Download.class);
	
	@Resource
	CrawHtmlServiceImpl crawHtmlServiceImpl;
	
	private static String MAC_ADDRESS = "";
	static{
		try{
			InetAddress ia = InetAddress.getLocalHost();
			byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
			StringBuffer sb = new StringBuffer("");
			for(int i=0; i<mac.length; i++) {
				if(i!=0) {
					sb.append("-");
				}
				//字节转换为整数
				int temp = mac[i]&0xff;
				String str = Integer.toHexString(temp);
				if(str.length()==1) {
					sb.append("0"+str);
				}else {
					sb.append(str);
				}
			}
			MAC_ADDRESS = sb.toString().toUpperCase();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private static WebClient webClient = null;
	static{
		BrowserVersion.FIREFOX_24.setBrowserLanguage("zh-CN");
		BrowserVersion.FIREFOX_24.setSystemLanguage("zh-CN");
		BrowserVersion.FIREFOX_24.setUserLanguage("zh-CN");
		BrowserVersion.FIREFOX_24.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:46.0) Gecko/20100101 Firefox/46.0");
		BrowserVersion.FIREFOX_24.setBrowserVersion(46);
		BrowserVersion.FIREFOX_24.setCpuClass("x64");
		webClient = new WebClient(BrowserVersion.FIREFOX_24);
		webClient.setWebConnection(new MyHttpWebConnection(webClient));
		if(false){
			webClient = new WebClient(BrowserVersion.FIREFOX_24,"127.0.0.1",8888);
		}else{
			webClient = new WebClient(BrowserVersion.FIREFOX_24);
		}
		webClient.getCache().clear();
		webClient.getCookieManager().clearCookies();
		webClient.getOptions().setJavaScriptEnabled(false);
		webClient.setJavaScriptTimeout(600*1000);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setActiveXNative(false);
		webClient.getOptions().setPopupBlockerEnabled(false);
		webClient.getOptions().setRedirectEnabled(true);
		webClient.getOptions().setTimeout(600000);
		webClient.getOptions().setDoNotTrackEnabled(true);
		webClient.getCookieManager().setCookiesEnabled(true);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setUseInsecureSSL(true);
		webClient.getOptions().setSSLInsecureProtocol("TLSv1.2");
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		webClient.setAlertHandler(new CollectingAlertHandler());
	}
	
	
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		webClient.getOptions().setJavaScriptEnabled(false);
		Download dl = new Download();
		dl.loadPage("http://news.sina.com.cn/c/nd/2018-03-13/doc-ifyscsmv0614178.shtml", "GET", null, null, "utf-8");
	}
	
	
	public void loadPage (String url, String method, Map<String, String> headers, List<NameValuePair> paramList,
			String charset) throws Exception {
		Page page = null;
//		String ip = "127.0.0.1";
//		int port = 8888;

		WebRequest request = new WebRequest(new URL(url), HttpMethod.valueOf(method));
//		request.setProxyHost(ip);
//		request.setProxyPort(port);
		if(charset != null){
			request.setCharset(charset);
		}
		if (headers != null) {
			request.getAdditionalHeaders().putAll(headers);
		}
		if (paramList != null) {
			request.setRequestParameters(paramList);
		}
		logger.info("url={}",url);
		page = webClient.getPage(request);
		// 请求成功之后获得响应码
		if (page != null) {
			int responseCode = page.getWebResponse().getStatusCode();
			String responseMsg = page.getWebResponse().getStatusMessage();
			logger.info("responseCode={},responseMsg={}",responseCode,responseMsg);
			
			JedisUtil.lpush(InitConfig.QUEUE_SOURCE, page.getWebResponse().getContentAsString());
			
			//将url source mac 哪台机器存入数据库
			CrawlHtml ct = new CrawlHtml();
			ct.setUrl(url);
			ct.setHtml(page.getWebResponse().getContentAsString());
			ct.setSource(Download.MAC_ADDRESS);//todo
			//ct.setCreateTime(DateTimeUtil.getAddDate("yyyy-MM-dd hh:mm:ss",0));
			ct.setCreateTime(new Date());
			crawHtmlServiceImpl.save(ct);
		}
	}
	
	


}
