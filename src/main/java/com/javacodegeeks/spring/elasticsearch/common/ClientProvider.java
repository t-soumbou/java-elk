package com.javacodegeeks.spring.elasticsearch.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class ClientProvider {

	private static TransportClient client = buildClient();

	public static TransportClient getClient() {
		return client;
	}

	@SuppressWarnings("resource")
	private static TransportClient buildClient() {
		TransportClient client = null;
		try {
			client = new PreBuiltTransportClient(Settings.EMPTY)
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(""), 9300));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return client;
	}
}
