package com.ydzq.hq.prod.netty.protocol;

import java.util.ArrayList;
import java.util.List; 
import org.apache.commons.lang.StringUtils;
import com.ydzq.hq.enums.E_HqMarket; 

/**
 * SOCKET
 * 
 * @author f.w
 *
 */
public class SocketVo {

	@Override
	public String toString() {
		return "SocketVo [mkt=" + mkt + ", sockets=" + sockets + "]";
	}

	public SocketVo(E_HqMarket mkt, String str) {
		List<Address> list = new ArrayList<Address>();
		if (StringUtils.isNotBlank(str)) {
			String[] mkt_i = str.split(";");
			for (String j : mkt_i) {
				if (StringUtils.isNotBlank(j)) {

					String[] mkt_i_j = j.split(":");
					String host = mkt_i_j[0];
					int port = Integer.parseInt(mkt_i_j[1]);
					list.add(new Address(host, port));
				}
			}
		}
		this.mkt = mkt;
		this.sockets = list;
	}

	public E_HqMarket getMkt() {
		return mkt;
	}

	public SocketVo setMkt(E_HqMarket mkt) {
		this.mkt = mkt;
		return this;
	}

	public List<Address> getSockets() {
		return sockets;
	}

	public SocketVo setSocket(List<Address> sockets) {
		this.sockets = sockets;
		return this;
	}

	public class Address {
		public Address(String host, int port) {
			this.host = host;
			this.port = port;
		}

		@Override
		public String toString() {
			return host + ":" + port;
		}

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		private String host;
		private int port;

	}

	private E_HqMarket mkt;
	private List<Address> sockets;
}