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
	/**
	 * The height of the ground
	 */
	public static final int GROUND_HEIGHT = 58;
	
	/**
	 * The Key for each image etc...
	 */
	private enum Key
	{
		Ground(0, 1002, 800, GROUND_HEIGHT, 422, true), 
		Sky(0, 0, 800, 450, 0, false), 
		Cloud(0, 450, 800, 447, 0, true), 
		Bush(0, 897, 800, 105, 351, true);
		
		//where the animation is located
		private final int animationX, animationY, animationW, animationH;
		
		//the current x position
		private int x, y;
		
		//do we scroll
		private boolean scroll;
		
		private Key(int animationX, int animationY, int animationW, int animationH, int startY, boolean scroll)
		{
			this.animationX = animationX;
			this.animationY = animationY;
			this.animationW = animationW;
			this.animationH = animationH;
			
			//do we scroll this
			this.scroll = scroll;
			
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
	
	/**
	 * Default x-coordinate for each animation
	 */
	private static final int DEFAULT_X = 0;
	
	/**
	 * The speed at which the ground/bushes move
	 */
	public static final int DEFAULT_X_SCROLL = 10;
	
	/**
	 * The speed at which the clouds move
	 */
	public static final int CLOUD_X_SCROLL = 2;
	
	//the current scroll speed
	private int scrollX = 0;
	
	public Background() 
	{
		super();
		
		//add animations
		addAnimation(Key.Bush);
		addAnimation(Key.Cloud);
		addAnimation(Key.Ground);
		addAnimation(Key.Sky);
		
		//reset
		reset();
	}
	
	public final void reset()
	{
		for (Key key : Key.values())
		{
			key.setX(DEFAULT_X);
		}
		
		//assign the scroll speed
		setScrollX(DEFAULT_X_SCROLL);
	}
	
	/**
	 * Assign the x scroll speed
	 * @param scrollX The speed to scroll the x-coordinate
	 */
	public void setScrollX(final int scrollX)
	{
		this.scrollX = scrollX;
	}
	
	/**
	 * Get the scroll speed
	 * @return The speed to scroll the x-coordinate
	 */
	public int getScrollX()
	{
		return this.scrollX;
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
		for (Key key : Key.values())
		{
			//if we are to scroll the animation
			if (key.scroll)
			{
				//determine the scroll speed
				switch (key)
				{
					case Bush:
					case Ground:
						setScrollX(DEFAULT_X_SCROLL);
						break;
						
					case Cloud:
						setScrollX(CLOUD_X_SCROLL);
						break;
					
					default:
						setScrollX(DEFAULT_X_SCROLL);
						break;
				}
				
				//update x-coordinate
				key.setX(key.getX() - getScrollX());
				
				//adjust if we move off the screen
				if (key.getX() < 0)
					key.setX(GamePanel.WIDTH);
			}
		}
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
			case Cloud:
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