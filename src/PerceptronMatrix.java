/* From Chris's code

import java.util.Arrays;
import java.util.stream.DoubleStream;
 This is the class for the first level of the Prometheus AI, the Neural Network
 It is implimented as a single layer Perceptron that is trained through Stochastic Gradient Descent (SGD)
 It takes inputs from the Drone's Sensors and calculates which of the Drone's current Actions is best suited for the situation

 Data is given to the Perceptron matrix as a 2D array of integers
 If the drone has N sensors and M actions, then one line of that data should be given in the form:
 [N,N,N,  M,M,M] for N=M=3
 So if you want the drone to respond to an input from its sensors being [1,0,1], and have all but the last action be a possibility,
 then it would look like [1,0,1,  1,1,0]
 There should be 3^N datapoints to cover each possible sensor value and configuration (sensors can be = [-1,0,1])

 The perceptron will calculate each possible Action given the Sensor input and choose the highest scoring Action, choosing ties randomly
 If no dominant Action exists, it throws an indecisiveException to the drone
*/

import java.util.Arrays;
import java.util.stream.DoubleStream;

public class  PerceptronMatrix {
	private double[] inputs; //[-1,0,1] values that correspond to the sensor values
	private double[] outputs; //[-1,0,1] values that correspond to the result of multiplying the inputs with the inner weights
	private Action[] actions; //Actions that the Drone associated with the perceptron can perform. Will dictate perceptron's height
	private Basic_Sensor[] sensors; //Sensors that the Drone associated with the perceptron can utilize. Will dictate perceptron's width
	private double[][] parameters; //the weights for the perceptron matrix
	private double[] bias;		//bias is added to weights when calculating scores

	public PerceptronMatrix(Basic_Sensor[] sensors, Action[] actions) {
		int output_number = actions.length;
		int input_number = sensors.length;
		
		this.sensors = sensors;
		this.inputs = new double[input_number];
		this.outputs = new double[output_number];
		this.actions = actions;
		this.parameters = new double[output_number][input_number];
		this.bias = new double[output_number];
	}
	
	//setters and getters
	public void setInputs(double[] inputs) {
		this.inputs = inputs;
	}
	public double[] getOutputs() {
		return this.outputs;
	}
	
	//math helper functions
	private static double sigmoid(double x){
	    return 1 / (1 + Math.exp(-x));
	}
	
	private double[] matrixMultiplication(double[] vector, double[][] matrix) {
	    int rows = matrix.length;
	    int columns = matrix[0].length;

	    double[] result = new double[rows];

		for(int row=0; row<rows;row++) {
			double sum=0;
			for(int column=0; column<columns;column++) {
				sum += matrix[row][column] * vector[column];
			}
			result[row] = sum;
		}
		return result;
	}
	
	private double[] vectorAddition(double[] vector1, double[] vector2) {
		double[] returnVector = new double[vector1.length];
		for(int i=0;i<vector1.length;i++) {
			returnVector[i] = vector1[i]+vector2[i];
		}
		return returnVector;
	}
	
	//MSE = (1/N) * Sum i=1->N (predicted_i - expected_i)^2
	private double meanSquareError(double[][] data_inputs, double[] data_classifications, double[] weights, double bias) {
		double error = 0;
		for (int i=0;i<data_inputs.length;i++) {
			double polyval = bias;
			for(int j=0;j<data_inputs[i].length;j++) {
				polyval += data_inputs[i][j] * weights[j];
			}
			error += Math.pow(data_classifications[i] - polyval, 2);
		}
		return (error/data_inputs.length);
	}
	
	//Calculates the output of each line of the perceptron by multiplying the input vector with the perceptron matrix and adding the bias vector
	//Then runs a sigmoid function over each result to compress the output to a value between 0 and 1
	public void calculateOutputs() {
			this.outputs = matrixMultiplication(this.inputs, this.parameters);
			this.outputs = vectorAddition(this.outputs, this.bias);
			DoubleStream stream = Arrays.stream(this.outputs).map(x ->{return Math.round(sigmoid(x)*100000)/100000.0;}); //rounds output to 5 decimals, useful for eliminating "noise" in the output
			this.outputs = stream.toArray();
	}
	
	//Calculates the outputs based on given inputs and then selects the action with the greatest weight, breaking ties randomly
	//If the highest weight is < 0.55 then it throws indecisiveException
	public Action makeDecision() throws indecisiveException {
		calculateOutputs();
		double max_score = 0;
		int index = 0;
		int max_index = 0;
		for(double score : outputs) {
			if(score>max_score) {
				max_score = score;
				max_index = index;
			}
			if(score == max_score) {
				double rand = Math.random(); //randomly break ties
				if(rand < 0.5) {
					max_index = index;
				}
			}
			index++;
		}
		if(max_score < 0.55) {
			indecisiveException ind = new indecisiveException("Max score was:" + max_score + " which is less than the required 0.3");
			throw ind;
		}
		else {
			return actions[max_index];
		}

	}

	//helper method for learning process
	private double predict(double pbias, double[] pweights, double[] pinputs) {

		double activation = pbias;
		for(int i = 0; i < pinputs.length; i++){
			activation += pweights[i] * pinputs[i];
		}
		return activation;
	}
	
	//Use SGD to learn the correct weights for a given row of the perceptron and bias matrix corresponding to a given Action
	//Current learning weight of 1e-4 and threshold of 1e-20 were not very scientifically reached. They work well though
	private void learnRowWeights(int row_number, double[][] training_data) {
		double[] weights = this.parameters[row_number].clone();
		double bias = this.bias[row_number];
		double[][] inputs = new double[training_data.length][this.inputs.length];
		double[] outputs =  new double[training_data.length];

		for(int row=0;row<training_data.length;row++) {
			for(int column=0;column<training_data[row].length;column++) {
				if(column < this.inputs.length) {
					inputs[row][column] = training_data[row][column];
				}
				else {
					outputs[row] = training_data[row][column];
				}
			}
		}
		int epoch = 10000000;
		double stepsize = 1e-4;
		double mseCutoff = 1e-20;

		for(int i=0;i<epoch; i++) {
			double[] sum = new double[this.inputs.length+1];

			for(int j=0;j<training_data.length;j++) {
				double prediction = predict(bias,weights,inputs[j]);
				double error = prediction - outputs[j];
				sum[0] += error;
				for(int k=1;k<sum.length;k++) {
					sum[k] += error * inputs[j][k-1];
				}
			}

			bias -= stepsize * (sum[0]/ training_data.length);
			for(int k=1;k<sum.length;k++) {
				weights[k-1] -= stepsize * (sum[k]/ training_data.length);
			}

			if( (meanSquareError(inputs, outputs, this.parameters[row_number],this.bias[row_number]) - meanSquareError(inputs, outputs, weights,bias)) < mseCutoff) {
				this.parameters[row_number] = weights.clone();
				this.bias[row_number] = bias;
				break;
			}
			this.parameters[row_number] = weights.clone();
			this.bias[row_number] = bias;
		}
	}
	
	//Trains all rows in the perceptron one-by-one given training data
	//Should be the initial call to the perceptron matrix
	//Takes the lareg data format of [N,N,N,M,M,M] and splits it into several [N,N,N,M] sets to pass to learnRowWeights()
	//Calls learnRowWeights() once for each Action the perceptron is responsible for.
	public void learnAllWeights(double[][] training_data) {
		double[][] training_subset = new double[training_data.length][this.inputs.length+1];
		for(int i=0;i<this.parameters.length;i++) {

			for(int row=0;row<training_subset.length;row++) {
				for(int column=0;column<training_subset[row].length;column++) {
					if(column<this.inputs.length) {
						training_subset[row][column] = training_data[row][column];
					}
					else {
						training_subset[row][column] = training_data[row][this.inputs.length+i];
					}
				}
			}
			learnRowWeights(i,training_subset);
		}
	}
	
	public void addSensor(Basic_Sensor sensor, double[][] training_data){
		Basic_Sensor[] sensor_append = new Basic_Sensor[this.sensors.length+1];
		int i=0;
		for(Basic_Sensor snsr : this.sensors){
			sensor_append[i] = snsr;
			i++;
		}
		sensor_append[sensor_append.length-1] = sensor;
		this.sensors = sensor_append;
		this.inputs = new double[this.inputs.length+1];

		double[][] parameters_append = new double[this.parameters.length][this.parameters[0].length+1];
		this.parameters = parameters_append;
		learnAllWeights(training_data);
	}
	
	public void addAction(Action action, double[][] training_data){
		Action[] action_append = new Action[this.actions.length+1];
		int i=0;
		for(Action actn : this.actions){
			action_append[i] = actn;
			i++;
		}
		action_append[action_append.length-1] = action;
		this.actions = action_append;
		this.outputs = new double[this.outputs.length+1];

		double[][] parameters_append = new double[this.parameters.length+1][this.parameters[0].length];
		i=0;
		for(double[] param : this.parameters){
			parameters_append[i] = param.clone();
			i++;
		}
		this.parameters = parameters_append;
		learnRowWeights(this.parameters.length-1, training_data);
	}

	//calling print() will print the coefficients associated with the perceptron matrix and the bias matrix,
	// as well as the Action names each row corresponds with
	public void print() {
		System.out.printf("%9s  ", "bias");
		for (int i =0;i<this.inputs.length;i++) {
			String out = "Input" + (i+1);
            System.out.printf("%9s  ",out);
        }
		System.out.println("");
		for(int i=0;i<this.parameters.length;i++) {
			printRow(this.parameters[i],i);
		}
	}
	
	//helper function to print()
	private void printRow(double[] row, int rowNum) {
		System.out.printf("% 6f |",this.bias[rowNum]);
		for (double i : row) {
			System.out.printf("% 6f  ",i);
		}
		System.out.printf("%-5s",this.actions[rowNum].getName());
		System.out.println();
	}
}

