package com.gamesbykevin.floppybird.pipes;

import java.util.ArrayList;

import com.gamesbykevin.androidframework.anim.Animation;
import com.gamesbykevin.androidframework.resources.Images;
import com.gamesbykevin.floppybird.assets.Assets;
import com.gamesbykevin.floppybird.background.Background;
import com.gamesbykevin.floppybird.common.ICommon;
import com.gamesbykevin.floppybird.entity.Entity;
import com.gamesbykevin.floppybird.game.Game;
import com.gamesbykevin.floppybird.panel.GamePanel;

import android.graphics.Canvas;

public final class Pipes extends Entity implements ICommon 
{
	/**
	 * The width of the pipe(s)
	 */
	public static final int PIPE_WIDTH = 89;
	
	/**
	 * The height of the pipe(s)
	 */
	public static final int PIPE_HEIGHT = 480;
	
	/**
	 * The time delay until we add a pipe
	 */
	public static final long PIPE_DELAY = 1300L;
	
	//keep track of the time
	private long time;
	
	/**
	 * The y-pixel difference between pipes for normal difficulty
	 */
	public static final int PIPE_GAP_NORMAL = 120;
	
	/**
	 * The y-pixel difference between pipes for hard difficulty
	 */
	public static final int PIPE_GAP_HARD = 100;
	
	/**
	 * The y-pixel difference between pipes for easy difficulty
	 */
	public static final int PIPE_GAP_EASY = 150;
	
	//the pipe gap setting chosen
	private int pipeGap = PIPE_GAP_NORMAL;
	
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
	 * The number of pipes allowed in the array list.<br>
	 * This will be determined by the width of the screen as well as the width of a single pipe
	 */
	private static final int PIPE_MAX = (GamePanel.WIDTH / PIPE_WIDTH);
	
	/**
	 * Array of x-coordinates that make up the top pipe, used for collision detection
	 */
	private static final int[] PIPE_TOP_X_POINTS = new int[] {-33,31,31,42,42,35,-37,-45,-45,-33};
	
	/**
	 * Array of y-coordinates that make up the top pipe, used for collision detection
	 */
	private static final int[] PIPE_TOP_Y_POINTS = new int[] {-240,-240,205,211,230,240,240,231,211,204};
	
	/**
	 * Array of x-coordinates that make up the bottom pipe, used for collision detection
	 */
	private static final int[] PIPE_BOTTOM_X_POINTS = new int[] {-37,35,42,42,31,31,-33,-33,-45,-45};
	
	/**
	 * Array of y-coordinates that make up the bottom pipe, used for collision detection
	 */
	private static final int[] PIPE_BOTTOM_Y_POINTS = new int[] {-240,-240,-232,-215,-206,240,240,-206,-215,-232};
	
	//game reference object
	private final Game game;
	
	/**
	 * This class will control the pipes in the game
	 */
	public Pipes(final Game game)
	{
		//store game reference
		this.game = game;
		
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
		//don't continue if the bird is dead or if the bird has not started
		if (game.getBird().isDead() || !game.getBird().hasStart())
			return;
		
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
				{
					//update the scrolling
					pipe.x -= Background.DEFAULT_X_SCROLL;
					
					//if the pipe was previously ahead, but am not any longer we add a point
					if (!pipe.cleared && pipe.x < game.getBird().getX())
					{
						//flag that we cleared the pipe
						pipe.cleared = true;
						
						//increase score
						game.getScoreboard().setCurrentScore(game.getScoreboard().getCurrentScore() + 1);
					}
				}
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
		
		//check for collision
		if (hasCollision(game.getBird()))
		{
			//flag game over
			game.setGameover(true);
			
			//flag bird dead
			game.getBird().setDead(true);
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
		for (Pipe pipe : getPipes())
		{
			//if the pipe is paused, we don't need to check
			if (pipe.pause)
				continue;
			
			//if the pipe is not close enough to the entity, we will skip it
			if (pipe.x > entity.getX() + entity.getWidth())
				continue;
			if (pipe.x + getWidth() < entity.getX())
				continue;
			
			//make sure the entity's outline is updated before checking collision
			entity.updateOutline();
			
			//only check collision with the top pipe if in range
			if (entity.getY() <= pipe.yTop + getHeight())
			{
				//set the location of the top pipe
				super.setX(pipe.x);
				super.setY(pipe.yTop);
				
				//update the outline
				updateOutline(PIPE_TOP_X_POINTS, PIPE_TOP_Y_POINTS);
				
				//if we have collision return true
				if (super.hasCollision(entity))
					return true;
			}
			
			//only check collision with the bottom pipe if in range
			if (entity.getY() + entity.getHeight() >= pipe.yBottom)
			{
				//set the location of the bottom pipe
				super.setX(pipe.x);
				super.setY(pipe.yBottom);
				
				//update the outline
				updateOutline(PIPE_BOTTOM_X_POINTS, PIPE_BOTTOM_Y_POINTS);
				
				//if we have collision return true
				if (super.hasCollision(entity))
					return true;
			}
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
		final int range = (int)((GamePanel.HEIGHT - PIPE_DISPLAY_MIN - getPipeGap() - Background.GROUND_HEIGHT - getHeight()) - minimumY);
		
		//pick the random starting location
		final int yTop = minimumY + (GamePanel.RANDOM.nextInt(range));
		
		//calculate the bottom pipe starting location
		final int yBottom = (int)(yTop + getHeight() + getPipeGap());
		
		/**
		 * If the size of the list exceeds the max lets see if we can reuse a pipe
		 */
		if (getPipes().size() > PIPE_MAX)
		{
			//check the list
			for (Pipe pipe : getPipes())
			{
				//if this pipe is paused, this will be our candidate
				if (pipe.pause)
				{
					//flag pause false
					pipe.pause = false;
					
					//flag that the bird did not clear the pipe
					pipe.cleared = false;
					
					//assign the x-coordinate
					pipe.x = x;
					
					//assign the y-coordinate top
					pipe.yTop = yTop;
					
					//assign the y-coordinate bottom
					pipe.yBottom = yBottom;
					
					//exit the loop
					break;
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
	
	/**
	 * Assign the pipe gap
	 * @param pipeGap The y-pixel distance between the top and bottom pipes
	 */
	public void setPipeGap(final int pipeGap)
	{
		this.pipeGap = pipeGap;
	}
	
	/**
	 * Get the pipe gap
	 * @return The y-pixel distance between the top and bottom pipes
	 */
	private int getPipeGap()
	{
		return this.pipeGap;
	}
	
	@Override
	public void reset()
	{
		//set a default animation
		super.getSpritesheet().setKey(Key.PipeTop);
		
		//assign the dimensions once, since both pipes will have the same dimensions
		super.setWidth(PIPE_WIDTH);
		super.setHeight(PIPE_HEIGHT);
		
		//make all pipes paused so they can be spawned
		for (Pipe pipe : getPipes())
		{
			pipe.pause = true;
		}
		
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
			super.render(canvas);
			
			//render the bottom pipe
			super.setY(pipe.yBottom);
			super.getSpritesheet().setKey(Key.PipeBottom);
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
		
		//did the bird clear this pipe
		private boolean cleared = false;
		
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