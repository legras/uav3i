package com.deev.interaction.uav3i.replay;

import java.util.Timer;
import java.util.TimerTask;
 
public class PlayTimer {
    public static boolean isPlaying = false;
    public static boolean isDownloadable= false;
    public static boolean loop = true;
    public static TimeLine timeLine ;
    
    public static void main(String[] args) {
        final PlayTimer pt = new PlayTimer();
 
        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
            	if (!loop)
            	{
            		if (isPlaying & timeLine.curseurTemps.time < timeLine.selectedMedia.endFocus)
            		{	
            			timeLine.curseurTemps.time++;
            		}
            	}
            	else
            	{
            		if (isPlaying)
            		{
            			if (timeLine.curseurTemps.time < timeLine.selectedMedia.endFocus)
            				timeLine.curseurTemps.time++;
            			else if (timeLine.curseurTemps.time == timeLine.selectedMedia.endFocus)
            				timeLine.curseurTemps.time = timeLine.selectedMedia.beginFocus;
   
            		}
            		
            	}
            	
            	
            	
            	if(isDownloadable)
            	{
	            	if (timeLine.selectedMedia.endFocus > timeLine.selectedMedia.start + timeLine.selectedMedia.length)
	            	{
	            		timeLine.selectedMedia.length++;
	            	}
	            	
	            	if (timeLine.selectedMedia.beginFocus < timeLine.selectedMedia.start)
	            	{
	            		timeLine.selectedMedia.start--;
	            		timeLine.selectedMedia.length++;
	            	}
	            	
            	}
            }
        }, 0, 1);
    }
}