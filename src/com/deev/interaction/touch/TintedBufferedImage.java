package com.deev.interaction.touch;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class TintedBufferedImage extends BufferedImage
{
	public TintedBufferedImage(BufferedImage source, Color tint)
	{
		super(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		for (int i=0; i<source.getWidth(); i++)
			for (int j=0; j<source.getHeight(); j++)
			{
				Color s = new Color(source.getRGB(i, j), true);
				float comp[] = tint.getColorComponents(null);
				for (int c=0; c<3; c++)
					comp[c] += .3f*(1.f-(float) j/source.getHeight())*(1.f-comp[c]);
				Color n = new Color(comp[0], comp[1], comp[2], (float) s.getAlpha()/255);
				setRGB(i, j, n.getRGB());
			}
	}
}
