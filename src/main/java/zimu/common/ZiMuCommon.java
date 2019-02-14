package zimu.common;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZiMuCommon {
	/**
	 * 将字符串复制到剪切板。
	 */
	public static void copyClipboard(String writeMe) {
		Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable tText = new StringSelection(writeMe);
		clip.setContents(tText, null);
	}
	
	/**
	 * 获取字符中的汉字部分
	 * @param title
	 * @return
	 */
	public static String getTitleCnStr(String title) {
		if(title == null)return "";
		title = title.replace(":", "：");
		//title = title.replace("_", "—");
		//title = title.replace("-", "ˉ");
		title = title.replace("[", "【");
		title = title.replace("]", "】");
		title = title.replace("修正版", "");
		String[] arr = title.split("\\.");
		if(arr.length > 2) {
			title = title.substring(0, title.lastIndexOf("."));
		}
		if(arr.length > 3) {
			title = title.substring(0, title.lastIndexOf("."));
		}
		
		String title2 = title.replaceAll("【.*】", "");
		
		//中文+标点组合
		Pattern pattern = Pattern.compile("[\u4E00-\u9FA5"
				+ "\u3002\uff1f\uff01\uff0c\u3001\uff1b\uff1a\u201c"
				+ "\u201d\u2018\u2019\uff08\uff09\u300a\u300b\u3008"
				+ "\u3009\u3010\u3011\u300e\u300f\u300c\u300d\ufe43"
				+ "\ufe44\u3014\u3015\u2026\u2014\uff5e\ufe4f\uffe5"
				+ "\u02c9]+");
		Matcher matcher = pattern.matcher(title2);
		String resStr = "";
		if(matcher.find()) {
			resStr = matcher.group(0);
		}else {
			matcher = pattern.matcher(title);
			if(matcher.find()) {
				resStr = matcher.group(0);
				resStr = resStr.replaceAll("[【】]+", "");
			}
		}
		//中文+英文+标点组合
		pattern = Pattern.compile("[\\w\u4E00-\u9FA5"
				+ "\u3002\uff1f\uff01\uff0c\u3001\uff1b\uff1a\u201c"
				+ "\u201d\u2018\u2019\uff08\uff09\u300a\u300b\u3008"
				+ "\u3009\u3010\u3011\u300e\u300f\u300c\u300d\ufe43"
				+ "\ufe44\u3014\u3015\u2026\u2014\uff5e\ufe4f\uffe5"
				+ "\u02c9]+");
		if((resStr == null || resStr.length() == 0) && Pattern.compile("[\u4E00-\u9FA5]+").matcher(title).find()) {
			matcher = pattern.matcher(title);
			if(matcher.find()) {
				resStr = matcher.group(0);
				resStr = resStr.replaceAll("[【】]+", "");
			}
		}
		

		return resStr;
	}

	public static void main(String[] args) {
//		System.out.println("[缩小人生]Downsizing.2017.1080p.Bluray.MKV.x264.AC3修正版.ass [ASS/SSA][双语][下载次数:1709]".
//				replaceAll("\\[[^]]*]\\[[^]]*]\\[下载次数.+]", ""));;
		
		//System.out.println(getTitleCnStr("【X战警：逆转未来 导演剪辑版】X-Men.Days.of.Future.Past.THE.ROGUE.CUT.1080p.BluRay.x264-SADPANDA"));
		//System.exit(0);
		String[] titles = new String[] {
			"test.a.b.c",
			"我是中文.a.b.c",
			"我是中文 a.b.c",
			"我是中文a.b.c",
			"我是-中文a.b.c",
			"我是_中文a.b.c",
			"[我是中文]a.b.c",
			"【获取】我是中文a.b.c",
			"[获wee取]我是中文a.b.c",
			"[超人特工队(国粤台英)].The.Incredibles.2004.BluRay.720p.x264.AC3.4Audios-CMCT.完美匹配国语chsc",
			"[缩小人生]Downsizing.2017.1080p.Bluray.MKV.x264.AC3修正版",
			"X战警：第一战.X-Men.First.Class.2011.2160p.BluRay",
			"战X警：第一战.X-Men.First.Class.2011.2160p.BluRay",
			"【X战警：逆转未来 导演剪辑版】X-Men.Days.of.Future.Past.THE.ROGUE.CUT.1080p.BluRay.x264-SADPANDA"
		};
		//System.out.println(getTitleCnStr("[获wee取]我是中文a.b.c"));
		for(int i = 0; i < titles.length; i++) {
			System.out.println(getTitleCnStr(titles[i]));
		}
	}

}
