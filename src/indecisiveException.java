/* From Chris's code.
This exception is used to tell the drone when it is ina situation that its current perceptron matrix is unable to resolve
It should alert the Drone to initiate new self-training, or for reserachers (us) to see when new data is needed to be given.
This object is thrown when an exception action is processed by the drone
*/

public class indecisiveException extends Exception {


	public indecisiveException(String message) {
		super(message);
	}

	public indecisiveException(String message, Throwable throwable) {
	    super(message, throwable);
	}

}
