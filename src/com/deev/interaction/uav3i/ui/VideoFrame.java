package com.deev.interaction.uav3i.ui;

import javax.swing.JFrame;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import com.deev.interaction.uav3i.model.VideoModel;
import com.deev.interaction.uav3i.util.UAV3iSettings;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

public class VideoFrame extends JFrame
{
	private final EmbeddedMediaPlayerComponent _mediaPlayerComponent;
	
	public VideoFrame()
	{
		super("VLC");
		
		//NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), UAV3iSettings.getVLCLibPath());
		//Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
		
		_mediaPlayerComponent = new EmbeddedMediaPlayerComponent();

        this.setContentPane(_mediaPlayerComponent);
        
        VideoModel.video.setMediaPlayerComponent(_mediaPlayerComponent);
	}
}
