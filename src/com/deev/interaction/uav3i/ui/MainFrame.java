package com.deev.interaction.uav3i.ui;


import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;

import com.deev.interaction.touch.Animator;
import com.deev.interaction.touch.ComponentLayer;
import com.deev.interaction.touch.RoundToggleButton;
import com.deev.interaction.touch.TintedBufferedImage;

import eu.telecom_bretagne.uav3i.UAV3iSettings;
import eu.telecom_bretagne.uav3i.maps.OsmMapGround;
import eu.telecom_bretagne.uav3i.maps.OsmMapInteractionPanel;
import eu.telecom_bretagne.uav3i.util.log.LoggerUtil;

/*
 * 
 * 
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame implements ActionListener
{

	public static OsmMapGround OSMMap;
	
	public enum MainFrameState {COMMAND, MAP, REPLAY};
	public static MainFrameState state = MainFrameState.MAP;
	public static ComponentLayer clayer;
	
	protected static TouchGlass _GLASS = null;
	public static TimeLine TIMELINE;
	
	public static Switcher3Buttons SWITCHER;
	protected static SymbolMap _SMAP;;

	public MainFrame()
	{
		super();
		// Pour capter les évenements TUIO via le bridge Touch2Tuio sous Windows.
		setTitle("uav3i");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try
		{
			setIconImage(ImageIO.read(this.getClass().getResource("img/3i_icon_small.png")));
		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Dimension screenSize = new Dimension(1366, 768);

		try
		{
			setUndecorated(true);
			screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			setBounds(0, 0, screenSize.width, screenSize.height);

			Robot rb = new Robot(); // move mouse cursor out of the way to lower right
			if (UAV3iSettings.getFullscreen())
				rb.mouseMove(screenSize.width, screenSize.height);
		}
		catch (HeadlessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (AWTException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JLayeredPane lpane = this.getLayeredPane();

		// ******* Carte OSM *******
		OsmMapGround grnd = new OsmMapGround();

		MainFrame.OSMMap = grnd;

		//GoogleMapInteractionPane mapInteractionPane = new GoogleMapInteractionPane(grnd, mapManagerUI);
		OsmMapInteractionPanel mapInteractionPane = new OsmMapInteractionPanel(grnd);
		grnd.setBounds(0, 0, screenSize.width, screenSize.height);
		lpane.add(grnd, new Integer(-20));
		mapInteractionPane.setBounds(0, 0, screenSize.width, screenSize.height);
		lpane.add(mapInteractionPane, new Integer(-4));

		SymbolMap map = new SymbolMap();
		map.setBounds(0, 0, screenSize.width, screenSize.height);
		lpane.add(map, new Integer(-10));
		_SMAP = map;

		FingerPane fingerpane = new FingerPane();
		fingerpane.setBounds(0, 0, screenSize.width, screenSize.height);
		fingerpane.setTopMap(map);
		lpane.add(fingerpane, new Integer(-2));

		clayer = new ComponentLayer();
		clayer.setBounds(0, 0, screenSize.width, screenSize.height);
		lpane.add(clayer, new Integer(-1));
		
		// ********** Splash ***************
		Splash3i splash = null;
		try
		{
			splash = new Splash3i(clayer);
			splash.setCenter(screenSize.width/2, screenSize.height/2);
		}
		catch (IOException e1)
		{
			LoggerUtil.LOG.log(Level.WARNING, "Could not make splash screen");
		}
		
		// ********** 3i button ************
		try
		{
			BufferedImage icon3i = ImageIO.read(this.getClass().getResource("img/3iButton.png"));
			RoundToggleButton button3i = new RoundToggleButton(
					new TintedBufferedImage(icon3i, new Color(.74f, .80f, .03f, 1.f)),
					new TintedBufferedImage(icon3i, new Color(.3f, .3f, .3f, 1.f)));
			clayer.add(button3i);
			button3i.setBounds(screenSize.width-12-icon3i.getWidth(), 12, icon3i.getWidth(), icon3i.getHeight());
			button3i.addActionListener(splash);
		}
		catch (IOException e1)
		{
			LoggerUtil.LOG.log(Level.WARNING, "Could not load 3i icon for button");
		}
		
		
		// ********** Mode switch **********
		Switcher3Buttons mswitch = new Switcher3Buttons(this);
		SWITCHER = mswitch;
		Dimension swd = mswitch.getSize();
		mswitch.setBounds(screenSize.width/2-swd.width/2, 12, swd.width, swd.height);
		clayer.add(mswitch);

		// ********** La TimeLine **********
		TIMELINE = new TimeLine(screenSize.width, screenSize.height);
		lpane.add(TIMELINE, new Integer(-3));

		_GLASS = new TouchGlass();

		Animator.addComponent(fingerpane);
		Animator.addAnimation(TIMELINE);
		Animator.addComponent(mapInteractionPane);
		Animator.go();

		this.addWindowListener(new WindowAdapter()
		{	
			public void windowClosing(WindowEvent we)
			{
				//_glass.die();
				System.exit(0);
			}

			public void windowActivated(WindowEvent e)
			{ 		       
				getContentPane().requestFocus();	
			}
		});

		this.addMouseListener(_GLASS);
		this.addMouseMotionListener(_GLASS);

		_GLASS.addTouchable(fingerpane);
		_GLASS.addTouchable(map);
		_GLASS.addTouchable(TIMELINE);
		_GLASS.addTouchable(mapInteractionPane);
		_GLASS.addTouchable(clayer);

		setGlassPane(_GLASS);		
		_GLASS.setVisible(true);
	}

	public static MainFrameState getAppState()
	{
		switch (SWITCHER.getMode())
		{
			case COMMAND:
				return MainFrameState.COMMAND;
			case REPLAY:
				return MainFrameState.REPLAY;
			case MAP:
			default:
				return MainFrameState.MAP;
		}	}
	
	public TouchGlass getGlass()
	{
		return _GLASS;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		switch (SWITCHER.getMode())
		{
			case COMMAND:
				TIMELINE.hide();
				break;
			case MAP:
				TIMELINE.hide();
				_SMAP.hideManoeuverButtons();
				break;
			case REPLAY:
				TIMELINE.show();
				_SMAP.hideManoeuverButtons();
				break;
			default:
				return;
		}
	}
}
