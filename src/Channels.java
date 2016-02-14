
public class Channels 
{
	public static int getBGR(byte b, byte g, byte r)
	{
		return ((0xFF & b) << 16) | ((0xFF & g) << 8) | (0xFF & r);
	}
	
	public static byte[] getChannels(int bgr)
	{
		byte b = (byte) ((bgr >> 16) & 0xff);
		byte g = (byte) ((bgr >> 8) & 0xff);
		byte r = (byte) ((bgr >> 0) & 0xff);
		
		byte[] color = {b,g,r};
		
		return color;
	}
}
