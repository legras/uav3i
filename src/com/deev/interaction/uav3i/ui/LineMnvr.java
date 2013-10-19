package com.deev.interaction.uav3i.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import uk.me.jstott.jcoord.LatLng;

public class LineMnvr extends Manoeuver
{
  // Points de la zone à regarder
	private LatLng _A, _B;
	// Point de la trajectoire du drone
	private LatLng _trajA, _trajB;
	
	private SymbolMap _smap;
	
	// Distance entre la zone à regarder et la trajectoire
	private double _currentRm = 500.;
	private double _lastRm;
	
	// Codage de l'orientation de la droite dans le plan ?
	private Point2D.Double _u, _v;
	
	static double RPX = 10.;

	/**
	 * @param map
	 * @param xA Point A x-coordinate (screen)
	 * @param yA Point A y-coordinate (screen)
	 * @param xB Point B x-coordinate (screen)
	 * @param yB Point B y-coordinate (screen)
	 */
	public LineMnvr(SymbolMap map, double xA, double yA, double xB, double yB)
	{
		_A = map.getLatLngForScreen(xA, yA);
		_B = map.getLatLngForScreen(xB, yB);
		
    _smap = map;
		
		double d = Math.sqrt((xA-xB)*(xA-xB)+(yA-yB)*(yA-yB));
		_u = new Point2D.Double((xB-xA)/d, (yB-yA)/d);
		_v = new Point2D.Double(-_u.y, _u.x);
		
	}
	
	public LineMnvr(SymbolMap map, LatLng A, LatLng B)
	{
    Point a = map.getScreenForLatLng(A);
		Point b = map.getScreenForLatLng(B);
		
		_A = A;
		_B = B;

		_smap = map;
		
		double d = Math.sqrt((a.x-b.x)*(a.x-b.x)+(a.y-b.y)*(a.y-b.y));
		_u = new Point2D.Double((b.x-a.x)/d, (b.y-a.y)/d);
		_v = new Point2D.Double(-_u.y, _u.x);

		// Mise à jour des deux extrémités de la trajectoire (différents
		// de la zone à regarder).
		setTrajectoryPoints(a, b);
	}
	
	@Override
	public void paint(Graphics2D g2)
	{
		AffineTransform old = g2.getTransform();
		
		Point Apx = _smap.getScreenForLatLng(_A);
		Point Bpx = _smap.getScreenForLatLng(_B);
		
		Area area = new Area();
		BasicStroke stroke = new BasicStroke((float) RPX*2.f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
		
		// Zone à regarder : ligne rouge
		GeneralPath line;
		line = new GeneralPath();
		line.moveTo(Apx.x, Apx.y);
		line.lineTo(Bpx.x, Bpx.y);
		
		area.add(new Area(stroke.createStrokedShape(line)));
		
		g2.setStroke(new BasicStroke(4.f));
		g2.setPaint(new Color(1.0f, 0.f, 0.f, 1.0f));
		g2.draw(area);
		g2.setPaint(new Color(1.0f, 0.1f, 0.1f, 0.2f));
		g2.fill(area);
		
		g2.setStroke(new BasicStroke(4.f));
		g2.setPaint(new Color(0.0f, 0.f, 1.0f, 1.0f));
		
		double Rpx = _smap.getPPM() * _currentRm;
		
		Point2D.Double LApx = new Point2D.Double(Apx.x + _v.x * Rpx,
												 Apx.y + _v.y * Rpx);
		
		Point2D.Double LBpx = new Point2D.Double(Bpx.x + _v.x * Rpx,
												 Bpx.y + _v.y * Rpx);

		// Trajectoire du drone : ligne bleue
		Line2D.Double l = new Line2D.Double(LApx, LBpx);
		
		g2.setStroke(new BasicStroke(2.f*(float)GRIP));
		if (!_adjusting)
			g2.setPaint(new Color(1.0f, 1.0f, 1.0f, 0.3f));
		else
			g2.setPaint(new Color(1.0f, 1.0f, 0.7f, 0.3f));
		g2.draw(l);
		
		g2.setStroke(new BasicStroke(4.f));
		g2.setPaint(new Color(0.0f, 0.f, 1.0f, 1.0f));
		g2.draw(l);
		
		g2.setTransform(old);
	}
	
	@Override
	public boolean adjustAtPx(double x, double y)
	{
	  // x,y : coordonnée du toucher écran
    // On projette tout en screen
	  // Coordonnées écran des deux points (zone rouge)
    Point Apx = _smap.getScreenForLatLng(_A);
    Point Bpx = _smap.getScreenForLatLng(_B);
    
    // Cacul du décallage par rapport à l'un des point (identique pour le second)
    double X = x-Apx.x;
    double Y = y-Apx.y;
    
    // Parallélisme avec la zone à regarder
    double u = X*_u.x + Y*_u.y;
    double v = X*_v.x + Y*_v.y;
    
    double currentRpx = _currentRm * _smap.getPPM();
    double lastRpx = _lastRm * _smap.getPPM();
    
    if (_adjusting)
    {
            currentRpx += v - lastRpx;
            lastRpx = v;
            
            _currentRm = currentRpx / _smap.getPPM();
            _lastRm = lastRpx / _smap.getPPM();

            // Mise à jour des deux extrémités de la trajectoire.
            setTrajectoryPoints(Apx, Bpx);

            return true;
    }
    
    if (isInterestedAtPx(x, y))
    {
            lastRpx = v;
            _adjusting = true;
    }
    
    _currentRm = currentRpx / _smap.getPPM();
    _lastRm = lastRpx / _smap.getPPM();

    // Mise à jour des deux extrémités de la trajectoire.
    setTrajectoryPoints(Apx, Bpx);

		return _adjusting;
	}
	
	public boolean isInterestedAtPx(double x, double y)
	{
		// On projette tout en screen
		Point Apx = _smap.getScreenForLatLng(_A);
		Point Bpx = _smap.getScreenForLatLng(_B);
		
		double X = x-Apx.x;
		double Y = y-Apx.y;
		
		double u = X*_u.x + Y*_u.y;
		double v = X*_v.x + Y*_v.y;
		
		double currentRpx = _currentRm * _smap.getPPM();
		
		return v > currentRpx-GRIP && v < currentRpx+GRIP && u > -GRIP && u < Apx.distance(Bpx)+GRIP;
	}

  /**
   * Calcul des deux points de définition de la trajectoire du drone à partir des
   * deux points de la zone à regarder et de la distance entre ces deux droites.<br/>
   * Note : lors de la modification de la distance, celle-ci est bien évidemment dépendante
   * de l'échelle de la carte.
   * 
   * @param a coordonnée écran du premier point de la zone à regarder.
   * @param b coordonnée écran du second point.
   */
  private void setTrajectoryPoints(Point a, Point b)
  {
    double Rpx = _smap.getPPM() * _currentRm;
    Point2D.Double LApx = new Point2D.Double(a.x + _v.x * Rpx, a.y + _v.y * Rpx);
    Point2D.Double LBpx = new Point2D.Double(b.x + _v.x * Rpx, b.y + _v.y * Rpx);
    _trajA = _smap.getLatLngForScreen(LApx.x, LApx.y);
    _trajB = _smap.getLatLngForScreen(LBpx.x, LBpx.y);
  }
  
  /**
   * Getter pour le point A de la trajectoire du drone (et non de la zone à regarder).
   * 
   * @return coordonnée en {@link LatLng} du point.
   */
  public LatLng getTrajA()
  {
    return _trajA;
  }

  /**
   * Getter pour le point B de la trajectoire du drone (et non de la zone à regarder).
   * 
   * @return coordonnée en {@link LatLng} du point.
   */
  public LatLng getTrajB()
  {
    return _trajB;
  }

}
