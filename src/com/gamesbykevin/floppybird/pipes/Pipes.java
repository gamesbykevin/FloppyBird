package com.gamesbykevin.floppybird.pipes;

import java.util.ArrayList;

import com.gamesbykevin.androidframework.anim.Animation;
import com.gamesbykevin.androidframework.base.Entity;
import com.gamesbykevin.androidframework.resources.Images;
import com.gamesbykevin.floppybird.assets.Assets;
import com.gamesbykevin.floppybird.background.Background;
import com.gamesbykevin.floppybird.common.ICommon;
import com.gamesbykevin.floppybird.panel.GamePanel;

import android.graphics.Canvas;
import android.graphics.Paint;

public final class Pipes extends Entity implements ICommon 
{
	/**
	 * The time delay until we add a pipe
	 */
	public static final long PIPE_DELAY = 1750L;
	
	//keep track of the time
	private long time;
	
	/**
	 * The y-pixel difference between pipes
	 */
	private static final int PIPE_GAP = 115;
	
	/**
	 * The minimum pixels that need to show for the pipe
	 */
	private static final int PIPE_DISPLAY_MIN = 50;
	
	/**
	 * Animation keys for the 2 pipe animations
	 */
	private enum Key
	{
		PipeTop, PipeBottom
	}

	//list of pipes in play
	private ArrayList<Pipe> pipes;
	
	/**
	 * The number of pipes allowed in the array list
	 */
	private static final int PIPE_MAX = 5;
	
	/**
	 * This class will control the pipes in the game
	 */
	public Pipes()
	{
		//add the pipe on the bottom
		super.getSpritesheet().add(Key.PipeBottom, new Animation(Images.getImage(Assets.ImageGameKey.pipe)));
		
		//add the pipe on the top
		super.getSpritesheet().add(Key.PipeTop, new Animation(Images.getImage(Assets.ImageGameKey.pipe1)));
		
		//create new list of pipes
		this.pipes = new ArrayList<Pipe>();
		
		//reset
		reset();
	}
	
	@Override
	public void update() throws Exception 
	{
		//update the pipes in our list
		for (Pipe pipe : getPipes())
		{
			//if the pipe is no longer on the screen, we will pause it
			if (pipe.x + getWidth() < 0)
			{
				//pause the pipe
				pipe.pause = true;
			}
			else
			{
				//if not paused we can scroll
				if (!pipe.pause)
					pipe.x -= Background.DEFAULT_X_SCROLL;
			}
		}
		
		//get the current time
		final long current = System.currentTimeMillis();
		
		//if enough time has passed
		if (current - time >= PIPE_DELAY)
		{
			//update the current time
			time = current;
			
			//spawn a pipe
			spawnPipe();
		}
	}

	/**
	 * Get the pipes
	 * @return The list of pipes
	 */
	private ArrayList<Pipe> getPipes()
	{
		return this.pipes;
	}
	
	/**
	 * Do we have collision with any pipe?
	 * @param entity The entity we want to check
	 * @return true if the entity has collision with any pipe, false otherwise
	 */
	public boolean hasCollision(final Entity entity)
	{
		//locate the 4 corners of the entity
		final int x1 = (int)(entity.getX());
		final int y1 = (int)(entity.getY());
		final int x2 = (int)(entity.getX() + entity.getWidth());
		final int y2 = (int)(entity.getY() + entity.getHeight());
		
		for (Pipe pipe : getPipes())
		{
			//if the pipe is paused, we don't need to check
			if (pipe.pause)
				continue;
			
			//if the pipe is not close enough to the entity, we will skip it
			if (pipe.x > x2)
				continue;
			if (pipe.x + getWidth() < x1)
				continue;
			
			//set the location of the top pipe
			super.setX(pipe.x);
			super.setY(pipe.yTop);
			
			//if the entity intersects the top pipe, we have collision
			if (getDestination().contains(x1, y1))
				return true;
			if (getDestination().contains(x1, y2))
				return true;
			if (getDestination().contains(x2, y1))
				return true;
			if (getDestination().contains(x2, y2))
				return true;
			
			//set the location of the bottom pipe
			super.setY(pipe.yBottom);
			
			//if the entity intersects the bottom pipe, we have collision
			if (getDestination().contains(x1, y1))
				return true;
			if (getDestination().contains(x1, y2))
				return true;
			if (getDestination().contains(x2, y1))
				return true;
			if (getDestination().contains(x2, y2))
				return true;
		}
		
		//no collision was found
		return false;
	}
	
	/**
	 * Spawn a pipe (top & bottom)
	 */
	private void spawnPipe()
	{
		//start at the far east
		final int x = GamePanel.WIDTH;
		
		//calculate the minimum y-coordinate
		final int minimumY = (int)(PIPE_DISPLAY_MIN - getHeight());
		
		//calculate the range
		final int range = (int)((GamePanel.HEIGHT - PIPE_DISPLAY_MIN - PIPE_GAP - Background.GROUND_HEIGHT - getHeight()) - minimumY);
		
		//pick the random starting location
		final int yTop = minimumY + (GamePanel.RANDOM.nextInt(range));
		
		//calculate the bottom pipe starting location
		final int yBottom = (int)(yTop + getHeight() + PIPE_GAP);
		
		/**
		 * If the size of the list exceeds the max lets see if we can reuse a pipe
		 */
		if (getPipes().size() > PIPE_MAX)
		{
			//check the list
			for (Pipe pipe : getPipes())
			{
				//if this pipe is paused, it is a candidate
				if (pipe.pause)
				{
					//flag pause false
					pipe.pause = false;
					
					//assign the x-coordinate
					pipe.x = x;
					
					//assign the y-coordinate top
					pipe.yTop = yTop;
					
					//assign the y-coordinate bottom
					pipe.yBottom = yBottom;
				}
			}
		}
		else
		{
			//create the pipe (top & bottom)
			Pipe pipe = new Pipe(x, yTop, yBottom);
			
			//flag pause false
			pipe.pause = false;
			
			//add the pipe to the list
			getPipes().add(pipe);
		}
	}
	
	@Override
	public void reset()
	{
		//set a default animation
		super.getSpritesheet().setKey(Key.PipeTop);
		
		//assign the dimensions
		super.setWidth(getSpritesheet().get().getImage().getWidth());
		
		//assign the dimensions
		super.setHeight(getSpritesheet().get().getImage().getHeight());
		
		//clear list of existing pipes
		getPipes().clear();
		
		//assign the current time
		resetTime();
	}

	/**
	 * Reset the timer that controls spawning the pipes
	 */
	public final void resetTime()
	{
		this.time = System.currentTimeMillis();
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
	}

	@Override
	public void render(Canvas canvas) throws Exception 
	{
		//render each pipe
		for (Pipe pipe : getPipes())
		{
			//skip this pipe if paused
			if (pipe.pause)
				continue;
			
			//both pipes will have the same x-coordinate
			super.setX(pipe.x);
			
			//render the top pipe
			super.setY(pipe.yTop);
			super.getSpritesheet().setKey(Key.PipeTop);
			
			canvas.drawRect(getDestination(), new Paint());
			
			super.render(canvas);
			
			//render the bottom pipe
			super.setY(pipe.yBottom);
			super.getSpritesheet().setKey(Key.PipeBottom);
			
			canvas.drawRect(getDestination(), new Paint());
			
			super.render(canvas);
		}
	}
	
	/**
	 * This class represents a single pipe
	 */
	private class Pipe
	{
		//coordinate where pipe(s) are
		private int x;
		
		//the location of the top and bottom
		private int yTop, yBottom;
		
		//pause the pipe scroll
		private boolean pause = true;
		
		private Pipe(final int x, final int yTop, final int yBottom)
		{
			//assign the x-coordinate
			this.x = x;
			
			//assign the y-coordinate top
			this.yTop = yTop;
			
			//assign the y-coordinate bottom
			this.yBottom = yBottom;
		}
	}
}