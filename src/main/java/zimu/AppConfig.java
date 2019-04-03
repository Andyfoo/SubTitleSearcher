package zimu;

import cn.hutool.core.util.URLUtil;
import zimu.util.StringUtil;

public class AppConfig {
	public static String appName = "SubTitleSearcher";
	public static String appTitle = "字幕下载";
	public static String appVer = "1.3.1";
	
	public static String appPath;
	public static boolean isExe;

	public static String[] subExtNames = new String[] {"sup", "srt", "ass", "ssa"};
	static {
		String path = AppMain.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		if(path.contains(":") && (path.startsWith("\\") || path.startsWith("/"))) {
			path = path.substring(1);
		}
		path = URLUtil.decode(path);
		String pathLcase = path.toLowerCase();
		if(pathLcase.endsWith(".exe") || pathLcase.endsWith(".jar")) {
			path = StringUtil.dirname(path);
			isExe = true;
		}else {
			isExe = false;
		}
		appPath = path;
		System.out.println("appPath="+appPath);
	}
}
