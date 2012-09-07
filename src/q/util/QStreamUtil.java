package q.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class QStreamUtil {
	
	public static String toStr(InputStream is) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte[] buf = new byte[4096];
		int ret = 0;
		while ((ret = is.read(buf)) > 0) {
			os.write(buf, 0, ret);
		}
		return new String(os.toByteArray());
	}

}
