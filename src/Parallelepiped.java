import java.awt.*;
import java.util.ArrayList;

public class Parallelepiped {

    Point3d a;
    Point3d g;
    Color color;
    ArrayList<Point3d> pointsList;
    ArrayList<Rect> rectsList;

    public Parallelepiped(Point3d a, Point3d g) {
        this(a, g, Color.WHITE);
    }

    public Parallelepiped(Point3d a, Point3d g, Color color) {
        this.a = a;
        this.g = g;
        this.color = color;
        this.pointsList = new ArrayList<>();
        rectsList = new ArrayList<>();
        this.otherPointsCounter(a,g);

    }

    public ArrayList<Point3d> getPointsList() {
        return pointsList;
    }

    public ArrayList<Rect> getRectsList() {
        return rectsList;
    }

    public void otherPointsCounter(Point3d a, Point3d g){

        Point3d b = new Point3d(g.x, a.y, a.z);
        Point3d c = new Point3d(g.x, a.y, g.z);
        Point3d d = new Point3d(a.x, a.y, g.z);
        Point3d e = new Point3d(a.x, g.y, a.z);
        Point3d f = new Point3d(g.x, g.y, a.z);
        Point3d h = new Point3d(a.x, g.y, g.z);

        pointsList.add(this.a);
        pointsList.add(b);
        pointsList.add(c);
        pointsList.add(d);
        pointsList.add(e);
        pointsList.add(f);
        pointsList.add(this.g);
        pointsList.add(h);


        rectsList.add(new Rect(a, b, c, d, this.color));
        rectsList.add(new Rect(a, b, f, e, this.color));
        rectsList.add(new Rect(b, c, g, f, this.color));
        rectsList.add(new Rect(a, d, h, e, this.color));
        rectsList.add(new Rect(c, d, h, g, this.color));
        rectsList.add(new Rect(e, h, g, f, this.color));

    }


}
