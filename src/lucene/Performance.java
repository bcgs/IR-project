package lucene;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Performance {

	public static void main(String[] args) {
		
		// Get 4 bidirectional matrixes from log file. 1 from each source.
		String file = "log.txt";
		int counterRow = 0;
		int[][] matrix = new int[1][1];
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			List<List<String>> stringListList = new ArrayList<List<String>>();
			String currentQuery;
			
			while((currentQuery = br.readLine()) != null) {
				if(currentQuery != null) {
					String[] query = currentQuery.split(" ");
					stringListList.add(Arrays.asList(query));
				}
			}
			matrix = Arrays.copyOf(matrix, stringListList.size());
			
			for (List<String> stringList : stringListList) {
				
				int[] newArray = new int[stringList.size()];
				
                for (int i = 0; i < stringList.size(); i++) {
                    newArray[i] = Integer.valueOf(stringList.get(i));
                }
	            matrix[counterRow] = newArray;
                counterRow++;
			}
			
			// Matrix contains all the 4 sources needed:  \\
			//   matrix[0], matrix[1] and matrix[2] => 00 \\
			//   matrix[3], matrix[4] and matrix[5] => 01 \\
			//   matrix[6], matrix[7] and matrix[8] => 10 \\
			// matrix[9], matrix[10] and matrix[11] => 11 \\
			
			// Print grid
//			for (int i = 0; i < matrix[0].length; i++) {
//				for (int j = 0; j < matrix[0].length; j++) {
//					System.out.print(matrix[i][j] + " ");
//				}
//				System.out.println();
//			}
			
//			evaluationMatrix(matrix, matrix);
			br.close();
		} catch (Exception e) {}
	}
	
	// Creates evaluation matrix and returns percentage
	public static double[] evaluationMatrix(int[][] matrix, int[][] relevance) {
		int[][] evaluation = new int[12][200];
		
		for (int j = 0; j < matrix.length; j++) {
			for (int j2 = 0; j2 < matrix[0].length; j2++) {
				if(j<3)
					if(relevance[j][j2] == matrix[j][j2])
						evaluation[j][j2] = 1;
				if(j>=3 && j<=5)
					if(relevance[j-3][j2] == matrix[j][j2])
						evaluation[j][j2] = 1;
				if(j>=6 && j<=8)
					if(relevance[j-6][j2] == matrix[j][j2])
						evaluation[j][j2] = 1;
				if(j>=9 && j<=11)
					if(relevance[j-9][j2] == matrix[j][j2])
						evaluation[j][j2] = 1;
			}
		}
		
		double[] perc = new double[12];
		for (int i = 0; i < evaluation.length; i++) {
			for (int j = 0; j < evaluation[0].length; j++)
				perc[i] += evaluation[i][j];
			perc[i] = perc[i]/200;
		}
		
		return perc;
	}
	
	public static void recall(int[][] relevance) {
		
	}
	
	public static void precision() {
		
	}

	public static void fMeasure() {
		
	}
}
