package ch.idsia.agents.controllers;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import java.awt.Rectangle;


/**
 * User: William Pheloung
 * Date: 2-6-2016
 */

public class CompetentAgent extends BasicMarioAIAgent implements Agent
{
	private enum JumpType {

        ENEMY, GAP, WALL, NONE
    }
    private JumpType jumpType = JumpType.NONE;
    private int jumpCount = 0,  jumpSize = -1;
    private int[] prevPos = new int[]{0, 0};
    private boolean[] action;
    private String name;
    
	public CompetentAgent()
	{
	    super("CompetentAgent");
	    reset();
	}
	
	public void reset()
	{
	    action = new boolean[Environment.numberOfButtons];
        action[Mario.KEY_RIGHT] = true;
        action[Mario.KEY_SPEED] = true;
	}
	
	private final static int getWallHeight(final byte[][] levelScene) {
        int y = 12, wallHeight = 0;
        while (y-- > 0 && levelScene[y][12] != 0) {
            wallHeight++;
        }
        return wallHeight;
    }

    private final static boolean dangerOfGap(final byte[][] levelScene) {
        for (int y = 12; y < levelScene.length; y++) {
            if (levelScene[y][12] != 0) {
                return false;
            }
        }
        return true;
    }

    private boolean enemyInRange() {
    	
    	for(int i = marioCenter[0]; i < Math.min(marioCenter[0] + 5, enemies.length); i++){
    		for(int j = marioCenter[1];  j < Math.min(marioCenter[1] + 5, enemies.length); j++){
    			if(enemies[i][j] != 0){
    				return true;
    			}
    		}    	
    	}
        return false;
    }
    
private boolean enemyBehind() {
    	
    	for(int i = marioCenter[0]; i < Math.min(marioCenter[0] + 5, enemies.length); i++){
    		for(int j = Math.min(marioCenter[1] -8, 0) ;  j < Math.min(marioCenter[1]-3, enemies.length); j++){
    			if(enemies[i][j] != 0){
    				return true;
    			}
    		}    	
    	}
        return false;
    }
    

    private final void setJump(final JumpType type, final int size) {
        jumpType = type;
        jumpSize = size;
        jumpCount = 0;
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
	
	public void PrintObstacles(){
		System.out.println("----------------------" + marioCenter[0] + " " + marioCenter[1]);
		for(int i= 0; i < levelScene.length; i++){
			String line = "";
			for(int j = 0; j < levelScene.length; j++){
				line += "|" +i+","+j+" " + levelScene[i][j]+ "|";
			}
			System.out.println(line);
		}
	}
	
	public boolean ObstacleAhead(){
		return levelScene[marioCenter[0] + 1][marioCenter[1]] != 0 || levelScene[marioCenter[0] + 2][marioCenter[1]] != 0;
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
	public float GetMarioSpeed(){
		return marioCenter[0] - prevPos[0];
	}
	
	public boolean CoinAhead(){

	    int x = marioCenter[0];
	    int y = marioCenter[1];
	    return false;
	}
	
	public final boolean[] getAction() {
        final float marioSpeed = GetMarioSpeed();
        final boolean dangerOfEnemy = enemyInRange();
        
        final boolean dangerOfEnemyAbove = EnemiesAbove();
        final boolean dangerOfGap = dangerOfGap(levelScene);
        
        
        boolean eb =enemyBehind();
        
        if ((isMarioOnGround || isMarioAbleToJump) && !jumpType.equals(JumpType.NONE)) {
            setJump(JumpType.NONE, -1);
        } else if (isMarioAbleToJump) {
            if (dangerOfGap && marioSpeed > 0) {
                setJump(JumpType.GAP, marioSpeed < 6 ? (int) (9 - marioSpeed) : 1);
            } else if(ObstacleAhead() || eb){
                setJump(JumpType.ENEMY, 7);            	
            }
        } else {
            jumpCount++;
        }

        
        final boolean isFalling = prevPos[1] < marioCenter[1] && jumpType.equals(JumpType.NONE);
        if(eb){
        	action[Mario.KEY_LEFT] = true;
        	action[Mario.KEY_RIGHT] = false;
        }
        else{

            action[Mario.KEY_LEFT] = isFalling && ((dangerOfEnemy && dangerOfEnemyAbove) || dangerOfGap);
            action[Mario.KEY_RIGHT] = !isFalling && !(dangerOfEnemyAbove && jumpType == JumpType.WALL);
        }
        
       
        action[Mario.KEY_JUMP] = !jumpType.equals(JumpType.NONE) && jumpCount < jumpSize;
        action[Mario.KEY_SPEED] = !(jumpType.equals(JumpType.ENEMY) && action[Mario.KEY_SPEED] && marioMode == 2);
        prevPos = marioCenter.clone();
        return action;
    }
}