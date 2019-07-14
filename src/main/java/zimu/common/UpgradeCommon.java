package zimu.common;

import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.File;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import zimu.AppConfig;
import zimu.gui.MainWin;
import zimu.util.HtHttpUtil;
import zimu.util.MyFileUtil;

public class UpgradeCommon {
	static final Log logger = LogFactory.get();
	static JSONObject data = null;
	static String upgradeCmdExe = "Y2QgJX5kcDANCmNvcHkgL1kgbmV3X3Zlci5leGUgLi5cU3ViVGl0bGVTZWFyY2hlci5leGUNCnN0YXJ0IC4uXFN1YlRpdGxlU2VhcmNoZXIuZXhlDQpjZCAuLg0KcGluZyAtbiAzIDEyNy4wLjAuMT5udWwNCnJkIC9TIC9RIHVwZ3JhZGUNCg==";
	static String upgradeCmdZip = "Y2QgJX5kcDANCi4uXGJpblw3ei5leGUgeCAteSAtYW9zIG5ld192ZXIuemlwDQp4Y29weSAvUSAvRSAvWSBTdWJUaXRsZVNlYXJjaGVyXCogLi5cDQpzdGFydCAuLlxTdWJUaXRsZVNlYXJjaGVyLmV4ZQ0KY2QgLi4NCnBpbmcgLW4gMyAxMjcuMC4wLjE+bnVsDQpyZCAvUyAvUSB1cGdyYWRlDQo=";
	
	/**
	 * 下载更新
	 * @return
	 */
	public static String download() {
		if(data == null || StrUtil.isEmpty(data.getStr("url"))) {
			logger.error("下载链接为空");
			return null;
		}
		String url = data.getStr("url");
		String sign = data.getStr("sign");
		String type = data.getStr("type");

		byte[] updateData = HtHttpUtil.http.getBytes(url);
		logger.info("下载完毕");
		if(updateData==null) {
			logger.error("下载数据为空");
			MainWin.frame.alert("下载错误，请稍后重试");
			return null;
		}
		
		String sign2 = SecureUtil.md5(new ByteArrayInputStream(updateData));//145316371511956DB5540F2EA608800C;
		//System.out.println(sign2);
		if(!sign2.equalsIgnoreCase(sign)) {
			logger.error("下载数据签名错误");
			MainWin.frame.alert("下载数据签名错误");
			return null;
		}
		String filepath = AppConfig.appPath + "upgrade/";
		MyFileUtil.dirCreate(filepath);
		String batFilename = filepath+"upgrade.cmd";
		String batStr = "";
		
		//生成更新bat文件
		if(type.equals("exe")){
			String filename = filepath+"new_ver.exe";
			MyFileUtil.fileWriteBin(filename, updateData);
			
			batStr = upgradeCmdExe;
		}else if(type.equals("zip")){
			String filename = filepath+"new_ver.zip";
			MyFileUtil.fileWriteBin(filename, updateData);
			
			batStr = upgradeCmdZip;
		}else {
			return null;
		}
		MyFileUtil.fileWriteBin(batFilename, Base64.decode(batStr));
		
		
		//System.out.println(updateData.length);
		return batFilename;
	}
	
	/**
	 * 判断是否有新版
	 * @return
	 */
	public static boolean checkNewVersion() {
		String url = AppConfig.upgradeUrl;
		String str = HtHttpUtil.http.get(url);
		logger.info(str);
		data = JSONUtil.parseObj(str);
		if(data==null) {
			logger.error("未发现新版");
			return false;
		}
		//System.out.println(data);
		return compareVersion(data.getStr("version"), AppConfig.appVer) > 0;
	}
	/**
	 * 比较版本号的大小,前者大则返回一个正数,后者大返回一个负数,相等则返回0
	 * 
	 * @param version1
	 * @param version2
	 * @return
	 */
	public static int compareVersion(String version1, String version2) {
		if (version1 == null || version2 == null) {
			return -999;
		}
		if (version1.equals(version2)) {
			return 0;
		}
		String[] versionArray1 = version1.split("\\.");// 注意此处为正则匹配，不能用"."；
		String[] versionArray2 = version2.split("\\.");
		int idx = 0;
		int minLength = Math.min(versionArray1.length, versionArray2.length);// 取最小长度值
		int diff = 0;
		while (idx < minLength && (diff = versionArray1[idx].length() - versionArray2[idx].length()) == 0// 先比较长度
				&& (diff = versionArray1[idx].compareTo(versionArray2[idx])) == 0) {// 再比较字符
			++idx;
		}
		// 如果已经分出大小，则直接返回，如果未分出大小，则再比较位数，有子版本的为大；
		diff = (diff != 0) ? diff : versionArray1.length - versionArray2.length;
		return diff;
	}
	
	/**
	 * 开始更新
	 */
	public static void upgrade() {
		//Runtime rt = Runtime.getRuntime();
		//StringBuffer result = new StringBuffer();
		try {
			String upgradeCmd = download();
			if(upgradeCmd == null) {
				logger.error("更新失败");
				return;
			}
			logger.info("更新命令: "+upgradeCmd);
			Desktop.getDesktop().open(new File(upgradeCmd));
			System.exit(0);
			//Process p = rt.exec(upgradeCmd);
			//p.waitFor();

//			InputStream fis = p.getInputStream();
//			InputStreamReader isr = new InputStreamReader(fis,"GBK");
//			BufferedReader br = new BufferedReader(isr);
//			String line = null;
//			while ((line = br.readLine()) != null) {
//				result.append(line);
//				result.append("\r\n");
//			}
//			logger.info(result.toString());
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 检测更新
	 */
	public static void autocheck() {
		new Thread() {
			public void run() {
				if(checkNewVersion()) {
					String desc = data.getStr("desc", "");
					String version = data.getStr("version");
					desc = desc.replace("<br/>", "\n").replace("<br>", "\n");
					MainWin.frame.confirm("检测到新版本："+version+"，是否更新？", "更新说明 ：\n"+desc, new Runnable() {
						@Override
						public void run() {
							upgrade();
						}
					}, null);
				}
			}
		}.start();
	}
	public static void main(String[] args) {
		checkNewVersion();
		System.out.println(System.getProperty("java.version"));
		//autocheck();
//		System.out.println(checkNewVersion());
//		System.out.println(download());
		upgrade();
	}

}
