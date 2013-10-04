package com.deev.interaction.uav3i.replay;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.lang.Math;

import javax.swing.JComponent;

import com.deev.interaction.common.ui.Animation;
import com.deev.interaction.common.ui.Touchable;
import com.deev.interaction.uav3i.model.Media;
import com.deev.interaction.uav3i.model.MediaStorefront;

public class TimeLine extends JComponent implements Touchable, Animation
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -923278315604054575L;
	
	private enum TimeLineState {HIDDEN, SHOWING, ACTIVE, HIDING};
	private TimeLineState _state;
	private float _vOffset;

	Media selectedMedia;

	Curseur curseurTemps;
	boolean curseurTempsIsTouched;
	Marqueur marqueurDebut;
	boolean marqueurDebutIsTouched;
	Marqueur marqueurFin;
	boolean marqueurFinIsTouched;
	CurseurDrone curseurDrone;
	boolean curseurDroneIsTouched;
	boolean playIsTouched;
	boolean pauseIsTouched;
	boolean loopIsTouched;

	boolean objectUsed;

	float oldTouch;
	float firstTouch;
	float centerXPosition;
	float lineYPosition;
	float timeLineHeight = 100;

	long dureeMission;
	long t0;
	float scale;
	float minScale;

	float screenWidth;


	public TimeLine(float screenWidth, float screenHeight)
	{
		super();

		_state = TimeLineState.HIDDEN;
		_vOffset = timeLineHeight;
		
		this.screenWidth = screenWidth;

		centerXPosition = screenWidth/2;
		lineYPosition = screenHeight-timeLineHeight/2;

		// TODO d�gager dureeMission
		dureeMission = 1000000;
		minScale = screenWidth/(dureeMission)*2.f;

		t0 = 0;
		scale = minScale;

		curseurDrone = new CurseurDrone(pixelToTime(centerXPosition));
		PlayTimer.timeLine = this;
		PlayTimer.main(null);
	}


	@Override
	public void paintComponent(Graphics g)
	{
		Rectangle rect = this.getBounds();

		Graphics2D g2 = (Graphics2D) g;

		g2.setPaint(new Color(1.f, 1.f, 1.f, .8f));

		// TODO jamais on ne modifie les bounds pendant qu'on peint !!!
		this.setBounds(rect.x,(int) (rect.height-timeLineHeight), rect.x+rect.width, (int) timeLineHeight);
		Rectangle rect2 = this.getBounds();

		this.setBounds(rect);
		
		AffineTransform old = g2.getTransform();
		g2.translate(0, _vOffset);
		
		g2.fillRect(rect2.x, rect2.y, rect2.width, rect2.height);

		ArrayList <Media> segmentsVideo = MediaStorefront.getMediaList();
		synchronized (segmentsVideo)
		{
			for (Media m : segmentsVideo)
				switch (m.getType())
				{	
				case LOCAL:
					Painter.paintMedia(g2, timeToScreen(m.start),
							timeToPixel(m.length),
							timeToScreen(m.beginFocus),
							timeToScreen(m.endFocus),
							lineYPosition,
							m == selectedMedia);
					break;

				case DOWNLOADING:
					Painter.paintDownload(g2, timeToScreen(m.start), timeToPixel(m.length), lineYPosition);
					break;

				default:
				}

			g2.setPaint(new Color(0.f, 0.f, 0.f, .2f));
			g2.setStroke(new BasicStroke(1.f));
			g2.draw(new Line2D.Float(rect2.x, rect2.y + rect2.height / 2, rect2.x + rect2.width, rect2.y + rect2.height / 2));

			if (selectedMedia != null) 
			{
				Painter.paintMarqueur(g,
						timeToScreen(marqueurDebut.time),
						lineYPosition, marqueurDebutIsTouched, false);
				Painter.paintMarqueur(g,
						timeToScreen(marqueurFin.time), lineYPosition,
						marqueurFinIsTouched, true);
				Painter.paintCurseur(g,
						timeToScreen(curseurTemps.time), lineYPosition,
						curseurTempsIsTouched);

				Painter.paintButtons(g, timeToScreen(selectedMedia.start + selectedMedia.length
						/ 2), lineYPosition - Painter.mediaHeight,
						playIsTouched, loopIsTouched, pauseIsTouched);

			}
		}

		Painter.paintCurseurDrone(g, timeToScreen(curseurDrone.time), lineYPosition, curseurDroneIsTouched);
		
		g2.setTransform(old);
	}

	@Override
	public float getInterestForPoint(float x, float y)
	{
		if (_state != TimeLineState.ACTIVE)
			return -1.f;
		
		Rectangle rect = this.getBounds();
		float interest = -1.f;

		if (y > rect.height - timeLineHeight)
			interest = 10.f;
		
		return interest;
	}

	@Override
	public void addTouch(float x, float y, Object touchref)
	{
		objectUsed = false;


		if (curseurDroneInterested(x, y, curseurDrone))
		{
			curseurDroneIsTouched = true;
			objectUsed = true;

		}

		if (selectedMedia != null)
		{
			if (curseurInterested(x, y, curseurTemps))
			{
				curseurTempsIsTouched = true;
				objectUsed = true;
			}
			else if (marqueurInterested(x, y, marqueurDebut))
			{
				marqueurDebutIsTouched = true;
				objectUsed = true;
			}
			else if (marqueurInterested(x, y, marqueurFin))
			{
				marqueurFinIsTouched = true;
				objectUsed = true;
			}

			if (buttonInterested(x, y, 0))
				playIsTouched = true;
			else if (buttonInterested(x, y, 1))
				pauseIsTouched = true;
			else if (buttonInterested(x, y, 2))
				loopIsTouched = true;
		}

		firstTouch = x;
		oldTouch = x;
	}

	@Override
	public void updateTouch(float x, float y, Object touchref)
	{
		if (curseurDroneIsTouched)
			moveCurseurDrone(oldTouch - x, curseurDrone);


		if (selectedMedia != null)
		{
			if (curseurTempsIsTouched)
				moveCurseur(oldTouch - x, curseurTemps);
			else if (marqueurDebutIsTouched)
			{
				moveMarqueur(oldTouch - x, marqueurDebut);
			}

			else if (marqueurFinIsTouched)
			{
				moveMarqueur(oldTouch - x, marqueurFin);
			}


			updateMarqueur();
			updateCurseur();


			selectedMedia.beginFocus = marqueurDebut.time;
			selectedMedia.endFocus = marqueurFin.time;
		}

		if (!objectUsed)
		{
			spanning(x-oldTouch);
			//zooming(oldTouch, x, oldTouch + 50, oldTouch + Math.abs(oldTouch-x) + 50);
		}

		oldTouch = x;
	}

	@Override
	public void removeTouch(float x, float y, Object touchref)
	{
		ArrayList<Media> segmentsVideo = MediaStorefront.getMediaList();
		synchronized (segmentsVideo)
		{
			if (!curseurTempsIsTouched & !marqueurDebutIsTouched & !marqueurFinIsTouched & !curseurDroneIsTouched & (firstTouch-10)<x & x<(firstTouch+10))
			{

				for (Media m : segmentsVideo)
				{
					if (mediaInterested(x, y, m))
					{
						if (m != selectedMedia)
						{
							selection(m);
						}
					}
				}
			}
		}

		if (selectedMedia != null)
		{
			curseurTempsIsTouched = false;
			marqueurDebutIsTouched = false;
			marqueurFinIsTouched = false;

			if (playIsTouched)
			{
				PlayTimer.loop = false;
				PlayTimer.isPlaying = true;
				playIsTouched = false;
			}
			else if (pauseIsTouched)
			{
				PlayTimer.isPlaying = false;
				pauseIsTouched = false;
			}
			else if (loopIsTouched)
			{
				PlayTimer.loop = true;
				PlayTimer.isPlaying = true;
				loopIsTouched = false;
			}
		}

		curseurDroneIsTouched = false;

		if (objectUsed)
			objectUsed = false;
	}

	@Override
	public void cancelTouch(Object touchref)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public int tick(int time)
	{
		switch (_state)
		{
			case HIDDEN : 
				_vOffset = timeLineHeight;
				break;
			case SHOWING : 
				_vOffset /= 2.;
				if (_vOffset < 1.f)
					_state = TimeLineState.ACTIVE;
				break;
			case ACTIVE : 
				_vOffset = 0.f;
				break;
			case HIDING : 
				_vOffset += (timeLineHeight-_vOffset)/2.f;
				if (_vOffset > timeLineHeight-1.f)
					_state = TimeLineState.HIDDEN;
				break;
			default:
				System.out.println("Default while on switching on TimeLineState: " + _state);
		}
		
		return 1;
	}

	@Override
	public int life()
	{
		return 1;
	}
	

	
	public void hide()
	{
		_state = TimeLineState.HIDING;
	}
	
	public void show()
	{
		_state = TimeLineState.SHOWING;
	}

	public void selection(Media m)
	{
		selectedMedia = m;
		m.beginFocus = m.start;
		m.endFocus = m.start + m.length;

		curseurTemps = new Curseur(m.start + m.length/2);
		curseurTempsIsTouched = false;
		marqueurDebut = new Marqueur(m.start, false);
		marqueurDebutIsTouched = false;
		marqueurFin = new Marqueur(m.start + m.length, true);
		marqueurFinIsTouched = false;

		playIsTouched = false;
		pauseIsTouched = false;
		loopIsTouched = false;

		PlayTimer.isDownloadable = true;

	}

	public float timeToPixel(long time)
	{
		float pixel = scale*time;

		return pixel;
	}

	public float timeToScreen (long time)
	{
		float pixel = scale*(time-t0);

		return pixel;
	}

	public long pixelToTime(float pixel)
	{
		long time = (long) (pixel/scale);

		return time;
	}

	public boolean mediaInterested(float x, float y, Media m)
	{
		boolean test = false;
		if (x > timeToScreen(m.start) & x < timeToScreen(m.start+m.length) & y > lineYPosition - Painter.mediaHeight/2 & y < lineYPosition + Painter.mediaHeight/2)
			test = true;


		return test;
	}

	public boolean curseurInterested(float x, float y, Curseur c)
	{
		boolean test = false;
		if (x > timeToScreen(c.time) - Painter.curseurWidth & x < timeToScreen(c.time) + Painter.curseurWidth & y > lineYPosition - Painter.mediaHeight/2 & y < lineYPosition + Painter.mediaHeight/2)
			test = true;


		return test;
	}

	public boolean curseurDroneInterested(float x, float y, CurseurDrone d)
	{
		boolean test = false;
		if (x > timeToScreen(d.time) - Painter.curseurDroneXSize /2 & x < timeToScreen(d.time) + Painter.curseurDroneXSize/2 & y > lineYPosition - Painter.curseurDroneYSize/2 & y < lineYPosition + Painter.curseurDroneYSize/2)
			test = true;


		return test;
	}

	public boolean marqueurInterested(float x, float y, Marqueur m)
	{
		boolean test = false;
		if (m.side)
		{
			if (x > timeToScreen(m.time) & x < timeToScreen(m.time) + Painter.marqueurXSize & y > lineYPosition - Painter.marqueurYSize/2 & y < lineYPosition + Painter.marqueurYSize/2)
				test = true;
		}
		else
		{
			if (x > timeToScreen(m.time) - Painter.marqueurXSize & x < timeToScreen(m.time) & y > lineYPosition - Painter.marqueurYSize/2 & y < lineYPosition + Painter.marqueurYSize/2)
				test = true;
		}

		return test;
	}

	public boolean buttonInterested(float x, float y, int button)
	{
		boolean test = false;

		if (x > timeToScreen(selectedMedia.start+selectedMedia.length/2)-Painter.buttonXSize/2+button*Painter.buttonXSize/3 & x < timeToScreen(selectedMedia.start+selectedMedia.length/2)-Painter.buttonXSize/2+(button+1)*Painter.buttonXSize/3 & y > lineYPosition - Painter.mediaHeight - Painter.buttonYSize/2 & y < lineYPosition - Painter.mediaHeight + Painter.buttonYSize/2)
			test = true;

		return test; 
	}

	public void moveCurseur(float move, Curseur c)
	{

		c.time = c.time - pixelToTime(move);

	}

	public void moveCurseurDrone(float move, CurseurDrone d)
	{

		d.time = d.time - pixelToTime(move);

	}

	public void moveMarqueur(float move, Marqueur m)
	{
		m.time = m.time - pixelToTime(move);
	}

	public void updateCurseur()
	{
		if (curseurTemps.time  < marqueurDebut.time )
			curseurTemps.time = marqueurDebut.time ;
		else if (curseurTemps.time > marqueurFin.time)
			curseurTemps.time = marqueurFin.time;
	}

	public void updateMarqueur()
	{
		/*
		if (marqueurDebut.time < selectedMedia.start)
			marqueurDebut.time = selectedMedia.start;
		else if (marqueurFin.time > selectedMedia.start + selectedMedia.length)
			marqueurFin.time = selectedMedia.start + selectedMedia.length;
		 */

		if (marqueurDebut.time > marqueurFin.time & marqueurDebutIsTouched & marqueurDebut.time > selectedMedia.start)
			marqueurFin.time = marqueurDebut.time;

		if (marqueurFin.time < marqueurDebut.time & marqueurFinIsTouched & marqueurFin.time < selectedMedia.start + selectedMedia.length)
			marqueurDebut.time = marqueurFin.time;


	}

	public void spanning(float move)
	{
		long newT0 = t0 - pixelToTime(move);

		if (newT0 >= 0 & (newT0 + pixelToTime(screenWidth)) <= dureeMission)
			t0 = newT0;
	}

	public void zooming(float x1, float x2, float x1p, float x2p)
	{	
		float newScale = Math.abs((x1-x2) / (x1p-x2p) * scale);
		float move = scale * x1 - newScale * x1p;
		System.out.println("x1=" + x1 + " x2=" + x2 + " x1'=" + x1p + " x2'=" + x2p);
		System.out.println("move=" + move);
		System.out.println("newScale=" + newScale);

		//if (newScale>minScale & newScale < 100)
		//	{
		scale = newScale;
		//spanning(move);
		//	}
	}
}
