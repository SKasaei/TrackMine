import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.emc.plainxml.PlainXmlModel;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.models.IRelativePathResolver;
import org.eclipse.uml2.uml.UMLPackage;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

/* Main.java
 *
 * Runs GeneticAlgorithm.java and logs the results into a file using Writer.java.
 * GA testing setup is according to pass/fail criteria
 * Pass criteria - 50 success
 * Fail criteria - 100 failures
 *
 * @author: Mohammad-Sajad Kasaei
 * @version: 1.1
 */

public class Main {
	Writer logWriter;
	GeneticAlgorithm ga;
	int MAX_RUN;
	int MAX_LENGTH;
	long[] runtimes;
	
	private LinkedList<Double> Best_Fit_Individuals = new LinkedList<Double>();
	private LinkedList<Double> Avg_Fit_Population = new LinkedList<Double>();


	private ArrayList<Chromosome> Generations;
	/* Instantiates the TesterGA class
	 *
	 */
	public Main() {
		logWriter = new Writer();
		MAX_RUN = 100;
		runtimes = new long[MAX_RUN];
		Generations = null;
	}

	/* Test method accepts the N/max length, and parameters mutation rate and max epoch to set for the GA accordingly.
	 *
	 * @param: max length/n
	 * Number of queens
	 * @param: mutation rate for GA
	 * Mutation probability is a parameter in a genetic algorithm that determines the likelihood that an individual will undergo the mutation process.
	 * @param: max epoch for GA
	 * The epoch operator replaces the entire population with a set of new random structures if the population stagnates.
	 */
	public void test(int maxLength, double mutationRate, int maxEpoch, String MyECLfile, List<IModel> InputModels) {
		MAX_LENGTH = maxLength;
		ga = new GeneticAlgorithm(MAX_LENGTH);										//define ga here
		ga.setMutation(mutationRate);
		ga.setEpoch(maxEpoch);
		ga.setECLFile(MyECLfile);
		ga.setInputModels(InputModels);
		long testStart = System.nanoTime();
		String filepath = "GA-N"+MAX_LENGTH+"-"+mutationRate+"-"+maxEpoch+".txt";
		long startTime = 0;
        long endTime = 0;
        long totalTime = 0;
        int fail = 0;
        int success = 0;
        
		logParameters();
		
		
        
        for(int i = 0; i < MAX_RUN; i++) {												//run 50 sucess to pass passing criteria
        	startTime = System.nanoTime();
        	
        	if (i != 0) {
        		Generations = ga.prepNextGeneration();
        		Best_Fit_Individuals.add(Generations.get(0).getFitness());
        		Avg_Fit_Population.add(getAvgFitPopulation());
        	}
        	
        		ga.algorithm(Generations);
        		endTime = System.nanoTime();
        		totalTime = endTime - startTime;
        		
        		System.out.println("Done");
        		System.out.println("run "+(i+1));
            	System.out.println("time in nanoseconds: "+totalTime);
            	System.out.println("Success!");
            	
            	runtimes[i] = totalTime;
            	
            	success++;
            	
            	//write to log
            	logWriter.add((String)("Run: "+i));
            	logWriter.add((String)("Runtime in nanoseconds: "+totalTime));
            	//logWriter.add((String)("Found at epoch: "+ga.getEpoch()));
            	logWriter.add((String)("Population size: "+ga.getPopSize()));
            	logWriter.add("");
            
				for(Chromosome c: ga.getSolutions()) {								//write solutions to log file
					logWriter.add(c , InputModels);
					logWriter.add("");
    			}
    			
        	startTime = 0;															//reset time
        	endTime = 0;
        	totalTime = 0;
        }
        drawAvgFitPopulation();
        logWriter.add("Runtime summary");
        logWriter.add("");
        
		for(int x = 0; x < runtimes.length; x++){									//print runtime summary
			logWriter.add(Long.toString(runtimes[x]));
		}
		
		long testEnd = System.nanoTime();
		logWriter.add(Long.toString(testStart));
		logWriter.add(Long.toString(testEnd));
		logWriter.add(Long.toString(testEnd - testStart));
		
		logWriter.add("Best_Fit_Individuals: " + Best_Fit_Individuals.toString());
		logWriter.add("Avg_Fit_Population: " + Avg_Fit_Population.toString());
       	logWriter.writeFile(filepath);
       	printRuntimes();
       	
	}

	/* Converts the parameters of GA to string and adds it to the string list in the writer class
	 *
	 */
	public void logParameters() {
        logWriter.add("Genetic Algorithm");
        logWriter.add("Parameters");
        logWriter.add((String)("MAX_LENGTH/N: "+MAX_LENGTH));
        logWriter.add((String)("STARTING_POPULATION: "+ga.getStartSize()));
        logWriter.add((String)("MAX_EPOCHS: "+ga.getMaxEpoch()));
        logWriter.add((String)("MATING_PROBABILITY: "+ga.getMatingProb()));
        logWriter.add((String)("MUTATION_RATE: "+ga.getMutationRate()));
        logWriter.add((String)("MIN_SELECTED_PARENTS: "+ga.getMinSelect()));
        logWriter.add((String)("MAX_SELECTED_PARENTS: "+ga.getMaxSelect()));
        logWriter.add((String)("OFFSPRING_PER_GENERATION: "+ga.getOffspring()));
        logWriter.add((String)("MINIMUM_SHUFFLES: "+ga.getShuffleMin()));
        logWriter.add((String)("MAXIMUM_SHUFFLES: "+ga.getShuffleMax()));
        logWriter.add("");
	}

	/* Prints the runtime summary in the console
	 *
	 */
	public void printRuntimes() {
		for(long x: runtimes){
			System.out.println("run with time "+x+" nanoseconds");
		}	
	}

	public static void main(String args[]) throws Exception {
		
		System.out.print("JAVA code implementation for the Algorithm.");
		System.out.print("\n");
		System.out.print("Writen by Mohammad-Sajad Kasaei");
		System.out.print("\n");
		System.out.print("=========================================");
		System.out.print("==========================================");
		System.out.print("\n");
		
		Main tester = new Main();
		
		// Enter input models
	    System.out.println("Please enter input models respectively.");
	    System.out.print("\n");
	    System.out.print("==========================================");
		System.out.print("\n");
	    int i = 1;
	    String finish = "n";
	    Scanner myObj = new Scanner(System.in);
	    System.out.println("Enter name of meta model:"); 
	    List<IModel> InputModels = new ArrayList<IModel>();
	    
	    while (finish.equals("n")) {
		    System.out.println("Model number:" + i);
		    System.out.println("Enter name of model:"); 
		    String Mname = myObj.nextLine();
		    try {
		    	// XML
		    	// InputModels.add(tester.loadXMLmodel(Mname));
		    	InputModels.add(tester.loadUMLmodel(Mname));
			} catch (EolModelLoadingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    System.out.println("Finish?(y/n):"); 
		    finish = myObj.nextLine();
		    i++;
	    }
	    
	    
	   // int NumberEpoch = Integer.valueOf(NumberofEpoch); string to int
	    // 4 = queen , 3 = epoch
	    String MyECLfile="C:\\Users\\Admin\\eclipse\\eclipse photon\\workspace\\org.eclipse.algorithm.TTM-GA\\ModelRep\\Compare_UML.ecl";
	    
		tester.test(InputModels.size(), 0.001, 3, MyECLfile, InputModels);
		
	}
		
public double getAvgFitPopulation() {
		
        int populationSize = Generations.size();
        double genTotal = 0.0;
        Chromosome thisChromo = null;
        double AvgFitPopulation = 0.0;
        
		  // sum of the fitnesses (fi)
        for(int i = 0; i < populationSize; i++) {												//get total fitness
            thisChromo = Generations.get(i);
            genTotal += thisChromo.getFitness();
        }
        AvgFitPopulation = genTotal/populationSize;
        return AvgFitPopulation;
	}

public void drawAvgFitPopulation() {
	System.out.print("\n");       
	System.out.print(Best_Fit_Individuals);
	System.out.print("\n");
	System.out.print(Avg_Fit_Population);
	System.out.print("\n");

}

public LinkedList<Double> getAvgFitPopulationCHART() {
    return Avg_Fit_Population;
}

public LinkedList<Double> getBestFitPopulationCHART() {
    return Best_Fit_Individuals;
}

// load emf model
public IModel loadEMFmodel(String MMname, String Mname) throws EolModelLoadingException {
	String MMURI="C:\\Users\\Admin\\eclipse\\eclipse photon\\workspace\\org.eclipse.algorithm.TTM-GA\\ModelRep\\simpleOO.ecore";
	String MURI="C:\\Users\\Admin\\eclipse\\eclipse photon\\workspace\\org.eclipse.algorithm.TTM-GA\\ModelRep\\u.model";
	EmfModel EMFModel = new EmfModel();
	EMFModel.setName(Mname);
	EMFModel.getAliases().add("In");
	EMFModel.setMetamodelFile(MMURI);
	EMFModel.setModelFile(MURI);
	EMFModel.load();
	return EMFModel;
}

//load XML model
public IModel loadXMLmodel(String Mname) throws EolModelLoadingException {
	PlainXmlModel catalogue = new PlainXmlModel();
	catalogue.setName(Mname);
	catalogue.getAliases().add("In");
	catalogue.setFile(new File("C:\\Users\\\\Admin\\eclipse\\eclipse photon\\workspace\\org.eclipse.algorithm.TTM-GA\\ModelRep\\"+Mname+".xml"));
	catalogue.load();
	return catalogue;
}

//load UML model
public IModel loadUMLmodel(String Mname) throws EolModelLoadingException, Exception {
	EPackage.Registry.INSTANCE.put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
	System.err.println(UMLPackage.eNS_URI);
	List<IModel> modelUML = new ArrayList<IModel>();
	modelUML.add(createEmfModelByURI(Mname, Mname+".uml", UMLPackage.eNS_URI, true, true));
	modelUML.get(0).getAliases().add("In");
	return modelUML.get(0);
}

//*
		protected static EmfModel createEmfModelByURI(String name, String model, 
				String metamodel, boolean readOnLoad, boolean storeOnDisposal) 
						throws EolModelLoadingException, URISyntaxException {
			EmfModel emfModel = new EmfModel();
			StringProperties properties = new StringProperties();
			properties.put(EmfModel.PROPERTY_NAME, name);
			properties.put(EmfModel.PROPERTY_METAMODEL_URI, metamodel);
			properties.put(EmfModel.PROPERTY_MODEL_URI, 
					getFileURI(model));
			properties.put(EmfModel.PROPERTY_READONLOAD, readOnLoad + "");
			properties.put(EmfModel.PROPERTY_STOREONDISPOSAL, 
					storeOnDisposal + "");
			emfModel.load(properties, (IRelativePathResolver) null);
			
			return emfModel;
		}
		protected static java.net.URI getFileURI(String fileName) throws URISyntaxException {
			String ModelFile="C:\\Users\\Admin\\eclipse\\eclipse photon\\workspace\\org.eclipse.algorithm.TTM-GA\\ModelRep\\"+fileName;
			return new File(ModelFile).toURI();
			}
		//*/
		
}
