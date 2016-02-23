package com.gamesbykevin.floppybird.entity;

import com.gamesbykevin.floppybird.panel.GamePanel;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Region;

public class Entity extends com.gamesbykevin.androidframework.base.Entity 
{
	//this will be the outline of our entity used for collision detection
	private Path outline;
	
	//the base coordinates for this entity
	private int[] xpoints, ypoints;

	//the region used for collision detection
	private Region region, clip, other;
	
	//rotation (degrees)
	private float rotation = 0;
	
	/**
	 * Default constructor
	 */
	public Entity() 
	{
		super();
		
		//update outline
		this.updateOutline();
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		region = null;
		clip = null;
		other = null;
		xpoints = null;
		ypoints = null;
		outline = null;
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
	
	/**
	 * Update the outline with the parameters defining the outline
	 * @param xpoints array of x-coordinates
	 * @param ypoints array of y-coordinates
	 */
	protected final void updateOutline(final int[] xpoints, final int[] ypoints)
	{
		this.xpoints = xpoints;
		this.ypoints = ypoints;
		
		//update outline
		this.updateOutline();
	}
	
	/**
	 * Update the outline based on the facing rotation and the current position
	 */
	public final void updateOutline()
	{
		//if the array is null, no need to continue
		if (xpoints == null || ypoints == null)
			return;
		
		//remove the previous coordinates
		getOutline().reset();
		
		//convert the rotation from degrees to radian's
		final double radians = Math.toRadians(getRotation());
		
		//we will offset from the center of the entity
		final double centerX = getX() + (getWidth() / 2);
		final double centerY = getY() + (getHeight() / 2);
		
		//update the coordinates of the outline
		for (int index = 0; index < xpoints.length; index++)
		{
            //determine the new coordinates for the outline based on the rotation
            final double newX = (xpoints[index] * Math.cos(radians)) - (ypoints[index] * Math.sin(radians));
            final double newY = (xpoints[index] * Math.sin(radians)) + (ypoints[index] * Math.cos(radians));
            
            //add the points to the path accordingly
            if (index == 0)
            {
            	getOutline().moveTo((int)(centerX + newX), (int)(centerY + newY));
            }
            else
            {
            	getOutline().lineTo((int)(centerX + newX), (int)(centerY + newY));
            }
		}
	}
	
	/**
	 * Get the outline
	 * @return The outline for this entity that will be used for collision detection
	 */
	protected Path getOutline()
	{
		//create the outline if it does not exist
		if (this.outline == null)
			this.outline = new Path();
		
		return this.outline;
	}
	
	/**
	 * Do we have collision with the specified entity?
	 * @param entity The entity we want to check collision against
	 * @return true = yes, false otherwise
	 */
	public boolean hasCollision(final Entity entity)
	{
		//create the region if it is not created yet
		if (region == null)
			region = new Region();
		if (other == null)
			other = new Region();
		
		//the boundary of the screen
		if (clip == null)
			clip = new Region(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
		
		//set the region according to the current outline
		final boolean result1 = region.setPath(getOutline(), clip);
		
		//set the region for the entity's current outline
		final boolean result2 = other.setPath(entity.getOutline(), clip);
		
		//make sure the regions are not empty
		if (result1 && result2)
		{
			//make sure result is false, if true is the result, then the regions do not intersect
			if (!region.quickReject(other))
			{
				//if true, we have collision
				if (region.op(other, Region.Op.INTERSECT))
					return true;
			}
		}
		
		//there is no collision
		return false;
	}
	
	@Override
	public void render(final Canvas canvas) throws Exception
	{
		super.render(canvas);
	}
}