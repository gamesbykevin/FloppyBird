package com.gamesbykevin.floppybird.storage.score;

import java.util.ArrayList;
import java.util.List;

import com.gamesbykevin.androidframework.anim.Animation;
import com.gamesbykevin.androidframework.base.Entity;
import com.gamesbykevin.androidframework.io.storage.Internal;
import com.gamesbykevin.androidframework.resources.Font;
import com.gamesbykevin.androidframework.resources.Images;
import com.gamesbykevin.floppybird.assets.Assets;
import com.gamesbykevin.floppybird.panel.GamePanel;
import com.gamesbykevin.floppybird.screen.OptionsScreen;
import com.gamesbykevin.floppybird.storage.settings.Settings;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Score extends Internal 
{
	/**
	 * The file name to track our score
	 */
	private static final String FILE_NAME = "Score";
	
	//list of score records
	private List<Record> records;
	
	//the current score
	private int currentScore = 0;
	
	//the paint object to render the current score
	private Paint paint;
	
	//the entity to render the score
	private Entity entity;
	
	//the dimensions of each number animation
	private static final int NUMBER_WIDTH = 55;
	private static final int NUMBER_HEIGHT = 78;
	
	private enum NumberKey
	{
		Zero(0), One(1), Two(2), Three(3), Four(4), 
		Five(5), Six(6), Seven(7), Eight(8), Nine(9);
		
		
		private int x, y = 0;
		
		private NumberKey(final int col)
		{
			x = col * NUMBER_WIDTH;
		}
	}
	
	//our array object for each digit in our score
	private DigitScore[] digits;
	
	/**
	 * The y-coordinate where the score is rendered
	 */
	private static final int SCORE_Y = 20;
	
	/**
	 * Create new score object to track high score
	 * @param screen Object to reference that has the modes we are tracking
	 * @param activity Object needed to write data to internal storage
	 */
	public Score(final OptionsScreen screen, final Activity activity) 
	{
		super(FILE_NAME, activity);
		
		//create new entity and set the dimensions
		this.entity = new Entity();
		this.entity.setWidth(NUMBER_WIDTH);
		this.entity.setHeight(NUMBER_HEIGHT);
		
		//add all number animations
		for (NumberKey key : NumberKey.values())
		{
			this.entity.getSpritesheet().add(key, new Animation(Images.getImage(Assets.ImageGameKey.numbers), key.x, key.y, NUMBER_WIDTH, NUMBER_HEIGHT));
		}
		
		//set default animation
		this.entity.getSpritesheet().setKey(NumberKey.Zero);
		
		//create font for the score render
		this.paint = new Paint();
		this.paint.setTypeface(Font.getFont(Assets.FontGameKey.ScoreFont));
		this.paint.setTextSize(64f);
		this.paint.setColor(Color.BLACK);
		
		//create list for our records
		this.records = new ArrayList<Record>();
		
        try
        {
            //get the # of the modes in the settings
            final int length = screen.getButtons().get(OptionsScreen.Key.Difficulty).getDescriptions().size();
            
            //if content exists load it
            if (super.getContent().toString().trim().length() > 0)
            {
                //split the content into an array (each score for each mode)
                final String[] data = super.getContent().toString().split(Settings.SEPARATOR);
                
                //load the score of each difficulty
                for (int difficultyIndex = 0; difficultyIndex < length; difficultyIndex++)
                {
                	//if we are out of bounds of the existing data array, there is a new difficulty we need to track the high score
                	if (difficultyIndex >= data.length)
                	{
                		//add default score to our array list
	                	this.records.add(new Record(difficultyIndex, 0));
                	}
                	else
                	{
	                	//get the score for the specified difficulty index
	                	final int score = Integer.parseInt(data[difficultyIndex]);
	                	
	                	//add loaded score to our array list
	                	this.records.add(new Record(difficultyIndex, score));
                	}
                }
            }
            else
            {
            	//else set a default score for each difficulty
                for (int difficultyIndex = 0; difficultyIndex < length; difficultyIndex++)
                {
                	//add default 0 score
                	this.records.add(new Record(difficultyIndex, 0));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
	}
	
	/**
	 * Assign the current score
	 * @param currentScore The current score
	 */
	public void setCurrentScore(final int currentScore)
	{
		//assign the score
		this.currentScore = currentScore;
		
    	//get the score and convert to string
    	String tmpScore = String.valueOf(currentScore);
    	
		//update the digits
    	if (this.digits == null || this.digits != null && this.digits.length != tmpScore.length())
    		this.digits = new DigitScore[tmpScore.length()];
		
		//calculate the starting point
    	int x = (GamePanel.WIDTH / 2) - ((tmpScore.length() * NUMBER_WIDTH) / 2);
    	
    	//index
    	int index = 0;
    	
    	//assign the animations for each character
    	for (char test : tmpScore.toCharArray())
    	{
    		//store the number key
    		NumberKey tmp = null;
    		
    		//identify which animation
    		switch (test)
    		{
	    		case '0':
    			default:
    				tmp = NumberKey.Zero;
	    			break;
	    			
	    		case '1':
	    			tmp = NumberKey.One;
	    			break;
	    			
	    		case '2':
	    			tmp = NumberKey.Two;
	    			break;
	    			
	    		case '3':
	    			tmp = NumberKey.Three;
	    			break;
	    			
	    		case '4':
	    			tmp = NumberKey.Four;
	    			break;
	    			
	    		case '5':
	    			tmp = NumberKey.Five;
	    			break;
	    			
	    		case '6':
	    			tmp = NumberKey.Six;
	    			break;
	    			
	    		case '7':
	    			tmp = NumberKey.Seven;
	    			break;
	    			
	    		case '8':
	    			tmp = NumberKey.Eight;
	    			break;
	    			
	    		case '9':
	    			tmp = NumberKey.Nine;
	    			break;
    		}
    		
    		if (this.digits[index] == null)
    		{
    			this.digits[index] = new DigitScore(x, tmp);
    		}
    		else
    		{
    			//update
    			this.digits[index].x = x;
    			this.digits[index].key = tmp;
    		}
    		
    		//change index
    		index++;
    		
    		//adjust x-coordinate
    		x += NUMBER_WIDTH;
    	}
	}
	
	/**
	 * Get the current score
	 * @return the current score
	 */
	public int getCurrentScore()
	{
		return this.currentScore;
	}
	
    /**
     * Save the scores to the internal storage
     */
    @Override
    public void save()
    {
        try
        {
            //remove all existing content
            super.getContent().delete(0, super.getContent().length());

            //save every record in our array list, to the internal storage
            for (Record record : records)
            {
            	//add score
            	super.getContent().append(record.getScore());
            	
            	//add delimiter to separate each difficulty score
        		super.getContent().append(Settings.SEPARATOR);
            }
            
            //remove the last character since there won't be any additional settings
            super.getContent().deleteCharAt(super.getContent().length() - 1);

            //save data
            super.save();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Get the high score
     * @param difficultyIndex The specified difficulty index
     * @return The high score for the specified difficulty index
     */
    public int getHighScore(final int difficultyIndex)
    {
    	//check each record
    	for (Record record : records)
    	{
    		//if the mode does not match, skip it
    		if (record.getDifficulty() != difficultyIndex)
    			continue;
    		
    		//return our score
    		return record.getScore();
    	}
    	
    	//if the mode was not found return 0
    	return 0;
    }
    
    /**
     * Update the score
     * @param difficultyIndex The difficulty we are checking
     * @param score The score we are checking
     * @return true if the score was updated with a new record, false otherwise
     */
    public boolean updateScore(final int difficultyIndex, final int score)
    {
    	//check each record
    	for (Record record : records)
    	{
    		//if the difficulty does not match, skip it
    		if (record.getDifficulty() != difficultyIndex)
    			continue;
    		
    		//if the score is bigger, we have a new record
    		if (score > record.getScore())
    		{
    			//set the new record
    			record.setScore(score);
    			
    			//save to internal storage
    			save();
    			
    			//score was updated successful
    			return true;
    		}
    		else
    		{
    			//no need to check the remaining
    			break;
    		}
    	}
    	
    	//score was not updated
    	return false;
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        
        if (records != null)
        {
        	records.clear();
        	records = null;
        }
    }
	
    /**
     * Render the current score
     * @param canvas
     */
    public void render(final Canvas canvas) throws Exception
    {
    	for (DigitScore digit : digits)
    	{
    		//assign location
    		entity.setX(digit.x);
    		entity.setY(SCORE_Y);
    		
    		//assign animation
    		entity.getSpritesheet().setKey(digit.key);
    		
    		//render animation
    		entity.render(canvas);
    	}
    }
    
    /**
     * This class will keep track of each digit in our score
     */
    private class DigitScore
    {
    	protected int x;
    	protected NumberKey key;
    	
    	private DigitScore(final int x, final NumberKey key)
    	{
    		this.x = x;
    		this.key = key;
    	}
    }
    
    /**
     * A score record for a specific game difficulty
     */
	private class Record
	{
		private final int difficulty;
		private int score;
		
		/**
		 * Create record of score
		 * @param difficulty The difficulty the score is for
		 * @param score The score for that mode
		 */
		public Record(final int difficulty, final int score)
		{
			this.difficulty = difficulty;
			setScore(score);
		}
		
		private int getDifficulty() { return this.difficulty; }
		
		private int getScore() { return this.score; }
		
		private final void setScore(final int score) { this.score = score; }
	}
}