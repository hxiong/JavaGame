package com.brackeen.javagamebook.tilegame.sprites;

import com.brackeen.javagamebook.graphics.Animation;

/**
    A Grub is a Creature that moves slowly on the ground.
*/
public class Grub extends Creature {
	
	private long shootTimer = 0;
	private long engageTimer = 0;   // when player enters the screen, grub will shoot in 0.5sec
	private boolean engaged = false;
	

    public Grub(Animation left, Animation right,
        Animation deadLeft, Animation deadRight)
    {
        super(left, right, deadLeft, deadRight);
    }


    public float getMaxSpeed() {
        return 0.05f;
    }
    
    public double getEngageTimer()
    {
    	return engageTimer;
    }
        
    /**
    monitor grub shooting, dt is the update elapsed time, inRange indicates wether the player is in range
    reload indicates the reload time for the grub
*/
    public boolean grubShooterMonitor(long dt,boolean inRange, long reload)
    {
    	if(inRange==false)
    	{
    		shootTimer = 0;
    		engageTimer = 0;
    		engaged = false;
    		return false;
    	}
    	else{
    		
    		if(engageTimer>=500&&engaged == false)
    		{
    			//first encounter, shoot bullet after 0.5 sec
    			engaged = true;
    			return true;
    		}
    		else if(engageTimer>=500&&engaged == true){
    			//engaged, start shoot based on half char shooting rate
	    		shootTimer += dt;
	    		if(shootTimer >reload)
	    		{
	    			shootTimer = 0;
	    			return true;
	    		}
    		}
    		else
    			engageTimer += dt;
    			
    		return false;
    	}
    }

}
