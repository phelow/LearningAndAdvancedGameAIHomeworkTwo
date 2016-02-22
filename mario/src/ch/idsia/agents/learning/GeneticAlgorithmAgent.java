package ch.idsia.agents.learning;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.evolution.Evolvable;
import ch.idsia.evolution.MLP;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: Apr 28, 2009
 * Time: 2:09:42 PM
 */
public class GeneticAlgorithmAgent extends BasicMarioAIAgent implements Agent, Evolvable
{

    private MLP mlp;
    private String name = "GeneticAlgorithmAgent";
    final int numberOfOutputs = 6;
    final int numberOfInputs = 361;
    private Environment environment;

    /*final*/
    protected byte[][] levelScene;
    /*final */
    protected byte[][] enemies;
    protected byte[][] mergedObservation;

    protected float[] marioFloatPos = null;
    protected float[] enemiesFloatPos = null;

    protected int[] marioState = null;

    protected int marioStatus;
    protected int marioMode;
    protected boolean isMarioOnGround;
    protected boolean isMarioAbleToJump;
    protected boolean isMarioAbleToShoot;
    protected boolean isMarioCarrying;
    protected int getKillsTotal;
    protected int getKillsByFire;
    protected int getKillsByStomp;
    protected int getKillsByShell;

    // values of these variables could be changed during the Agent-Environment interaction.
    // Use them to get more detailed or less detailed description of the level.
    // for information see documentation for the benchmark <link: marioai.org/marioaibenchmark/zLevels
    int zLevelScene = 1;
    int zLevelEnemies = 0;


    public GeneticAlgorithmAgent()
    {
    	super("GeneticAlgorithmAgent");
        mlp = new MLP(numberOfInputs, 10, numberOfOutputs);
    }

    private GeneticAlgorithmAgent(MLP mlp)
    {
    	super("GeneticAlgorithmAgent");
        this.mlp = mlp;
    }

    public Evolvable getNewInstance()
    {
        return new GeneticAlgorithmAgent(mlp.getNewInstance());
    }

    public Evolvable copy()
    {
        return new GeneticAlgorithmAgent(mlp.copy());
    }

    public void integrateObservation(Environment environment)
    {
        this.environment = environment;
        levelScene = environment.getLevelSceneObservationZ(zLevelScene);
        enemies = environment.getEnemiesObservationZ(zLevelEnemies);
        mergedObservation = environment.getMergedObservationZZ(1, 0);

        this.marioFloatPos = environment.getMarioFloatPos();
        this.enemiesFloatPos = environment.getEnemiesFloatPos();
        this.marioState = environment.getMarioState();

        // It also possible to use direct methods from Environment interface.
        //
        marioStatus = marioState[0];
        marioMode = marioState[1];
        isMarioOnGround = marioState[2] == 1;
        isMarioAbleToJump = marioState[3] == 1;
        isMarioAbleToShoot = marioState[4] == 1;
        isMarioCarrying = marioState[5] == 1;
        getKillsTotal = marioState[6];
        getKillsByFire = marioState[7];
        getKillsByStomp = marioState[8];
        getKillsByShell = marioState[9];
    }

    public void giveIntermediateReward(float intermediateReward)
    {

    }

    public void reset()
    { mlp.reset(); }

    public void mutate()
    { mlp.mutate(); }

    public boolean[] getAction()
    {
//        double[] inputs = new double[]{probe(-1, -1, levelScene), probe(0, -1, levelScene), probe(1, -1, levelScene),
//                              probe(-1, 0, levelScene), probe(0, 0, levelScene), probe(1, 0, levelScene),
//                                probe(-1, 1, levelScene), probe(0, 1, levelScene), probe(1, 1, levelScene),
//                                1};
        /*double[] inputs = new double[]{probe(-1, -1, mergedObservation), probe(0, -1, mergedObservation), probe(1, -1, mergedObservation),
                probe(-1, 0, mergedObservation), probe(0, 0, mergedObservation), probe(1, 0, mergedObservation),
                probe(-1, 1, mergedObservation), probe(0, 1, mergedObservation), probe(1, 1, mergedObservation),
                1};*/
    	double[] inputs = new double[361];
    	for(int i = 0; i < mergedObservation.length; i++){
    		for(int j = 0; j < mergedObservation.length; j++){
    			inputs[i * mergedObservation.length + j] = mergedObservation[i][j];
    		}
    	}
        double[] outputs = mlp.propagate(inputs);
        boolean[] action = new boolean[numberOfOutputs];
        for (int i = 0; i < action.length; i++)
            action[i] = outputs[i] > 0;
        return action;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    private double probe(int x, int y, byte[][] scene)
    {
    	/*
    	System.out.println("----Mario Scene----");
    	for(int i = 0; i < scene.length; i++){
    		String p = "";
    		for(int j = 0; j < scene.length; j++){
    			p = p +"[" + i + "," + j + "] : " + scene[i][j];
    		}
    		System.out.println(p);
    	}
    	System.out.println("--END Mario Scene--");*/
    	if(x < 0 || y < 0 || x > scene.length || y > scene.length){
    		return 0;
    	}
        return (scene[x][y] != 0) ? 1 : 0;
    }
}
