package zimu.server.controllers;

import java.awt.Desktop;
import java.net.URI;

import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import zimu.down.DownZIMu;
import zimu.gui.parms.DownParm;
import zimu.gui.parms.SearchParm;

/**
 * 列表页面查询接口
 * @author FH
 *
 */
public class ApiController extends Base {
	static final Log logger = LogFactory.get();
	
	public void open_url() {
		HttpRequest request = getRequest();
		HttpResponse response = getResponse();
		String url = request.getParaToStr("url");
		if(url == null) {
			outJsonpMessage(request,response, 1, "请求数据错误");
			return;
		}
		try {
			Desktop.getDesktop().browse(new URI(url));
		} catch (Exception e1) {
			e1.printStackTrace();
			outJsonpMessage(request,response, 1, "系统错误");
			return;
		}
		outJsonpMessage(request,response, 0, "OK");
	}
	/**
	 * 查询字幕
	 */
	public void zimu_list() {
		HttpRequest request = getRequest();
		HttpResponse response = getResponse();
		String data = request.getParaToStr("data");
		if(data == null) {
			outJsonpMessage(request,response, 1, "请求数据错误");
			return;
		}
		
		JSONObject dataJson = JSONUtil.parseObj(data);
		
		
		SearchParm searchParm = new SearchParm();
		searchParm.from_sheshou = dataJson.getJSONObject("searchParm").getBool("from_sheshou");
		searchParm.from_subhd = dataJson.getJSONObject("searchParm").getBool("from_subhd");
		searchParm.from_xunlei = dataJson.getJSONObject("searchParm").getBool("from_xunlei");
		searchParm.from_zimuku = dataJson.getJSONObject("searchParm").getBool("from_zimuku");
		try {
			DownZIMu.searchList(searchParm);
			System.out.println(DownZIMu.dataArr);
		}catch(Exception e) {
			logger.error(e);
			outJsonpMessage(request,response, 1, "查询出错");
			return;
		}
		
		JSONObject resp = new JSONObject();
		resp.put("list", DownZIMu.dataArr);
		outJsonpMessage(request,response, 0, "OK", resp);
	}
	
	/**
	 * 下载字幕
	 */
	public void zimu_down() {
		HttpRequest request = getRequest();
		HttpResponse response = getResponse();
		String data = request.getParaToStr("data");
		if(data == null) {
			outJsonpMessage(request,response, 1, "请求数据错误");
			return;
		}
		
		JSONObject dataJson = JSONUtil.parseObj(data);
		JSONArray items = dataJson.getJSONArray("items");
		if(items == null || items.size()==0) {
			outJsonpMessage(request,response, 1, "请选择字幕文件");
			return;
		}
		//System.out.println(data);
		
		DownParm downParm;
		JSONObject row;
		int item_id;
		for(int i = 0; i < items.size(); i++) {
			row = items.getJSONObject(i);
			item_id = row.getInt("id");
			downParm = new DownParm();
			downParm.charset = row.getStr("charset", "");
			downParm.simplified = row.getBool("simplified", false);
			downParm.filenameType = row.getInt("filenameType", 1);

			try {
				DownZIMu.down(item_id, downParm);
			}catch(Exception e) {
				logger.error(e);
			}
			
		}
		outJsonpMessage(request,response, 0, "OK");
	}
}
