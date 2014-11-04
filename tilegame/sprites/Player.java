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

    public Player(Animation left, Animation right,
        Animation deadLeft, Animation deadRight)
    {
        super(left, right, deadLeft, deadRight);
        this.health = 20;
        this.MOVE_SCALE = 0.05f;
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
    
    public void updateHealth(int a){
    	this.health += a;
    }
}
