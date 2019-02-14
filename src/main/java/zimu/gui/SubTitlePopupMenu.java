package zimu.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import zimu.common.ZiMuCommon;

public class SubTitlePopupMenu {
	JPopupMenu popupMenu;
	Map<String, ActionListener> actionListenerMap = new HashMap<String, ActionListener>();
	JTable jTable;
	public boolean simplified = false;
	
	String titleStr = "";
	String titleCnStr = "";
	
	public SubTitlePopupMenu(JTable jTable) {
		popupMenu = new JPopupMenu();
		this.jTable = jTable;
		setJTable();
		addDefaultEvent();
	}

	public void createPopupMenu() {
		if(simplified) {
			add("编码：UTF-8", "charset_utf8");
			add("编码：GBK", "charset_gbk");
			add("编码：ISO-8859-1", "charset_iso_8859_1");
			add("编码：Big5", "charset_big5");
			popupMenu.addSeparator();
		}
		add("下载字幕", "download");
		popupMenu.addSeparator();
		add("下载字幕(UTF-8 繁转简)", "download_simplified_utf8");
		add("下载字幕(GBK 繁转简)", "download_simplified_gbk");
		add("下载字幕(Big5 繁转简)", "download_simplified_big5");
		popupMenu.addSeparator();
		if(titleStr!=null && titleStr.length() > 0) {
			add("复制文字："+titleStr, "copy_title");
		}
		if(titleCnStr!=null && titleCnStr.length() > 0) {
			add("复制文字："+titleCnStr, "copy_title_cn");
		}
		
		//popupMenu.addSeparator();
		//add("当前目录改名为xxxx", "rename_title");
		//add("当前目录改名为xxxx", "rename_title_cn");

	}
	
	public void addDefaultEvent() {
		addEvent("copy_title", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ZiMuCommon.copyClipboard(titleStr);
			}
		});
		addEvent("copy_title_cn", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ZiMuCommon.copyClipboard(titleCnStr);
			}
		});
	}

	public void setJTable() {
		jTable.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				// 鼠标右键点击事件

				// 判断是否为鼠标的BUTTON3按钮，BUTTON3为鼠标右键
				if (evt.getButton() == java.awt.event.MouseEvent.BUTTON3) {
					// 通过点击位置找到点击为表格中的行
					int focusedRowIndex = jTable.rowAtPoint(evt.getPoint());
					if (focusedRowIndex == -1) {
						return;
					}
//					int focusedColIndex = jTable.columnAtPoint(evt.getPoint());
//					if (focusedColIndex != 3) {
//						return;
//					}
					// 将表格所选项设为当前右键点击的行
					jTable.setRowSelectionInterval(focusedRowIndex, focusedRowIndex);
					int selRow = jTable.getSelectedRow();
					if(selRow > -1) {
						String title = (String) jTable.getValueAt(selRow, 2);
						if(title.contains(".")) {
							title = title.substring(0, title.lastIndexOf("."));
						}
						if(title.contains("下载次数")) {
							title = title.replaceAll("\\[[^]]*]\\[[^]]*]\\[下载次数.+]", "");
						}
						titleStr = title;
						titleCnStr = ZiMuCommon.getTitleCnStr(title);
					}
					popupMenu.removeAll();
					createPopupMenu();
					
					// 弹出菜单
					popupMenu.show(jTable, evt.getX(), evt.getY());
				}

			}
		});

	}

	public void add(String title, String actionListenerKey) {
		JMenuItem item = new JMenuItem();
		//item.setHorizontalAlignment(SwingConstants.CENTER);
		item.setBorder(BorderFactory.createEmptyBorder(3, 15, 3, 6));
		item.setText(title);
		if(actionListenerMap.containsKey(actionListenerKey)) {
			item.addActionListener(actionListenerMap.get(actionListenerKey));
		}
		popupMenu.add(item);
	}
	public void addEvent(String actionListenerKey, ActionListener actionListener) {
		actionListenerMap.put(actionListenerKey, actionListener);
	}
}
