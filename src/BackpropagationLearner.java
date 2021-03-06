import java.util.Random;
import java.util.ArrayList;

public class BackpropagationLearner extends SupervisedLearner{
		
	public class Node{
		
		private double[] weight;
		private double[] weight_change;
		private int num_weight;
		private double output;
		private double delta;
		
		public Node(int num_weight){
			this.set_output(0);
			this.set_delta(0);
			this.weight = new double[num_weight];
			this.weight_change = new double[num_weight];
			this.num_weight = num_weight;
			initialize_weight(); 		// when created, the node initializes its weights
		}

		public void initialize_weight(){
			for(int i = 0; i < weight.length; i++){
				weight[i] = get_gaussian();
			}
		}
		
		public void print_weight(){
			for(int i = 0; i < weight.length; i++){
				System.out.println(weight[i]);
			}
		}
		
		public double get_gaussian(){
			double min = -1/Math.sqrt(num_weight);
			double max = 1/Math.sqrt(num_weight);
			
			boolean condition = true;
			while(condition){
				double gaussian = random.nextGaussian();
				if(gaussian > max || gaussian < min || gaussian == 0){
					// do nothing
				}
				else{
					return gaussian;
				}
			}
			return -1;
//			return 1;
		}
		public void set_weight_element(int index, double change){
			this.weight[index] = change;
		}		
		public double get_weight_element(int index){
			return weight[index];
		}
		public double[] get_weight() {
			return weight;
		}
		public void set_weight(double[] weight) {
			this.weight = weight;
		}
		public double get_output() {
			return output;
		}
		public void set_output(double output) {
			this.output = output;
		}
		public void set_weight_change_element(int index, double change){
			this.weight_change[index] = change;
		}
		public double get_weight_change_element(int index){
			return weight_change[index];
		}
		public double[] get_weight_change() {
			return weight_change;
		}
		public void set_weight_change(double[] weight_change) {
			this.weight_change = weight_change;
		}
		public int get_num_weight() {
			return num_weight;
		}
		public void set_num_weight(int num_weight) {
			this.num_weight = num_weight;
		}
		public double get_delta() {
			return delta;
		}
		public void set_delta(double delta) {
			this.delta = delta;
		}
	}
	
	Random random;
	ArrayList<ArrayList<Node>> layers;	
	int bias = 1;
	
	public BackpropagationLearner(Random rand){
		random = rand;
	}
	
	public void create_layer(int num_input, int num_output, int num_layer){

		layers = new ArrayList<ArrayList<Node>>(); 
		
		int[] num_nodes = new int[num_layer+2];
//		num_nodes[0] = num_input;
//		num_nodes[1] = 64; 		// specify the number of nodes for the first hidden layer
//		num_nodes[2] = 64;		// specify the number of nodes for the second hidden layer
//		num_nodes[3] = num_output;

//		num_nodes[0] = num_input;
//		num_nodes[1] = 64; 		// specify the number of nodes for the first hidden layer
//		num_nodes[2] = 64;		// specify the number of nodes for the second hidden layer
//		num_nodes[3] = 64;
//		num_nodes[4] = num_output;

		num_nodes[0] = num_input;
		num_nodes[1] = 64; 		// specify the number of nodes for the first hidden layer
		num_nodes[2] = 64;		// specify the number of nodes for the second hidden layer
		num_nodes[3] = 64;
		num_nodes[4] = 64;	
		num_nodes[5] = num_output;

//		num_nodes[0] = num_input;
//		num_nodes[1] = 64; 		// specify the number of nodes for the first hidden layer
//		num_nodes[2] = num_output;
		

		
		for(int i = 0; i <= num_layer; i++){
			ArrayList<Node> one_layer = new ArrayList<Node>();			
			for(int j = 0; j < num_nodes[i+1]; j++){
				Node new_node = new Node(num_nodes[i]+1);
				one_layer.add(new_node);
			}
			layers.add(one_layer);
		}
		
// print all the weights
//		for(int i = 0; i < layers.size(); i++){
//			for(int j = 0; j < layers.get(i).size(); j++){
//				layers.get(i).get(j).print_weight();
//				System.out.println();
//			}
//		}		
		
	}

	public void forward_path(Matrix features, int a){ 		// 'a' is the row index of the Matrix
		for(int i = 0; i < layers.size(); i++){
			if(i == 0){ 		// first hidden layer
				for(int j = 0; j < layers.get(i).size(); j++){
					double net = bias*layers.get(i).get(j).get_weight_element(0);
					for(int k = 0; k < features.cols(); k++){
						net += features.get(a, k)*layers.get(i).get(j).get_weight_element(k+1);
					}
					double output = 1/(1+Math.exp(-net));
					//						System.out.println("output: " + output);
					layers.get(i).get(j).set_output(output);
				}
			}
			else{ 				// all the other layers
				for(int j = 0; j < layers.get(i).size(); j++){
					double net = bias*layers.get(i).get(j).get_weight_element(0);
					for(int k = 0; k < layers.get(i).get(j).get_num_weight()-1; k++){
						net += layers.get(i-1).get(k).get_output()*layers.get(i).get(j).get_weight_element(k+1);
					}
					double output = 1/(1+Math.exp(-net));
//					System.out.println("output: " + output);
					layers.get(i).get(j).set_output(output);
				}
			}
		}	
	}
	
	public double backward_path(Matrix features, Matrix labels, int a){ 		// 'a' is the row index of the Matrix
		double learning_rate = 0.1;		// specify the learning rate
//		double learning_rate = 1;
		double momentum = 0;
		double mse = 0;
		
		for(int i = layers.size()-1; i >= 0; i--){
			if(i == layers.size()-1){ 		// output layer
				for(int j = 0; j < layers.get(i).size(); j++){
					double output = layers.get(i).get(j).get_output();
					double fnet = output*(1-output);
					int answer = -1;
					if(labels.get(a, 0) == j){
						answer = 1;
					}
					else{
						answer = 0;
					}
//					System.out.println("answer: " + answer);
					double delta = (answer-output)*fnet;
					
					mse += 0.5*Math.pow((answer-output), 2);
					
//					double delta = (labels.get(a, 0)-output)*fnet;
//					System.out.println("delta: " + delta);
					layers.get(i).get(j).set_delta(delta);
					for(int k = 0; k < layers.get(i).get(j).get_num_weight(); k++){
						double change = 0;
						if(k == 0){ 		// for bias node output
							change = learning_rate*delta*bias + momentum*layers.get(i).get(j).get_weight_change_element(k);
						}
						else{
							change = learning_rate*delta*layers.get(i-1).get(k-1).get_output() + momentum*layers.get(i).get(j).get_weight_change_element(k);
						}
//						System.out.println("change: " + change);
						layers.get(i).get(j).set_weight_change_element(k, change);
					}
				}
			}

			else if(i > 0){ 			// hidden layer
				for(int j = 0; j < layers.get(i).size(); j++){
					double output = layers.get(i).get(j).get_output();
					double fnet = output*(1-output);
					double sum = 0;
					for(int k = 0; k < layers.get(i+1).size(); k++){
						sum += layers.get(i+1).get(k).get_delta()*layers.get(i+1).get(k).get_weight_element(j+1);
					}
					double delta = fnet*sum;
					layers.get(i).get(j).set_delta(delta);
					for(int k = 0; k < layers.get(i).get(j).get_num_weight(); k++){
						double change = 0;
						if(k == 0){ 		// for bias node output
							change = learning_rate*delta*bias + momentum*layers.get(i).get(j).get_weight_change_element(k);
						}
						else{
							change = learning_rate*delta*layers.get(i-1).get(k-1).get_output() + momentum*layers.get(i).get(j).get_weight_change_element(k);
						}
						layers.get(i).get(j).set_weight_change_element(k, change);
					}						
				}
			}
			else{			// first layer
				for(int j = 0; j < layers.get(i).size(); j++){
					double output = layers.get(i).get(j).get_output();
					double fnet = output*(1-output);
					double sum = 0;
					for(int k = 0; k < layers.get(i+1).size(); k++){
						sum += layers.get(i+1).get(k).get_delta()*layers.get(i+1).get(k).get_weight_element(j+1);
					}
					double delta = fnet*sum;
//					System.out.println("delta: " + delta);
					layers.get(i).get(j).set_delta(delta);
					for(int k = 0; k < layers.get(i).get(j).get_num_weight(); k++){
						double change = 0;
						if(k == 0){ 		// for bias node output
							change = learning_rate*delta*bias + momentum*layers.get(i).get(j).get_weight_change_element(k);
						}
						else{
							change = learning_rate*delta*features.get(a, k-1) + momentum*layers.get(i).get(j).get_weight_change_element(k);
						}
//						System.out.println("change: " + change);
						layers.get(i).get(j).set_weight_change_element(k, change);
					}						
				}					
			}
		}
		return mse;
	}

	public void update_weights(){
		for(int i = 0; i < layers.size(); i++){
			for(int j = 0; j < layers.get(i).size(); j++){
				for(int k = 0; k < layers.get(i).get(j).num_weight; k++){
//					System.out.println("weight: " + layers.get(i).get(j).get_weight_element(k) + "\tweight_change: " + layers.get(i).get(j).get_weight_change_element(k));
					double new_weight = layers.get(i).get(j).get_weight_element(k)+layers.get(i).get(j).get_weight_change_element(k);
					layers.get(i).get(j).set_weight_element(k, new_weight);
//					System.out.println("new_weight: " + layers.get(i).get(j).get_weight_element(k));
				}
			}
		}
	}
	
	public void train(Matrix features, Matrix labels) throws Exception {
		int num_input = features.cols(); 
		int num_output = labels.valueCount(0);
//		int num_output = 1;
		int num_layer = 4;		// specify the number of hidden layers
		create_layer(num_input, num_output, num_layer);		// a function to set up the hidden layers
		
		double train_percent = 0.9;
		int train_size = (int)(train_percent*features.rows());

		
		int counter = 0;
		boolean loop_condition = true;
		double previous_accuracy = 0;
		
		ArrayList<Double> train_mse = new ArrayList<>();
		ArrayList<Double> test_mse = new ArrayList<>();
		ArrayList<Double> accuracies = new ArrayList<>();
		
		while(loop_condition){	
			features.shuffle(random, labels);
			Matrix train_features = new Matrix(features, 0, 0, train_size, features.cols());
			Matrix train_labels = new Matrix(labels, 0, 0, train_size, 1);
			Matrix test_features = new Matrix(features, train_size, 0, features.rows()-train_size, features.cols());
			Matrix test_labels = new Matrix(labels, train_size, 0, features.rows()-train_size, 1);
			double mse = 0;
			for(int i = 0; i < train_features.rows(); i++){
//				forward_path(features, i);
//				backward_path(features, labels, i);
//				update_weights();
				
				forward_path(train_features, i);
				mse += backward_path(train_features, train_labels, i);
				update_weights();
			}
			mse = mse/train_features.rows();
			train_mse.add(mse);
			
			mse = 0;
			for(int i = 0; i < test_features.rows(); i++){
				
				forward_path(test_features, i);
				mse += backward_path(test_features, test_labels, i);
				update_weights();
			}
			mse = mse/test_features.rows();
			test_mse.add(mse);
			
			double current_accuracy = calculate_accuracy(test_features, test_labels);
//			if((current_accuracy > 0.9 && previous_accuracy > current_accuracy) || current_accuracy == 1.0){
//				loop_condition = false;
//			}
			
			if(counter > 100){
				double lowest = 1;
				for(int i = 0; i < 100; i++){
					if(accuracies.get(accuracies.size()-1-i) < lowest){
						lowest = accuracies.get(accuracies.size()-1-i);
					}
				}
				if(lowest > current_accuracy){
					loop_condition = false;
				}
			}
			
//			if(current_accuracy > 0.9){
//				loop_condition = false;
//			}
			accuracies.add(current_accuracy);
			System.out.println(counter + " epochs" + "\taccuracy: " + current_accuracy);
			previous_accuracy = current_accuracy;
			counter++;
		}
		
//		for(int i = 0; i < train_mse.size(); i++){
//			System.out.print(train_mse.get(i) + ",");
//		}
//		System.out.println();
//		for(int i = 0; i < test_mse.size(); i++){
//			System.out.print(test_mse.get(i) + ",");
//		}
//		System.out.println();
//		for(int i = 0; i < accuracies.size(); i++){
//			System.out.print(accuracies.get(i) + ",");
//		}
//		System.out.println();
		
		System.out.println("train_mse: " + train_mse.get(train_mse.size()-1));
		System.out.println("validation_mse: " + test_mse.get(test_mse.size()-1));
		System.out.println("validation_accuracy: " + accuracies.get(accuracies.size()-1));
		
	}
	
	public double calculate_accuracy(Matrix features, Matrix labels) throws Exception{
		int correctCount = 0;
		double[] prediction = new double[1];
		for(int i = 0; i < features.rows(); i++)
		{
			double[] feat = new double[features.cols()];
			feat = features.row(i);
								
			int targ = (int)labels.get(i, 0);
			predict(feat, prediction);
			int pred = (int)prediction[0];
								
			if(pred == targ)
				correctCount++;
		}
		double current_accuracy = (double)correctCount / features.rows();
		
		return current_accuracy;
	}

	public void predict(double[] features, double[] labels) throws Exception {
		double final_index = -1;
		for(int i = 0; i < layers.size(); i++){
			if(i == 0){ 		// first hidden layer
				for(int j = 0; j < layers.get(i).size(); j++){
					double net = bias*layers.get(i).get(j).get_weight_element(0);
					for(int k = 0; k < features.length; k++){
						net += features[k]*layers.get(i).get(j).get_weight_element(k+1);
					}
					double output = 1/(1+Math.exp(-net));
					layers.get(i).get(j).set_output(output);
				}
			}
			else if(i < layers.size()-1){ 				// other hidden layers
				for(int j = 0; j < layers.get(i).size(); j++){
					double net = bias*layers.get(i).get(j).get_weight_element(0);
					for(int k = 0; k < layers.get(i-1).size(); k++){
						net += layers.get(i-1).get(k).get_output()*layers.get(i).get(j).get_weight_element(k+1);
					}
					double output = 1/(1+Math.exp(-net));
					layers.get(i).get(j).set_output(output);
				}
			}
			else{		// output layer
				double answer = -1;
				int index = -1;
				for(int j = 0; j < layers.get(i).size(); j++){
					double net = bias*layers.get(i).get(j).get_weight_element(0);
					for(int k = 0; k < layers.get(i-1).size(); k++){
						net += layers.get(i-1).get(k).get_output()*layers.get(i).get(j).get_weight_element(k+1);
					}
					double output = 1/(1+Math.exp(-net));
					layers.get(i).get(j).set_output(output);
//					System.out.println("j: " + j + "\toutput: " + output);
					if(output > answer){
						answer = output;
						index = j;
					}
				}
				final_index = index;
			}
		}
		labels[0] = final_index;
		
	}
	public double predict(double[] features, int target) throws Exception {
		double mse = 0;
		for(int i = 0; i < layers.size(); i++){
			if(i == 0){ 		// first hidden layer
				for(int j = 0; j < layers.get(i).size(); j++){
					double net = bias*layers.get(i).get(j).get_weight_element(0);
					for(int k = 0; k < features.length; k++){
						net += features[k]*layers.get(i).get(j).get_weight_element(k+1);
					}
					double output = 1/(1+Math.exp(-net));
					layers.get(i).get(j).set_output(output);
				}
			}
			else if(i < layers.size()-1){ 				// other hidden layers
				for(int j = 0; j < layers.get(i).size(); j++){
					double net = bias*layers.get(i).get(j).get_weight_element(0);
					for(int k = 0; k < layers.get(i-1).size(); k++){
						net += layers.get(i-1).get(k).get_output()*layers.get(i).get(j).get_weight_element(k+1);
					}
					double output = 1/(1+Math.exp(-net));
					layers.get(i).get(j).set_output(output);
				}
			}
			else{		// output layer
				for(int j = 0; j < layers.get(i).size(); j++){
					double answer = -1;
					if(target == j){
						answer = 1;
					}
					else{
						answer = 0;
					}
					double net = bias*layers.get(i).get(j).get_weight_element(0);
					for(int k = 0; k < layers.get(i-1).size(); k++){
						net += layers.get(i-1).get(k).get_output()*layers.get(i).get(j).get_weight_element(k+1);
					}
					double output = 1/(1+Math.exp(-net));
					layers.get(i).get(j).set_output(output);
					mse += 0.5*Math.pow((answer-output), 2);
				}
			}
		}		
		return mse;
	}

}

