package com.brackeen.javagamebook.tilegame.sprites;

import com.brackeen.javagamebook.graphics.Sprite;

public class Bullet extends Sprite {
	
//	// position (pixels)
//    private float x;
//    private float y;
//    // velocity (pixels per millisecond)
//    private float dx;
//    private float dy;
//    
//  public Bullet(float a, float b){
//	  super(null);
//	  this.x = a;
//	  this.y = b;
//	  this.dx = 5;
//	  this.dy = 0;
//  };
//
	private int bulletFlag;   //0 for player bullet, 1 for grub bullet
	private boolean isDead = false;
	private int bulletDirection = 1;  // 1 means right,-1 means left
	//private float distanceTraveled = 0;
  public void update(long elapsedTime){
   setX(getX() + (getVelocityX()) * elapsedTime);
  // setY(getY() + dy * elapsedTime;
   //in the update method, check the bullet status, if it is outside the boundary or collide with an enemy
   
  }
//  
//  public float getX(){
//	  return x;
//  }
//  
//  public float getY(){
//	  return y;
//  }
	
	public Bullet(float a, float b,int flag){
		super(null);
		setX(a);
		setY(b);
		if(flag==0)
			setVelocityX(0.5f);
		else if(flag==1)
			setVelocityX(0.25f);
		setVelocityY(0);
		bulletFlag = flag;
	}
	
	public void setDirection(int dirction)
	{
		//set the direction of the bullet
		setVelocityX(dirction*getVelocityX());
		bulletDirection = dirction;

	}
	
	public int getDirection()
	{
		return bulletDirection;
	}
	
	public int getWidth()
	{
		return 30;
	}
	
	public int getHeight()
	{
		return 30;
	}
	
	public void setDead()
	{
		isDead = true;
	}
	
    /**
     * get the bullet flag for the current bullet
*/
	public int getBulletFlag()
	{
		return bulletFlag;
	}
}
