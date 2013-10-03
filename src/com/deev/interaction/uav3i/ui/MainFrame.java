package com.deev.interaction.uav3i.ui;


import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;

import com.deev.interaction.common.ui.Animator;
import com.deev.interaction.common.ui.ComponentLayer;
import com.deev.interaction.common.ui.FingerPane;
import com.deev.interaction.uav3i.replay.TimeLine;

/*
 * 
 * 
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame
{
	protected TouchGlass _glass = null;

	public static OsmMapGround OSMMap;

	public MainFrame(String domain)
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
			// rb.mouseMove(screenSize.width, screenSize.height);
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

		//		MapGround grnd = null;
		//		BufferedImage image;
		//		try
		//		{
		//			image = ImageIO.read(this.getClass().getResource(Launcher.REGION+".png"));			
		//grnd = new MapGround(image);
		//			grnd.panPx(image.getWidth()/2., -image.getHeight()/2.);
		//		}
		//		catch (IOException e1)
		//		{
		//			// TODO Auto-generated catch block
		//			e1.printStackTrace();
		//		}

		// ******* Carte OSM *******
		OsmMapGround grnd = new OsmMapGround();

		MainFrame.OSMMap = grnd;

		//GoogleMapInteractionPane mapInteractionPane = new GoogleMapInteractionPane(grnd, mapManagerUI);
		OsmMapInteractionPanel mapInteractionPane = new OsmMapInteractionPanel(grnd);
		grnd.setBounds(0, 0, screenSize.width, screenSize.height);
		lpane.add(grnd, new Integer(-20));
		mapInteractionPane.setBounds(0, 0, screenSize.width, screenSize.height);
		lpane.add(mapInteractionPane, new Integer(-4));

		SymbolMap map = new SymbolMap(domain);
		map.setBounds(0, 0, screenSize.width, screenSize.height);
		lpane.add(map, new Integer(-10));
		map.alignWith(grnd);
		// map.setupToSize();

		FingerPane fingerpane = new FingerPane();
		fingerpane.setBounds(0, 0, screenSize.width, screenSize.height);
		fingerpane.setTopMap(map);
		lpane.add(fingerpane, new Integer(-2));

		ComponentLayer clayer = new ComponentLayer();
		clayer.setBounds(0, 0, screenSize.width, screenSize.height);
		lpane.add(clayer, new Integer(-1));

		// ********** La TimeLine **********
		TimeLine tm = new TimeLine(screenSize.width, screenSize.height);
		TimeLine.tl = tm;
		tm.setBounds(0, 0, screenSize.width, screenSize.height);
		lpane.add(tm, new Integer(-3));

		_glass = new TouchGlass();
		_glass.setTopMap(map);
		_glass.setComponentLayer(clayer);

		Animator.addComponent(fingerpane);
		Animator.addComponent(tm);
		Animator.addComponent(mapInteractionPane);
		Animator.go();

		this.addWindowListener(new WindowAdapter()
		{	
			public void windowClosing(WindowEvent we)
			{
				//_glass.die();
				// System.exit(0);
			}

			public void windowActivated(WindowEvent e)
			{ 		       
				getContentPane().requestFocus();	
			}
		});

		this.addMouseListener(_glass);
		this.addMouseMotionListener(_glass);

		_glass.addTouchable(fingerpane);
		_glass.addTouchable(tm);
		_glass.addTouchable(mapInteractionPane);

		setGlassPane(_glass); // niveau affichage superieur

		_glass.setVisible(true);
		//grnd.updateTransform();
	}

	public TouchGlass getGlass()
	{
		return _glass;
	}
}
