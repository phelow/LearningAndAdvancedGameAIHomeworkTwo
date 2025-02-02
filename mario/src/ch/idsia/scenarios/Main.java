package ch.idsia.scenarios;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.ForwardAgent;
import ch.idsia.agents.controllers.ForwardJumpingAgent;
import ch.idsia.agents.learning.SimpleMLPAgent;
import ch.idsia.agents.learning.MediumSRNAgent;
import ch.idsia.agents.learning.GeneticAlgorithmAgent;
import ch.idsia.benchmark.mario.engine.GlobalOptions;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.mario.environments.MarioEnvironment;
import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.benchmark.tasks.MarioCustomSystemOfValues;
import ch.idsia.benchmark.tasks.ProgressTask;
import ch.idsia.evolution.Evolvable;
import ch.idsia.evolution.ea.ES;
import ch.idsia.evolution.ea.CustomES;
import ch.idsia.evolution.ea.GeneticES;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.utils.Stats;
import ch.idsia.utils.wox.serial.Easy;

/**
 * Created by IntelliJ IDEA. User: Sergey Karakovskiy, sergey at idsia dot ch Date: Mar 17, 2010 Time: 8:28:00 AM
 * Package: ch.idsia.scenarios
 */
public final class Main
{
	final static int generations = 10000;
	final static int populationSize = 100;
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
	    CmdLineOptions options = new CmdLineOptions(args);
//      Evolvable initial = new LargeSRNAgent();
//      Evolvable initial = new SmallSRNAgent();
//      Evolvable initial = new MediumSRNAgent();
      Evolvable initial = new GeneticAlgorithmAgent();
//  Evolvable initial = new GeneticAlgorithmAgent();
//      Evolvable initial = new MediumMLPAgent();
//      if (args.length > 0)
//      {
//          initial = (Evolvable) AgentsPool.load (args[0]);
//      }
  options.setTimeLimit(100);
  options.setAgent((Agent) initial);
  options.setFPS(GlobalOptions.MaxFPS);
  options.setPauseWorld(false);
  options.setVisualization(false);
  ProgressTask task = new ProgressTask(options);
//      MultiSeedProgressTask task = new MultiSeedProgressTask(options);
//      MushroomTask task = new MushroomTask(options);
  int seed = 6189642;
  options.setLevelRandSeed(seed);
//      int seed = (int) (Math.random () * Integer.MAX_VALUE / 100000);
 // int seed = options.getLevelRandSeed();
 CustomES es = new CustomES(task, initial, populationSize);
  System.out.println("Evolving " + initial + " with task " + task);
//      int difficulty = 0;
//      System.out.println("seed = " + seed);
//      task.uid = seed;

//      options.setLevelRandSeed(seed);
//      BasicTask bt = new MultiSeedProgressTask(new CmdLineOptions(args));
//      CmdLineOptions c = new CmdLineOptions(new String[]{"-vis", "on", "-fps", "24"});

  // start learning in mode 0
  System.out.println("options.getTimeLimit() = " + options.getTimeLimit());
//      options.setMarioMode(0);
  String fileName = "evolved-" + "-uid-" + seed + ".xml";
  DecimalFormat df = new DecimalFormat();
  float bestScore = 600;

  int dif = 0;

  for (int gen = 0; gen < generations; gen++)
  {
	  seed--;
	  dif--;
	  if(dif < 0){
		  dif = 9;
	  }
	  if(seed < 0){
		  seed = 1000;
		  
	  }
	  options.setLevelDifficulty(dif);
	  options.setLevelRandSeed(seed);
//          System.out.print("<a = " + options.getMarioMode() + "> ");
          //task.setStartingSeed(gen);
      es.nextGeneration();

      float fitn = es.getBestFitnesses()[0];// Appending data to text file

      String path = "E:\\Development\\SchoolWork Spring 2016\\Learning and Advanced Game AI\\MarioTesting\\GeneticAlgorithmTest.txt";

      File file = new File(path);

      FileWriter fileWriter = new FileWriter(file,true);

      BufferedWriter bufferFileWriter = new BufferedWriter(fileWriter);

      fileWriter.append("Generation: " + gen + " CurrentBest: " + df.format(fitn) + " AllTimeBest: " + df.format(bestScore) + " Seed: " + seed + " Difficulty: " + dif + ";");

      bufferFileWriter.close();

      System.out.println("Generation: " + gen + " CurrentBest: " + df.format(fitn) + " AllTimeBest: " + df.format(bestScore) + " Seed: " + seed + " Difficulty: " + dif + ";");
//          int marioStatus = task.getEnvironment().getEvaluationInfo().marioStatus;

      if (fitn > bestScore /*&& marioStatus == Environment.MARIO_STATUS_WIN*/)
      {
          bestScore = fitn;
          fileName = "evolved-progress-" + options.getAgentFullLoadName() + gen + "-uid-" + seed + ".xml";
          final Agent a = (Agent) es.getBests()[0];
//          Easy.save(a, fileName);
          task.dumpFitnessEvaluation(bestScore, "fitnessImprovements-" + options.getAgentFullLoadName() + ".txt");
//              c.setLevelRandSeed(options.getLevelRandSeed());
//              c.setLevelDifficulty(options.getLevelDifficulty());
//              c.setTimeLimit(options.getTimeLimit());
          options.setAgent(a);
          System.out.println("a = " + options.getMarioMode());
//              task.setAgent(a);
          options.setVisualization(true);
          options.setFPS(42);
          task.getEnvironment().reset(options);
          
          task.evaluate(a);
          options.setVisualization(false);
          options.setFPS(100);

          System.out.print("MODE: = " + task.getEnvironment().getEvaluationInfo().marioMode);
          System.out.print("TIME LEFT: " + task.getEnvironment().getEvaluationInfo().timeLeft);
          System.out.println(", STATUS = " + task.getEnvironment().getEvaluationInfo().marioStatus);

//              difficulty++;
//              options.setLevelDifficulty(difficulty);
      }
  }

  System.out.println("\n\n\n\n\n\n\n\n\n");
  try
  {
      Stats.main(new String[]{fileName, "0"});
  } catch (IOException e)
  {
      e.printStackTrace();
  }
  System.exit(0);
	}

}