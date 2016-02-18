package com.gamesbykevin.floppybird.bird;

import com.gamesbykevin.androidframework.anim.Animation;
import com.gamesbykevin.androidframework.base.Entity;
import com.gamesbykevin.androidframework.resources.Images;
import com.gamesbykevin.floppybird.assets.Assets;
import com.gamesbykevin.floppybird.common.ICommon;
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
	public static final int START_Y = 75;
	
	/**
	 * The duration between each animation
	 */
	private static final long ANIMATION_DELAY = 250;
	
	//rotation (degrees)
	private float rotation = 0;
	
	public Bird()
	{
		//add these animations
		addAnimation(Assets.ImageGameKey.bird1, 150, 103);
		addAnimation(Assets.ImageGameKey.bird2, 150, 103);
		addAnimation(Assets.ImageGameKey.bird3, 150, 108);
		addAnimation(Assets.ImageGameKey.bird4, 150, 107);
		
		//reset
		reset();
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
	
	/**
	 * Assign the rotation
	 * @param rotation The desired rotation (degrees)
	 */
	public void setRotation(final float rotation)
	{
		this.rotation = rotation;
	}
	
	/**
	 * Get the rotation
	 * @return The current rotation (degrees)
	 */
	public float getRotation()
	{
		return this.rotation;
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
	}
	
	@Override
	public void update() throws Exception 
	{
		
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