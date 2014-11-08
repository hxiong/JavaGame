package com.brackeen.javagamebook.tilegame;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Iterator;

import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.AudioFormat;

import com.brackeen.javagamebook.graphics.*;
import com.brackeen.javagamebook.sound.*;
import com.brackeen.javagamebook.input.*;
import com.brackeen.javagamebook.test.GameCore;
import com.brackeen.javagamebook.tilegame.sprites.*;

/**
    GameManager manages all parts of the game.
*/
public class GameManager extends GameCore {

    public static void main(String[] args) {
        new GameManager().run();
    }

    // uncompressed, 44100Hz, 16-bit, mono, signed, little-endian
    private static final AudioFormat PLAYBACK_FORMAT =
        new AudioFormat(44100, 16, 1, true, false);

    private static final int DRUM_TRACK = 1;

    public static final float GRAVITY = 0.002f;

    private Point pointCache = new Point();
    private TileMap map;
    private MidiPlayer midiPlayer;
    private SoundManager soundManager;
    private ResourceManager resourceManager;
    private Sound prizeSound;
    private Sound boopSound;
    private Sound bulletSound;
    private Sound deadSound;  // char got hit
    private InputManager inputManager;
    private TileMapRenderer renderer;

    private GameAction moveLeft;
    private GameAction moveRight;
    private GameAction jump;
    private GameAction exit;
    private GameAction pshoot;   // player shoot
    private boolean pshootGotPressed = false;
    private double pshootPressTimer = 0;
    private double autoFireCD = 1.5;
    private int playerDirectionFlag = 1;  // 1 means facing right, -1 means facing left
    private int clip = 0;   //the number of shoots fired in automatic firing mode


    public void init() {
        super.init();

        // set up input manager
        initInput();

        // start resource manager
        resourceManager = new ResourceManager(
        screen.getFullScreenWindow().getGraphicsConfiguration());

        // load resources
        renderer = new TileMapRenderer();
        renderer.setBackground(
            resourceManager.loadImage("background.png"));

        // load first map
        map = resourceManager.loadNextMap();

        // load sounds
        soundManager = new SoundManager(PLAYBACK_FORMAT);
        deadSound = soundManager.getSound("sounds/prize.wav");
        prizeSound = soundManager.getSound("sounds/prize.wav");
        boopSound = soundManager.getSound("sounds/boop2.wav");   
        bulletSound = soundManager.getSound("sounds/bulletWhizz.wav");
      
        // start music
        midiPlayer = new MidiPlayer();
        Sequence sequence =
            midiPlayer.getSequence("sounds/music.midi");
        midiPlayer.play(sequence, true);
        toggleDrumPlayback();
    }


    /**
        Closes any resurces used by the GameManager.
    */
    public void stop() {
        super.stop();
        midiPlayer.close();
        soundManager.close();
    }


    private void initInput() {
        moveLeft = new GameAction("moveLeft");
        moveRight = new GameAction("moveRight");
        
        jump = new GameAction("jump",
            GameAction.DETECT_INITAL_PRESS_ONLY);
        exit = new GameAction("exit",
            GameAction.DETECT_INITAL_PRESS_ONLY);
        
        pshoot = new GameAction("pshoot");

        inputManager = new InputManager(
            screen.getFullScreenWindow());
        inputManager.setCursor(InputManager.INVISIBLE_CURSOR);

        inputManager.mapToKey(moveLeft, KeyEvent.VK_LEFT);
        inputManager.mapToKey(moveRight, KeyEvent.VK_RIGHT);
        inputManager.mapToKey(jump, KeyEvent.VK_UP);
        inputManager.mapToKey(exit, KeyEvent.VK_ESCAPE);
        inputManager.mapToKey(pshoot, KeyEvent.VK_S);
        
   ////     inputManager ....
    }


    private void checkInput(long elapsedTime) {

        if (exit.isPressed()) {
            stop();
        }

        Player player = (Player)map.getPlayer();
        if (player.isAlive()) {
            float velocityX = 0;
            if (moveLeft.isPressed()) {
                velocityX-=player.getMaxSpeed();
                playerDirectionFlag = -1;
            }
            if (moveRight.isPressed()) {
                velocityX+=player.getMaxSpeed();
                playerDirectionFlag = 1;
            }
            if (jump.isPressed()) {
                player.jump(false);
            }
            player.setVelocityX(velocityX);
            if(pshoot.isPressed()){
            	pshootGotPressed = true;
            }
                     
            if (clip == 0 && pshoot.isReleased()&&pshootGotPressed==true){
            	// add shooting sound when bullet shot
                soundManager.play(bulletSound);
            	Bullet bullet = new Bullet(player.getX(),player.getY(),0);
            	bullet.setDirection(playerDirectionFlag);
            	map.addBullet(bullet);
            	pshootGotPressed = false;
            	autoFireCD = 0;
            	clip = 0;         	
            }
        }

    }


    public void draw(Graphics2D g) {
        renderer.draw(g, map,
            screen.getWidth(), screen.getHeight());
    }


    /**
        Gets the current map.
    */
    public TileMap getMap() {
        return map;
    }


    /**
        Turns on/off drum playback in the midi music (track 1).
    */
    public void toggleDrumPlayback() {
        Sequencer sequencer = midiPlayer.getSequencer();
        if (sequencer != null) {
            sequencer.setTrackMute(DRUM_TRACK,
                !sequencer.getTrackMute(DRUM_TRACK));
        }
    }


    /**
        Gets the tile that a Sprites collides with. Only the
        Sprite's X or Y should be changed, not both. Returns null
        if no collision is detected.
    */
    public Point getTileCollision(Sprite sprite,
        float newX, float newY)
    {
        float fromX = Math.min(sprite.getX(), newX);
        float fromY = Math.min(sprite.getY(), newY);
        float toX = Math.max(sprite.getX(), newX);
        float toY = Math.max(sprite.getY(), newY);

        // get the tile locations
        int fromTileX = TileMapRenderer.pixelsToTiles(fromX);
        int fromTileY = TileMapRenderer.pixelsToTiles(fromY);
        int toTileX = TileMapRenderer.pixelsToTiles(
            toX + sprite.getWidth() - 1);
        int toTileY = TileMapRenderer.pixelsToTiles(
            toY + sprite.getHeight() - 1);

        // check each tile for a collision
        for (int x=fromTileX; x<=toTileX; x++) {
            for (int y=fromTileY; y<=toTileY; y++) {
                if (x < 0 || x >= map.getWidth() ||
                    map.getTile(x, y) != null)
                {
                    // collision found, return the tile
                    pointCache.setLocation(x, y);
                    return pointCache;
                }
            }
        }

        // no collision found
        return null;
    }


    /**
        Checks if two Sprites collide with one another. Returns
        false if the two Sprites are the same. Returns false if
        one of the Sprites is a Creature that is not alive.
    */
    public boolean isCollision(Sprite s1, Sprite s2) {
        // if the Sprites are the same, return false
        if (s1 == s2) {
            return false;
        }

        // if one of the Sprites is a dead Creature, return false
        if (s1 instanceof Creature && !((Creature)s1).isAlive()) {
            return false;
        }
        if (s2 instanceof Creature && !((Creature)s2).isAlive()) {
            return false;
        }

        // get the pixel location of the Sprites
        int s1x = Math.round(s1.getX());
        int s1y = Math.round(s1.getY());
        int s2x = Math.round(s2.getX());
        int s2y = Math.round(s2.getY());

        // check if the two sprites' boundaries intersect
        return (s1x < s2x + s2.getWidth() &&
            s2x < s1x + s1.getWidth() &&
            s1y < s2y + s2.getHeight() &&
            s2y < s1y + s1.getHeight());
    }


    /**
        Gets the Sprite that collides with the specified Sprite,
        or null if no Sprite collides with the specified Sprite.
    */
    public Sprite getSpriteCollision(Sprite sprite) {

        // run through the list of Sprites
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite otherSprite = (Sprite)i.next();
            if (isCollision(sprite, otherSprite)) {
                // collision found, return the Sprite
                return otherSprite;
            }
        }
        // no collision found
        return null;
    }


    /**
        Updates Animation, position, and velocity of all Sprites
        in the current map.
    */
    public void update(long elapsedTime) {
    	
    	automaticFiring(pshootGotPressed,elapsedTime);
    	
        Creature player = (Creature)map.getPlayer();


        // player is dead! start map over
        if (player.getState() == Creature.STATE_DEAD) {
            map = resourceManager.reloadMap();
            return;
        }

        // get keyboard/mouse input
        checkInput(elapsedTime);

        // update player
        updateCreature(player, elapsedTime);
        player.update(elapsedTime);
        // monitor player health
        if(player instanceof Player){((Player) player).healthMonitor(elapsedTime);}

        // update other sprites
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite sprite = (Sprite)i.next();
            if (sprite instanceof Creature) {
                Creature creature = (Creature)sprite;
                if (creature.getState() == Creature.STATE_DEAD) {
                	//// update player score when kills
                  //  soundManager.play(boopSound);
                	((Player) player).updateScore(1);
    			    soundManager.play(boopSound);
                    i.remove();
                }
                else {
                    updateCreature(creature, elapsedTime);
                }
            }
            // normal update
            sprite.update(elapsedTime);
        }
        
        // update bullet
        Iterator b = (Iterator) map.getBulletList();
        while(b.hasNext()){
        	Bullet bullet = (Bullet) b.next();
        	if(bullet.getBulletFlag()==0)
        		bullet.setVelocityX((0.5f+Math.abs(player.getVelocityX()))*bullet.getDirection());
        	bullet.update(elapsedTime);
        	
        	// run through all the creatures to see if the bullet is colliding with any creature or going out of bounds.
        	Iterator tmpList = map.getSprites();
        	while(tmpList.hasNext()){
        		Sprite sprite = (Sprite)tmpList.next();
        		//check collision with creatures
        		if(isCollision(sprite,bullet)){
        			if((sprite instanceof Creature)&&(bullet.getBulletFlag()==0)){
        				Creature creature = (Creature)sprite;
        				creature.setState(1);
        				b.remove();        
        				break;
        			}
        		}
        	}
        	
        	Player players = (Player)map.getPlayer();
			if((isCollision(players,bullet))&&(bullet.getBulletFlag()==1)){
				if(players.getHealth()-5<=0&&players.isHurt()==false)
				{
					players.updateHealth(0);
            		players.setState(Creature.STATE_DYING);
				}
				else if(players.isHurt()==false&&players.getHealth()-5>0){
					players.updateHealth(-5);
					players.getHurt();
				}
				b.remove();
				break;
			}
        	
    		//check out of sight
    		if((bullet.getX()>player.getX()+500||bullet.getX()<player.getX()-500))
    		{
    			b.remove();
    			break;
    		}
        	
        }
    }


    /**
        Updates the creature, applying gravity for creatures that
        aren't flying, and checks collisions.
    */
    private void updateCreature(Creature creature,
        long elapsedTime)
    {

        // apply gravity
        if (!creature.isFlying()) {
            creature.setVelocityY(creature.getVelocityY() +
                GRAVITY * elapsedTime);
        }

        // change x
        float dx = creature.getVelocityX();
        float oldX = creature.getX();
        float newX = oldX + dx * elapsedTime;
        Point tile =
            getTileCollision(creature, newX, creature.getY());
        if (tile == null) {
            creature.setX(newX);
        }
        else {
            // line up with the tile boundary
            if (dx > 0) {
                creature.setX(
                    TileMapRenderer.tilesToPixels(tile.x) -
                    creature.getWidth());
            }
            else if (dx < 0) {
                creature.setX(
                    TileMapRenderer.tilesToPixels(tile.x + 1));
            }
            creature.collideHorizontal();
        }
        
        
        if(creature instanceof Grub)
        {

        	Grub grub = (Grub)creature;
        	boolean tmp = false;//hold the shootermonitor return variable
        	//grub bullet update shit
            Creature player = (Creature)map.getPlayer();

        	if(Math.abs(player.getX()-Math.round(creature.getX()))<500&&(creature.getState()!=1||creature.getState()!=2))
        	{
        		tmp = grub.grubShooterMonitor(elapsedTime, true, 1400);
        	}
        	else{
        		tmp = grub.grubShooterMonitor(elapsedTime, false, 1400);
        	}
        	if(tmp){
            	Bullet b = new Bullet(Math.round(creature.getX()),Math.round(creature.getY()),1);
            	if(player.getX()-creature.getX()>0)
            		b.setDirection(1);
            	else 
            		b.setDirection(-1);
            	map.addBullet(b);

        	}
        }
        
        
        if (creature instanceof Player) {
            checkPlayerCollision((Player)creature, false);
        }

        // change y
        float dy = creature.getVelocityY();
        float oldY = creature.getY();
        float newY = oldY + dy * elapsedTime;
        tile = getTileCollision(creature, creature.getX(), newY);
        if (tile == null) {
            creature.setY(newY);
        }
        else {
            // line up with the tile boundary
            if (dy > 0) {
                creature.setY(
                    TileMapRenderer.tilesToPixels(tile.y) -
                    creature.getHeight());
            }
            else if (dy < 0) {
                creature.setY(
                    TileMapRenderer.tilesToPixels(tile.y + 1));
            }
            creature.collideVertical();
        }
        if (creature instanceof Player) {
            boolean canKill = (oldY < creature.getY());
            checkPlayerCollision((Player)creature, canKill);
        }

    }


    /**
        Checks for Player collision with other Sprites. If
        canKill is true, collisions with Creatures will kill
        them.
    */
    public void checkPlayerCollision(Player player,
        boolean canKill)
    {
        if (!player.isAlive()) {
            return;
        }

        // check for player collision with other sprites
        Sprite collisionSprite = getSpriteCollision(player);
        if (collisionSprite instanceof PowerUp) {
            acquirePowerUp((PowerUp)collisionSprite);
            // update player score by 5 for each mushroom eat
            player.updateScore(5);
            //acquire mushroom, gain health
            if(player.getHealth()<=40)
            {
            	if(player.getHealth()+5>40)
            		player.setHealth(40);
            	else
            		player.updateHealth(5);
            }
        }
        else if (collisionSprite instanceof Creature) {
            Creature badguy = (Creature)collisionSprite;
            if (canKill&&player.onTheGround()==false) {
                // kill the badguy and make player bounce
               // soundManager.play(boopSound);
                badguy.setState(Creature.STATE_DYING);
                player.setY(badguy.getY() - player.getHeight());
                player.jump(true);
                if(player.getHealth()<=40)
                {
                	if(player.getHealth()+10>40)
                		player.setHealth(40);
                	else
                		player.updateHealth(10);
                }
            }
            else {
            	//// update the player health, if below 0, then die
            	if(player.isHurt()==false){
	            		// player dies!
					player.updateHealth(0);
	            	player.setState(Creature.STATE_DYING);
	            	// play sound when player die
	            	soundManager.play(deadSound);
            	}
            }
        }
    }


    /**
        Gives the player the speicifed power up and removes it
        from the map.
    */
    public void acquirePowerUp(PowerUp powerUp) {
        // remove it from the map
        map.removeSprite(powerUp);

        if (powerUp instanceof PowerUp.Star) {
            // do something here, like give the player points
            soundManager.play(prizeSound);
        }
        else if (powerUp instanceof PowerUp.Music) {
            // change the music
            soundManager.play(prizeSound);
            toggleDrumPlayback();
        }
        else if (powerUp instanceof PowerUp.Goal) {
            // advance to next map
            soundManager.play(prizeSound,
                new EchoFilter(2000, .7f), false);
            map = resourceManager.loadNextMap();
        }
    }
    
    public void automaticFiring(boolean theStupidButtonIsPressed,float dt){
    	if(theStupidButtonIsPressed==false)	return;    // shoot button
    	
    	if(autoFireCD < 1000)
    	{
    		autoFireCD += dt;
    		return;
    	}
    	//after pressing the pshoot button for 1 sec, maybe 200,player will fire automatically.
    	if(pshootPressTimer<200)	
    	{
    		pshootPressTimer += dt;
    		return;
    	}
    	//limited 10 rounds per clip
        Player player = (Player)map.getPlayer();
    	if(clip<10)
    	{
    		Bullet bullet = new Bullet(player.getX(),player.getY(),0);
        	bullet.setDirection(playerDirectionFlag);
        	map.addBullet(bullet);
        	pshootPressTimer = 0;
        	clip++;
        //	soundManager.play(bulletSound);
        }
    	if(clip==10){
    		//fired 10 bullets, cool down for 1 sec
    		autoFireCD = 0;
    		clip = 0;
    	}    	
    }

}
