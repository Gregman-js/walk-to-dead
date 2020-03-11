package menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import loader.HighScoreLoader;
import loader.ImageLoader;
import main.Game;
import main.Window;
import net.User;

public class MyMenu {
	
	private MenuButton[] buttons;
	private String[] buttonsTitles = {"Play Singleplayer", "Connect as client", "Enable Server", "Quit"};
	public MenuButton username;
	
	private MenuButton[] pauseButtons;
	private String[] pauseButtonsTitles = {"Resume", "Menu", "Quit"};
	
	private MenuButton[] gameOverButtons;
	private String[] gameOverButtonsTitles = {"Restart", "Menu", "Quit"};
	
	private static final String gameTitle = "Walk to Dead";
	private static final int top = Window.getWindowSize().height / 2 + 70;
	private BufferedImage walp;
	private static final int space = 10;
	private static final int width = 300;
	private static final int height = 40;
	private Font font;
	private Game game;
	
	public MyMenu(Game game) {
		this.game = game;
		InputStream file = HighScoreLoader.class.getResourceAsStream("/minecraftfont.otf");
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, file).deriveFont(40f);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		 ge.registerFont(font);
		walp = ImageLoader.loadImage("/gamewalp.jpg");
		buttons = new MenuButton[buttonsTitles.length];
		for(int i = 0; i < buttonsTitles.length; i++)
			buttons[i] = new MenuButton(buttonsTitles[i], i, top, space, width, height);
		username = new MenuButton("Username: " + game.getUserName(), Window.getWindowSize().height - 130, 20, width, height);
		
		pauseButtons = new MenuButton[pauseButtonsTitles.length];
		for(int i = 0; i < pauseButtonsTitles.length; i++)
			pauseButtons[i] = new MenuButton(pauseButtonsTitles[i], i, top, space, width, height);
		
		gameOverButtons = new MenuButton[gameOverButtonsTitles.length];
		for(int i = 0; i < gameOverButtonsTitles.length; i++)
			gameOverButtons[i] = new MenuButton(gameOverButtonsTitles[i], i, top, space, width, height);
		
	}

	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g.drawImage(walp, 0, 0, Window.getWindowSize().width, Window.getWindowSize().height, null);
		g.setFont(font);
		g.setFont(g.getFont().deriveFont(70f));
		FontMetrics fontMetrics = g.getFontMetrics();
		g.setColor(Color.white);
		g.drawString(gameTitle, Window.getWindowSize().width/2 - fontMetrics.stringWidth(gameTitle)/2, 200);
		g.setFont(g.getFont().deriveFont(30f));
		for(int i = 0; i < buttons.length; i++)
			buttons[i].render(g);
		g.setFont(g.getFont().deriveFont(25f));
		if(game.getServer().running)
			g.setColor(Color.green);
		else
			g.setColor(Color.red);
		g.drawString("Server " + (game.getServer().running ? "ON" : "OFF"), 20, Window.getWindowSize().height - 20);
		
		if(game.getClient().connected)
			g.setColor(Color.green);
		else
			g.setColor(Color.red);
		g.drawString("Client connection " + (game.getClient().connected ? "ON" : "OFF"), 20, Window.getWindowSize().height - 50);
		g.setColor(Color.white);
		if(game.getUserName() != null && !game.getUserName().isEmpty())
			username.render(g);
		if(game.getServer().running) {
			int left = (int)(Window.getWindowSize().width - 270);
			Rectangle table = new Rectangle(left, (int)(280), 250, 330);
			g2d.draw(table);
			g.drawString("Users", (int)(Window.getWindowSize().width - 180), 310);
			g.drawLine(left, 320, left + 250, 320);
			for(int i = 0; i < game.getServer().getUsers().size(); i++) {
				User user = game.getServer().getUsers().get(i);
				g.drawString(user.getUsername(), left + 10, 350 + i*30);
			}
		}
	}
	
	public void renderPause(Graphics g) {
//		g.drawImage(walp, 0, 0, Window.getWindowSize().width, Window.getWindowSize().height, null);
		g.setColor(new Color(0,0,0,150));
		g.fillRect(0, 0, Window.getWindowSize().width, Window.getWindowSize().height);
		g.setFont(font);
		g.setFont(g.getFont().deriveFont(70f));
		FontMetrics fontMetrics = g.getFontMetrics();
		g.setColor(Color.white);
		g.drawString(gameTitle, Window.getWindowSize().width/2 - fontMetrics.stringWidth(gameTitle)/2, 200);
		g.setFont(g.getFont().deriveFont(40f));
		for(int i = 0; i < pauseButtons.length; i++)
			pauseButtons[i].render(g);
	}

	public MenuButton[] getButtons() {
		MenuButton[] bt = new MenuButton[buttons.length + 1];
		System.arraycopy( buttons, 0, bt, 0, buttons.length );
		bt[buttons.length] = username;
		return bt;
	}
	
	public MenuButton getButtonByName(String name) {
		for(MenuButton bt : buttons) {
			if(bt.getTitle() == name)
				return bt;
		}
		return null;
	}
	
	public MenuButton[] getPauseButtons() {
		return pauseButtons;
	}

	public void renderGameOver(Graphics g) {
		g.setColor(new Color(0,0,0,150));
		g.fillRect(0, 0, Window.getWindowSize().width, Window.getWindowSize().height);
		g.setFont(font);
		g.setFont(g.getFont().deriveFont(70f));
		FontMetrics fontMetrics = g.getFontMetrics();
		g.setColor(Color.white);
		g.drawString(gameTitle, Window.getWindowSize().width/2 - fontMetrics.stringWidth(gameTitle)/2, 160);
		g.setFont(g.getFont().deriveFont(40f));
		for(int i = 0; i < gameOverButtons.length; i++)
			gameOverButtons[i].render(g);
		
		renderScore(g);
	}

	private void renderScore(Graphics g) {
		int width = 300;
		int height = 170;
		Rectangle scoreTable = new Rectangle(60, 200, width, height);
		Graphics2D g2d = (Graphics2D)g;
		g2d.draw(scoreTable);
		g.setFont(g.getFont().deriveFont(32f));
		FontMetrics fontMetrics = g.getFontMetrics();
		String text = "Score Board";
		g.drawString(text, scoreTable.x + width/2 - fontMetrics.stringWidth(text)/2, scoreTable.y + 40);
		int lineDown = 60;
		g.drawLine(scoreTable.x, scoreTable.y + lineDown, scoreTable.x + width, scoreTable.y + lineDown);
		
		g.setFont(g.getFont().deriveFont(25f));
		fontMetrics = g.getFontMetrics();
		g.drawString("Level", scoreTable.x + 25, scoreTable.y + lineDown + 40);
		g.drawString("Killed", scoreTable.x + 180, scoreTable.y + lineDown + 40);
		lineDown = 100;
		g.drawString(Game.getLevel() + "", scoreTable.x + 80 - fontMetrics.stringWidth(Game.getLevel() + ""), scoreTable.y + lineDown + 40);
		g.drawString(game.getHandler().getPlayer().getKill() + "", scoreTable.x + 240 - fontMetrics.stringWidth(game.getHandler().getPlayer().getKill() + ""), scoreTable.y + lineDown + 40);
		
		renderHighScore(g);
	}

	private void renderHighScore(Graphics g) {
		int width = 300;
		int height = 170;
		Rectangle scoreTable = new Rectangle(60, 400, width, height);
		Graphics2D g2d = (Graphics2D)g;
		g2d.draw(scoreTable);
		g.setFont(g.getFont().deriveFont(32f));
		FontMetrics fontMetrics = g.getFontMetrics();
		String text = "Record";
		g.drawString(text, scoreTable.x + width/2 - fontMetrics.stringWidth(text)/2, scoreTable.y + 40);
		int lineDown = 60;
		g.drawLine(scoreTable.x, scoreTable.y + lineDown, scoreTable.x + width, scoreTable.y + lineDown);
		
		g.setFont(g.getFont().deriveFont(25f));
		fontMetrics = g.getFontMetrics();
		g.drawString("Level", scoreTable.x + 25, scoreTable.y + lineDown + 40);
		g.drawString("Killed", scoreTable.x + 180, scoreTable.y + lineDown + 40);
		lineDown = 100;
		g.drawString(Game.getHighScore().level + "", scoreTable.x + 80 - fontMetrics.stringWidth(Game.getHighScore().level + ""), scoreTable.y + lineDown + 40);
		g.drawString(Game.getHighScore().killed + "", scoreTable.x + 240 - fontMetrics.stringWidth(Game.getHighScore().killed + ""), scoreTable.y + lineDown + 40);
	}

	public MenuButton[] getGameOverButtons() {
		return gameOverButtons;
	}

}
