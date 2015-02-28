package com.jcodes.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class DatagramChannelServer {
	DatagramChannel channel;

	public DatagramChannelServer(int port) {
		try {
			channel = DatagramChannel.open();
			// datagramChannel.bind(new InetSocketAddress(port));
			channel.socket().bind(new InetSocketAddress(port));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void read(DatagramChannel channel,ByteBuffer buffer) {
		try {
			buffer.clear();
			while (channel.read(buffer) != -1) {
				channel.read(buffer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void write(DatagramChannel channel,ByteBuffer buffer) {
		try {
			buffer.clear();
			while (channel.read(buffer) != -1) {
				channel.read(buffer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {

	}

}
