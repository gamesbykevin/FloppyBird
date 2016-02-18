package com.gamesbykevin.floppybird.storage.score;

import java.util.ArrayList;
import java.util.List;

import com.gamesbykevin.androidframework.io.storage.Internal;
import com.gamesbykevin.floppybird.screen.OptionsScreen;
import com.gamesbykevin.floppybird.storage.settings.Settings;

import android.app.Activity;

public class Score extends Internal 
{
	/**
	 * The file name to track our score
	 */
	private static final String FILE_NAME = "Score";
	
	//list of score records
	private List<Record> records;
	
	/**
	 * Create new score object to track high score
	 * @param screen Object to reference that has the modes we are tracking
	 * @param activity Object needed to write data to internal storage
	 */
	public Score(final OptionsScreen screen, final Activity activity) 
	{
		super(FILE_NAME, activity);
		
		//create list for our records
		this.records = new ArrayList<Record>();
		
        try
        {
            //get the # of the modes in the settings
            final int length = 0;//screen.getButtons().get(OptionsScreen.Key.Mode).getDescriptions().size();
            
            //if content exists load it
            if (super.getContent().toString().trim().length() > 0)
            {
                //split the content into an array (each score for each mode)
                final String[] data = super.getContent().toString().split(Settings.SEPARATOR);
                
                //load the score of each mode
                for (int modeIndex = 0; modeIndex < length; modeIndex++)
                {
                	//if we are out of bounds of the existing data array, there is a new mode we need to track the high score
                	if (modeIndex >= data.length)
                	{
                		//add default score to our array list
	                	this.records.add(new Record(modeIndex, 0));
                	}
                	else
                	{
	                	//get the score for the specified mode index
	                	final int score = Integer.parseInt(data[modeIndex]);
	                	
	                	//add loaded score to our array list
	                	this.records.add(new Record(modeIndex, score));
                	}
                }
            }
            else
            {
            	//else set a default score for each mode
                for (int modeIndex = 0; modeIndex < length; modeIndex++)
                {
                	//add default 0 score
                	this.records.add(new Record(modeIndex, 0));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
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
            	
            	//add delimiter to separate each mode score
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
     * @param modeIndex The specified game mode index
     * @return The high score for the specified mode index
     */
    public int getHighScore(final int modeIndex)
    {
    	//check each record
    	for (Record record : records)
    	{
    		//if the mode does not match, skip it
    		if (record.getMode() != modeIndex)
    			continue;
    		
    		//return our score
    		return record.getScore();
    	}
    	
    	//if the mode was not found return 0
    	return 0;
    }
    
    /**
     * Update the score
     * @param modeIndex The mode we are checking
     * @param score The score we are checking
     * @return true if the score was updated with a new record, false otherwise
     */
    public boolean updateScore(final int modeIndex, final int score)
    {
    	//check each record
    	for (Record record : records)
    	{
    		//if the mode does not match, skip it
    		if (record.getMode() != modeIndex)
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
     * A score record for a specific game mode
     */
	private class Record
	{
		private final int mode;
		private int score;
		
		/**
		 * Create record of score
		 * @param mode The mode the score is for
		 * @param score The score for that mode
		 */
		public Record(final int mode, final int score)
		{
			this.mode = mode;
			setScore(score);
		}
		
		private int getMode() { return this.mode; }
		
		private int getScore() { return this.score; }
		
		private final void setScore(final int score) { this.score = score; }
	}
}