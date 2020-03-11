package listener;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import main.Game;
import main.GameState;

public class KeyInput extends KeyAdapter {
	
	private boolean[] keyDown = new boolean[4];
	public static boolean isEPressed = false;
	private Game game;
	
	public KeyInput(Game game) {
		this.game = game;
		Arrays.fill(keyDown, false);
	}
	
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if(Game.state == GameState.Game) {
			if(key == KeyEvent.VK_W) {
				game.getHandler().getPlayer().setVelY(-1);
				keyDown[0] = true;
			}
			if(key == KeyEvent.VK_S) {
				game.getHandler().getPlayer().setVelY(1);
				keyDown[1] = true;
			}
			if(key == KeyEvent.VK_D) {
				game.getHandler().getPlayer().setVelX(1);
				keyDown[2] = true;
			}
			if(key == KeyEvent.VK_A) {
				game.getHandler().getPlayer().setVelX(-1);
				keyDown[3] = true;
			}
			if(key == KeyEvent.VK_R) {
				game.getHandler().getPlayer().getGun().reload();
			}
			if(key == KeyEvent.VK_H) {
				game.getHandler().getPlayer().heal();
			}
			if(key == KeyEvent.VK_G) {
				game.getHandler().getPlayer().throwGranade();
			}
			
			if(key == KeyEvent.VK_E) {
				isEPressed  = true;
			}
			
			if(key >= 48 && key <= 57) {
				key = key - 48;
				game.getHandler().getMap().changeFieldType(key);
			}
		}
		
		if(key == KeyEvent.VK_P) {
			if(Game.state == GameState.Pause)
				Game.state = GameState.Game;
			else if(Game.state == GameState.Game) {
				Game.state = GameState.Pause;
			}
		}
		
		if(key == KeyEvent.VK_ESCAPE) {
			if(Game.state == GameState.Pause)
				Game.state = GameState.Game;
			else if(Game.state == GameState.Game) {
				Game.state = GameState.Pause;
			}
			else
				game.exit();
			
		}
		
	}
	
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		if(Game.state == GameState.Game) {
			if(key == KeyEvent.VK_W) keyDown[0] = false;
			if(key == KeyEvent.VK_S) keyDown[1] = false;
			if(key == KeyEvent.VK_D) keyDown[2] = false;
			if(key == KeyEvent.VK_A) keyDown[3] = false;
			if(key == KeyEvent.VK_E) {
				isEPressed  = false;
			}
			
			if(!keyDown[0] && !keyDown[1])
				game.getHandler().getPlayer().setVelY(0);
			if(!keyDown[2] && !keyDown[3])
				game.getHandler().getPlayer().setVelX(0);
		}
	}
}
