package com.gamesbykevin.floppybird.common;

import com.gamesbykevin.androidframework.resources.Disposable;

import android.graphics.Canvas;

public interface ICommon extends Disposable
{
	/**
	 * Update the entity
	 * @throws Exception
	 */
	public void update() throws Exception;

	/**
	 * Render the entity
	 * @param canvas Object used to render pixels
	 */
	public void render(final Canvas canvas) throws Exception;
}
