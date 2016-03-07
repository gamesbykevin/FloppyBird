package com.gamesbykevin.floppybird.pipes;

import java.util.ArrayList;

import com.gamesbykevin.androidframework.anim.Animation;
import com.gamesbykevin.androidframework.resources.Audio;
import com.gamesbykevin.androidframework.resources.Images;
import com.gamesbykevin.floppybird.assets.Assets;
import com.gamesbykevin.floppybird.background.Background;
import com.gamesbykevin.floppybird.common.ICommon;
import com.gamesbykevin.floppybird.entity.Entity;
import com.gamesbykevin.floppybird.game.Game;
import com.gamesbykevin.floppybird.panel.GamePanel;
import com.gamesbykevin.floppybird.screen.OptionsScreen;
import com.gamesbykevin.floppybird.storage.score.Score;

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
	 * The width of the fuel
	 */
	private static final int FUEL_WIDTH = 39;
	
	/**
	 * The height of the fuel
	 */
	private static final int FUEL_HEIGHT = 38;
	
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
	
	/**
	 * The minimum pixels that need to show for the pipe
	 */
	private static final int PIPE_DISPLAY_MIN = 50;
	
	/**
	 * Animation keys for the 2 pipe animations
	 */
	private enum Key
	{
		PipeTop, PipeBottom, Fuel
	}

	//list of pipes in play
	private ArrayList<Pipe> pipes;
	
	//the fuel for the specific game type
	private ArrayList<Fuel> fuels;
	
	//the pipe gap setting chosen
	private int pipeGap = PIPE_GAP_NORMAL;
	
	/**
	 * The max number of objects allowed<br>
	 * This will help be determined by the width of the screen as well as other factors
	 */
	private static final int MAX = (GamePanel.WIDTH / PIPE_WIDTH);
	
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
	
	/**
	 * Array of x-coordinates that make up the fuel, used for collision detection
	 */
	private static final int[] FUEL_X_POINTS = new int[] {-19, 20, 20, -19};
	
	/**
	 * Array of y-coordinates that make up the fuel, used for collision detection
	 */
	private static final int[] FUEL_Y_POINTS = new int[] {-19, -19, 19, 19};
	
	//game reference object
	private final Game game;
	
	/**
	 * The number of pixels required to spawn another pipe
	 */
	private static final int PIPE_PIXEL_SPAWN = 375;
	
	//current pixel progress that will determine if we spawn another pipe
	private int pipePixelProgress = 0;
	
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
		
		//add the fuel animation
		super.getSpritesheet().add(Key.Fuel, new Animation(Images.getImage(Assets.ImageGameKey.fuel)));
		
		//create new list of pipes
		this.pipes = new ArrayList<Pipe>();
		
		//create new list of fuel
		this.fuels = new ArrayList<Fuel>();
		
		//reset
		reset();
	}
	
	@Override
	public void update() throws Exception 
	{
		//don't continue if the bird is dead or if the bird has not started
		if (game.getBird().isDead() || !game.getBird().hasStart())
			return;
		
		//set the pipe dimensions first
		super.setWidth(PIPE_WIDTH);
		super.setHeight(PIPE_HEIGHT);
		
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
						//play sound
						Audio.play(Assets.AudioGameKey.Score);
						
						//flag that we cleared the pipe
						pipe.cleared = true;
						
						//increase score
						game.getScoreboard().setCurrentScore(game.getScoreboard().getCurrentScore() + 1);
						game.getDigits().setNumber(game.getScoreboard().getCurrentScore(), 0, Score.SCORE_Y, true);
					}
				}
			}
		}
		
		//set the fuel dimensions
		super.setWidth(FUEL_WIDTH);
		super.setHeight(FUEL_HEIGHT);
		
		//update the fuel in our list
		for (Fuel fuel : getFuel())
		{
			//if the fuel is no longer on the screen, we will pause it
			if (fuel.x + getWidth() < 0)
			{
				//pause the fuel
				fuel.pause = true;
			}
			else
			{
				//if not paused we can scroll
				if (!fuel.pause)
					fuel.x -= Background.DEFAULT_X_SCROLL;
			}
		}
		
		//increase the pipe pixel progress
		this.pipePixelProgress += Background.DEFAULT_X_SCROLL;
		
		//if we met the requirement for spawning
		if (this.pipePixelProgress >= PIPE_PIXEL_SPAWN)
		{
			//reset the progress
			this.pipePixelProgress = 0;
			
			//spawn
			spawn();
		}
		
		//check for fuel collision
		if (hasFuelCollision(game.getBird()))
		{
			//add fuel to the bird
			game.getBird().addFuel();
			
			//play sound effect
			Audio.play(Assets.AudioGameKey.Fuel);
		}
		else if (hasPipeCollision(game.getBird()))
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
	 * Get the fuel
	 * @return The list of fuel
	 */
	private ArrayList<Fuel> getFuel()
	{
		return this.fuels;
	}
	
	/**
	 * Do we have collision with any pipe?
	 * @param entity The entity we want to check
	 * @return true if the entity has collision with any pipe, false otherwise
	 */
	public boolean hasPipeCollision(final Entity entity)
	{
		//set the dimensions of the pipe
		super.setWidth(PIPE_WIDTH);
		super.setHeight(PIPE_HEIGHT);
		
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
	 * Do we have collision with any fuel?
	 * @param entity The entity we want to check
	 * @return true if the entity has collision with any fuel, false otherwise
	 */
	public boolean hasFuelCollision(final Entity entity)
	{
		//set the dimensions of the fuel
		super.setWidth(FUEL_WIDTH);
		super.setHeight(FUEL_HEIGHT);
		
		for (Fuel fuel : getFuel())
		{
			//if the fuel is paused, we don't need to check
			if (fuel.pause)
				continue;
			
			//if the fuel is not close enough to the entity, we will skip it
			if (fuel.x > entity.getX() + entity.getWidth())
				continue;
			if (fuel.x + getWidth() < entity.getX())
				continue;
			
			//make sure the entity's outline is updated before checking collision
			entity.updateOutline();
			
			//skip if the fuel is not in range
			if (fuel.y + getHeight() < entity.getY() || 
				fuel.y > entity.getY() + entity.getHeight())
				continue;
			
			//set the center as the position
			super.setX(fuel.x + (getWidth() / 2));
			super.setY(fuel.y + (getHeight() / 2));
			
			//now check the distance
			final double distance = super.getDistance(entity.getX() + (entity.getWidth() / 2), entity.getY() + (entity.getHeight() / 2));
			
			//don't check for collision if the fuel is not close enough
			if (distance > FUEL_WIDTH)
				continue;
			
			//set the location of the fuel
			super.setX(fuel.x);
			super.setY(fuel.y);
			
			//update the outline
			updateOutline(FUEL_X_POINTS, FUEL_Y_POINTS);
			
			//finally check for collision
			if (super.hasCollision(entity))
			{
				//flag it paused
				fuel.pause = true;
				
				//return true
				return true;
			}
		}
		
		//no collision was found
		return false;
	}
	
	/**
	 * Spawn a pipe (top & bottom)<br>
	 * We will also spawn the fuel here if playing challenge mode
	 */
	private void spawn()
	{
		//set the pipe dimensions first
		super.setWidth(PIPE_WIDTH);
		super.setHeight(PIPE_HEIGHT);
		
		//start at the far east
		int x = GamePanel.WIDTH;
		
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
		if (getPipes().size() > MAX)
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
		
		//check the game mode is challenge to see if we spawn fuel
		if (game.getScreen().getScreenOptions().getIndex(OptionsScreen.Key.Mode) == 1)
		{
			//pick random coordinate
			x += (PIPE_WIDTH + GamePanel.RANDOM.nextInt(PIPE_PIXEL_SPAWN - PIPE_WIDTH - FUEL_WIDTH));
			
			//make the y-coordinate close enough
			int y = yBottom - (getPipeGap() / 2);
			
			//pick random coordinate
			if (GamePanel.RANDOM.nextBoolean())
			{
				y -= GamePanel.RANDOM.nextInt(PIPE_DISPLAY_MIN);
			}
			else
			{
				y += GamePanel.RANDOM.nextInt(PIPE_DISPLAY_MIN);
			}
			
			//make sure the fuel stays on screen
			if (y < 0)
				y = 0;
			if (y > GamePanel.HEIGHT - Background.GROUND_HEIGHT - FUEL_HEIGHT)
				y = GamePanel.HEIGHT - Background.GROUND_HEIGHT - FUEL_HEIGHT;
			
			/**
			 * If the size of the list exceeds the max lets see if we can reuse the fuel
			 */
			if (getFuel().size() > MAX)
			{
				//check the list
				for (Fuel fuel : getFuel())
				{
					//if this fuel is paused, this will be our candidate
					if (fuel.pause)
					{
						//flag pause false
						fuel.pause = false;
						
						//assign the x-coordinate
						fuel.x = x;
						
						//assign the y-coordinate
						fuel.y = y;
						
						//exit the loop
						break;
					}
				}
			}
			else
			{
				//create the fuel
				Fuel fuel = new Fuel(x, y);
				
				//flag pause false
				fuel.pause = false;
				
				//add the fuel to the list
				getFuel().add(fuel);
			}
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
		
		//pause all fuel so it can be spawned if needed
		for (Fuel fuel : getFuel())
		{
			fuel.pause = true;
		}
		
		//reset the pipe progress
		this.pipePixelProgress = 0;
	}

	@Override
	public void dispose()
	{
		super.dispose();
	}

	@Override
	public void render(Canvas canvas) throws Exception 
	{
		//set the dimensions of the pipes
		super.setWidth(PIPE_WIDTH);
		super.setHeight(PIPE_HEIGHT);
		
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
		
		//set the dimensions of the fuel
		super.setWidth(FUEL_WIDTH);
		super.setHeight(FUEL_HEIGHT);
		
		//render the fuel
		for (Fuel fuel : getFuel())
		{
			//skip if paused
			if (fuel.pause)
				continue;
			
			//set coordinates
			super.setX(fuel.x);
			super.setY(fuel.y);
			
			//set the appropriate animation
			super.getSpritesheet().setKey(Key.Fuel);
			
			//render the fuel
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
	
	/**
	 * This class represents a fuel power up
	 */
	private class Fuel
	{
		//the location of the fuel
		private int x, y;
		
		//do we pause the fuel?
		private boolean pause = true;
		
		private Fuel(final int x, final int y)
		{
			this.x = x;
			this.y = y;
		}
	}
}