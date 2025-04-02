package org.omnione.did.wallet.util.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.Provider;
import java.security.Security;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.omnione.did.wallet.util.GDPLogger;

/**
 * HTTP Client
 *
 * @author tykim
 *
 */
public class HttpClient {

	/**
	 * HTTP Connection READ_TIMEOUT
	 */
	public static int READ_TIMEOUT = 60000;

	/**
	 * HTTP Connection CONNECT_TIMEOUT
	 */
	public static int CONNECT_TIMEOUT = 60000;

	/**
	 * HTTPS Valid Check
	 */
	public static boolean VALID_HTTPS = false;
	
	public static boolean VALID_HTTPS_SETTING = false;

	public String send(List<String> serverUrlList, String subUrl, String json) throws HttpException {
		HttpException lastException = null;
		for (String serverUrl : serverUrlList) {
			try {
				return send(serverUrl + subUrl, json);
			} catch (HttpException e) {
				lastException = e;
				if (e.getCause() instanceof ConnectException) {
					System.out.println(e.getCause());
					continue;
				} else if (e.getCause() instanceof SocketTimeoutException) {
					System.out.println(e.getCause());
					continue;
				} else if (e.getCause() instanceof IOException) {
					System.out.println(e.getCause());
					continue;
				} else {
					break;
				}
			}
		}
		throw lastException;
	}

	public String send(String serverUrl, String json) throws HttpException {
		return send(serverUrl, json, null);
	}
	
	public String send(String serverUrl, String json, Map<String, String> headerMap) throws HttpException {
		
		GDPLogger.print("serverUrl", serverUrl);
		GDPLogger.print("json", json);

		String contentType = "application/json; charset=UTF-8";

		String charset = "UTF-8";
		String method = "POST";
		String url = serverUrl;

		InputStream is = null;
		OutputStream writer = null;
		HttpURLConnection connection = null;
		String resultMsg = "";
		int code = 0;
		
		
		BufferedReader rd = null;
		try {
			URL urlO = new URL(url);
			GDPLogger.print("1");

			if (urlO.getProtocol().toLowerCase().equals("https")) {
				if (!VALID_HTTPS) {
					GDPLogger.print("2");
					trustAllHosts();
					HttpsURLConnection conn = (HttpsURLConnection) urlO.openConnection();
					conn.setHostnameVerifier(new HostnameVerifier() {
						public boolean verify(String paramString, SSLSession paramSSLSession) {
							return true;
						}
					});
					connection = conn;
				} else {
					HttpsURLConnection conn = (HttpsURLConnection) urlO.openConnection();
					connection = conn;
				}
			} else {
				connection = (HttpURLConnection) urlO.openConnection();
			}

			if (contentType != null) {
				connection.setRequestProperty("Content-Type", contentType);
			}

			GDPLogger.print("HTTP Send Data Length", json.getBytes(charset).length + "");

			connection.setRequestProperty("Content-Length", String.valueOf(json.getBytes(charset).length));
			if (headerMap != null && !headerMap.isEmpty()) {
				Set<String> keys =  headerMap.keySet();
				for (String key : keys) {
					connection.setRequestProperty(key, headerMap.get(key));
				}
			}
			
			
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setDefaultUseCaches(false);
			connection.setRequestMethod(method);
			connection.setReadTimeout(READ_TIMEOUT);
			connection.setConnectTimeout(CONNECT_TIMEOUT);
			writer = connection.getOutputStream();
			writer.write(json.getBytes(charset));
			writer.flush();
			writer.close();

			code = connection.getResponseCode();
			
			if(GDPLogger.FLAG) {
				GDPLogger.print("code => ["+code +"]");
			}
			
			if (code < HttpURLConnection.HTTP_BAD_REQUEST) {
				is = connection.getInputStream();
			} else {
				is = connection.getErrorStream();
			}
			
			if(GDPLogger.FLAG) {
				GDPLogger.print("inputstream => ["+is +"]");
			}
			
			String line = null;
			StringBuffer resp = new StringBuffer();
			rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			while ((line = rd.readLine()) != null) {
				resp.append(line);
			}
//			rd.close();
//			

			resultMsg = resp.toString();
						
		} catch (Exception ex) {
			if(GDPLogger.FLAG) {
				ex.printStackTrace();
			}
			throw new HttpException(ex.getMessage(), ex);

		} finally {
			if (rd != null) {
				try {
					rd.close();
				} catch (IOException e1) {
					rd = null;
				}
			}
			if (writer != null)
				try {
					writer.close();
				} catch (Exception e) {
					writer = null;
				}
			
			if(connection  != null) {
				connection.disconnect();
			}
			
		}

		if (code >= HttpURLConnection.HTTP_BAD_REQUEST) {
			GDPLogger.debug("HTTP ERROR CODE: " + code);
			GDPLogger.debug(resultMsg);
			throw new HttpException(code, resultMsg);
		}
		GDPLogger.print("HTTP RECV Data Length", resultMsg.getBytes().length + "");
		GDPLogger.debug(resultMsg);
		return resultMsg;
	}
	
	public String getJson(String serverUrl) throws HttpException {
		return getJson(serverUrl, null);
	}
	
	public String getJson(String serverUrl, Map<String, String> headerMap) throws HttpException {

		GDPLogger.print("serverUrl", serverUrl);

		String contentType = "application/json; charset=UTF-8";

		String charset = "UTF-8";
		String method = "GET";
		String url = serverUrl;

		InputStream is = null;
		OutputStream writer = null;
		HttpURLConnection connection = null;
		String resultMsg = "";
		int code = 0;

		BufferedReader rd = null;
		try {
			URL urlO = new URL(url);

			if (urlO.getProtocol().toLowerCase().equals("https")) {
				if (!VALID_HTTPS) {
					trustAllHosts();
					HttpsURLConnection conn = (HttpsURLConnection) urlO.openConnection();
					conn.setHostnameVerifier(new HostnameVerifier() {
						public boolean verify(String paramString, SSLSession paramSSLSession) {
							return true;
						}
					});
					connection = conn;
				} else {
					HttpsURLConnection conn = (HttpsURLConnection) urlO.openConnection();
					connection = conn;
				}
			} else {
				connection = (HttpURLConnection) urlO.openConnection();
			}

			if (contentType != null) {
				connection.setRequestProperty("Content-Type", contentType);
			}

			if (headerMap != null && !headerMap.isEmpty()) {
				Set<String> keys =  headerMap.keySet();
				for (String key : keys) {
					connection.setRequestProperty(key, headerMap.get(key));
				}
			}
			
			connection.setRequestProperty("Content-Length", "0");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setDefaultUseCaches(false);
			connection.setRequestMethod(method);
			connection.setReadTimeout(READ_TIMEOUT);
			connection.setConnectTimeout(CONNECT_TIMEOUT);
			connection.connect();
			

			code = connection.getResponseCode();
			if (code < HttpURLConnection.HTTP_BAD_REQUEST) {
				is = connection.getInputStream();
			} else {
				is = connection.getErrorStream();
			}

			String line = null;
			StringBuffer resp = new StringBuffer();
			rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			while ((line = rd.readLine()) != null) {
				resp.append(line);
			}
//			rd.close();

			resultMsg = resp.toString();

		} catch (Exception ex) {
			if(GDPLogger.FLAG) {
				ex.printStackTrace();
			}
			throw new HttpException(ex.getMessage(), ex);

		} finally {
			if (rd != null) {
				try {
					rd.close();
				} catch (IOException e1) {
					rd = null;
				}
			}
			if (writer != null)
				try {
					writer.close();
				} catch (Exception e) {
					writer = null;
				}
		}

		if (code >= HttpURLConnection.HTTP_BAD_REQUEST) {
			GDPLogger.debug("HTTP ERROR CODE: " + code);
			GDPLogger.debug(resultMsg);
			throw new HttpException(code, resultMsg);
		}
		GDPLogger.print("HTTP RECV Data Length", resultMsg.getBytes().length + "");
		GDPLogger.debug(resultMsg);
		return resultMsg;
	}
	

	private static void trustAllHosts() {
		if(!VALID_HTTPS_SETTING) {
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return new java.security.cert.X509Certificate[] {};
				}
	
				public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
				}
	
				public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
				}
			} };
			try {
				// SSLContext sc = SSLContext.getInstance("TLS");
				SSLContext sc = null;
				if (System.getProperty("java.version").startsWith("1.6.") == true) {
					//1.6버전에서는 tls1.2 버전을 지원하지 않음, EOS Node는 tls1.2 버전만 사용하기 때문에 
					//jdk 버전을 올리던, 아래와 같이 bouncycastle을 사용해야함 
					//bouncycastle tls lib 
					bouncyCastleProviderLoad();
					sc = SSLContext.getInstance("TLSv1.2", "BCJSSE");
				}else {
					sc = SSLContext.getInstance("TLSv1.2");
				}
				
				sc.init(null, trustAllCerts, new java.security.SecureRandom());
				HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			VALID_HTTPS_SETTING = true;
		}
	}
	
	private static void bouncyCastleProviderLoad() {
		if (Security.getProvider("BC") == null) {
			try {
				
				
				Class bouncyCastleProviderClass = Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider");
				Object bouncyCastleProviderObj = bouncyCastleProviderClass.newInstance();

				Security.addProvider((Provider) bouncyCastleProviderObj);
				GDPLogger.debug("Class Instance : org.bouncycastle.jce.provider.BouncyCastleProvider : "+ bouncyCastleProviderObj.getClass().getName());
				
				
				Class bouncyCastleJsseProviderClass = Class.forName("org.bouncycastle.jsse.provider.BouncyCastleJsseProvider");
				Object bouncyCastleJsseProviderObj = bouncyCastleJsseProviderClass.getDeclaredConstructor(Provider.class).newInstance(bouncyCastleProviderObj);
				
				
				Security.addProvider((Provider) bouncyCastleJsseProviderObj);
				GDPLogger.debug("Class Instance : org.bouncycastle.jsse.provider.BouncyCastleJsseProvider : "+ bouncyCastleJsseProviderObj.getClass().getName());

				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
