package zimu.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import zimu.util.regex.RegexUtil;

/**
 * 使用jodd访问http页面
 */
public class HtHttpUtil {
	static final Log logger = LogFactory.get();
	
	public boolean debug = false;

	public String default_charset = "UTF-8";
	public int _time_out = 50000;// 超时时间

	public String _ua = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36";
	public String _accept = "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5";
	public String _accept_encoding = "gzip,deflate";
	public String _accept_language = "zh-cn";
	public String _accept_charset = "gb2312,utf-8;q=0.7,*;q=0.7";
	public String _stream_media_type = "text/html";

	public Map<String, String> _headers = new HashMap<String, String>();

	/**
	 * 静态实例
	 */
	public static HtHttpUtil http = newInstance();

	public static HtHttpUtil newInstance() {
		return new HtHttpUtil();
	}

	public HttpRequest addHeader(HttpRequest req) {
		if (_headers.size() > 0) {
			Set<String> keyset = _headers.keySet();
			for (String key : keyset) {
				req.header(key, _headers.get(key));
			}
		}
		return req;
	}
	
	/**
	 * 获取头部文件名配置
	 * Content-Disposition=[attachment; filename="Downsizing.2017.1080p.Bluray.MKV.x264.AC3.ass"]
	 * @param resp
	 * @return
	 */
	public static String getFileName(HttpResponse resp) {
		String desp = new String(resp.header("Content-Disposition").getBytes(CharsetUtil.CHARSET_ISO_8859_1), CharsetUtil.CHARSET_UTF_8);
		if(desp == null || desp.indexOf("filename") == -1)return null;
		return RegexUtil.getMatchStr(desp, "filename=\"([^\"]+)\"");
	}

	/**
	 * 读取网页
	 * 
	 * @param url
	 *                = url地址
	 * @param charset
	 *                = 编码
	 * 
	 * @return 网页内容
	 */
	public String get(String url) {
		return get(url, default_charset);
	}

	public String get(String url, String charset) {
		return get(url, charset, _ua);
	}

	public String get(String url, String charset, String ua) {
		return get(url, charset, ua, null);
	}

	public String get(String url, String charset, String ua, String refer) {
		logger.info("get=" + url);

		try {
			HttpResponse response = addHeader(HttpRequest.get(url).header(Header.USER_AGENT, ua).header(Header.ACCEPT_CHARSET, charset).charset(charset).header(Header.ACCEPT, _accept)
					.header(Header.ACCEPT_ENCODING, _accept_encoding).header(Header.REFERER, refer != null ? refer : url).header(Header.ACCEPT_LANGUAGE, _accept_language).timeout(_time_out))
							.execute();

			if(debug) {
				logger.info(response.toString());
			}
			int statusCode = response.getStatus();
			if (statusCode == 301) {
				return get(response.header("Location"),charset, ua, url);
			}else if (statusCode == 200) {
				return response.body();
			} else {
				logger.error(url + ", failed: " + statusCode);
				return null;
			}
		} catch (Exception e) {
			logger.error(e);
			return null;
		} finally {
		}
	}

	/**
	 * 读取网页
	 * 
	 * @param url
	 *                = url地址
	 * @param charset
	 *                = 编码
	 * 
	 * @return 网页内容
	 */
	public String post(String url) {
		return post(url, null, default_charset);
	}

	public String post(String url, Map<String, Object> list) {
		return post(url, list, default_charset, _ua);
	}

	public String post(String url, Map<String, Object> list, String charset) {
		return post(url, list, charset, _ua);
	}

	public String post(String url, Map<String, Object> list, String charset, String ua) {
		return post(url, list, charset, _ua, null);
	}

	public String post(String url, Map<String, Object> list, String charset, String ua, String refer) {
		logger.info("post=" + url);
		try {
			HttpResponse response = addHeader(HttpRequest.post(url).form(list).header(Header.USER_AGENT, ua).header(Header.ACCEPT_CHARSET, charset).charset(charset).header(Header.ACCEPT, _accept)
					.header(Header.ACCEPT_ENCODING, _accept_encoding).header(Header.REFERER, refer != null ? refer : url).header(Header.ACCEPT_LANGUAGE, _accept_language).timeout(_time_out))
							.execute();

			if(debug) {
				logger.info("response header:"+getRespHeaderStr(response));
			}
			int statusCode = response.getStatus();
			if (statusCode == 301) {
				return post(response.header("Location"), list, charset, ua, url);
			}else if (statusCode == 200) {
				return response.body();
			} else {
				logger.error(url + ", failed: " + statusCode);
				return null;
			}
		} catch (Exception e) {
			logger.error(e);
			return null;
		} finally {
		}
	}

	/**
	 * post 流数据
	 * 
	 * @param url
	 *                = url地址
	 * @param encode
	 *                = 编码
	 * 
	 * @return 网页内容
	 */
	public String postStream(String url) {
		return postStream(url, null, default_charset);
	}

	public String postStream(String url, String data) {
		return postStream(url, data, default_charset, _ua);
	}

	public String postStream(String url, String data, String charset) {
		return postStream(url, data, charset, _ua);
	}

	public String postStream(String url, String data, String charset, String ua) {
		return postStream(url, data, charset, _ua, null);
	}

	public String postStream(String url, String data, String charset, String ua, String refer) {
		logger.info("postStream=" + url);
		try {
			HttpResponse response = addHeader(HttpRequest.post(url).body(data, _stream_media_type).header(Header.USER_AGENT, ua).header(Header.ACCEPT_CHARSET, charset).charset(charset)
					.header(Header.ACCEPT, _accept).header(Header.ACCEPT_ENCODING, _accept_encoding).header(Header.REFERER, refer != null ? refer : url)
					.header(Header.ACCEPT_LANGUAGE, _accept_language).timeout(_time_out)).execute();

			if(debug) {
				logger.info("response header:"+getRespHeaderStr(response));
			}
			int statusCode = response.getStatus();
			if (statusCode == 301) {
				return postStream(response.header("Location"),data, charset, ua, url);
			}else if (statusCode == 200) {
				return response.body();
			} else {
				logger.error(url + ", failed: " + statusCode);
				return null;
			}
		} catch (Exception e) {
			logger.error( e);
			return null;
		} finally {
		}

	}

	
	public String getRespHeaderStr(HttpResponse response) {
		StringBuilder sb = StrUtil.builder();
		sb.append("Response Headers: ").append(StrUtil.CRLF);
		Map<String, List<String>> headers = response.headers();
		for (Entry<String, List<String>> entry : headers.entrySet()) {
			sb.append("\t").append(entry).append(StrUtil.CRLF);
		}
		return sb.toString();
	}
	/**
	 * 返回HttpResponse
	 * 
	 * @param url
	 *                = url地址
	 * @return 网页内容
	 */
	public HttpResponse getResponse(String url) {
		return getResponse(url, _ua);
	}

	public HttpResponse getResponse(String url, String ua) {
		return getResponse(url, ua, null);
	}

	public HttpResponse getResponse(String url, String ua, String refer) {
		logger.info("getResponse=" + url);

		try {
			HttpResponse response = addHeader(HttpRequest.get(url).header(Header.USER_AGENT, ua).header(Header.ACCEPT, _accept).header(Header.ACCEPT_ENCODING, _accept_encoding)
					.header(Header.REFERER, refer != null ? refer : url).header(Header.ACCEPT_LANGUAGE, _accept_language).timeout(_time_out)).execute();

			if(debug) {
				logger.info("response header:"+getRespHeaderStr(response));
			}
			int statusCode = response.getStatus();
			if (statusCode == 301) {
				return getResponse(response.header("Location"), ua, url);
			}else if (statusCode == 200) {
				return response;
			} else {
				logger.error(url + ", failed: " + statusCode);
				return null;
			}
		} catch (Exception e) {
			logger.error(e);
			return null;
		} finally {
		}
	}
	/**
	 * 读取2进制数据
	 * 
	 * @param url
	 *                = url地址
	 * @return 网页内容
	 */
	public byte[] getBytes(String url) {
		return getBytes(url, _ua);
	}

	public byte[] getBytes(String url, String ua) {
		return getBytes(url, ua, null);
	}

	public byte[] getBytes(String url, String ua, String refer) {
		logger.info("getBytes=" + url);

		try {
			HttpResponse response = addHeader(HttpRequest.get(url).header(Header.USER_AGENT, ua).header(Header.ACCEPT, _accept).header(Header.ACCEPT_ENCODING, _accept_encoding)
					.header(Header.REFERER, refer != null ? refer : url).header(Header.ACCEPT_LANGUAGE, _accept_language).timeout(_time_out)).execute();

			if(debug) {
				logger.info("response header:"+getRespHeaderStr(response));
			}
			int statusCode = response.getStatus();
			if (statusCode == 301) {
				return getBytes(response.header("Location"), ua, url);
			}else if (statusCode == 200) {
				return response.bodyBytes();
			} else {
				logger.error(url + ", failed: " + statusCode);
				return null;
			}
		} catch (Exception e) {
			logger.error(e);
			return null;
		} finally {
		}
	}

	public byte[] postBytes(String url) {
		return postBytes(url, null, _ua, null);
	}

	public byte[] postBytes(String url, Map<String, Object> list) {
		return postBytes(url, list, _ua, null);
	}

	public byte[] postBytes(String url, Map<String, Object> list, String ua) {
		return postBytes(url, list, _ua, null);
	}

	public byte[] postBytes(String url, Map<String, Object> list, String ua, String refer) {
		logger.info("postBytes=" + url);

		try {
			HttpResponse response = addHeader(HttpRequest.post(url).form(list).header(Header.USER_AGENT, ua).header(Header.ACCEPT, _accept).header(Header.ACCEPT_ENCODING, _accept_encoding)
					.header(Header.REFERER, refer != null ? refer : url).header(Header.ACCEPT_LANGUAGE, _accept_language).timeout(_time_out)).execute();

			if(debug) {
				logger.info("response header:"+getRespHeaderStr(response));
			}
			int statusCode = response.getStatus();
			if (statusCode == 301) {
				return postBytes(response.header("Location"),list, ua, url);
			}else if (statusCode == 200) {
				return response.bodyBytes();
			} else {
				logger.error(url + ", failed: " + statusCode);
				return null;
			}
		} catch (Exception e) {
			logger.error(e);
			return null;
		} finally {
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String url = "http://www.test.com";
		url = "http://localhost/print_r.php";
		url = "http://localhost/print_r_utf8.php";

		// System.out.println(inst.get(url));
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("aaaa", "111");
		System.out.println(http.post(url, data));

		//byte[] bytes = http.getBytes("http://www.oschina.net/img/newindex-03.svg?t=1451961935000");
		// System.out.println(inst.postStream(url, "asdsasa中文sfdasd"));

	}

}
