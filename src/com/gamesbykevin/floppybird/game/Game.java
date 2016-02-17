package com.gamesbykevin.floppybird.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Vibrator;
import android.view.MotionEvent;

import com.gamesbykevin.androidframework.resources.Audio;
import com.gamesbykevin.androidframework.resources.Font;
import com.gamesbykevin.androidframework.resources.Images;
import com.gamesbykevin.floppybird.assets.Assets;
import com.gamesbykevin.floppybird.panel.GamePanel;
import com.gamesbykevin.floppybird.screen.OptionsScreen;
import com.gamesbykevin.floppybird.screen.ScreenManager;
import com.gamesbykevin.floppybird.screen.ScreenManager.State;
import com.gamesbykevin.floppybird.storage.score.Score;

/**
 * The main game logic will happen here
 * @author ABRAHAM
 */
public final class Game implements IGame
{
    //our main screen object reference
    private final ScreenManager screen;
    
    //paint object to draw text
    private Paint paint;
    
    //the paint object we use to render the hint
    private Paint paintHint;
    
    //is the game being reset
    private boolean reset = false;
    
    //has the player been notified (has the user seen the loading screen)
    private boolean notify = false;
    
    //is the game over?
    private boolean gameover = false;
    
    //track the best score for each mode index
    private Score scoreboard;
    
    //the duration we want to vibrate the phone for
    private static final long VIBRATION_DURATION = 500L;
    
    /**
     * Create our game object
     * @param screen The main screen
     * @throws Exception
     */
    public Game(final ScreenManager screen) throws Exception
    {
        //our main screen object reference
        this.screen = screen;
        
        //create a new score board
        this.scoreboard = new Score(screen.getScreenOptions(), screen.getPanel().getActivity());
    }
    
    /**
     * Get the main screen object reference
     * @return The main screen object reference
     */
    public ScreenManager getScreen()
    {
        return this.screen;
    }
    
    /**
     * Is the game over?
     * @return true = yes, false = no
     */
    private boolean hasGameover()
    {
    	return this.gameover;
    }
    
    /**
     * Flag the game over
     * @param gameover true = yes, false = no
     */
    private void setGameover(final boolean gameover)
    {
    	this.gameover = gameover;
    }
    
    /**
     * Get the score board
     * @return The object containing the personal best records
     */
    private Score getScoreboard()
    {
    	return this.scoreboard;
    }
    
    /**
     * Reset the game
     */
    private void reset() 
    {
    	//make sure we have notified first
    	if (hasNotify())
    	{
        	//flag reset false
        	setReset(false);
        	
        	//flag game over false
        	setGameover(false);
        	
    		//reset depending on the game mode
    		switch (getScreen().getScreenOptions().getIndex(OptionsScreen.Key.Mode))
    		{
    		
    		}
    	}
    }
    
    /**
     * Flag reset, we also will flag notify to false if reset is true
     * @param reset true to reset the game, false otherwise
     */
    @Override
    public void setReset(final boolean reset)
    {
    	this.reset = reset;
    	
    	//flag that the user has not been notified, since we are resetting
    	if (hasReset())
    		setNotify(false);
    }
    
    /**
     * Do we have reset flagged?
     * @return true = yes, false = no
     */
    public boolean hasReset()
    {
    	return this.reset;
    }
    
    /**
     * Flag notify
     * @param notify True if we notified the user, false otherwise
     */
    private void setNotify(final boolean notify)
    {
    	this.notify = notify;
    }
    
    /**
     * Do we have notify flagged?
     * @return true if we notified the user, false otherwise
     */
    protected boolean hasNotify()
    {
    	return this.notify;
    }
    
    /**
     * Get the paint object
     * @return The paint object used to draw text in the game
     */
    public Paint getPaint()
    {
    	//if the object has not been created yet
    	if (this.paint == null)
    	{
            //create new paint object
            this.paint = new Paint();
            //this.paint.setTypeface(Font.getFont(Assets.FontGameKey.Default));
            this.paint.setTextSize(48f);
            this.paint.setColor(Color.WHITE);
            this.paint.setLinearText(false);
    	}
    	
        return this.paint;
    }
    
    /**
     * Get the paint object
     * @return The paint object we use for the hint
     */
    private Paint getPaintHint()
    {
    	if (this.paintHint == null)
    	{
    		this.paintHint = new Paint();
    		this.paintHint.setAlpha(255);
    	}
    	
    	return this.paintHint;
    }
    
    @Override
    public void update(final int action, final float x, final float y) throws Exception
    {
    	//if reset we can't continue
    	if (hasReset())
    		return;
    	
    	if (action == MotionEvent.ACTION_UP)
    	{
    		
    	}
    	
		if (action == MotionEvent.ACTION_DOWN)
		{
			
		}
		else if (action == MotionEvent.ACTION_MOVE)
    	{
			
    	}
    }
    
    /**
     * Update game
     * @throws Exception 
     */
    public void update() throws Exception
    {
        //if we are to reset the game
        if (hasReset())
        {
        	//reset the game
        	reset();
        }
        else
        {
			//if the game hasn't been flagged over
			if (!hasGameover())
			{
				//flag game over true
				setGameover(true);
				
        		//change the state
        		getScreen().setState(State.GameOver);
			}
        }
    }
    
    /**
     * Vibrate the phone if the setting is enabled
     */
    private void vibrate()
    {
		//make sure vibrate option is enabled
		if (getScreen().getScreenOptions().getIndex(OptionsScreen.Key.Vibrate) == 0)
		{
    		//get our vibrate object
    		Vibrator v = (Vibrator) getScreen().getPanel().getActivity().getSystemService(Context.VIBRATOR_SERVICE);
    		 
			//vibrate for a specified amount of milliseconds
			v.vibrate(VIBRATION_DURATION);
		}
    }
    
    /**
     * Render game elements
     * @param canvas Where to write the pixel data
     * @throws Exception 
     */
    @Override
    public void render(final Canvas canvas) throws Exception
    {
    	if (hasReset())
    	{
			//render loading screen
			//canvas.drawBitmap(Images.getImage(Assets.ImageMenuKey.Splash), 0, 0, null);
			
			//flag that the user has been notified
			setNotify(true);
    	}
    	else
    	{
    		
    	}
    }
    
    @Override
    public void dispose()
    {
        this.paint = null;
        
        if (this.scoreboard != null)
        {
        	this.scoreboard.dispose();
        	this.scoreboard = null;
        }
    }
}