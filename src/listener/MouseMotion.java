package listener;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import main.Game;
import main.GameState;
import main.Window;
import menu.MenuButton;

public class MouseMotion implements MouseMotionListener {
	
	private Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
	private Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
	private Game game;
	
	public MouseMotion(Game game) {
		this.game = game;
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(Game.state == GameState.Game) {
			int screenX = e.getX();
			int screenY = e.getY();
			game.getHandler().getMap().setMousePoint(screenX, screenY);
			getMove(screenX, screenY);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		int screenX = e.getX();
		int screenY = e.getY();
		Point mousePoint = new Point(screenX, screenY);
		boolean isSthHovered = false;
		if(Game.state == GameState.Game) {
			game.getHandler().getMap().setMousePoint(screenX, screenY);
			getMove(screenX, screenY);
		} else if(Game.state == GameState.Menu) {
			MenuButton[] buttons = game.getMenu().getButtons();
			for(MenuButton button : buttons) {
				if(button.getBounds().contains(mousePoint)) {
					button.setHover(true);
					isSthHovered = true;
				}
				else
					button.setHover(false);
			}
		} else if(Game.state == GameState.Pause) {
			MenuButton[] buttons = game.getMenu().getPauseButtons();
			for(MenuButton button : buttons) {
				if(button.getBounds().contains(mousePoint)) {
					button.setHover(true);
					isSthHovered = true;
				}
				else
					button.setHover(false);
			}
		} else if(Game.state == GameState.GameOver) {
			MenuButton[] buttons = game.getMenu().getGameOverButtons();
			for(MenuButton button : buttons) {
				if(button.getBounds().contains(mousePoint)) {
					button.setHover(true);
					isSthHovered = true;
				}
				else
					button.setHover(false);
			}
		}
		if(isSthHovered)
			Window.frame.setCursor(handCursor);
		else
			Window.frame.setCursor(defaultCursor);
	}
	
	private void getMove(int screenX, int screenY) {
		int playerX = (int)game.getHandler().getPlayer().getCenterX() + game.getHandler().getMap().getOfsmap().x + game.getHandler().getPlayer().getSize()/2;
		int playerY = (int)game.getHandler().getPlayer().getCenterY() + game.getHandler().getMap().getOfsmap().y + game.getHandler().getPlayer().getSize()/2;
	    int posX = screenX - playerX;
	    int posY = screenY - playerY;
	    float angle = (float) Math.toDegrees(Math.atan2(posY, posX));
	    if(angle < 0)
	        angle += 360;
	    game.getHandler().getPlayer().setAngle(angle);
	}

}
