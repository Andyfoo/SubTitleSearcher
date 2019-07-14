package zimu.server;

import com.hibegin.http.server.WebServerBuilder;
import com.hibegin.http.server.config.ServerConfig;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import zimu.AppConfig;
import zimu.server.controllers.ApiController;
import zimu.server.controllers.IndexController;

public class ServerMain {
	static final Log logger = LogFactory.get();
	static int tryCount = 0;
	public static void start() {
		logger.info("ServerMain start(port:"+AppConfig.serverPort+")");
		ServerConfig serverConfig = new ServerConfig();
		serverConfig.setHost("127.0.0.1");
		serverConfig.setPort(AppConfig.serverPort);
		serverConfig.setHttpJsonMessageConverter(new MyHttpJsonMessageConverter());
		serverConfig.getRouter().addMapper("", IndexController.class);
		serverConfig.getRouter().addMapper("/api", ApiController.class);
		boolean runResult = new WebServerBuilder.Builder().serverConfig(serverConfig).build().startWithThread();
		logger.info("ServerMain start runResult="+runResult);
		if(!runResult && tryCount++ < 50) {
			AppConfig.serverPort++;
			start();
		}

	}
	public static void main(String[] args) {
		start();
	}

}
