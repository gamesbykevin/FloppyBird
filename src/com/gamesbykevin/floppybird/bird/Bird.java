package com.gamesbykevin.floppybird.bird;

import com.gamesbykevin.androidframework.anim.Animation;
import com.gamesbykevin.androidframework.resources.Images;
import com.gamesbykevin.floppybird.assets.Assets;
import com.gamesbykevin.floppybird.common.ICommon;
import com.gamesbykevin.floppybird.entity.Entity;
import com.gamesbykevin.floppybird.panel.GamePanel;

import android.graphics.Canvas;

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
	
	/**
	 * Array of x-coordinates that make up the bird, used for collision detection
	 */
	private static final int[] BIRD_X_POINTS = new int[] {-24, -13, 6, 17, 24, 24, 37, 27, 18, -1, -23, -25};
	
	/**
	 * Array of y-coordinates that make up the bird, used for collision detection
	 */
	private static final int[] BIRD_Y_POINTS = new int[] {-4, -15, -17, -12, -5, 3, 10, 10, 14, 19, 10, 5};
	
	/**
	 * Default constructor to create a new bird
	 */
	public Bird()
	{
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
		setStart(false);
		
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
		//flag start true
		setStart(true);
		
		//set the y-velocity
		super.setDY(JUMP_HEIGHT_MAX);
		
		//reset the current animation
		super.getSpritesheet().get().reset();
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
		final float progress = (current / range);
		
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
	}
}