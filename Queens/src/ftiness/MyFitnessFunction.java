package ftiness;

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;

public class MyFitnessFunction extends FitnessFunction
{
	private static final long serialVersionUID = -7606824690611896580L;

	public static final int MAX_FITNESS = 1000000;
	public static final int CLASH_PENALTY = 10000;

	@Override
	protected double evaluate(IChromosome potentialSolution)
	{
		int fitness = MAX_FITNESS;

		fitness -= calculateHorizontalPenalty(potentialSolution);
		fitness -= calculateDiagonalPenalty(potentialSolution);

		if (fitness < 0) fitness = 0;
		return fitness;
	}

	private int calculateHorizontalPenalty(IChromosome potentialSolution)
	{
		int fitnessPenalty = 0;
		int boardSize = potentialSolution.size();

		for (int i = 0; i < boardSize; i++)
		{
			for (int j = 0; j < boardSize; j++)
			{
				if (potentialSolution.getGene(i).equals(potentialSolution.getGene(j)) && i != j)
				{
					fitnessPenalty += CLASH_PENALTY;
				}
			}
		}

		return fitnessPenalty;
	}

	private int calculateDiagonalPenalty(IChromosome potentialSolution)
	{
		int fitnessPenalty = 0;
		int boardSize = potentialSolution.size();

		for (int i = 0; i < boardSize; i++)
		{
			int selected = ((Integer) potentialSolution.getGene(i).getAllele()).intValue();

			// UP-RIGHT DIAGONAL
			for (int j = i + 1, k = 1; j < boardSize; j++, k++)
			{
				if (((Integer) potentialSolution.getGene(j).getAllele()).intValue() == selected + k)
				{
					fitnessPenalty += CLASH_PENALTY;
				}
			}
			// DOWN-LEFT DIAGONAL
			for (int j = i - 1, k = 1; j >= 0; j--, k++)
			{
				if (((Integer) potentialSolution.getGene(j).getAllele()).intValue() == selected - k)
				{
					fitnessPenalty += CLASH_PENALTY;
				}
			}
			// UP-LEFT DIAGONAL
			for (int j = i - 1, k = 1; j >= 0; j--, k++)
			{
				if (((Integer) potentialSolution.getGene(j).getAllele()).intValue() == selected + k)
				{
					fitnessPenalty += CLASH_PENALTY;
				}
			}
			//DOWN_RIGHT DIAGONAL
			for (int j = i + 1, k = 1; j < boardSize; j++, k++)
			{
				if (((Integer) potentialSolution.getGene(j).getAllele()).intValue() == selected - k)
				{
					fitnessPenalty += CLASH_PENALTY;
				}
			}
		}
		return fitnessPenalty;
	}

}
