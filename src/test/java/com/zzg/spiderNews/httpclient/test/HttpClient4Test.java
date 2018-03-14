//package com.zzg.spiderNews.httpclient.test;
//
//import java.net.URI;
//
//import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.protocol.HttpClientContext;
//import org.apache.http.impl.client.CloseableHttpClient;
//
//import com.rong360.crawler.page.CrawlerPage;
//import com.rong360.crawler.util.URIUtils;
//
//public class HttpClient4Test {
//	
//	/**
//     * 处理GET请求
//     *
//     * @param crawlerPage
//     */
//    public void requestGet(CrawlerPage crawlerPage) {
//
//
//        /***** 设置当前请求URI *****/
//        URI uri = crawlerPage.getUriData().getUri();
//        HttpGet httpGet = new HttpGet();
//        httpGet.setURI(uri);
//
//        /***** 设置当前请求头信息 *****/
//        configMethod(httpGet, crawlerPage);
//
//        /***** HTTP状态 *****/
//        int statusCode = 0;
//        String ip = "";
//        try {
//            CloseableHttpClient httpClient = HttpClientPool.createInstance();
//            HttpClientContext context = HttpClientContext.create();
//            ip = setProxy(context, httpGet, crawlerPage);
//            log.info(ip + ", url:" + uri + ", cookie:" + crawlerPage.getUriData().getCookieString());
//            HttpResponse response = httpClient.execute(httpGet, context);
//            statusCode = response.getStatusLine().getStatusCode();
//            if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY || statusCode == HttpStatus.SC_MOVED_PERMANENTLY) {
//                httpGet.abort();
//                String url = response.getFirstHeader("Location").getValue();
//                URI newUri = URIUtils.getHttpURL(url);
//                log.info("Location:" + url);
//                httpGet = new HttpGet();
//                httpGet.setURI(uri.resolve(newUri));
//                crawlerPage.getUriData().setRedirectUri(uri.resolve(newUri));
//                configMethod(httpGet, crawlerPage);
//                httpGet.setHeader("Referer", url);
//                response = httpClient.execute(httpGet, context);
//                statusCode = response.getStatusLine().getStatusCode();
//            }
//            if (statusCode != HttpStatus.SC_OK) {
//                if (httpGet != null) {
//                    httpGet.releaseConnection();
//                }
//                log.warn("statusCode=" + statusCode);
//                return;
//            }
//            /***** 处理HTTP200 *****/
//            innerProcess200(crawlerPage, response);
//
//        } catch (Exception e) {
//            if (crawlerPage.getUriData().getRetryCount() < maxRetryCount) {
//                log.warn(ip + ", url:" + crawlerPage.getUriData().getStrUri() + ", message:" + e.getMessage() + ", cookie:" + crawlerPage.getUriData().getCookieString());
//                crawlerPage.getUriData().setRetryCount(crawlerPage.getUriData().getRetryCount() + 1);
//                if (!crawlerPage.getProxy().isUseSameProxy()) {
//                    /*****失败时清空当前IP, 切换到另外一个IP*****/
//                    crawlerPage.getProxy().setProxyIp(null);
//                }
//                if (httpGet != null) {
//                    httpGet.releaseConnection();
//                }
//                process(crawlerPage);
//            } else {
//                String msg = ip + ", url:" + crawlerPage.getUriData().getStrUri() + ", message:" + e.getMessage() + ", cookie:" + crawlerPage.getUriData().getCookieString();
//                if (isLogToWarn(crawlerPage)) {
//                    log.warn(msg);
//                } else {
//                    log.error(msg);
//                }
//            }
//        } finally {
//            processCrawlerStatusCode(statusCode, crawlerPage);
//            if (httpGet != null) {
//                httpGet.releaseConnection();
//            }
//        }
//    }
//
//
//	public static void main(String[] args) {
//	    TestDemo02 demo02 = new TestDemo02();
//	    String url = "http://192.168.1.1/interface!sendMsg.action";
//	    String result = demo02.postMsg(url, "123");
//	    System.out.println(result);
//	}
//}