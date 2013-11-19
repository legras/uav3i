package com.deev.interaction.touch;

import java.awt.Graphics2D;

public interface Animation
{
	public abstract int tick(int time); // L'animation n'est plus valide si elle renvoie < 0
	public abstract int life();
}
