package zimu.common.sites;

import java.io.RandomAccessFile;
import java.security.MessageDigest;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import zimu.util.HtHttpUtil;

public class XunLeiCommon {
	static final Log logger = LogFactory.get();
	
	public static void main(String[] args) throws Exception {
		String fileName = "H:/_tmp/MOV/downsizing.2017.720p.bluray.x264-geckos.mkv";
		System.out.println(DownList(fileName));
		
	}
	public static JSONArray DownList(String fileName) throws Exception {
		String cid = getCid(fileName);
		String result = HtHttpUtil.http.get(String.format("http://sub.xmp.sandai.net:8000/subxl/%s.json", cid));
		if(result == null) {
			logger.error("未查询到结果");
			return null;
		}
		//System.out.println(result);
		JSONObject json = JSONUtil.parseObj(result);
		return json.getJSONArray("sublist");
	}
	/**
	 * 
	 * http://sub.xmp.sandai.net:8000/subxl/${cid}.json
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public static String getCid(String filePath) throws Exception{
		MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
		RandomAccessFile file = new RandomAccessFile(filePath, "r");
		long fileLength = file.length();
		if(fileLength < 0xF000) {
			byte[] buffer = new byte[0xF000];
			file.seek(0);
			file.read(buffer);
			file.close();
			return bytesToString(messageDigest.digest(buffer)).toUpperCase();
		}
		int bufferSize = 0x5000;
		long[] positions = new long[]{0, fileLength / 3, fileLength - bufferSize};
		for (int i = 0; i < positions.length; i++) {
			long position = positions[i];
			byte[] buffer = new byte[bufferSize];
			file.seek(position);
			file.read(buffer);
			messageDigest.update(buffer);
		}
		file.close();
		return bytesToString(messageDigest.digest()).toUpperCase();
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
	
	
}
