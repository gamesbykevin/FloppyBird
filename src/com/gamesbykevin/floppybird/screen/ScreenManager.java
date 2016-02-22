package com.gamesbykevin.floppybird.screen;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import com.gamesbykevin.androidframework.resources.Audio;

import com.gamesbykevin.androidframework.resources.Disposable;
import com.gamesbykevin.androidframework.resources.Images;
import com.gamesbykevin.androidframework.screen.Screen;
import com.gamesbykevin.floppybird.assets.Assets;
import com.gamesbykevin.floppybird.background.Background;
import com.gamesbykevin.floppybird.panel.GamePanel;

import java.util.HashMap;

/**
 * This class will contain the game screens
 * @author ABRAHAM
 */
public final class ScreenManager implements Screen, Disposable
{
    /**
     * These are the different states in our game
     */
    public enum State 
    {
        Ready, Running, Paused, Options, Exit, GameOver
    }
    
    //the current state of the game
    private State state = State.Ready;
    
    //our game panel
    private final GamePanel panel;
    
    //the screens in our main screen
    private HashMap<State, Screen> screens;
    
    //the paint object used for the button text
    private Paint paint;
    
    /**
     * The x-coordinate where we want the logo to be displayed
     */
    public static final int LOGO_X = 25;
    
    /**
     * The y-coordinate where we want the logo to be displayed
     */
    public static final int LOGO_Y = 50;
    
    /**
     * The x-coordinate where we want to start putting the buttons
     */
    public static final int BUTTON_X = 40;
    
    /**
     * The y-coordinate where we want to start putting the buttons
     */
    public static final int BUTTON_Y = 250;
    
    /**
     * The y-coordinate spacing between each button
     */
    public static final int BUTTON_Y_INCREMENT = MenuScreen.BUTTON_HEIGHT + (int)(MenuScreen.BUTTON_HEIGHT * .25);
    
    /**
     * The y-coordinate spacing between each button
     */
    public static final int BUTTON_X_INCREMENT = MenuScreen.BUTTON_WIDTH + (int)(MenuScreen.BUTTON_WIDTH * .25);
    
    /**
     * The alpha visibility to apply when darkening the background
     */
    public static final int ALPHA_DARK = 115;
    
    /**
     * Default font size
     */
    public static final float DEFAULT_FONT_SIZE = 20f;
    
    //the scrolling background
    private Background background;
    
    /**
     * Create our main screen
     * @param panel The reference to our game panel
     */
    public ScreenManager(final GamePanel panel)
    {
        //store our game panel reference
        this.panel = panel;
        
        //create new hash map
        this.screens = new HashMap<State, Screen>();
        this.screens.put(State.Ready, new MenuScreen(this));
        this.screens.put(State.Paused, new PauseScreen(this));
        this.screens.put(State.Exit, new ExitScreen(this));
        this.screens.put(State.Options, new OptionsScreen(this));
        this.screens.put(State.GameOver, new GameoverScreen(this));
        this.screens.put(State.Running, new GameScreen(this));
        
        //default to the ready state
        setState(State.Ready);
        
        //create the background
        this.background = new Background();
    }
    
    @Override
    public boolean update(final int action, final float x, final float y) throws Exception
    {
        return getScreen(getState()).update(action, x, y);
    }
    
    /**
     * Get the paint object
     * @return The paint object primarily used for the menus
     */
    public Paint getPaint()
    {
        if (paint == null)
        {
            //create new paint object
            this.paint = new Paint();
            
            //make the default font text bold
            this.paint.setTypeface(Typeface.DEFAULT_BOLD);
            
            //set the text size
            this.paint.setTextSize(DEFAULT_FONT_SIZE);

            //set the color
            this.paint.setColor(Color.BLACK);
        }
        
        //return object
        return this.paint;
    }
    
    /**
     * Update runtime logic here (if needed)
     * @throws Exception 
     */
    @Override
    public void update() throws Exception
    {
    	//update current screen
        getScreen(getState()).update();
        
        //only update the scrolling when playing the game
        if (getState() == State.Running)
        	this.background.update();
    }
    
    /**
     * Get the game panel
     * @return Our game panel object reference
     */
    public GamePanel getPanel()
    {
        return this.panel;
    }
    
    public State getState()
    {
        return this.state;
    }
    
    public Screen getScreen(final State state)
    {
        return screens.get(state);
    }
    
    public GameoverScreen getScreenGameover()
    {
        return (GameoverScreen)screens.get(State.GameOver);
    }
    
    public PauseScreen getScreenPaused()
    {
        return (PauseScreen)screens.get(State.Paused);
    }
    
    public GameScreen getScreenGame()
    {
        return (GameScreen)screens.get(State.Running);
    }
    
    public OptionsScreen getScreenOptions()
    {
        return (OptionsScreen)screens.get(State.Options);
    }
    
    /**
     * Change the state
     * @param state The state of the game. Running, Paused, Ready, Game Over, etc..
     */
    public void setState(final State state)
    {
    	try
    	{
	        //if pausing store the previous state
	        if (state == State.Paused)
	        {
	            //stop sound
	            Audio.stop();
	            
	            //set the previous state
	            getScreenPaused().setStatePrevious(getState());
	        }
	        else if (state == State.GameOver)
	        {
	            if (getState() != State.Paused)
	            {
	                //reset screen
	                getScreen(state).reset();
	            }
	        }
	        else if (state == State.Ready)
	        {
	        	//if we are on the menu for the first time or coming from any state besides the options
	        	if (getState() == null || getState() != State.Options)
	        	{
	        		//stop all sound
	        		Audio.stop();
	        		
	        		//play menu theme
	        		//Audio.play(Assets.AudioMenuKey.Theme, true);
	        	}
	        }
	        else if (state == State.Running)
	        {
	        	//stop all sound
	        	Audio.stop();
	        	
	        	//play song
	        	//Audio.play(Assets.AudioGameKey.Music, true);
	        }
    	}
    	finally
    	{
        	//assign the state
	        this.state = state;
    	}
    }
    
    public void render(final Canvas canvas) throws Exception
    {
        if (canvas != null)
        {
            //fill background
            canvas.drawColor(Color.BLACK);
            
            //draw the background
            background.render(canvas);
            
            //render the game
            getScreenGame().render(canvas);
            
            //render the appropriate screen
            switch (getState())
            {
                case Ready:
                	
                	//darken the background if the game exists
                	if (getScreenGame().getGame() != null)
                		darkenBackground(canvas);
                    
                    //draw menu
                    if (getScreen(getState()) != null)
                        getScreen(getState()).render(canvas);
                    break;

                case Running:
                	//game is already rendered, don't need to do anything here
                    break;

                case Paused:
                	
                    //if the previous state is not running, render it
                    if (getScreenPaused().getStatePrevious() != State.Running)
                        getScreen(getScreenPaused().getStatePrevious()).render(canvas);
                    
                    //darken background
                    darkenBackground(canvas);
                    
                    if (getScreen(getState()) != null)
                        getScreen(getState()).render(canvas);
                    break;

                case Options:
                	
                	//darken the background if the game exists
                	if (getScreenGame().getGame() != null)
                		darkenBackground(canvas);
                    
                    if (getScreen(getState()) != null)
                        getScreen(getState()).render(canvas);
                    break;
                    
                case Exit:
                	
                    //darken background
                    darkenBackground(canvas);
                    
                    if (getScreen(getState()) != null)
                        getScreen(getState()).render(canvas);
                    break;
                    
                case GameOver:
                	
                    //render game over info
                    getScreen(getState()).render(canvas);
                    break;

                //this shouldn't happen
                default:
                    throw new Exception("Undefined state " + state.toString());
            }
        }
    }
    
    /**
     * Reset any necessary screen elements here
     */
    @Override
    public void reset()
    {
        //do we need anything here
    }
    
    /**
     * Draw an overlay over the background
     * @param canvas Object we are writing pixel data to
     * @param alpha The visibility of the overlay we are to display, ranging from 0 (0% visible) - 255 (100% visible)
     */
    public static final void darkenBackground(final Canvas canvas, int alpha)
    {
        //keep in range
        if (alpha < 0)
            alpha = 0;
        if (alpha > 255)
            alpha = 255;
        
        //darken background
        canvas.drawARGB(alpha, 0, 0, 0);
    }
    
    /**
     * Draw an overlay over the background
     * @param canvas Object we are writing pixel data to
     */
    public static final void darkenBackground(final Canvas canvas)
    {
        //darken background
        darkenBackground(canvas, ALPHA_DARK);
    }
    
    @Override
    public void dispose()
    {
        if (paint != null)
        	paint = null;
        
        if (screens != null)
        {
            for (Screen screen : screens.values())
            {
                if (screen != null)
                {
                    screen.dispose();
                    screen = null;
                }
            }
            
            screens.clear();
            screens = null;
        }
        
        if (background != null)
        {
        	background.dispose();
        	background = null;
        }
    }
}