package com.jcodes.mina.protocal;

public interface TcpConstants {

	public static interface RecvPacketRuleConstants {

		/** 接包规则类型：定长 */
		public static final String TYPE_FIXLENGTH = "fixLength";
		/** 接包规则类型：长度标识 */
		public static final String TYPE_LENGTHIDENTIFY = "lengthIdentify";
		/** 接包规则类型：结束符 */
		public static final String TYPE_ENDCHAR = "endChar";
		/** 接包规则类型：自定义 */
		public static final String TYPE_CUSTOM = "custom";

		public static final String TYPE_CUSTOMEX = "customex";
	}

	public static interface HeartBeatRuleConstants {

		/** 心跳包内容规则类型：字符序 */
		public static final String TYPE_CHARS = "chars";
		/** 心跳包内容规则类型：字节码序 */
		public static final String TYPE_BYTES = "bytes";
	}

	public static interface LengthIdentifyConstants {

		/** 长度标识字段类型：字符序 */
		public static final String VALTYPE_CHARS = "chars";
		/** 长度标识字段类型：网络字节序 */
		public static final String VALTYPE_BYTES = "bytes";
		/** 长度标识字段类型：反网络字节序 */
		public static final String VALTYPE_OBYTES = "obytes";

		/** 长度标识字段长度类型:字符码 */
		public static final String FILLTYPE_CHAR = "char";
		/** 长度标识字段长度类型:字节码 */
		public static final String FILLTYPE_BYTE = "byte";

		/** 长度标识字段填充方向:左填充 */
		public static final String FILLLOCATION_LEFT = "left";
		/** 长度标识字段填充方向:右填充 */
		public static final String FILLLOCATION_RIGHT = "right";
	}

	public static interface ObjectSerialConstants {

		/** 组包支持最大对象大小 */
		public static final int ENCODE_MAX_SIZE = 1 << 20;
		/** 解包支持最大对象大小 */
		public static final int DECODE_MAX_SIZE = 1 << 20;
	}

}
