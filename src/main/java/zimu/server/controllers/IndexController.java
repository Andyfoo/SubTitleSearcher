package zimu.server.controllers;

import zimu.AppConfig;

public class IndexController extends Base {
	public void index() {
		//ServerInfo.getVersion()
		outHtml(getResponse(), AppConfig.appName + " " + AppConfig.appVer);
	}
}
