package zimu.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import zimu.AppConfig;
import zimu.down.DownZIMu;
import zimu.util.gui.XGlassPane;

public class MainWin {
	static final Log logger = LogFactory.get();
	public static Image icon = Toolkit.getDefaultToolkit().getImage(MainWin.class.getResource("/res/icon/app.png")); 
	public static JFrame frame;
	public static JPanel filePathPanel;
	public static JTextField filePathText;
	public static JButton searchButton;
	public static JButton downButton;
	public static JButton fileButton;
	public static JLabel aboutButton;
	public static JCheckBox simplifiedCheckBox;
	public static JCheckBox sheshouCheckBox;
	public static JCheckBox xunleiCheckBox;
	public static JCheckBox zimukuCheckBox;
	
	
	
	
	public static JTable subTable;
	public static JScrollPane subTableScrollPane;
	public static Vector<Vector<Object>> subTableData = new Vector<Vector<Object>>();
	public static Vector<String> subTableColumn = new Vector<String>();
	public static DefaultTableModel subTableModel = new DefaultTableModel(subTableData, subTableColumn);
	
	public static SubTitlePopupMenu subTitlePopupMenu;
	
	public static JLabel statusLabel;
	
	public static String movFilename;
	public static String lastSelPath  = null ;

	public static void init() {
		GuiConfig.setUIFont();
		frame = new JFrame(String.format("%s V%s", AppConfig.appTitle, AppConfig.appVer));
		frame.setSize(920, 600);
		frame.setMinimumSize(new Dimension(800, 500));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		//frame.setAlwaysOnTop(true);
		frame.setLayout(new BorderLayout());
		
		
		
		frame.setIconImage(icon);
		// frame.setResizable(false);

		// Toolkit tool = frame.getToolkit();
		// frame.setIconImage(tool.getImage(""));

		// frame.getContentPane( ).add(label1);

		JPanel topPanel = new JPanel(new BorderLayout());
		//topPanel.setLayout(new GridLayout(2, 1, 5, 5));
		
		//topPanel.setBackground(new Color(100, 200, 200));
		frame.add(topPanel, BorderLayout.NORTH);
		
		

		//JLabel filePathLabel = new JLabel("请拖拽视频文件到这里：");
		filePathText = new JTextField(12);
		filePathText.setBackground(new Color(193, 193, 193));
		filePathText.setForeground(Color.BLACK);
		filePathText.setEditable(false);
		filePathText.setText("请拖拽视频文件到这里……");
		filePathText.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

		filePathPanel = new JPanel(new BorderLayout());
		filePathPanel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
		filePathPanel.setPreferredSize(new Dimension(0, 50));
		//filePathPanel.add(filePathLabel, BorderLayout.WEST);
		filePathPanel.add(filePathText, BorderLayout.CENTER);
		topPanel.add(filePathPanel, BorderLayout.NORTH);
		JPanel btnsPanel = new JPanel();
		
		fileButton = new JButton("选择文件");
		//去掉按钮文字周围的焦点框
		fileButton.setFocusPainted(false);
		fileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"视频文件(*.mkv; *mp4; *.mov; *.avi; *.ts)", "mkv", "mp4", "mov", "avi", "ts");
				jfc.setFileFilter(filter);
				if(lastSelPath!=null) {
					jfc.setCurrentDirectory(new File(lastSelPath));
				}
				
				
				jfc.showDialog(new JLabel(), "选择视频文件");
				File file = jfc.getSelectedFile();
				if (file != null && file.isFile()) {
					String filepath = file.getAbsolutePath();
					if(file.isDirectory()) {
						alert("请选择有效的视频文件");
						return;
					}
					setFile(filepath);
					startSearch();
				}
				
			}
			
		});
		btnsPanel.add(fileButton);
		
		
		
		sheshouCheckBox = new JCheckBox("射手", true);
		sheshouCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		sheshouCheckBox.setFocusPainted(false);
		btnsPanel.add(sheshouCheckBox);
		xunleiCheckBox = new JCheckBox("迅雷", true);
		xunleiCheckBox.setFocusPainted(false);
		btnsPanel.add(xunleiCheckBox);
		zimukuCheckBox = new JCheckBox("字幕库(较慢)", true);
		zimukuCheckBox.setFocusPainted(false);
		btnsPanel.add(zimukuCheckBox);
		
		searchButton = new JButton("搜索");
		//去掉按钮文字周围的焦点框
		searchButton.setFocusPainted(false);
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startSearch();
			}
			
		});
		btnsPanel.add(searchButton);
		

		
		simplifiedCheckBox = new JCheckBox("繁体转简体");
		simplifiedCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		simplifiedCheckBox.setFocusPainted(false);
		simplifiedCheckBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				JCheckBox jcb = (JCheckBox) e.getItem();
				TableColumnModel tableColumnModel = subTable.getColumnModel();
				TableColumn tc = tableColumnModel.getColumn(5);
				TableColumn headerTc = subTable.getTableHeader().getColumnModel().getColumn(5);
				if (jcb.isSelected()) {
					subTitlePopupMenu.simplified = true;
					tc.setWidth(70);
					tc.setMinWidth(70);
					tc.setMaxWidth(70);
					
					headerTc.setWidth(70);
					headerTc.setMinWidth(70);
					headerTc.setMaxWidth(70);
					
				} else {
					
					subTitlePopupMenu.simplified = false;
					tc.setWidth(0);
					tc.setMinWidth(0);
					tc.setMaxWidth(0);
					
					headerTc.setWidth(0);
					headerTc.setMinWidth(0);
					headerTc.setMaxWidth(0);
				}
				subTable.updateUI();
			}
			
		});
		btnsPanel.add(simplifiedCheckBox);
		downButton = new JButton("下载选中");
		//去掉按钮文字周围的焦点框
		downButton.setFocusPainted(false);
		downButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//System.out.println(subTable.getSelectedRows().length);

				downloadSelected(null, false);
			}
			
		});
		btnsPanel.add(downButton);

		
		ImageIcon aboutImg = new ImageIcon(MainWin.class.getResource("/res/img/about.png"));
		aboutButton = new JLabel(aboutImg);
		aboutButton.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));
		aboutButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				new AboutDialog(frame).setVisible(true);
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
			
		});
		btnsPanel.add(aboutButton);
		
		topPanel.add(btnsPanel, BorderLayout.SOUTH);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBackground(new Color(200, 200, 200));
		
		//Object[] columnTitle = { "序号", "名称", "语言", "类型","匹配度" };
		//Object[][] tableData = { new Object[] {1, "asdfdas.srt", "简体", "srt","0星"  }};//1, "asdfdas.srt", "简体", "srt","0星"
		
		//subTable = new JTable(tableData, columnTitle){
		subTable = new JTable(){
			private static final long serialVersionUID = 1L;
			//设置JTable为只读
			public boolean isCellEditable(int row, int column) {
				if(column == 5) {
					return true;
				}
				return false;
			}
			
		};
		initPopupMenu();
		
		
		subTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(e.getValueIsAdjusting()) {
					//System.out.println("true");
					return;
				}
				//System.out.println("false");
				// System.out.println(subTable.getSelectedRow());
				//String selectedData = null;

				int[] selectedRow = subTable.getSelectedRows();
//				int[] selectedColumns = subTable.getSelectedColumns();
//
//				for (int i = 0; i < selectedRow.length; i++) {
//					for (int j = 0; j < selectedColumns.length; j++) {
//						selectedData = (String) subTable.getValueAt(selectedRow[i], selectedColumns[j]);
//					}
//				}
//				System.out.println("Selected: " + selectedData);
				
				if(selectedRow.length>0) {
					downButton.setEnabled(true);
				}else {
					downButton.setEnabled(false);
				}
			}
			
			
		});

		subTableColumn.add("key");
		subTableColumn.add("序号");
		subTableColumn.add("名称");
		subTableColumn.add("评价");
		subTableColumn.add("来源");
		subTableColumn.add("字幕编码");
		subTable.setModel(subTableModel);
		TableColumnModel tableColumnModel = subTable.getColumnModel();
		//tableColumnModel.removeColumn(tableColumnModel.getColumn(0));
		//subTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableColumnModel.getColumn(0).setMinWidth(0);
		tableColumnModel.getColumn(0).setMaxWidth(0);
		tableColumnModel.getColumn(1).setMinWidth(50);
		tableColumnModel.getColumn(1).setMaxWidth(50);
		//tableColumnModel.getColumn(2).setWidth(650);

		tableColumnModel.getColumn(3).setMinWidth(90);
		tableColumnModel.getColumn(3).setMaxWidth(90);
		tableColumnModel.getColumn(4).setMinWidth(90);
		tableColumnModel.getColumn(4).setMaxWidth(90);
		//tableColumnModel.getColumn(5).setMinWidth(70);
		//tableColumnModel.getColumn(5).setMaxWidth(70);

		tableColumnModel.getColumn(5).setMinWidth(0);
		tableColumnModel.getColumn(5).setMaxWidth(0);
		

		DefaultTableCellRenderer r = new DefaultTableCellRenderer();   
		r.setHorizontalAlignment(JTextField.CENTER);
		tableColumnModel.getColumn(0).setResizable(false);
		tableColumnModel.getColumn(1).setCellRenderer(r);
		tableColumnModel.getColumn(3).setCellRenderer(r);
		tableColumnModel.getColumn(4).setCellRenderer(r);
		tableColumnModel.getColumn(5).setCellRenderer(r);
		   
//		Vector vRow = new Vector();
//		vRow.add("cell 0 0");
//		vRow.add("cell 0 1");
//		vRow.add("cell 0 1");
//		vRow.add("cell 0 1");
//		vRow.add("cell 0 1");
//		subTableData.add(vRow);
		
		subTable.setRowHeight(24);

		Dimension headerSize = subTable.getTableHeader().getPreferredSize();
		headerSize.height = 26;
		subTable.getTableHeader().setPreferredSize(headerSize);
		subTable.getTableHeader().setReorderingAllowed(false);
		subTableScrollPane = new JScrollPane(subTable);
		subTableScrollPane.getViewport().setBackground(Color.WHITE);

		mainPanel.add(subTableScrollPane, BorderLayout.CENTER);
		
		JPanel statusPanel = new JPanel(new BorderLayout());
		statusPanel.setOpaque(true);
		statusPanel.setBackground(new Color(205, 205, 205));
		mainPanel.add(statusPanel, BorderLayout.NORTH);
		
		statusLabel = new JLabel("未选择视频文件");
		//statusLabel.setBackground(new Color(242, 242, 242));
		statusLabel.setForeground(new Color(0, 0, 255));
		statusLabel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
		statusPanel.add(statusLabel, BorderLayout.WEST);
		
		JLabel alertLabel = new JLabel("下载后会覆盖原字幕，请谨慎操作");
		//alertLabel.setBackground(new Color(242, 242, 242));
		alertLabel.setForeground(Color.RED);
		alertLabel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
		statusPanel.add(alertLabel, BorderLayout.EAST);
		
		frame.add(mainPanel, BorderLayout.CENTER);
		
		
//		JPanel footerPanel = new JPanel(new BorderLayout());
//		footerPanel.setBackground(new Color(240, 240, 240));
//		//footerPanel.setSize(100, 80);
//		JLabel copyrightLabel = new JLabel("test");
//		copyrightLabel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
//		footerPanel.add(copyrightLabel, BorderLayout.WEST);
//		frame.add(footerPanel, BorderLayout.SOUTH);
		
		frame.setVisible(true);
		searchButton.setEnabled(false);
		downButton.setEnabled(false);
		initDragImport();
	}
	
	/**
	 * 初始弹出菜单
	 */
	public static void initPopupMenu() {
		subTitlePopupMenu = new SubTitlePopupMenu(subTable);
		subTitlePopupMenu.addEvent("download", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				downloadSelected(null, false);
			}
		});
		subTitlePopupMenu.addEvent("download_simplified_utf8", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				downloadSelected("UTF-8", true);
			}
		});
		subTitlePopupMenu.addEvent("download_simplified_gbk", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				downloadSelected("GBK", true);
			}
		});
		subTitlePopupMenu.addEvent("download_simplified_big5", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				downloadSelected("Big5", true);
			}
		});
		subTitlePopupMenu.addEvent("charset_utf8", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setSelectedCharset("UTF-8");
			}
		});
		subTitlePopupMenu.addEvent("charset_gbk", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setSelectedCharset("GBK");
			}
		});
		subTitlePopupMenu.addEvent("charset_iso_8859_1", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setSelectedCharset("ISO8859-1");
			}
		});
		subTitlePopupMenu.addEvent("charset_big5", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setSelectedCharset("Big5");
			}
		});
		subTitlePopupMenu.createPopupMenu();
	}
	
	public static void setSelectedCharset(String charset) {
		int[] selectedRow = subTable.getSelectedRows();
		for (int i = 0; i < selectedRow.length; i++) {
			subTable.setValueAt(charset, selectedRow[i], 5);
		}
	}
	
	/**
	 * 获取行数据
	 * @param key
	 * @return
	 */
	public static Vector<Object> getSubTableData(String key) {
		for(int i = 0; i < subTableData.size(); i++) {
			Vector<Object> colList = subTableData.get(i);
			if(key.equals(colList.get(0))) {
				return colList;
			}
		}
		return null;
	}
	
	public static void downloadSelected(String _charset, boolean simplified) {
		int[] selectedRow = subTable.getSelectedRows();
		XGlassPane.loading(MainWin.frame);
		new Thread() {
			public void run() {
				int successCount = 0;
				for (int i = 0; i < selectedRow.length; i++) {
					String key = (String) subTable.getValueAt(selectedRow[i], 0);
					String charset = (String) subTable.getValueAt(selectedRow[i], 5);
					if(_charset != null) {
						charset = _charset;
					}
					boolean downResult = DownZIMu.down(i,key, charset, simplified);
					if(downResult) {
						successCount++;
					}
				}
				XGlassPane.closePane(MainWin.frame);
				XGlassPane.tips(MainWin.frame, String.format("下载完毕，%s个成功，%s个失败", successCount, selectedRow.length-successCount), true);
			}
		}.start();
	}
	public static void startSearch() {
		DownZIMu.clear();
		XGlassPane.loading(MainWin.frame);
		MainWin.subTable.updateUI();
		new Thread() {
			public void run() {
				DownZIMu.searchList();
				XGlassPane.closePane(MainWin.frame);
			}
		}.start();
		
		

	}	
	public static void setFile(String filepath) {
		File file = new File(filepath);
		lastSelPath = file.getParentFile().getAbsolutePath();
		filePathText.setText(filepath);
		movFilename = filepath;
		logger.info(movFilename);
	}		
	public static void setStatusLabel(String str) {
		statusLabel.setText(str);
	}
	public static void alert(String str) {
		JOptionPane.showMessageDialog(MainWin.frame, str);
	}
	
	/**
	 * 设置拖拽文件
	 */
	public static void initDragImport() {
		TransferHandler transferHandler = new TransferHandler() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean importData(JComponent comp, Transferable t) {
				try {
					Object o = t.getTransferData(DataFlavor.javaFileListFlavor);

					String filepath = o.toString();
					if (filepath.startsWith("[")) {
						filepath = filepath.substring(1);
					}
					if (filepath.endsWith("]")) {
						filepath = filepath.substring(0, filepath.length() - 1);
					}
					File file = new File(filepath);
					if(file.isDirectory()) {
						alert("请选择有效的视频文件");
						return false;
					}
					setFile(filepath);
					startSearch();
					//JOptionPane.showMessageDialog(frame, filepath);
					return true;
				} catch (Exception e) {
					logger.error(e);
				}
				return false;
			}

			@Override
			public boolean canImport(JComponent comp, DataFlavor[] flavors) {
				for (int i = 0; i < flavors.length; i++) {
					if (DataFlavor.javaFileListFlavor.equals(flavors[i])) {
						return true;
					}
				}
				return false;
			}
		};
		filePathText.setTransferHandler(transferHandler);
		//frame.setTransferHandler(transferHandler);
		frame.getRootPane().setTransferHandler(transferHandler);
	}

	public static void main(String[] args) {
		init();
		movFilename = "H:/_tmp/MOV/downsizing.2017.720p.bluray.x264-geckos.mkv";
		startSearch();
		//JNotification.showNotification(frame,"title","message",JNotification.INFO_MESSAGE,10,JNotification.PARENT_CENTER,false);

		
	}

}
