package zimu.gui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.filechooser.FileNameExtensionFilter;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import zimu.AppConfig;
import zimu.common.ZiMuCommon;
import zimu.gui.parms.MovFileInfo;
import zimu.gui.parms.SearchParm;

public class MainWinJsApp {
	MainWin mainWin;
	public MainWinJsApp(MainWin mainWin) {
		this.mainWin = mainWin;
	}

	/**
	 * 选择视频文件
	 * @return
	 */
	public boolean openMovFile() {
		JFileChooser jfc = new JFileChooser();
		
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"视频文件(*.mkv; *mp4; *.mov; *.avi; *.ts)", "mkv", "mp4", "mov", "avi", "ts");
		jfc.setFileFilter(filter);
		if(MovFileInfo.lastSelPath!=null) {
			jfc.setCurrentDirectory(new File(MovFileInfo.lastSelPath));
		}
		int status = jfc.showDialog(new JLabel(), "选择视频文件");
		
		if(JFileChooser.APPROVE_OPTION == status) {
			File file = jfc.getSelectedFile();
			if (file != null && file.isFile()) {
				String filepath = file.getAbsolutePath();
				if(file.isDirectory()) {
					alert("请选择有效的视频文件");
					return false;
				}
				MovFileInfo.setFile(filepath);
				return true;
			}
		}else if(JFileChooser.ERROR_OPTION == status) {
			alert("选择文件失败");
			return false;
		}
		return false;
	}
	//复制字符串
	public void copyClipboard(String str) {
		ZiMuCommon.copyClipboard(str);
	}
	
	//退出程序
	public void exit() {
		System.exit(0);
	}
	
	/**
	 * 获取初始化数据
	 */
	public String getInitData() {
		JSONObject resp = new JSONObject();
		JSONObject fileinfo = new JSONObject();
		fileinfo.put("lastSelPath", MovFileInfo.lastSelPath);
		fileinfo.put("movFilename", MovFileInfo.movFilename);
		resp.put("fileinfo", fileinfo);
		resp.put("searchParm", JSONUtil.parseObj(SearchParm.def));
		resp.put("serverPort", AppConfig.serverPort);
		return resp.toString();
	}
	
	public void alert(String msg) {
		mainWin.alert(msg);
	}
	
	public void about() {
		new AboutDialog(mainWin).setVisible(true);
	}
	
	public void test() {
		System.out.println("test");
	}
	
	protected void finalize()
	                 throws Throwable{
		System.out.println("exit");
	}
	
	public static void main(String[] args) {
		System.out.println(JSONUtil.parseObj(SearchParm.def));
		
	}
}

