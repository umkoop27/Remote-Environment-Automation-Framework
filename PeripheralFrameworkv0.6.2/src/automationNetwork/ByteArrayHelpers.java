/**
 * 
 */
package automationNetwork;

/**
 * Some static methods for dealing with bytes and byte arrays.
 * @author Paul
 *
 */



public class ByteArrayHelpers 
{
	public enum ByteArrayMode 
	{
		BYTE_ARRAY_MODE_HEX,
		BYTE_ARRAY_MODE_DEC
	}
	
	public static int fromByteArray(byte[] bytes) 
	{
	     return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
	}
	
	public static String getByteArrayString(byte[] bytes)
	{
		return getByteArrayString(bytes, ByteArrayMode.BYTE_ARRAY_MODE_HEX);
	}
	
	public static String getByteArrayString(byte[] bytes, ByteArrayMode mode)
	{
		StringBuilder sBuilder = new StringBuilder();
		
		for (byte b : bytes) 
		{
			switch (mode) 
			{
			case BYTE_ARRAY_MODE_HEX:
				sBuilder.append(String.format("0x%x ", b));
				break;

			case BYTE_ARRAY_MODE_DEC:
				sBuilder.append(String.format("%d ", b));
				break;

			default:
				break;
			}			
		}
		return sBuilder.toString();
	}
	
	public static byte[] concat(byte[] A, byte[] B) 
	{
		int aLen = A.length;
		int bLen = B.length;
		byte[] C= new byte[aLen+bLen];
		System.arraycopy(A, 0, C, 0, aLen);
		System.arraycopy(B, 0, C, aLen, bLen);
		return C;
	}

}
