package com.gamesbykevin.floppybird.screen;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;

import java.util.HashMap;

import com.gamesbykevin.androidframework.awt.Button;
import com.gamesbykevin.androidframework.resources.Audio;
import com.gamesbykevin.androidframework.resources.Disposable;
import com.gamesbykevin.androidframework.resources.Images;
import com.gamesbykevin.androidframework.screen.Screen;
import com.gamesbykevin.floppybird.MainActivity;
import com.gamesbykevin.floppybird.assets.Assets;
import com.gamesbykevin.floppybird.storage.settings.Settings;

/**
 * This screen will contain the game options
 * @author GOD
 */
public class OptionsScreen implements Screen, Disposable
{
    //our logo reference
    private final Bitmap logo;
    
    //list of buttons
    private HashMap<Key, Button> buttons;
    
    //our main screen reference
    private final ScreenManager screen;
    
    //our storage settings object
    private Settings settings;
    
    //buttons to access each button in the list
    public enum Key
    {
    	Back, Sound, Vibrate, Difficulty, Mode, Instructions, Facebook, Twitter
    }
    
    //the user selection
    private Key selection = null;
    
    public OptionsScreen(final ScreenManager screen)
    {
        //our logo reference
        this.logo = Images.getImage(Assets.ImageMenuKey.Logo);

        //create buttons hash map
        this.buttons = new HashMap<Key, Button>();

        //store our screen reference
        this.screen = screen;
        
        //start coordinates
        int y = ScreenManager.BUTTON_Y;
        int x = ScreenManager.BUTTON_X;
        
        //add sound option
        addButtonSound(x, y);
        
        //add vibrate option
        x += ScreenManager.BUTTON_X_INCREMENT;
        addButtonVibrate(x, y);
        
        //add difficulty button
        x += ScreenManager.BUTTON_X_INCREMENT;
        addButtonDifficulty(x, y);
        
        //add game mode
        x += ScreenManager.BUTTON_X_INCREMENT;
        addButtonMode(x, y);
        
        //the back button
        x = ScreenManager.BUTTON_X;
        y += ScreenManager.BUTTON_Y_INCREMENT + (ScreenManager.BUTTON_Y_INCREMENT * .25);
        addButtonBack(x, y);
        
        //add social media icons after the above, because the dimensions are different
        addIcons();
        
        //setup each button
        for (Key key : Key.values())
        {
        	final Button button = buttons.get(key);
        	
        	switch (key)
        	{
	        	case Instructions:
	        	case Facebook:
	        	case Twitter:
	        		button.setWidth(MenuScreen.ICON_DIMENSION);
	            	button.setHeight(MenuScreen.ICON_DIMENSION);
	            	button.updateBounds();
	        		break;
        		
        		default:
                	button.setWidth(MenuScreen.BUTTON_WIDTH);
                	button.setHeight(MenuScreen.BUTTON_HEIGHT);
                	button.updateBounds();
                	button.positionText(getScreen().getPaint());
        			break;
        	}
        }
        
        //create our settings object last, which will load the previous settings
        this.settings = new Settings(this, screen.getPanel().getActivity());
    }
    
    private ScreenManager getScreen()
    {
    	return this.screen;
    }
    
    /**
     * Get the list of buttons.<br>
     * We typically use this list to help load/set the settings based on the index of each button.
     * @return The list of buttons on the options screen
     */
    public HashMap<Key, Button> getButtons()
    {
    	return this.buttons;
    }
    
    /**
     * Add icons, including links to social media
     */
    private void addIcons()
    {
        Button tmp = new Button(Images.getImage(Assets.ImageMenuKey.Instructions));
        tmp.setX(MenuScreen.ICON_X_INSTRUCTIONS);
        tmp.setY(MenuScreen.ICON_Y);
        this.buttons.put(Key.Instructions, tmp);
        
        tmp = new Button(Images.getImage(Assets.ImageMenuKey.Facebook));
        tmp.setX(MenuScreen.ICON_X_FACEBOOK);
        tmp.setY(MenuScreen.ICON_Y);
        this.buttons.put(Key.Facebook, tmp);
        
        tmp = new Button(Images.getImage(Assets.ImageMenuKey.Twitter));
        tmp.setX(MenuScreen.ICON_X_TWITTER);
        tmp.setY(MenuScreen.ICON_Y);
        this.buttons.put(Key.Twitter, tmp);
    }
    
    private void addButtonBack(final int x, final int y)
    {
        Button button = new Button(Images.getImage(Assets.ImageMenuKey.Button));
        button.addDescription("Go  Back");
        button.setX(x);
        button.setY(y);
        this.buttons.put(Key.Back, button);
    }
    
    private void addButtonSound(final int x, final int y)
    {
        Button button = new Button(Images.getImage(Assets.ImageMenuKey.Button));
        button.addDescription("Sound: On");
        button.addDescription("Sound: Off");
        button.setX(x);
        button.setY(y);
        this.buttons.put(Key.Sound, button);
    }

    private void addButtonMode(final int x, final int y)
    {
        Button button = new Button(Images.getImage(Assets.ImageMenuKey.Button));
        button.addDescription("Mode: Endless");
        button.addDescription("Mode: Survival");
        button.setX(x);
        button.setY(y);
    	this.buttons.put(Key.Mode, button);
    }
    
    private void addButtonDifficulty(final int x, final int y)
    {
        Button button = new Button(Images.getImage(Assets.ImageMenuKey.Button));
        button.addDescription("Skill: Normal");
        button.addDescription("Skill: Hard");
        button.addDescription("Skill: Easy");
        button.setX(x);
        button.setY(y);
    	this.buttons.put(Key.Difficulty, button);
    }
    
    private void addButtonVibrate(final int x, final int y)
    {
        Button button = new Button(Images.getImage(Assets.ImageMenuKey.Button));
        button.addDescription("Vibrate: On");
        button.addDescription("Vibrate: Off");
        button.setX(x);
        button.setY(y);
    	this.buttons.put(Key.Vibrate, button);
    }
    
    /**
     * Assign the index.
     * @param key The key of the button we want to change
     * @param index The desired index
     */
    public void setIndex(final Key key, final int index)
    {
    	buttons.get(key).setIndex(index);
    }
    
    /**
     * Get the index selection of the specified button
     * @param key The key of the button we want to check
     * @return The current selection for the specified button key
     */
    public int getIndex(final Key key)
    {
    	return buttons.get(key).getIndex();
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
    
    /**
     * Reset any necessary screen elements here
     */
    @Override
    public void reset()
    {
        //remove the selection
        setSelection(null);
    	
        if (buttons != null)
        {
        	for (Key key : Key.values())
        	{
        		//get the current button
        		Button button = buttons.get(key);
        		
        		try
        		{
	        		switch (key)
	        		{
						case Back:
						case Sound:
						case Vibrate:
						case Difficulty:
						case Mode:
							button.positionText(getScreen().getPaint());
							break;
							
						//do nothing for these
						case Instructions:
						case Facebook:
						case Twitter:
							break;
							
						default:
							throw new Exception("Key not handled here: " + key);
	        		}
        		}
        		catch (Exception e)
        		{
        			e.printStackTrace();
        		}
        	}
        }
    }
    
    @Override
    public boolean update(final int action, final float x, final float y) throws Exception
    {
    	//we only want motion event up
    	if (action != MotionEvent.ACTION_UP)
    		return true;
    	
    	//if there is a selection, no need to continue
    	if (getSelection() != null)
    		return true;
    	
        if (buttons != null)
        {
        	for (Key key : Key.values())
        	{
        		//get the current button
        		Button button = buttons.get(key);
        		
        		//if the button does not exist skip to the next
        		if (button == null)
        			continue;
        		
    			//if we did not select this button, skip to the next
    			if (!button.contains(x, y))
    				continue;
				
    			//store the button selection
    			setSelection(key);
    			
    			//no need to continue
    			return false;
        	}
        }
    	
        //return true
        return true;
    }
    
    @Override
    public void update() throws Exception
    {
    	//handle selections here
    	if (getSelection() != null)
    	{
    		//get the button selection
    		Button button = buttons.get(selection);
    		
    		switch (getSelection())
    		{
				case Back:
					//change index
					button.setIndex(button.getIndex() + 1);
					
	                //store our settings
	                settings.save();
	                
	                //set ready state
	                getScreen().setState(ScreenManager.State.Ready);
	                
	                //play sound effect
	                Audio.play(Assets.AudioMenuKey.Selection);
	                
	                //end of case
	                break;
	                
				case Vibrate:
				case Difficulty:
				case Mode:
					
					//change index
					button.setIndex(button.getIndex() + 1);
					
					//position the text
			        button.positionText(getScreen().getPaint());
					
	                //play sound effect
	                Audio.play(Assets.AudioMenuKey.Selection);
	                
	                //end of case
	                break;
	                
				case Sound:
	    			
					//change index
					button.setIndex(button.getIndex() + 1);
					
					//position the text
			        button.positionText(getScreen().getPaint());
			        
	                //flip setting
	                Audio.setAudioEnabled(!Audio.isAudioEnabled());
	                
	                //play sound effect
	                Audio.play(Assets.AudioMenuKey.Selection);
	                
	                //end of case
	                break;
	                
				case Instructions:
					
	                //play sound effect
	                Audio.play(Assets.AudioMenuKey.Selection);
	                
	                //go to instructions
					getScreen().getPanel().getActivity().openWebpage(MainActivity.WEBPAGE_GAME_INSTRUCTIONS_URL);
	                
	                //end of case
	                break;
					
				case Facebook:
					
	                //play sound effect
	                Audio.play(Assets.AudioMenuKey.Selection);
	                
	                //go to instructions
					getScreen().getPanel().getActivity().openWebpage(MainActivity.WEBPAGE_FACEBOOK_URL);
	                
	                //end of case
	                break;
					
				case Twitter:
					
	                //play sound effect
	                Audio.play(Assets.AudioMenuKey.Selection);
	                
	                //go to instructions
					getScreen().getPanel().getActivity().openWebpage(MainActivity.WEBPAGE_TWITTER_URL);
	                
	                //end of case
	                break;
				
				default:
	            	throw new Exception("Key not setup here: " + selection);
    		}
    		
    		//remove selection
    		setSelection(null);
    	}
    	
    	//if the game object exists, update it
    	if (getScreen().getScreenGame().getGame() != null)
    		getScreen().getScreenGame().getGame().update();
    }
    
    @Override
    public void render(final Canvas canvas) throws Exception
    {
        //draw our main logo
        canvas.drawBitmap(logo, ScreenManager.LOGO_X, ScreenManager.LOGO_Y, null);
        
        //draw the menu buttons
    	for (Key key : Key.values())
    	{
    		if (buttons.get(key) != null)
    		{
    			switch (key)
    			{
	    			case Back:
	    			case Sound:
	    			case Vibrate:
	    			case Difficulty:
	    			case Mode:
	    				buttons.get(key).render(canvas, getScreen().getPaint());
	    				break;
	    				
	    			case Instructions:
	    			case Facebook:
	    			case Twitter:
	    				buttons.get(key).render(canvas);
	    				break;
	    				
	    			default:
	    				throw new Exception("Button with key not setup here: " + key);
    			}
    		}
    	}
    }
    
    @Override
    public void dispose()
    {
        if (settings != null)
        {
            settings.dispose();
            settings = null;
        }
        
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