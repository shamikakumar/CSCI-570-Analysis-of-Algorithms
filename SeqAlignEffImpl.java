import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.*;
import java.io.*;
import java.util.*;

class SeqAlignEffImpl {

	// class to store final min alignment
	private static class Solution {
		String alignment1;
		String alignment2;
		int minPenalty;
	}

	// hardcoded gap penalty and mismatch penalty array
	private static int pgap = 30;
	private static int[][] mismatchArr = { { 0, 110, 48, 94 }, { 110, 0, 118, 48 }, { 48, 118, 0, 110 },
			{ 94, 48, 110, 0 } };
	private static int minPen = 0;

	// function that reads "input.txt" and generates strings from base values
	public String[] inputGen() {
		BufferedReader reader;
		String regex = "[0-9]*";
		int j = 0, k = 0;
		String str1 = null, str2 = null;
		try {
			reader = new BufferedReader(new FileReader(
					"input.txt"));
			String line = reader.readLine();
			str1 = line;
			line = reader.readLine();
			while (Pattern.matches(regex, line)) {
				j++;
				int index1 = Integer.parseInt(line);
				str1 = str1.substring(0, index1 + 1) + str1 + str1.substring(index1 + 1, str1.length());
				line = reader.readLine();
			}
			str2 = line;
			line = reader.readLine();
			while (line != null) {
				k++;
				int index2 = Integer.parseInt(line);
				str2 = str2.substring(0, index2 + 1) + str2 + str2.substring(index2 + 1, str2.length());
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String[] res = new String[2];
		res[0] = str1;
		res[1] = str2;
		return res;
	}

	// function to return mismatch penalty for x,y
	static int getMismatchCost(char x, char y) {
		int i, j;
		if (x == 'A')
			i = 0;
		else if (x == 'C')
			i = 1;
		else if (x == 'G')
			i = 2;
		else
			i = 3;

		if (y == 'A')
			j = 0;
		else if (y == 'C')
			j = 1;
		else if (y == 'G')
			j = 2;
		else
			j = 3;

		return mismatchArr[i][j];
	}

	static int[] spaceEffAlignment(String x, String y) {
		int m = x.length();
		int n = y.length();
		int B[][] = new int[m + 1][2];
		for (int i = 0; i <= m; i++)
			B[i][0] = pgap * i;

		for (int j = 1; j <= n; j++) {
			B[0][1] = j * pgap;
			for (int i = 1; i <= m; i++) {
				int temp = Math.min(pgap + B[i - 1][1], pgap + B[i][0]);
				B[i][1] = Math.min(temp, B[i - 1][0] + getMismatchCost(x.charAt(i - 1), y.charAt(j - 1)));
			}
			for (int k = 0; k <= m; k++) {
				B[k][0] = B[k][1];
			}
		}
		int res[] = new int[m + 1];
		for (int i = 0; i < res.length; i++)
			res[i] = B[i][1];
		return res;
	}

	static int[] backwardsSpaceEffAlignment(String x, String y) {
		int m = x.length();
		int n = y.length();
		int B[][] = new int[m + 1][2];
		for (int i = 0; i <= m; i++)
			B[i][0] = pgap * i;

		for (int j = 1; j <= n; j++) {
			B[0][1] = j * pgap;
			for (int i = 1; i <= m; i++) {
				int temp = Math.min(pgap + B[i - 1][1], pgap + B[i][0]);
				B[i][1] = Math.min(temp, B[i - 1][0] + getMismatchCost(x.charAt(m - i), y.charAt(n - j)));
			}
			for (int k = 0; k <= m; k++) {
				B[k][0] = B[k][1];
			}
		}
		int res[] = new int[m + 1];
		for (int i = 0; i < res.length; i++)
			res[i] = B[i][1];
		return res;
	}

	// dynamic programming implementation of sequence alignment problem
	static Solution getMinPenalty(String x, String y) {
		int i, j;
		int m = x.length();
		int n = y.length();

		int dp[][] = new int[n + m + 1][n + m + 1];

		for (int[] x1 : dp)
			Arrays.fill(x1, 0);

		for (i = 0; i <= (n + m); i++) {
			dp[i][0] = i * pgap;
			dp[0][i] = i * pgap;
		}

		// fill in dp array
		for (i = 1; i <= m; i++) {
			for (j = 1; j <= n; j++) {
				if (x.charAt(i - 1) == y.charAt(j - 1)) {
					dp[i][j] = dp[i - 1][j - 1];
				} else {
					dp[i][j] = Math.min(Math.min(dp[i - 1][j - 1] + getMismatchCost(x.charAt(i - 1), y.charAt(j - 1)),
							dp[i - 1][j] + pgap),
							dp[i][j - 1] + pgap);
				}
			}
		}

		int l = n + m;

		i = m;
		j = n;

		int xpos = l;
		int ypos = l;

		int xans[] = new int[l + 1];
		int yans[] = new int[l + 1];

		// reconstruct alignment strings
		while (!(i == 0 || j == 0)) {
			if (x.charAt(i - 1) == y.charAt(j - 1)) {
				xans[xpos--] = (int) x.charAt(i - 1);
				yans[ypos--] = (int) y.charAt(j - 1);
				i--;
				j--;
			} else if (dp[i - 1][j - 1] + getMismatchCost(x.charAt(i - 1), y.charAt(j - 1)) == dp[i][j]) {
				xans[xpos--] = (int) x.charAt(i - 1);
				yans[ypos--] = (int) y.charAt(j - 1);
				i--;
				j--;
			} else if (dp[i - 1][j] + pgap == dp[i][j]) {
				xans[xpos--] = (int) x.charAt(i - 1);
				yans[ypos--] = (int) '_';
				i--;
			} else if (dp[i][j - 1] + pgap == dp[i][j]) {
				xans[xpos--] = (int) '_';
				yans[ypos--] = (int) y.charAt(j - 1);
				j--;
			}
		}
		while (xpos > 0) {
			if (i > 0)
				xans[xpos--] = (int) x.charAt(--i);
			else
				xans[xpos--] = (int) '_';
		}
		while (ypos > 0) {
			if (j > 0)
				yans[ypos--] = (int) y.charAt(--j);
			else
				yans[ypos--] = (int) '_';
		}

		int id = 1;
		for (i = l; i >= 1; i--) {
			if ((char) yans[i] == '_' &&
					(char) xans[i] == '_') {
				id = i + 1;
				break;
			}
		}

		minPen = dp[m][n];
		String align1 = "", align2 = "";

		for (i = id; i <= l; i++) {
			align1 += (char) xans[i];
			;
		}
		for (i = id; i <= l; i++) {
			align2 += (char) yans[i];
		}

		Solution s = new Solution();
		s.alignment1 = align1;
		s.alignment2 = align2;
		s.minPenalty = minPen;
		return s;
	}

	// efficient divide and conquer implementation of sequence alignment problem
	static Solution getMinPenaltyEff(String x, String y) {
		int m = x.length();
		int n = y.length();

		if (n <= 2 || m <= 2) {

			return getMinPenalty(x, y);

		} else {
			int[] F = spaceEffAlignment(x, y.substring(0, n / 2));
			int[] B = backwardsSpaceEffAlignment(x, y.substring(n / 2));

			int[] partition = new int[m];
			int min = Integer.MAX_VALUE, cut = 0;
			for (int j = 0; j < m; j++) {
				partition[j] = F[j] + B[m - j];
				if (partition[j] < min) {
					min = partition[j];
					cut = j;
				}
			}
			F = null;
			B = null;
			partition = null;
			Solution callLeft = new Solution();
			Solution callRight = new Solution();

			callLeft = getMinPenaltyEff(x.substring(0, cut), y.substring(0, n / 2));
			callRight = getMinPenaltyEff(x.substring(cut), y.substring(n / 2));

			Solution res = new Solution();
			res.alignment1 = callLeft.alignment1 + callRight.alignment1;
			res.alignment2 = callLeft.alignment2 + callRight.alignment2;
			res.minPenalty = callLeft.minPenalty + callRight.minPenalty;

			return res;
		}

	}

	// generate "output.txt" having alignment strings, min alignment cost and
	// performance metrics
	static void genOutputFile(double duration, double memory, Solution s) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
			if (s.alignment1.length() <= 50) {
				writer.write(s.alignment1);
			} else {
				writer.write(s.alignment1.substring(0, 50) + " " + s.alignment1.substring(s.alignment1.length() - 50));
			}
			writer.newLine();
			if (s.alignment2.length() <= 50) {
				writer.write(s.alignment2);
			} else {
				writer.write(s.alignment2.substring(0, 50) + " " + s.alignment2.substring(s.alignment2.length() - 50));
			}
			writer.newLine();
			writer.write(Float.toString((float) s.minPenalty));
			writer.newLine();
			writer.write(Double.toString(duration));
			writer.newLine();
			writer.write(Double.toString(memory));

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		SeqAlignEffImpl s = new SeqAlignEffImpl();
		double start = System.nanoTime();
		double beforeUsedMem = runtime.totalMemory() - runtime.freeMemory();
		String[] input = new String[2];
		input = s.inputGen();
		Solution sln = new Solution();
		sln = s.getMinPenaltyEff(input[0], input[1]);
		double end = System.nanoTime();
		double duration = (end - start) * 0.000000001;
		double afterUsedMem = runtime.totalMemory() - runtime.freeMemory();
		double mem = (afterUsedMem - beforeUsedMem) / 1024;
		s.genOutputFile(duration, mem, sln);
	}
}