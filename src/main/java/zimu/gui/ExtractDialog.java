package zimu.gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import com.github.stuxuhai.jpinyin.ChineseHelper;

import cn.hutool.core.date.DateTime;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import zimu.AppConfig;
import zimu.gui.parms.DownParm;
import zimu.util.MyFileUtil;
import zimu.util.StringUtil;
import zimu.util.WinRarUtil;

public class ExtractDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	static final Log logger = LogFactory.get();

	String title;

	private JFXPanel bsPanel = new JFXPanel();
	WebView webview;
	Scene webviewScene;

	byte[] archiveData;
	String archiveExt;
	String subFilename;

	String archivePath;
	List<File> archiveFiles;

	DownParm downParm;
	int zimuIndex;

	public ExtractDialog(Frame parent, int index, DownParm downParm, String title, String ext, String filename, byte[] data) {
		super(parent, true);

		this.downParm = downParm;
		this.zimuIndex = index;
		this.title = title;
		if (data == null) {
			data = new byte[0];
		}
		this.archiveData = data;
		this.archiveExt = ext;
		this.subFilename = filename;

		initData();
		initComponents();
	}

	@Override
	protected JRootPane createRootPane() {
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		JRootPane rootPane = new JRootPane();
		rootPane.registerKeyboardAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				escapeKeyProc();
			}
		}, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

		return rootPane;
	}

	protected void escapeKeyProc() {
		clear();
		setVisible(false);
	}

	public void alert(String str) {
		JOptionPane.showMessageDialog(this, str);
	}

	private void initData() {
		archivePath = AppConfig.appPath + "tmpData/" + DateTime.now().toString("yyyyMMddHHmmss");
		archiveFiles = WinRarUtil.unRar(archiveExt, archiveData, archivePath);
		if (archiveFiles == null) {
			logger.error("解压失败");
			alert("压缩文件解压失败");
			return;
		}

	}

	public boolean saveSelected(JSONArray items) {
		for (int i = 0; i < items.size(); i++) {
			JSONObject item = items.getJSONObject(i);
			DownParm downParm = new DownParm();
			downParm.charset = item.getStr("charset", "");
			downParm.simplified = item.getBool("simplified", false);
			downParm.filenameType = item.getInt("filenameType", 1);
			saveFile(item.getStr("title"), downParm);
		}
		return true;
	}

	private void saveFile(String dtitle, DownParm downParm) {
		for (int i = 0; i < archiveFiles.size(); i++) {
			File file = archiveFiles.get(i);
			String title = file.getName();
			if (title.equals(dtitle)) {
				String filename = subFilename;
				if (downParm.filenameType == DownParm.filenameType_BAT) {
					filename += (".chn" + (zimuIndex + 1)) + "&" + i + "." + StringUtil.extName(file);
				} else {
					filename += "." + StringUtil.extName(file);
				}
				logger.info("save=" + filename);
				// if(MainWin.simplifiedCheckBox != null && MainWin.simplifiedCheckBox.isSelected()) {
				if (downParm.simplified) {
					String dataStr = null;
					try {
						dataStr = new String(MyFileUtil.fileReadBin(file), downParm.charset);
						dataStr = ChineseHelper.convertToSimplifiedChinese(dataStr);
						MyFileUtil.fileWrite(filename, dataStr, "UTF-8", "");
					} catch (Exception e) {
						logger.error(e);
					}
				} else {
					MyFileUtil.fileWriteBin(filename, MyFileUtil.fileReadBin(file));
				}
			}
		}
	}

	/**
	 * 清除临时目录
	 */
	private void clear() {
		try {
			WinRarUtil.clear(archivePath);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private void initComponents() {
		ExtractDialog _this = this;
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		// setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setIconImage(MainWin.icon);
		setSize(820, 460);
		// setResizable(false);

		setLocationRelativeTo(this.getParent());
		setTitle("请选择压缩包中要保存的字幕文件");

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				_this.clear();
				_this.dispose();
			}
		});

		add(bsPanel, BorderLayout.CENTER);
		openUrl(MainWin.class.getResource("/html/extract_dialog.html").toExternalForm());

	}

	/**
	 * 打开网址
	 * 
	 * @param url
	 */
	private void openUrl(String url) {
		com.sun.javafx.webkit.WebConsoleListener.setDefaultListener((webView, message, lineNumber, sourceId) -> {
			logger.info("from webview: " + message + " [" + sourceId + " - " + lineNumber + "]");
		});
		ExtractDialogJsApp extractDialogJsApp = new ExtractDialogJsApp(this);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				webview = new WebView();
				// webview.setContextMenuEnabled(false);

				WebEngine webEngine = webview.getEngine();

				webEngine.setJavaScriptEnabled(true);

				webEngine.setOnError(event -> {
					logger.info(event.getMessage());
				});
				((JSObject) webEngine.executeScript("window")).setMember("javaApp", extractDialogJsApp);

				webEngine.load(url);
				webviewScene = new Scene(webview);
				bsPanel.setScene(webviewScene);
			}
		});
	}

	public static void main(String args[]) {
		GuiConfig.setUIFont();
		// String filename = "E:/workspace/_me/dev/my_libs/test_lib/data/file/archive/test.7z";

		String ext = "rar";
		String filename = "H:/_tmp/MOV/[zmk.tw]Downsizing.2017.1080p.BluRay.x264-GECKOS." + ext;
		filename = "E:/workspace/_me/dev/my_tools/SubTitleSearcher/target/test.rar";

		byte[] data = MyFileUtil.fileReadBin(filename);

		final JFrame frame = new JFrame("test");
		frame.setSize(900, 500);
		// frame.setBackground(Color.LIGHT_GRAY);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		new ExtractDialog(frame, 1, DownParm.def, "asdfffffff", ext, "aaa", data).setVisible(true);
		System.exit(0);
	}

}
