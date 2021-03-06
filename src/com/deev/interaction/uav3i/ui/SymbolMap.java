package com.deev.interaction.uav3i.ui;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import org.openstreetmap.gui.jmapviewer.Coordinate;

import TUIO.TuioCursor;

import com.deev.interaction.touch.Touchable;
import com.deev.interaction.uav3i.model.CameraFootprint;
import com.deev.interaction.uav3i.model.UAVDataPoint;
import com.deev.interaction.uav3i.model.UAVModel;
import com.deev.interaction.uav3i.model.UAVWayPoint;
import com.deev.interaction.uav3i.model.VideoModel;
import com.deev.interaction.uav3i.ui.Manoeuver.ManoeuverRequestedStatus;
import com.deev.interaction.uav3i.util.UAV3iSettings;
import com.deev.interaction.uav3i.util.UAV3iSettings.Mode;
import com.deev.interaction.uav3i.util.log.LoggerUtil;
import com.deev.interaction.uav3i.util.paparazzi_settings.flight_plan.FlightPlanFacade;
import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTO;

import uk.me.jstott.jcoord.LatLng;

@SuppressWarnings("serial")
public class SymbolMap extends Map implements Touchable
{		
	private ArrayList<Manoeuver> _manoeuvers = null;
	private Manoeuver _currentMnvr = null;
	private Object _adjustingTouch = null;
	private boolean _areShareAndAskLocked = false;
	private int manoeuverSequence = 0;
	
	private ArrayList<Touchable> _touchSymbols;
	private HashMap<Object, Touchable> _touchedSymbols;

	private Trajectory _trajectory;
	private long _lastTrajectoryUpdate = 0;
	
	private Ruler _ruler = null;

	private LatLng _startPoint;
	private int _maxDistanceFromHome;

	protected static BufferedImage _uavImage      = null;
	protected static BufferedImage _uavGrayImage  = null;
	protected static BufferedImage _waypointImage = null;
	protected static BufferedImage _camImage      = null;

	public SymbolMap()
	{
		super();

		setVisible(true);
		setOpaque(false);
		Color back = new Color(0.f, 0.f, 0.f, .0f);
		setBackground(back);	

		_manoeuvers = new ArrayList<Manoeuver>();
		_touchSymbols = new ArrayList<Touchable>();
		_touchedSymbols = new HashMap<Object, Touchable>();
		
		_trajectory = new Trajectory();
		
		_ruler = new Ruler(this);
		addTouchSymbol(_ruler);
		
	    _startPoint          = FlightPlanFacade.getInstance().getStartPoint();
	    _maxDistanceFromHome = FlightPlanFacade.getInstance().getMaxDistanceFromHome();


		// Dessin UAV + way points
		try
		{
//      _uavImage      = ImageIO.read(this.getClass().getResource("/img/uav.png"));
      _uavImage      = ImageIO.read(this.getClass().getResource("/img/rotorcraft.png"));
			_uavGrayImage  = ImageIO.read(this.getClass().getResource("/img/rotorcraft gray.png"));
			_waypointImage = ImageIO.read(this.getClass().getResource("/img/waypoint.png"));
			_camImage      = ImageIO.read(this.getClass().getResource("/img/cam_target.png"));
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addTouchSymbol(Touchable t)
	{
		synchronized (_touchSymbols)
		{
			_touchSymbols.add(t);
		}
	}
	
	public void removeTouchSymbol(Touchable t)
	{
		synchronized (_touchSymbols)
		{
			_touchSymbols.remove(t);
			
			for(Iterator<Entry<Object,Touchable>> it = _touchedSymbols.entrySet().iterator(); it.hasNext();)
			{
				Entry<Object,Touchable> entry = it.next();
			      if(entry.getValue() == t)
			      {
			        it.remove();
			      }
			}
		}
	}

	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		paint(g2);
	}

	public synchronized void paint(Graphics2D g2)
	{	
		if (_currentMnvr != null)
		{
			_currentMnvr.instaMoveButtons();
		}
		
		long currentTime = System.currentTimeMillis();

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		// Zone d'opérations
		float radius = (float) (_maxDistanceFromHome * getPPM());
		float THICK = 40.f;
		
		Point2D.Double center = getScreenForLatLng(_startPoint);
		float[] dist = {0.f, radius/(radius+THICK), radius/(radius+THICK)+.000001f, 1.f};
		Color[] colors = {
				(Color) Palette3i.getPaint(Palette3i.UAV_SCOPE_CLEAR),
				(Color) Palette3i.getPaint(Palette3i.UAV_SCOPE_CLEAR),
				(Color) Palette3i.getPaint(Palette3i.UAV_SCOPE),
				(Color) Palette3i.getPaint(Palette3i.UAV_SCOPE_CLEAR)};
		RadialGradientPaint gradPaint = new RadialGradientPaint(center, radius + THICK, dist, colors);

		g2.setPaint(gradPaint);
		g2.fill(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		
		// Command Grid ?
		if (MainFrame.SWITCHER.getMode() == Switcher3Buttons.Switcher3ButtonsMode.COMMAND)
		{
			
			LatLng startPoint = FlightPlanFacade.getInstance().getStartPoint();

			Point p = MainFrame.OSMMap.getMapViewer().getMapPosition(startPoint.getLat(), startPoint.getLng(), false);
			
			double step = _ruler.getSmallestPxStep();
			
			int x, y;
			
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			g2.setPaint(Color.WHITE);
			
			for (x = p.x; x < screenSize.width; x += step)
			{
				g2.drawLine(x, 0, x, screenSize.height);				
			}
			
			for (x = p.x - (int) step; x > 0; x -= step)
			{
				g2.drawLine(x, 0, x, screenSize.height);				
			}
			
			for (y = p.y; y < screenSize.height; y += step)
			{
				g2.drawLine(0, y, screenSize.width, y);				
			}
			
			for (y = p.y - (int) step; y > 0; y -= step)
			{
				g2.drawLine(0, y, screenSize.width, y);				
			}
		}
		
		// WayPoints
		FontRenderContext frc = g2.getFontRenderContext();
		Font f = new Font("Futura", Font.PLAIN, 12);
		g2.setStroke(new BasicStroke(3.f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
		TextLayout textTl;
		Shape outline;
		synchronized (this)
		{
			for(UAVWayPoint wayPoint : UAVModel.getWayPoints().getWayPoints())
			{
				LatLng wayPointPosition = wayPoint.getWayPointPosition();
				if(wayPointPosition != null)
				{
					Point p = MainFrame.OSMMap.getMapViewer().getMapPosition(wayPointPosition.getLat(), wayPointPosition.getLng(), false);
					g2.drawImage(_waypointImage,
							p.x - _waypointImage.getWidth()/2,
							p.y - _waypointImage.getHeight()/2,
							null);

					
					textTl = new TextLayout(wayPoint.getWayPointName(), f, frc);
					outline = textTl.getOutline(null);
					
					AffineTransform old = g2.getTransform();
					g2.translate(p.x+_waypointImage.getWidth()/2, p.y+_waypointImage.getHeight()/2);
					g2.setPaint(Palette3i.getPaint(Palette3i.WHITE_BG));
					g2.draw(outline);
					g2.setPaint(Color.GRAY);
					g2.fill(outline);
					g2.setTransform(old);
				}
			}
		}
		
		// Update de trajectoire
		if (currentTime - _lastTrajectoryUpdate > 500)
		{
			_trajectory.update();
			_lastTrajectoryUpdate = currentTime;
		}

		// Tracé de trajectoire
//    GeneralPath fullTrajectory = _trajectory.getFullPath(this); 
    GeneralPath fullTrajectory = _trajectory.getRecentPath(this); 

		final float dash1[] = {8.f, 4.f};
	    final BasicStroke dashed =
	        new BasicStroke(2.f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f, dash1, 0.f);
		
		if (fullTrajectory != null)
		{
			g2.setPaint(new Color(1.f, 1.f, 1.f, .5f));
			// g2.setPaint(new Color(0.f, 0.f, 0.f, .3f));
			g2.setStroke(new BasicStroke(4.f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
			g2.draw(fullTrajectory);
			g2.setPaint(Color.RED);
			g2.setStroke(dashed);
			g2.draw(fullTrajectory);
		}
		
		if (MainFrame.SWITCHER.getMode() == Switcher3Buttons.Switcher3ButtonsMode.REPLAY)
		{
			// Tracé de séquence
			GeneralPath seqTrajectory = _trajectory.getTrajectorySequence(this,
					MainFrame.TIMELINE.getTimeSeqStart(),
					MainFrame.TIMELINE.getTimeSeqend());
			
			if (seqTrajectory != null)
			{
				g2.setPaint(new Color(1.f, 1.f, 1.f, .7f));
				g2.setStroke(new BasicStroke(10.f, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
				g2.draw(seqTrajectory);
			}
		}
		// --------- Manoeuvers --------------------------------------------------
		synchronized(this)
		{
			for (Manoeuver m : _manoeuvers)
				m.paint(g2);
		}

		if (_ruler != null)
			_ruler.paint(g2);
		
		// Footprint
		long time;
		if (MainFrame.SWITCHER.getMode() == Switcher3Buttons.Switcher3ButtonsMode.REPLAY)
			time = MainFrame.TIMELINE.getTimeCursorPosition();
		else
			time = System.currentTimeMillis();

		// FIXME : en mode PAPARAZZI_REMOTE, le temps de recevoir des données, une NullPointerException se fait un peu vive...
		// On la catche pour le moment...
		try
		{
			CameraFootprint cfp = UAVModel.getDataPointAtTime(time).getCameraFootprint();

			if (cfp != null)
			{
				GeneralPath foot = footPrintPath(cfp);
				g2.setPaint(Palette3i.FOOTPRINT_FILL.getPaint());
				g2.fill(foot);
				g2.setStroke(new BasicStroke(2.f));
				g2.setPaint(Palette3i.FOOTPRINT_DRAW.getPaint());
				g2.draw(foot);
			}
		}
		catch(NullPointerException npe)
		{
		}

		// Dessin UAV
		AffineTransform old = g2.getTransform();	
		BufferedImage uavImg;
		UAVDataPoint uavpoint;
		
		if (MainFrame.SWITCHER.getMode() == Switcher3Buttons.Switcher3ButtonsMode.REPLAY)
		{
			uavImg = _uavGrayImage;
		}
		else
		{
			uavImg = _uavImage;
		}

		uavpoint = UAVModel.getDataPointAtTime(System.currentTimeMillis());
		if (uavpoint != null)
		{
			Point2D.Double uav = getScreenForLatLng(uavpoint.latlng);
			double course = Math.PI/2. - uavpoint.course/180.*Math.PI;
			g2.translate(uav.x, uav.y);

			g2.rotate(Math.PI/2.-course);
			g2.drawImage(uavImg, -uavImg.getWidth()/2, -uavImg.getHeight()/2, null);
		}
		g2.setTransform(old);

    // Temporaire : dessin de la cible caméra
		/*
    if(uavpoint != null && uavpoint.getCameraTarget() != null)
    {
      Point camTarget = MainFrame.OSMMap.getMapViewer().getMapPosition(uavpoint.getCameraTarget().getLat(), uavpoint.getCameraTarget().getLng(), false);
      g2.drawImage(_camImage,
                   camTarget.x - _camImage.getWidth()/2,
                   camTarget.y - _camImage.getHeight()/2,
                   null);
    }
    g2.setTransform(old);
    */

//		// Cam status
//		if(UAVModel.getCamCoord() != null && UAVModel.getCamTargetCoord() != null)
//		{
//      Point cam       = MainFrame.OSMMap.getMapViewer().getMapPosition(UAVModel.getCamCoord().getLat(), UAVModel.getCamCoord().getLng(), false);
//      Point camTarget = MainFrame.OSMMap.getMapViewer().getMapPosition(UAVModel.getCamTargetCoord().getLat(), UAVModel.getCamTargetCoord().getLng(), false);
//      
//      g2.drawImage(_camImage,
//                   cam.x - _camImage.getWidth()/2,
//                   cam.y - _camImage.getHeight()/2,
//                   null);
//      g2.drawImage(_camImage,
//                   camTarget.x - _camImage.getWidth()/2,
//                   camTarget.y - _camImage.getHeight()/2,
//                   null);
//		}
		
		// Dessin UAV
		old = g2.getTransform();	
		if (MainFrame.SWITCHER.getMode() == Switcher3Buttons.Switcher3ButtonsMode.REPLAY)
		{
			uavImg = _uavImage;
			
			uavpoint = UAVModel.getDataPointAtTime(MainFrame.TIMELINE.getTimeCursorPosition());
			if (uavpoint != null)
			{
				Point2D.Double uav = getScreenForLatLng(uavpoint.latlng);
				double course = Math.PI/2. - uavpoint.course/180.*Math.PI;
				g2.translate(uav.x, uav.y);

				g2.rotate(Math.PI/2.-course);
				g2.drawImage(uavImg, -uavImg.getWidth()/2, -uavImg.getHeight()/2, null);

			}
		}
		g2.setTransform(old);
	}
	
	private GeneralPath footPrintPath(CameraFootprint footprint)
	{
		GeneralPath poly = new GeneralPath();
		
		Point2D.Double p = null;
		
		for (LatLng ll : footprint)
		{
			if (p == null)
			{
				p = getScreenForLatLng(ll);
				poly.moveTo(p.x,  p.y);
			}
			else
			{
				p = getScreenForLatLng(ll);
				poly.lineTo(p.x,  p.y);
			}
		}

		poly.closePath();
		
		return poly;
	}

	@Override
	public double getPPM()
	{
		return 1. / MainFrame.OSMMap.getMapViewer().getMeterPerPixel();
	}

	public Point2D.Double getScreenForLatLng(LatLng latlng)
	{	
		Point a = MainFrame.OSMMap.getMapViewer().getMapPosition(latlng.getLat(), latlng.getLng(), false);
		Point b = new Point(a.x+1, a.y+1);
		
		LatLng A = getLatLngForScreen(a.x, a.y);
		LatLng B = getLatLngForScreen(b.x, b.y);
		
		double X = (double) a.x + (latlng.getLng()-A.getLng()) / (B.getLng()-A.getLng());
		double Y = (double) a.y + (latlng.getLat()-A.getLat()) / (B.getLat()-A.getLat());
		
		return new Point2D.Double(X, Y);
	}

	public LatLng getLatLngForScreen(double x, double y)
	{		
		int X = (int) x;
		int Y = (int) y;

		Coordinate A = MainFrame.OSMMap.getMapViewer().getPosition(X, Y);
		Coordinate B = MainFrame.OSMMap.getMapViewer().getPosition(X+1, Y+1);

		double lat = A.getLat() + (y-(double)Y) * (B.getLat()-A.getLat());
		double lon = A.getLon() + (x-(double)X) * (B.getLon()-A.getLon());
		
		return new LatLng(lat, lon);
	}


	public boolean adjustAtPx(double x, double y)
	{
		if (_currentMnvr == null)
			synchronized(this)
			{
				for (Manoeuver m : _manoeuvers)
					if (m.isAdjustmentInterestedAtPx(x, y))
						_currentMnvr = m;
						
			}
		
		// On demande au manoeuver de s'ajuster et on récupère la valeur booléenne
		// du résultat pour la renvoyer ensuite même si elle n'est pas utilisée...
		boolean result = _currentMnvr.adjustAtPx(x, y);
		
		// Move update on Veto UI
		if(_currentMnvr.isShared())
		  UAVModel.communicateManoeuver(_currentMnvr);

		_currentMnvr.positionButtons();
		
		return result;
	}


	public void stopAdjusting()
	{
		if (_currentMnvr == null)
			return;

		_currentMnvr.positionButtons();
		_currentMnvr.stopAdjusting();
		//_adjustingMnvr = null;

		// TODO : François, vérifie que la mise à null de cette variable est correcte. 
		if (!(_adjustingTouch instanceof TuioCursor))
			_adjustingTouch = null;
	}

	@Override
	public float getInterestForPoint(float x, float y)
	{
		synchronized(this)
		{
			for (Manoeuver m : _manoeuvers)
				if (m.isAdjustmentInterestedAtPx(x, y))
					return m.getAdjustInterest();			
		}

		synchronized (_touchSymbols)
		{
			float interest = -1.f;
			
			Iterator<Touchable> itr = _touchSymbols.iterator();
			while(itr.hasNext())
			{
				Touchable t = itr.next();
				float i = t.getInterestForPoint(x, y);
				if (i > interest)
				{
					interest = i;
				}
			}
			
			return interest;
		}
	}

	@Override
	public void addTouch(float x, float y, Object touchref)
	{
		synchronized(this)
		{
			for (Manoeuver m : _manoeuvers)
				if (m.isAdjustmentInterestedAtPx(x, y))
				{
					_currentMnvr = m;
					_adjustingTouch = touchref;
					adjustAtPx(x, y);
					return;
				}
		}

		synchronized (_touchSymbols)
		{
			float interest = Float.NEGATIVE_INFINITY;
			Touchable T = null;
			
			Iterator<Touchable> itr = _touchSymbols.iterator();
			while(itr.hasNext())
			{
				Touchable t = itr.next();
				float i = t.getInterestForPoint(x, y);
				if (i > interest)
				{
					T = t;
					interest = i;
				}
			}
			
			T.addTouch(x, y, touchref);
			_touchedSymbols.put(touchref, T);
			if (T instanceof Manoeuver)
			{
				_currentMnvr = (Manoeuver) T;
				_currentMnvr.setRequestedStatus(ManoeuverRequestedStatus.NONE);
			}
		}
	}

	@Override
	public void updateTouch(float x, float y, Object touchref)
	{
		if (_currentMnvr != null && _adjustingTouch == touchref)
		{
			if (_currentMnvr.isModifiable())
			{
				adjustAtPx(x, y);
				return;
			}
			else
			{
				cancelTouch(touchref);
				return;
			}
		}
		
		synchronized (_touchedSymbols)
		{
			Touchable T = _touchedSymbols.get(touchref);

			if (T != null)
			{
				T.updateTouch(x, y, touchref);
				if(T instanceof Manoeuver)
				{
	        Manoeuver mnvr = (Manoeuver) T;
	        // Move update on Veto UI
	        if(mnvr.isShared())
	          UAVModel.communicateManoeuver(mnvr);
				}
			}
		}
	}

	@Override
	public void removeTouch(float x, float y, Object touchref)
	{
		if (_adjustingTouch == touchref)
		{
			stopAdjusting();
			return;
		}
		
		synchronized (_touchedSymbols)
		{
			Touchable T = _touchedSymbols.get(touchref);
			
			if (T != null)
				T.removeTouch(x, y, touchref);
		}
	}

	@Override
	public void cancelTouch(Object touchref)
	{
		if (_adjustingTouch == touchref)
			stopAdjusting();
		
		synchronized (_touchedSymbols)
		{
			Touchable T = _touchedSymbols.get(touchref);
			
			T.cancelTouch(touchref);
			// TODO à tester !
		}
	}

	/**
	 * @param mnvr can be null
	 */
	public void shareManoeuver(Manoeuver mnvr)
	{
	  if(mnvr != null)
	    UAVModel.communicateManoeuver(mnvr);
	  else
	    UAVModel.clearManoeuver();
		
		synchronized(this)
		{
			for (Manoeuver m : _manoeuvers)
			{
				if (m == mnvr)
					m.setShared(true);
				else
					m.setShared(false);
			}
		}
	}
	
	public void askExecutionManoeuver(Manoeuver mnvr)
	{
	  shareManoeuver(mnvr);
	  //mnvr.setRequestedStatus(ManoeuverRequestedStatus.ASKED);
	  // TODO: voir avec Fanfan mais ça me parait inutil

	  // On lock les share et les jump pour tous et le delete sur mnvr
	  _areShareAndAskLocked = true;

	  for (Manoeuver m : _manoeuvers)
	  {
	    m.lockShareAndAsk(true);
	  }

	  mnvr.lockDelete(true);
	  mnvr.setAsked(true);

	  UAVModel.executeManoeuver(mnvr);

    // En mode PAPARAZZI_DIRECT, on simule l'acceptation de l'opérateur Paparazzi
	  // pour remettre en état les statuts des manoeuvres.
    if(UAV3iSettings.getMode() == Mode.PAPARAZZI_DIRECT)
    {
      answerManoeuver(mnvr.getId(), true);
    }
	}
	
	public void answerManoeuver(int id, boolean accepted)
	{
		Manoeuver mnvr = findManoeuverById(id);
		
		if (accepted)
			mnvr.setRequestedStatus(ManoeuverRequestedStatus.ACCEPTED);
		else
			mnvr.setRequestedStatus(ManoeuverRequestedStatus.REFUSED);

		// On unlock les share et les jump et le delete
		_areShareAndAskLocked = false;

		for (Manoeuver m : _manoeuvers)
		{
		  m.lockShareAndAsk(false);
		  m.lockDelete(false);
		  m.setAsked(false);
		}
	}

	public boolean areShareAndAskLocked()
	{
		return _areShareAndAskLocked;
	}
	
	public void addManoeuver(Manoeuver mnvr)
	{
		mnvr.setId(++manoeuverSequence);
		synchronized (this)
		{
			_manoeuvers.add(mnvr);
			addTouchSymbol(mnvr);
			_currentMnvr = mnvr;
		}
	}
	
	public void hideManoeuverButtons()
	{
		synchronized(this)
		{
			for (Manoeuver m : _manoeuvers)
				m.hidebuttons();
		}
	}
	
	public void deleteManoeuver(Manoeuver mnvr)
	{
		synchronized (this)
		{
			_manoeuvers.remove(mnvr);
			removeTouchSymbol(mnvr);
		}
	}
	
	public Manoeuver findManoeuverById(int manoeuverId)
	{
	  for(Manoeuver mnvr : _manoeuvers)
	  {
	    if(mnvr.getId() == manoeuverId)
	      return mnvr;
	  }
	  return null;
	}

}
