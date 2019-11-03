/* From Chris's project, with minor modifications.
"This class is used to give actions to the drone.
Each action has a name associated with it, how that action changes the robot's position, and how it changes it's direction.
All values assume that the robot currently has direction=0 (i.e the drone is facing due north)

An example action would be:
name = "Forward Left"
x_delta = "-1"
y_delta = "1"
direction_delta = "0"

Alternatively, an action can throw an exception to the drone saying that it is in a situation that it does not know what to do.
This is used to be able to force the robot to learn."
 */

// Purpose: Defines an action that a MOVEABLE can perform
// Creator: Chris O'Connor
//
// TO DO List
// ==========
// Commenting and code reformatting							 Joseph		Done
// What is the purpose of direction_delta?                  TBD       To Do

public class Action {
	private String name;                    // Name of action
	private int x_delta = 0;                // Change in x direction
	private int y_delta = 0;                // Change in y direction
	private double direction_delta = 0;     // ??
	private indecisiveException exception;  // This classes exception

    // Purpose: Constructor
    // Note   : Requires name and delta values, but not exception
	public Action(String name, int x, int y, double dir) {
		this.name = name;
		this.x_delta = x;
		this.y_delta = y;
		this.direction_delta = dir;
		this.exception = null;
	}

	// Purpose: Constructor for exception action
    // Note   : Only requires the message the exception delivers to initialize
	public Action(String exception_message){
		this.name = "Exception";
		this.exception = new indecisiveException(exception_message);
	}

	// Purpose: Throws the action's exception
	public void exception() throws indecisiveException{
		throw this.exception;
	}

	// getter methods
	public int    getXDelta(){
		return x_delta;
	}
	public int    getYDelta(){
		return y_delta;
	}
	public double getDirectionDelta(){
		return direction_delta;
	}

	public String getName() {
		return this.name;
	}
}