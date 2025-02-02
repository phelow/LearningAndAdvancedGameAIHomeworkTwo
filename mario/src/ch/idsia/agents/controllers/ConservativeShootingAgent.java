package ch.idsia.agents.controllers;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;

/**
 * User: William Pheloung
 * Date: 2-5-2016
 */

public class ConservativeShootingAgent extends BasicMarioAIAgent implements Agent
{
int trueJumpCounter = 0;
int trueSpeedCounter = 0;
boolean lastShot = false;

public ConservativeShootingAgent()
{
    super("ConservativeShootingAgent");
    reset();
}

public void reset()
{
    action = new boolean[Environment.numberOfButtons];
    trueJumpCounter = 0;
    trueSpeedCounter = 0;
}

private boolean DangerOfGap(byte[][] levelScene)
{
    int fromX = receptiveFieldWidth / 2;
    int fromY = receptiveFieldHeight / 2;

    if (fromX > 3)
    {
        fromX -= 2;
    }

    for (int x = fromX; x < receptiveFieldWidth; ++x)
    {
        boolean f = true;
        for (int y = fromY; y < receptiveFieldHeight; ++y)
        {
            if (getReceptiveFieldCellValue(y, x) != 0)
                f = false;
        }
        if (f ||
                getReceptiveFieldCellValue(marioCenter[0] + 1, marioCenter[1]) == 0 ||
                (marioState[1] > 0 &&
                        (getReceptiveFieldCellValue(marioCenter[0] + 1, marioCenter[1] - 1) != 0 ||
                                getReceptiveFieldCellValue(marioCenter[0] + 1, marioCenter[1]) != 0)))
            return true;
    }
    return false;
}

private boolean DangerOfGap()
{
    return DangerOfGap(levelScene);
}

public int getReceptiveFieldCellValue(int x, int y)
{
    if (x < 0 || x >= levelScene.length || y < 0 || y >= levelScene[0].length)
        return 0;

    return levelScene[x][y];
}

public boolean NextFilled(){

    int x = marioCenter[0];
    int y = marioCenter[1];
    
    if(getReceptiveFieldCellValue(x+1,y) != 0){
    	return true;
    }
    return false;
}

public boolean EnemyInJumpingRange(){
    boolean enemiesAhead = false;

    int x = marioCenter[0];
    int y = marioCenter[1];
    for(int i = x; i < Math.min(enemies.length,x+4); i++){
    	for(int j = 0; j < Math.min(enemies.length,y+10); j++){
    		if(enemies[i][j] != 0){
    			return true;
    		}
    	}
    }
    
    return false;
}

public boolean EnemiesAbove(){
	 int x = marioCenter[0];
	    int y = marioCenter[1];
	    for(int i = x; i < Math.min(enemies.length,x+4); i++){
	    	for(int j = y+1; j < Math.min(enemies.length,y+20); j++){
	    		if(enemies[i][j] != 0){
	    			return true;
	    		}
	    	}
	    }
	    
	    return false;
}

public boolean CheckForEnemies(){
    int x = marioCenter[0];
    int y = marioCenter[1];
    for(int i = x-1; i < Math.min(enemies.length,x+20); i++){
    	for(int j = 0; j < Math.min(enemies.length,y+10); j++){
    		if(enemies[i][j] != 0){
    			return true;
    		}
    	}
    }
    
    return false;
}
int consecutiveShotAttempts = 0;
public boolean[] getAction()
{
    // this Agent requires observation integrated in advance.
	
	//if there is any enemies ahead, jump up and down and spam fire
	boolean enemiesAhead = CheckForEnemies();
	
	

	action[Mario.KEY_LEFT] = false;
	
	if(enemiesAhead && consecutiveShotAttempts < 5 && Mario.fire){
		action[Mario.KEY_RIGHT] = false;
		
		if( isMarioAbleToShoot && lastShot == false){
	        action[Mario.KEY_SPEED] = true;
			System.out.println("shooting");
			lastShot = true;
		}
		else {
			action[Mario.KEY_SPEED] = false;
			action[Mario.KEY_RIGHT] = true;
			lastShot = false;
		}
		if (isMarioAbleToJump || (!isMarioOnGround && action[Mario.KEY_JUMP]))
        {
            action[Mario.KEY_JUMP] = true;
        }
		
		consecutiveShotAttempts++;
	}
	else{
		consecutiveShotAttempts=0;
		action[Mario.KEY_RIGHT] = true;
	    if (getReceptiveFieldCellValue(marioCenter[0], marioCenter[1] + 2) != 0 ||
	            getReceptiveFieldCellValue(marioCenter[0], marioCenter[1] + 1) != 0 ||
	            DangerOfGap())
	    {
	        if (isMarioAbleToJump || (!isMarioOnGround && action[Mario.KEY_JUMP]) || EnemyInJumpingRange())
	        {
	            action[Mario.KEY_JUMP] = true;
	        }
	        ++trueJumpCounter;
	    } else
	    {
	        action[Mario.KEY_JUMP] = false;
	        trueJumpCounter = 0;
	    }
	
	    if (trueJumpCounter > 16)
	    {
	        trueJumpCounter = 0;
	        action[Mario.KEY_JUMP] = false;
	    }
	    action[Mario.KEY_SPEED] = DangerOfGap();
	}

    
    
    return action;
}
}
