package zimu.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import zimu.AppConfig;
import zimu.gui.parms.MovFileInfo;
import zimu.server.ServerMain;

public class MainWin extends JFrame {
	private static final long serialVersionUID = 1L;
	static final Log logger = LogFactory.get();
	public static Image icon = Toolkit.getDefaultToolkit().getImage(MainWin.class.getResource("/res/icon/app.png"));

	private JFXPanel bsPanel = new JFXPanel();
	WebView webview;
	Scene webviewScene;
	
	public static MainWin frame;
	
	public static MainWin get() {
		frame = new MainWin();
		return frame;
	}

	public void init() {
		ServerMain.start();
		GuiConfig.setUIFont();
		setVisible(true);
		setTitle(String.format("%s V%s", AppConfig.appTitle, AppConfig.appVer));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(920, 600);
		setMinimumSize(new Dimension(800, 500));
		setLayout(new BorderLayout());
		setLocationRelativeTo(null);

		setIconImage(icon);

		add(bsPanel, BorderLayout.CENTER);
		openUrl(MainWin.class.getResource("/html/mainwin.html").toExternalForm());

		
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
		final MainWinJsApp webkitJsApp = new MainWinJsApp(this);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				webview = new WebView();
				//webview.setContextMenuEnabled(false);
				
			
				WebEngine webEngine = webview.getEngine();
				
				webEngine.setJavaScriptEnabled(true);
				webEngine.setOnAlert(event -> jsAlert(event.getData()));
				webEngine.setConfirmHandler(message -> jsConfirm(message));
				
				webEngine.setOnStatusChanged(event -> {
					//logger.info(event.getData());
				});
				webEngine.setOnError(event -> {
					logger.info(event.getMessage());
				});

//				webEngine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
//					if (newState == State.SCHEDULED) {
//						//System.out.println("state: scheduled");
//					} else if (newState == State.RUNNING) {
//						//System.out.println("state: running");
//					} else if (newState == State.SUCCEEDED) {
//						//System.out.println("state: succeeded");
//						//webview.getEngine().executeScript("search_start()");
//					}
//				});
				
				
				((JSObject) webEngine.executeScript("window")).setMember("javaApp", webkitJsApp);
				

				
				webEngine.load(url);
				webviewScene = new Scene(webview);
				bsPanel.setScene(webviewScene);
				

				//webEngine.executeScript("alert(322)");
				
				initDragImport();
			}
		});
	}

	/**
	 * 设置拖拽文件
	 */
	public void initDragImport() {

		webview.setOnDragOver(event -> {
			if (event.getDragboard().hasFiles()) {
				/* allow for both copying and moving, whatever user chooses */
				//event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
				event.acceptTransferModes(TransferMode.LINK);
			}
			event.consume();
		});
		webview.setOnDragDropped(event -> {
			Dragboard db = event.getDragboard();
			boolean success = false;
			if (db.hasFiles()) {
				//logger.info(db.getFiles().toString());
				success = true;
				
				if (db.getFiles().get(0).isDirectory()) {
					alert("请选择有效的视频文件");
					return ;
				}
				String filepath = db.getFiles().get(0).getAbsolutePath();
				//System.out.println(">>>"+filepath);
				MovFileInfo.setFile(filepath);
				
				new Thread() {
					public void run() {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								webview.getEngine().executeScript("page_callback()");
							}
						});

					}
				}.start();
				// startSearch();
				// JOptionPane.showMessageDialog(frame, filepath);
			}
			/*
			 * let the source know whether the string was successfully transferred and used
			 */
			event.setDropCompleted(success);

			event.consume();
		});

		// webviewScene.addEventHandler(eventType, eventHandler);
		// setTransferHandler(transferHandler);
	}

	private void jsAlert(String message) {
		JOptionPane.showMessageDialog(this, message);
	}

	private boolean jsConfirm(String message) {
		int r = JOptionPane.showConfirmDialog(this, message, "提示信息", JOptionPane.YES_NO_OPTION);
		if (r == JOptionPane.YES_OPTION) {
			return true;
		} else if (r == JOptionPane.NO_OPTION) {
			return false;
		}
		return false;
	}

	public void alert(String str) {
		JOptionPane.showMessageDialog(this, str);
	}

	public void confirm(String title, String str, Runnable yes, Runnable no) {
		int r = JOptionPane.showConfirmDialog(this, str, title, JOptionPane.YES_NO_OPTION);
		if (r == JOptionPane.YES_OPTION && yes != null) {
			(new Thread(yes)).start();
		} else if (r == JOptionPane.NO_OPTION && no != null) {
			(new Thread(no)).run();
		}
	}

	public static void main(String[] args) throws IOException {
		//MovFileInfo.setFile("H:/_tmp/MOV/downsizing.2017.720p.bluray.x264-geckos.mkv");
		MovFileInfo.setFile("E:/workspace/_me/dev/my_tools/SubTitleSearcher/target/downsizing.2017.720p.bluray.x264-geckos.mkv");
		
		MainWin mainWin = new MainWin();
		mainWin.init();
	}
	
	
}
