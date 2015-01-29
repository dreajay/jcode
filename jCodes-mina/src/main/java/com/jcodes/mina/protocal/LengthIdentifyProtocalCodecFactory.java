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

import com.jcodes.util.ByteUtils;

/**
 * 长度标示协议报文
 * 
 */
public class LengthIdentifyProtocalCodecFactory implements
		ProtocolCodecFactory, TcpConstants.LengthIdentifyConstants {

	public static final String LENGTH = "LENGTH";
	protected static final Object COMMHEAD_BS = "LENGTH_BYTEARRAY";
	private int offset = 0;
	/** 定长报文总长度 */
	private int lenFieldLen = 0;
	private String lenFieldType;
	private String lenFillType;
	private String lenFillChar;
	private String lenFillLocation;
	private int reduce = 0;

	private ProtocolEncoder encoder;
	private ProtocolDecoder decoder;

	public void setLenFillType(String string) {
		this.lenFillType = string;
	}

	public void setLenFillChar(String string) {
		this.lenFillChar = string;
	}

	public void setLenFillLocation(String string) {
		this.lenFillLocation = string;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void setLenFieldLen(int lenFieldLen) {
		this.lenFieldLen = lenFieldLen;
	}

	public void setReduce(int reduce) {
		this.reduce = reduce;
	}

	public void setLenFieldType(String valType) {
		this.lenFieldType = valType;
	}

	public LengthIdentifyProtocalCodecFactory() {
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
				if (in.remaining() < (offset + lenFieldLen))
					return false;
				if (session.getAttribute(LENGTH) == null) {
					byte[] commHead = new byte[offset + lenFieldLen];
					in.get(commHead, 0, offset + lenFieldLen);
					byte[] lenField = new byte[lenFieldLen];
					System.arraycopy(commHead, offset, lenField, 0, lenFieldLen);
					session.setAttribute(COMMHEAD_BS, commHead);
					session.setAttribute(LENGTH, getLengthFieldValue(lenField));
				}
				int len = (Integer) session.getAttribute(LENGTH);
				// len - reduce为应接包总长度
				len = len - reduce;
				if (in.remaining() < len)
					return false;
				byte[] message = new byte[len];
				in.get(message);
				byte[] commHead = (byte[]) session.getAttribute(COMMHEAD_BS);
				byte[] allData = new byte[commHead.length + len];
				System.arraycopy(commHead, 0, allData, 0, commHead.length);
				System.arraycopy(message, 0, allData, commHead.length, len);
				out.write(allData);
				// 清理缓存，缓存信息不能放在dispose里，因为对于长连接未关闭session，所以未能调用到dispose
				session.removeAttribute(LENGTH);
				session.removeAttribute(COMMHEAD_BS);
				return true;
			}

			@Override
			public void dispose(IoSession session) throws Exception {
				super.dispose(session);
			}
		};
	}

	/**
	 * 根据字符序或字节序(网络字节序)获取长度字段的值
	 */
	private int getLengthFieldValue(byte[] bs) {
		if (VALTYPE_CHARS.equals(lenFieldType)) {
			boolean left = true;
			if (FILLLOCATION_RIGHT.equals(lenFillLocation))
				left = false;
			byte ch = 0;
			if (FILLTYPE_CHAR.equals(lenFillType)) {
				ch = lenFillChar.getBytes()[0];
			} else if (FILLTYPE_BYTE.equals(lenFillType)) {
				try {
					ch = Byte.parseByte(lenFillChar);
				} catch (NumberFormatException e) {
					ch = 0;
				}
			}
			// 处理后得到的bField已除去了填充字符
			byte[] bField = ByteUtils.removeFillByte(bs, (byte) ch, left);
			try {
				return Integer.parseInt(new String(bField).trim());
			} catch (NumberFormatException e) {
				return 0;
			}
		} else if (VALTYPE_BYTES.equals(lenFieldType)) {
			// 因为考虑是数字类型，故填充字节肯定是ascii为0的字节，同样obytes也是
			return ByteUtils.bytes2Int(bs);
		} else if (VALTYPE_OBYTES.equals(lenFieldType)) {
			byte[] bField = ByteUtils.reverse(bs);
			return ByteUtils.bytes2Int(bField);
		}
		return 0;
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
