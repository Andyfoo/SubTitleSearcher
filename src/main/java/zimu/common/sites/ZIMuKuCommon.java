package zimu.common.sites;

import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import zimu.util.ExeJsUtil;
import zimu.util.HtHttpUtil;
import zimu.util.StringUtil;
import zimu.util.regex.RegexUtil;

public class ZIMuKuCommon {
	static final Log logger = LogFactory.get();
	/**
	 * 字幕库启用新域名：www.zimuku.la （强烈推荐收藏永久备用米：zmk.tw）

	 */
	//static String baseUrl = "https://www.zimuku.cn";
	static String baseUrl = "https://www.zimuku.la";

	
	
	
	public static void main(String[] args) throws Exception {
		//System.out.println(DownList("憨豆特工.mkv"));
		//System.out.println(DownList("downsizing.2017.720p.bluray.x264-geckos.mkv"));
		//System.out.println(DownList("From.Beijing.with.Love.1994.720p.BluRay.x264-WiKi.mkv"));
		//System.out.println(getPageList("From.Beijing.with.Love"));
		
		System.out.println(downContent("/detail/101779.html"));;
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
		String result = httpGet(baseUrl+url);
		String downUrl = RegexUtil.getMatchStr(result, 
				"<a\\s+id=\"down1\"\\s+href=\"([^\"]*/dld/[\\w]+\\.html)\""
				, Pattern.DOTALL);
		if(downUrl == null)return null;
		if(!downUrl.startsWith("http")) {
			downUrl = baseUrl + downUrl;
		}
		
		result = httpGet(downUrl);
		if(result == null)return null;
		//System.out.println(result);
		JSONArray resList = RegexUtil.getMatchList(result, 
				"<li><a\\s+rel=\"nofollow\"\\s+href=\"([^\"]*/download/[^\"]+)\"", Pattern.DOTALL);
		if(resList == null || resList.size() == 0 || resList.getJSONArray(0).size() == 0)return null;
		//HtHttpUtil.http.debug=true;
		HttpResponse httpResp = HtHttpUtil.http.getResponse(addBaseUrl(resList.getJSONArray(0).getStr(0)), null, downUrl);
		int i = 0;
		while(httpResp == null && resList.size() > ++i) {
			httpResp = HtHttpUtil.http.getResponse(addBaseUrl(resList.getJSONArray(1).getStr(0)), null, downUrl);
		}
		//System.out.println(httpResp);
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
	
	public static String addBaseUrl(String url) {
		if(url == null || url.startsWith("http://") || url.startsWith("https://")) {
			return url;
		}
		return baseUrl+url;
	}
	
	/**
	 * 获取下载网址列表
	 * @return
	 */
	public static JSONArray getDetailList(String url) {
		String result = httpGet(baseUrl+url);
		//System.out.println(result);
		Document doc = Jsoup.parse(result);
		Elements matchList = doc.select("#subtb tbody tr");
		if(matchList.size() == 0)return new JSONArray();
		//System.out.println(matchList.html());
		JSONArray resList = new JSONArray();
		for(int i  = 0 ; i < matchList.size(); i++) {
			Element row = matchList.get(i);
			JSONObject resRow = new JSONObject();
			resRow.put("url", row.selectFirst("a").attr("href"));
			resRow.put("title", row.selectFirst("a").attr("title"));
			resRow.put("ext", row.selectFirst(".label-info").text());
			Elements authorInfos = row.select(".gray");
			StringBuffer authorInfo = new StringBuffer();
			authorInfos.forEach(element ->{
				authorInfo.append(element.text() + "，");
			});
			if(authorInfo.length() > 0) {
				resRow.put("authorInfo", authorInfo.toString().substring(0, authorInfo.length()-1));
			}else {
				resRow.put("authorInfo", "");
			}
			
			resRow.put("lang", row.selectFirst("img").attr("alt"));
			resRow.put("rate", row.selectFirst(".rating-star").attr("title").replace("字幕质量:", ""));
			resRow.put("downCount", row.select("td").get(3).text());
			resList.add(resRow);
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
		if(list.size() == 0 && (pos = title.lastIndexOf(".internal")) > 0) {
			title = title.substring(0, pos+9);
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
		String result = httpGet(baseUrl+"/search?q="+URLUtil.encodeAll(title, CharsetUtil.CHARSET_UTF_8));
		//System.out.println(result);
		JSONArray resList = RegexUtil.getMatchList(result, "<p\\s+class=\"tt\\s+clearfix\"><a\\s+href=\"(/subs/[\\w]+\\.html)\"\\s+"
				+ "target=\"_blank\"><b>(.*?)</b></a></p>", Pattern.DOTALL);
		//System.out.println(resList);
		if(resList == null) {
			return new JSONArray();
		}
		
		return resList;
	}
	
	public static String httpGet(String url) {
		String result = HtHttpUtil.http.get(url);
		if(result!=null && StrUtil.count(result, "url")>10 && result.contains("<script")) {
			String jsStr = RegexUtil.getMatchStr(result, "<script[^>]*>(.*?)</script>");
			jsStr = jsStr.replaceAll("window.location[\\s]*=[\\s]*url", "");
			jsStr = jsStr.replaceAll("location[\\s]*=[\\s]*url", "");
			if(jsStr==null) {
				jsStr = "";
			}
			String jsVal = null;
			try {
				jsVal = ExeJsUtil.getJsVal("function getUrl(){"+jsStr+";return url;} getUrl()");
			}catch(Exception e) {
				logger.error(e);
			}
			if(jsVal!=null&&jsVal.length()>0) {
				return httpGet(jsVal.contains("://") ? jsVal : baseUrl+jsVal);
			}
		}
		return result;
	}
}
