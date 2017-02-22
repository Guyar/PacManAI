package gr.PacManAI;

import java.awt.geom.Point2D;

public interface Constants {
    public final static int NEUTRAL = 0;
    public final static int UP = 1;
    public final static int RIGHT = 2;
    public final static int DOWN = 3;
    public final static int LEFT = 4;

    public final Point2D Up = new Point2D.Double(0, -1);
    public final Point2D Right = new Point2D.Double(1, 0);
    public final Point2D Down = new Point2D.Double(0, 1);
    public final Point2D Left = new Point2D.Double(-1, 0);

    

}
