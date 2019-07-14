package zimu.server.controllers;

import java.io.ByteArrayInputStream;

import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.web.Controller;

import cn.hutool.json.JSONObject;

public class Base extends Controller {

	/**
	 * 获取返回信息
	 * 
	 * @param result 
	 * 		0=成功
	 * 		>=1=失败
	 * @param message
	 * @return
	 */
	protected JSONObject getAjaxMessage(int result, String message) {
		return getAjaxMessage(result, message, null);
	}

	protected JSONObject getAjaxMessage(int result, String message, JSONObject data) {
		JSONObject json = new JSONObject();
		json.put("result", result);
		json.put("message", message);

		if (data != null) {
			json.putAll(data);
		}

		return json;
	}
	public void outAjaxMessage(HttpResponse resp, int result, String message) {
		outJson(resp, getAjaxMessage(result, message));
	}
	public void outAjaxMessage(HttpResponse resp, int result, String message, JSONObject data) {
		outJson(resp, getAjaxMessage(result, message, data));
	}
	public void outJsonpMessage(HttpRequest req, HttpResponse resp, int result, String message) {
		String callback = req.getParaToStr("callback");
		if(callback == null || callback.length() ==0)callback="callback";
		outJsonp(resp, callback + "(" + getAjaxMessage(result, message).toString()+");");
	}
	public void outJsonpMessage(HttpRequest req, HttpResponse resp, int result, String message, JSONObject data) {
		String callback = req.getParaToStr("callback");
		if(callback == null || callback.length() ==0)callback="callback";
		outJsonp(resp, callback + "(" + getAjaxMessage(result, message, data).toString()+");");
	}
	public void outText(HttpResponse resp, String str){
		//resp.addHeader("Access-Control-Allow-Origin", "*");
		resp.renderHtmlStr(str);
	}
	public void outHtml(HttpResponse resp, String str) {
		//resp.addHeader("Access-Control-Allow-Origin", "*");
		resp.renderHtmlStr(str);
	}
	public void outJson(HttpResponse resp, JSONObject json) {
		resp.addHeader("Access-Control-Allow-Origin", "*");
		resp.renderJson(json);
	}
	public void outJsonp(HttpResponse resp, String data) {
		resp.addHeader("Access-Control-Allow-Origin", "*");
		out(resp, 200, "application/javascript; charset=utf-8", data.getBytes());
	}
	public boolean out(HttpResponse resp,int status, String contentType, byte[] data) {
		try {
			resp.addHeader("Content-Type", contentType);
			resp.write( new ByteArrayInputStream(data), status);
			return true; 
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
