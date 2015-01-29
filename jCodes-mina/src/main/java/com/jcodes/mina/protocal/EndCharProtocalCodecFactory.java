package com.jcodes.mina.protocal;

import java.io.ByteArrayOutputStream;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;


/**
 * 分隔符报文协议
 *
 */
public class EndCharProtocalCodecFactory implements ProtocolCodecFactory {
	
	public static final String LAST_ALLDATA = "LAST_ALLDATA";
	/**
	 * 结束字符
	 */
	private byte endChar;
	
	/**
	 * 转义字符
	 */
	private byte escapeChar = '\\';
	
	private ProtocolEncoder encoder;
	private ProtocolDecoder decoder;

	public void setEndChar(String endChar) {
		if(endChar.matches("0[x,X][0-9,a-f,A-F]+"))
			this.endChar = Integer.decode(endChar).byteValue();
		else
			this.endChar = endChar.getBytes()[0];
	}

	public void setEscapeChar(String escapeChar) {
		this.escapeChar = escapeChar.getBytes()[0];
	}

	public EndCharProtocalCodecFactory() {
		encoder = new ProtocolEncoderAdapter() {
			@Override
			public void encode(IoSession session, Object message,
					ProtocolEncoderOutput out) throws Exception {
				byte[] bs = (byte[]) message;
				IoBuffer buffer = IoBuffer.allocate(bs.length).setAutoExpand(true);
				buffer.put(bs);
		        buffer.flip();
		        out.write(buffer);
			}
		};
		decoder = new CumulativeProtocolDecoder() {
			@Override
			protected boolean doDecode(IoSession session, IoBuffer in,
					ProtocolDecoderOutput out) throws Exception {
				int remain = in.remaining();
				byte[] temp = new byte[remain];
				in.get(temp);
				if (session.getAttribute(LAST_ALLDATA) == null) {
					session.setAttribute(LAST_ALLDATA, new ByteArrayOutputStream(512));
				}
				//修复大数据量时没有取出session中数据问题
				ByteArrayOutputStream bos = (ByteArrayOutputStream) session.getAttribute(LAST_ALLDATA);
				int len = bos.size();
				bos.write(temp);
				byte[] allData = temp;
				if (allData[0] == endChar) {
					out.write(new byte[]{allData[0]});
			    	session.removeAttribute(LAST_ALLDATA);
					return true;
				}
				for (int i = 1; i < allData.length; i++) {
					if (allData[i-1] != escapeChar && allData[i] == endChar) {
						byte[] buf = bos.toByteArray();
						byte[] towrite = new byte[len + i + 1];
						System.arraycopy(buf, 0, towrite, 0, len + i + 1);
						out.write(towrite);
						bos.flush();
						bos.close();
				    	session.removeAttribute(LAST_ALLDATA);
						return true;
					}
				}
				return false;
			}
			 @Override
		    public void dispose(IoSession session) throws Exception {
		    	super.dispose(session);
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
