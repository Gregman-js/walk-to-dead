package map;

import java.awt.Color;
import java.awt.Rectangle;

public class Field {
	
	private int x, y, fieldSize;
	private FieldType type;
	
	public Field(int i, int j, int fieldSize, FieldType type) {
		this.x = i;
		this.y = j;
		this.fieldSize = fieldSize;
		this.type = type;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public FieldType getType() {
		return type;
	}
	
	public void setType(FieldType type) {
		this.type = type;
	}
	
	public int getFieldSize() {
		return fieldSize;
	}
	
	public Color getColor() {
		return null;
	}
	
	public Rectangle getBounds() {
		return new Rectangle(x, y, fieldSize, fieldSize);
	}
	
	public Rectangle getOffsetBounds(int offsetX, int offsetY) {
		return new Rectangle(x + offsetX, y + offsetY, fieldSize, fieldSize);
	}

}
