package com.deev.interaction.common.ui;

public interface Touchable
{
	/**
	 * Returns a value that represents the interest of the Touchable for the given point.
	 * Negative values typically indicate no interest for the given point, positive value
	 * indicate a growing interest. Scale is application dependent.
	 * @param x
	 * @param y
	 * @return value
	 */
	float getInterestForPoint(float x, float y);
	
	void addTouch(float x, float y, Object touchref);
	
	void updateTouch(float x, float y, Object touchref);
	
	void removeTouch(float x, float y, Object touchref);
	
	void cancelTouch(Object touchref);
}
