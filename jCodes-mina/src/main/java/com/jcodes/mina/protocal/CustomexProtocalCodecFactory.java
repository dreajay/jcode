package com.jcodes.mina.protocal;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class CustomexProtocalCodecFactory implements ProtocolCodecFactory,
		TcpConstants {

	private ProtocolEncoder encoder;
	private ProtocolDecoder decoder;
	private String handler;
	public static final String LAST_DATA = "lastData";

	public void setHandler(String handler) {
		this.handler = handler;
	}

	public CustomexProtocalCodecFactory() {

		encoder = new ProtocolEncoderAdapter() {

			@Override
			public void encode(IoSession session, Object message,
					ProtocolEncoderOutput out) throws Exception {

				byte[] bs = (byte[]) message;
				IoBuffer buffer = IoBuffer.allocate(bs.length).setAutoExpand(
						true);
				buffer.put(bs);
				buffer.flip();
				out.write(buffer);

			}

		};

		decoder = new CumulativeProtocolDecoder() {

			@Override
			protected boolean doDecode(IoSession session, IoBuffer in,
					ProtocolDecoderOutput out) throws Exception {

				Object[] ret;
				boolean result = false;
				byte[] bs = null;
				if (session.getAttribute(LAST_DATA) != null) {

					byte[] lastData = (byte[]) session.getAttribute(LAST_DATA);
					bs = new byte[lastData.length + in.remaining()];
					System.arraycopy(lastData, 0, bs, 0, lastData.length);
					byte[] nowData = new byte[in.remaining()];
					in.get(nowData);
					System.arraycopy(nowData, 0, bs, lastData.length,
							nowData.length);

				} else {
					bs = new byte[in.remaining()];
					in.get(bs);
				}

				// 根据设置用TcpRecvParser子类实现自定义解析
				Class<?> zz = Class.forName(handler);
				TcpRecvParser parser = (TcpRecvParser) zz.newInstance();
				ret = parser.parseRevContent(bs);
				result = (Boolean) ret[0];

				// 保留剩下的字符
				if (result == true) {

					int readLength = (Integer) ret[1];
					// System.out.println("readLength:" + readLength);
					// System.out.println("bs.length:" + bs.length);
					// 当请求方有分包时，则需要做该判定
					if (bs.length >= readLength) {
						byte[] remainData = new byte[bs.length - readLength];
						byte[] useData = new byte[readLength];
						System.arraycopy(bs, 0, useData, 0, readLength);
						System.arraycopy(bs, readLength, remainData, 0,
								remainData.length);
						session.setAttribute(LAST_DATA, remainData);
						out.write(useData);
						return true;
					} else {
						session.setAttribute(LAST_DATA, bs);
						return false;
					}
				} else {
					session.setAttribute(LAST_DATA, bs);
					return false;
				}

			}

		};
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return encoder;

	}

	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return decoder;
	}

}
