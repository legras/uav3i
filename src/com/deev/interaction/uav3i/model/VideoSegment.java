package com.deev.interaction.uav3i.model;

import java.util.ArrayList;

public class VideoSegment
{
	private long _start;
	private long _end;
	private String _name;
	
	public VideoSegment(long start, long end)
	{
		this(start, end, "FILE"+start+".AVI");
	}
	
	public VideoSegment(long start, long end, String name)
	{
		_start = start;
		_end = end;
		_name = name;
	}
	
	public long getStart()
	{
		return _start;
	}
	
	public long getEnd()
	{
		return _end;
	}
	
	public long getLength()
	{
		return _end-_start;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public static ArrayList<VideoSegment> makeDummyVSegmentList()
	{
		ArrayList<VideoSegment> list = new ArrayList<VideoSegment>();
		
		long l30min = 30 * 60 * 1000;

		long start = System.currentTimeMillis() - (long) (l30min*1.2);
		
		while (start < System.currentTimeMillis() + 4*l30min)
		{
			list.add(new VideoSegment(start, start+l30min));
			start += l30min;
		}
		
		return list;
	}
}
