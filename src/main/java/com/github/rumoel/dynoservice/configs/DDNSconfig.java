package com.github.rumoel.dynoservice.configs;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DDNSconfig {
	private String apiKey = UUID.randomUUID().toString();

	private int id = Integer.MAX_VALUE;
	private String name = "somedomain.com";
	private String group = "office";
	private String ipv4Address = "1.2.3.4";
	private String ipv6Address = "1111:2222:3333::4444";
	private int ttl = 90;
	private boolean ipv4 = true;
	private boolean ipv6 = true;

	private boolean ipv4WildcardAlias = true;
	private boolean ipv6WildcardAlias = true;
	private boolean allowZoneTransfer = false;
	private boolean dnssec = false;

	private boolean prepare = false;
}
