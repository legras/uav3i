package com.deev.interaction.uav3i.ui;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import uk.me.jstott.jcoord.LatLng;

import com.deev.interaction.uav3i.ui.Manoeuver.ManoeuverRequestedStatus;
import com.deev.interaction.uav3i.util.log.LoggerUtil;
import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTO;

public class SurfaceObjectMnvr extends Manoeuver
{
	private LatLng _center;
	private double _angle = 0.1;
	private Point2D.Double _lookAt;
	private Point2D.Double _lastLookAt;
	static double LOOK_RADIUS = 32.;
	
	private enum SurfaceObjectMnvrStates {NONE, TRANSLATE, ROTATE};
	private SurfaceObjectMnvrStates _moveState = SurfaceObjectMnvrStates.NONE;
	private boolean _isMoving = false;
	private Point2D.Double _offCenter;
	private double _lastTouchAngle;

	private static BufferedImage _imgObjectBig= null;
	private static BufferedImage _imgDataBig= null;
	private static BufferedImage _imgObjectSmall= null;
	private static BufferedImage _imgDataSmall= null;
	
	public SurfaceObjectMnvr(SymbolMap map, LatLng c)
	{
		_center = c;
		_smap = map;
		_lookAt = new Point2D.Double(0.5, 0);
			
		try
		{
			if (_imgObjectBig == null)
			{
				_imgObjectBig = ImageIO.read(this.getClass().getResource("/img/surfo obj big.png"));
				_imgDataBig = ImageIO.read(this.getClass().getResource("/img/surfo data big.png"));
				_imgObjectSmall = ImageIO.read(this.getClass().getResource("/img/surfo obj small.png"));
				_imgDataSmall = ImageIO.read(this.getClass().getResource("/img/surfo data small.png"));
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		// ********** ManoeuverButtons **********
		try
		{
			_buttons = new ManoeuverButtons(this, MainFrame.clayer);
		}
		catch (IOException e1)
		{
			LoggerUtil.LOG.log(Level.WARNING, "could not create buttons for manoeuver");
			_buttons = null;
		}

		positionButtons();
	}
	
	public Point2D.Double lookRelativeFromPx(Point2D.Double px)
	{
		Point2D.Double centerPx = _smap.getScreenForLatLng(_center);
		
		Point2D.Double pr = px;

		pr.x -= centerPx.x;
		pr.y -= centerPx.y;
		
		pr.x /= 256.;
		pr.y /= 256.;
		
		return new Point2D.Double(Math.cos(-_angle)*pr.x - Math.sin(-_angle)*pr.y,
								  Math.sin(-_angle)*pr.x + Math.cos(-_angle)*pr.y);
	}
	
	public Point2D.Double lookPxFromRelative(Point2D.Double pr)
	{
		Point2D.Double centerPx = _smap.getScreenForLatLng(_center);
		
		Point2D.Double px = new Point2D.Double();
		
		px.x = centerPx.x + 256 * (Math.cos(_angle)*pr.x - Math.sin(_angle)*pr.y);
		px.y = centerPx.y + 256 * (Math.sin(_angle)*pr.x + Math.cos(_angle)*pr.y);
		
		return px;
	}
	
	public double getRadius()
	{
		return isBigOnScreen() ? 256. : 64.;
	}
	
	public boolean isBigOnScreen()
	{
		return _smap.getPPM() > 1.;
	}
	
	@Override
	public float getInterestForPoint(float x, float y)
	{
		if (_moveState != SurfaceObjectMnvrStates.NONE)
			return -1.f;
				
		Point2D.Double centerPx = _smap.getScreenForLatLng(_center);
		
		if (centerPx.distance(x, y) < getRadius())
			return getMoveInterest();

		Rectangle2D.Double box;
		
		if (isBigOnScreen())
			box = new Rectangle2D.Double(centerPx.x, centerPx.y-64., 384., 128.);
		else
			box = new Rectangle2D.Double(centerPx.x, centerPx.y-64., 256., 128.);
		
		if (box.contains(x, y))
			return getMoveInterest();
		else
			return -1.f;
	}



	@Override
	public void addTouch(float x, float y, Object touchref)
	{
		if (_isMoving)
			return;

		super.addTouch(x, y, touchref);
		
		if (!isModifiable())
		{
			cancelTouch(touchref);
			return;
		}
		
		if (!isModifiable())
		{
			cancelTouch(touchref);
			return;
		}
		
		_isMoving = true;
		
		Point2D.Double centerPx = _smap.getScreenForLatLng(_center);
		
		if (centerPx.distance(x, y) > getRadius()-80. && centerPx.distance(x, y) < getRadius())
		{
			_moveState = SurfaceObjectMnvrStates.ROTATE;
			_lastTouchAngle = Math.atan2(y-centerPx.y, x-centerPx.x);
		}
		else
		{
			_moveState = SurfaceObjectMnvrStates.TRANSLATE;
			_offCenter = new Point2D.Double(x-centerPx.x, y-centerPx.y);
		}
	}

	@Override
	public void updateTouch(float x, float y, Object touchref)
	{
		super.updateTouch(x, y, touchref);
		
		switch (_moveState)
		{
			case ROTATE:
				Point2D.Double centerPx = _smap.getScreenForLatLng(_center);
				double touchAngle = Math.atan2(y-centerPx.y, x-centerPx.x);
				
				while (touchAngle < 0.) touchAngle += 2*Math.PI;
				while (touchAngle > 2*Math.PI) touchAngle -= 2*Math.PI;
				
				_angle += touchAngle - _lastTouchAngle;
				
				while (_angle < 0.) _angle += 2*Math.PI;
				while (_angle > 2*Math.PI) _angle -= 2*Math.PI;
				
				_lastTouchAngle = touchAngle;
				break;
		
			case TRANSLATE:
				Point2D.Double newCenterPx = new Point2D.Double(x-_offCenter.x, y-_offCenter.y);
				_center = _smap.getLatLngForScreen(newCenterPx.x, newCenterPx.y);
				break;
				
			default:
				cancelTouch(touchref);
				return;	
		}
		
		positionButtons();
	}
	
	
	@Override
	public void removeTouch(float x, float y, Object touchref)
	{
		_isMoving = false;
		_offCenter = null;
		_moveState = SurfaceObjectMnvrStates.NONE;
		
		positionButtons();
	}

	@Override
	public void cancelTouch(Object touchref)
	{
		_isMoving = false;
		_offCenter = null;
		_moveState = SurfaceObjectMnvrStates.NONE;
		
		positionButtons();
	}

	@Override
	public void paint(Graphics2D g2)
	{
		AffineTransform old = g2.getTransform();

		Point2D.Double centerPx = _smap.getScreenForLatLng(_center);
		g2.translate(centerPx.x, centerPx.y);
		
		if (isBigOnScreen())
		{
			g2.drawImage(_imgDataBig, 384-_imgDataBig.getWidth(), (int) (-_imgDataBig.getHeight()/2.), null);
			g2.rotate(_angle);
			g2.drawImage(_imgObjectBig, (int) (-_imgObjectBig.getWidth()/2.), (int) (-_imgObjectBig.getHeight()/2.), null);
			g2.rotate(-_angle);
			g2.translate(_imgObjectBig.getWidth()/2., 0);
			drawData(g2);
		}
		else
		{
			g2.drawImage(_imgDataSmall, 192-_imgDataSmall.getWidth(), (int) (-_imgDataSmall.getHeight()/2.), null);
			g2.rotate(_angle);
			g2.drawImage(_imgObjectSmall, (int) (-_imgObjectSmall.getWidth()/2.), (int) (-_imgObjectSmall.getHeight()/2.), null);
			g2.rotate(-_angle);
			g2.translate(_imgObjectSmall.getWidth()/2., 0);
			drawData(g2);
		}

		g2.setTransform(old);
		
		if (isBigOnScreen())
		{
			Point2D.Double c = lookPxFromRelative(_lookAt);
			Ellipse2D.Double ell = new Ellipse2D.Double(c.x-LOOK_RADIUS, c.y-LOOK_RADIUS, 2*LOOK_RADIUS, 2*LOOK_RADIUS);
			paintFootprint(g2, ell, getRequestedStatus() != ManoeuverRequestedStatus.NONE);
		}
	}

	public void drawData(Graphics2D g2)
	{
		String LAT, LON, COURSE;
		
		if (_center.getLat() < 0.)
		{
			LAT = String.format("S%.4f", -_center.getLat());
		}
		else
		{
			LAT = String.format("N%.4f", _center.getLat());
		}
		
		if (_center.getLng() < 0.)
		{
			LON = String.format("W%.4f", -_center.getLng());
		}
		else
		{
			LON = String.format("E%.4f", _center.getLng());
		}
		
		while (_angle > 2*Math.PI)
		{
			_angle -= 2*Math.PI;
		}
		
		int course = (int) Math.floor(.5 + 16.*_angle/(2.*Math.PI));
		final String courses[] = {	"N", "NNW", "NW", "WNW",
									"W", "WSW", "SW", "SSW",
									"S", "SSE", "SE", "ESE",
									"E", "ENE", "NE", "NNE"};
		
		COURSE = courses[(16-course)%16];
		
		// Label
		FontRenderContext frc = g2.getFontRenderContext();
		Font f = new Font("Futura", Font.PLAIN, 24);
		TextLayout textTl;
		Shape outline;

		double lineH = 32.;
		
		AffineTransform old = g2.getTransform();
		
		g2.translate(-2., -1.5*lineH+24);
		
		textTl = new TextLayout(LAT, f, frc);
		outline = textTl.getOutline(null);
		//Rectangle2D b = outline.getBounds2D();
		
		g2.setPaint(_M_WHITE);
		g2.setStroke(new BasicStroke(5.f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
		g2.draw(outline);

		g2.setPaint(_M_GREY);
		g2.fill(outline);
		
		g2.translate(0., lineH);
		
		textTl = new TextLayout(LON, f, frc);
		outline = textTl.getOutline(null);
		//Rectangle2D b = outline.getBounds2D();
		
		g2.setPaint(_M_WHITE);
		g2.setStroke(new BasicStroke(5.f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
		g2.draw(outline);

		g2.setPaint(_M_GREY);
		g2.fill(outline);
		
		g2.translate(0., lineH);
		
		textTl = new TextLayout("⎈ "+COURSE, f, frc);
		outline = textTl.getOutline(null);
		//Rectangle2D b = outline.getBounds2D();
		
		g2.setPaint(_M_WHITE);
		g2.setStroke(new BasicStroke(5.f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
		g2.draw(outline);

		g2.setPaint(_M_GREY);
		g2.fill(outline);
		
		g2.setTransform(old);
	}
	
	
	@Override
	public ManoeuverDTO toDTO()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void positionButtons()
	{
		if (_buttons == null)
			return;
		
		Point2D.Double centerPx = _smap.getScreenForLatLng(_center);
		
		if (isBigOnScreen())		
			_buttons.setPositions(centerPx, 292, Math.PI, true);
		else
			_buttons.setPositions(centerPx, 104, Math.PI, true);
	}

	@Override
	public boolean adjustAtPx(double x, double y)
	{
		Point2D.Double pr = lookRelativeFromPx(new Point2D.Double(x, y));
		
		_buttons.show();
		setRequestedStatus(ManoeuverRequestedStatus.NONE);

		if (_adjusting)
		{
			_lookAt.x += pr.x - _lastLookAt.x;
			_lookAt.y += pr.y - _lastLookAt.y;
			
			_lastLookAt = pr;

			// TODO: contraindre au cercle ?

			return true;
		}

		if (isAdjustmentInterestedAtPx(x, y))
		{
			_lastLookAt = pr;
			_adjusting = true;
		}

		return _adjusting;
	}

	@Override
	public boolean isAdjustmentInterestedAtPx(double x, double y)
	{
		Point2D.Double L = lookPxFromRelative(_lookAt);
		
		return L.distance(x, y) < LOOK_RADIUS;
	}

}
