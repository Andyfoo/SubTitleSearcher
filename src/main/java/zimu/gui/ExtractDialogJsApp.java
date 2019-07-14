package zimu.gui;

import java.io.File;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import zimu.AppConfig;
import zimu.util.StringUtil;

public class ExtractDialogJsApp {
	static final Log logger = LogFactory.get();
	ExtractDialog extractDialog;

	public ExtractDialogJsApp(ExtractDialog extractDialog) {
		this.extractDialog = extractDialog;
	}

	/**
	 * 获取初始化数据
	 */
	public String getInitData() {
		JSONObject resp = new JSONObject();
		JSONArray list = new JSONArray();
		for (int i = 0; i < extractDialog.archiveFiles.size(); i++) {
			File file = extractDialog.archiveFiles.get(i);
			String title = file.getName();
			
			if(!ArrayUtil.contains(AppConfig.subExtNames, StringUtil.extName(file).toLowerCase())){
				continue;
			}
			
			String key = title;
			JSONObject row = new JSONObject();
			row.put("key", key);
			row.put("title", title);
			row.put("size", file.length());
			row.put("sizeF", StringUtil.getPrintSize(file.length()));
			
			list.add(row);
		}
		resp.put("list", list);
		resp.put("title", extractDialog.title);
		resp.put("archiveExt", extractDialog.archiveExt);
		resp.put("archiveSize", extractDialog.archiveData.length);
		resp.put("archiveSizeF", StringUtil.getPrintSize(extractDialog.archiveData.length));
		return resp.toString();
	}
	/**
	 * 下载压缩文件中的字幕
	 * @param data
	 * @return
	 */
	public boolean downArchiveFile(String data) {
		//System.out.println(data);
		if(data == null || data.length() < 10) {
			logger.error("data=null");
			return false;
		}
		JSONObject dataJson = JSONUtil.parseObj(data);
		JSONArray items = dataJson.getJSONArray("items");
		if(items == null || items.size() == 0) {
			logger.error("items=null");
			return false;
		}
		return extractDialog.saveSelected(items);
	}
	public void test() {
		System.out.println("test");
	}
}