package com.gamesbykevin.floppybird.storage.score;

import com.gamesbykevin.androidframework.anim.Animation;
import com.gamesbykevin.androidframework.base.Entity;
import com.gamesbykevin.androidframework.resources.Images;
import com.gamesbykevin.floppybird.assets.Assets;
import com.gamesbykevin.floppybird.panel.GamePanel;

import android.graphics.Canvas;

/**
 * This class will render a number using a specific sprite sheet
 */
public class Digits extends Entity 
{
	//the current number
	private int number = 0;
	
	//the dimensions of each number animation
	private static final int NUMBER_WIDTH = 55;
	private static final int NUMBER_HEIGHT = 78;
	
	private enum Key
	{
		Zero(0), One(1), Two(2), Three(3), Four(4), 
		Five(5), Six(6), Seven(7), Eight(8), Nine(9);
		
		
		private int x, y = 0;
		
		private Key(final int col)
		{
			x = col * NUMBER_WIDTH;
		}
	}
	
	//our array object for each digit in our score
	private Number[] numbers;

	/**
	 * Default constructor
	 */
	public Digits() 
	{
		//set the dimensions
		setWidth(NUMBER_WIDTH);
		setHeight(NUMBER_HEIGHT);
		
		//add all number animations
		for (Key key : Key.values())
		{
			getSpritesheet().add(key, new Animation(Images.getImage(Assets.ImageGameKey.numbers), key.x, key.y, NUMBER_WIDTH, NUMBER_HEIGHT));
		}
		
		//set default animation
		getSpritesheet().setKey(Key.Zero);
	}
	
	/**
	 * Assign the number
	 * @param number The desired number
	 * @param x The starting x-coordinate
	 * @param y The y-coordinate
	 * @param center Center the text on the screen
	 */
	public void setNumber(final int number, int x, final int y, final boolean center)
	{
		//assign the score
		this.number = number;
		
    	//get the score and convert to string
    	String tmpNumber = String.valueOf(number);
    	
		//update the digits
    	if (this.numbers == null || this.numbers != null && this.numbers.length != tmpNumber.length())
    		this.numbers = new Number[tmpNumber.length()];
		
		//calculate the starting point, which will override the specified x parameter
    	if (center)
    		x = (GamePanel.WIDTH / 2) - ((tmpNumber.length() * NUMBER_WIDTH) / 2);
    	
    	//assign the y-coordinate
    	setY(y);
    	
    	//index
    	int index = 0;
    	
    	//assign the animations for each character
    	for (char test : tmpNumber.toCharArray())
    	{
    		//store the number key
    		Key tmp = null;
    		
    		//identify which animation
    		switch (test)
    		{
	    		case '0':
    			default:
    				tmp = Key.Zero;
	    			break;
	    			
	    		case '1':
	    			tmp = Key.One;
	    			break;
	    			
	    		case '2':
	    			tmp = Key.Two;
	    			break;
	    			
	    		case '3':
	    			tmp = Key.Three;
	    			break;
	    			
	    		case '4':
	    			tmp = Key.Four;
	    			break;
	    			
	    		case '5':
	    			tmp = Key.Five;
	    			break;
	    			
	    		case '6':
	    			tmp = Key.Six;
	    			break;
	    			
	    		case '7':
	    			tmp = Key.Seven;
	    			break;
	    			
	    		case '8':
	    			tmp = Key.Eight;
	    			break;
	    			
	    		case '9':
	    			tmp = Key.Nine;
	    			break;
    		}
    		
    		if (this.numbers[index] == null)
    		{
    			this.numbers[index] = new Number(x, tmp);
    		}
    		else
    		{
    			//update
    			this.numbers[index].x = x;
    			this.numbers[index].key = tmp;
    		}
    		
    		//change index
    		index++;
    		
    		//adjust x-coordinate
    		x += NUMBER_WIDTH;
    	}
	}
	
	/**
	 * Get the current number
	 * @return the current number
	 */
	public int getNumber()
	{
		return this.number;
	}
	
    /**
     * Render the assigned number
     * @param canvas
     */
    public void render(final Canvas canvas) throws Exception
    {
    	for (Number digit : numbers)
    	{
    		//assign x-coordinate location
    		setX(digit.x);
    		
    		//assign animation
    		getSpritesheet().setKey(digit.key);
    		
    		//render animation
    		super.render(canvas);
    	}
    }
    

    /**
     * This class will keep track of each digit in our score
     */
    private class Number
    {
    	protected int x;
    	protected Key key;
    	
    	private Number(final int x, final Key key)
    	{
    		this.x = x;
    		this.key = key;
    	}
    }
    
}