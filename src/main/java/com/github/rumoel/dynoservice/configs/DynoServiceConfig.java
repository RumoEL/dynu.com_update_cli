package com.github.rumoel.dynoservice.configs;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.rumoel.dynoservice.header.DynoserviceHeader;

import lombok.Getter;
import lombok.Setter;

public class DynoServiceConfig {
	@Getter
	@Setter
	private ArrayList<DDNSconfig> ddns = new ArrayList<>();
	Logger logger = LoggerFactory.getLogger(getClass());

	@Getter
	@Setter
	private boolean prepare = false;

	@Getter
	@Setter
	private int delay = (10 * 60 * 1000);

	public DynoServiceConfig() {
		for (int i = 0; i < 2; i++) {
			DDNSconfig ddnsConfig = new DDNSconfig();
			if (i == 1) {
				ddnsConfig.setPrepare(true);
			}
			ddnsConfig.setName("example.com");
			String ipv4Address = DynoserviceHeader.getIpAddrUtils().getExternalIPv4();
			ddnsConfig.setIpv4Address(ipv4Address);
			ddns.add(ddnsConfig);
		}
	}
}
