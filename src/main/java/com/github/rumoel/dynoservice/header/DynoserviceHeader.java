package com.github.rumoel.dynoservice.header;

import java.io.File;

import com.github.rumoel.dynoservice.configs.DynoServiceConfig;
import com.github.rumoel.utils.IpAddrUtils;

import lombok.Getter;
import lombok.Setter;

public class DynoserviceHeader {
	private DynoserviceHeader() {
	}

	@Getter
	private static IpAddrUtils ipAddrUtils = new IpAddrUtils();
	@Getter
	@Setter
	private static DynoServiceConfig dynoServiceConfig = new DynoServiceConfig();

	@Getter
	private static File rootDir = new File("configsDynoService");
	@Getter
	private static File configFile = new File(rootDir, "config.yml");
}
