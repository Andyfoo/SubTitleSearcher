package zimu.util.regex;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

public class Test {

	public static void main(String[] args) {
		String string = "the quick-brown fox jumps over the lazy-dog.";
		String pattern = "\\b(\\w)(\\w*?)\\b";
		// 将每个单词改为首字大写其他字母小写
		System.out.println(RegexUtil.replaceAll(string, pattern, new AbstractReplaceCallBack() {
			public String doReplace(String text, int index, Matcher matcher) {
				return $(1).toUpperCase() + $(2).toLowerCase();
			}
		}));
		// 输出:The Quick-Brown Fox Jumps Over The Lazy-Dog.

		// 将文本中类似aaa-bbb-ccc的替换为AaaBbbCcc
		string = "the quick-brown fox jumps over the lazy-dog. aaa-bbbb-cccc-ddd";
		pattern = "\\b\\w+(?:-\\w+)+\\b";
		System.out.println(RegexUtil.replaceAll(string, pattern, new AbstractReplaceCallBack() {
			private ReplaceCallBack callBack = new AbstractReplaceCallBack() {
				public String doReplace(String text, int index, Matcher matcher) {
					return $(1).toUpperCase() + $(2).toLowerCase();
				}
			};

			public String doReplace(String text, int index, Matcher matcher) {
				return RegexUtil.replaceAll(text, "(?:\\b|-)(\\w)(\\w*?)\\b", callBack);
			}
		}));
		// 输出: the QuickBrown fox jumps over the LazyDog. AaaBbbbCcccDdd

		// 过滤安全字符... TODO 应提取为一个方法
		final Map<String, String> map = new HashMap<String, String>() {
			private static final long serialVersionUID = 1L;
			{
				put("<", "&lt;");
				put(">", "&gt;");
				put("\"", "&quot;");
				put("'", "&apos;");
			}
		};
		ReplaceCallBack callBack = new ReplaceCallBack() {
			public String replace(String text, int index, Matcher matcher) {
				return map.get(text);
			}
		};
		string = "<html><body>xxxxx 1<4 & 7>5</body></html>";
		System.out.println(RegexUtil.replaceAll(string.replace("&", "&amp;"), "[<>\"\']", callBack));
	}

}
