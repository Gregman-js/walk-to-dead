package main;

import java.awt.Canvas;
import java.awt.Dimension;

import javax.swing.JFrame;

import listener.WListener;

public class Window extends Canvas {

	private static final long serialVersionUID = -1478604005915452565L;
	
	private static Dimension windowSize;
	public static JFrame frame;
	
	public Window(int width, int height, String title, Game game) {
		frame = new JFrame(title);
		frame.setPreferredSize(new Dimension(width, height));
		frame.setMaximumSize(new Dimension(width, height));
		frame.setMinimumSize(new Dimension(width, height));
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.add(game);
		frame.addWindowListener(new WListener(game));
		frame.setVisible(true);
		
		windowSize = frame.getContentPane().getSize();
	}
	
	public static Dimension getWindowSize() {
		return windowSize;
	}

	public static boolean isInWindow(float x, float y, float size) {
		if(x + size < 0 || 
				x > Window.getWindowSize().width ||
				y + size < 0 || 
				y > Window.getWindowSize().height)
			return true;
		else
			return false;
	}
	
}
