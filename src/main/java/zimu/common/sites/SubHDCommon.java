package zimu.common.sites;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import zimu.AppConfig;
import zimu.util.HtHttpUtil;
import zimu.util.StringUtil;
import zimu.util.regex.RegexUtil;

public class SubHDCommon {
	static final Log logger = LogFactory.get();
	static String baseUrl = "https://subhd.tv";
	
	public static void main(String[] args) throws Exception {
		//System.out.println(DownList("憨豆特工.mkv"));
		System.out.println(DownList("downsizing.2017.720p.bluray.x264-geckos.mkv"));
		System.out.println(getDetailList("/do0/3578939"));
		
		
		System.out.println(downContent("/ar0/378333"));;
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
			resp.addAll(detailList);
		}
		return resp;
	}
	
	/**
	 * 下载字幕
	 * @param url
	 * @return
	 */
	public static JSONObject downContent(String url) {
		String result = HtHttpUtil.http.get(baseUrl+url, HtHttpUtil.http.default_charset, HtHttpUtil.http._ua, baseUrl+url);
		Document doc = Jsoup.parse(result);
		Elements matchList = doc.select("#down");
		if(matchList.size() == 0)return null;
		Element down = matchList.get(0);
		Map<String, Object> postData = new HashMap<String, Object>();
		postData.put("sub_id", matchList.attr("sid"));
		postData.put("dtoken", down.attr("dtoken"));
		result = HtHttpUtil.http.post(baseUrl+"/ajax/down_ajax", postData);
		if(result == null || !result.contains("}"))return null;
		JSONObject resultJson = JSONUtil.parseObj(result);
		if(resultJson == null || !resultJson.getBool("success"))return null;
		String downUrl = resultJson.getStr("url");
		String filename = StringUtil.basename(downUrl);
		//HtHttpUtil.http.debug=true;
		byte[] data = HtHttpUtil.http.getBytes(downUrl, HtHttpUtil.http._ua, baseUrl+url);
		
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
		String result = HtHttpUtil.http.get(baseUrl+url, HtHttpUtil.http.default_charset, HtHttpUtil.http._ua, baseUrl+url);
		Document doc = Jsoup.parse(result);
		Elements matchList = doc.select(".d_table tr");
		//System.out.println(matchList.html());
		JSONArray detailList = new JSONArray();
		for (Element matchRow : matchList) {
			if(matchRow.select(".dt_edition").size() == 0)continue;
			String html = matchRow.html();
			String htmlLower = html.toLowerCase();
			String downUrl = matchRow.select(".dt_down a").attr("href");
			String title = matchRow.select(".dt_edition a").text().trim();
			int downCount = Integer.valueOf(RegexUtil.getMatchStr(matchRow.select(".dt_count").text(), "([\\d]+)"));
			String ext = "";
			for(String extName : AppConfig.subExtNames) {
				//if(StrUtil.isNotEmpty(RegexUtil.getMatchStr(html, "(>"+extName+"<)", Pattern.CASE_INSENSITIVE))) {
				if(htmlLower.contains(">"+extName+"<")) {
					ext += extName;
					ext += ",";
				}
			}
			if(ext.endsWith(",")) {
				ext=ext.substring(0, ext.length()-1);
			}else {
				ext="其它";
			}
			
			String lang = "";
			String[] langList = new String[] {"双语", "简体", "繁体", "英文"};
			for(String langName : langList) {
				if(htmlLower.contains(">"+langName+"<")) {
					lang += langName;
					lang += ",";
				}
			}
			if(lang.endsWith(",")) {
				lang=lang.substring(0, lang.length()-1);
			}else {
				lang="其它";
			}
			
			Elements labels = matchRow.select(".label");
			StringBuffer labelInfo = new StringBuffer();
			labels.forEach(element ->{
				labelInfo.append(element.text() + "，");
			});
			if(labelInfo.length() > 0) {
				labelInfo.delete(labelInfo.length()-1, labelInfo.length());
			}
			String zimuzu = matchRow.select("a.gray").text();
			
			JSONObject dataRow = new JSONObject();
			dataRow.put("url", downUrl);
			dataRow.put("title", title);
			dataRow.put("ext", ext);
			dataRow.put("lang",lang);
			dataRow.put("rate", "-");
			dataRow.put("downCount", downCount);
			dataRow.put("labelInfo", labelInfo);
			dataRow.put("zimuzu", zimuzu);
			detailList.add(dataRow);
		}
		return detailList;
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
		String result = HtHttpUtil.http.get(baseUrl+"/search0/"+URLUtil.encodeAll(title, CharsetUtil.CHARSET_UTF_8), HtHttpUtil.http.default_charset,HtHttpUtil.http._ua, baseUrl);
		//System.out.println(result);
		JSONArray resList = RegexUtil.getMatchList(result, "<a href=\"(/do[\\w]+/[\\w]+)\"><img", Pattern.DOTALL);
		//System.out.println(resList);
		if(resList == null) {
			return new JSONArray();
		}
		
		return resList;
	}
}
