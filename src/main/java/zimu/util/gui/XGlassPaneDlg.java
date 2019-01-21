package zimu.util.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;

import zimu.gui.GuiConfig;

public class XGlassPaneDlg extends JComponent implements MouseListener {
	private static final long serialVersionUID = 1L;
	

	public static void main(String[] args) {

		final JDialog frame = new JDialog();
		frame.setSize(800, 500);
		// frame.setBackground(Color.LIGHT_GRAY);
		frame.setVisible(true);
		JButton btn = new JButton("test");
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tips(frame, "sadfsa");
			}
			
		});
		frame.add(btn);
		//	tips(frame, "asdfasdfdas", 10000);
		//XGlassPaneDlg xGlassPane  =  loading(frame);
//		
//		new Thread() {
//			public void run() {
//				try {
//					Thread.sleep(4000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				xGlassPane.close();
//				tips(frame, "asdfasdfdas");
//				new Thread() {
//					public void run() {
//						try {
//							Thread.sleep(4000);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						loading(frame);
//					}
//				}.start();
//				
//			}
//		}.start();
		
	}
	static Map<JDialog, XGlassPaneDlg> instanceMap = new ConcurrentHashMap<JDialog, XGlassPaneDlg> ();
	public JDialog parent;
	public ImageIcon loadingImg;
	public JLabel loadingLabelAni;
	public Rectangle loadingLabelAniBounds;
	
	public JLabel tipsLabel;
	public Rectangle tipsLabelBounds;
	
	public boolean shadeClose = false;
	public String type = "";

	public XGlassPaneDlg(JDialog parent) {
		GuiConfig.setUIFont();
		this.parent = parent;
		this.addMouseListener(this);
		setOpaque(false);
		parent.setGlassPane(this);
		
		//loading
		loadingImg = new ImageIcon(XGlassPaneDlg.class.getClass().getResource("/res/img/loading.gif"));
		//loadingImg.setImage(loadingImg.getImage().getScaledInstance((int)(loadingImg.getIconWidth()*0.8), (int)(loadingImg.getIconHeight()*0.8), Image.SCALE_DEFAULT));
		loadingLabelAni = new JLabel(loadingImg);
		loadingLabelAniBounds = new Rectangle(100, 200, loadingImg.getIconWidth(), loadingImg.getIconHeight());
		loadingLabelAni.setBounds(loadingLabelAniBounds);
		loadingLabelAni.setVisible(false);
		add(loadingLabelAni);
		
		
		
		//tips
		tipsLabel = new JLabel("", JLabel.CENTER);
		tipsLabel.setOpaque(true);
		tipsLabel.setBackground(new Color(0, 0, 0, 130));
		tipsLabel.setForeground(new Color(255, 255, 255));
		tipsLabelBounds = new Rectangle(100, 200, 320, 50);
		tipsLabel.setBounds(tipsLabelBounds);
		tipsLabel.setVisible(false);
		add(tipsLabel);
		
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				resize();
			}
		});
	}
	/**
	 * 窗口大小改变事件处理
	 */
	public void resize() {
		//setSize(parent.getSize());
		if(loadingLabelAni.isVisible()) {
			loadingLabelAniBounds.x = (getWidth() - loadingLabelAni.getWidth()) / 2;
			loadingLabelAniBounds.y = (getHeight() - loadingLabelAni.getHeight()) / 2;
			loadingLabelAni.setBounds(loadingLabelAniBounds);
		}
		if(tipsLabel.isVisible()) {
			tipsLabelBounds.x = (getWidth() - tipsLabel.getWidth()) / 2;
			tipsLabelBounds.y = (getHeight() - tipsLabel.getHeight()) / 2;
			tipsLabel.setBounds(tipsLabelBounds);
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(new Color(255, 255, 255, 120));
		g.fillRect(0, 0, getWidth(), getHeight());
		super.paintComponent(g);
	}
	public static XGlassPaneDlg getInstance(JDialog parent) {
		if(!instanceMap.containsKey(parent)) {
			instanceMap.put(parent, new XGlassPaneDlg(parent));
		}
		return instanceMap.get(parent);
		//return new XGlassPane(parent);
	}
	
	/**
	 * 加载提示层
	 * @param parent
	 * @return
	 */
	public static XGlassPaneDlg loading(JDialog parent) {
		XGlassPaneDlg xGlassPane = getInstance(parent);
		xGlassPane.type = "loading";
		xGlassPane.shadeClose = false;
		xGlassPane.setVisible(true);
		xGlassPane.loadingLabelAni.setVisible(true);
		xGlassPane.resize();
		return xGlassPane;
	}
	public static void loadingThread(JDialog parent) {

		new Thread() {
			public void run() {
				try {
					XGlassPaneDlg xGlassPane = getInstance(parent);
					xGlassPane.type = "loading";
					xGlassPane.shadeClose = false;
					xGlassPane.setVisible(true);
					xGlassPane.loadingLabelAni.setVisible(true);
					xGlassPane.resize();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public static void tips(JDialog parent, String msg) {
		tips(parent, msg, 2000, false);
	}
	public static void tips(JDialog parent, String msg, int timeout) {
		tips(parent, msg, timeout, false);
	}
	public static void tips(JDialog parent, String msg, boolean shadeClose) {
		tips(parent, msg, 2000, shadeClose);
	}
	/**
	 * 提示层
	 * @param parent
	 * @param msg
	 * @param timeout
	 * @param shadeClose
	 */
	public static void tips(JDialog parent, String msg, final int timeout, boolean shadeClose) {
		final XGlassPaneDlg xGlassPane = getInstance(parent);
		xGlassPane.type = "tips";
		xGlassPane.shadeClose = shadeClose;
		xGlassPane.tipsLabel.setText(msg);
		xGlassPane.setVisible(true);
		xGlassPane.tipsLabel.setVisible(true);
		xGlassPane.resize();
		
		if(timeout > 0) {
			new Thread() {
				public void run() {
					try {
						Thread.sleep(timeout);
					} catch (InterruptedException e) {
						//e.printStackTrace();
					}
					xGlassPane.close();
				}
			}.start();
		}
		
	}
	
	public void close() {
		setVisible(false);
		loadingLabelAni.setVisible(false);
		
		tipsLabel.setVisible(false);
	}
	public static void closePane(JDialog parent) {
		getInstance(parent).close();
	}
	public static void closePaneDelay(JDialog parent) {
		closePaneDelay(parent, null);
	}
	public static void closePaneDelay(JDialog parent, Runnable doRun) {
		new Thread() {
			public void run() {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					//e.printStackTrace();
				}
				getInstance(parent).close();
				if(doRun != null) {
					doRun.run();
				}
			}
		}.start();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(shadeClose) {
			close();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(shadeClose) {
			close();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}
}
