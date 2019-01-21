package zimu.tests;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;


public class Test2 extends JComponent {
	private static final long serialVersionUID = 1L;
	JLabel loadingLabel;
	public static void main(String[] args) {
		JFrame frame = new JFrame("test");
		frame.setSize(800, 500);
		//frame.setBackground(Color.LIGHT_GRAY);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Test2 glasspane = new Test2(frame);
		//frame.setGlassPane(glasspane);
		frame.setContentPane(glasspane);
		glasspane.setVisible(true);
	}
	public Test2(JFrame frame) {
		setLayout(null);
		setBounds(0,0,frame.getWidth(), frame.getHeight());
		setOpaque(false);
		setBackground(new Color(0, 0, 0, 30));
		loadingLabel = new JLabel("请稍候......");
		loadingLabel.setForeground(Color.BLUE);
		loadingLabel.setOpaque(true);
		loadingLabel.setBackground(new Color(0, 0, 0, 30));
		loadingLabel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
		loadingLabel.setBounds(100, 200, 110, 110);
		
		add(loadingLabel);


	}
}
