import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.models.IModel;

import javax.swing.JButton;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;

public class MainGA extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainGA frame = new MainGA();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainGA() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 424, 208);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel_1 = new JLabel("Welcome to the Execution of TTMM-GA");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 22));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setVerticalAlignment(SwingConstants.TOP);
		lblNewLabel_1.setBounds(0, 49, 408, 32);
		contentPane.add(lblNewLabel_1);
		
		JButton btnNewButton = new JButton("Run Algorithm");
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				System.out.print("JAVA code implementation for the Algorithm.");
				System.out.print("\n");
				System.out.print("Writen by Mohammad-Sajad Kasaei");
				System.out.print("\n");
				System.out.print("=========================================");
				System.out.print("==========================================");
				System.out.print("\n");
				
				Main tester = new Main();
				List<String> NameOfModels = new ArrayList<String>();
				
				for(int hh = 1 ; hh < 10 ; hh++) {
					NameOfModels.add("h"+hh);
				}
				for(int pp = 1 ; pp < 15 ; pp++) {
					NameOfModels.add("p"+pp);
				}
				
				for(int ww = 1 ; ww < 18 ; ww++) {
					NameOfModels.add("w"+ww);
				}
				for(int bb = 1 ; bb < 16 ; bb++) {
					NameOfModels.add("b"+bb);
				}
				
				
				
				
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
				    System.out.println(NameOfModels.get(i-1));
				  //  String Mname = myObj.nextLine();
				  
				    	// XML
				    	// InputModels.add(tester.loadXMLmodel(Mname));
				    	try {
							InputModels.add(tester.loadUMLmodel(NameOfModels.get(i-1)));
						} catch (EolModelLoadingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
				   // System.out.println("Finish?(y/n):"); 
				   // finish = myObj.nextLine();
				    	if (i == 40) 
				    		{
				    		finish = "y";
				    		}
				    i++;
			    }
			    
			    
			   // int NumberEpoch = Integer.valueOf(NumberofEpoch); string to int
			    // 4 = queen , 3 = epoch
			    String MyECLfile="C:\\Users\\Admin\\eclipse\\eclipse photon\\workspace\\org.eclipse.algorithm.TTM-GA\\ModelRep\\Compare_UML.ecl";
			    
				tester.test(InputModels.size(), 0.001, 3, MyECLfile, InputModels);
				LineChart frame = new LineChart(tester.getBestFitPopulationCHART(), tester.getAvgFitPopulationCHART());
				frame.setVisible(true);
				
			}
		});
		btnNewButton.setBounds(10, 126, 388, 32);
		contentPane.add(btnNewButton);
		
		
	}
}
