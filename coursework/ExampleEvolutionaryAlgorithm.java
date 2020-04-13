package coursework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import model.Fitness;
import model.Individual;
import model.LunarParameters.DataSet;
import model.NeuralNetwork;

/**
 * Implements a basic Evolutionary Algorithm to train a Neural Network
 * 
 * You Can Use This Class to implement your EA or implement your own class that extends {@link NeuralNetwork} 
 * 
 */
public class ExampleEvolutionaryAlgorithm extends NeuralNetwork {
	

	/**
	 * The Main Evolutionary Loop
	 */
	@Override
	public void run() {		
		//Initialise a population of Individuals with random weights
		population = initialise();

		//Record a copy of the best Individual in the population
		best = getBest();
		System.out.println("Best From Initialisation " + best);

		/**
		 * main EA processing loop
		 */		
		
		while (evaluations < Parameters.maxEvaluations) {

			/**
			 * this is a skeleton EA - you need to add the methods.
			 * You can also change the EA if you want 
			 * You must set the best Individual at the end of a run
			 * 
			 */

			// Select 2 Individuals
			//Individual parent1 = tournament();
			//Individual parent2 = tournament();
			
			Individual parent1 = roulette();
			Individual parent2 = roulette();

			// Generate a child by crossover. Not Implemented			
			ArrayList<Individual> children = reproduce(parent1, parent2);
			
			//mutate the offspring
			mutate(children);
			
			// Evaluate the children
			evaluateIndividuals(children);			

			// Replace children in population
			replace(children);

			// check to see if the best has improved
			best = getBest();
			
			// Implemented in NN class. 
			outputStats();
			
			//Increment number of completed generations			
		}

		//save the trained network to disk
		saveNeuralNetwork();
	}

	

	/**
	 * Sets the fitness of the individuals passed as parameters (whole population)
	 * 
	 */
	private void evaluateIndividuals(ArrayList<Individual> individuals) {
		for (Individual individual : individuals) {
			individual.fitness = Fitness.evaluate(individual, this);
		}
	}


	/**
	 * Returns a copy of the best individual in the population
	 * 
	 */
	private Individual getBest() {
		best = null;;
		for (Individual individual : population) {
			if (best == null) {
				best = individual.copy();
			} else if (individual.fitness < best.fitness) {
				best = individual.copy();
			}
		}
		return best;
	}

	/**
	 * Generates a randomly initialised population
	 * 
	 */
	private ArrayList<Individual> initialise() {
		population = new ArrayList<>();
		for (int i = 0; i < Parameters.popSize; ++i) {
			//chromosome weights are initialised randomly in the constructor
			Individual individual = new Individual();
			population.add(individual);
		}
		evaluateIndividuals(population);
		return population;
	}

	/**
	 * SELECTION
	 * 
	 * 
	/*private Individual select() {		
		Individual parent = population.get(Parameters.random.nextInt(Parameters.popSize));
		return parent.copy();
	} Previous version*/
	//Same as get best but to accept the list for the selection methodology
	private Individual getBestSelection(ArrayList<Individual> selection) {
		best = null;;
		for (Individual individual : population) {
			if (best == null) {
				best = individual.copy();
			} else if (individual.fitness < best.fitness) {
				best = individual.copy();
			}
		}
		return best;
	}
	
	//Selection - Tournament
	//https://www.geeksforgeeks.org/tournament-selection-ga/
	/*
	 * 1.Select individuals from population and test against each other
	 * 2.Get best individual from group
	 * 3.Repeat
	 * 4.Now have the best of the lot
	 * 
	 */
	private Individual tournament(){
		ArrayList<Individual> selection = new ArrayList<Individual>();
		Random random = new Random(); //pick individuals to become parents
		for(int i = 0; i < Parameters.popSize/6; i++){ //need to play with this in combo with other parameters
			selection.add(population.get(random.nextInt(Parameters.popSize)));
		}
		
		Individual best = new Individual();
		best = null;
		for(Individual individual : selection){
			if(best == null){ //best first in array
				best = individual.copy();
			}
			else if (individual.fitness < best.fitness){
				best = individual.copy();
			}
		}
		return best;
	}
	
	//Selection - Roulette
	//https://www.youtube.com/watch?v=9JzFcGdpT8E
	//https://stackoverflow.com/questions/298301/roulette-wheel-selection-algorithm
	/*
	 * 1.Calculate sum of fitness
	 * 2.Get random number between 0 -> sum
	 * 3.Loop through population, adding fitnesses to a comparison value
	 * 4.for the individual in which this comparison is larger than the random, they are chosen
	 * gambling yo
	 * 
	 */
	public static double positiveRandom(double sumOfFitness)
	{
		double random = (double) Math.random()*sumOfFitness;
		return random;
		//I'm dumb as **** dunno how to do this in java had to use a function
	}
	
	private Individual roulette()
	{
		double sumOfFitness = 0;
		Individual best = null; //blah blah needs to be initialised I hate java
		
		for(int i = 0; i < population.size(); i++)
		{
			sumOfFitness += population.get(i).fitness;
		}
		
		//get positive random number
		double random = 0;
		random = positiveRandom(sumOfFitness);
		
		double compare = 0;
		
		for(Individual individual : population)
		{
			compare += individual.fitness;
			
			if(compare < sumOfFitness) 
			{
				best = individual;
				return best;
			}
		}
		return best;
	}
	
	

	/**
	 * CROSSOVER / REPRODUCTION
	 * 
	 * 
	private ArrayList<Individual> reproduce(Individual parent1, Individual parent2) {
		ArrayList<Individual> children = new ArrayList<>();
		children.add(parent1.copy());
		children.add(parent2.copy());			
		return children;
	} Previous version*/
	
	//1pt - Week 3 lecture
	/*
	 * 1.Select random cut point in chromosome
	 * 2.Swap tails from parents
	 * If time permitting may attempt 2/n point
	 * 
	 */
	private ArrayList<Individual> reproduce(Individual parent1, Individual parent2) {
		ArrayList<Individual> children = new ArrayList<>();
		
		Individual child1 = new Individual();
		Individual child2 = new Individual();
		
		Random random = new Random();
		
		int cutPoint = random.nextInt(parent1.chromosome.length);
		
		for(int i = 0; i < cutPoint; i++){
			child1.chromosome[i] = parent1.chromosome[i];
			child2.chromosome[i] = parent2.chromosome[i];
			
			for(i = cutPoint; i < parent2.chromosome.length; i++){
				child1.chromosome[i] = parent2.chromosome[i];
				child2.chromosome[i] = parent1.chromosome[i];
			}
		}
		children.add(child1);
		children.add(child2);
		return children;
	}
	
	/**
	 * Mutation
	 * 
	 * 
	 */
	private void mutate(ArrayList<Individual> individuals) {		
		for(Individual individual : individuals) {
			for (int i = 0; i < individual.chromosome.length; i++) {
				if (Parameters.random.nextDouble() < Parameters.mutateRate) {
					if (Parameters.random.nextBoolean()) {
						individual.chromosome[i] += (Parameters.mutateChange);
					} else {
						individual.chromosome[i] -= (Parameters.mutateChange);
					}
				}
			}
		}		
	}

	/**
	 * 
	 * Replaces the worst member of the population 
	 * (regardless of fitness)
	 * 
	 */
	private void replace(ArrayList<Individual> individuals) {
		for(Individual individual : individuals) {
			int idx = getWorstIndex();		
			population.set(idx, individual);
		}		
	}

	

	/**
	 * Returns the index of the worst member of the population
	 * @return
	 */
	private int getWorstIndex() {
		Individual worst = null;
		int idx = -1;
		for (int i = 0; i < population.size(); i++) {
			Individual individual = population.get(i);
			if (worst == null) {
				worst = individual;
				idx = i;
			} else if (individual.fitness > worst.fitness) {
				worst = individual;
				idx = i; 
			}
		}
		return idx;
	}	

	@Override
	public double activationFunction(double x) {
		if (x < -20.0) {
			return -1.0;
		} else if (x > 20.0) {
			return 1.0;
		}
		return Math.tanh(x);
	}
}
