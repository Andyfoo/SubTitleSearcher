package zimu.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import zimu.AppConfig;

public class AboutDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	public AboutDialog(Frame parent) {
		super(parent, true);
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
		setVisible(false);
	}
	private void initComponents() {

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setIconImage(MainWin.icon);
		setSize(500, 300);
		setResizable(false);

		setLocationRelativeTo(this.getParent());
		setTitle("About " + AppConfig.appName);

		JPanel mainPanel = new JPanel(new BorderLayout());
		add(mainPanel);

		JPanel leftPanel = new JPanel();
		mainPanel.add(leftPanel, BorderLayout.WEST);

		ImageIcon iconImg = new ImageIcon(MainWin.icon);
		iconImg.setImage(iconImg.getImage().getScaledInstance((int) (iconImg.getIconWidth() * 0.5), (int) (iconImg.getIconHeight() * 0.5), Image.SCALE_SMOOTH));
		JLabel iconLabel = new JLabel(iconImg);
		iconLabel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
		leftPanel.add(iconLabel);

		HyperlinkListener hlLsnr = new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() != HyperlinkEvent.EventType.ACTIVATED)
					return;
				// 超链接标记中必须带有协议指定，e.getURL()才能得到，否则只能用e.getDescription()得到href的内容。
				// JOptionPane.showMessageDialog(InfoDialog.this, "URL:"+e.getURL()+"\nDesc:"+ e.getDescription());
				URL linkUrl = e.getURL();
				if (linkUrl != null) {
					try {
						Desktop.getDesktop().browse(linkUrl.toURI());
					} catch (Exception e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(AboutDialog.this, "超链接错误", "无法打开超链接:" + linkUrl + "\n详情:" + e1, JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(AboutDialog.this, "超链接错误", "超链接信息不完整:" + e.getDescription() + "\n请确保链接带有协议信息，如http://,mailto:", JOptionPane.ERROR_MESSAGE);
				}
			}
		};

		JTextPane infoArea = new JTextPane();
		//设置css单位(px/pt)和chrome一致
		infoArea.putClientProperty(JTextPane.W3C_LENGTH_UNITS, true);
		infoArea.addHyperlinkListener(hlLsnr);
		infoArea.setContentType("text/html");
		infoArea.setText(getInfo());
		infoArea.setEditable(false);
		infoArea.setBorder(BorderFactory.createEmptyBorder(2, 10, 6, 10));
		infoArea.setFocusable(false);

		JScrollPane infoAreaScrollPane = new JScrollPane(infoArea);
		infoAreaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		infoAreaScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		infoAreaScrollPane.getViewport().setBackground(Color.WHITE);
		mainPanel.add(infoAreaScrollPane, BorderLayout.CENTER);

	}


	String getInfo() {
		String fontfamily = GuiConfig.defaultFont.getFamily();
		String htmlFont = "font-size:12px;font-family:"+fontfamily+";";
		StringBuffer sb = new StringBuffer();
		sb.append("<pre style=\""+htmlFont+"\">");
		sb.append("软件名称：" + AppConfig.appName + "\n");
		sb.append("软件版本：" + AppConfig.appVer + "\n");
		sb.append("软件网址：<a href=\"https://github.com/Andyfoo/SubTitleSearcher\">github.com/Andyfoo/SubTitleSearcher</a>");
		sb.append("</pre>");

		sb.append("<div style=\"color:red;"+htmlFont+"\">");
		sb.append("<strong style=\"font-size:14px\">免责声明：</strong>");
		sb.append("<ol style=\"color:red;"+htmlFont+";padding:0;margin:0;margin-left:15px\">\n");
		sb.append("<li>本软件仅供个人学习和研究之用，不得用于商业或者非法用途。</li>\n");
		sb.append("<li>下载后会覆盖原字幕，请谨慎操作。</li>\n");
		sb.append("<li>所有字幕均来源于互联网，请使用者自行承担风险。</li>\n");
		sb.append("<li>作者仅提供学习和研究工具，不对其内容的准确性、可靠性、正当性、安全性、合法性等负责,亦不承担任何法律责任。</li>\n");
		sb.append("<ol>\n");
		sb.append("</div>");
		return sb.toString();
		// return "<html><a href=\"sda\">asdfas</a></html>";
	}

	public static void main(String args[]) {
		final JFrame frame = new JFrame("test");
		frame.setSize(800, 500);
		// frame.setBackground(Color.LIGHT_GRAY);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JButton btn = new JButton("test");
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new AboutDialog(frame).setVisible(true);
			}

		});
		frame.add(btn);
		new AboutDialog(frame).setVisible(true);

	}

}