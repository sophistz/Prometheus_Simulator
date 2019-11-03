public class ImmovableObj implements WorldObjects {
	private String type;
	private String name;
	private int xCoord;
	private int yCoord;
	private int xTrans;
	private int yTrans;
	public ImmovableObj() {
		
	}
	public ImmovableObj(String type, String name, int X, int Y, int xTrans, int yTrans) {
		this.type = type;
		this.name = name;
		this.xCoord = X;
		this.yCoord = Y;
		this.xTrans = xTrans;
		this.yTrans = yTrans;
	}
	public String getType() {
		return type;
	}
	public String getName() {
		return name;
	}
	public int getX() {
		return xCoord;
	}
	public int getY() {
		return yCoord;
	}
	public int offsetX() {
		return xTrans;
	}
	public int offsetY() {
		return yTrans;
	}
}