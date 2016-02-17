package com.gamesbykevin.floppybird.game;

import com.gamesbykevin.androidframework.resources.Disposable;

import android.graphics.Canvas;

/**
 * Game interface methods
 * @author GOD
 */
public interface IGame extends Disposable
{
	/**
	 * Flag reset
	 * @param reset Do we want to reset the game true = yes, false = no
	 * @throws Exception
	 */
    public void setReset(final boolean reset) throws Exception;
    
    /**
     * Logic to update element
     */
    public void update() throws Exception;
    
    /**
     * Logic to render the game
     * @param canvas Canvas where we draw
     * @throws Exception
     */
    public void render(final Canvas canvas) throws Exception;
    
    /**
     * Update the game based on a motion event
     * @param action The action of the MotionEvent
     * @param x (x-coordinate)
     * @param y (y-coordinate)
     * @throws Exception
     */
    public void update(final int action, final float x, final float y) throws Exception;
}
