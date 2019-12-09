import java.awt.*;
import java.util.ArrayList;

import static java.awt.Color.white;

public class Rect {
    Point3d v1, v2, v3, v4;
    Color color;
    double distance;

    ArrayList<Point3d> pointsList;


    public Rect(Point3d v1, Point3d v2, Point3d v3, Point3d v4) {
        this(v1, v2, v3, v4, Color.WHITE);
    }

    public Rect(Point3d v1, Point3d v2, Point3d v3, Point3d v4, Color color) {
        pointsList = new ArrayList<>();
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.v4 = v4;
        pointsList.add(this.v1);
        pointsList.add(this.v2);
        pointsList.add(this.v3);
        pointsList.add(this.v4);
        this.color = color;
        this.distance = Point3d.distance(Point3d.middle(v1,v3), new Point3d(0,0,0));
    }

    public boolean isVisible(double z)
    {
        return v1.z > z && v2.z>z && v3.z>z && v4.z>z;
    }

    public ArrayList<Point3d> getPointsList() {
        return pointsList;
    }

//    public Point3d norm()
//    {
//
//    }
}

