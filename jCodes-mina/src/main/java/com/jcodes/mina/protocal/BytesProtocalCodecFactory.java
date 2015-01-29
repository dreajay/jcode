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

public class BytesProtocalCodecFactory implements ProtocolCodecFactory {
	
	
	private ProtocolEncoder encoder;
	private ProtocolDecoder decoder;
	
	public BytesProtocalCodecFactory(){
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
				byte[] buff = new byte[in.remaining()];
				in.get(buff);
				out.write(buff);
				return true;
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
