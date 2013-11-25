package com.deev.interaction.touch;

import java.awt.Color;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

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
	
	public static TexturePaint makeTexture(URL url, int size) throws IOException
	{
		BufferedImage img = ImageIO.read(url);

		return new TexturePaint(img, new Rectangle2D.Double(0, 0, size, size)); 
	}
	
	
}
