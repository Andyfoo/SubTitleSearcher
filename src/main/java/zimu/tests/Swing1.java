package zimu.tests;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

//import java.awt.List;
/**
 * Description: <br/>
 * 网站: <a href="http://www.crazyit.org">疯狂Java联盟</a> <br/>
 * Copyright (C), 2001-2012, Yeeku.H.Lee <br/>
 * This program is protected by copyright laws. <br/>
 * Program Name: <br/>
 * Date:
 * 
 * @author Yeeku.H.Lee kongyeeku@163.com
 * @version 1.0
 */
public class Swing1 {
	JFrame f = new JFrame("测试");
	// 定义一个按钮,并为之指定图标
	Icon okIcon = new ImageIcon("ico/ok.png");
	JButton ok = new JButton("确认", okIcon);
	// 定义一个单选按钮，初始处于选中状态
	JRadioButton male = new JRadioButton("男", true);
	// 定义一个单选按钮，初始处于没有选中状态
	JRadioButton female = new JRadioButton("女", false);
	// 定义一个ButtonGroup，用于将上面两个JRadioButton组合在一起
	ButtonGroup bg = new ButtonGroup();
	// 定义一个复选框，初始处于没有选中状态。
	JCheckBox married = new JCheckBox("是否已婚？", false);
	String[] colors = new String[] { "红色", "绿色", "蓝色" };
	// 定义一个下拉选择框
	// JComboBox<String> colorChooser = new JComboBox<>(colors);
	JComboBox<Object> colorChooser = new JComboBox<Object>(colors);
	// 定义一个列表选择框
	// JList<String> colorList = new JList<>(colors);
	JList<Object> colorList = new JList<Object>(colors);
	// 定义一个8行、20列的多行文本域
	JTextArea ta = new JTextArea(8, 20);
	// 定义一个40列的单行文本域
	JTextField name = new JTextField(40);
	JMenuBar mb = new JMenuBar();
	JMenu file = new JMenu("文件");
	JMenu edit = new JMenu("编辑");
	// 创建“新建”菜单项，并为之指定图标
	Icon newIcon = new ImageIcon("ico/new.png");
	JMenuItem newItem = new JMenuItem("新建", newIcon);
	// 创建“保存”菜单项，并为之指定图标
	Icon saveIcon = new ImageIcon("ico/save.png");
	JMenuItem saveItem = new JMenuItem("保存", saveIcon);
	// 创建“退出”菜单项，并为之指定图标
	Icon exitIcon = new ImageIcon("ico/exit.png");
	JMenuItem exitItem = new JMenuItem("退出", exitIcon);
	JCheckBoxMenuItem autoWrap = new JCheckBoxMenuItem("自动换行");
	// 创建“复制”菜单项，并为之指定图标
	JMenuItem copyItem = new JMenuItem("复制", new ImageIcon("ico/copy.png"));
	// 创建“粘贴”菜单项，并为之指定图标
	JMenuItem pasteItem = new JMenuItem("粘贴", new ImageIcon("ico/paste.png"));
	JMenu format = new JMenu("格式");
	JMenuItem commentItem = new JMenuItem("注释");
	JMenuItem cancelItem = new JMenuItem("取消注释");
	// 定义一个右键菜单用于设置程序风格
	JPopupMenu pop = new JPopupMenu();
	// 用于组合三个风格菜单项的ButtonGroup
	ButtonGroup flavorGroup = new ButtonGroup();
	// 创建五个单选框按钮，用于设定程序的外观风格
	JRadioButtonMenuItem metalItem = new JRadioButtonMenuItem("Metal风格", true);
	JRadioButtonMenuItem nimbusItem = new JRadioButtonMenuItem("Nimbus风格");
	JRadioButtonMenuItem windowsItem = new JRadioButtonMenuItem("Windows风格");
	JRadioButtonMenuItem classicItem = new JRadioButtonMenuItem("Windows经典风格");
	JRadioButtonMenuItem motifItem = new JRadioButtonMenuItem("Motif风格");

	// -----------------用于执行界面初始化的init方法---------------------
	public void init() {
		// 创建一个装载了文本框、按钮的JPanel
		JPanel bottom = new JPanel();
		bottom.add(name);
		bottom.add(ok);
		f.add(bottom, BorderLayout.SOUTH);
		// 创建一个装载了下拉选择框、三个JCheckBox的JPanel
		JPanel checkPanel = new JPanel();
		checkPanel.add(colorChooser);
		bg.add(male);
		bg.add(female);
		checkPanel.add(male);
		checkPanel.add(female);
		checkPanel.add(married);
		// 创建一个垂直排列组件的Box，盛装多行文本域JPanel
		Box topLeft = Box.createVerticalBox();
		// 使用JScrollPane作为普通组件的JViewPort
		JScrollPane taJsp = new JScrollPane(ta); // ⑤
		topLeft.add(taJsp);
		topLeft.add(checkPanel);
		// 创建一个水平排列组件的Box，盛装topLeft、colorList
		Box top = Box.createHorizontalBox();
		top.add(topLeft);
		top.add(colorList);
		// 将top Box容器添加到窗口的中间
		f.add(top);
		// -----------下面开始组合菜单、并为菜单添加监听器----------
		// 为newItem设置快捷键，设置快捷键时要使用大写字母
		newItem.setAccelerator(KeyStroke.getKeyStroke('N', InputEvent.CTRL_MASK)); // ①
		newItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ta.append("用户单击了“新建”菜单\n");
			}
		});
		// 为file菜单添加菜单项
		file.add(newItem);
		file.add(saveItem);
		file.add(exitItem);
		// 为edit菜单添加菜单项
		edit.add(autoWrap);
		// 使用addSeparator方法来添加菜单分隔线
		edit.addSeparator();
		edit.add(copyItem);
		edit.add(pasteItem);
		// 为commentItem组件添加提示信息
		commentItem.setToolTipText("将程序代码注释起来！");
		// 为format菜单添加菜单项
		format.add(commentItem);
		format.add(cancelItem);
		// 使用添加new JMenuItem("-")的方式不能添加菜单分隔符
		edit.add(new JMenuItem("-"));
		// 将format菜单组合到edit菜单中，从而形成二级菜单
		edit.add(format);
		// 将file、edit菜单添加到mb菜单条中
		mb.add(file);
		mb.add(edit);
		// 为f窗口设置菜单条
		f.setJMenuBar(mb);
		// -----------下面开始组合右键菜单、并安装右键菜单----------
		flavorGroup.add(metalItem);
		flavorGroup.add(nimbusItem);
		flavorGroup.add(windowsItem);
		flavorGroup.add(classicItem);
		flavorGroup.add(motifItem);
		pop.add(metalItem);
		pop.add(nimbusItem);
		pop.add(windowsItem);
		pop.add(classicItem);
		pop.add(motifItem);
		// 为5个风格菜单创建事件监听器
		ActionListener flavorListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (e.getActionCommand().equals("Metal风格")) {
						changeFlavor(1);
					}
					if (e.getActionCommand().equals("Nimbus风格")) {
						changeFlavor(2);
					}
					if (e.getActionCommand().equals("Windows风格")) {
						changeFlavor(3);
					}
					if (e.getActionCommand().equals("Windows经典风格")) {
						changeFlavor(4);
					}
					if (e.getActionCommand().equals("Motif风格")) {
						changeFlavor(5);
					}
				} catch (Exception ee) {
					ee.printStackTrace();
				}
			}
		};
		// 为五个风格菜单项添加事件监听器
		metalItem.addActionListener(flavorListener);
		nimbusItem.addActionListener(flavorListener);
		windowsItem.addActionListener(flavorListener);
		classicItem.addActionListener(flavorListener);
		motifItem.addActionListener(flavorListener);
		// 调用该方法即可设置右键菜单，无须使用事件机制
		ta.setComponentPopupMenu(pop); // ④
		// 设置关闭窗口时，退出程序
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setVisible(true);
	}

	// 定义一个方法，用于改变界面风格
	private void changeFlavor(int flavor) throws Exception {
		switch (flavor) {
		// 设置Metal风格
		case 1:
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			break;
		// 设置Nimbus风格
		case 2:
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			break;
		// 设置Windows风格
		case 3:
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			break;
		// 设置Windows经典风格
		case 4:
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
			break;
		// 设置Motif风格
		case 5:
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			break;
		}
		// 更新f窗口内顶级容器以及内部所有组件的UI
		SwingUtilities.updateComponentTreeUI(f.getContentPane()); // ②
		// 更新mb菜单条以及内部所有组件的UI
		SwingUtilities.updateComponentTreeUI(mb);
		// 更新pop右键菜单以及内部所有组件的UI
		SwingUtilities.updateComponentTreeUI(pop);
	}

	public static void main(String[] args) {
		// 设置Swing窗口使用Java风格
		// JFrame.setDefaultLookAndFeelDecorated(true); //③
		new Swing1().init();
	}
}