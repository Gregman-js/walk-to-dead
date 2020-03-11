package listener;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;

import main.Game;
import main.GameState;
import main.Window;
import menu.MenuButton;

public class Mouse extends MouseAdapter {
	
	private Game game;
	
	public Mouse(Game game) {
		this.game = game;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int screenX = e.getX();
		int screenY = e.getY();
		Point mousePoint = new Point(screenX, screenY);
		if(Game.state == GameState.Menu) {
			MenuButton[] buttons = game.getMenu().getButtons();
			for(MenuButton button : buttons) {
				if(button.getBounds().contains(mousePoint)) {
					if(button.getTitle() == "Play Singleplayer") {
						game.disconnectAll();
						Game.state = GameState.Game;
					}
					else if(button.getTitle() == "Start game") {
						game.startMultiServer();
					}
					else if(button.getTitle() == "Enable Server") {
						game.setServerEnabled(true);
						button.setTitle("Disable Server");
						MenuButton cl = game.getMenu().getButtonByName("Disconnect");
						if(cl != null)
							cl.setTitle("Connect as client");
					}
					else if(button.getTitle() == "Disable Server") {
						game.setServerEnabled(false);
						button.setTitle("Enable Server");
					}
					else if(button.getTitle() == "Connect as client") {
						game.setClientEnabled(true);
						if(game.getClient().running)
							button.setTitle("Disconnect");
						MenuButton sr = game.getMenu().getButtonByName("Disable Server");
						if(sr != null)
							sr.setTitle("Enable Server");
					}
					else if(button.getTitle() == "Disconnect") {
						game.setClientEnabled(false);
						button.setTitle("Connect as client");
					}
					else if(button.getTitle() == "Quit") {
						game.exit();
					}
					else if(button.getTitle().contains("Username:")) {
						game.setUserName(JOptionPane.showInputDialog(Window.frame, "Enter your username?"));
						button.setTitle("Username: " + game.getUserName());
					}
				}
			}
		} else if(Game.state == GameState.Pause) {
			MenuButton[] buttons = game.getMenu().getPauseButtons();
			for(MenuButton button : buttons) {
				if(button.getBounds().contains(mousePoint)) {
					if(button.getTitle() == "Resume") {
						Game.state = GameState.Game;
					}
					else if(button.getTitle() == "Menu") {
						game.saveScore();
						game.restartGame(GameState.Menu);
						
					}
					else if(button.getTitle() == "Quit") {
						game.exit();
					}
				}
			}
		} else if(Game.state == GameState.GameOver) {
			MenuButton[] buttons = game.getMenu().getGameOverButtons();
			for(MenuButton button : buttons) {
				if(button.getBounds().contains(mousePoint)) {
					if(button.getTitle() == "Restart") {
						if(game.getClient().running)
							game.restartClientGame();
						else if(game.getServer().running)
							game.restartServerGame();
						else
							game.restartGame();
					}
					else if(button.getTitle() == "Menu") {
						game.restartGame(GameState.Menu);
						
					}
					else if(button.getTitle() == "Quit") {
						game.exit();
					}
				}
			}
		}

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(Game.state == GameState.Game) {
			if(game.getHandler().getPlayer().getGun() != null)
				game.getHandler().getPlayer().getGun().setFire(true);
		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(game.getHandler().getPlayer().getGun() != null)
			game.getHandler().getPlayer().getGun().setFire(false);

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
