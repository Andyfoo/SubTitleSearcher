package zimu.util.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;



/**
 <pre>
 
 多个flags 这样使用  Pattern.CASE_INSENSITIVE|Pattern.DOTALL
Pattern.CASE_INSENSITIVE  
	将启动对ASCII字符不区分大小写匹配  
Pattern.UNICODE_CASE  
	将启动Unicode字符不区分大小写匹配  
Pattern.DOTALL  
	将启动dotall模式,该模式下,"."将表示任意字符,包括回车符  
	
Pattern.CANON_EQ 当且仅当两个字符的”正规分解(canonical decomposition)”都完全相同的情况下，才认定匹配.比如用了这个标志之后，表达式”a\u030A”会匹配”?”.默认情况下，不考虑”规范相等性(canonical equivalence)”.

Pattern.CASE_INSENSITIVE(?i) 默认情况下,大小写不明感的匹配只适用于US-ASCII字符集.这个标志能让表达式忽略大小写进行匹配.要想对Unicode字符进行大小不明感的匹 配,只要将UNICODE_CASE与这个标志合起来就行了.

Pattern.COMMENTS(?x) 在这种模式下,匹配时会忽略(正则表达式里的)空格字符(译者注:不是指表达式里的”\s”，而是指表达式里的空格,tab,回车之类).注释从#开始,一直到这行结束.可以通过嵌入式的标志来启用Unix行模式.

Pattern.DOTALL(?s)在这种模式下，表达式’.’可以匹配任意字符，包括表示一行的结束符。默认情况下，表达式’.’不匹配行的结束符.

Pattern.MULTILINE(?m)在这种模式下,’\^’和’$’分别匹配一行的开始和结束.此外,’^’仍然匹配字符串的开始,’$’也匹配字符串的结束.默认情况下,这两个表达式仅仅匹配字符串的开始和结束.

Pattern.UNICODE_CASE(?u) 在这个模式下,如果你还启用了CASE_INSENSITIVE标志,那么它会对Unicode字符进行大小写不明感的匹配.默认情况下,大小写不敏感的匹配只适用于US-ASCII字符集.

Pattern.UNIX_LINES(?d) 在这个模式下,只有’\n’才被认作一行的中止,并且与’.’,’^’,以及’$’进行匹配.
 </pre>
 *
 */
public class RegexUtil {
	public static void main(String[] args) {
		System.out.println(getMatchStr("aaaabb=123bccc", "aaaabb=([\\d]+)"));
		System.out.println(getMatchList("aaaabb=123bccc345AAA,aaaabb123bccc345AAA,aaaabb123bccc345AAA", "aaaabb=([\\d]+)[^,]+(AAA)"));
	}
	
	/**
	 * 获取匹配字符串
	 * 
	 * @return 
	 */
	public static String getMatchStr(String str, String regex) {
		if (str == null) {
			return "";
		}
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(str);
		if (m.find()) {
			return m.group(1);
		}
		return "";
	}
	public static String getMatchStr(String str, String regex, int flags) {
		if (str == null) {
			return "";
		}
		Pattern pattern = Pattern.compile(regex, flags);
		Matcher m = pattern.matcher(str);
		if (m.find()) {
			return m.group(1);
		}
		return "";
	}	
	/**
	 * 获取匹配列表--1维数组
	 * @param str
	 * @param regex
	 * @return
	 */
	public static String[] getMatchArray(String str, String regex) {
		if (str == null) {
			return new String[]{};
		}
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(str);
		while (m.find()) {
			String[] arr = new String[m.groupCount()];
			for(int i = 1; i <= m.groupCount(); i++){
				arr[i-1] = m.group(i);
			}
			return arr;
		}
		return new String[]{};
	}

	public static String[] getMatchArray(String str, String regex, int flags) {
		if (str == null) {
			return new String[]{};
		}
		Pattern pattern = Pattern.compile(regex, flags);
		Matcher m = pattern.matcher(str);
		while (m.find()) {
			String[] arr = new String[m.groupCount()];
			for(int i = 1; i <= m.groupCount(); i++){
				arr[i-1] = m.group(i);
			}
			return arr;
		}
		return new String[]{};
	}

	/**
	 * 获取匹配列表--2维数组
	 * @param str
	 * @param regex
	 * @return
	 */
	public static JSONArray getMatchList(String str, String regex) {
		JSONArray list = new JSONArray();
		if (str == null) {
			return list;
		}
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(str);
		while (m.find()) {
			JSONArray row = new JSONArray();
			for(int i = 1; i <= m.groupCount(); i++){
				row.add(m.group(i));
			}
			list.add(row);
		}
		return list;
	}
	public static JSONArray getMatchList(String str, String regex, int flags) {
		JSONArray list = new JSONArray();
		if (str == null) {
			return list;
		}
		Pattern pattern = Pattern.compile(regex, flags);
		Matcher m = pattern.matcher(str);
		while (m.find()) {
			JSONArray row = new JSONArray();
			for(int i = 1; i <= m.groupCount(); i++){
				row.add(m.group(i));
			}
			list.add(row);
		}
		return list;
	}
	
	/**
	 * 将String中的所有regex匹配的字串全部替换掉
	 * 
	 * @param string
	 *                代替换的字符串
	 * @param regex
	 *                替换查找的正则表达式
	 * @param replacement
	 *                替换函数
	 * @return
	 */
	public static String replaceAll(String string, String regex, ReplaceCallBack replacement) {
		return replaceAll(string, Pattern.compile(regex), replacement);
	}
	public static String replaceAll(String string, String regex, int flags, ReplaceCallBack replacement) {
		return replaceAll(string, Pattern.compile(regex, flags), replacement);
	}

	/**
	 * 将String中的所有pattern匹配的字串替换掉
	 * 
	 * @param string
	 *                代替换的字符串
	 * @param pattern
	 *                替换查找的正则表达式对象
	 * @param replacement
	 *                替换函数
	 * @return
	 */
	public static String replaceAll(String string, Pattern pattern, ReplaceCallBack replacement) {
		if (string == null) {
			return null;
		}
		Matcher m = pattern.matcher(string);
		if (m.find()) {
			StringBuffer sb = new StringBuffer();
			int index = 0;
			while (true) {
				m.appendReplacement(sb, replacement.replace(m.group(0), index++, m));
				if (!m.find()) {
					break;
				}
			}
			m.appendTail(sb);
			return sb.toString();
		}
		return string;
	}

	/**
	 * 将String中的regex第一次匹配的字串替换掉
	 * 
	 * @param string
	 *                代替换的字符串
	 * @param regex
	 *                替换查找的正则表达式
	 * @param replacement
	 *                替换函数
	 * @return
	 */
	public static String replaceFirst(String string, String regex, ReplaceCallBack replacement) {
		return replaceFirst(string, Pattern.compile(regex), replacement);
	}
	public static String replaceFirst(String string, String regex, int flags, ReplaceCallBack replacement) {
		return replaceFirst(string, Pattern.compile(regex, flags), replacement);
	}

	/**
	 * 将String中的pattern第一次匹配的字串替换掉
	 * 
	 * @param string
	 *                代替换的字符串
	 * @param pattern
	 *                替换查找的正则表达式对象
	 * @param replacement
	 *                替换函数
	 * @return
	 */
	public static String replaceFirst(String string, Pattern pattern, ReplaceCallBack replacement) {
		if (string == null) {
			return null;
		}
		Matcher m = pattern.matcher(string);
		StringBuffer sb = new StringBuffer();
		if (m.find()) {
			m.appendReplacement(sb, replacement.replace(m.group(0), 0, m));
		}
		m.appendTail(sb);
		return sb.toString();
	}
	
	

	/**
	 * 转义正则特殊字符 （$()*+.[]?\^{},|）
	 * 
	 * @param keyword
	 * @return
	 */
	public static String escapeExprSpecialWord(String keyword) {
		if (StrUtil.isNotEmpty(keyword)) {
			//Java过滤正则表达式特殊字代码如下(注意:\\需要第一个替换，否则replace方法替换时会有逻辑bug)
			String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" };
			for (String key : fbsArr) {
				if (keyword.contains(key)) {
					keyword = keyword.replace(key, "\\" + key);
				}
			}
		}
		return keyword;
	}
}
