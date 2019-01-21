package zimu.util.regex;

import java.util.regex.Matcher;

/**
 * 字符串替换的回调接口
 * 
 * @author yeyong
 * 
 */
public interface ReplaceCallBack {
	/**
	 * 将text转化为特定的字串返回
	 * 
	 * @param text
	 *                指定的字符串
	 * @param index
	 *                替换的次序
	 * @param matcher
	 *                Matcher对象
	 * @return
	 */
	public String replace(String text, int index, Matcher matcher);
}
