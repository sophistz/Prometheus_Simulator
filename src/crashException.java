public class crashException extends Exception {


	public crashException(String message) {
		super(message);
	}

	public crashException(String message, Throwable throwable) {
	    super(message, throwable);
	}

}
