/* LineChart.java
 *
 * Topic Tracking in Model Mining using Genetic Algorithm.
 * Drawing Line chart for the project.
 * https://github.com/SKasaei/TrackMine
 *
 * @author: Mohammad-Sajad Kasaei
 * @version: 1.1
 */

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Scanner;
import java.awt.event.ActionEvent;

/**
 * This program demonstrates how to draw line chart with CategoryDataset
 * using JFreechart library.
 * @author www.codejava.net
 *
 */
public class LineChart extends JFrame {
	
	private LinkedList<Double> Best_Fit_Individuals = new LinkedList<Double>();
	private LinkedList<Double> Avg_Fit_Population = new LinkedList<Double>();
 
    public LineChart(LinkedList<Double> BFI , LinkedList<Double> AFP) {
        super("Line Chart For TTMM-GA");
        
    	Best_Fit_Individuals = BFI;
    	Avg_Fit_Population = AFP;
        
        JPanel chartPanel = createChartPanel();
        getContentPane().add(chartPanel, BorderLayout.CENTER);
        
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
 
    private JPanel createChartPanel() {
        String chartTitle = "TTMM-GA Algorithm";
        String categoryAxisLabel = "Generations";
        String valueAxisLabel = "Fitness Popularity";
     
        CategoryDataset dataset = createDataset();
     
        JFreeChart chart = ChartFactory.createLineChart(chartTitle,
                categoryAxisLabel, valueAxisLabel, dataset);
        CategoryPlot plot = chart.getCategoryPlot();
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        
     // sets paint color for each series
     renderer.setSeriesPaint(0, Color.YELLOW);
     renderer.setSeriesPaint(1, Color.CYAN);
     renderer.setSeriesPaint(2, Color.BLUE);
     renderer.setSeriesPaint(3, Color.YELLOW);
      
     // sets thickness for series (using strokes)
     renderer.setSeriesStroke(0, new BasicStroke(4.0f));
     renderer.setSeriesStroke(1, new BasicStroke(3.0f));
     renderer.setSeriesStroke(2, new BasicStroke(2.0f));
     renderer.setSeriesStroke(3, new BasicStroke(1.5f));
      
     plot.setRenderer(renderer);
     
        return new ChartPanel(chart);
    }
 
    private CategoryDataset createDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String series1 = "Avg_Fit_Population";
        String series2 = "Best_Fit_Individuals";
       
        for(int i = 0; i<Avg_Fit_Population.size();i++)
        {
        	 dataset.addValue(Avg_Fit_Population.get(i), series1, Integer.toString(i));
        }
     
        for(int i = 0; i<Best_Fit_Individuals.size();i++)
        {
        	 dataset.addValue(Best_Fit_Individuals.get(i), series2, Integer.toString(i));
        }
     
        return dataset;
    }
 
 
   
}