//From Chris's project, with minor modifications to fit my code
/* "This class is used to represent sensors the drone is equipped with.
Each sensor has a name associated with it, the position of the sensor relative to the drone,
 what type of WorldObject it is sensitive to, and whether it is a positive or negative sensor.

All x and y values assume that the robot currently has direction=0 (i.e the drone is facing due north)

An example sensor would be:
name = "Forward Left Immovable"
signal = new Immovable()
x = "-1" left
y = "1" forward
positive = true

If the object being sensed is different from the signal type, then a 0 is given to the Drone
If the object being sensed matches the signal type, then 1 is returned if (positive==True) and -1 else"
*/

public class Basic_Sensor {
	private Class<? extends WorldObjects> signal;
	private int x;
	private int y;
	private int range = 1;
	private boolean positive = true;

	public Basic_Sensor(WorldObjects signal, int x, int y, boolean positive) { //constructor for when you want to specify positive/negative
		this.signal = signal.getClass();
		this.x = x;
		this.y = y;
		this.positive = positive;
	}
	public Basic_Sensor(WorldObjects signal, int x, int y) { //constructor when you assume a sensor is positive (the normal workflow)
		this.signal = signal.getClass();
		this.x = x;
		this.y = y;
	}

	//setter/getter methods
	public Class getSignal() {
		return this.signal;
	}

	public int[] getX() {
		return new int[]{this.x};
	}

	public int[] getY() {
		return new int[]{this.y};
	}
	public int getRange(){
		return this.range;
	}
	
	//This function takes a WorldObject and returns the value appropriate with this sensor
	private WorldObjects getObject(GridWorld world, int[][] visible_cells) {
		int[][] worldArray = world.getWorld();
		
		WorldObjects visible_object = null;
		
		for(int i = 0; i < this.range; i++) {
			int x = visible_cells[i][0];
			int y = visible_cells[i][1];
			System.out.println("inside getObject");
			if(0 <= y && y < world.getWidth() && 0 <= x && x < world.getHeight()) {
				if( worldArray[x][y] == 1) 
					visible_object = world.getImObject(x,y);
				else if( worldArray[x][y] == 3)
					visible_object = world.getRobot(x, y);
			}
			else
				visible_object = null;
			i++;
		}
		return visible_object;
	}
	
	public double score(GridWorld world, int[][] visible) {
		int weight = -1;
		if (this.positive)
			weight = 1;
		WorldObjects object = getObject(world, visible);
		
		if(object == null) {
			return 1.0*weight;
		}
		else if(object.getClass().equals(this.getSignal())) {
			return -1.0*weight;
		}
		else {
			return 0.0;
		}
	}
}