package zimu.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.github.stuxuhai.jpinyin.ChineseHelper;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import zimu.AppConfig;
import zimu.util.MyFileUtil;
import zimu.util.StringUtil;
import zimu.util.WinRarUtil;
import zimu.util.gui.XGlassPaneDlg;

public class ExtractDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	static final Log logger = LogFactory.get();

	String title;

	JCheckBox simplifiedCheckBox;
	JButton downButton;
	JTable subTable;

	JScrollPane subTableScrollPane;
	Vector<Vector<Object>> subTableData = new Vector<Vector<Object>>();
	Vector<String> subTableColumn = new Vector<String>();
	DefaultTableModel subTableModel = new DefaultTableModel(subTableData, subTableColumn);
	
	SubTitlePopupMenu subTitlePopupMenu;

	byte[] archiveData;
	String archiveExt;
	String subFilename;
	
	String archivePath;
	List<File> archiveFiles;

	JSONObject dataMap = new JSONObject(true);
	int no = 0;
	
	
	public ExtractDialog(Frame parent, String title, String ext,String filename, byte[] data) {
		super(parent, true);
		
		this.title = title;
		if (data == null) {
			data = new byte[0];
		}
		this.archiveData = data;
		this.archiveExt = ext;
		this.subFilename = filename;
		initComponents();
		initData();
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
		archivePath = AppConfig.appPath + "tmpData/" + DateTime.now().toString("yyyyMMddHHmmss") ;
		archiveFiles = WinRarUtil.unRar(archiveExt, archiveData, archivePath);
		if(archiveFiles == null) {
			logger.error("解压失败");
			XGlassPaneDlg.tips(this, "压缩文件解压失败");
			return;
		}
		for (int i = 0; i < archiveFiles.size(); i++) {
			File file = archiveFiles.get(i);
			String title = file.getName();
			String key = title;
			String size = StringUtil.getPrintSize(file.length());
			if(ArrayUtil.contains(AppConfig.subExtNames, StringUtil.extName(file).toLowerCase())){
				addRow(key, title, size, JSONUtil.parseObj(file));
			}
		}
		
		downButton.setEnabled(false);
		if(dataMap.size() > 0) {
			Set<String> keySet = dataMap.keySet();
			for(String key : keySet) {
				JSONObject row = dataMap.getJSONObject(key);
				Vector<Object> vRow = new Vector<Object>();
				vRow.add(row.get("key"));
				vRow.add(row.get("no"));
				vRow.add(row.get("title"));
				vRow.add(row.get("size"));
				vRow.add("UTF-8");
				subTableData.add(vRow);
			}
			//downButton.setEnabled(true);
			subTable.updateUI();
			
		}else {
			subTable.updateUI();
			return;
		}
	}
	private void saveFile(int index, String key, String charset, boolean simplified) {
		for (int i = 0; i < archiveFiles.size(); i++) {
			File file = archiveFiles.get(i);
			String title = file.getName();
			if(key.equals(title)) {
				String filename = subFilename+".chn"+index+"."+StringUtil.extName(file);
				logger.info("save="+filename);
				//if(MainWin.simplifiedCheckBox != null && MainWin.simplifiedCheckBox.isSelected()) {
				if(simplified || simplifiedCheckBox.isSelected()) {
					String dataStr = null;
					try {
						dataStr = new String(MyFileUtil.fileReadBin(file), charset);
						dataStr = ChineseHelper.convertToSimplifiedChinese(dataStr);
						MyFileUtil.fileWrite(filename, dataStr, "UTF-8", "");
					} catch (Exception e) {
						logger.error(e);
					}
				}else {
					MyFileUtil.fileWriteBin(filename, MyFileUtil.fileReadBin(file));
				}
			}
		}
	}
	

	/**
	 * 获取行数据
	 * @param key
	 * @return
	 */
	public Vector<Object> getSubTableData(String key) {
		for(int i = 0; i < subTableData.size(); i++) {
			Vector<Object> colList = subTableData.get(i);
			if(key.equals(colList.get(0))) {
				return colList;
			}
		}
		return null;
	}
	/**
	 * 清除临时目录
	 */
	private void clear() {
		try {
			WinRarUtil.clear(archivePath);
		}catch(Exception e) {
			logger.error(e);
		}
	}
	private void initComponents() {
		ExtractDialog _this = this;
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		//setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
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
		
		JPanel titlePanel = new JPanel();
		titlePanel.setOpaque(true);
		titlePanel.setBackground(new Color(205, 205, 205));
		add(titlePanel, BorderLayout.NORTH);
		JLabel alertLabel = new JLabel(String.format("<html>%s<span style=\"color:blue\">(%s, %s)</span></html>", title, archiveExt, StringUtil.getPrintSize(archiveData.length)));
		// alertLabel.setForeground(Color.BLUE);
		alertLabel.setSize(100, 0);
		alertLabel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
		titlePanel.add(alertLabel);

		JPanel btnsPanel = new JPanel();
		add(btnsPanel, BorderLayout.SOUTH);

		
		simplifiedCheckBox = new JCheckBox("繁体转简体", MainWin.simplifiedCheckBox!=null&&MainWin.simplifiedCheckBox.isSelected());
		simplifiedCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		simplifiedCheckBox.setFocusPainted(false);
		simplifiedCheckBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				JCheckBox jcb = (JCheckBox) e.getItem();
				TableColumnModel tableColumnModel = subTable.getColumnModel();
				TableColumn tc = tableColumnModel.getColumn(4);
				TableColumn headerTc = subTable.getTableHeader().getColumnModel().getColumn(4);
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
		// 去掉按钮文字周围的焦点框
		downButton.setFocusPainted(false);
		
		
		downButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveSelected(null, false);
			}

		});
		btnsPanel.add(downButton);
		
		JButton closeButton = new JButton("关闭窗口");
		closeButton.setFocusPainted(false);
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_this.clear();
				_this.dispose();
			}

		});
		btnsPanel.add(closeButton);
		
		JLabel spaceLabel = new JLabel("　");
		spaceLabel.setLayout(null);
		spaceLabel.setPreferredSize(new Dimension(100, 20));
		//spaceLabel.setOpaque(true);
		//spaceLabel.setBackground(Color.black);
		btnsPanel.add(spaceLabel);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBackground(new Color(200, 200, 200));
		subTable = new JTable() {
			private static final long serialVersionUID = 1L;

			// 设置JTable为只读
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		subTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(e.getValueIsAdjusting()) {
					//System.out.println("true");
					return;
				}
			
				int[] selectedRow = subTable.getSelectedRows();

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
		subTableColumn.add("大小");
		subTableColumn.add("字幕编码");
		subTable.setModel(subTableModel);
		TableColumnModel tableColumnModel = subTable.getColumnModel();
		// tableColumnModel.removeColumn(tableColumnModel.getColumn(0));
		// subTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableColumnModel.getColumn(0).setMinWidth(0);
		tableColumnModel.getColumn(0).setMaxWidth(0);
		tableColumnModel.getColumn(1).setMinWidth(50);
		tableColumnModel.getColumn(1).setMaxWidth(50);
		// tableColumnModel.getColumn(2).setWidth(650);
		tableColumnModel.getColumn(3).setMinWidth(100);
		tableColumnModel.getColumn(3).setMaxWidth(100);
		
		if(simplifiedCheckBox.isSelected()) {
			tableColumnModel.getColumn(4).setMinWidth(70);
			tableColumnModel.getColumn(4).setMaxWidth(70);
		}else {

			tableColumnModel.getColumn(4).setMinWidth(0);
			tableColumnModel.getColumn(4).setMaxWidth(0);
		}

		DefaultTableCellRenderer r = new DefaultTableCellRenderer();
		r.setHorizontalAlignment(JTextField.CENTER);
		tableColumnModel.getColumn(0).setResizable(false);
		tableColumnModel.getColumn(1).setCellRenderer(r);
		tableColumnModel.getColumn(3).setCellRenderer(r);
		tableColumnModel.getColumn(4).setCellRenderer(r);
		subTable.setRowHeight(24);

		Dimension headerSize = subTable.getTableHeader().getPreferredSize();
		headerSize.height = 26;
		subTable.getTableHeader().setPreferredSize(headerSize);
		subTable.getTableHeader().setReorderingAllowed(false);
		subTableScrollPane = new JScrollPane(subTable);
		subTableScrollPane.getViewport().setBackground(Color.WHITE);

		mainPanel.add(subTableScrollPane, BorderLayout.CENTER);

		add(mainPanel, BorderLayout.CENTER);
		
		initPopupMenu();
	}
	/**
	 * 初始弹出菜单
	 */
	public void initPopupMenu() {
		subTitlePopupMenu = new SubTitlePopupMenu(subTable);
		subTitlePopupMenu.addEvent("download", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveSelected(null, false);
			}
		});
		subTitlePopupMenu.addEvent("download_simplified_utf8", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveSelected("UTF-8", true);
			}
		});
		subTitlePopupMenu.addEvent("download_simplified_gbk", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveSelected("GBK", true);
			}
		});
		subTitlePopupMenu.addEvent("download_simplified_big5", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveSelected("Big5", true);
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

	public void setSelectedCharset(String charset) {
		int[] selectedRow = subTable.getSelectedRows();
		for (int i = 0; i < selectedRow.length; i++) {
			subTable.setValueAt(charset, selectedRow[i], 4);
		}
	}
	public void saveSelected(String _charset, boolean simplified) {
		ExtractDialog _this = this;
		int[] selectedRow = subTable.getSelectedRows();
		XGlassPaneDlg.loading(_this);
		
		new Thread() {
			public void run() {
				for (int i = 0; i < selectedRow.length; i++) {
					String key = (String) subTable.getValueAt(selectedRow[i], 0);
					String charset = (String) subTable.getValueAt(selectedRow[i], 4);
					if(_charset != null) {
						charset = _charset;
					}
					saveFile(i, key, charset, simplified);
				}
				XGlassPaneDlg.closePane(_this);
				XGlassPaneDlg.tips(_this, "下载完毕");
			}
		}.start();
	}
	
	public void addRow(String key, String title, String size, JSONObject data) {
		JSONObject dataRow = new JSONObject();
		dataRow.put("key", key);
		dataRow.put("title", title);
		dataRow.put("size", size);
		dataRow.put("data", data);

		dataRow.put("no", ++no);

		dataMap.put(key, dataRow);
	}

	public static void main(String args[]) {
		GuiConfig.setUIFont();
		//String filename = "E:/workspace/_me/dev/my_libs/test_lib/data/file/archive/test.7z";
		
		String ext = "rar";
		String filename = "H:/_tmp/MOV/[zmk.tw]Downsizing.2017.1080p.BluRay.x264-GECKOS."+ext;
		//filename = "E:/workspace/_me/dev/my_tools/SubTitleSearcher/target/test.rar";
		
		byte[] data = MyFileUtil.fileReadBin(filename);

		final JFrame frame = new JFrame("test");
		frame.setSize(900, 500);
		// frame.setBackground(Color.LIGHT_GRAY);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		new ExtractDialog(frame, "asdfffffff", ext,"aaa", data).setVisible(true);
		System.exit(0);
	}

}