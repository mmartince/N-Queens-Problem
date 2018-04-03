package main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.CrossoverOperator;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.IntegerGene;
import org.jgap.impl.MutationOperator;

import ftiness.MyFitnessFunction;

public class Main
{
	public static final int NUMBER_OF_EVOLUTIONS = 100000;
	// public static final BigDecimal MAX_NUMBER_OF_ITERATIONS = new
	// BigDecimal("10000000000000"); //More work time if needed
	public static final long MAX_NUMBER_OF_ITERATIONS = 1000000000; // ~4 minutes of work
	public static final int POPULATION_SIZE = 40;
	public static final int MAX_BOARD_SIZE = 10000;
	public static int BOARD_SIZE;
	public static int NUMBER_OF_ITERATIONS = 0;
	// public static final BigDecimal NUMBER_OF_ITERATIONS = new BigDecimal(0);

	public static void main(String[] args)
	{

		Scanner scan = new Scanner(System.in);
		System.out.println("Unesite zeljene dimenzije ploce (NxN):");
		BOARD_SIZE = scan.nextInt();
		scan.close();

		FitnessFunction fitnessFunction = new MyFitnessFunction();

		Configuration configuration = new DefaultConfiguration();

		try
		{
			PrintWriter writer = new PrintWriter(new FileOutputStream(new File("results.txt"), true));

			configuration.addGeneticOperator(new CrossoverOperator(configuration));
			configuration.addGeneticOperator(new MutationOperator(configuration));
			configuration.setKeepPopulationSizeConstant(false);
			configuration.setPreservFittestIndividual(true);
			configuration.setFitnessFunction(fitnessFunction);

			Gene[] sampleGenes = new Gene[BOARD_SIZE];

			for (int i = 0; i < BOARD_SIZE; i++)
			{
				sampleGenes[i] = new IntegerGene(configuration, 0, BOARD_SIZE - 1);
			}

			Chromosome sampleChromosome = new Chromosome(configuration, sampleGenes);

			configuration.setSampleChromosome(sampleChromosome);

			configuration.setPopulationSize(POPULATION_SIZE);

			long start = System.currentTimeMillis();

			Genotype population = Genotype.randomInitialGenotype(configuration);
			
			System.out.println("Working...");

			for (int i = 0; i < NUMBER_OF_EVOLUTIONS; i++)
			{

				IChromosome bestSolutionSoFar = population.getFittestChromosome();

				if (bestSolutionSoFar.getFitnessValue() == MyFitnessFunction.MAX_FITNESS)
				{
					System.out.println("Solution found in " + i + " generations:");
					writer.print(i + "\t\t\t");
					printGenetic(bestSolutionSoFar);
					break;
				}

				population.evolve();

				if (i == NUMBER_OF_EVOLUTIONS - 1)
				{
					System.out.println("No perfect solution found, best solution after " + i + " generations:");
					writer.print("no\t\t" + i + "\t\t\t");
					printGenetic(bestSolutionSoFar);
				}
			}

			long end = System.currentTimeMillis();

			System.out.println("GA time: " + (((double) end - start) / 1000) + "s");
			writer.println(((double) end - start) / 1000);
			writer.close();
		}
		catch (InvalidConfigurationException ex)
		{
			ex.printStackTrace();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		System.out.println("Working...");

		algorithmicSolution();
	}

	public static void algorithmicSolution()
	{
		char[][] board = new char[BOARD_SIZE][BOARD_SIZE];
		int solved = -1;

		for (int i = 0; i < BOARD_SIZE; i++)
		{
			for (int j = 0; j < BOARD_SIZE; j++)
			{
				board[i][j] = ' ';
			}
		}
		long start = System.currentTimeMillis();

		solved = findNext(board, 0);
		if (solved == 0) System.out.println("There is no solution");
		else if (solved == -1)
		{
			System.out.println("No solution found");
		}

		long end = System.currentTimeMillis();

		System.out.println("Algorithm time: " + (((double) end - start) / 1000) + "s");
	}

	public static int findNext(char[][] board, int pos)
	{
		if (NUMBER_OF_ITERATIONS == MAX_NUMBER_OF_ITERATIONS)
		{
			System.out.println("Failsafe triggered!");
			return -1;
		}
		if (pos == BOARD_SIZE)
		{
			return 1;
		}
		int i = 0;
		int result = 0;

		for (; i < BOARD_SIZE; i++)
		{

			if (board[pos][i] == ' ' && IsValid(board, pos, i))
			{
				board[pos][i] = 'Q';
				NUMBER_OF_ITERATIONS++;
				result = findNext(board, pos + 1);
				if (result == 0)
				{
					board[pos][i] = ' ';
				}
				else if (result == 1 && pos == 0)
				{
					System.out.println("Solution found: ");
					printAlgorithmic(board);
					//printAlgorithmicV2(board);
					return result;
				}
				else if (result == 1 || result == -1) return result;
			}
		}
		if (i == BOARD_SIZE) return 0;
		return -1;
	}

	public static boolean IsValid(char[][] board, int pos, int i)
	{
		if (!isHorizontalValid(board, pos, i)) return false;
		if (!isVerticallValid(board, pos, i)) return false;
		if (!isDiagonalValid(board, pos, i)) return false;
		return true;
	}

	public static boolean isDiagonalValid(char[][] board, int pos, int count)
	{
		// UP-RIGHT DIAGONAL
		for (int i = pos, j = count; i >= 0 && j < BOARD_SIZE; i--, j++)
		{
			if (board[i][j] == 'Q') return false;
		}
		// DOWN-LEFT DIAGONAL
		for (int i = pos, j = count; i < BOARD_SIZE && j >= 0; i++, j--)
		{
			if (board[i][j] == 'Q') return false;
		}
		// DOWN-RIGHT DIAGONAL
		for (int i = pos, j = count; i < BOARD_SIZE && j < BOARD_SIZE; i++, j++)
		{
			if (board[i][j] == 'Q') return false;
		}
		// UP-LEFT DIAGONAL
		for (int i = pos, j = count; i >= 0 && j >= 0; i--, j--)
		{
			if (board[i][j] == 'Q') return false;
		}
		return true;
	}

	public static boolean isHorizontalValid(char[][] board, int pos, int count)
	{

		for (int i = 0; i < BOARD_SIZE; i++)
		{
			if (board[pos][i] == 'Q') { return false; }
		}
		return true;
	}

	public static boolean isVerticallValid(char[][] board, int pos, int count)
	{

		for (int i = 0; i < BOARD_SIZE; i++)
		{
			if (board[i][count] == 'Q') { return false; }
		}
		return true;
	}

	public static void printGenetic(IChromosome chromosome)
	{
		char[][] board = new char[BOARD_SIZE][BOARD_SIZE];

		for (int i = 0; i < chromosome.size(); i++)
		{
			int j = ((Integer) chromosome.getGene(i).getAllele()).intValue();
			board[j][i] = 'Q';
		}

		for (int j = 0; j < chromosome.size(); j++)
		{
			System.out.print(((Integer) chromosome.getGene(j).getAllele()).intValue() + " ");
		}
		System.out.println("\nFitness: " + chromosome.getFitnessValue());
		System.out.println("Board is read from top to bottom:");
		printAlgorithmic(board);
	}

	public static void printAlgorithmic(char[][] board)
	{
		System.out.print(" ");
		for (int i = 0; i < BOARD_SIZE * 2; i++)
		{
			System.out.print("_");
		}
		System.out.println();
		for (int i = 0; i < BOARD_SIZE; i++)
		{
			System.out.print(" ");
			for (int j = 0; j < BOARD_SIZE; j++)
			{
				System.out.print("|");
				System.out.print(board[i][j]);
			}
			System.out.print("|");
			System.out.println();
		}
		System.out.print(" ");
		for (int i = 0; i < BOARD_SIZE * 2; i++)
		{
			System.out.print("-");
		}
		System.out.println();
	}

	public static void printAlgorithmicV2(char[][] board)
	{
		System.out.print(" ");
		for (int i = 0; i < BOARD_SIZE * 2; i++)
		{
			System.out.print("_");
		}
		System.out.println();
		for (int i = 0; i < BOARD_SIZE; i++)
		{
			System.out.print(" ");
			for (int j = 0; j < BOARD_SIZE; j++)
			{
				System.out.print("|");
				System.out.print(board[i][j]);
			}
			System.out.print("|");
			System.out.println();
			System.out.print(" ");
			for (int k = 0; k < BOARD_SIZE * 2; k++)
			{
				System.out.print("-");
			}
			System.out.println();
		}
		System.out.println();
	}
}
