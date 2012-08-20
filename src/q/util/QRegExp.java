package q.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式
 *
 */
public class QRegExp {

	/**
	 * 匹配多个#abc#
	 */
	public static Matcher sharp2sharp(String str){
		return Pattern.compile("(#[^#]+?#)").matcher(str);
	}
}
