/* Chromosome.java
 *
 * Chromosome class used by GeneticAlgorithm.java
 * Contains the positions of the queens in a solution as well as its conflicts, fitness and selection probability. 
 * Base code at https://github.com/jimsquirt/JAVA-GA
 *
 * @author: Mohammad-Sajad Kasaei
 * @version: 1.1
 */

public class Chromosome {
	private int MAX_LENGTH; 					//n size
	private int[] gene; 						//contains the location of each queen
	private double fitness;						//the fitness of this chromosome towards the solution
	private boolean selected; 					//if selected for mating
	private double selectionProbability; 		//probabiblity of beaing selected for mating in roulette
	
	private boolean selectionByGeneration;
	/* Instantiate the chromosome.
	 *
	 * @param: size of n
	 */
	public Chromosome(int n) {
		MAX_LENGTH = n;
		gene = new int[MAX_LENGTH];
		fitness = 0.0;
		selected = false;
		selectionProbability = 0.0;
		selectionByGeneration = false;
		
		initChromosome();
	}

	/* Plots the queens in the board.
	 *
	 * @param: a nxn board
	 */
	public void plotQueens(String[][] board) {
        for(int i = 0; i < MAX_LENGTH; i++) {
            board[i][gene[i]] = "Q";
        }
	}

	/* Clears the board.
	 *
	 * @param: a nxn board
	 */
	public void clearBoard(String[][] board) {
		for (int i = 0; i < MAX_LENGTH; i++) {
			for (int j = 0; j < MAX_LENGTH; j++) {
				board[i][j] = "";
			}
		}
	}

	/* Initializes the chromosome into diagonal queens.
	 *
	 */
	public void initChromosome() {
		for(int i = 0; i < MAX_LENGTH; i++) {
			gene[i] = i;
		}
	}
	
	/* Gets the gene/data on a specified index.
	 *
	 * @param: index of data
	 * @return: position of queen
	 */
	public int getGene(int index) {
		return gene[index];
	}

	/* Sets the gene/data on a specified index.
	 *
	 * @param: index of data
	 * @param: new position of queen
	 */
	public void setGene(int index, int position) {
		this.gene[index] = position;
	}
	
	/* Gets the fitness of a chromosome.
	 *
	 * @return: fitness of chromosome
	 */
	public double getFitness() {
		return fitness;
	}
	
	/* Sets the fitness of the chromosome.
	 *
	 * @param: new fitness
	 */
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	/* Gets whether the chromosome is selected.
	 *
	 * @return: boolean value if slected
	 */
	public boolean isSelected() {
		return selected;
	}
	
	/* Gets whether the chromosome is selected.
	 *
	 * @param: boolean value if slected
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public void setSelectedGeneration(boolean selected) {
		this.selectionByGeneration = selected;
	}
	
	/* Gets the selection probability of the chromosome.
	 *
	 * @return: selection probability of the chromosome
	 */
	public double getSelectionProbability() {
		return selectionProbability;
	}
	
	public boolean getSelectionGeneration() {
		return selectionByGeneration;
	}
	
	/* sets the selection probability of the chromosome.
	 *
	 * @param: new selection probability of the chromosome
	 */
	public void setSelectionProbability(double selectionProbability) {
		this.selectionProbability = selectionProbability;
	}
	
	 /* Gets the max length.
	 *
	 * @return: max length
	 */
	public int getMaxLength() {
	   return MAX_LENGTH;
	}
}