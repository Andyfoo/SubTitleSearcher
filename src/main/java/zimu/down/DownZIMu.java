package zimu.down;

import java.util.Set;
import java.util.Vector;

import com.github.stuxuhai.jpinyin.ChineseHelper;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import zimu.AppConfig;
import zimu.common.ShooterCommon;
import zimu.common.XunLeiCommon;
import zimu.common.ZIMuKuCommon;
import zimu.gui.ExtractDialog;
import zimu.gui.MainWin;
import zimu.util.HtHttpUtil;
import zimu.util.MyFileUtil;
import zimu.util.StringUtil;

public class DownZIMu {
	static final Log logger = LogFactory.get();
	static JSONObject dataMap = new JSONObject(true);
	static int no = 0;
	
	public static boolean down(int num, String key, String charset, boolean simplified) {
		JSONObject row = dataMap.getJSONObject(key);
		if(row == null) {
			//MainWin.alert("数据错误");
			logger.error("数据错误");
			return false;
		}

		String from = row.getStr("from");
		JSONObject data = row.getJSONObject("data");
		
		String filepath = StringUtil.dirname(MainWin.movFilename);
		String filenameStr = StringUtil.basename(MainWin.movFilename);
		String filenameBase = filenameStr.substring(0, filenameStr.lastIndexOf("."));
		boolean downResult = false;
		if(from.equals("射手网")) {
			JSONObject fileRow = data.getJSONArray("Files").getJSONObject(0);
			String link = fileRow.getStr("Link");
			downResult = downAndSave(row, filepath+filenameBase + (num > 0 ? ".chn" + num : "") + "." + fileRow.getStr("Ext"), link, charset, simplified);
		}else if(from.equals("迅雷")) {
			JSONObject fileRow = data;
			String link = fileRow.getStr("surl");
			downResult = downAndSave(row, filepath+filenameBase + (num > 0 ? ".chn" + num : "") + "." + fileRow.getStr("Ext"), link, charset, simplified);
		}else if(from.equals("字幕库")) {
			JSONObject fileRow = data;
			String link = fileRow.getStr("url");
			downResult = downAndSave_ZiMuKu(row, filepath+filenameBase + (num > 0 ? ".chn" + num : ""), link, charset, simplified);
		}
		
		if(!downResult) {
			logger.error("下载失败");
		}
		return downResult;
		
	}
	
	public static boolean downAndSave_ZiMuKu(JSONObject row, String filename, String url, String charset, boolean simplified) {
		try {
			JSONObject zimuContent = ZIMuKuCommon.downContent(url);
			if(zimuContent == null)return false;
			String ext = zimuContent.getStr("ext");
			
			byte[] data = Base64.decode(zimuContent.getStr("data"));
			// *.srt、*.ass、*.ssa、*.zip、*.rar、*.7z
			if(data == null || data.length < 100) {
				return false;
			}
			logger.info("save="+filename);
			if(ext.equals("zip") || ext.equals("rar") || ext.equals("7z")) {
				new ExtractDialog(MainWin.frame, row.getJSONObject("data").getStr("title"),ext,filename, data).setVisible(true);
				return true;
			}else if(ArrayUtil.contains(AppConfig.subExtNames, ext.toLowerCase())){
				//Vector<Object> colData = MainWin.getSubTableData(row.getStr("key"));
				//String charset = (String)colData.get(5);
				
				filename += "." + ext;
				if(simplified || MainWin.simplifiedCheckBox.isSelected()) {
					String dataStr = new String(data, charset);
					dataStr = ChineseHelper.convertToSimplifiedChinese(dataStr);
					MyFileUtil.fileWrite(filename, dataStr, "UTF-8", "");
				}else {
					MyFileUtil.fileWriteBin(filename, data);
				}
			}else {
				return false;
			}
			
			return true;
		}catch(Exception e) {
			logger.error(e);
		}
		return false;
		
		
	}
	public static boolean downAndSave(JSONObject row, String filename, String url, String charset, boolean simplified) {
		try {
			byte[] data = HtHttpUtil.http.getBytes(url, null, url);
			if(data == null || data.length < 100) {
				return false;
			}
			logger.info("save="+filename);
			//Vector<Object> colData = MainWin.getSubTableData(row.getStr("key"));
			//String charset = (String)colData.get(5);
			if(simplified || MainWin.simplifiedCheckBox.isSelected()) {
				String dataStr = new String(data, charset);
				dataStr = ChineseHelper.convertToSimplifiedChinese(dataStr);
				MyFileUtil.fileWrite(filename, dataStr, "UTF-8", "");
			}else {
				MyFileUtil.fileWriteBin(filename, data);
			}
			return true;
		}catch(Exception e) {
			logger.error(e);
		}
		return false;
	}
	public static void searchList() {
		MainWin.setStatusLabel("正在查询...");
		MainWin.subTable.updateUI();
		
		JSONArray list = null;
		String filenameStr = StringUtil.basename(MainWin.movFilename);
		String filenameBase = filenameStr.substring(0, filenameStr.lastIndexOf("."));
		
		//射手
		if(MainWin.sheshouCheckBox.isSelected()) {
			try {
				list = ShooterCommon.DownList(MainWin.movFilename);
				//subTable.set
				//System.out.println(list);
				if(list!=null && list.size() > 0) {
					for(int i = 0; i < list.size(); i++) {
						JSONObject row = list.getJSONObject(i);
						JSONObject fileRow = row.getJSONArray("Files").getJSONObject(0);
						String key = DigestUtil.md5Hex(row.toString());
						String rate = "-";
						addRow(key, filenameBase + "." + fileRow.getStr("Ext"), rate, row, "射手网");
					}
				}
				
			} catch (Exception e) {
				logger.error(e);
			}
		}
		
		//迅雷
		if(MainWin.xunleiCheckBox.isSelected()) {
			try {
				list = XunLeiCommon.DownList(MainWin.movFilename);
				//subTable.set
				//System.out.println(list);
				if(list!=null && list.size() > 0) {
					for(int i = 0; i < list.size(); i++) {
						JSONObject row = list.getJSONObject(i);
						if(row == null || row.size() == 0 || row.isNull("surl")) {
							continue;
						}
						row.put("Ext", row.getStr("surl").substring(row.getStr("surl").lastIndexOf(".")+1));
						String key = DigestUtil.md5Hex(row.toString());
						String title = row.getStr("sname");
						if(StrUtil.isEmpty(title)) {
							title = filenameBase + "." + row.getStr("Ext");
							
						}
						String rate = StrUtil.isNotBlank(row.getStr("rate")) ? row.getStr("rate")+"星" : "-";
						addRow(key, title,rate, row, "迅雷");
					}
				}
				
			} catch (Exception e) {
				logger.error(e);
			}
		}
		//字幕库
		if(MainWin.zimukuCheckBox.isSelected()) {
			try {
				list = ZIMuKuCommon.DownList(StringUtil.basename(MainWin.movFilename));
				if(list!=null && list.size() > 0) {
					for(int i = 0; i < list.size(); i++) {
						JSONObject row = list.getJSONObject(i);
						
						row.put("Ext", row.getStr("ext"));
						String key = DigestUtil.md5Hex(row.toString());
						String title = String.format("%s [%s][%s][下载次数:%s]", row.getStr("title"), 
								row.getStr("ext"), row.getStr("lang"), row.getStr("downCount"));
						
						String rate = StrUtil.isNotBlank(row.getStr("rate")) ? row.getStr("rate") : "-";
						addRow(key, title,rate, row, "字幕库");
					}
				}
				
			} catch (Exception e) {
				logger.error(e);
			}
		}
		
		if(dataMap.size() > 0) {
			MainWin.setStatusLabel("查询到"+dataMap.size()+"个字幕");
			Set<String> keySet = dataMap.keySet();
			for(String key : keySet) {
				JSONObject row = dataMap.getJSONObject(key);
				Vector<Object> vRow = new Vector<Object>();
				vRow.add(row.get("key"));
				vRow.add(row.get("no"));
				vRow.add(row.get("title"));
				vRow.add(row.get("rate"));
				vRow.add(row.get("from"));
				vRow.add("UTF-8");
				MainWin.subTableData.add(vRow);
			}
			MainWin.searchButton.setEnabled(true);
			MainWin.subTable.updateUI();
			
		}else {
			MainWin.setStatusLabel("未查询到字幕");
			MainWin.searchButton.setEnabled(true);
			MainWin.subTable.updateUI();
			return;
		}
		
		
	}
	public static void clear() {
		MainWin.searchButton.setEnabled(false);
		MainWin.downButton.setEnabled(false);

		MainWin.subTable.clearSelection();
		MainWin.subTable.removeAll();
		MainWin.subTableData.clear();
		dataMap.clear();
		no = 0;
		MainWin.subTable.updateUI();
	}
	public static void addRow(String key, String title,String rate,JSONObject data, String from) {
		JSONObject dataRow = new JSONObject();
		dataRow.put("key", key);
		dataRow.put("title", title);
		dataRow.put("rate", rate);
		dataRow.put("data", data);
		dataRow.put("from", from);
		if(!dataMap.containsKey(key)) {
			dataRow.put("no", ++no);
		}
		dataMap.put(key, dataRow);
	}
	public static void main(String[] args) {
		

	}

}
