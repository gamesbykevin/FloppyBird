package com.gamesbykevin.floppybird.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Vibrator;
import android.view.MotionEvent;

import com.gamesbykevin.androidframework.resources.Audio;
import com.gamesbykevin.androidframework.resources.Images;
import com.gamesbykevin.floppybird.assets.Assets;
import com.gamesbykevin.floppybird.background.Background;
import com.gamesbykevin.floppybird.bird.Bird;
import com.gamesbykevin.floppybird.pipes.Pipes;
import com.gamesbykevin.floppybird.screen.OptionsScreen;
import com.gamesbykevin.floppybird.screen.ScreenManager;
import com.gamesbykevin.floppybird.screen.ScreenManager.State;
import com.gamesbykevin.floppybird.storage.score.Digits;
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
    
    //is the game being reset
    private boolean reset = false;
    
    //has the player been notified (has the user seen the loading screen)
    private boolean notify = false;
    
    //is the game over?
    private boolean gameover = false;
    
    //track the best score for each mode index
    private Score scoreboard;
    
    //object used to render a nice looking number
    private Digits digits;
    
    //the duration we want to vibrate the phone for
    private static final long VIBRATION_DURATION = 500L;
    
    //our bird
    private Bird bird;
    
    //collection of pipes
    private Pipes pipes;
    
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
        
        //create our bird
        this.bird = new Bird(this);
        
        //create our pipes container
        this.pipes = new Pipes(this);
        
        //create new instance
        this.digits = new Digits();
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
     * Get the digits
     * @return Object used to render numbers
     */
    public Digits getDigits()
    {
    	return this.digits;
    }
    
    /**
     * Get the bird
     * @return The bird in play
     */
    public Bird getBird()
    {
    	return this.bird;
    }
    
    /**
     * Get the pipes
     * @return The container for the pipes in play
     */
    public Pipes getPipes()
    {
    	return this.pipes;
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
    public void setGameover(final boolean gameover)
    {
    	this.gameover = gameover;
    	
    	//if the game is flagged as over 
    	if (hasGameover())
    		getScreen().setState(State.GameOver);
    }
    
    /**
     * Get the score board
     * @return The object containing the personal best records
     */
    public Score getScoreboard()
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
        	
        	if (getBird() != null)
        		getBird().reset();
        	
        	if (getPipes() != null)
        		getPipes().reset();
        	
        	//reset current score
        	getScoreboard().setCurrentScore(0);
        	getDigits().setNumber(0, 0, Score.SCORE_Y, true);
        	
    		//reset depending on the difficulty
    		switch (getScreen().getScreenOptions().getIndex(OptionsScreen.Key.Difficulty))
    		{
	    		//Normal
	    		case 0:
    			default:
    				getPipes().setPipeGap(Pipes.PIPE_GAP_NORMAL);
	    			break;
	    			
	    		//Hard
	    		case 1:
	    			getPipes().setPipeGap(Pipes.PIPE_GAP_HARD);
	    			break;
	    			
	    		//Easy
	    		case 2:
	    			getPipes().setPipeGap(Pipes.PIPE_GAP_EASY);
	    			break;
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
    
    @Override
    public void update(final int action, final float x, final float y) throws Exception
    {
    	//if reset we can't continue
    	if (hasReset())
    		return;
    	
    	//if the game is over, we can't continue
    	if (hasGameover())
    		return;
    	
    	if (action == MotionEvent.ACTION_UP)
    	{
    		if (getBird() != null)
    		{
    			//did the bird start yet
    			final boolean started = getBird().hasStart();
    			
    			//start jumping
    			getBird().jump();
    			
    			if (!started)
    				getPipes().resetTime();
    		}
    	}
    	else if (action == MotionEvent.ACTION_DOWN)
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
        	//update the bird
    		getBird().update();
    		
    		//update the pipes
    		getPipes().update();
    		
    		//the background should be scrolling if the bird is not dead
        	if (!getBird().isDead())
        		getScreen().getBackground().update();
        }
    }
    
    /**
     * Vibrate the phone if the setting is enabled
     */
    public void vibrate()
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
			canvas.drawBitmap(Images.getImage(Assets.ImageMenuKey.Splash), 0, 0, null);
			
			//flag that the user has been notified
			setNotify(true);
    	}
    	else
    	{
    		//render pipes
        	if (getPipes() != null)
        		getPipes().render(canvas);
        	
        	//render bird
    		if (getBird() != null)
    			getBird().render(canvas);
    		
    		//render current score as long as the bird is alive
    		if (getDigits() != null && !getBird().isDead())
    			getDigits().render(canvas);
    		
    		//render the ground
    		getScreen().getBackground().renderAnimation(canvas, Background.Key.Ground);
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
        
        if (this.bird != null)
        {
        	this.bird.dispose();
        	this.bird = null;
        }
    }
}