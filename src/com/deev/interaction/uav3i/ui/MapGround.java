package com.deev.interaction.uav3i.ui;
/**
 * 
 */


import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;


/**
 * @author legras
 *
 */
@SuppressWarnings("serial")
public class MapGround extends Map
{
	BufferedImage _image;
	AffineTransform _transform;
	AffineTransform _offset;

	private Point2D.Double _center; // in meter (georef)
	private double _ppm; // pixels per meter
	
	private double _cXmin, _cXmax, _cYmin, _cYmax; // Image bounds
	
	/**
	 * MapGround provides a basic implementation of a map via a raster image. The scale is
	 * assumed at 1 pixel == 1 meter. The image is positioned in the positive quarter of the
	 * Euclidean metric plane (upper left). The map is zoommable and pannable and limits 
	 * movements so that the component is always filled with image data (no exploring beyond 
	 * image limits).
	 * 
	 * @param image
	 */
	public MapGround(BufferedImage image)
	{
    System.out.println("MapGround -> constructeur");
		_image = image;
		_center = new Point2D.Double(0., 0.);
	
		_offset = new AffineTransform();
		_offset.translate(0., -_image.getHeight());
		
		setPPM(1.);
    System.out.println("MapGround -> construit");
	}
	
	public void updateTransform()
	{		
    System.out.println("MapGround -> updateTransform");
		_transform = new AffineTransform();
		_transform.setToScale(_ppm, _ppm);
		_transform.translate(-_center.x+getWidth()/_ppm/2., _center.y+getHeight()/_ppm/2.);
	}
	
	public boolean checkBounds()
	{
    System.out.println("MapGround -> checkBounds");
		boolean modified = false;
		
		if (_center.x < _cXmin)
		{
			_center.x = _cXmin;
			modified = true;
		}
		
		if (_center.y < _cYmin)
		{
			_center.y = _cYmin;
			modified = true;
		}
		
		if (_center.x > _cXmax)
		{
			_center.x = _cXmax;
			modified = true;
		}
		
		if (_center.y > _cYmax)
		{
			_center.y = _cYmax;
			modified = true;
		}	
		
		return modified;
	}
	
	public void paintComponent(Graphics g)
	{
	  //System.out.println("MapGround -> paintComponent");
		Graphics2D g2 = (Graphics2D) g;
		
		g2.transform(_transform);
		g2.drawImage(_image, _offset, null);
	}
	
	
	/* (non-Javadoc)
	 * @see com.deev.interaction.susie.Map#moveP(double, double)
	 */
	@Override
	public void panPx(double dx, double dy)
	{
    System.out.println("MapGround -> panPx");
		double x = _center.x;
		double y = _center.y;
				
		_center = new Point2D.Double(x + dx/_ppm, y - dy/_ppm);
	}

	/* (non-Javadoc)
	 * @see com.deev.interaction.susie.Map#zoomP(double, double, double)
	 */
	@Override
	public void zoomPx(double zfactor, double x, double y)
	{
    System.out.println("MapGround -> zoomPx");
		// if (zfactor < 1. && _ppm > 10.)
		// 	return;
		
		double dx = x - getWidth()/2.;
		double dy = y - getHeight()/2.;
		
		panPx(dx, dy);
		setPPM(_ppm*zfactor);
		panPx(-dx, -dy);
	}
	
	public Point2D.Double getCenter()
	{
    System.out.println("MapGround -> getCenter");
		return _center;
	}
	
	public void setCenter(Point2D.Double center)
	{
    System.out.println("MapGround -> setCenter");
		_center = center;
	}
	
	/**
	 * @return the scale of the map in pixels per meter.
	 */
	public double getPPM()
	{
    System.out.println("MapGround -> getPPM");
		return _ppm;
	}
	
	public void setPPM(double ppm)
	{
    System.out.println("MapGround -> setPPM");
		_ppm = ppm;
		
		_cXmin = getWidth()/2./_ppm;
		_cXmax = _image.getWidth() - _cXmin;
		_cYmin = getHeight()/2./_ppm;
		_cYmax = _image.getHeight() - _cYmin;
	}

	@Override
	public void alignWith(Map map)
	{
    System.out.println("MapGround -> alignWith");
		Point2D.Double center = map.getCenter();
		
		_center.x = center.x;
		_center.y = center.y;
		setPPM(map.getPPM());
		
		updateTransform();
	}

}
