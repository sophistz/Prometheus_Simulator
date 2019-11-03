import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

//import java.io.*;
//import java.awt.*;                //graphics library

// Purpose: Main program that launches the test simulator
// Creator: Chris O'Connor, Doris Zhou and Joseph Vybihal
// Started: January 2018
//
// To Do List
// ==========
// Build first iteration of code			Chris				Done
// Fix UI and basic operations				Doris				Done
// Command and reformat the code			Joseph				In process
//		Note: I believe the main simulation loop can be reorganized more efficiently
// Put it on github or bitbucket			Joseph				Done
// Improve simulator features				TBD					To do
// Improve simulator physics				TBD					To do
// Sonic sensor NN simulation				TBD					To do
// Touch sensor NN simulation				TBD					To do
// Battery sensor NN simulation				TBD					To do
// 2D vision NN simulation					TBD					To do

public class Main {

	// -------------------------------------------- main() ----------------------------------------------
	// Purpose:
	//    1. Asks for and load world config file
	//    2. Instantiate objects for simulation
	//    3. Start the simulator

	public static void main(String[] args){

		// Step 1: Ask for config filename

//		Scanner scan = new Scanner(System.in);
//		System.out.println("Enter the name of your config file: ");
//		String nameJSON = scan.next();
		String nameJSON = "testworld.json";
//		scan.close();
//		System.out.println(nameJSON);

		
		// Step 2: Parse JSON file and instantiate all objects
		
		JSONParser jsonParser = new JSONParser();
		
		try (FileReader reader = new FileReader(nameJSON))
		{
			Object obj = jsonParser.parse(reader);
			JSONObject worldObj = (JSONObject) obj;

			// Step 3: parseWorld() - instantiates all the objects
			// Step 4: simulator()  - runs the simulator given the objects

			simulator(parseWorld(worldObj));		//calling simulator
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		catch(ParseException e) {
			e.printStackTrace();
		}
	}

	// ------------------------------------------ parseWorld() -----------------------------------------------
	// Purpose: Creates the world and objects in that world.
	//		1. Map
	//		2. Immoveable objects
	//		3. Moveable objects
	//		4. Robots
	//
	// Returns: The GridWorld object that contains the world map

	private static GridWorld parseWorld(JSONObject world) 
	{
		JSONObject worldObject = (JSONObject) world.get("world");

		// Step 1. Read (and echo) the dimensions of the grid world
		
		long w = (long) worldObject.get("width");
		Integer width = (int) (long) w;
		System.out.println(width);
		
		long h = (long) worldObject.get("height");
		Integer height = (int) (long) h;
		System.out.println(height);

		// Step 2. Instantiate the world map
		
		GridWorld gWorld = new GridWorld(width, height);
		
		// Step 3. Get objects and instantiating non-autonomous objects

		JSONArray worldObjArray = (JSONArray) worldObject.get("objects");
		Iterator objItr         = worldObjArray.iterator();

		while (objItr.hasNext()) {
			JSONObject innerObj = (JSONObject) objItr.next();

			// Echo the object found

			System.out.println(innerObj.get("objType")+ " " + innerObj.get("objName") + " "+innerObj.get("objX") + " " 
					+ innerObj.get("objY")+" " + innerObj.get("xTransl") + " " + innerObj.get("yTransl"));

			// Get object parameters

			String type = (String) innerObj.get("objType");
			String name = (String) innerObj.get("objName");
			
			long tmpX = (long) innerObj.get("objX");
			Integer x = (int) (long) tmpX;
			
			long tmpY = (long) innerObj.get("objY");
			Integer y = (int) (long) tmpY;
			
			long tmpXT = (long) innerObj.get("xTransl");
			Integer xTransl = (int) (long) tmpXT;
			
			long tmpYT = (long) innerObj.get("yTransl");
			Integer yTransl = (int) (long) tmpYT;

			// Build the object

			if(type.equals("river")) {
				ImmovableObj imObj = new ImmovableObj(type, name, x, y, xTransl, yTransl);
				gWorld.addImmovObj(x, y, xTransl, yTransl, imObj);
			}
			else if(type.equals("rock")) {
				ImmovableObj imObj2 = new ImmovableObj(type, name, x, y, xTransl, yTransl);
				gWorld.addImmovObj(x, y, xTransl, yTransl, imObj2);
			}
			else if(type.equals("grass")) {
				ImmovableObj imObj2 = new ImmovableObj(type, name, x, y, xTransl, yTransl);
				gWorld.addImmovObj(x, y, xTransl, yTransl, imObj2);
			}
			else if(type.equals("bird")) {
				MovableObj movObj = new MovableObj(type, name, x, y);
				gWorld.addMovObj(x, y, movObj);
			}
			else if(type.equals("grasshopper")) {
				MovableObj movObj = new MovableObj(type, name, x, y);
				gWorld.addMovObj(x, y, movObj);
			}
		}
		
		// Step 4. Get robots and instantiate them in the world

		JSONArray worldRobArray = (JSONArray) worldObject.get("robots");    //getting robots 
		Iterator robItr         = worldRobArray.iterator();

		// Define robot capabilities

		Action fl = new Action("fl",-1,1,0);
		Action f  = new Action("f",0,1,0);
		Action fr = new Action("fr",1,1,0);
		Action l  = new Action("l",-1,0,0);
		Action r  = new Action("r",1,0,0);
		Action bl = new Action("bl",-1,-1,0);
		Action b  = new Action("b",-1,0,0);
		Action br = new Action("br",-1,1,0);
		Action rl = new Action("rl",0,0,-.25);
		Action rr = new Action("rr",0,0,.25);
		Action[] actions = {fl,f,fr,l,r,bl,b,br,rl,rr};

		Basic_Sensor fl_Im = new Basic_Sensor(new ImmovableObj(),-1,1);
		Basic_Sensor f_Im  = new Basic_Sensor(new ImmovableObj(), 0,1);
		Basic_Sensor fr_Im = new Basic_Sensor(new ImmovableObj(), 1,1);
		Basic_Sensor[] sensors = {fl_Im,f_Im,fr_Im};

		double[][] data = {	// ------------------------------  NN training data
				{ 1, 1, 1,	 1, 1, 1,0,0,0,0,0,0,0},
				{-1,-1,-1,	-1,-1,-1,0,0,0,0,0,1,1},
				{ 0, 0, 0,	 0, 0, 0,0,0,0,0,0,0,0},

				{-1,-1, 1,	-1,-1, 1,0,0,0,0,0,0,0},
				{-1, 1, 1,	-1, 1, 1,0,0,0,0,0,0,0},
				{ 1, 1,-1,	 1, 1,-1,0,0,0,0,0,0,0},
				{ 1,-1,-1,	 1,-1,-1,0,0,0,0,0,0,0},
				{-1, 1,-1,	-1, 1,-1,0,0,0,0,0,0,0},
				{ 1,-1, 1,	 1,-1, 1,0,0,0,0,0,0,0},

				{ 0, 0, 1,	 0, 0, 1,0,0,0,0,0,0,0},
				{ 0, 1, 1,	 0, 1, 1,0,0,0,0,0,0,0},
				{ 1, 0, 0,	 1, 0, 0,0,0,0,0,0,0,0},
				{ 1, 1, 0,	 1, 1, 0,0,0,0,0,0,0,0},
				{ 1, 0, 1,	 1, 0, 1,0,0,0,0,0,0,0},
				{ 0, 1, 0,	 0, 1, 0,0,0,0,0,0,0,0},

				{-1,-1, 0,	-1,-1, 0,0,0,0,0,0,0,0},
				{-1, 0, 0,	-1, 0, 0,0,0,0,0,0,0,0},
				{ 0,-1,-1,	 0,-1,-1,0,0,0,0,0,0,0},
				{ 0, 0,-1,	 0, 0,-1,0,0,0,0,0,0,0},
				{-1, 0,-1,	-1, 0,-1,0,0,0,0,0,0,0},
				{ 0,-1, 0,	 0,-1, 0,0,0,0,0,0,0,0}
		};

		// Process all the robots

		while (robItr.hasNext()) {
			JSONObject innerRob = (JSONObject) robItr.next();

			// Echo robot information

			System.out.println(innerRob.get("robName")+ " " + innerRob.get("AI") + " "+innerRob.get("robX") + " " 
					+ innerRob.get("robY"));

			// Get robot parameters

			String name = (String) innerRob.get("robName");
			String ai = (String) innerRob.get("AI");
			
			long tmpX = (long) innerRob.get("robX");
			Integer x = (int) (long) tmpX;
			
			long tmpY = (long) innerRob.get("robY");
			Integer y = (int) (long) tmpY;
			
			// -- old code
			//Robot robot = new Robot(name, x, y, false);
			//gWorld.addRobot(x, y, robot);

			// Build the robot

			Drone drone = new Drone(name, actions, sensors, gWorld, x, y, 0, false);
			gWorld.addDrone(x, y, drone);
			drone.trainDrone(data);
		}

		return gWorld;
	}

	// ------------------------------------------- simulator() -----------------------------------------------
	// Purpose:
	//
	// Input: The world map and objects in the world

	private static void simulator(GridWorld world){
		// Step 1. Instantiate the world map

		world.createWorld(true); // maybe this method name needs to change <------
		
		// STep 2. define external references to internal world data structures
		//			(maybe bad practice ... setters and getters  instead ?
		
		final Robot[][] robots              = world.getRobots();
		final ImmovableObj[][] immoveables  = world.getImObj();
		final Drone[][] drones              = world.getDrones();
		final ArrayList<Drone> droneList    = world.getDroneList();
		final ArrayList<MovableObj> movList = world.getMovList();
		final MovableObj[][] movables       = world.getMovObjs();
		final int[][] worldArray            = world.getWorld();
		boolean c = false;

		// Step 3. Run the simulation for 10 iterations
		//		( this needs to be changed in the future to run until a stop condition )
		
		for(int i = 0; i < 10; i++) {
			int k = 0;

			// Process drone actions

			for(Drone drone : droneList) {
				int x0 = drone.getX();
				int y0 = drone.getY();

				// Echo old position
				System.out.println("old: " + x0 + "," + y0);

				int[][] visible = drone.getAllVisible();

				world.createWorld(false);		//not yet showing the visible squares

				// MOVEABLE objects decide where they want to move (but they do not move yet)
				//   (this loop should probably be outside of the above for loop)

				try {
					int l = 0;

					for(MovableObj movObj : movList) {

						System.out.println("Inside movables");

						// BIRDS --------------------------------------------------------------------------

						if (movObj.getType().equals("bird")) {
							System.out.println("Inside bird");

							// moves east or west

							int x = movObj.getX();
							int y = movObj.getY();

							if(y-1 >= 0 && worldArray[x][y-1] == 0) {
								movObj.setX(x);
								movObj.setY(y-1);
								worldArray[x][y-1] = 2;
								movables[x][y-1] = movObj;
								world.setMove(x, y, x, y-1, movObj);
							}
							else if(y+1 < world.getHeight() && worldArray[x][y+1] == 0) {
								movObj.setX(x);
								movObj.setY(y+1);
								worldArray[x][y+1] = 2;
								movables[x][y+1] = movObj;
								world.setMove(x, y, x, y+1, movObj);
							}

							world.createWorld(false);
						}

						// GRASSHOPPER ---------------------------------------------------------------------

						if (movObj.getType().equals("grasshopper")) {

							// moves east or west

							int x = movObj.getX();
							int y = movObj.getY();

							if(y-2 >= 0 && worldArray[x][y-2] == 0) {
								movObj.setX(x);
								movObj.setY(y-2);
								worldArray[x][y-2] = 2;
								movables[x][y-1] = movObj;
								world.setMove(x, y, x, y-2, movObj);
							}
							else if(y+2 < world.getHeight() && worldArray[x][y+2] == 0) {
								movObj.setX(x);
								movObj.setY(y+2);
								worldArray[x][y+2] = 2;
								movables[x][y+2] = movObj;
								world.setMove(x, y, x, y+2, movObj);
							}

							world.createWorld(false);
						}

						// Artificial delay to loop   <------- maybe remove in the future

						try {
							TimeUnit.SECONDS.sleep(1);
						}
						catch(InterruptedException e) {
							System.out.println("Wait issue");
						}

						l++;
					}

					// Process ROBOTS ----------------------------------------------------------------------

					drone.makeDecision();
				}
				catch(indecisiveException e) {
					drone.setMistake(false);
					System.out.println("Indecisive decision");
					break; // <---------------------------------------- End loop??
				}

				// Given the decisions made by the MOVEABLE objects, try to make the move, test for move errors

				// This is the movement requested by the moveable object
				int x1           = drone.getX();
				int y1           = drone.getY();
				double direction = drone.getDirection();

				// Test to see if this move can be made
				try {
					// Echo move request
					System.out.println(x1 + "," + y1);

					// Test validity of move

					if(0 <= y1 && y1 < world.getWidth() && 0 <= x1 && x1 < world.getHeight() && worldArray[x1][y1] == 1) {
				
						// Bumped into an immovable object

						drone.setMistake(true);
						crashException crash = new crashException("You crashed: immovable object");
						throw crash;
					
					}
					else if(0 <= y1 && y1 < world.getWidth() && 0 <= x1 && x1 < world.getHeight() && worldArray[x1][y1] == 2) {
					
						// Bumped into a movable object

						drone.setMistake(true);
						crashException crash = new crashException("You crashed: movable object");
						throw crash;
				
					}
					else if(0 <= y1 && y1 < world.getWidth() && 0 <= x1 && x1 < world.getHeight() && worldArray[x1][y1] == 3) {
						
						// Bumped into a robot object

						drone.setMistake(true);
						crashException crash = new crashException("You crashed: robot object");
						throw crash;
				
					}
					else if (0 <= y1 && y1 < world.getWidth() && 0 <= x1 && x1 < world.getHeight() && worldArray[x1][y1] == 0) {

						// Valid move, make the move

						worldArray[x1][y1] = 3;
						drones[x1][y1] = drone;
						drone.setX(x1);
						drone.setY(y1);
						if(x0 != x1 || y0 != y1) {
							System.out.println(x0 + "," + y0);
							worldArray[x0][y0] = 0;
							drones[x0][y0] = null;
						}
						drone.setDirection(direction);
					}
					else if (y1 < 0 || y1 >= world.getHeight() || x1 < 0 || x1 >= world.getWidth()) {

						// Out of bounds so do nothing

						drone.setX(x0);
						drone.setY(y0);
						worldArray[x0][y0] = 3;
						drones[x0][y0] = drone;
					}
					try {
						TimeUnit.SECONDS.sleep(1);
					}
					catch(InterruptedException e) {
						System.out.println("Wait issue");
					}
				}
				catch(crashException e) {
					System.out.println("Crash at (" + x1 + "," + y1 + ")");
					c = true;
					break;
				}
				if(c == false)
					k++;
				else
					break;
			}
			System.out.println(i);
		}
		
	
		/* ---------------------   runs the robots without the drones  ------------------------------------

		(maybe remove the following code ? )

		for(int k = 0; k < 10; k++) {	// 10 iterations; at each iteration, the robot moves one step in a random direction
			for(int i = 0; i < world.getWidth(); i++) {
				for(int j = 0; j < world.getHeight(); j++) {
					if(worldArray[i][j] == 3) {		//a robot is in this position
						double direction = robots[i][j].randDirection();
						robots[i][j].setDirection(direction);
						if (direction < 0.25) {			//north
							if(i == 0 || worldArray[i-1][j] != 0) {	//contains an object or cannot move because at border
								System.out.println("Inside if of north");
								world.getRobot(i, j).setMistake(true);
								world.createWorld(false);
							}
							else {	//empty spot and robot can move
								Robot newRob = new Robot(robots[i][j].getName(), i-1, j, false);
								world.updateWorld(newRob, i-1, j, i, j);
								world.createWorld(false);
							}
							try{
								TimeUnit.SECONDS.sleep(1);
							}
							catch(InterruptedException e){
								System.out.println("Wait issue");
							}		
						}
						else if (direction < 0.5) {			//east
							if(j == (world.getHeight() - 1) || worldArray[i][j+1] != 0) {	//contains an object
								world.getRobot(i, j).setMistake(true);
								world.createWorld(false);
							}
							else {	//empty spot and robot can move
								Robot newRob = new Robot(robots[i][j].getName(), i, j+1, false);
								world.updateWorld(newRob, i, j+1, i, j);
								world.createWorld(false);
							}
							try{
								TimeUnit.SECONDS.sleep(1);
							}
							catch(InterruptedException e){
								System.out.println("Wait issue");
							}	
						}
						else if (direction < 0.75) {			//south
							if(i == (world.getWidth()-1) || worldArray[i+1][j] != 0) {	//contains an object
								world.getRobot(i, j).setMistake(true);
								world.createWorld(false);
							}
							else {	//empty spot and robot can move
								Robot newRob = new Robot(robots[i][j].getName(), i+1, j, false);
								world.updateWorld(newRob, i+1, j, i, j);
								world.createWorld(false);
							}
							try{
								TimeUnit.SECONDS.sleep(1);
							}
							catch(InterruptedException e){
								System.out.println("Wait issue");
							}	
						}
						else if (direction < 1) {			//west
							if(j == 0 || worldArray[i][j-1] != 0) {	//contains an object
								world.getRobot(i, j).setMistake(true);
								world.createWorld(false);
							}
							else {	//empty spot and robot can move
								Robot newRob = new Robot(robots[i][j].getName(), i, j-1, false);
								world.updateWorld(newRob, i, j-1, i, j);
								world.createWorld(false);
							}
							try{
								TimeUnit.SECONDS.sleep(1);
							}
							catch(InterruptedException e){
								System.out.println("Wait issue");
							}	
						}
					}
				}
			}
		} */
		
		
	} 
	
}