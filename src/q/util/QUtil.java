package q.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class QUtil {
	
	//=============================== 日期类 =====================================
	protected void Date(){}
	
	/**
	 * 将日期字符串解析成Date类型
	 * @param dateStr 日期字符串，如 "2011-08-18 15:04:01"
	 * @param pattern 解析模式，如 "yyyy-MM-dd HH:mm:ss"
	 */
	public static final Date Date_parseStrToDate(String dateStr, String pattern) throws ParseException {
		return new SimpleDateFormat(pattern).parse(dateStr);
	}
	
	/**
	 * 将Date类型格式化成日期字符串
	 * @param pattern 解析模式，如 "yyyy-MM-dd HH:mm:ss"
	 */
	public static final String Date_formatDateToStr(Date date, String pattern){
		return new SimpleDateFormat(pattern).format(date);
	}
	
	/**
	 * 获得星期几
	 * @param date
	 * @return
	 */
	public static final String Date_getWeekStr(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		switch(c.get(Calendar.DAY_OF_WEEK)){
		case Calendar.SUNDAY : return "周日";
		case Calendar.MONDAY : return "周一";
		case Calendar.TUESDAY : return "周二";
		case Calendar.WEDNESDAY : return "周三";
		case Calendar.THURSDAY : return "周四";
		case Calendar.FRIDAY : return "周五";
		case Calendar.SATURDAY : return "周六";
		}
		return null;
	}
	
	/**
     * 计算两个日期相差多久
     * @return long[] {天数, 小时数, 分钟数, 秒数}
     */
    /**
     * @param date1
     * @param date2
     * @return
     */
    public static long[] Date_getDistance(Date date1, Date date2) {
        long day = 0, hour = 0, min = 0, sec = 0;
        long time1 = date1.getTime(), time2 = date2.getTime();
        long diff ;
        if(time1 < time2) {
            diff = time2 - time1;
        } else {
            diff = time1 - time2;
        }
		day = diff / (24 * 60 * 60 * 1000);
		hour = (diff / (60 * 60 * 1000) - day * 24);
		min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
		sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        return new long[]{day, hour, min, sec};
    }
    
	//======================================== File ========================================
	protected void File(){};
	
	/**
	 * 遍历指定文件夹
	 * @param dir 目标文件夹
	 */
	public static void File_iterateFiles(String dir) {
		File f = new File(dir);
		File[] files = f.listFiles();
		if (null != files) {
			for (File ff : files) {
				if (ff.isDirectory()) {
					File_iterateFiles(ff.getPath());
				} else {
					//TODO
				}
			}
		}
	}
	
	/**
	 * 检查文件后缀名是否为指定的后缀
	 * @param f
	 * @param extens，如extens = {"jpg", "gif", "png"}
	 */
	public static boolean File_checkExten(File f, String[] extens){
		String fName = f.getName();
		int lastIndex = fName.lastIndexOf(".");
		if (lastIndex != -1) {
			String end = fName.substring(lastIndex + 1).toLowerCase();
			for(String s : extens){
				if(end.equals(s)){
					return true;
				}
			}
		}
		return false;
	}
		
    //===================================== Math ==============================================
    protected void Math(){};
    
    /** 
     * 转换short为byte 
     */ 
    public static byte[] Math_toByte(short s) {  
    	return new byte[]{
			(byte) (s >> 0),
			(byte) (s >> 8)
    	};
    }  
 
    /** 
     * 通过byte数组取到short 
     */ 
    public static short Math_toShort(byte[] b) {  
        return (short) (((b[1] << 8) | b[0] & 0xff));  
    }  
 
    /** 
     * 转换int为byte数组 
     */ 
    public static byte[] Math_toByte(int x) {  
    	return new byte[]{
			(byte) (x >> 0),
			(byte) (x >> 8),
			(byte) (x >> 16),
			(byte) (x >> 24)
    	};
    }  
 
    /** 
     * 通过byte数组取到int 
     */ 
    public static int Math_toInt(byte[] bb) {  
        return (int) ((((bb[3] & 0xff) << 24)  
                | ((bb[2] & 0xff) << 16)  
                | ((bb[1] & 0xff) << 8) 
                | ((bb[0] & 0xff) << 0)));  
    }  
 
    /** 
     * 转换long型为byte数组 
     */ 
    public static byte[] Math_toByte(long x) {  
    	return new byte[]{
        	(byte) (x >> 0),
        	(byte) (x >> 8), 
        	(byte) (x >> 16),  
        	(byte) (x >> 24),  
        	(byte) (x >> 32),  
        	(byte) (x >> 40),  
        	(byte) (x >> 48),  
        	(byte) (x >> 56)
    	};
    }  
 
    /** 
     * 通过byte数组取到long 
     */ 
    public static long Math_toLong(byte[] bb) {  
        return ((((long) bb[7] & 0xff) << 56)  
                | (((long) bb[6] & 0xff) << 48)  
                | (((long) bb[5] & 0xff) << 40)  
                | (((long) bb[4] & 0xff) << 32)  
                | (((long) bb[3] & 0xff) << 24)  
                | (((long) bb[2] & 0xff) << 16)  
                | (((long) bb[1] & 0xff) << 8) 
                | (((long) bb[0] & 0xff) << 0));  
    }  
 
    /** 
     * 字符到字节转换 
     */ 
    public static byte[] Math_toByte(char ch) {  
    	byte[] bb = new byte[2];
        int temp = (int) ch;  
        // byte[] b = new byte[2];  
        for (int i = 0; i < 2; i ++ ) {  
            bb[i] = new Integer(temp & 0xff).byteValue(); // 将最高位保存在最低位  
            temp = temp >> 8; // 向右移8位  
        }  
        return bb;
    }  
 
    /** 
     * 字节到字符转换 
     */ 
    public static char Math_toChar(byte[] b) {  
        int s = 0;  
        if (b[1] > 0)  
            s += b[1];  
        else 
            s += 256 + b[0];  
        s *= 256;  
        if (b[0] > 0)  
            s += b[1];  
        else 
            s += 256 + b[0];  
        char ch = (char) s;  
        return ch;  
    }  
 
    /** 
     * float转换byte 
     */ 
    public static byte[] Math_toByte(float x) {  
        byte[] bb = new byte[4];  
        int l = Float.floatToIntBits(x);  
        for (int i = 0; i < 4; i++) {  
            bb[i] = new Integer(l).byteValue();  
            l = l >> 8;  
        }  
        return bb;
    }  
 
    /** 
     * 通过byte数组取得float 
     */ 
    public static float Math_toFloat(byte[] b) {  
        int l;  
        l = b[0];  
        l &= 0xff;  
        l |= ((long) b[1] << 8);  
        l &= 0xffff;  
        l |= ((long) b[2] << 16);  
        l &= 0xffffff;  
        l |= ((long) b[3] << 24);  
        return Float.intBitsToFloat(l);  
    }  
 
    /** 
     * double转换byte 
     */ 
    public static byte[] Math_toByte(double x) {  
        byte[] bb = new byte[8];  
        long l = Double.doubleToLongBits(x);  
        for (int i = 0; i < 4; i++) {  
            bb[i] = new Long(l).byteValue();  
            l = l >> 8;  
        }  
        return bb;
    }  
 
    /** 
     * 通过byte数组取得double 
     */ 
    public static double Math_toDouble(byte[] b) {  
        long l;  
        l = b[0];  
        l &= 0xff;  
        l |= ((long) b[1] << 8);  
        l &= 0xffff;  
        l |= ((long) b[2] << 16);  
        l &= 0xffffff;  
        l |= ((long) b[3] << 24);  
        l &= 0xffffffffl;  
        l |= ((long) b[4] << 32);  
        l &= 0xffffffffffl;  
        l |= ((long) b[5] << 40);  
        l &= 0xffffffffffffl;  
        l |= ((long) b[6] << 48);  
        l &= 0xffffffffffffffl;  
        l |= ((long) b[7] << 56);  
        return Double.longBitsToDouble(l);  
    }  
}
