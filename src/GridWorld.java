import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GridWorld{
	
	private ImmovableObj[][] immovableArray;	//array of immovable objects, xy coordinates
	private MovableObj[][] movableArray;		//array of movable objects. xy coordinates (not robots)
	private ArrayList<MovableObj> movList = new ArrayList<MovableObj>();
	private Robot[][] robotArray;				//array of robots 
	private Drone[][] droneArray;				//array of drones
	private ArrayList<Drone> droneList = new ArrayList<Drone>();	 
	private JLabel[][] LabelArray;				//array of the labels
	private int[][] worldArray;					//array of world grid: 1 for immovable object; 2 for movable object; 3 for robot
	private int width;
	private int height;

	public GridWorld(int w, int h) {  //constructor 
		this.width = w;
		this.height = h;
		this.immovableArray = new ImmovableObj[width][height];
		this.movableArray = new MovableObj[width][height];
		this.robotArray = new Robot[width][height];
		this.droneArray = new Drone[width][height];
		this.LabelArray = new JLabel[width][height];
		this.worldArray = new int[width][height];
	}
	
	//get and set methods
	
	public int getWidth() {
		return this.width;
	}
	public int getHeight() {
		return this.height;
	}
	public Robot[][] getRobots(){
		return this.robotArray;
	}
	public ImmovableObj[][] getImObj(){
		return this.immovableArray;
	}
	public MovableObj[][] getMovObjs(){
		return this.movableArray;
	}
	public Drone[][] getDrones() {
		return this.droneArray;
	}
	public int[][] getWorld(){
		return this.worldArray;
	}
	public ImmovableObj getImObject(int x, int y) {
		return this.immovableArray[x][y];
	}
	public MovableObj getMovObj(int x, int y) {
		return this.movableArray[x][y];
	}
	public ArrayList<MovableObj> getMovList() {
		return movList;
	}
	public Robot getRobot(int x, int y) {
		return this.robotArray[x][y];
	}
	public Drone getDrone(int x, int y) {
		return this.droneArray[x][y];
	}
	public ArrayList<Drone> getDroneList() {
		return droneList;
	}
	public void setMove(int oldx, int oldy, int x, int y, MovableObj m) {
		this.worldArray[x][y] = 2;
		this.worldArray[oldx][oldy] = 0;
		this.movableArray[oldx][oldy] = null;
		this.movableArray[x][y] = m;
	}
	
	//methods to add the various objects to the world 

	public void addImmovObj(int x, int y, int xTransl, int yTransl, ImmovableObj imObj) {
		System.out.println(immovableArray[x][y]);
		if (worldArray[x][y] == 0 && immovableArray[x][y] == null) {
			for(int i = 0; i <= xTransl; i++) {
				for(int j = 0; j <= yTransl; j++) {
					if(worldArray[x+i][y+j] == 0 && immovableArray[x+i][y+j] == null) {
						immovableArray[x+i][y+j] = imObj;
						worldArray[x+i][y+j] = 1;
					}
					else {
						System.out.println("Error adding immovable object translation");
						break;
					}
				}
			}
		}
		else
			System.out.println("At" + x + " , " + y +"is already an object");
	}
	
	public void addRobot(int x, int y, Robot rob) {
		//double direction;
		if (worldArray[x][y] == 0 && robotArray[x][y] == null) {
			worldArray[x][y] = 3;
			robotArray[x][y] = rob;
			//direction = rob.randDirection();
		}
		else {
			System.out.println("Could not add robot. Square already has object");
			//direction = -1.0;
		}
		//return direction;
	}
	public void addDrone(int x, int y, Drone drone) {
		//double direction;
		if (worldArray[x][y] == 0 && droneArray[x][y] == null) {
			worldArray[x][y] = 3;
			droneArray[x][y] = drone;
			droneList.add(drone);
			//direction = rob.randDirection();
		}
		else {
			System.out.println("Could not add robot. Square already has object");
			//direction = -1.0;
		}
		//return direction;
	}
	
	public void addMovObj(int x, int y, MovableObj movObj) {
		if (worldArray[x][y] == 0 && movableArray[x][y] == null) {
			movableArray[x][y] = movObj;
			worldArray[x][y] = 2;
			movList.add(movObj);
		}
		else 
			System.out.println("Error adding movable object");
	}
	
	public void updateWorld(Robot robot, int x, int y, int oldX, int oldY) {
		worldArray[x][y] = 3;
		robotArray[x][y] = robot;
		
		worldArray[oldX][oldY] = 0;
		robotArray[oldX][oldY] = null;
	}
	
	//main function displays the world 
	public void createWorld(boolean firstTime) {	//if the first time the world is being displayed	
		JFrame mainFrame = new JFrame("The World");
		mainFrame.setSize(1000,1000);
		mainFrame.setLayout(new GridLayout(1, 1));									//Initializing the window
		
		JPanel grid = new JPanel();													//Initializing the Jpanel
		grid.setLayout(new GridLayout(width, height));														//and placing it in the window
		mainFrame.add(grid);
		
		if(firstTime == true) {
			for(int i = 0; i < this.worldArray.length; i++) {
				for(int j = 0; j < this.worldArray[0].length; j++) {
					JLabel current = new JLabel("", SwingConstants.CENTER);
					if(worldArray[i][j] == 1) {
						if(immovableArray[i][j].getType().equals("river")) {
							current.setBackground(Color.cyan);
						}
						if(immovableArray[i][j].getType().equals("rock")) {
							current.setBackground(Color.GRAY);
						}
						if(immovableArray[i][j].getType().equals("grass")) {
							current.setBackground(Color.green);
						}
						current.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
						current.setOpaque(true);	
						current.setText(this.immovableArray[i][j].getName());
					}
					else if (worldArray[i][j] == 2) {
						if(movableArray[i][j].getType().equals("bird")) {
							current.setBackground(Color.YELLOW);
						}
						if(movableArray[i][j].getType().equals("grasshopper")) {
							current.setBackground(Color.darkGray);
						}
						current.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
						current.setOpaque(true);	
						current.setText(this.movableArray[i][j].getName());
					}
					else if(worldArray[i][j] == 3) {
						if(droneArray[i][j].getMistake() == false) {
							current.setBackground(Color.pink);
							current.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
							current.setOpaque(true);	
							current.setText(this.droneArray[i][j].getName());
						}
						else {
							current.setBackground(Color.red);
							current.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
							current.setOpaque(true);	
							current.setText(this.droneArray[i][j].getName());
						}
					}
					else if(worldArray[i][j] == 0) {
						current.setBackground(Color.white);
						current.setOpaque(true);
						current.setBorder(BorderFactory.createLineBorder(Color.BLACK));
					}
					this.LabelArray[i][j] = current;
					grid.add(this.LabelArray[i][j]);
				}
			}
			mainFrame.setVisible(true);	
		}
		else {	//world already initialized 
			for(int i = 0; i < this.worldArray.length; i++) {
				for(int j = 0; j < this.worldArray[0].length; j++) {
					if(worldArray[i][j] == 3) {
						if(droneArray[i][j].getMistake() == false) {
							this.LabelArray[i][j].setBackground(Color.pink);
							this.LabelArray[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
							this.LabelArray[i][j].setOpaque(true);	
							this.LabelArray[i][j].setText(this.droneArray[i][j].getName());
						}
						else {
							this.LabelArray[i][j].setBackground(Color.red);
							this.LabelArray[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
							this.LabelArray[i][j].setOpaque(true);	
							this.LabelArray[i][j].setText(this.droneArray[i][j].getName());
						}
					}
					else if(worldArray[i][j] == 0) {
						this.LabelArray[i][j].setBackground(Color.white);
						this.LabelArray[i][j].setOpaque(true);
						this.LabelArray[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
					}
					else if(worldArray[i][j] == 2) {
						if(movableArray[i][j].getType().equals("bird")) {
							this.LabelArray[i][j].setBackground(Color.YELLOW);
							this.LabelArray[i][j].setOpaque(true);
							this.LabelArray[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
							this.LabelArray[i][j].setText(this.movableArray[i][j].getName());
						}
						else if(movableArray[i][j].getType().equals("grasshopper")) {
							this.LabelArray[i][j].setBackground(Color.ORANGE);
							this.LabelArray[i][j].setOpaque(true);
							this.LabelArray[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
							this.LabelArray[i][j].setText(this.movableArray[i][j].getName());
						}
					}
					//this.LabelArray[i][j] = this.LabelArray[i][j];
					//grid.add(this.LabelArray[i][j]);
				}
			}
		}
	}
}