package com.github.rumoel.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class IpAddrUtils {
	private ArrayList<String> ipv4Resolvers = new ArrayList<>();
	private ArrayList<String> ipv6Resolvers = new ArrayList<>();

	public IpAddrUtils() {
		ipv4Resolvers.add("http://checkip.amazonaws.com");
		ipv4Resolvers.add("http://ident.me");
	}

	public String getExternalIPv4() {
		return getDataFromResolver(ipv4Resolvers);
	}

	public String getExternalIPv6() {
		return getDataFromResolver(ipv6Resolvers);
	}

	private String getDataFromResolver(ArrayList<String> resolvers) {
		for (String resolver : resolvers) {
			String answ = null;
			try {
				answ = getDataFromUrl(resolver);
				if (answ == null) {
					continue;
				}
				return answ;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private String getDataFromUrl(String resolver) throws IOException {
		String ip;
		URL whatismyip = new URL(resolver);
		try (BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()))) {
			ip = in.readLine();
			if (ip != null) {
				return ip;
			}
		}
		return null;
	}
}
