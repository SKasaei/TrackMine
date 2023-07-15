/* GeneticAlgorithm.java
 *
 * Topic Tracking in Model Mining using Genetic Algorithm.
 * Code is based on partially mapped crossover genetic algortihm.
 * https://github.com/SKasaei/TrackMine
 *
 * @author: Mohammad-Sajad Kasaei
 * @version: 1.1
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.eclipse.epsilon.ecl.EclModule;
import org.eclipse.epsilon.ecl.IEclModule;
import org.eclipse.epsilon.eol.models.IModel;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class GeneticAlgorithm {
	/*GA PARAMETERS*/
	private int MAX_LENGTH;                 // chess board width. or n in n queens
	private int START_SIZE;				    // Population size at start.
	private int MAX_EPOCHS;                 // Arbitrary number of test cycles.
	private double MATING_PROBABILITY;      // Probability of two chromosomes mating. Range: 0.0 < MATING_PROBABILITY < 1.0
	private double MUTATION_RATE;           // Mutation Rate. Range: 0.0 < MUTATION_RATE < 1.0
	private int MIN_SELECT;                 // Minimum parents allowed for selection.
	private int MAX_SELECT;                 // Maximum parents allowed for selection. Range: MIN_SELECT < MAX_SELECT < START_SIZE
	private int OFFSPRING_PER_GENERATION;   // New offspring created per generation. Range: 0 < OFFSPRING_PER_GENERATION < MAX_SELECT.
	private int MINIMUM_SHUFFLES;           // For randomizing starting chromosomes
	private int MAXIMUM_SHUFFLES;

	private int nextMutation;               // For scheduling mutations.
	private ArrayList<Chromosome> population;
	private ArrayList<Chromosome> solutions;
	private Random rand;
	private int childCount;
	private int mutations;
	private int epoch;
	private int populationSize;
	
	private String ECLFile;
	private List<IModel> InputModels;
	
	private LinkedList<Double> Best_Fit_Individuals = new LinkedList<Double>();
	private LinkedList<Double> Avg_Fit_Population = new LinkedList<Double>();

	/* Instantiates the genetic algorithm along with its parameters.
	 *
	 * @param: size of n queens
	 */
	public GeneticAlgorithm(int n) {
		MAX_LENGTH = n;
		START_SIZE = 50;
		MAX_EPOCHS = 2;
		MATING_PROBABILITY = 0.7;
		MUTATION_RATE = 0.001;
		MIN_SELECT = 40; 
		MAX_SELECT = 70;
		OFFSPRING_PER_GENERATION = 30;
		MINIMUM_SHUFFLES = 8; 
		MAXIMUM_SHUFFLES = 20;  
		epoch = 0;
		populationSize = 0;
	}

	/* Starts the genetic algorithm solving for n queens.
	 *
	 */
	public boolean algorithm(ArrayList<Chromosome> Generations) {
		population = new ArrayList<Chromosome>();
		solutions = new ArrayList<Chromosome>();
		rand = new Random();
		nextMutation = 0;
		childCount = 0;                 
		mutations = 0;
		epoch = 0;
		populationSize = 0;

		boolean done = false;
		Chromosome thisChromo = null;
		nextMutation = getRandomNumber(0, (int)Math.round(1.0 / MUTATION_RATE));
		/*

		if (Generations != null) {
			this.population = Generations;
			
			}else {
				initialize();
			}
*/
		if (Generations != null) {
			initialize();
			this.population.remove(0);
			this.population.add(Generations.get(0)); // add best inv to population
			}else {
				initialize();
			}
		
			populationSize = population.size();
		
			getFitness();
		
			rouletteSelection();

			mating();
			
			//prints the best solutions 
			population = prepNextGeneration();
			System.out.print("These are "+ population.size() *0.1 + "Best individuals in the population");
			for (int i=0 ; i < population.size() *0.1; i++ ) {
			this.solutions.add(population.get(i));
			thisChromo = population.get(i);
			printSolution(thisChromo);
			}

		System.out.println("Encountered " + mutations + " mutations in " + childCount + " offspring."); 
		
		return done;
	}

	/* Starts the mating process with the selected chromosomes.
	 *
	 */
	public void mating() {
		int getRand = 0;
        int parentA = 0;
        int parentB = 0;
        int newIndex1 = 0;
        int newIndex2 = 0;
        Chromosome newChromo1 = null;
        Chromosome newChromo2 = null;

        // In each generation 2 OFFSPRINGs are introduced
        for(int i = 0; i < OFFSPRING_PER_GENERATION; i++) {
            parentA = chooseParent();
            // Test probability of mating.
            getRand = getRandomNumber(0, 100);
            if(getRand <= MATING_PROBABILITY * 100) {
                parentB = chooseParent(parentA);
                newChromo1 = new Chromosome(MAX_LENGTH);
                newChromo2 = new Chromosome(MAX_LENGTH);
                population.add(newChromo1);
                newIndex1 = population.indexOf(newChromo1);
                population.add(newChromo2);
                newIndex2 = population.indexOf(newChromo2);
                
                // partiallyMappedCrossover (PMX)
                partiallyMappedCrossover(parentA, parentB, newIndex1, newIndex2);

                if(childCount - 1 == nextMutation) {
                    exchangeMutation(newIndex1, 1);
                } else if (childCount == nextMutation) {
                    exchangeMutation(newIndex2, 1);
                }

                childCount += 2;

                // Schedule next mutation.
                if(childCount % (int)Math.round(1.0 / MUTATION_RATE) == 0) {
                    nextMutation = childCount + getRandomNumber(0, (int)Math.round(1.0 / MUTATION_RATE));
                    //System.out.println("HYE   "+nextMutation);
                }
            }
        } // i
	}

	/* Crossovers two chromosome parents. Uses partiallyMappedCrossover (PMX) technique.
	 *
	 * @param: parent A
	 * @param: parent B
	 * @param: child A
	 * @param: child B
	 */
	public void partiallyMappedCrossover(int chromA, int chromB, int child1, int child2) {
        int j = 0;
        int item1 = 0;
        int item2 = 0;
        int pos1 = 0;
        int pos2 = 0;
        Chromosome thisChromo = population.get(chromA);
        Chromosome thatChromo = population.get(chromB);
        Chromosome newChromo1 = population.get(child1);
        Chromosome newChromo2 = population.get(child2);
        int crossPoint1 = getRandomNumber(0, MAX_LENGTH - 1);
        int crossPoint2 = getExclusiveRandomNumber(MAX_LENGTH - 1, crossPoint1);
        
        //gets the crosspoint from where to swap
        if(crossPoint2 < crossPoint1) {
            j = crossPoint1;
            crossPoint1 = crossPoint2;
            crossPoint2 = j;
        }

        // Copy Parent genes to offspring.
        for(int i = 0; i < MAX_LENGTH; i++) {
            newChromo1.setGene(i, thisChromo.getGene(i));
            newChromo2.setGene(i, thatChromo.getGene(i));
        }

        for(int i = crossPoint1; i <= crossPoint2; i++) {
            // Get the two items to swap.
            item1 = thisChromo.getGene(i);
            item2 = thatChromo.getGene(i);

            // Get the items//  positions in the offspring.
            for(j = 0; j < MAX_LENGTH; j++) {
                if(newChromo1.getGene(j) == item1) {
                    pos1 = j;
                } else if (newChromo1.getGene(j) == item2) {
                    pos2 = j;
                }
            } // j

            // Swap them.
            if(item1 != item2) {
                newChromo1.setGene(pos1, item2);
                newChromo1.setGene(pos2, item1);
            }

            // Get the items//  positions in the offspring.
            for(j = 0; j < MAX_LENGTH; j++) {
                if(newChromo2.getGene(j) == item2) {
                    pos1 = j;
                } else if(newChromo2.getGene(j) == item1) {
                    pos2 = j;
                }
            } // j

            // Swap them.
            if(item1 != item2) {
                newChromo2.setGene(pos1, item1);
                newChromo2.setGene(pos2, item2);
            }

        } // i
	}

	/* Chooses a randomly selected parent.
	 *
	 * @return: random index of parent
	 */
	public int chooseParent() {
    	// Overloaded function, see also "chooseparent(ByVal parentA As Integer)".
        int parent = 0;
        Chromosome thisChromo = null;
        boolean done = false;

        while(!done) {
            // Randomly choose an eligible parent.
            parent = getRandomNumber(0, population.size() - 1);
            thisChromo = population.get(parent);
            if(thisChromo.isSelected() == true) {
                done = true;
            }
        }

        return parent;    	
    }    
    
    /* Chooses a randomly selected parent which is not the parameter.
	 *
	 * @param: selected parent index
	 * @return: random index of parent
	 */
    public int chooseParent(int parentA) {
        // Overloaded function, see also "chooseparent()".
        int parent = 0;
        Chromosome thisChromo = null;
        boolean done = false;

        while(!done) {
            // Randomly choose an eligible parent.
            parent = getRandomNumber(0, population.size() - 1);
            if(parent != parentA){
                thisChromo = population.get(parent);
                if(thisChromo.isSelected() == true){
                    done = true;
                }
            }
        }

        return parent;    	
    } 

	/* Chooses selected parents based on roulette selection.
	 *
	 */
	public void rouletteSelection() {
   	 	int j = 0;
        int populationSize = population.size();
        int maximumToSelect = getRandomNumber(MIN_SELECT, MAX_SELECT);
        double genTotal = 0.0;
        double selTotal = 0.0;
        double rouletteSpin = 0.0;
        Chromosome thisChromo = null;
        Chromosome thatChromo = null;
        boolean done = false;
        
        // sum of the fitnesses (fi)
        for(int i = 0; i < populationSize; i++) {												//get total fitness
            thisChromo = population.get(i);
            genTotal += thisChromo.getFitness();
        }
        
        genTotal *= 0.01;															

        // divide =>  pi = fi/sigma fi
        for(int i = 0; i < populationSize; i++) {
            thisChromo = population.get(i);
            thisChromo.setSelectionProbability(thisChromo.getFitness() / genTotal);		//set selection probability. the more fit the better selection probability
        }
        
        for(int i = 0; i < maximumToSelect; i++) {										//selects parents
            rouletteSpin = getRandomNumber(0, 99);
            j = 0;
            selTotal = 0;
            done = false;
            while(!done) {
                thisChromo = population.get(j);
                selTotal += thisChromo.getSelectionProbability();
                if(selTotal >= rouletteSpin) {
					 if(j == 0) {
					    thatChromo = population.get(j);
					 } else if(j >= populationSize - 1) {
					     thatChromo = population.get(populationSize - 1);
					 } else {
					     thatChromo = population.get(j-1);
					 }
					thatChromo.setSelected(true);
					done = true;
                } else {
                    j++;
                }
            }
        }
	}

	/* Sets the fitness of each chromosome based on its conflicts
	 *
	 */
	public void getFitness() {
		// Lowest errors = 100%, Highest errors = 0%
		int populationSize = population.size();
		Chromosome thisChromo = null;
	//	double bestScore = 0;
	//	double worstScore = 0;

		// The worst score would be the one with the highest energy, best would be lowest.
		//worstScore = Collections.max(population).getConflicts();

		// Convert to a weighted percentage.
	//	bestScore = worstScore - Collections.min(population).getConflicts();
		
		for(int i = 0; i < populationSize; i++) {
			double totalMatches = 0;
			double TrueMatches = 0;
			double IndScore = 0;
			double temp = 0;
			int fitWeightCount = 1;
			thisChromo = population.get(i);
			for(int j=0; j < thisChromo.getMaxLength()-1; j++) {
				try {
					IEclModule eclModule = new EclModule();
					eclModule.parse(new File(ECLFile));
					InputModels.get(thisChromo.getGene(j)).getAliases().remove(0);
					InputModels.get(thisChromo.getGene(j)).getAliases().add("In");
					InputModels.get(thisChromo.getGene(j+1)).getAliases().remove(0);
					InputModels.get(thisChromo.getGene(j+1)).getAliases().add("Out");
					eclModule.getContext().getModelRepository().addModels(InputModels.get(thisChromo.getGene(j)),InputModels.get(thisChromo.getGene(j+1)));
					eclModule.execute();
					totalMatches = eclModule.getContext().getMatchTrace().size();
					TrueMatches = eclModule.getContext().getMatchTrace().getReduced().size();
				
					//if (j == 0) {
						IndScore += TrueMatches/totalMatches; 
					//	}
					//else {
					//IndScore += TrueMatches/(totalMatches * Math.abs((temp - (TrueMatches/totalMatches))));
					//}
					if ( (temp >= 0.3) && ( (TrueMatches/totalMatches) >= 0.3 ) ){
						fitWeightCount++;
					}
					temp = TrueMatches/totalMatches;

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			double fitWeight = 0.9 * fitWeightCount;
			IndScore = IndScore * fitWeight;
			
			thisChromo.setFitness(IndScore);
		}   
	}

	/* Resets all flags in the selection
	 *
	 */ 
	public  ArrayList<Chromosome> prepNextGeneration() {
		ArrayList<Chromosome> generation = new ArrayList<Chromosome>();
		Chromosome thisChromo = null;
		Chromosome thatChromo = null;

		double thisFitness = 0;
		double thatFitness = 0;
		int thisIndex = 0;
		double arr[];
		arr = new double[population.size()];
		
		  for(int i = 0; i < population.size(); i++) {												//get total fitness
	            thisChromo = population.get(i);
	            thisFitness = thisChromo.getFitness();
	            thisIndex = population.indexOf(thisChromo);
	            arr[i] = thisFitness;
	        }
		  Arrays.sort(arr);

		  for (int i = 0, j = arr.length - 1; i < j; i++, j--) {
			  double tmp = arr[i];
	            arr[i] = arr[j];
	            arr[j] = tmp;
	        }
		  
		  // population size = START_SIZE 
		  for(int i = 0; i < START_SIZE; i++) {	
			  
			  thatFitness = arr[i];
			  for(int j = 0 ; j < population.size(); j++) {
				  thatChromo = population.get(j);
				  if ( (thatChromo.getFitness() == thatFitness) && (population.get(j).getSelectionGeneration()==false))
				  {
					  population.get(j).setSelectedGeneration(true);
					  generation.add(thatChromo);
					  break;
				  }
			  }
		  }
		  for(int i = 0; i < generation.size(); i++) {	
			  generation.get(i).setSelectedGeneration(false);
		  }
		
return generation;
	}

	/* Prints the nxn board with the queens
	 *
	 * @param: a chromosome
	 */ 
	public void printSolution(Chromosome solution) {
		String board[][] = new String[1][MAX_LENGTH];

		// Clear the board.
		for(int x = 0; x < MAX_LENGTH; x++) {
			board[0][x] = "";
		}

		for(int x = 0; x < MAX_LENGTH; x++) {
			board[0][x] = InputModels.get(population.get(0).getGene(x)).getName();
		}
		
		// Display the board.
		System.out.println("Arrangement of models:");
		for(int y = 0; y < MAX_LENGTH; y++) {
			System.out.print(board[0][y]);
			if (y == MAX_LENGTH-1) {System.out.print("\n");}else {
			System.out.print("<->");
			}
		}
	}

	/* Initializes all of the chromosomes' placement of queens in ramdom positions.
	 *
	 */ 
	public void initialize() {
		int shuffles = 0;
		Chromosome newChromo = null;
		int chromoIndex = 0;

		for(int i = 0; i < START_SIZE; i++)  {
			newChromo = new Chromosome(MAX_LENGTH);
			population.add(newChromo);
			chromoIndex = population.indexOf(newChromo);

			// Randomly choose the number of shuffles to perform.
			shuffles = getRandomNumber(MINIMUM_SHUFFLES, MAXIMUM_SHUFFLES);
			exchangeMutation(chromoIndex, shuffles);
			
			//CHANGE!!!!!!!!!!!!!!!!!!!!
			//population.get(chromoIndex).computeConflicts();
		}
	}

	/* Changes the position of the queens in a chromosome randomly according to the number of exchanges
	 *
	 * @param: index of the chromosome
	 * @param: number of exhanges
	 */ 
	public void exchangeMutation(int index, int exchanges) {
		int tempData = 0;
		int gene1 = 0;
		int gene2 = 0;
		Chromosome thisChromo = null;
		thisChromo = population.get(index);

		for(int i = 0; i < exchanges; i++) {
			gene1 = getRandomNumber(0, MAX_LENGTH - 1);
			gene2 = getExclusiveRandomNumber(MAX_LENGTH - 1, gene1);

			// Exchange the chosen genes.
			tempData = thisChromo.getGene(gene1);
			thisChromo.setGene(gene1, thisChromo.getGene(gene2));
			thisChromo.setGene(gene2, tempData);
		}
		mutations++;
	}

	/* Gets a random number with the exception of the parameter
	 *
	 * @param: the maximum random number
	 * @param: number to to be chosen
	 * @return: random number
	 */ 
	public int getExclusiveRandomNumber(int high, int except) {
		boolean done = false;
		int getRand = 0;

		while(!done) {
			getRand = rand.nextInt(high);
			if(getRand != except){
				done = true;
			}
		}
		return getRand;  
	}

	/* Gets a random number in the range of the parameters
	 *
	 * @param: the minimum random number
	 * @param: the maximum random number
	 * @return: random number
	 */ 
	public int getRandomNumber(int low, int high) {
   		return (int)Math.round((high - low) * rand.nextDouble() + low);
	}
   /* gets the solutions
	 *
	 * @return: solutions
	 */  
	public ArrayList<Chromosome> getSolutions() {
		return solutions;
	}
	
	/* gets the epoch
	 *
	 * @return: epoch
	 */ 
	public int getEpoch() {
		return epoch;
	}
	
	/* gets the population size
	 *
	 * @return: pop size
	 */ 
	public int getPopSize() {
		return population.size();
	}
	
	/* gets the start size
	 *
	 * @return: start size
	 */ 
	public int getStartSize() {
		return START_SIZE;
	}
	
	/* gets the mating prob
	 *
	 * @return: mating prob
	 */ 
	public double getMatingProb() {
		return MATING_PROBABILITY;
	}
	
	/* gets the mutation rate
	 *
	 * @return: mutation rate
	 */ 
	public double getMutationRate() {
		return MUTATION_RATE;
	}
	
	/* gets the start size
	 *
	 * @return: start size
	 */ 
	public int getMinSelect() {
		return MIN_SELECT;
	}
	
	/* gets the mating prob
	 *
	 * @return: mating prob
	 */ 
	public double getMaxSelect() {
		return MAX_SELECT;
	}
	
	/* gets the mutation rate
	 *
	 * @return: mutation rate
	 */ 
	public double getOffspring() {
		return OFFSPRING_PER_GENERATION;
	}
	
	/* gets the max epoch
	 *
	 * @return: max epoch
	 */ 
	public int getMaxEpoch() {
		return MAX_EPOCHS;
	}

	/* gets the min shuffle
	 *
	 * @return: min shuffle
	 */ 
	public int getShuffleMin() {
		return MINIMUM_SHUFFLES;
	}

	/* gets the max shuffle
	 *
	 * @return: max shuffle
	 */ 
	public int getShuffleMax() {
		return MAXIMUM_SHUFFLES;
	}

	/* sets the mutation rate
	 *
	 * @param: new mutation rate value
	 */ 
	public void setMutation(double newMutation) {
		this.MUTATION_RATE = newMutation;
	}

	/* sets the new max epoch
	 *
	 * @param: new max epoch value
	 */ 
	public void setEpoch(int newMaxEpoch) {
		this.MAX_EPOCHS = newMaxEpoch;
	}
	
	
	public double getAvgFitPopulation() {
		
        int populationSize = population.size();
        double genTotal = 0.0;
        Chromosome thisChromo = null;
        double AvgFitPopulation = 0.0;
        
		  // sum of the fitnesses (fi)
        for(int i = 0; i < populationSize; i++) {												//get total fitness
            thisChromo = population.get(i);
            genTotal += thisChromo.getFitness();
        }
        AvgFitPopulation = genTotal/populationSize;
        return AvgFitPopulation;
	}
	
	public double getBestFitIndividuals() {
		
		ArrayList<Chromosome> generation = new ArrayList<Chromosome>();
		Chromosome thisChromo = null;

		double thisFitness = 0;
		int thisIndex = 0;
		double arr[];
		arr = new double[population.size()];
		
		  for(int i = 0; i < population.size(); i++) {												//get total fitness
	            thisChromo = population.get(i);
	            thisFitness = thisChromo.getFitness();
	            thisIndex = population.indexOf(thisChromo);
	            arr[i] = thisFitness;
	        }
		  Arrays.sort(arr);

		  for (int i = 0, j = arr.length - 1; i < j; i++, j--) {
			  double tmp = arr[i];
	            arr[i] = arr[j];
	            arr[j] = tmp;
	        }
		  
        return arr[0];
	}

	/* sets the ECL file
	 *
	 * @param: new max epoch value
	 */ 
	public void setECLFile(String Myfile) {
		this.ECLFile = Myfile;
	}
	
	public void setInputModels(List<IModel> InputM) {
		this.InputModels = InputM;
	}
}