package com.gamesbykevin.floppybird.bird;

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

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

public class Bird extends Entity implements ICommon
{
	/**
	 * The start x-coordinate for the bird
	 */
	public static final int START_X = 100;
	
	/**
	 * The start y-coordinate for the bird
	 */
	public static final int START_Y = 100;
	
	/**
	 * The duration between each animation
	 */
	private static final long ANIMATION_DELAY = 75L;
	
	/**
	 * The number of pixels to climb when jumping
	 */
	private static final int JUMP_HEIGHT_MAX = -10;
	
	/**
	 * The number of pixels to fall when falling
	 */
	private static final int DROP_HEIGHT_MAX = 22;
	
	//did the game start
	private boolean start = false;
	
	//is the bird dead
	private boolean dead = false;
	
	/**
	 * The maximum amount of fuel allowed
	 */
	private static final int FUEL_MAX = 500;
	
	/**
	 * The allowed amount of fuel to add
	 */
	private static final int FUEL_ADD = 40;
	
	//the amount of fuel the bird has
	private int fuel = FUEL_MAX;
	
	/**
	 * Array of x-coordinates that make up the bird, used for collision detection
	 */
	private static final int[] BIRD_X_POINTS = new int[] {-24, -13, 6, 17, 24, 24, 37, 27, 18, -1, -23, -25};
	
	/**
	 * Array of y-coordinates that make up the bird, used for collision detection
	 */
	private static final int[] BIRD_Y_POINTS = new int[] {-4, -15, -17, -12, -5, 3, 10, 10, 14, 19, 10, 5};
	
	
	/**
	 * The width of the fuel tank we will render
	 */
	private static final int FUEL_WIDTH = (int)(GamePanel.WIDTH * .75);
	
	//the width of the fuel tank
	private int width =  FUEL_WIDTH;
	
	/**
	 * The starting x-coordinate of the fuel tank
	 */
	private static final int FUEL_X = 100;
	
	/**
	 * The height of the fuel tank
	 */
	private static final int FUEL_HEIGHT = Background.GROUND_HEIGHT / 2;
	
	/**
	 * The starting y-coordinate of the fuel tank
	 */
	private static final int FUEL_Y = GamePanel.HEIGHT - FUEL_HEIGHT - (FUEL_HEIGHT / 2);
	
	/**
	 * The transparency of the color in the fuel tank
	 */
	private static final int FUEL_ALPHA = 120;
	
	//the paint object to render the fuel tank
	private Paint paint;
	
	//game reference object
	private final Game game;
	
	/**
	 * Default constructor to create a new bird
	 */
	public Bird(final Game game)
	{
		//store our game reference object
		this.game = game;
		
		//create paint object
		this.paint = new Paint();
		this.paint.setStyle(Style.FILL);
		
		//add these animations
		addAnimation(Assets.ImageGameKey.bird1, 75, 52);
		addAnimation(Assets.ImageGameKey.bird2, 75, 52);
		addAnimation(Assets.ImageGameKey.bird3, 75, 54);
		addAnimation(Assets.ImageGameKey.bird4, 75, 54);
		
		//reset
		reset();
		
		super.updateOutline(BIRD_X_POINTS, BIRD_Y_POINTS);
	}
	
	/**
	 * Increase our fuel amount
	 */
	public void addFuel()
	{
		setFuel(getFuel() + FUEL_ADD);
	}
	
	/**
	 * Assign the fuel
	 * @param fuel The desired amount
	 */
	private final void setFuel(final int fuel)
	{
		this.fuel = fuel;
		
		//make sure we stay in the boundary
		if (getFuel() < 0)
			setFuel(0);
		if (getFuel() > FUEL_MAX)
			setFuel(FUEL_MAX);
	}
	
	/**
	 * Get the fuel
	 * @return The amount of fuel left
	 */
	public final int getFuel()
	{
		return this.fuel;
	}
	
	/**
	 * Flag the bird dead
	 * @param dead true = yes, false = no
	 */
	public final void setDead(final boolean dead)
	{
		//if we weren't dead and am now for the first time
		if (!isDead() && dead)
		{
			//stop the music
			Audio.stop();
			
			//play dead audio sound effect
			Audio.play(Assets.AudioGameKey.Dead);
			
			//flag game over
			game.setGameover(true);
			
			//vibrate phone
			game.vibrate();
		}

		
		this.dead = dead;
	}
	
	/**
	 * Is the bird dead?
	 * @return true = yes, false = no
	 */
	public final boolean isDead()
	{
		return this.dead;
	}
	
	/**
	 * Flag the bird to start
	 * @param start true = yes, false = no
	 */
	public final void setStart(final boolean start)
	{
		this.start = start;
	}
	
	/**
	 * Has the bird started
	 * @return true = yes, false = no
	 */
	public final boolean hasStart()
	{
		return this.start;
	}
	
	/**
	 * Add the animation to the sprite sheet
	 * @param key The image key
	 * @param w width of the animation
	 * @param h height of the animation
	 */
	private final void addAnimation(final Assets.ImageGameKey key, final int w, final int h)
	{
		final int cols = 4;
		final int rows = 1;
		
		//create animation object
		Animation animation = new Animation(Images.getImage(key), 0, 0, w, h, cols, rows, cols);
		
		//we don't want this to loop
		animation.setLoop(false);
		
		//set the delay between each frame
		animation.setDelay(ANIMATION_DELAY);
		
		//add animation to the sprite sheet
		super.getSpritesheet().add(key, animation);
	}
	
	/**
	 * Pick a random bird animation.<br>
	 * Reset the location and rotation of the bird
	 */
	public final void reset()
	{
		//the bird did not start
		setStart(false);
		
		//the bird is not dead
		setDead(false);
		
		//reset location
		setX(START_X);
		setY(START_Y);
		
		//reset the rotation
		setRotation(0);
		
		//pick a random animation
		switch(GamePanel.RANDOM.nextInt(4))
		{
			default:
			case 0:
				super.getSpritesheet().setKey(Assets.ImageGameKey.bird1);
				break;
				
			case 1:
				super.getSpritesheet().setKey(Assets.ImageGameKey.bird2);
				break;
				
			case 2:
				super.getSpritesheet().setKey(Assets.ImageGameKey.bird3);
				break;
				
			case 3:
				super.getSpritesheet().setKey(Assets.ImageGameKey.bird4);
				break;
		}
		
		//set the width based on the current animation
		super.setWidth(getSpritesheet().get().getImage().getWidth());
		
		//set the height based on the current animation
		super.setHeight(getSpritesheet().get().getImage().getHeight());
		
		//reset the fuel
		setFuel(FUEL_MAX);
		
		//set the color
		assignFuelColor();
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
	}
	
	/**
	 * Jump the bird
	 */
	public final void jump()
	{
		//if the bird is dead we can't jump
		if (isDead())
			return;
		
		//flag start true
		setStart(true);
		
		//set the y-velocity
		super.setDY(JUMP_HEIGHT_MAX);
		
		//reset the current animation
		super.getSpritesheet().get().reset();
	}
	
	/**
	 * Assign the appropriate color to display by the amount of fuel remaining
	 */
	private void assignFuelColor()
	{
		//find out how full/empty the fuel tank is
		final float progress = (((float)getFuel() / (float)FUEL_MAX) * 100);
		
		if (progress >= 75)
		{
			paint.setColor(Color.GREEN);
		}
		else if (progress >= 50)
		{
			paint.setColor(Color.YELLOW);
		}
		else if (progress >= 25)
		{
			paint.setColor(Color.argb(255, 255, 165, 0));
		}
		else
		{
			paint.setColor(Color.RED);
		}
		
		//set the transparency of the color
		paint.setAlpha(FUEL_ALPHA);
	}
	
	@Override
	public void update() throws Exception 
	{
		//if we did not start
		if (!hasStart())
			return;
		
		//update the y-coordinate
		setY(getY() + getDY());
		
		//keep the bird on the screen
		if (getY() < 0)
			setY(0);
		
		//increase the y-velocity
		setDY(getDY() + 1);
		
		//limit how fast we can jump
		if (getDY() < JUMP_HEIGHT_MAX)
			setDY(JUMP_HEIGHT_MAX);
		
		//limit how fast we can fall
		if (getDY() > DROP_HEIGHT_MAX)
			setDY(DROP_HEIGHT_MAX);
		
		//update the rotation based on the y-velocity
		updateRotation();
		
		//update the animation
		getSpritesheet().update();
		
		//make sure the bird didn't hit the ground
		if (getY() + getHeight() > GamePanel.HEIGHT - Background.GROUND_HEIGHT)
		{
			//position bird right above the ground
			setY(GamePanel.HEIGHT - Background.GROUND_HEIGHT - getHeight());
			
			//flag the bird as dead
			setDead(true);
		}
		else if (game.getScreen().getScreenOptions().getIndex(OptionsScreen.Key.Mode) == 1)
		{
			//if no more fuel, game over
			if (getFuel() <= 0)
				setDead(true);
			
			//decrease the fuel
			setFuel(getFuel() - 1);
			
			//set the color
			assignFuelColor();
			
			//determine the width of the fuel tank
			this.width = (int) (FUEL_WIDTH * ((double)getFuel() / (double)FUEL_MAX));
		}
	}
	
	/**
	 * Rotate the bird depending on the current y-velocity
	 */
	private void updateRotation()
	{
		//determine the velocity range
		final float range = ((float)DROP_HEIGHT_MAX - (float)JUMP_HEIGHT_MAX);
		
		//find out how far we are from the DROP_HEIGHT_MAX
		final float current = (float)DROP_HEIGHT_MAX - (float)getDY();
		
		//find out how much we have progressed towards the full range
		float progress = (current / range);
		
		if (isDead())
			progress = 0;
		
		//now we can assign the rotation
		setRotation(45 - (90 * progress));
	}
	
	@Override
	public void render(final Canvas canvas) throws Exception
	{
		//save the canvas here so the rotation changes below only affect this object
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		
		//rotate the canvas
        canvas.rotate(getRotation(), (float)(getX() + (getWidth() / 2)), (float)(getY() + (getHeight() / 2)));
        
        //render the current animation
        super.render(canvas);
        
        //restore canvas to previous state so only this object is affected
        canvas.restore();
        
		//check the game mode is challenge to see if we render the fuel tank
		if (game.getScreen().getScreenOptions().getIndex(OptionsScreen.Key.Mode) == 1)
			canvas.drawRect(FUEL_X, FUEL_Y, FUEL_X + width, FUEL_Y + FUEL_HEIGHT, paint);
	}
}