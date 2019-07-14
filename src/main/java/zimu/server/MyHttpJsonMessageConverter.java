package zimu.server;

import com.hibegin.http.server.config.HttpJsonMessageConverter;

import cn.hutool.json.JSONUtil;

public class MyHttpJsonMessageConverter implements HttpJsonMessageConverter {

	@Override
	public String toJson(Object obj) throws Exception {
		return JSONUtil.toJsonStr(obj);
	}

	@Override
	public Object fromJson(String jsonStr) throws Exception {
		return JSONUtil.parse(jsonStr);
	}

}
