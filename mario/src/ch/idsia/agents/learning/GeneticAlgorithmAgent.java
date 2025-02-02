package ch.idsia.agents.learning;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.evolution.Evolvable;
import ch.idsia.evolution.MLP;

import ch.idsia.evolution.CustomMLP;
/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: Apr 28, 2009
 * Time: 2:09:42 PM
 */
public class GeneticAlgorithmAgent extends BasicMarioAIAgent implements Agent, Evolvable
{

    public CustomMLP customMlp;
    private String name = "GeneticAlgorithmAgent";
    final int numberOfOutputs = 6;
    final int numberOfInputs = 377;
    final int neuronsPerLevel = 50;
    private Environment environment;

    double[] outputs;
    
    /*final*/
    protected byte[][] levelScene;
    /*final */
    protected byte[][] enemies;
    protected byte[][] prevEnemies;
    protected byte[][] mergedObservation;

    protected float[] marioFloatPos = null;
    protected float[] marioPrevFloatPos = null;
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
    private boolean firstFrame = true;

    public GeneticAlgorithmAgent()
    {
    	super("GeneticAlgorithmAgent");
    	customMlp = new CustomMLP(numberOfInputs, neuronsPerLevel, numberOfOutputs);
    	customMlp.mutationMagnitude = .1f;
    	
    	customMlp.learningRate = .2f;
    }

    private GeneticAlgorithmAgent(CustomMLP mlp)
    {
    	super("GeneticAlgorithmAgent");
        this.customMlp = mlp;
    }

    public Evolvable getNewInstance()
    {
        return new GeneticAlgorithmAgent(customMlp.getNewInstance());
    }

    public Evolvable copy()
    {
        return new GeneticAlgorithmAgent(customMlp.copy());
    }

    public void integrateObservation(Environment environment)
    {
    	if(this.firstFrame == false){
    		marioPrevFloatPos = marioFloatPos;
    	}
    	
        this.environment = environment;
        levelScene = environment.getLevelSceneObservationZ(zLevelScene);
        enemies = environment.getEnemiesObservationZ(zLevelEnemies);
        mergedObservation = environment.getMergedObservationZZ(1, 0);
        if(firstFrame == false){
	        for(int i = 0; i < mergedObservation.length; i++){
	        	for(int j = 0; j < mergedObservation.length; j++){
	        		if(prevEnemies[i][j] != 0 && mergedObservation[i][j] == 0){
	        			mergedObservation[i][j] = (byte) (prevEnemies[i][j] + 300);
	        		}
	        	}
	        }
        }

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
        
        prevEnemies = enemies;
        
        
    }

    public void giveIntermediateReward(float intermediateReward)
    {

    }

    public void reset()
    { customMlp.reset(); }

    public void mutate()
    { customMlp.mutate(); }
    
//---- start rules based mario functions
		
	public float GetMarioSpeed(){
		return this.marioFloatPos[0] - this.marioPrevFloatPos[0];
	}
	//--- end rules based mario functions
    

	public float GetMarioVerticalSpeed(){
		return this.marioFloatPos[1] - this.marioPrevFloatPos[1];
	}
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
    	double[] inputs = new double[numberOfInputs];
    	for(int i = 0; i < mergedObservation.length; i++){
    		for(int j = 0; j < mergedObservation.length; j++){
    			inputs[i * mergedObservation.length + j] = mergedObservation[i][j];
    		}
    	}
    	
    	inputs[361] = marioStatus;
    	inputs[362] = marioMode;
    	inputs[363] =  isMarioOnGround ? 1.0 : 0.0;
    	inputs[364] = isMarioAbleToJump ? 1.0 : 0.0;
    	inputs[365] = isMarioAbleToShoot? 1.0 : 0.0;
    	inputs[366] = isMarioCarrying? 1.0 : 0.0;
    	inputs[367] = this.marioFloatPos[0];
    	inputs[368] = this.marioFloatPos[1];
    	if(firstFrame == false){
    		inputs[369] = outputs[0];
    		inputs[370] = outputs[1];
    		inputs[371] = outputs[2];
    		inputs[372] = outputs[3];
    		inputs[373] = outputs[4];
    		inputs[374] = outputs[5];
    		inputs[375] = GetMarioSpeed();
    		inputs[376] = GetMarioVerticalSpeed();
    	}
    	else{

    		firstFrame = false;
    	}
    	/*
    	 * 
        this.marioState = environment.getMarioState();
        marioStatus = marioState[0];
        marioMode = marioState[1];

        isMarioOnGround = marioState[2] == 1;
        isMarioAbleToJump = marioState[3] == 1;
        isMarioAbleToShoot = marioState[4] == 1;
        isMarioCarrying = marioState[5] == 1;
        
    	 * 
    	 */
    	
        outputs = customMlp.propagate(inputs);
        customMlp.backPropagate(outputs);
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

    private int probe(int x, int y, byte[][] scene)
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
        return scene[x][y];
    }
}
