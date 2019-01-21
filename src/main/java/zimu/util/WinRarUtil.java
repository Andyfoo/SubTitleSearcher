package zimu.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import zimu.AppConfig;

public class WinRarUtil {
	static final Log logger = LogFactory.get();
	public static String binPath = "";
	public static void main(String[] args) throws Exception {
		binPath = "E:\\workspace\\_me\\dev\\my_libs\\test_lib\\data\\file\\archive\\UnRAR.exe";

		String srcData = "E:\\workspace\\_me\\dev\\my_libs\\test_lib\\data\\file\\archive\\test2.rar";
		byte[] data = MyFileUtil.fileReadBin(srcData);
		String objPath = "E:\\workspace\\_me\\dev\\my_libs\\test_lib\\data\\file\\archive\\"+System.currentTimeMillis();
		String extName = "rar";
		List<File> result = unRar(extName, data, objPath);
		
		System.out.println(JSONUtil.toJsonPrettyStr(result));
		clear(objPath);
	}
	static {
		if(AppConfig.isExe) {
			binPath = AppConfig.appPath + "bin/7z.exe";
		}else {
			binPath = AppConfig.appPath + "../../_release/SubTitleSearcher/bin/7z.exe";
		}
		//System.out.println(binPath);
	}
	
	
	/**
	 * 清除目录
	 * @param objPath
	 */
	public static void clear(String objPath) {
		if(new File(objPath).exists()) {
			logger.info("清除临时目录:"+objPath);
			MyFileUtil.deleteFile(objPath);
		}
	}
	
	/**
	 * 返回当前系统路径分隔符
	 * @param objPath
	 * @return
	 */
	public static String osPath(String objPath) {
		return objPath.replace("/", File.separator);
	}
	
	/**
	 * 解压二进制压缩数据
	 * @param extName
	 * @param data
	 * @param objPath
	 * @return
	 */
	public static List<File> unRar(String extName, byte[] data, String objPath) {
		Runtime rt = Runtime.getRuntime();
		StringBuffer result = new StringBuffer();
		try {
			logger.info("在临时目录中解压:"+objPath);
			if(!new File(objPath).exists()) {
				MyFileUtil.dirCreate(objPath, true);
			}
			String objFile = String.format("%s/data.%s", objPath, extName);
			MyFileUtil.fileWriteBin(objFile, data);
			//String execStr = String.format("%s e -r -o+ -ap %s -ad %s", osPath(binPath), osPath(objFile), osPath(objPath));
			String execStr = String.format("%s e \"%s\" -o\"%s\" -aoa", osPath(binPath), osPath(objFile), osPath(objPath+"/data"));
			Process p = rt.exec(execStr);//rt.exec(execStr, null, new File(objPath));
			logger.info("解压命令: "+execStr);

			InputStream fis = p.getInputStream();
			InputStreamReader isr = new InputStreamReader(fis,"GBK");
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				result.append(line);
				result.append("\r\n");
			}
			logger.info(result.toString());
			if(result.indexOf("Everything is Ok") > 0) {//if(result.indexOf("全部正常") > 0) {
				logger.info("解压成功");
			}else {
				logger.error("解压失败");
				return null;
			}
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		return MyFileUtil.getFileList(objPath, true);
	}

}
