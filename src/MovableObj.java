public class MovableObj implements WorldObjects {
	private String type;
	private String name;
	private int xCoord;
	private int yCoord;
	private int direction;	//0 = N; 1 = S; 2 = E; 3 = W
	public MovableObj(String type, String name, int X, int Y) {
		this.type = type;
		this.name = name;
		this.xCoord = X;
		this.yCoord = Y;
		this.direction = 0; 		//default direction will be 0 (North)
	}
	
	//movable objects will be: birds, ants, grass-hoppers 
	
	//simple get and set methods 
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
	
	public void setX(int newX) {
		this.xCoord = newX;
	}
	public void setY(int newY) {
		this.yCoord = newY;
	}
	public int getDirection() {
		return direction;
	}
	public void setDirection(int d) {
		this.direction = d;
	}
}