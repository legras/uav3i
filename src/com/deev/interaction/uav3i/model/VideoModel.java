package com.deev.interaction.uav3i.model;

import java.util.ArrayList;

public class VideoModel
{
	public static VideoModel video = null;
	
	private boolean _isPlaying;
	private boolean _isPaused;
	private long _seqStart;
	private long _seqEnd;
	
	// Dummy
	private long _lastCursorPosition;
	private long _cursorPosition;
	private long _playStart;
	private ArrayList<VideoSegment> _segmentList;
	
	public static void initialize()
	{
		if (video == null)
			video = new VideoModel();
	}
	
	public VideoModel()
	{
		_segmentList = VideoSegment.makeDummyVSegmentList();
	}
	
	public CameraFootprint getFootprintNow()
	{
		return getFootprintAtTime(System.currentTimeMillis());
	}
	
	public CameraFootprint getFootprintAtTime(long time)
	{
		UAVDataPoint data = UAVModel.store.getDataPointAtTime(time);
		return new CameraFootprint(data.latlng, data.course, time);
	}
	
	
	
	public void setPlaySequence(long start, long end)
	{
		_seqStart = start;
		_seqEnd = end;
		_lastCursorPosition = _cursorPosition = _seqStart;
		_isPlaying = false;
	}
	
	public long getCursorPosition()
	{
		if (_isPaused)
			return _lastCursorPosition;
		else
			_cursorPosition = _lastCursorPosition + System.currentTimeMillis() - _playStart;
		
		return _cursorPosition;
	}
	
	public boolean isPlaying()
	{
		return _isPlaying;
	}
	
	/**
	 * Has sense only if isPlaying() is true.
	 * @return
	 */
	public boolean isPaused()
	{
		return _isPaused;
	}
	
	public void play()
	{
		_playStart = System.currentTimeMillis();
		_isPlaying = true;
		_isPaused = false;
	}
	
	public void pause()
	{
		_lastCursorPosition = getCursorPosition();
		_isPaused = true;
	}
	
	public ArrayList<VideoSegment> getVideoSegments()
	{
		return _segmentList;
	}
}
