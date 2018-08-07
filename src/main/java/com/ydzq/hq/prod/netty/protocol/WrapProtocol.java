package com.ydzq.hq.prod.netty.protocol;

import io.netty.buffer.ByteBuf;

/**
 * SOCKET包头协议
 * 
 * @author f.w
 *
 */
public class WrapProtocol {

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(address+":WrapVo [flag=" + flag + ", type=" + type + ", len=" + len);

		if (bytebuf != null)
			buffer.append(" ,bytebuf=" + bytebuf.toString());

		buffer.append("]");
		return buffer.toString();
	}
 
	public int getFlag() {
		return flag;
	}

	public WrapProtocol setFlag(int flag) {
		this.flag = flag;
		return this;
	}

	public int getType() {
		return type;
	}

	public WrapProtocol setType(int type) {
		this.type = type;
		return this;
	}

	public int getLen() {
		return len;
	}

	public WrapProtocol setLen(int len) {
		this.len = len;
		return this;
	}

	public ByteBuf getBytebuf() {
		return bytebuf;
	}

	public WrapProtocol setBytebuf(ByteBuf bytebuf) {
		this.bytebuf = bytebuf;
		return this;
	}

	private int flag;
	private int type;
	private int len;
	private ByteBuf bytebuf;
	private String address;

}