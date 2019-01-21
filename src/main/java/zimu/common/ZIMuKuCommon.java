package zimu.common;

import java.util.regex.Pattern;

import cn.hutool.core.codec.Base64;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import zimu.util.HtHttpUtil;
import zimu.util.StringUtil;
import zimu.util.regex.RegexUtil;

public class ZIMuKuCommon {
	static final Log logger = LogFactory.get();
	static String baseUrl = "https://www.zimuku.cn";
	
	public static void main(String[] args) throws Exception {
		//System.out.println(DownList("憨豆特工.mkv"));
		System.out.println(DownList("downsizing.2017.720p.bluray.x264-geckos.mkv"));
		
		
		
		//System.out.println(downContent("/detail/101779.html"));;
		//detail/100250.html
	}
	/**
	 * 下载字幕列表
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static JSONArray DownList(String fileName) throws Exception {
		JSONArray mainList = getFuzzyPageList(fileName);
		//System.out.println(mainList);
		
		JSONArray resp = new JSONArray();
		for(int i = 0; i < mainList.size(); i++) {
			JSONArray row = mainList.getJSONArray(i);
			//System.out.println("row="+row);
			JSONArray detailList = getDetailList(row.getStr(0));
			if(detailList ==  null)continue;
			for(int j = 0; j < detailList.size(); j++) {
				JSONArray detailRow = detailList.getJSONArray(j);
				JSONObject respRow = new JSONObject();
				respRow.put("url", detailRow.getStr(0));
				respRow.put("title", detailRow.getStr(1));
				respRow.put("ext", detailRow.getStr(2));
				respRow.put("lang", detailRow.getStr(3));
				respRow.put("rate", detailRow.getStr(4));
				respRow.put("downCount", detailRow.getStr(5));
				//System.out.println("detailRow="+detailRow);
				resp.add(respRow);
			}
		}
		return resp;
	}
	
	/**
	 * 下载字幕
	 * @param url
	 * @return
	 */
	public static JSONObject downContent(String url) {
		String result = HtHttpUtil.http.get(baseUrl+url);
		String downUrl = RegexUtil.getMatchStr(result, 
				"<a\\s+id=\"down1\"\\s+href=\"(/dld/[\\w]+\\.html)\""
				, Pattern.DOTALL);
		if(downUrl == null)return null;
		downUrl = baseUrl + downUrl;
		result = HtHttpUtil.http.get(downUrl);
		if(result == null)return null;
		//System.out.println(result);
		JSONArray resList = RegexUtil.getMatchList(result, 
				"<li><a\\s+rel=\"nofollow\"\\s+href=\"(/download/[^\"]+)\"", Pattern.DOTALL);
		if(resList == null || resList.size() == 0 || resList.getJSONArray(0).size() == 0)return null;
		//HtHttpUtil.http.debug=true;
		HttpResponse httpResp = HtHttpUtil.http.getResponse(baseUrl+resList.getJSONArray(0).getStr(0), null, downUrl);
		int i = 0;
		while(httpResp == null && resList.size() > ++i) {
			httpResp = HtHttpUtil.http.getResponse(baseUrl+resList.getJSONArray(1).getStr(0), null, downUrl);
		}
		if(httpResp == null)return null;
		String filename = HtHttpUtil.getFileName(httpResp);
		byte[] data = httpResp.bodyBytes();
		//System.out.println(filename);
		JSONObject resp = new JSONObject();
		resp.put("filename", filename);
		resp.put("ext", StringUtil.extName(filename).toLowerCase());
		resp.put("data", Base64.encode(data));
		
		return resp;
	}
	
	/**
	 * 获取下载网址列表
	 * @return
	 */
	public static JSONArray getDetailList(String url) {
		String result = HtHttpUtil.http.get(baseUrl+url);
		//System.out.println(result);
		JSONArray resList = RegexUtil.getMatchList(result, 
				"<a\\s+href=\"(/detail/[\\w]+\\.html)\"\\s+target=\"_blank\"\\s+title=\"([^\"]*)\">"
				+ ".*?<span\\s+class=\"label\\s+label-info\">(.*?)</span>"
				+ ".*?<img\\s+border=\"0\"\\s+src=\".*?\"\\s+alt=\"(.*?)\""
				+ ".*?title=\"字幕质量:(.*?)\">"
				+ ".*?<td\\s+class=\"tac\\s+hidden-xs\">([\\d]+)</td>", Pattern.DOTALL);
		//System.out.println(resList);
		if(resList == null) {
			return new JSONArray();
		}
		
		
		return resList;
	}
	
	/**
	 * 模糊查询页面列表
	 * @param title
	 * @return
	 */
	public static JSONArray getFuzzyPageList(String title) {
		int pos = title.lastIndexOf(".");
		title = title.toLowerCase();
		title = pos > 0 ? title.substring(0, pos) : title;
		JSONArray list = getPageList(title);
		if(list.size() == 0 && (pos = title.lastIndexOf("bluray")) > 0) {
			title = title.substring(0, pos+6);
			list = getPageList(title);
		}
		if(list.size() == 0 && (pos = title.lastIndexOf(".2160p")) > 0) {
			title = title.substring(0, pos+6);
			list = getPageList(title);
			if(list.size() == 0) {
				title = title.substring(0, pos);
				list = getPageList(title);
			}
		}
		if(list.size() == 0 && (pos = title.lastIndexOf(".1080p")) > 0) {
			title = title.substring(0, pos+6);
			list = getPageList(title);
			if(list.size() == 0) {
				title = title.substring(0, pos);
				list = getPageList(title);
			}
		}
		if(list.size() == 0 && (pos = title.lastIndexOf(".720p")) > 0) {
			title = title.substring(0, pos+5);
			list = getPageList(title);
			if(list.size() == 0) {
				title = title.substring(0, pos);
				list = getPageList(title);
			}
		}
		if(list.size() == 0 && (pos = title.lastIndexOf(".480p")) > 0) {
			title = title.substring(0, pos+5);
			list = getPageList(title);
			if(list.size() == 0) {
				title = title.substring(0, pos);
				list = getPageList(title);
			}
		}
		//System.out.println(list);
		return list;
	}
	/**
	 * 获取页面列表
	 * @param title
	 * @return
	 */
	public static JSONArray getPageList(String title) {
		String result = HtHttpUtil.http.get(baseUrl+"/search?q="+HttpUtil.encodeUtf8(title));
		//System.out.println(result);
		JSONArray resList = RegexUtil.getMatchList(result, "<p\\s+class=\"tt\\s+clearfix\"><a\\s+href=\"(/subs/[\\w]+\\.html)\"\\s+"
				+ "target=\"_blank\"><b>(.*?)</b></a></p>", Pattern.DOTALL);
		//System.out.println(resList);
		if(resList == null) {
			return new JSONArray();
		}
		
		return resList;
	}
}
