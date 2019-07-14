package zimu.gui.parms;

import java.io.File;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

public class MovFileInfo {
	static final Log logger = LogFactory.get();
	public static String movFilename = "";
	public static String lastSelPath = null;
	
	
	public static void setFile(String filepath) {
		File file = new File(filepath);
		lastSelPath = file.getParentFile().getAbsolutePath();
		movFilename = filepath;
		logger.info(movFilename);
	}	
}
