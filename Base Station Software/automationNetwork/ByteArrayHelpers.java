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
	public static int fromByteArray(byte[] bytes) 
	{
	     return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
	}
	
	public static String getByteArrayString(byte[] bytes)
	{
		StringBuilder sBuilder = new StringBuilder();
		
		for (byte b : bytes) 
		{
			sBuilder.append(String.format("%d ", b));
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
