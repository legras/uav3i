package com.deev.interaction.uav3i.ui;


import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;

import com.deev.interaction.common.ui.Animator;
import com.deev.interaction.common.ui.ComponentLayer;
import com.deev.interaction.uav3i.replay.TimeLine;

import eu.telecom_bretagne.uav3i.UAV3iSettings;
import eu.telecom_bretagne.uav3i.maps.OsmMapGround;
import eu.telecom_bretagne.uav3i.maps.OsmMapInteractionPanel;

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
	
	protected TouchGlass _glass = null;
	protected TimeLine _timeline;
	
	protected static Switcher3Buttons _SWITCHER;
	protected static ManoeuverButtons _MNVR_BUTTONS;

	public MainFrame()
	{
		super();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try
		{
			setIconImage(ImageIO.read(this.getClass().getResource("3i_icon_small.png")));
		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Dimension screenSize = new Dimension(1366, 768);
		//    Dimension screenSize = new Dimension(25, 212);
		//    Dimension screenSize = new Dimension(1280, 600);
		//    Dimension screenSize = new Dimension(1400, 400);
		//    Dimension screenSize = new Dimension(200, 1500);
		//    Dimension screenSize = new Dimension(500, 400);

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
		map.alignWith(grnd);

		FingerPane fingerpane = new FingerPane();
		fingerpane.setBounds(0, 0, screenSize.width, screenSize.height);
		fingerpane.setTopMap(map);
		lpane.add(fingerpane, new Integer(-2));

		ComponentLayer clayer = new ComponentLayer();
		clayer.setBounds(0, 0, screenSize.width, screenSize.height);
		lpane.add(clayer, new Integer(-1));
		
		// ********** Mode switch **********
		Switcher3Buttons mswitch = new Switcher3Buttons(this);
		_SWITCHER = mswitch;
		Dimension swd = mswitch.getSize();
		mswitch.setBounds(screenSize.width/2-swd.width/2, 2, swd.width, swd.height);
		clayer.add(mswitch);
		
		// ********** ManoeuverButtons **********
		try
		{
			_MNVR_BUTTONS = new ManoeuverButtons(this);
			Dimension mbd = _MNVR_BUTTONS.getSize();
			_MNVR_BUTTONS.setBounds(400, 400, mbd.width, mbd.height);
			clayer.add(_MNVR_BUTTONS);
			System.out.println("MainFrame: ManoeuverButtons OK");
		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
			_MNVR_BUTTONS = null;
		}

		// ********** La TimeLine **********
		TimeLine tm = new TimeLine(screenSize.width, screenSize.height);
		lpane.add(tm, new Integer(-3));
		_timeline = tm;

		_glass = new TouchGlass();

		Animator.addComponent(fingerpane);
		Animator.addAnimation(tm);
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

		this.addMouseListener(_glass);
		this.addMouseMotionListener(_glass);

		_glass.addTouchable(fingerpane);
		_glass.addTouchable(map);
		_glass.addTouchable(tm);
		_glass.addTouchable(mapInteractionPane);
		_glass.addTouchable(clayer);

		setGlassPane(_glass);		
		_glass.setVisible(true);
	}

	public static MainFrameState getAppState()
	{
		switch (_SWITCHER.getMode())
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
		return _glass;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		switch (_SWITCHER.getMode())
		{
			case COMMAND:
				_timeline.hide();
				break;
			case MAP:
				_timeline.hide();
				break;
			case REPLAY:
				_timeline.show();
				break;
			default:
				return;
		}
	}
}
