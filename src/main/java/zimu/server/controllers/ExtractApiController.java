package zimu.server.controllers;

import java.io.File;

import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import zimu.AppConfig;
import zimu.gui.ExtractDialog;
import zimu.util.StringUtil;
/*
 * 解压文件接口
 */
public class ExtractApiController extends Base {
	static final Log logger = LogFactory.get();
	
	/**
	 * 获取初始化数据
	 */
	public void get_init_data() {
		JSONObject resp = new JSONObject();
		JSONArray list = new JSONArray();
		for (int i = 0; i < ExtractDialog.extractDialog.archiveFiles.size(); i++) {
			File file = ExtractDialog.extractDialog.archiveFiles.get(i);
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
		resp.put("title", ExtractDialog.extractDialog.title);
		resp.put("archiveExt", ExtractDialog.extractDialog.archiveExt);
		resp.put("archiveSize", ExtractDialog.extractDialog.archiveData.length);
		resp.put("archiveSizeF", StringUtil.getPrintSize(ExtractDialog.extractDialog.archiveData.length));
		
		outJsonpMessage(request,response, 0, "OK", resp);
	}
	/**
	 * 下载压缩文件中的字幕
	 * @param data
	 * @return
	 */
	public void down_archive_file() {
		HttpRequest request = getRequest();
		HttpResponse response = getResponse();
		String data = request.getParaToStr("data");
		if(data == null) {
			outJsonpMessage(request,response, 1, "请求数据错误");
			return;
		}
		logger.info("data="+data);
		if(data == null || data.length() < 10) {
			logger.error("data=null");
			outJsonpMessage(request,response, 1, "参数错误");
			return;
		}
		JSONObject dataJson = JSONUtil.parseObj(data);
		JSONArray items = dataJson.getJSONArray("items");
		if(items == null || items.size() == 0) {
			logger.error("items=null");
			outJsonpMessage(request,response, 1, "参数错误");
			return;
		}
		
		JSONObject resp = new JSONObject();
		resp.put("saveSelected", ExtractDialog.extractDialog.saveSelected(items));
		outJsonpMessage(request,response, 0, "OK", resp);
	}
}
