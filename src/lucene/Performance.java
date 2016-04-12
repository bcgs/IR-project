package lucene;

import java.io.BufferedReader;
import java.io.FileReader;
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

			// Counting docs hit
			int[] retrievedDocs = new int[12];
			for (int i = 0; i < matrix.length; i++)
				for (int j = 0; j < matrix[0].length; j++)
					if(matrix[i][j]==1) retrievedDocs[i] += 1;

			int[] relevantDocs = new int[3];
			for (int i = 0; i < relevance.length; i++)
				for (int j = 0; j < relevance[0].length; j++)
					if(relevance[i][j]==1) relevantDocs[i] += 1;
			

			// Matrix contains all the 4 sources needed:  \\
			//   matrix[0], matrix[1] and matrix[2] => 00 \\
			//   matrix[3], matrix[4] and matrix[5] => 01 \\
			//   matrix[6], matrix[7] and matrix[8] => 10 \\
			// matrix[9], matrix[10] and matrix[11] => 11 \\

			// Print grid
			//				for (int i = 0; i < matrix[0].length; i++) {
			//					for (int j = 0; j < matrix[0].length; j++) {
			//						System.out.print(matrix[i][j] + " ");
			//					}
			//					System.out.println();
			//				}

			double[] p = precision(evaluationMatrix2(matrix, relevance), retrievedDocs);
			double[] r = recall(evaluationMatrix2(matrix, relevance), relevantDocs);
			fMeasure(p, r);
			accuracy(evaluationMatrix(matrix, relevance));
			
			br.close();
		} catch (Exception e) {}
	}

	// Creates evaluation matrix and returns percentage
	public static int[][] evaluationMatrix(int[][] matrix, int[][] relevance) {
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

		return evaluation;
	}

	public static int[][] evaluationMatrix2(int[][] matrix, int[][] relevance) {
		int[][] evaluation = new int[12][200];

		for (int j = 0; j < matrix.length; j++) {
			for (int j2 = 0; j2 < matrix[0].length; j2++) {
				if(j<3)
					if(relevance[j][j2]==1 && matrix[j][j2]==1)
						evaluation[j][j2] = 1;
				if(j>=3 && j<=5)
					if(relevance[j-3][j2]==1 && matrix[j][j2]==1)
						evaluation[j][j2] = 1;
				if(j>=6 && j<=8)
					if(relevance[j-6][j2]==1 && matrix[j][j2]==1)
						evaluation[j][j2] = 1;
				if(j>=9 && j<=11)
					if(relevance[j-9][j2]==1 && matrix[j][j2]==1)
						evaluation[j][j2] = 1;
			}
		}
		return evaluation;
	}

	public static double[] precision(int[][] evaluation, int[] retrievedDocs) {
		double[] perc = new double[12];
		for (int i = 0; i < evaluation.length; i++) {
			for (int j = 0; j < evaluation[0].length; j++)
				perc[i] += evaluation[i][j];
			perc[i] = perc[i]/retrievedDocs[i];
		}
		System.out.println("\nPrecision:");
		System.out.println("Q1\tQ2\tQ3");
		for (int i = 0; i < perc.length; i++) {
			System.out.print(perc[i]+"\t");
			if((i % 3) == 2)
				System.out.println();
		}
		return perc;
	}

	public static double[] recall(int[][] evaluation, int[] relevantDocs) {
		double[] perc = new double[12];
		for (int i = 0; i < evaluation.length; i++) {
			for (int j = 0; j < evaluation[0].length; j++)
				perc[i] += evaluation[i][j];
			perc[i] = perc[i]/relevantDocs[i%3];
		}
		System.out.println("\nRecall:");
		System.out.println("Q1\tQ2\tQ3");
		for (int i = 0; i < perc.length; i++) {
			System.out.print(perc[i]+"\t");
			if((i % 3) == 2)
				System.out.println();
		}
		return perc;
	}

	public static double[] accuracy(int[][] evaluation) {
		double mediaQ1=0.0, mediaQ2=0.0, mediaQ3=0.0;
		double[] perc = new double[12];

		for (int i = 0; i < evaluation.length; i++) {
			for (int j = 0; j < evaluation[0].length; j++)
				perc[i] += evaluation[i][j];
			perc[i] = perc[i]/200;
		}
		System.out.println("\n---------------------");
		System.out.println("Accuracy:");
		System.out.println("Q1\tQ2\tQ3");
		for (int i = 0; i < perc.length; i++) {
			System.out.print(perc[i]+"\t");
			if((i % 3) == 2)
				System.out.println();
		}
		System.out.println("\nSystem accuracy:");
		for (int j = 0; j < 4; j++) {
			mediaQ1 += perc[j*3];
			mediaQ2 += perc[j*3+1];
			mediaQ3 += perc[j*3+2];
		}
		System.out.println("Q1\tQ2\tQ3");
		System.out.print(mediaQ1/4+"\t"+mediaQ2/4+"\t"+mediaQ3/4);

		return perc;
	}

	public static void fMeasure(double[] p, double[] r) {
		double[] fmeasure = new double[12];
		for (int i = 0; i < fmeasure.length; i++)
			fmeasure[i] = (2*p[i]*r[i])/(p[i]+r[i]);

		System.out.println("\nf-measure:");
		System.out.println("Q1\tQ2\tQ3");
		for (int i = 0; i < fmeasure.length; i++) {
			System.out.print(fmeasure[i]+"\t");
			if((i % 3) == 2)
				System.out.println();
		}
	}

}
