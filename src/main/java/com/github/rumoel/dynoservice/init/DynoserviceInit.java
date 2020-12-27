package com.github.rumoel.dynoservice.init;

import java.io.IOException;
import java.util.ServiceConfigurationError;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.rumoel.dynoservice.configs.DDNSconfig;
import com.github.rumoel.dynoservice.configs.DynoServiceConfig;
import com.github.rumoel.dynoservice.header.DynoserviceHeader;

public class DynoserviceInit extends Thread {
	static Logger logger = LoggerFactory.getLogger(DynoserviceInit.class);
	static ResponseHandler<String> responseHandler;

	public static void main(String[] args) throws IOException {
		responseHandler = new ResponseHandler<String>() {
			@Override
			public String handleResponse(HttpResponse response) throws IOException {
				int status = response.getStatusLine().getStatusCode();
				HttpEntity entity = response.getEntity();
				if (status >= 200 && status < 300) {
					return entity != null ? EntityUtils.toString(entity) : null;
				} else {
					throw new ClientProtocolException("Unexpected response status: " + status);
				}
			}

		};
		if (args.length > 1) {
			if (args[0].equalsIgnoreCase("--getID")) {
				String apiKey = args[1];
				try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
					HttpGet httpGet = new HttpGet("https://api.dynu.com/v2/dns");
					httpGet.setHeader("Content-type", "application/json");
					httpGet.setHeader("API-Key", apiKey);
					String responseBody = httpclient.execute(httpGet, responseHandler);

					logger.info(responseBody);
				}
			} else {
				logger.info("HELP: java -jar FILE.jar --getID APIkey");
			}
			return;
		}
		DynoserviceInit dynoserviceInit = new DynoserviceInit();
		dynoserviceInit.initConfig();
		dynoserviceInit.start();
	}

	private void initConfig() throws IOException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		if (!DynoserviceHeader.getConfigFile().exists()) {
			if (!DynoserviceHeader.getConfigFile().getParentFile().exists()) {
				DynoserviceHeader.getConfigFile().getParentFile().mkdirs();
			}
			if (DynoserviceHeader.getConfigFile().createNewFile()) {
				mapper.writeValue(DynoserviceHeader.getConfigFile(), DynoserviceHeader.getDynoServiceConfig());
				throw new ServiceConfigurationError(
						"please edit " + DynoserviceHeader.getConfigFile().getAbsolutePath());
			}
		}
		DynoServiceConfig newValue = mapper.readValue(DynoserviceHeader.getConfigFile(), DynoServiceConfig.class);
		DynoserviceHeader.setDynoServiceConfig(newValue);
		if (!DynoserviceHeader.getDynoServiceConfig().isPrepare()) {
			throw new ServiceConfigurationError("please edit " + DynoserviceHeader.getConfigFile().getAbsolutePath());
		}
	}

	@Override
	public void run() {
		while (true) {
			for (DDNSconfig ddns : DynoserviceHeader.getDynoServiceConfig().getDdns()) {
				if (!ddns.isPrepare()) {
					continue;
				}
				try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
					String apikey = ddns.getApiKey();

					int id = ddns.getId();
					String group = ddns.getGroup();
					String name = ddns.getName();
					String newIpv4 = DynoserviceHeader.getIpAddrUtils().getExternalIPv4();
					String newIpv6 = DynoserviceHeader.getIpAddrUtils().getExternalIPv6();
					int ttl = ddns.getTtl();
					boolean ipv4 = ddns.isIpv4();
					boolean ipv6 = ddns.isIpv6();

					boolean ipv4WildcardAlias = ddns.isIpv4WildcardAlias();
					boolean ipv6WildcardAlias = ddns.isIpv4WildcardAlias();
					boolean allowZoneTransfer = ddns.isAllowZoneTransfer();
					boolean dnssec = ddns.isDnssec();

					JSONObject jsonData = new JSONObject();
					jsonData.put("name", name);
					jsonData.put("group", group);
					jsonData.put("ipv4Address", newIpv4);
					jsonData.put("ipv6Address", newIpv6);

					jsonData.put("ttl", ttl);
					jsonData.put("ipv4", ipv4);
					jsonData.put("ipv6", ipv6);
					jsonData.put("ipv4WildcardAlias", ipv4WildcardAlias);
					jsonData.put("ipv6WildcardAlias", ipv6WildcardAlias);
					jsonData.put("allowZoneTransfer", allowZoneTransfer);
					jsonData.put("dnssec", dnssec);

					HttpPost httpPost = new HttpPost("https://api.dynu.com/v2/dns/" + id);
					httpPost.setHeader("Content-type", "application/json");
					httpPost.setHeader("API-Key", apikey);

					StringEntity stringEntity = new StringEntity(jsonData.toJSONString());
					httpPost.setEntity(stringEntity);
					String responseBody = httpclient.execute(httpPost, responseHandler);
					String msg = (System.currentTimeMillis() / 1000) + name + ":" + responseBody;
					logger.info(msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			try {
				Thread.sleep(DynoserviceHeader.getDynoServiceConfig().getDelay());
			} catch (Exception e) {
				logger.error("error sleep", e);
			}
		}
	}
}
