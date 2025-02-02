package ch.idsia.evolution.ea;

import ch.idsia.agents.Agent;
import ch.idsia.agents.learning.GeneticAlgorithmAgent;
import ch.idsia.benchmark.tasks.Task;
import ch.idsia.evolution.EA;
import ch.idsia.evolution.Evolvable;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: Apr 29, 2009
 * Time: 12:16:49 PM
 */
public class GeneticES implements EA
{

    private final GeneticAlgorithmAgent[] population;
    private GeneticAlgorithmAgent[] repopulation;
    private final float[] fitness;
    private final float[] childFitness;
    private final int elite;
    private final Task task;
    private final int evaluationRepetitions = 1;

    private final int parent = 40;
    private final int child = 60;
    
    java.util.Random Random = new java.util.Random();
    
    public GeneticES(Task task, Evolvable initial, int populationSize)
    {
    	this.repopulation = new GeneticAlgorithmAgent[populationSize + child];
        this.population = new GeneticAlgorithmAgent[populationSize];
        for (int i = 0; i < population.length; i++)
        {
            population[i] = (GeneticAlgorithmAgent) initial.getNewInstance();
        }
        
        for (int i = 0; i < repopulation.length; i++)
        {
            repopulation[i] = (GeneticAlgorithmAgent) initial.getNewInstance();
        }
        this.fitness = new float[populationSize];
        this.childFitness = new float[populationSize + child];
        this.elite = populationSize / 10;
        this.task = task;
    }

    public void nextGeneration()
    {
        for (int i = 0; i < elite; i++)
        {
            evaluate(i);
        }

        sortPopulationByFitness();
        //Use the top 20% to replace the bottom 50% with children
        //Mutate the 50-60% 
        //Don't touch the 60%-100%
        for(int i = parent; i < parent + child; i++){
        	
        	population[i].mutate();
        	evaluate(i);
        }

        for(int i =0; i < population.length; i++){
        	repopulation[i] = population[i];
        }
        
        
        
        for(int i = population.length; i < repopulation.length; i++){
            int mateA = Random.nextInt(parent);
            int mateB = Random.nextInt(parent);
            int mateC = Random.nextInt(parent);
        	repopulation[i].customMlp.psoRecombine(population[mateA].customMlp, population[mateB].customMlp, population[mateC].customMlp);
        	evaluateChildPool(i);
        }
        
        shuffle();
        sortChildPopulationByFitness();
        for(int i=0; i < population.length; i++){
        	population[i] = repopulation[i];
        }
    }

    private void evaluate(int which)
    {
        fitness[which] = 0;
        for (int i = 0; i < evaluationRepetitions; i++)
        {
            population[which].reset();
            fitness[which] += task.evaluate((Agent) population[which])[0];
//            System.out.println("which " + which + " fitness " + fitness[which]);
        }
        fitness[which] = fitness[which] / evaluationRepetitions;
    }
    
    private void evaluateChildPool(int which)
    {
    	childFitness[which] = 0;
        for (int i = 0; i < evaluationRepetitions; i++)
        {
            repopulation[which].reset();
            childFitness[which] += task.evaluate((Agent) repopulation[which])[0];
//            System.out.println("which " + which + " fitness " + fitness[which]);
        }
        childFitness[which] = childFitness[which] / evaluationRepetitions;
    }

    private void shuffle()
    {
        for (int i = 0; i < population.length; i++)
        {
            swap(i, (int) (Math.random() * population.length));
        }
    }

    private void sortPopulationByFitness()
    {
        for (int i = 0; i < population.length; i++)
        {
            for (int j = i + 1; j < population.length; j++)
            {
                if (fitness[i] < fitness[j])
                {
                    swap(i, j);
                }
            }
        }
    }
    
    private void sortChildPopulationByFitness()
    {
        for (int i = 0; i < repopulation.length; i++)
        {
            for (int j = i + 1; j < repopulation.length; j++)
            {
                if (childFitness[i] < childFitness[j])
                {
                    childSwap(i, j);
                }
            }
        }
    }

    private void childSwap(int i, int j)
    {
        float cache = childFitness[i];
        childFitness[i] = childFitness[j];
        childFitness[j] = cache;
        GeneticAlgorithmAgent gcache = repopulation[i];
        repopulation[i] = repopulation[j];
        repopulation[j] = gcache;
    }
    
    private void swap(int i, int j)
    {
        float cache = fitness[i];
        fitness[i] = fitness[j];
        fitness[j] = cache;
        GeneticAlgorithmAgent gcache = population[i];
        population[i] = population[j];
        population[j] = gcache;
    }

    public Evolvable[] getBests()
    {
        return new Evolvable[]{population[0]};
    }

    public float[] getBestFitnesses()
    {
        return new float[]{fitness[0]};
    }

}
