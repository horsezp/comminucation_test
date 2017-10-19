package com.yuexiu.stockprice;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PriceService {

	private static String folder = "./";

	private static String incremental = "Y";

	public static HttpClient wrapClient(HttpClient base) throws Exception {
		SSLContext ctx = SSLContext.getInstance("TLSv1");
		X509TrustManager tm = new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			}
		};

		ctx.init(null, new TrustManager[] { tm }, null);
		SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		ClientConnectionManager ccm = base.getConnectionManager();
		SchemeRegistry registry = ccm.getSchemeRegistry();
		registry.register(new Scheme("https", 443, ssf));
		return new DefaultHttpClient(ccm, base.getParams());
	}

	public static void main(String[] args) throws Exception {

		if (args != null && args.length == 1) {
			folder = args[0];
		} else if (args != null && args.length == 2) {
			folder = args[0];
			incremental = args[1];
		}

		getPriceHistory("hk00405", "0405.HK");
		getPriceHistory("hk06139", "6139.HK");
		getPriceHistory("hk87001", "87001.HK");
		getPriceHistory("hk01426", "1426.HK");
		getPriceHistory("hk01275", "1275.HK");
		getHSIPrices();

	}

	private static String[] getNameInfo(String stockId) throws Exception {

		// http://hq.sinajs.cn/list=hk00405
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet("http://hq.sinajs.cn/list=" + stockId);
		HttpResponse response = client.execute(get);
		// System.out.println(response.getStatusLine().getStatusCode());
		if (response.getStatusLine().getStatusCode() != 200) {
			System.exit(-1);
		}
		HttpEntity httpEntity = response.getEntity();
		String result = EntityUtils.toString(httpEntity);//  
		String content = result.split("\"")[1];
		// System.out.println(content);
		return content.split(",");
	}

	private static void getHSIPrices() throws Exception {
		System.out.println("开始获取 恒生指数 历史价格");
		// https://hk.finance.yahoo.com/quote/%5EHSI/history?p=%5EHSI
		HttpClient client = wrapClient(new DefaultHttpClient());
		HttpGet get = new HttpGet("https://hk.finance.yahoo.com/quote/%5EHSI/history?p=%5EHSI");
		HttpResponse response = client.execute(get);
		if (response.getStatusLine().getStatusCode() != 200) {
			System.out.println("错误：" + response.getStatusLine().getStatusCode() );
			System.exit(-1);
		}
		HttpEntity httpEntity = response.getEntity();
		String result = EntityUtils.toString(httpEntity);// 
		// System.out.println(result);
		String[] lines = result.split("\n");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		for (String line : lines) {
			if (line.startsWith("root.App.main")) {
				// System.out.println(line);
				String[] values = line.split("\"prices\":");
				// System.out.println(values[1]);
				String quoteContent = values[1].split(",\"isPending\":")[0];
				final GsonBuilder builder = new GsonBuilder();
				builder.setVersion(1.0);
				final Gson gson = builder.create();

				Price[] list = gson.fromJson(quoteContent, Price[].class);
				List<String> priceValues = new ArrayList<String>();

				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.DATE, -7);
				Date oneWeekAgo = calendar.getTime();

				for (Price p : list) {
					Date date = new Date(p.getDate() * 1000);
					String dateValue = format.format(date);

					// 只拿一个月的数据
					if ("Y".equals(incremental)) {
						if (date.before(oneWeekAgo)) {
							continue;
						}
					}

					if (p.getClose() != null) {
						String closePrice = p.getClose().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
						// System.out.println(p.getClose());
						String content = "HSI,HSI,恒生指数," + dateValue + "," + closePrice;
						// System.out.println(content);
						priceValues.add(content);
					}
				}
				FileUtils.writeLines(new File(folder + File.separator + "HSI.csv"), "UTF-8", priceValues);
				break;
			}
		}
		System.out.println("开始获取 恒生指数 历史价格结束");
	}

	private static void getPriceHistory(String hkCode, String stockId)
			throws Exception, IOException, ClientProtocolException {
		
		System.out.println("开始获取 "+ stockId +" 历史价格");

		String[] infos = getNameInfo(hkCode);
		HttpClient client = wrapClient(new DefaultHttpClient());
		HttpGet get = new HttpGet("https://hk.finance.yahoo.com/quote/" + stockId + "/history?p=" + stockId);
		HttpResponse response = client.execute(get);
		// System.out.println(response.getStatusLine().getStatusCode());
		if (response.getStatusLine().getStatusCode() != 200) {
			System.out.println("错误：" + response.getStatusLine().getStatusCode() );
			System.exit(-1);
		}
		HttpEntity httpEntity = response.getEntity();
		String result = EntityUtils.toString(httpEntity);// ȡ��Ӧ���ַ���
		String[] lines = result.split("\n");

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		for (String line : lines) {
			if (line.startsWith("root.App.main")) {
				// System.out.println(line);
				String[] values = line.split("\"prices\":");
				// System.out.println(values[1]);
				String quoteContent = values[1].split(",\"isPending\":")[0];
				final GsonBuilder builder = new GsonBuilder();
				builder.setVersion(1.0);
				final Gson gson = builder.create();

				Price[] list = gson.fromJson(quoteContent, Price[].class);

				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.DATE, -7);
				Date oneWeekAgo = calendar.getTime();

				List<String> priceValues = new ArrayList<String>();
				for (Price p : list) {
					Date date = new Date(p.getDate() * 1000);
					String dateValue = format.format(date);
					// 只拿一个月的数据
					if ("Y".equals(incremental)) {
						if (date.before(oneWeekAgo)) {
							continue;
						}
					}
					if (p.getClose() != null) {
						String closePrice = p.getClose().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
						// System.out.println(p.getClose());
						String content = stockId + "," + infos[0] + "," + infos[1] + "," + dateValue + "," + closePrice;
						// System.out.println(content);
						priceValues.add(content);
					}
				}
				FileUtils.writeLines(new File(folder + File.separator + stockId + ".csv"), "UTF-8", priceValues);
				break;
			}
		}
		
		System.out.println("开始获取 "+ stockId +" 历史价格结束");
	}

}
