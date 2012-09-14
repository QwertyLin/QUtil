package q.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class QStreamUtil {
	
	public static String toStr(InputStream input) throws IOException {
		return new String(toByte(input));
	}
	
	public static byte[] toByte(InputStream input) throws IOException {
        byte[] buf = new byte[1024];
        int len = -1;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while ((len = input.read(buf)) != -1) {
        	output.write(buf, 0, len);
        }
        byte[] data = output.toByteArray();
        output.close();
        input.close();
        return data;
   } 

}
