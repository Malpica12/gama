/*******************************************************************************************************
 *
 * HatchLineSegment.java, in gama.ext.libs, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.ext.libs.kabeja.dxf.helpers;


/**
 * The Class HatchLineSegment.
 *
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 */
public class HatchLineSegment {
    
    /** The start point. */
    protected Point startPoint;
    
    /** The direction. */
    protected Vector direction;
    
    /** The angle. */
    protected double angle;
    
    /** The total length. */
    protected double totalLength;
    
    /** The length. */
    protected double length;
    
    /** The current length. */
    protected double currentLength;
    
    /** The pattern. */
    protected double[] pattern;
    
    /** The l. */
    protected double l;
    
    /** The index. */
    protected int index;
    
    /** The line. */
    protected ParametricLine line;

    /**
     * Instantiates a new hatch line segment.
     *
     * @param startPoint the start point
     * @param angle the angle in degrees
     * @param length the length
     */
    public HatchLineSegment(Point startPoint, double angle, double length) {
        this.startPoint = startPoint;
        this.angle = Math.toRadians(angle);
        this.totalLength = length;
    }

    /**
     * Instantiates a new hatch line segment.
     *
     * @param startPoint the start point
     * @param direction the direction
     * @param length the length
     */
    public HatchLineSegment(Point startPoint, Vector direction, double length) {
        this.startPoint = startPoint;
        this.direction = direction;
        this.totalLength = length;
    }

    /**
     * Instantiates a new hatch line segment.
     *
     * @param line the line
     * @param length the length
     * @param startLength the start length
     * @param pattern the pattern
     */
    public HatchLineSegment(ParametricLine line, double length,
        double startLength, double[] pattern) {
        this.startPoint = line.getStartPoint();
        this.angle = Math.toRadians(angle);
        this.totalLength = length;
        this.currentLength = startLength;
        this.pattern = pattern;
        this.line = line;
        this.initialize(startLength);
    }

    /**
     * Gets the start point.
     *
     * @return the start point
     */
    public Point getStartPoint() {
        return this.startPoint;
    }

    /**
     * Gets the length.
     *
     * @return the length
     */
    public double getLength() {
        return this.totalLength;
    }

    /**
     * Gets the point.
     *
     * @param offset the offset
     * @return the point
     */
    public Point getPoint(double offset) {
        Point p = new Point();
        p.setX(this.startPoint.getX() +
            (Math.cos(this.angle) * this.totalLength));
        p.setY(this.startPoint.getY() +
            (Math.sin(this.angle) * this.totalLength));

        return p;
    }

    /**
     * Gets the point at.
     *
     * @param para the para
     * @return the point at
     */
    public Point getPointAt(double para) {
        return line.getPointAt(para);
    }

    /**
     * Checks for next.
     *
     * @return true, if successful
     */
    public boolean hasNext() {
        return this.length <= totalLength;
    }

    /**
     * Next.
     *
     * @return the double
     */
    public double next() {
        double l = this.currentLength;
        this.length += Math.abs(this.currentLength);

        if (index == pattern.length) {
            index = 0;
        }

        this.currentLength = pattern[index];
        index++;

        return l;
    }

    /**
     * Initialize.
     *
     * @param startLength the start length
     */
    protected void initialize(double startLength) {
        double l = 0;

        for (int i = 0; i < pattern.length; i++) {
            l += Math.abs(pattern[i]);

            // System.out.println("test Pattern part:"+pattern[i]+" startLength="+startLength+" currentLength:"+l);
            if (l > startLength) {
                this.currentLength = l - startLength;

                if (pattern[i] < 0) {
                    //System.out.println("is empty");
                    this.currentLength *= (-1);
                }

                //System.out.println("pattern startet bei="+i+" mit length="+this.currentLength);
                this.index = i + 1;

                return;
            }
        }
    }

    /**
     * Checks if is solid.
     *
     * @return true, if is solid
     */
    public boolean isSolid() {
        return pattern.length == 0;
    }
}