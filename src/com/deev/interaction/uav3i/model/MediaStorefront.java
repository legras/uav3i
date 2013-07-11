package com.deev.interaction.uav3i.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;

import com.deev.interaction.common.ui.Animation;
import com.deev.interaction.uav3i.model.Media.MediaType;

public class MediaStorefront
{
	private static int _DELAY_ = 50;//en milliseconde = temps de rafraichissement
	protected static ArrayList<Media> _mediaList = null;
	protected static long _current;
	protected static long _lastUpdate = -1;
	protected static float _rate = 1.5f;
	
	public static ArrayList<Media> getMediaList()
	{
		checkList();
	
		return _mediaList;
	}
	
	protected static void checkList()
	{
		if (_mediaList == null)
			_mediaList = new ArrayList<Media>();
	}
	
	public static long currentTime()
	{
		return System.currentTimeMillis();
	}
	
	public static void start()
	{
		checkList();
		
		TimerTask task = new TimerTask()
		{
			public void run()
			{
				long time = currentTime();
				if (_lastUpdate < 0)
					_lastUpdate = time-1;
				
				updateDownloads((long) ((time-_lastUpdate)*_rate));
				while (clean()) ;
				
				_lastUpdate = time;
			}
		};
		
		Timer timer = new Timer();
		timer.schedule(task, 0, _DELAY_);
	}
	
	private static boolean clean()
	{
		Media m1, m2;
		
		synchronized (_mediaList)
		{					
			int n = _mediaList.size();
			
			for (int i=0; i<n-1; i++)
				for (int j=i+1; j<n; j++)
				{
					m1 = _mediaList.get(i);
					m2 = _mediaList.get(j);
					
					if ((m1.getType() == m2.getType()) && m1.doesTouch(m2))
					{
						m1.join(m2);
						_mediaList.remove(m2);
						return true;
					}
				}			
		}
		
		return false;
	}
	
	public static void addMedia(String name, long start, long length)
	{
		checkList();
		
		Media m = new Media(name);
		m.start = start;
		m.length = length;
		_mediaList.add(m);
	}
	
	public static void addDownload(String name, long start, long length)
	{
		checkList();
		
		Media m = new Media(name);
		m.setType(MediaType.DOWNLOADING);
		m.start = start;
		m.length = length;
		_mediaList.add(m);
	}
	
	protected static void updateDownloads(long delta)
	{
		synchronized (_mediaList)
		{
			ArrayList<Media> addList = new ArrayList<Media>();
		    Iterator<Media> itr = _mediaList.iterator();
		    Media m, slice;
		    while (itr.hasNext())
			{
				m = itr.next();
							
				if (m.getType() == MediaType.DOWNLOADING)
				{
					slice = new Media(m.name);
					slice.start = m.start;
					slice.length = delta;
					
					addList.add(slice);
					
					m.start += delta;
					m.length -= delta;
					if (m.length <= 0)
						itr.remove();
				}
			}
		    
		    itr = addList.iterator();
		    while (itr.hasNext())
		    	_mediaList.add(itr.next());
		}
	}
	
	public static long getCurrentDisplayedVideoTime()
	{
		return _current;
	}
	
	public static void testFill()
	{	
		checkList();
		
		addMedia("video001", 50000, 300000);
		addDownload("video00jh", 500001, 60000);
		addMedia("video002", 525000, 150000);
		addMedia("video003", 700000, 50000);
		addMedia("video004", 800000, 150000);	
		
	}
}
