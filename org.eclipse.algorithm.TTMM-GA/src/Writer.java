/* Writer.java
 *
 * Class that contains a string list to be written in a log file.
 * @author: Mohammad-Sajad Kasaei
 * @version: 1.1
 */

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.epsilon.eol.models.IModel;

public class Writer {
	private ArrayList<String> list;
	
	/* Instantiates the writer class.
	 *
	 */
	public Writer() {
		list = new ArrayList<String>();
	}

	/* Accepts a string to add to the string list in the writer class.
	 *
	 * @param: a line string to write into the log
	 */
	public void add(String line) {
		list.add(line);
	}
	
	/* Accepts a chromosome and converts the content solution into strings then adds it to the string list.
	 *
	 * @param: a chromosome to write into the log
	 */
	public void add(Chromosome c, List<IModel> InputModels) {
		int n = c.getMaxLength();
		String board[] = new String[n];

		clearBoard(board, n);

		for(int x = 0; x < n; x++) {
			board[x] = InputModels.get(c.getGene(x)).getName();
		}

		printBoard(board, n);
	}
	
	/* Clears a 2D string board with empty string.
	 *
	 * @param: a 2D string board
	 * @param: length of n
	 */
	public void clearBoard(String[] board, int n) {
		// Clear the board.
		for(int x = 0; x < n; x++) {
				board[x] = "";
		}
	}
	
	/* Replaces the position of the queens with Q in the string board and a dot for indexes with no queens.
	 *
	 * @param: a 2D string board
	 * @param: length of n
	 */
	public void printBoard(String[] board, int n) {
		// Display the board.
		for(int y = 0; y < n; y++) {
			list.add(board[y]);
		}
	}
	
	/* Writes the string list into a log file.
	 *
	 * @param: a string filename
	 */
	public void writeFile(String filename) {
		try{
        	FileWriter fw = new FileWriter(filename);
			BufferedWriter bw = new BufferedWriter(fw);
		
			for(int i = 0; i < list.size(); i++) {
				bw.write(list.get(i));
				bw.newLine();
				bw.flush();
			}

			bw.close();
        } catch (IOException e) {
        	System.out.println("Writing failed");
        }
		
	}
}
