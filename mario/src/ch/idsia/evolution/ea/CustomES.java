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
public class CustomES implements EA
{

    private final GeneticAlgorithmAgent[] population;
    private final float[] fitness;
    private final int elite;
    private final Task task;
    private final int evaluationRepetitions = 1;

    private final int parent = 20;
    private final int child = 40;
    private final int untouched = 30;
    private final int mutated = 10;
    
    private final int chanceToPullFromUntouched = 90;
    private final int chanceToPullFromMutated = 95;
    private final int chanceToPullFromChild= 98;
    
    java.util.Random Random = new java.util.Random();
    
    public CustomES(Task task, Evolvable initial, int populationSize)
    {
        this.population = new GeneticAlgorithmAgent[populationSize];
        for (int i = 0; i < population.length; i++)
        {
            population[i] = (GeneticAlgorithmAgent) initial.getNewInstance();
        }
        this.fitness = new float[populationSize];
        this.elite = populationSize / 10;
        this.task = task;
    }

    public void nextGeneration()
    {
        for (int i = 0; i < parent + child + untouched + mutated; i++)
        {
            evaluate(i);
        }

        sortPopulationByFitness();
        //Use the top 20% to replace the bottom 50% with children
        //Mutate the 50-60% 
        //Don't touch the 60%-100%
        for(int i = parent + untouched; i < parent + untouched + mutated; i++){
        	
        	population[i].mutate();
        	evaluate(i);
        }
        
        

        for(int i = parent + untouched + mutated; i < parent + untouched + mutated + child; i++){
        	int mateA = Random.nextInt(parent);
            int mateB = Random.nextInt(parent);
            int mateC = Random.nextInt(parent);
            
            if(Random.nextInt(100) > chanceToPullFromUntouched){
            	mateA += Random.nextInt(untouched);
            }
            
            if(Random.nextInt(100) > chanceToPullFromUntouched){
            	mateB += Random.nextInt(untouched);
            }
            
            if(Random.nextInt(100) > chanceToPullFromUntouched){
            	mateC += Random.nextInt(untouched);
            }
            
            if(Random.nextInt(100) > chanceToPullFromMutated){
            	mateA += Random.nextInt(mutated);
            }
            
            if(Random.nextInt(100) > chanceToPullFromMutated){
            	mateB += Random.nextInt(mutated);
            }
            
            if(Random.nextInt(100) > chanceToPullFromMutated){
            	mateC += Random.nextInt(mutated);
            }
            

            if(Random.nextInt(100) > chanceToPullFromChild){
            	mateC += Random.nextInt(child);
            }
            
            if(Random.nextInt(100) > chanceToPullFromChild){
            	mateA += Random.nextInt(child);
            }
            
            if(Random.nextInt(100) > chanceToPullFromChild){
            	mateB += Random.nextInt(child);
            }
            
            if(Random.nextInt(100) > 98){
            	mateC += Random.nextInt(child);
            }
        	population[i].customMlp.psoRecombine(population[mateA].customMlp, population[mateB].customMlp, population[mateC].customMlp);
            
        	population[i].mutate();
        	evaluate(i);
        }
        
        /*
        for (int i = elite; i < population.length; i++)
        {
        	GeneticAlgorithmAgent newAgent = (GeneticAlgorithmAgent) population[i - elite].copy();
            population[i].customMlp.psoRecombine(population[Random.nextInt(elite)].customMlp, population[Random.nextInt(elite)].customMlp, population[Random.nextInt(elite)].customMlp);
            population[i].mutate();
            evaluate(i);
        
        }
        */
        
        shuffle();
        sortPopulationByFitness();
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
