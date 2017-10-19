package com.yuexiu.stockprice;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
	
	static String folder ="";

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
		
		folder = args[0];
		getPriceHistory("hk00405","0405.HK");
		getPriceHistory("hk06139","6139.HK");
		getPriceHistory("hk87001","87001.HK");
		getPriceHistory("hk01426","1426.HK");
		getPriceHistory("hk01275","1275.HK");
		getHISPrices();

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
		String result = EntityUtils.toString(httpEntity);// ȡ��Ӧ���ַ���
		String content = result.split("\"")[1];
		//System.out.println(content);
		return content.split(",");
	}
	
	private static void getHISPrices() throws Exception {
		
		//https://hk.finance.yahoo.com/quote/%5EHSI/history?p=%5EHSI
		
		HttpClient client = wrapClient(new DefaultHttpClient());
		HttpGet get = new HttpGet("https://hk.finance.yahoo.com/quote/%5EHSI/history?p=%5EHSI");
		HttpResponse response = client.execute(get);
		// System.out.println(response.getStatusLine().getStatusCode());
		if (response.getStatusLine().getStatusCode() != 200) {
			System.exit(-1);
		}
		HttpEntity httpEntity = response.getEntity();
		String result = EntityUtils.toString(httpEntity);// ȡ��Ӧ���ַ���
		//System.out.println(result);
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
				for (Price p : list) {
					Date date = new Date(p.getDate() * 1000);
					String dateValue = format.format(date);
					if (p.getClose() != null) {
						String closePrice = p.getClose().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
						// System.out.println(p.getClose());
						String content ="HIS,HIS,恒生指数," + dateValue + "," + closePrice;
						// System.out.println(content);
						priceValues.add(content);
					}
				}
				FileUtils.writeLines(new File(folder + File.separator +  "HIS.csv"), "UTF-8",priceValues);
				break;
			}
		}
	}

	private static void getPriceHistory(String hkCode, String stockId)
			throws Exception, IOException, ClientProtocolException {

		String[] infos = getNameInfo(hkCode);
		HttpClient client = wrapClient(new DefaultHttpClient());
		HttpGet get = new HttpGet("https://hk.finance.yahoo.com/quote/" + stockId + "/history?p=" + stockId);
		HttpResponse response = client.execute(get);
		// System.out.println(response.getStatusLine().getStatusCode());
		if (response.getStatusLine().getStatusCode() != 200) {
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

				List<String> priceValues = new ArrayList<String>();
				for (Price p : list) {
					Date date = new Date(p.getDate() * 1000);
					String dateValue = format.format(date);
					if (p.getClose() != null) {
						String closePrice = p.getClose().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
						// System.out.println(p.getClose());
						String content = stockId + "," + infos[0] + "," + infos[1] + "," + dateValue + "," + closePrice;
						// System.out.println(content);
						priceValues.add(content);
					}
				}
				FileUtils.writeLines(new File(folder + File.separator +stockId + ".csv"), "UTF-8",priceValues);
				break;
			}
		}
	}

}
