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
	 * The y-coordinate where the score is rendered
	 */
	public static final int SCORE_Y = 20;
	
	//the current score
	private int currentScore = 0;
	
	/**
	 * The character to separate each stat apart
	 */
	private static final String SEPARATOR_STAT = "-";
	
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
            //get the # of difficulties in the settings
            final int difficultyLength = screen.getButtons().get(OptionsScreen.Key.Difficulty).getDescriptions().size();
            
            //get the # of game modes from the settings
            final int modeLength = screen.getButtons().get(OptionsScreen.Key.Mode).getDescriptions().size();
            
            //if content exists load it
            if (super.getContent().toString().trim().length() > 0)
            {
                //split the content into an array (each record is separated by a specific character)
                final String[] records = super.getContent().toString().split(Settings.SEPARATOR);
                
                //loop through each record
                for (int index = 0; index < records.length; index++)
                {
                	//separate each stat for the current record
                	final String[] stats = records[index].split(SEPARATOR_STAT);
                	
                	//the mode
                	final int modeIndex = Integer.parseInt(stats[0]);
                	
                	//the difficulty
                	final int difficultyIndex = Integer.parseInt(stats[1]);
                	
                	//get the score for the specified difficulty index
                	final int score = Integer.parseInt(stats[2]);
                	
                	//add loaded score to our array list
                	this.records.add(new Record(modeIndex, difficultyIndex, score));
                }
            }
            else
            {
            	//add default 0 score for all
            	for (int modeIndex = 0; modeIndex < modeLength; modeIndex++)
            	{
	                for (int difficultyIndex = 0; difficultyIndex < difficultyLength; difficultyIndex++)
	                {
	                	this.records.add(new Record(modeIndex, difficultyIndex, 0));
	                }
            	}
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
	}
	
	/**
	 * Get the current score
	 * @return The current score
	 */
	public int getCurrentScore()
	{
		return this.currentScore;
	}
	
	/**
	 * Set the current score
	 * @param currentScore The current score
	 */
	public void setCurrentScore(final int currentScore)
	{
		this.currentScore = currentScore;
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
            	//add the game mode
            	super.getContent().append(record.getMode());
            	
            	//separate the stats
            	super.getContent().append(SEPARATOR_STAT);
            	
            	//add the difficulty
            	super.getContent().append(record.getDifficulty());
            	
            	//separate the stats
            	super.getContent().append(SEPARATOR_STAT);
            	
            	//add score
            	super.getContent().append(record.getScore());
            	
            	//add delimiter to separate each record
        		super.getContent().append(Settings.SEPARATOR);
            }
            
            //remove the last character since there won't be any additional elements
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
     * @param modeIndex The specified mode index
     * @param difficultyIndex The specified difficulty index
     * @return The high score for the specified difficulty index
     */
    public int getHighScore(final int modeIndex, final int difficultyIndex)
    {
    	//check each record
    	for (Record record : records)
    	{
    		//if the difficulty does not match, skip it
    		if (record.getDifficulty() != difficultyIndex)
    			continue;
    		
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
     * @param modeIndex The game mode played
     * @param difficultyIndex The difficulty we are checking
     * @param score The score we are checking
     * @return true if the score was updated with a new record, false otherwise
     */
    public boolean updateScore(final int modeIndex, final int difficultyIndex, final int score)
    {
    	//check each record
    	for (Record record : records)
    	{
    		//if the difficulty does not match, skip it
    		if (record.getDifficulty() != difficultyIndex)
    			continue;
    		
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
     * A score record for a specific game difficulty
     */
	private class Record
	{
		private final int difficulty;
		private final int mode;
		private int score;
		
		/**
		 * Create record of score
		 * @param mode The game mode played
		 * @param difficulty The difficulty the score is for
		 * @param score The score for that mode
		 */
		public Record(final int mode, final int difficulty, final int score)
		{
			this.mode = mode;
			this.difficulty = difficulty;
			setScore(score);
		}
		
		private int getDifficulty() { return this.difficulty; }
		
		private int getScore() { return this.score; }
		
		private int getMode() { return this.mode; }
		
		private final void setScore(final int score) { this.score = score; }
	}
}