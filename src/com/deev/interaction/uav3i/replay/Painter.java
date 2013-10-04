package com.deev.interaction.uav3i.replay;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Painter {
	
	//Dimensions du logo curseurTemps.png
	static float curseurXSize = 87/2;
	static float curseurYSize = 111/2;
	static float curseurWidth = 4;
	
	//Dimensions du logo curseurDrone.png
	static float curseurDroneXSize = 138/3;
	static float curseurDroneYSize = 159/3;
	
	//Dimensions du logo curseur.png
	static float marqueurXSize = 87/2;
	static float marqueurYSize = 165/2;
	
	//Dimensions des segments Media
	static float mediaHeight = 80;
	
	//Dimensions des boutons
	static float buttonXSize = 220;
	static float buttonYSize = 100;
	
	static float playXSize = 201/4;
	static float playYSize = 233/4;
	static float pauseXSize = 158/4;
	static float pauseYSize = 216/4;
	static float loopXSize = 411/6;
	static float loopYSize = 411/6;
	
	
	
	//Fonction de dessin du curseur
	public static void paintCurseur(Graphics g, float xPosition, float yPosition, boolean isTouched)
	{
		/**
		BufferedImage logo = null;
		try {
			if (!isTouched)
				logo = ImageIO.read(new File("image/curseurTemps.png"));
			else
				logo = ImageIO.read(new File("image/curseurTempsN.png"));
		} catch (IOException e) {
		}}
		*/

		
		Graphics2D curseur = (Graphics2D) g;
		curseur.setColor(Color.red);
		if (!isTouched)
			curseur.fillRect((int) (xPosition-curseurWidth/2),(int) (yPosition - mediaHeight/2),(int) curseurWidth,(int) mediaHeight);
		else
			curseur.fillRect((int) (xPosition-curseurWidth),(int) (yPosition - mediaHeight/2),(int) (2*curseurWidth),(int) mediaHeight);
		
		//curseur.drawImage(logo,(int) (xPosition - curseurXSize/2),(int)  (yPosition - curseurYSize/4),(int)  (xPosition + curseurXSize/2),(int) (yPosition + 3*curseurYSize/4),0,0,87,111, null);
	}
	
	//Fonction de dessin des marqueurs
	public static void paintMarqueur(Graphics g, float xPosition, float yPosition, boolean isTouched, boolean side)
	{
		BufferedImage logo = null;
		try {
			if (!isTouched)
				logo = ImageIO.read(new File("image/curseur.png"));
				
			else
				logo = ImageIO.read(new File("image/curseurN.png"));
		} catch (IOException e) {

		}

		
		Graphics2D marqueur = (Graphics2D) g;
		
		if (side)
		{
			marqueur.drawImage(logo,(int) (xPosition + marqueurXSize),(int) (yPosition - marqueurYSize/2),(int) (xPosition),(int) (yPosition + marqueurYSize/2),0,0,87,165, null);		}
		else
		{
			marqueur.drawImage(logo,(int) (xPosition - marqueurXSize),(int) (yPosition - marqueurYSize/2),(int) (xPosition),(int) (yPosition + marqueurYSize/2),0,0,87,165, null);
		}	
	}
	
	//Fonction de dessin des Medias
	public static void paintDownload(Graphics g, float start, float length, float yPosition)
	{
		Graphics2D g2 = (Graphics2D) g;

		g2.setColor(Color.green);		
		g2.fillRect((int) start,(int) (yPosition - mediaHeight/2),(int) length,(int) mediaHeight);
	}
	
	//Fonction de dessin des Medias
	public static void paintMedia(Graphics g, float start, float length, float startFocus, float endFocus, float yPosition, boolean isSelected)
	{
		Graphics2D media = (Graphics2D) g;
		if (isSelected)
			media.setColor(new Color(1.f, 0.4f, 0.f, .5f));
		else
			media.setColor(new Color(1.f, 1.f, 0.f, .5f));
		
		
		media.fillRect((int) start,(int) (yPosition - mediaHeight/2),(int) length,(int) mediaHeight);
		
		if (isSelected)
		{
			media.setColor(new Color(0.f, 0.f, 0.f, 0.5f));
			if (start < startFocus)
				media.fillRect((int) start, (int) (yPosition-mediaHeight/2), (int) (startFocus - start), (int) mediaHeight);
			if (start + length > endFocus)
				media.fillRect((int) endFocus,(int) (yPosition-mediaHeight/2),(int) (start + length - endFocus), (int) mediaHeight);
			
			if (endFocus > start + length)
			{
				media.setColor(Color.red);
				media.fillRect((int) (start + length), (int) (yPosition-mediaHeight/2), (int) (endFocus - start - length), (int) mediaHeight);
			}
			
			if (startFocus < start)
			{
				media.setColor(Color.red);
				media.fillRect((int) startFocus, (int) (yPosition-mediaHeight/2), (int) (start - startFocus), (int) mediaHeight);
				
			}
		}
	}
	
	//Fonction de dessin du curseurDrone
	public static void paintCurseurDrone(Graphics g, float xPosition, float yPosition, boolean isTouched)
	{
		
		BufferedImage logo = null;
		try {
			if (!isTouched)
				logo = ImageIO.read(new File("image/curseurDrone.png"));
			else
				logo = ImageIO.read(new File("image/curseurDroneN.png"));
		} catch (IOException e) {
		
		}
		

		
		Graphics2D curseur = (Graphics2D) g;
		curseur.drawImage(logo,(int) (xPosition - curseurDroneXSize/2),(int)  (yPosition - curseurDroneYSize/2),(int)  (xPosition + curseurDroneXSize/2),(int) (yPosition + curseurDroneYSize/2),0,0,138,159, null);
	}
	
	//Fonction de dessin des Boutons
	public static void paintButtons(Graphics g, float xPosition, float yPosition, boolean playIsTouched, boolean loopIsTouched, boolean pauseIsTouched)
	{
		Graphics2D buttons = (Graphics2D) g;
		
		g.setColor(new Color(0.5f,0.5f,0.5f,0.5f));
		g.fillRoundRect((int) (xPosition - buttonXSize/2), (int) (yPosition - buttonYSize/2), (int) buttonXSize, (int) buttonYSize, 50, 50);
		
		//Dessine play
		BufferedImage logo = null;
		try {
			if (!playIsTouched)
				logo = ImageIO.read(new File("image/play.png"));
			else
				logo = ImageIO.read(new File("image/playN.png"));
		} catch (IOException e) {
		
		}
				
		buttons.drawImage(logo, (int) (xPosition - buttonXSize/3 - playXSize/2), (int) (yPosition - playYSize/2), (int) (xPosition - buttonXSize/3 + playXSize/2), (int) (yPosition + playYSize/2), 0, 0, 201, 233, null);
		
		//Dessine Pause
		logo = null;
		try {
			if (!pauseIsTouched)
				logo = ImageIO.read(new File("image/pause.png"));
			else
				logo = ImageIO.read(new File("image/pauseN.png"));
		} catch (IOException e) {
		
		}
				
		buttons.drawImage(logo, (int) (xPosition - pauseXSize/2), (int) (yPosition - pauseYSize/2), (int) (xPosition + pauseXSize/2), (int) (yPosition + pauseYSize/2), 0, 0, 158, 216, null);
		
		//Dessine Loop
		logo = null;
		try {
			if (!loopIsTouched)
				logo = ImageIO.read(new File("image/loop.png"));
			else
				logo = ImageIO.read(new File("image/loopN.png"));
		} catch (IOException e) {
		
		}
				
		buttons.drawImage(logo, (int) (xPosition + buttonXSize/3 - loopXSize/2), (int) (yPosition - loopYSize/2), (int) (xPosition + buttonXSize/3 + loopXSize/2), (int) (yPosition + loopYSize/2), 0, 0, 411, 411, null);
		
	}

}
