package com.deev.interaction.uav3i.ui;


import javafx.scene.paint.Color;
import javax.swing.JFrame;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;


public class VideoFrame extends JFrame
{
	static String filename = "file:/Users/legras/Desktop/1_EO_20140715_103018.flv";
	public static MediaPlayer mediaPlayer;
	
	public VideoFrame()
	{
		super("AIS Video");
		
		final JFXPanel jFXPanel = new JFXPanel();
        this.add(jFXPanel);
         
        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                initFxLater(jFXPanel);
            }
        });
		
        
	}
	
	private static void initFxLater(JFXPanel panel)
	{
        Group root = new Group();
        Scene scene = new Scene(root, 640, 480);
        scene.setFill(Color.BLACK);
         
        // create media player
        Media media = new Media(filename);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
         
        // create mediaView and add media player to the viewer
        MediaView mediaView = new MediaView(mediaPlayer);
        ((Group)scene.getRoot()).getChildren().add(mediaView);
         
        panel.setScene(scene);
    }
}
