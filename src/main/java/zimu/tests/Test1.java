package zimu.tests;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import zimu.util.StringUtil;

public class Test1 {
	public static String getUrl(String url) {
		try {
			HttpResponse response = HttpRequest.get(url).execute();

			System.out.println(response.toString());
			int statusCode = response.getStatus();
			if (statusCode == 301 || statusCode == 302) {
				String location = response.header("Location");
				if (!location.toLowerCase().startsWith("http")) {
					location = StringUtil.getBaseUrl(url) + location;
				}
				return getUrl(location);
			} else if (statusCode == 200) {
				return response.body();
			} else {
				System.out.println(url + ", failed: " + statusCode);
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
		}
	}
	public static void main(String[] args) {
		getUrl("https://subhd.tv");
	}

}
