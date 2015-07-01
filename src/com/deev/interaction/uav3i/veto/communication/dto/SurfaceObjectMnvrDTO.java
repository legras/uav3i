package com.deev.interaction.uav3i.veto.communication.dto;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.deev.interaction.uav3i.ui.Manoeuver.ManoeuverRequestedStatus;
import com.deev.interaction.uav3i.veto.ui.Veto;

import uk.me.jstott.jcoord.LatLng;

public class SurfaceObjectMnvrDTO extends ManoeuverDTO
{
	private LatLng _center;
	private Point2D.Double _oldCenterPx = new Point2D.Double(-1,-1);
	private double _angle = 0.1;
	private Point2D.Double _lookAt;
	static double LOOK_RADIUS = 32.;
	
  private static BufferedImage _imgObjectBig= null;
  static
  {
    try
    {
      if (_imgObjectBig == null)
      {
        _imgObjectBig = ImageIO.read(SurfaceObjectMnvrDTO.class.getResource("/img/surfo obj big.png"));
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

	
	public SurfaceObjectMnvrDTO(int id, LatLng center, double angle, double lookX, double lookY)
	{
		this.id = id;
		_center = center;
		_angle = angle;
		_lookAt = new Point2D.Double(lookX, lookY);
		
//		try
//		{
//			if (_imgObjectBig == null)
//			{
//				_imgObjectBig = ImageIO.read(this.getClass().getResource("/img/surfo obj big.png"));
//			}
//		}
//		catch (IOException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	
	@Override
	public void paint(Graphics2D g2)
	{
		AffineTransform old = g2.getTransform();

		Point2D.Double centerPx = Veto.getSymbolMapVeto().getScreenForLatLng(_center);
		g2.translate(centerPx.x, centerPx.y);
		
		g2.rotate(_angle);
		g2.drawImage(_imgObjectBig, (int) (-_imgObjectBig.getWidth()/2.), (int) (-_imgObjectBig.getHeight()/2.), null);
		g2.rotate(-_angle);
		g2.translate(128., 192.);
		drawData(g2);

		g2.setTransform(old);
		
		Point2D.Double c = lookPxFromRelative(_lookAt);
		Ellipse2D.Double ell = new Ellipse2D.Double(c.x-LOOK_RADIUS, c.y-LOOK_RADIUS, 2*LOOK_RADIUS, 2*LOOK_RADIUS);
		paintFootprint(g2, ell);
	}

	@Override
	public LatLng getCenter()
	{
		return _center;
	}

	@Override
	public void positionButtons()
	{
		Point2D.Double centerPx = Veto.getSymbolMapVeto().getScreenForLatLng(_center);
	    
	    if (buttons != null)
	    {
	    	// Est-ce que ça vaut le coup de recalculer la position des boutons ?
	    	if(!centerPx.equals(_oldCenterPx))
	    	{
	    		_oldCenterPx = centerPx;
	    		buttons.setPositions(centerPx, 292, Math.PI/2, true);
	    	}
	    }
	}

	public Point2D.Double lookPxFromRelative(Point2D.Double pr)
	{
		Point2D.Double centerPx = Veto.getSymbolMapVeto().getScreenForLatLng(_center);
		
		Point2D.Double px = new Point2D.Double();
		
		px.x = centerPx.x + 256 * (Math.cos(_angle)*pr.x - Math.sin(_angle)*pr.y);
		px.y = centerPx.y + 256 * (Math.sin(_angle)*pr.x + Math.cos(_angle)*pr.y);
		
		return px;
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
	
}
