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
	
  public void update(long elapsedTime){
   setX(getX() + getVelocityX() * elapsedTime);
  // setY(getY() + dy * elapsedTime;
  }
//  
//  public float getX(){
//	  return x;
//  }
//  
//  public float getY(){
//	  return y;
//  }
	
	public Bullet(float a, float b){
		super(null);
		setX(a);
		setY(b);
		setVelocityX(0.5f);
		setVelocityY(0);
	}
}
