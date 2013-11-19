package com.deev.interaction.touch;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.PathIterator;

public class FingerStroke implements Stroke
{
	private float _thickness;
	private static final float FLATNESS = 1;

	public FingerStroke(float thickness)
	{
		_thickness = thickness;
	}

	@Override
	public Shape createStrokedShape( Shape shape )
	{
		Area result = new Area();
		PathIterator it = new FlatteningPathIterator( shape.getPathIterator( null ), FLATNESS );
		float points[] = new float[6];
		float moveX = 0, moveY = 0;
		float lastX = 0, lastY = 0;
		float thisX = 0, thisY = 0;
		int type = 0;
		float radius;
		float lastRadius = 0;
		float t = 0;
		float length = measurePathLength(shape);
		
		while ( !it.isDone() )
		{
			type = it.currentSegment( points );
			switch( type )
			{
				case PathIterator.SEG_MOVETO:
					moveX = lastX = points[0];
					moveY = lastY = points[1];
					lastRadius = 0;
					t = 0;
					break;
	
				case PathIterator.SEG_CLOSE:
					points[0] = moveX;
					points[1] = moveY;
					// Fall into....
	
				case PathIterator.SEG_LINETO:
					thisX = points[0];
					thisY = points[1];
					float dx = thisX-lastX;
					float dy = thisY-lastY;
					float d = (float)Math.sqrt( dx*dx + dy*dy );
					
					dx /= d;
					dy /= d;
					t += d;
					
					radius = _thickness * (t/length / 2);
						
					Ellipse2D.Float circle = new Ellipse2D.Float(	thisX - radius,
																	thisY - radius,
																	radius*2, radius*2);
					
					result.add(new Area(circle));
					
					int[] polyX = {(int) (lastX-dy*lastRadius), (int) (lastX+dy*lastRadius), (int) (thisX+dy*radius), (int) (thisX-dy*radius)};
					int[] polyY = {(int) (lastY+dx*lastRadius), (int) (lastY-dx*lastRadius), (int) (thisY-dx*radius), (int) (thisY+dx*radius)};
					Polygon poly = new Polygon(polyX, polyY, 4);
					result.add(new Area(poly));
					
					lastX = thisX;
					lastY = thisY;
					lastRadius = radius;
					break;
			}
			it.next();
		}

		return result;
	}

	public float measurePathLength( Shape shape ) {
		PathIterator it = new FlatteningPathIterator( shape.getPathIterator( null ), FLATNESS );
		float points[] = new float[6];
		float moveX = 0, moveY = 0;
		float lastX = 0, lastY = 0;
		float thisX = 0, thisY = 0;
		int type = 0;
        float total = 0;

		while ( !it.isDone() ) {
			type = it.currentSegment( points );
			switch( type ){
			case PathIterator.SEG_MOVETO:
				moveX = lastX = points[0];
				moveY = lastY = points[1];
				break;

			case PathIterator.SEG_CLOSE:
				points[0] = moveX;
				points[1] = moveY;
				// Fall into....

			case PathIterator.SEG_LINETO:
				thisX = points[0];
				thisY = points[1];
				float dx = thisX-lastX;
				float dy = thisY-lastY;
				total += (float)Math.sqrt( dx*dx + dy*dy );
				lastX = thisX;
				lastY = thisY;
				break;
			}
			it.next();
		}

		return total;
	}

}