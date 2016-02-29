package com.gamesbykevin.floppybird.screen;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

import java.util.HashMap;

import com.gamesbykevin.androidframework.awt.Button;
import com.gamesbykevin.androidframework.resources.Audio;
import com.gamesbykevin.androidframework.resources.Disposable;
import com.gamesbykevin.androidframework.resources.Font;
import com.gamesbykevin.androidframework.resources.Images;
import com.gamesbykevin.androidframework.screen.Screen;
import com.gamesbykevin.floppybird.MainActivity;
import com.gamesbykevin.floppybird.assets.Assets;
import com.gamesbykevin.floppybird.panel.GamePanel;

/**
 * The game over screen
 * @author GOD
 */
public class GameoverScreen implements Screen, Disposable
{
    //our main screen reference
    private final ScreenManager screen;
    
    //object to paint message
    private Paint paint;
    
    //the messages to display
    private String message = "";
    
    //where we draw the images and text
    private int messageX = 0, messageY = 0;
    
    //time we have displayed text
    private long time;
    
    /**
     * The amount of time to wait until we render the game over menu
     */
    private static final long DELAY_MENU_DISPLAY = 1250L;
    
    //do we display the menu
    private boolean display = false;
    
    /**
     * The text to display for the new game
     */
    private static final String BUTTON_TEXT_NEW_GAME = "Retry";
    
    /**
     * The text to display for the menu
     */
    private static final String BUTTON_TEXT_MENU = "Menu";
    
    //list of buttons
    private HashMap<Key, Button> buttons;
    
    /**
     * Keys to access each button
     * @author GOD
     *
     */
    public enum Key
    {
    	Restart, Menu, Rate
    }
    
    //the menu selection made
    private Key selection = null;
    
    /**
     * Create the game over screen
     * @param screen Parent screen manager object
     */
    public GameoverScreen(final ScreenManager screen)
    {
        //store our parent reference
        this.screen = screen;
        
        //create buttons hash map
        this.buttons = new HashMap<Key, Button>();
        
        //the start location of the button
        int y = ScreenManager.BUTTON_Y;
        int x = ScreenManager.BUTTON_X;

        //create our buttons
        addButton(x, y, Key.Restart, BUTTON_TEXT_NEW_GAME);
        
        x += ScreenManager.BUTTON_X_INCREMENT;
        addButton(x, y, Key.Menu, BUTTON_TEXT_MENU);
        
        x += ScreenManager.BUTTON_X_INCREMENT;
        addButton(x, y, Key.Rate, MenuScreen.BUTTON_TEXT_RATE_APP);
    }
    
    /**
     * Add button to our list
     * @param x desired x-coordinate
     * @param y desired y-coordinate
     * @param index Position to place in our array list
     * @param description The text description to add
     */
    private void addButton(final int x, final int y, final Key key, final String description)
    {
    	//create new button
    	Button button = new Button(Images.getImage(Assets.ImageMenuKey.Button));
    	
    	//position the button
    	button.setX(x);
    	button.setY(y);
    	
    	//assign the dimensions
    	button.setWidth(MenuScreen.BUTTON_WIDTH);
    	button.setHeight(MenuScreen.BUTTON_HEIGHT);
    	button.updateBounds();
    	
    	//add the text description
    	button.addDescription(description);
    	button.positionText(screen.getPaint());
    	
    	//add button to the list
    	this.buttons.put(key, button);
    }
    
    /**
     * Reset any necessary screen elements here
     */
    @Override
    public void reset()
    {
        //reset timer
        time = System.currentTimeMillis();
        
        //do we display the menu
        setDisplay(false);
        
        //make sure the button text is centered
        for (Button button : buttons.values())
        {
        	button.positionText(screen.getPaint());
        }
        
        //remove the selection
        setSelection(null);
    }
    
    /**
     * Assign the message
     * @param message The message we want displayed
     */
    public void setMessage(final String message)
    {
        //assign the message(s)
        this.message = message;
        
        //create temporary rectangle
        Rect tmp = new Rect();
        
        //create paint text object for the message
        if (this.paint == null)
        {
	        //assign metrics
        	this.paint = new Paint();
        	//this.paint.setTypeface(Font.getFont(Assets.FontGameKey.Default));
        	this.paint.setColor(Color.WHITE);
        	this.paint.setTextSize(64f);
        }
        
        //get the rectangle around the message
        this.paint.getTextBounds(message, 0, message.length(), tmp);
        
        //calculate the position of the message
        this.messageX = (GamePanel.WIDTH / 2) - (tmp.width() / 2);
        this.messageY = (int)buttons.get(Key.Rate).getY() + 175;
    }
    
    /**
     * Flag display
     * @param display true if we want to display the buttons, false otherwise
     */
    private void setDisplay(final boolean display)
    {
    	this.display = display;
    }
    
    /**
     * Do we display the buttons?
     * @return true = yes, false = no
     */
    private boolean hasDisplay()
    {
    	return this.display;
    }
    
    /**
     * Get the menu selection
     * @return The unique key of the button the user pressed
     */
    private Key getSelection()
    {
    	return this.selection;
    }
    
    /**
     * Set the menu selection
     * @param selection The unique key of the button the user pressed
     */
    private void setSelection(final Key selection)
    {
    	this.selection = selection;
    }
    
    @Override
    public boolean update(final int action, final float x, final float y) throws Exception
    {
        //if we aren't displaying the menu, return false
        if (!hasDisplay())
            return false;
        
        //don't continue if we already made a selection
        if (getSelection() != null)
        	return false;
        
        if (action == MotionEvent.ACTION_UP)
        {
        	for (Key key : Key.values())
        	{
        		//get the current button
        		Button button = buttons.get(key);
        		
        		//if we did not click this button skip to the next
        		if (!button.contains(x, y))
        			continue;
        		
        		//assign the user selection
        		setSelection(key);
        		
                //no more events required
                return false;
        	}
        }
        
        //no action was taken here and we may need additional events
        return true;
    }
    
    @Override
    public void update() throws Exception
    {
    	if (getSelection() != null)
    	{
			//handle each button different
			switch (getSelection())
			{
				case Restart:
				
	                //reset with the same settings
	                screen.getScreenGame().getGame().setReset(true);
	                
	                //move back to the game
	                screen.setState(ScreenManager.State.Running);
	                
	                //play sound effect
	                Audio.play(Assets.AudioMenuKey.Selection);
	                
	                //end of case
	                break;
	
	    		case Menu:
	                
	                //move to the main menu
	                screen.setState(ScreenManager.State.Ready);
	                
	                //play sound effect
	                Audio.play(Assets.AudioMenuKey.Selection);
	                
	                //end of case
	                break;
	    			
	    		case Rate:
	                
	                //play sound effect
	                Audio.play(Assets.AudioMenuKey.Selection);
	                
	                //go to rate game page
	                screen.getPanel().getActivity().openWebpage(MainActivity.WEBPAGE_RATE_URL);
	                
	                //end of case
	                break;
	    			
				default:
					throw new Exception("Key not setup here: " + getSelection());
			}
			
			//remove selection
			setSelection(null);
			
            //remove messages
            setMessage("");
    	}
    	else
    	{
	    	//we still want to update the game to continue showing the balls in motion
	    	this.screen.getScreenGame().update();
	    	
	        //if not displaying the menu, track timer
	        if (!hasDisplay())
	        {
	            //if time has passed display menu
	            if (System.currentTimeMillis() - time >= DELAY_MENU_DISPLAY)
	            {
	            	//display the menu
	            	setDisplay(true);
	
	                //anything else to do here?
	            }
	        }
    	}
    }
    
    @Override
    public void render(final Canvas canvas) throws Exception
    {
        if (hasDisplay())
        {
            //only darken the background when the menu is displayed
            ScreenManager.darkenBackground(canvas);
            
            //render message(s)
            canvas.drawText(this.message, messageX, messageY, this.paint);
        
            //render the buttons
            for (Key key : Key.values())
            {
            	buttons.get(key).render(canvas, screen.getPaint());
            }
        }
    }
    
    @Override
    public void dispose()
    {
        if (paint != null)
        	paint = null;
        
        if (buttons != null)
        {
        	for (Key key : Key.values())
	        {
	        	if (buttons.get(key) != null)
	        	{
	        		buttons.get(key).dispose();
	        		buttons.put(key, null);
	        	}
	        }
	        
	        buttons.clear();
	        buttons = null;
        }
    }
}