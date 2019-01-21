package zimu.common;

import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import zimu.util.HtHttpUtil;
import zimu.util.MyFileUtil;
import zimu.util.StringUtil;

public class ShooterCommon {
	static final Log logger = LogFactory.get();
	private final static int BUFFER_SIZE = 4096;
	
	public static void main(String[] args) throws Exception {
		String fileName = "H:/_tmp/MOV/downsizing.2017.720p.bluray.x264-geckos.mkv";
		//fileName = "H:/_tmp/MOV/超人特工队.720p.国英台粤.mkv";
	
		JSONArray list = DownList(fileName);
		System.out.println(list.toJSONString(8));
		
		String url = list.getJSONObject(0).getJSONArray("Files").getJSONObject(0).getStr("Link");
		//HtHttpUtil.http.debug = true;
		MyFileUtil.fileWrite("H:/_tmp/MOV/a.ass", HtHttpUtil.http.get(url, null,null, "https://www.shooter.cn/api/subapi.php"), "UTF-8", "");
		
	}
	public static JSONArray DownList(String fileName) throws Exception {
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("filehash", getHash(fileName));
		paramMap.put("pathinfo", StringUtil.basename(fileName));
		paramMap.put("format", "json");
		paramMap.put("lang", "Chn");
		byte[] result = HtHttpUtil.http.postBytes("https://www.shooter.cn/api/subapi.php", paramMap);
		if(result == null || result[0] == -1) {
			logger.error("未查询到结果");
			return null;
		}
		return JSONUtil.parseArray(new String(result));
	}
	
	public static String getHash(String filePath) throws Exception{
		
		RandomAccessFile file = new RandomAccessFile(filePath, "r");
		long fileLength = file.length();
		
		
		long[] positions = new long[]{4096, fileLength / 3 * 2, fileLength / 3, fileLength - 8192};
		StringBuilder stringBuilder = new StringBuilder();
		for (long position : positions) {
			byte[] buffer = new byte[BUFFER_SIZE];
			if(fileLength < position) {
				file.close();
				return stringBuilder.toString();
			}
			file.seek(position);
			int realBufferSize = file.read(buffer);
			buffer = Arrays.copyOfRange(buffer, 0, realBufferSize);
			stringBuilder.append(bytesToMD5(buffer));
			stringBuilder.append(";");
		}
		file.close();
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		return stringBuilder.toString();
	}
	
	private static String bytesToString(byte[] bytes){
		StringBuilder stringBuilder = new StringBuilder();
		for (byte b : bytes) {
			int bias = (b & 0xf0) >>> 4;
			stringBuilder.append(Integer.toHexString(bias));
			bias = b & 0xf;
			stringBuilder.append(Integer.toHexString(bias));
		}
		return stringBuilder.toString();
	}
	
	
	private static String bytesToMD5(byte[] bytes) throws Exception{
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		byte[] buffer = messageDigest.digest(bytes);
		return bytesToString(buffer);
	}
}
