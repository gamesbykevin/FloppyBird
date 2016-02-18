package com.gamesbykevin.floppybird.background;

import com.gamesbykevin.androidframework.anim.Animation;
import com.gamesbykevin.androidframework.base.Entity;
import com.gamesbykevin.androidframework.resources.Images;
import com.gamesbykevin.floppybird.assets.Assets;
import com.gamesbykevin.floppybird.common.ICommon;
import com.gamesbykevin.floppybird.panel.GamePanel;

import android.graphics.Canvas;

public class Background extends Entity implements ICommon
{
	private enum Key
	{
		Ground(0, 1002, 800, 58, GROUND_Y), 
		Sky(0, 0, 800, 450, SKY_Y), 
		Cloud(0, 450, 800, 447, CLOUD_Y), 
		Bush(0, 897, 800, 105, BUSH_Y);
		
		//where the animation is located
		private final int animationX, animationY, animationW, animationH;
		
		//the current x position
		private int x, y;
		
		private Key(int animationX, int animationY, int animationW, int animationH, int startY)
		{
			this.animationX = animationX;
			this.animationY = animationY;
			this.animationW = animationW;
			this.animationH = animationH;
			
			//set starting position
			setX(DEFAULT_X);
			setY(startY);
		}
		
		/**
		 * Update the current x-coordinate
		 * @param x x-coordinate
		 */
		private void setX(final int x)
		{
			this.x = x;
		}
		
		/**
		 * Get the x
		 * @return The current x-coordinate
		 */
		private int getX()
		{
			return this.x;
		}
		
		/**
		 * Update the current y-coordinate
		 * @param y y-coordinate
		 */
		private void setY(final int y)
		{
			this.y = y;
		}
		
		/**
		 * Get the y
		 * @return The current y-coordinate
		 */
		private int getY()
		{
			return this.y;
		}
	}
	
	//default locations for each animation
	private static final int DEFAULT_X = 0;
	private static final int BUSH_Y = 351;
	private static final int GROUND_Y = 422;
	private static final int SKY_Y = 0;
	private static final int CLOUD_Y = 0;
	
	/**
	 * The speed at which the ground/bushes move
	 */
	public static final int DEFAULT_SCROLL_X = 5;
	
	public Background() 
	{
		super();
		
		//add animations
		addAnimation(Key.Bush);
		addAnimation(Key.Cloud);
		addAnimation(Key.Ground);
		addAnimation(Key.Sky);
	}
	
	public void reset()
	{
		for (Key key : Key.values())
		{
			key.setX(DEFAULT_X);
		}
	}
	
	/**
	 * Add animation
	 * @param key The key of the animation we want to add
	 */
	private void addAnimation(final Key key)
	{
		//create animation of key
		Animation animation = new Animation(
			Images.getImage(Assets.ImageGameKey.sheet), 
			key.animationX, 
			key.animationY, 
			key.animationW, 
			key.animationH
		);
		
		//no need to loop
		animation.setLoop(false);
		
		//no delay either
		animation.setDelay(0);
		
		//add animation to the sprite sheet
		super.getSpritesheet().add(key, animation);
	}

	@Override
	public void dispose()
	{
		super.dispose();
	}
	
	@Override
	public void update() throws Exception 
	{
		//update the scroll position of some of the objects
		Key.Ground.setX(Key.Ground.getX() - DEFAULT_SCROLL_X);
		Key.Bush.setX(Key.Bush.getX() - DEFAULT_SCROLL_X);
		
		//adjust if moving off the screen
		if (Key.Ground.getX() < 0)
			Key.Ground.setX(GamePanel.WIDTH);
		if (Key.Bush.getX() < 0)
			Key.Bush.setX(GamePanel.WIDTH);
	}
	
	@Override
	public void render(final Canvas canvas) throws Exception
	{
		//render the sky first
		renderAnimation(canvas, Key.Sky);
		
		//then render the clouds
		renderAnimation(canvas, Key.Cloud);
		
		//then render the bushes
		renderAnimation(canvas, Key.Bush);
		
		//then render the ground
		renderAnimation(canvas, Key.Ground);
	}
	
	/**
	 * Render the specific animation
	 * @param canvas
	 * @param key
	 * @throws Exception
	 */
	private void renderAnimation(final Canvas canvas, final Key key) throws Exception
	{
		super.getSpritesheet().setKey(key);
		super.setWidth(key.animationW);
		super.setHeight(key.animationH);
		
		switch (key)
		{
			case Bush:
			case Ground:
				super.setY(key.getY());
				
				super.setX(key.getX());
				super.render(canvas);
				super.setX(key.getX() + key.animationW);
				super.render(canvas);
				super.setX(key.getX() - key.animationW);
				super.render(canvas);
				break;
			
			default:
				super.setX(key.getX());
				super.setY(key.getY());
				super.render(canvas);
				break;
				
		}
	}
}