package com.brackeen.javagamebook.tilegame;

import java.awt.Image;
import java.util.LinkedList;
import java.util.Iterator;

import com.brackeen.javagamebook.graphics.Sprite;
import com.brackeen.javagamebook.tilegame.sprites.Bullet;

/**
    The TileMap class contains the data for a tile-based
    map, including Sprites. Each tile is a reference to an
    Image. Of course, Images are used multiple times in the tile
    map.
*/
public class TileMap {

    private Image[][] tiles;
    private int[][] tileAttri;
    private LinkedList sprites;
    private Sprite player;
    private LinkedList<Bullet> bullets;

    /**
        Creates a new TileMap with the specified width and
        height (in number of tiles) of the map.
    */
    public TileMap(int width, int height) {
        tiles = new Image[width][height];
        tileAttri = new int[width][height];
        sprites = new LinkedList();
        bullets = new LinkedList();
    }


    /**
        Gets the width of this TileMap (number of tiles across).
    */
    public int getWidth() {
        return tiles.length;
    }


    /**
        Gets the height of this TileMap (number of tiles down).
    */
    public int getHeight() {
        return tiles[0].length;
    }


    /**
        Gets the tile at the specified location. Returns null if
        no tile is at the location or if the location is out of
        bounds.
    */
    public Image getTile(int x, int y) {
        if (x < 0 || x >= getWidth() ||
            y < 0 || y >= getHeight())
        {
            return null;
        }
        else {
            return tiles[x][y];
        }
    }


    /**
        Sets the tile at the specified location.
    */
    public void setTile(int x, int y, Image tile) {
        tiles[x][y] = tile;
    }
    
    /**
     * Set tile Attribute, there are three kind of tiles, exploding, gas and normal,
     * flag=0->normal; flag=1->exploding; flag=2->gas
     */
    public void setTileAttribute(int x, int y, int flag)
    {
    	tileAttri[x][y]=flag;
    }

    public int getTileAttribute(int x,int y)
    {
    	if(x<0)x=0;
    	if(y<0)y=0;
    	return tileAttri[x][y];
    }

    /**
        Gets the player Sprite.
    */
    public Sprite getPlayer() {
        return player;
    }


    /**
        Sets the player Sprite.
    */
    public void setPlayer(Sprite player) {
        this.player = player;
    }


    /**
        Adds a Sprite object to this map.
    */
    public void addSprite(Sprite sprite) {
        sprites.add(sprite);
    }


    /**
        Removes a Sprite object from this map.
    */
    public void removeSprite(Sprite sprite) {
        sprites.remove(sprite);
    }


    /**
        Gets an Iterator of all the Sprites in this map,
        excluding the player Sprite.
    */
    public Iterator getSprites() {
        return sprites.iterator();
    }
    
    /**  
        add bullet object to this map
    */
    public void addBullet(Bullet bullet){
    	bullets.add(bullet);
    }
    public void removeBullet(Bullet bullet){
    	bullets.remove(bullet);
    }
//    public LinkedList<Bullet> getBulletList(){
//    	return bullets;
//    }
    public Iterator getBulletList(){
    	return bullets.iterator();
    }

}
