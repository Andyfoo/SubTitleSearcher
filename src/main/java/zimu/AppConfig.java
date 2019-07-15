package zimu;

import cn.hutool.core.util.URLUtil;
import zimu.util.StringUtil;

public class AppConfig {
	public static String appName = "SubTitleSearcher";
	public static String appTitle = "字幕下载";
	//public static String appTitle = "SubTitleSearcher";
	public static String appVer = "2.0.2";
	
	public static String appPath;
	public static boolean isExe;
	
	public static int serverPort = 11122;

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
	
	public static String upgradeUrl = "https://raw.githubusercontent.com/Andyfoo/SubTitleSearcher/master/_upgrade/last";
	//public static String upgradeUrl = "http://localhost:8080/update/last";
	
}
