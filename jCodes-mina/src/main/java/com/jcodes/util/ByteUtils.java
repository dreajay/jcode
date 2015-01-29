package com.jcodes.util;

import java.io.UnsupportedEncodingException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ByteUtils {
	
	public static final char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	
	
	//~ 基本类型转ByteArray
	/**
	 * @see #long2Bytes(long f)
	 */
	public static byte[] short2Bytes(short f) {
		return long2Bytes(f, longRealLen(f));
	}
	
	/**
	 * @see #long2Bytes(long f)
	 */
	public static byte[] int2Bytes(int f) {
		return long2Bytes(f, longRealLen(f));
	}
	
	/**
	 * 长整型根据实际占用内存大小转换成对应长度的字节数组.<br>
	 * 例：<br>
	 * long2Bytes(128)，占用1个字节返回0x80		<br>
	 * long2Bytes(10240)，占用2个字节返回0x2800	<br>
	 * @param f
	 * @return
	 */
	public static byte[] long2Bytes(long f) {
		return long2Bytes(f, longRealLen(f));
	}

	/**
	 * 将数字类型转换成字节数组
	 * @param f			数字
	 * @param length	返回的字节数组长度
	 * @return	字节数组
	 */
	public static byte[] long2Bytes(long f, int length) {
		byte[] ret = new byte[length];
		Arrays.fill(ret, (byte) 0);

		int len = longRealLen(f);
		for (int i = 0; i < len; i++) {
			ret[length - len + i] = (byte) ((f >> (len - 1 - i) * 8) & 0xff); // 右移8位 
		}
		return ret;
	}
	
	// 得到整型变量的真实长度
	public static int longRealLen(long f) {
		long s = f;
		int i = 0;
		while (s > 0) {
			s = s >> 8;
			i++;
		}
		return i;
	}

	//~ ByteArray转基本类型
	/**
	 * @see #bytes2Long(byte[] bs)
	 */
	public static short bytes2Short(byte[] bs) {
		return (short) bytes2Long(bs);
	}

	/**
	 * @see #bytes2Long(byte[] bs)
	 */
	public static int bytes2Int(byte[] bs) {
		return (int) bytes2Long(bs);
	}
	
	/**
	 * 将字节数组转换成整数.<br>
	 * @param bs
	 * @return
	 */
	public static long bytes2Long(byte[] bs) {
		long ret = 0;
		int length = bs.length;
		for (int i = 0; i < length; i++) {
			ret += ((long)(bs[i] & 0xff) << (length - 1 - i) * 8); // 左移8位
		}
		return ret;
	}
	
	public static byte[] char2Bytes(char ch) {
		return String.valueOf(ch).getBytes();
	}
	
	public static char bytes2Char(byte[] bs) {
		return new String(bs).charAt(0);
	}
	
	/**
	 * 字符串转字节数组，可忽略UnsupportedEncodingException
	 * @param str
	 * @param encoding
	 * @return
	 */
	public static byte[] str2Bytes(String str, String encoding) {
		try {
			return str.getBytes(encoding);
		} catch (UnsupportedEncodingException e) {
			return str.getBytes();
		}
	}
	
	/**
	 * 字节数组转字符串，可忽略UnsupportedEncodingException
	 * @param bytes
	 * @param encoding
	 * @return
	 */
	public static String bytes2Str(byte[] bytes, String encoding) {
		try {
			return new String(bytes, encoding);
		} catch (UnsupportedEncodingException e) {
			return new String(bytes);
		}
	}
	
	/**
	 * 转换成2进制串
	 * @param bs
	 * @return
	 */
	public static String toBinaryStr(byte[] bs) {
		StringBuilder sb = new StringBuilder();
		int tmp = 0;
		for (int i = 0; i < bs.length; i++) {
			tmp = bs[i] & 0xff;
			sb.append(StringUtils.fillChar(Integer.toBinaryString(tmp), '0', 8, true));
		}
		int end = -1;
		for (int i = 0; i < sb.length(); i++) {
			if (sb.charAt(i) != '0') {
				end = i;
				break;
			}
		}
		if (end != -1) sb.delete(0, end);
		return sb.toString();
	}
	
	/**
	 * 转换成16进制串
	 * @param bs
	 * @return
	 */
	public static String toHexStr(byte[] bs) {
		return toHexStr(bs, false);
	}
	
	/**
	 * @param bs
	 * @param sep 字节之间是否添加空格分隔符
	 * @return
	 */
	public static String toHexStr(byte[] bs, boolean sep) {
		/*StringBuilder sb = new StringBuilder();
		int tmp = 0;
		for (int i = 0; i < bs.length; i++) {
			tmp = bs[i] & 0xff;
			String hex = Integer.toHexString(tmp);
			if (hex.length() < 2)
				sb.append('0').append(hex);
			else
				sb.append(hex);
			if (sep) sb.append(' ');
		}
		int end = -1;
		for (int i = 0; i < sb.length(); i++) {
			if (sb.charAt(i) != '0') {
				end = i;
				break;
			}
		}
		if (end != -1) sb.delete(0, end);
		return sb.toString();*/
		StringBuilder sb = new StringBuilder(bs.length * (sep ? 3 : 2));
		byte byte0;
		for (int i = 0; i < bs.length; i++) {
			byte0 = bs[i];
			// 取字节中高 4 位的数字转换, >>> 为逻辑右移，将符号位一起右移
			sb.append(hexDigits[byte0 >>> 4 & 0xf]);
			// 取字节中低 4 位的数字转换
			sb.append(hexDigits[byte0 & 0xf]);
			if (sep) sb.append(' ');
		}
		return sb.toString();
	}
	
	public static String toHexAsciiStr(byte[] bs) {
		StringBuilder ret = new StringBuilder(new String(bs));
		ret.append(" (0x").append(toHexStr(bs, true).trim()).append(')');
		return ret.toString();
	}
	
	/**
	 * 16进制串转字节数组
	 * @param hex
	 * @return
	 */
	public static byte[] hexStr2Bytes(String hex) {
		int i = 0;
		byte[] data = new byte[hex.length() / 2];
		for (int n = 0; n < hex.length(); n += 2) {
			String temp = hex.substring(n, n + 2);
			int tt = Integer.valueOf(temp, 16);
			data[i] = (byte) tt;
			i++;
		}
		return data;
	}

	/**
	 * 将字节数组转换成char，并拼成字符串
	 * 
	 * @param bytes
	 * @return
	 */
	public static String dumphex(byte[] bytes) {
		int bsLen = bytes.length;
		String head = "-Location- -0--1--2--3--4--5--6--7--8--9--A--B--C--D--E--F--0--1--2--3--4--5--6--7--8--9--A--B--C--D--E--F- ---ASCII Code---\n";
		StringBuilder ret = new StringBuilder(head.length() + bsLen*3);
		ret.append(head);
		for (int i = 0; i < bsLen;) {
			ret.append(lpadding(Integer.toHexString(i), 4, "0")).append('(');
			ret.append(lpadding("" + i, 4, "0")).append(") ");
			for (int j = 0; j < 32; j++) {
				String hex = i + j >= bsLen ? ".." : Integer
						.toHexString((int)(bytes[i + j]&0xff));
				if (hex.length() < 2) ret.append("0");
				ret.append(hex).append(' ');
			}
			ret.append(' ');
			for (int j = 0; j < 32; j++) {
				if (i + j >= bsLen) ret.append('.');
				else if (bytes[i + j] < 20 && bytes[i + j] >= 0) ret.append('*');
				else {
					if (bytes[i + j] > 0) ret.append((char) bytes[i + j]);
					else if (bsLen > i+j+1) {
						String s = new String(bytes, i + j, 2);
						ret.append(s);
						j++;
					} 
					else
						ret.append((char) bytes[i + j]);
				}
			}
			ret.append('\n');
			i += 32;
		}
		return ret.toString();
	}
	
	private static String lpadding(String s, int n, String padding) {
		StringBuilder strbuf = new StringBuilder();
		for (int i = 0; i < n - s.length(); i++) {
			strbuf.append(padding);
		}
		strbuf.append(s);
		return strbuf.toString();
	}
	
	/**
	 * 字节数组填充操作.<br>
	 * @param rs 源字节数组
	 * @param ch 填充的字节
	 * @param num 目标字节数组长度
	 * @param left 左填充还是右填充
	 * @return 补全后的字节数组
	 */
	public static byte[] fillByte(byte[] rs, byte ch, int num, boolean left) {
		int rsLen = rs.length;
		byte[] ret = new byte[Math.abs(num)];
		Arrays.fill(ret, ch);
		// 左补全
		if (left) {
			if (num >= rsLen)
				System.arraycopy(rs, 0, ret, num - rsLen, rsLen);
			else
				System.arraycopy(rs, 0, ret, 0, ret.length);
		} else {
			if (num >= rsLen)
				System.arraycopy(rs, 0, ret, 0, rsLen);
			else
				System.arraycopy(rs, 0, ret, 0, ret.length);
		}
		return ret;
	}
	
	/**
	 * 删除字节数组的填充字节
	 * @param rs
	 * @param ch
	 * @param left true为左填充，false为右填充
	 * @return
	 */
	public static byte[] removeFillByte(byte[] rs, byte ch, boolean left) {
		if (left) {
			if (rs[0] != ch) return rs;
			int idx = rs.length;
			for (int i = 0; i < rs.length; i++) {
				if (rs[i] != ch) {
					idx = i;
					break;
				}
			}
			byte[] ret = new byte[rs.length - idx];
			System.arraycopy(rs, idx, ret, 0, ret.length);
			return ret;
		} else {
			if (rs[rs.length-1] != ch) return rs;
			int idx = -1;
			for (int i = rs.length - 1; i >= 0; i--) {
				if (rs[i] != ch) {
					idx = i;
					break;
				}
			}
			byte[] ret = new byte[idx + 1];
			System.arraycopy(rs, 0, ret, 0, ret.length);
			return ret;
		}
	}
	
	public static byte[] getRemainBytes(ByteBuffer bb) {
		if (!bb.hasRemaining()) throw new BufferUnderflowException(); 
		byte[] data = bb.array();
		byte[] remain = new byte[bb.remaining()];
		System.arraycopy(data, bb.position(), remain, 0, remain.length);
		return remain;
	}
	
	public static byte[] getUsedBytes(ByteBuffer bb) {
		byte[] data = bb.array();
		byte[] remain = new byte[bb.position()];
		System.arraycopy(data, 0, remain, 0, remain.length);
		return remain;
	}
	
	/**
	 * 用于检查数组偏移量及后面长度是否越界，即off/len/off+len是否超出size大小.<br>
	 * 如：checkBounds(10, 20, bs.length) 检查offset为10，len为20时，是否超出了数组bs的长度
	 * @param off 偏移位置
	 * @param len 偏移位置后len长度
	 * @param size 数组大小
	 */
	public static void checkBounds(int off, int len, int size) throws IndexOutOfBoundsException {
		if ((off | len | (off + len) | (size - (off + len))) < 0)
			throw new IndexOutOfBoundsException();
	}
	
	/**
	 * 将byte[]基本类型包装成Byte[]
	 * @param bs
	 * @return
	 */
	public static Byte[] wraps(byte[] bs) {
		Byte[] ret = new Byte[bs.length];
		for (int i = 0; i < bs.length; i++)
			ret[i] = bs[i];
		return ret;
	}
	
	/**
	 * 将Byte[]转换成基本类型byte[]
	 * @param bs
	 * @return
	 */
	public static byte[] unwraps(Byte[] bs) {
		byte[] ret = new byte[bs.length];
		for (int i = 0; i < bs.length; i++)
			ret[i] = bs[i];
		return ret;
	}
	
	/**
	 * 将字节数组bs反序，即高低位互换(Big Endian与Little Endian变换)
	 * @param bs
	 * @return
	 */
	public static byte[] reverse(byte[] bs) {
		int len = bs.length;
		byte[] ret = new byte[len];
		for (int i = 0;i < len; i++) {
			ret[len-1-i] = bs[i];
		}
		return ret;
	}
	
	public static byte[] copyOf(byte[] original, int newLength) {
        byte[] copy = new byte[newLength];
        System.arraycopy(original, 0, copy, 0,
                         Math.min(original.length, newLength));
        return copy;
    }
	
	/**
	 *  在源数组中找出与目标数组，完全匹配的下标。
	 * @param source
	 * @param fromIndex 原数组中偏移量
	 * @param dst
	 * @return
	 */
	public static int indexOf(byte[] source,int fromIndex, byte[] dst)
	{
		if (fromIndex >= source.length) {
            return (dst.length == 0 ? source.length : -1);
		}
		
		if (fromIndex < 0) {
    	    fromIndex = 0;
    	}
		
		if (dst.length  == 0) {
		    return fromIndex;
		}
		
		byte first  = dst[0];
        int max = source.length - dst.length;
        for (int i = fromIndex; i <= max; i++) {
            /* Look for first character. */
            if (source[i] != first) {
                while (++i <= max && source[i] != first);
            }

            /* Found first character, now look at the rest of v2 */
            if (i <= max) {
                int j = i + 1;
                int end = j + dst.length - 1;
                for (int k =  1; j < end && source[j] ==
                		dst[k]; j++, k++);

                if (j == end) {
                    /* Found whole string. */
                    return i;
                }
            }
        }
        return -1;
	}
	
	/**
	 * 把字节数组转换为mock文件格式的字符串
	 * @param bs
	 * @return
	 */
	public static String bytes2MockStr(byte[] bs) {
		int length = bs.length;
		StringBuilder sb = new StringBuilder(length+2);
		int byteToInt;
		int visible = 0;
		int invisible = 0;
		for (int k = 0; k < length;k++) {
			// 双字节字符，汉字，范围0x8140 - 0xFEFE(129-254 64-254)
			byteToInt = bs[k] & 0xFF;
			if (byteToInt >= 129 && byteToInt <= 254) {
				if (k + 1 < length) {
					int byteToInt2 = bs[k + 1] & 0xFF;
					if (byteToInt2 >= 64 && byteToInt2 <= 254) {
						if(visible == 0 && invisible == 0) {
							sb.append("[");
						} else if(visible == 0 && invisible > 0) {
							sb.append("][");
						} 
						sb.append(new String(new byte[] { bs[k], bs[k + 1] }));
						k++;// 这里和外层的k++和起来，下次循环的时候会跳过两个字节
						visible++;
						invisible = 0;
						if(k == length - 1) sb.append("]");
						continue;
					}
				}
				
			}
			if (byteToInt == 9 || byteToInt == 10 || byteToInt == 13 || (byteToInt >= 32 && byteToInt <= 126)){// 可见单字节字符
				if(visible == 0 && invisible == 0) {
					sb.append("[");
				} else if(visible == 0 && invisible > 0) {
					sb.append("][");
				} 
				sb.append((char) byteToInt);
				visible++;
				invisible = 0;
			} else {// 不可见单字节字符 除了\t、\r、\n
				if(invisible == 0 && visible == 0) {
					sb.append("[0X");
				} else if(invisible == 0 && visible > 0) {
					sb.append("][0X");
				} 
				String t = Integer.toHexString(byteToInt);
				if (t.length() == 1)
					t = "0" + t;
				sb.append(t.toUpperCase());
				invisible++;
				visible = 0;
			}
			
			if(k == length - 1) {
				sb.append("]");
			}
		}
		return sb.toString();
	}
}
