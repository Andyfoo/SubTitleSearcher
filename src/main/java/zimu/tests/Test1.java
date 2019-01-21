package zimu.tests;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Test1 extends JPanel {

	private static final long serialVersionUID = 1L;
	/**
	 * A label displays status text and loading icon.
	 */
	private JLabel statusLabel = new JLabel("Reading data, please wait...");

	public Test1() {

		statusLabel.setHorizontalAlignment(JLabel.CENTER);

		// Must add a mouse listener, otherwise the event will not be
		// captured
		this.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(MouseEvent e) {
			}
		});

		this.setLayout(new BorderLayout());

		this.add(statusLabel);
		// Transparent
		setOpaque(false);
	}

	/**
	 * Set the text to be displayed on the glass pane.
	 * 
	 * @param text
	 */
	public void setStatusText(final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				statusLabel.setText(text);
			}
		});
	}

	/**
	 * Install this to the jframe as glass pane.
	 * 
	 * @param frame
	 */
	public void installAsGlassPane(JFrame frame) {
		frame.setGlassPane(this);
	}

	/**
	 * A small demo code of how to use this glasspane.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame("Test GlassPane");
		final Test1 glassPane = new Test1();
		glassPane.installAsGlassPane(frame);
		JButton button = new JButton("Test Query");
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// Call in new thread to allow the UI to update
				Thread th = new Thread() {
					public void run() {
						glassPane.setVisible(true);
						glassPane.setCursor(new Cursor(Cursor.WAIT_CURSOR));
						// TODO Long time operation here
//						try {
//							Thread.sleep(2000);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						glassPane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//						glassPane.setVisible(false);
					}
				};

				th.start();
			}
		});
		frame.getContentPane().setLayout(new FlowLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(button);
		frame.setSize(200, 200);
		frame.setVisible(true);
	}

}