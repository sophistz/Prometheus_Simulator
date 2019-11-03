public class Robot implements WorldObjects {
	private String name;
	private int xCoord;
	private int yCoord;
	private double direction = 0.0;
	private boolean mistake = false;
	
	public Robot() {
		
	}
	public Robot(String name, int X, int Y, boolean mistake) { //constructor
		this.name = name;
		this.xCoord = X;
		this.yCoord = Y;
		this.direction = 0.0;
		this.mistake = mistake;
	}
	
	public String getType() {
		return "robot";
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
	public boolean getMistake() {
		return this.mistake;
	}
	public double getDirection() {
		return this.direction;
	}
	public void setMistake(boolean m) {
		this.mistake = m;
	}
	public void setDirection(double direction) {
		this.direction = direction;
	}
	public double randDirection() {			//random direction step generator 
		double d = Math.random();
		return d;
	}
}