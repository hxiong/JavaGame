package com.brackeen.javagamebook.tilegame.sprites;

import com.brackeen.javagamebook.graphics.Animation;

/**
    The Player.
*/
public class Player extends Creature {

    private static final float JUMP_SPEED = -.95f;

    private boolean onGround;
    
    //// player health status
    private final float MOVE_SCALE;
    private float health;
    private float maxHealth = 40;
    private long invincibleTimer = 0;    //1sec invincible time
    private boolean gettingHurt = false;
    private long motionlessTimer = 0;  
    private int score;
    private int automodeOn;   //asserted when in automode;
    
    public Player(Animation left, Animation right,
        Animation deadLeft, Animation deadRight)
    {
        super(left, right, deadLeft, deadRight);
        this.health = 20;
        this.MOVE_SCALE = 0.05f;
        this.score = 0;
        this.automodeOn = 0;
    }
    
    public void setAutoMode(int m){
    	this.automodeOn = m;
    }
    public int getAutoMode(){
    	return automodeOn;
    }
    
    public int getScore(){
    	return this.score;
    }
    
    public void updateScore(int s){
    	this.score +=s;
    }


    public void collideHorizontal() {
        setVelocityX(0);
        this.health -= 1*MOVE_SCALE;
        
    }


    public void collideVertical() {
        // check if collided with ground
        if (getVelocityY() > 0) {
            onGround = true;
        }
        setVelocityY(0);
    }


    public void setY(float y) {
        // check if falling
        if (Math.round(y) > Math.round(getY())) {
            onGround = false;
        }
        super.setY(y);
    }


    public void wakeUp() {
        // do nothing
    }


    /**
        Makes the player jump if the player is on the ground or
        if forceJump is true.
    */
    public void jump(boolean forceJump) {
        if (onGround || forceJump) {
            onGround = false;
            setVelocityY(JUMP_SPEED);
        }
    }


    public float getMaxSpeed() {
    	if (this.health < 40){  this.health += 1*MOVE_SCALE;}
    	
        return 0.5f;
    }
    
    public int getHealth(){
    	return Math.round(this.health);
    }
    
    public void setHealth(int currentHealth)
    {
    	this.health = currentHealth;
    }
    
    public void updateHealth(int a){
    	this.health += a;
    }
    
    public boolean onTheGround()
    {
    	return onGround;
    }
    
    public boolean isHurt()
    {
    	return gettingHurt;
    }
    
    public void getHurt()
    {
    	gettingHurt = true;
    }
    
    public void healthMonitor(long dt)
    {
    	if(gettingHurt==true)
    	{
    		invincibleTimer += dt;
    		if(invincibleTimer > 200){
    			gettingHurt = false;
    			invincibleTimer = 0;
    		}
    	}
    	if(getVelocityX()==0)
    	{
    		motionlessTimer += dt;
    		if(motionlessTimer>1000)
    		{
    			motionlessTimer = 0;
    			  if(getHealth()<=40)
                  {
                  	if(getHealth()+5>40)
                  		setHealth(40);
                  	else
                  		updateHealth(5);
                  }
    		}
    	}
    	else
    		motionlessTimer = 0;
    }
    
}
