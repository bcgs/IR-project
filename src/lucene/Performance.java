package lucene;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Performance {

	public static void main(String[] args) {

		int[][] relevance = {
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
		};

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

			// Counting docs hit
			int[] retrievedDocs = new int[12];
			for (int i = 0; i < matrix.length; i++)
				for (int j = 0; j < matrix[0].length; j++)
					if(matrix[i][j]==1) retrievedDocs[i] += 1;

			// Counting relevantDocs hit
			int[] relevantDocs = new int[3];
			for (int i = 0; i < relevance.length; i++)
				for (int j = 0; j < relevance[0].length; j++)
					if(relevance[i][j]==1) relevantDocs[i] += 1;


			double[] p = precision(evaluationMatrix(matrix, relevance, false), retrievedDocs);
			double[] r = recall(evaluationMatrix(matrix, relevance, false), relevantDocs);
			fMeasure(p, r);
			accuracy(evaluationMatrix(matrix, relevance, true));

			br.close();
		} catch (Exception e) {}
	}

	public static int[][] evaluationMatrix(int[][] matrix, int[][] relevance, boolean eval) {
		int[][] evaluation = new int[12][200];

		for (int j = 0; j < matrix.length; j++) {
			for (int j2 = 0; j2 < matrix[0].length; j2++) {
				if(j<3)
					if(eval) {
						if(relevance[j][j2] == matrix[j][j2])
							evaluation[j][j2] = 1;
					} else 
						if(relevance[j][j2]==1 && matrix[j][j2]==1)
							evaluation[j][j2] = 1;
				if(j>=3 && j<=5)
					if(eval) {
						if(relevance[j-3][j2] == matrix[j][j2])
							evaluation[j][j2] = 1;
					} else 
						if(relevance[j-3][j2]==1 && matrix[j][j2]==1)
							evaluation[j][j2] = 1;
				if(j>=6 && j<=8)
					if(eval) {
						if(relevance[j-6][j2] == matrix[j][j2])
							evaluation[j][j2] = 1;
					} else
						if(relevance[j-6][j2]==1 && matrix[j][j2]==1)
							evaluation[j][j2] = 1;
				if(j>=9 && j<=11)
					if(eval) {
						if(relevance[j-9][j2] == matrix[j][j2])
							evaluation[j][j2] = 1;
					} else 
						if(relevance[j-9][j2]==1 && matrix[j][j2]==1)
							evaluation[j][j2] = 1;
			}
		}
		return evaluation;
	}
	
	public static double round3(double value) {
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(3, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
	
	public static void media(double[] matrix) {
		double mediaQ1=0.0, mediaQ2=0.0, mediaQ3=0.0;
		for (int i = 0; i < 4; i++) {
			mediaQ1 += matrix[i*3];
			mediaQ2 += matrix[i*3+1];
			mediaQ3 += matrix[i*3+2];
		}
		System.out.println("Media:");
		System.out.print(round3(mediaQ1/4)+"\t"+round3(mediaQ2/4)+"\t"+round3(mediaQ3/4));
		System.out.println();
	}
	
	public static void printMatrix(double[] matrix) {
		System.out.println("Q1\tQ2\tQ3");
		for (int i = 0; i < matrix.length; i++) {
			System.out.print(matrix[i]+"\t");
			if((i % 3) == 2)
				System.out.println();
		}
	}

	public static double[] precision(int[][] evaluation, int[] retrievedDocs) {
		double[] perc = new double[12];
		for (int i = 0; i < evaluation.length; i++) {
			for (int j = 0; j < evaluation[0].length; j++)
				perc[i] += evaluation[i][j];
			perc[i] = round3(perc[i]/retrievedDocs[i]);
		}
		System.out.println("\nPrecision:");
		printMatrix(perc);
		media(perc);
		
		return perc;
	}

	public static double[] recall(int[][] evaluation, int[] relevantDocs) {
		double[] perc = new double[12];
		for (int i = 0; i < evaluation.length; i++) {
			for (int j = 0; j < evaluation[0].length; j++)
				perc[i] += evaluation[i][j];
			perc[i] = round3(perc[i]/relevantDocs[i%3]);
		}
		System.out.println("\nRecall:");
		printMatrix(perc);
		media(perc);
		
		return perc;
	}

	public static double[] accuracy(int[][] evaluation) {
		double[] perc = new double[12];

		for (int i = 0; i < evaluation.length; i++) {
			for (int j = 0; j < evaluation[0].length; j++)
				perc[i] += evaluation[i][j];
			perc[i] = round3(perc[i]/200);
		}
		System.out.println("\n---------------------");
		System.out.println("System accuracy:");
		printMatrix(perc);
		media(perc);
		
		return perc;
	}

	public static void fMeasure(double[] p, double[] r) {
		double[] fmeasure = new double[12];
		for (int i = 0; i < fmeasure.length; i++)
			fmeasure[i] = round3((2*p[i]*r[i])/(p[i]+r[i]));

		System.out.println("\nf-measure:");
		printMatrix(fmeasure);
		media(fmeasure);
	}

}
