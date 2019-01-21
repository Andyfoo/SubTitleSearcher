package zimu;

import java.io.File;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import zimu.gui.MainWin;

public class AppMain {
	static final Log logger = LogFactory.get();
	public static void main(String[] args) {
		try {
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
			//UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			//UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.mac.MacLookAndFeel");//需要在相关的操作系统上方可实现
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");//需要在相关的操作系统上方可实现
		} catch (Exception e) {
			logger.error(e);
		}
		MainWin.init();
		if(args.length > 0) {
			//final File fileFromCommandLine = getFileFromCommandLine(args);
			//MainWin.setFile(fileFromCommandLine.getAbsolutePath());
			if(args.length > 0) {
				String filepath = args[0];
				File file = new File(filepath);
				if(file.exists()) {
					MainWin.setFile(file.getAbsolutePath());
					MainWin.startSearch();
				}else {
					filepath = System.getProperty("user.dir") + File.separator + filepath;
					file = new File(filepath);
					if(file.exists()) {
						MainWin.setFile(file.getAbsolutePath());
						MainWin.startSearch();
					}
				}
			}
			//JOptionPane.showMessageDialog(MainWin.frame, System.getProperty("user.dir"));
		}
	}
 

}
