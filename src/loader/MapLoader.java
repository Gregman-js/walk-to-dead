package loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;

import map.Field;
import map.FieldType;

public class MapLoader implements Serializable {
	
	private static final long serialVersionUID = -7291470023491810235L;
	
	private static String fileName = "/tilemap.txt";
	private static FieldType[][][] ax;
	
	public static void generateToFile() {
		ax = new FieldType[2][100][100];
		for(int l = 0; l < 2; l++)
			for(int i = 0; i < 100; i++)
				for(int j = 0; j < 100; j++) {
					ax[l][i][j] = FieldType.Grass;
				}
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
	
	public static void saveMapToFile(Field[][][] field) {
		ax = new FieldType[2][100][100];
		for(int l = 0; l < 2; l++)
			for(int i = 0; i < 100; i++)
				for(int j = 0; j < 100; j++) {
					if(field[l][i][j] != null)
						ax[l][i][j] = field[l][i][j].getType();
					else
						ax[l][i][j] = null;
				}
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
	
	public static FieldType[][][] readFromFile() {
		FieldType[][][] xd = null;
		InputStream fil = HighScoreLoader.class.getResourceAsStream(fileName);
		try(ObjectInputStream s = new ObjectInputStream(fil)) {
			    try {
					xd = (FieldType[][][]) s.readObject();
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
