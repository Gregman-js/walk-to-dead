package loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;

public class HighScoreLoader implements Serializable {
	
	private static final long serialVersionUID = -7291470023491810235L;
	
	private static String fileName = "/highScore.txt";
	private static Score ax;
	
	public static void generateToFile() {
		ax = new Score();
		ax.level = 2;
		ax.killed = 5;
		ax.username = "";
		URL resourceUrl = HighScoreLoader.class.getResource(fileName);
		File file = null;
		try {
			file = new File(resourceUrl.toURI());
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try(FileOutputStream f = new FileOutputStream(file);
		    ObjectOutput s = new ObjectOutputStream(f)) {
		    s.writeObject(ax);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveScoreToFile(String username, int l, int k) {
		Score score = readFromFile();
		int level = l > score.level ? l : score.level;
		int kill = k > score.killed ? k : score.killed;
		ax = new Score(username, level, kill);
		URL resourceUrl = HighScoreLoader.class.getResource(fileName);
		File file = null;
		try {
			file = new File(resourceUrl.toURI());
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try(FileOutputStream f = new FileOutputStream(file);
		    ObjectOutput s = new ObjectOutputStream(f)) {
		    s.writeObject(ax);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Score readFromFile() {
		Score xd = null;
		URL resourceUrl = HighScoreLoader.class.getResource(fileName);
		File file = null;
		try {
			file = new File(resourceUrl.toURI());
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try(FileInputStream in = new FileInputStream(file);
			    ObjectInputStream s = new ObjectInputStream(in)) {
			    try {
					xd = (Score) s.readObject();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		return xd;
	}
}
