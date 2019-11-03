// Purpose: This defines the robot avatar.  This is an abstract class used by simulator.  There is another class
//          named Robot that contains the robot information.  The avatar has the AI reference.
// Creator: Chris O'Connor and Doris Zhou
// Note   : The Perceptron is embedded in this class
//
// To Do List
// ==========
// Commenting and code reformatting							Joseph		Done
// PerceptronMatrix should be private						TBD			To Do
// Maybe PerceptronMatrix should be within class Robot?	TBD			To Do
// Maybe error in addSensor(), see NOTE						TBD			To Do

public class Drone {
	public PerceptronMatrix perceptronMatrix;	// The NN used to determine the motion of the avatar
	private String name;						// Drone name
	private int xCoord;							// Drone's x-coordinate
	private int yCoord;							// Drone's y-coordinate
	private double direction;					// The direction the drone is facing
	private GridWorld world;					// Call-back reference to the world
	private Basic_Sensor[] sensors;				// On-board sensors
	private Action[] actions;					// Legal actions
	private boolean mistake;					// Error flag

	// Purpose: Constructor
	// Note   : Creating robot first and then adding AI later
	public Drone(String name, Action[] actions, Basic_Sensor[] sensors, GridWorld world, int x, int y, int direction, boolean mistake) {

		this.name      = name;
		this.actions   = actions;
		this.sensors   = sensors;
		this.world     = world;
		this.xCoord    = x;
		this.yCoord    = y;
		this.direction = direction;
		this.mistake   = mistake;

		Robot drone = new Robot(this.name, xCoord, yCoord, false);
		drone.setDirection(direction);

		//this.world.addRobot(xCoord, yCoord, drone);
		this.perceptronMatrix = new PerceptronMatrix(this.sensors, this.actions);
	}

	//setter and getter methods
	public String getName() {
		return this.name;
	}
	
	public int getX() {
		return this.xCoord;
	}
	
	public int getY() {
		return this.yCoord;
	}
	
	public double getDirection() {
		return this.direction;
	}
	
	public boolean getMistake() {
		return this.mistake;
	}
	
	public void setMistake(boolean m) {
		this.mistake = m;
	}
	
	public void setX(int x) {
		this.xCoord = x;
	}
	
	public void setY(int y) {
		this.yCoord = y;
	}
	
	public void setDirection(double dir) {
		this.direction = dir;
	}

	// Purpose: trains the drone's perceptron matrix
	public void trainDrone(double[][] training_data) {
		this.perceptronMatrix.learnAllWeights(training_data);
	}

	// Purpose: Append a new sensor and add it to the perceptronMatrix
	public void addSensor(Basic_Sensor sensor, double[][] training_data) {
		// Step 1. Create a new this.sensors local list with one additional sensor appended
		// NOTE: This is a local array, why are we doing this?
		Basic_Sensor[] sensor_append = new Basic_Sensor[this.sensors.length+1];
		int i = 0;

		for(Basic_Sensor s : this.sensors) {
			sensor_append[i] = s;
			i++;
		}
		sensor_append[sensor_append.length-1] = sensor;
		// this.sensors = sensor_append; <---------------------------------------------------

		// Step 2. Add the new sensor to the perceptronMatrix
		this.perceptronMatrix.addSensor(sensor, training_data);
	}

	// Purpose: Append a new action and add it to the perceptronMatrix
	public void addAction(Action action, double[][] training_data) {
		// Step 1. Create a new this.action local list with one additional action appended
		// NOTE: This is a local array, why are we doing this?
		Action[] action_append = new Action[this.actions.length+1];
		int i = 0;

		for(Action a : this.actions) {
			action_append[i] = a;
			i++;
		}
		action_append[action_append.length-1] = action;
		this.actions = action_append;

		// Step 2. Add the new action to the perceptronMatrix
		this.perceptronMatrix.addAction(action, training_data);
	}
	
	// Purpose: To get the x and y coordinates based on drone's current orientation for one sensor
	public int[][] getVisible(Basic_Sensor sensor) {
		int [][] visible = new int[this.sensors.length][2];		// 2 by 2 matrix
		int i = 0;

		for (int x : sensor.getX()) {
			for(int y : sensor.getY()) {
				if (this.direction < 0.25) {	//facing north
					visible[i][0] = this.xCoord + x;
					visible[i][1] = this.yCoord - y;
				}
				else if(this.direction < 0.5) {	//facing east
					visible[i][0] = this.xCoord + y;
					visible[i][1] = this.yCoord + x;
				}
				else if (this.direction < 0.75) {	//facing south
					visible[i][0] = this.xCoord - x;
					visible[i][1] = this.yCoord + y;
				}
				else if(this.direction < 1.0) {	//facing west
					visible[i][0] = this.xCoord - y;
					visible[i][1] = this.yCoord - x;
				}
			}
			i++;
		}

		return visible;
	}
	
	// Purpose: To get all the coordinates for the list of sensors
	public int[][] getAllVisible() {
		int [][] visible = new int[this.sensors.length][2];		// 2 by 2 matrix
		int i = 0;

		for (Basic_Sensor sensor : this.sensors) {
			for(int x : sensor.getX()) {
				for(int y : sensor.getY()) {
					if (this.direction < 0.25) {	//facing north
						visible[i][0] = this.xCoord + x;
						visible[i][1] = this.yCoord - y;
					}
					else if(this.direction < 0.5) {	//facing east
						visible[i][0] = this.xCoord + y;
						visible[i][1] = this.yCoord + x;
					}
					else if (this.direction < 0.75) {	//facing south
						visible[i][0] = this.xCoord - x;
						visible[i][1] = this.yCoord + y;
					}
					else if(this.direction < 1.0) {	//facing west
						visible[i][0] = this.xCoord - y;
						visible[i][1] = this.yCoord - x;
					}
				}
			}
			i++;
		}

		return visible;		
	}

	// Purpose: The Drone makes a move using the perceptronMatrix
	// Notes  :
	//    Uses visible 2d array as inputs for perceptron matrix.
	//    Matrix then makes a decision and outputs an action for the drone, which
	//    the drone takes. If the drone is indecisive, will throw an exception
	public void makeDecision() throws indecisiveException {
		int i = 0;
		double[] inputs = new double[this.sensors.length];

		for(Basic_Sensor sensor : this.sensors) {
			int[][] visible = getVisible(sensor);
			inputs[i] = sensor.score(world, visible);
			i++;
		}

		this.perceptronMatrix.setInputs(inputs);

		try {
			Action decision = this.perceptronMatrix.makeDecision();
			takeAction(decision);
		}
		catch(indecisiveException e) {
			throw e;
		}
	}
	
	// Purpose: Performs the action and updates drone's position and orientation
	private void takeAction(Action action) {
		// Step 1. If drone is not facing north, then have to translate what dx and dy means
		if (this.direction < 0.25) {
			this.xCoord += action.getXDelta();
			this.yCoord -= action.getYDelta();
		}
		else if(this.direction < 0.5) {
			this.yCoord += action.getXDelta();
			this.xCoord += action.getYDelta();
		}
		else if(this.direction < 0.75) {
			this.xCoord -= action.getXDelta();
			this.yCoord += action.getYDelta();
		}
		else if(this.direction < 0.75) {
			this.yCoord -= action.getXDelta();
			this.xCoord -= action.getYDelta();
		}

		// Step 2. Do the action
		this.direction += action.getDirectionDelta();
		int tmp = Math.floorMod((int) (100*this.direction), 100);
		this.direction = tmp/100.0;
	}

}
