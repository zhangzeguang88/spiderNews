//package com.zzg.spiderNews.httpclient.test;
//
//import java.security.KeyManagementException;
//import java.security.cert.CertificateException;
//import java.security.cert.X509Certificate;
//
//import javax.net.ssl.SSLContext;
//
//import org.apache.http.client.CookieStore;
//import org.apache.http.client.config.CookieSpecs;
//import org.apache.http.client.config.RequestConfig;
//import org.apache.http.config.Registry;
//import org.apache.http.config.RegistryBuilder;
//import org.apache.http.conn.socket.ConnectionSocketFactory;
//import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
//import org.apache.http.conn.ssl.SSLContextBuilder;
//import org.apache.http.conn.ssl.TrustStrategy;
//import org.apache.http.cookie.Cookie;
//import org.apache.http.cookie.CookieOrigin;
//import org.apache.http.cookie.CookieSpec;
//import org.apache.http.cookie.CookieSpecProvider;
//import org.apache.http.cookie.MalformedCookieException;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.DefaultRedirectStrategy;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.impl.client.LaxRedirectStrategy;
//import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
//import org.apache.http.impl.cookie.BestMatchSpecFactory;
//import org.apache.http.impl.cookie.BrowserCompatSpec;
//import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
//import org.apache.http.protocol.HttpContext;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//
///**
// * 
// * @ClassName: HttpClientPool
// * @Description:HttpClient连接池
// * @author xiongwei
// * @date 2015-11-09 上午11:34:06
// * 
// */
//public class HttpClientPool {
//
//	/***** 日志记录 *****/
//	private static Logger log = LoggerFactory.getLogger(HttpClientPool.class);
//
//	/***** HTTP最大连接数 *****/
//	private static final int MAX_TOTAL_CONNECTIONS = 50;
//
//	/***** HTTP最大路由数 *****/
//	private static final int MAX_ROUTE_CONNECTIONS = 20;
//
//	/***** HTTP连接超时时间 *****/
//	public static final int CONNECT_TIMEOUT = 10000;
//
//	/***** HTTP套接字SOCKET超时时间 *****/
//	public static final int SOCKET_TIMEOUT = 20000;
//
//
//	/***** 连接池管理对象 *****/
//	private static PoolingHttpClientConnectionManager connectionManager = null;
//	
//	/*****初始化连接池 *****/
//	static {
//		try {
//		      SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
//	                 //信任所有
//	                 public boolean isTrusted(X509Certificate[] chain,
//	                                 String authType) throws CertificateException {
//	                     return true;
//	                 }
//	             }).build();
//			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
//					.<ConnectionSocketFactory> create()
//					.register("http", new MyPlainSocketFactory())
//					.register("https",new MySSLSocketFactory(sslContext,SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER))
//					.build();
//
//			connectionManager = new PoolingHttpClientConnectionManager(
//					socketFactoryRegistry);
//			connectionManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
//			connectionManager.setDefaultMaxPerRoute(MAX_ROUTE_CONNECTIONS);
//
//		} catch (KeyManagementException e) {
//			log.error("KeyManagementException", e);
//		} catch (Exception e) {
//			log.error("NoSuchAlgorithmException", e);
//		}
//	}
//	
//
//	/****
//	 * 创建CloseableHttpClient实例
//	 * @return
//	 */
//	public static  CloseableHttpClient createInstance() {
//		
//		boolean redirectAll = false;
//		
//		/***** Cookie策略提供者 *****/
//		CookieSpecProvider easySpecProvider = new CookieSpecProvider() {
//			public CookieSpec create(HttpContext context) {
//				return new BrowserCompatSpec() {
//					@Override
//					public void validate(Cookie cookie, CookieOrigin origin)
//							throws MalformedCookieException {
//						// Oh, I am easy
//					}
//				};
//			}
//
//		};
//		DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
//		if (redirectAll) {
//			redirectStrategy = new LaxRedirectStrategy();
//		}
//		Registry<CookieSpecProvider> cookieSpecProvider = RegistryBuilder
//				.<CookieSpecProvider> create().register(CookieSpecs.BEST_MATCH,
//						new BestMatchSpecFactory()).register(
//						CookieSpecs.BROWSER_COMPATIBILITY,
//						new BrowserCompatSpecFactory()).register("easy",
//						easySpecProvider).build();
//
//		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT)
//		                                                    .setSocketTimeout(SOCKET_TIMEOUT)
//		                                                    .setConnectionRequestTimeout(CONNECT_TIMEOUT)
//		                                                    .setRedirectsEnabled(true)
//		                                                    .setStaleConnectionCheckEnabled(true)
//		                                                    .setCookieSpec("easy")
//				                                            .build();
//
//		CookieStore cookieStore = CookieStoreHolder.borrowCookieStore();
//		return HttpClients.custom().setConnectionManager(connectionManager)
//		                           .setDefaultRequestConfig(requestConfig)
//		                           .setDefaultCookieStore(cookieStore)
//		                           .setDefaultCookieSpecRegistry(cookieSpecProvider)
//		                           .setRedirectStrategy(redirectStrategy)
//				                   .build();
//	}
//	
//}
