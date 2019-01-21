package zimu.gui;

import java.awt.Font;

import javax.swing.UIManager;

public class GuiConfig {
	public static Font defaultFont = new Font("微软雅黑", Font.PLAIN, 12);
	public static void setUIFont() {
		
		String names[] = { "Label", "CheckBox", "PopupMenu", "MenuItem", "CheckBoxMenuItem", "JRadioButtonMenuItem", "ComboBox", "Button", "Tree", "ScrollPane", "TabbedPane", "EditorPane", "TitledBorder", "Menu",
				"TextArea", "OptionPane", "MenuBar", "ToolBar", "ToggleButton", "ToolTip", "ProgressBar", "TableHeader", "Panel", "List", "ColorChooser", "PasswordField", "TextField", "Table", "Label",
				"Viewport", "RadioButtonMenuItem", "RadioButton", "DesktopPane", "InternalFrame"};
		for (String item : names) {
			UIManager.put(item + ".font", defaultFont);
		}
	}
}
