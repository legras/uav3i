package com.deev.interaction.touch;

import java.awt.Color;

public class Palette
{
	public static Color blendColors(Color c1, int level, int spread, Color c2)
	{
		float components1[] = c1.getColorComponents(null);
		float components2[] = c2.getColorComponents(null);
		float c[] = {0.f, 0.f, 0.f, 1.f};
		
		int n = Math.min(components1.length, components2.length);
		
		float s = (float) level / (float) spread;
		
		for (int i=0; i<n; i++)
			c[i] = components1[i] + s * (components2[i] - components1[i]);
		
		return new Color(c[0], c[1], c[2], c[3]);
	}
	
	
}
