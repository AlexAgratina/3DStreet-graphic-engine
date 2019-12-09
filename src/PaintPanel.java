import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Collections;


public class PaintPanel extends JPanel implements KeyListener, MouseMotionListener {

    int     xPosition,
            yPosition;

    double  distance;

    Point3d start_point,
            light_point,
            up,
            down;

    boolean needFill = true,
            mouseControl = false;


    ArrayList<Rect> rects = new ArrayList<Rect>();
    ArrayList<Parallelepiped> houses = new ArrayList<Parallelepiped>();
    ArrayList<Point3d> road = new ArrayList<>();


    private void turn(char axis, double direction)
    {
        double angle = direction * 10 * Math.PI / 180;
        double [][] matrix = {{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}};
        if (axis == 'x')
        {
            matrix[1][1] = Math.cos(angle);
            matrix[1][2] = -Math.sin(angle);
            matrix[2][1] = Math.sin(angle);
            matrix[2][2] = Math.cos(angle);
        }
        else if (axis == 'y')
        {
            matrix[0][0] = Math.cos(angle);
            matrix[0][2] = Math.sin(angle);
            matrix[2][0] = -Math.sin(angle);
            matrix[2][2] = Math.cos(angle);
        }
        else
        {
            matrix[0][0] = Math.cos(angle);
            matrix[0][1] = -Math.sin(angle);
            matrix[1][0] = Math.sin(angle);
            matrix[1][1] = Math.cos(angle);
        }

        houses.forEach(house->house.pointsList.forEach(point3d -> point3d.transform(matrix)));
        road.forEach(point3d -> point3d.transform(matrix));
        up.transform(matrix);
        down.transform(matrix);

    }

    private void move(char axis, int direction)
    {
        double [][] matrix = {{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}};
        if (axis == 'x')
            matrix[0][3] = direction;
        else if(axis == 'y')
            matrix[1][3] = direction;
        else
            matrix[2][3] = direction;

        houses.forEach(house->house.pointsList.forEach(point3d -> point3d.transform(matrix)));
        road.forEach(point3d -> point3d.transform(matrix));
        up.transform(matrix);
        down.transform(matrix);

    }

    public PaintPanel()
    {
        super();
        this.addKeyListener(this);
        this.addMouseMotionListener(this);
        init();
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Initialization
        int     widht = getWidth(),
                height = getHeight();

        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(Color.BLACK);
        g2.fillRect(0,0,widht,height);
        //g2.translate(widht/2,height/2);
        if (mouseControl)
            mControl();
        double upDistance = Point3d.distance(up,start_point);
        double downDistance = Point3d.distance(down,start_point);

        if(upDistance <= downDistance+10)
            drawRoad(g2);

        // RECTANGLES DRAWING ======================================================
        this.rects.clear();
        if (needFill)
        {
            for (Parallelepiped house : houses) {
                for (Rect rect : house.getRectsList()) {
                    if (rect.isVisible(start_point.z))
                        rects.addAll(cutter(rect, 5));
//                    if (rect.isVisible(start_point.z)) {
//                        Point3d m12 = Point3d.middle(rect.v1, rect.v2),
//                                m23 = Point3d.middle(rect.v2, rect.v3),
//                                m34 = Point3d.middle(rect.v3, rect.v4),
//                                m14 = Point3d.middle(rect.v1, rect.v4);
//                        Point3d mm = Point3d.middle(m12, m34);
//                        this.rects.add(new Rect(rect.v1, m12, mm, m14, rect.color));
//                        this.rects.add(new Rect(m12, rect.v2, m23, mm, rect.color));
//                        this.rects.add(new Rect(m23, rect.v3, m34, mm, rect.color));
//                        this.rects.add(new Rect(mm, m34, rect.v4, m14, rect.color));
//                    }
                }
            }
        }
        else{
            for (Parallelepiped house : houses) {
                for (Rect rect : house.getRectsList()) {
                    if (rect.isVisible(start_point.z)) {
                        this.rects.add(rect);
                    }
                }
            }
        }

        Collections.sort(rects, (lr, rr) -> {
            double  lDistance = 0,
                    rDistance = 0;
            for (Point3d point : lr.getPointsList()) {
                lDistance += Point3d.distance(point, start_point);
            }
            for (Point3d point : rr.getPointsList()) {
                rDistance += Point3d.distance(point, start_point);
            }
            lDistance /= 4;
            rDistance /= 4;
            // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
            return lDistance > rDistance ? -1 : (lDistance < rDistance) ? 1 : 0;
        });

//
        for (Rect rect: rects) {
            Point3d v1 = rect.v1.project(distance, widht, height);
            Point3d v2 = rect.v2.project(distance, widht, height);
            Point3d v3 = rect.v3.project(distance, widht, height);
            Point3d v4 = rect.v4.project(distance, widht, height);

            Path2D path = new Path2D.Double();

            path.moveTo(v1.x, v1.y);
            path.lineTo(v2.x, v2.y);
            path.lineTo(v3.x, v3.y);
            path.lineTo(v4.x, v4.y);
            path.closePath();

            g2.setColor(rect.color);
            if (needFill)
            {
                g2.fill(path);
                Path2D outlinePath = new Path2D.Double(path);
                g2.setColor(Color.BLACK);
                g2.draw(outlinePath);
                g2.setColor(Color.BLACK);

            }
            else
            {
                g2.draw(path);
            }
        }

        if(upDistance > downDistance+10)
            drawRoad(g2);

        g2.setColor(Color.WHITE);
        g2.drawString("x="+xPosition+" y="+yPosition, 5, 20);
        g2.drawString("Focus distance: "+ distance, 5, 35);
        g2.drawString("Up: "+ upDistance, 5, 45);
        g2.drawString("Down: "+ downDistance, 5, 55);

        updateUI();
    }

    private void mControl(){
        int xControl = 10* xPosition/getWidth();
        int yControl = 10* yPosition/getHeight();

        switch (xControl) {
            case 3:
                turn('y', 0.001);
                break;
            case 2:
                turn('y', 0.002);
                break;
            case 1:
                turn('y', 0.003);
                break;
            case 0:
                turn('y', 0.005);
                break;
            case 6:
                turn('y', -0.001);
                break;
            case 7:
                turn('y', -0.002);
                break;
            case 8:
                turn('y', -0.003);
                break;
            case 9:
                turn('y', -0.005);
                break;
            default:
                break;
        }

        switch (yControl) {
            case 3:
                turn('x', -0.001);
                break;
            case 2:
                turn('x', -0.002);
                break;
            case 1:
                turn('x', -0.003);
                break;
            case 0:
                turn('x', -0.005);
                break;
            case 6:
                turn('x', 0.001);
                break;
            case 7:
                turn('x', 0.002);
                break;
            case 8:
                turn('x', 0.003);
                break;
            case 9:
                turn('x', 0.005);
                break;
            default:
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        Character key = e.getKeyChar();
        if (key == '-')
        {
            if(distance > 10)
                distance -= 10;
        }
        if (key == '+')
        {
            if(distance < 5000)
                distance += 10;
        }
        if (key == 'd')
          move('x', -1);
        if (key == 'a')
          move('x', 1);
        if (key == 'w')
            move('z', -1);
        if (key == 's')
            move('z', 1);
        if (key == 'r')
            move('y', 1);
        if (key == 'f')
            move('y', -1);
        if (key == '2')
            turn('x', 1);
        if (key == '8')
            turn('x', -1);
        if (key == '4')
            turn('y', 1);
        if (key == '6')
            turn('y', -1);
        if (key == 'e')
            turn('z', -0.5);
        if (key == 'q')
            turn('z', 0.5);
        if (key == ' ')
            needFill = !needFill;
        if (key == 'm')
            mouseControl = !mouseControl;
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            houses.clear();
            road.clear();
            init();
        }
        this.updateUI();
    }

        @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }
    @Override
    public void mouseMoved(MouseEvent e) {
        yPosition = e.getY();
        xPosition = e.getX();
    }

    private ArrayList<Rect> cutter(Rect rect, int size)
    {
        ArrayList<Rect> temp = new ArrayList<>();

        if (Point3d.distance(rect.v1, rect.v2) < size && Point3d.distance(rect.v3, rect.v2) < size)
        {
            temp.add(rect);
            return temp;
        }
            Point3d m12 = Point3d.middle(rect.v1, rect.v2),
                    m23 = Point3d.middle(rect.v2, rect.v3),
                    m34 = Point3d.middle(rect.v3, rect.v4),
                    m14 = Point3d.middle(rect.v1, rect.v4);
            Point3d mm = Point3d.middle(m12, m34);
            temp.addAll(cutter(new Rect(rect.v1, m12, mm, m14, rect.color), size));
            temp.addAll(cutter(new Rect(m12, rect.v2, m23, mm, rect.color), size));
            temp.addAll(cutter(new Rect(m23, rect.v3, m34, mm, rect.color), size));
            temp.addAll(cutter(new Rect(mm, m34, rect.v4, m14, rect.color), size));
            return temp;
    }

    // ROAD DRAWING =============================================================
    private void drawRoad(Graphics2D g2){
        Path2D roadPath = new Path2D.Double();
        g2.setColor(Color.LIGHT_GRAY);
        boolean isFirst = true;

        for (Point3d point : road)
        {
            if (point.z > start_point.z) {
                point = point.project(distance, getWidth(), getHeight());
                if (isFirst) {
                    roadPath.moveTo(point.x, point.y);
                    isFirst = false;
                } else {
                    roadPath.lineTo(point.x, point.y);
                }
            }
        }
        roadPath.closePath();
        if (needFill)
            g2.fill(roadPath);
        else
            g2.draw(roadPath);
    }

    private void init()
    {
        distance = 600;
        start_point = new Point3d(0,0,0);
        light_point = new Point3d(0,0,-1);

        up = new Point3d(0,-100,0);
        down = new Point3d(0,100,0);
        mouseControl = false;


        houses.add(new Parallelepiped(new Point3d(-20,10,30), new Point3d(-30, -10, 40), Color.RED));
        houses.add(new Parallelepiped(new Point3d(30,5,10), new Point3d(29.8, -15, 20),Color.ORANGE));
        houses.add(new Parallelepiped(new Point3d(29,10,10), new Point3d(28.8, -10, 20),Color.GREEN.darker()));
        houses.add(new Parallelepiped(new Point3d(-20,10,90), new Point3d(-30, -10, 100),Color.BLUE));
        houses.add(new Parallelepiped(new Point3d(30,10,150), new Point3d(20, -10, 140),Color.MAGENTA));

        road.add(new Point3d(-20, 10, 0));
        road.add(new Point3d(-10, 10, 0));
        road.add(new Point3d(0, 10, 0));
        road.add(new Point3d(10, 10, 0));
        road.add(new Point3d(20, 10, 0));
        road.add(new Point3d(20, 10, 10));
        road.add(new Point3d(20, 10, 20));
        road.add(new Point3d(20, 10, 30));
        road.add(new Point3d(20, 10, 40));
        road.add(new Point3d(20, 10, 50));
        road.add(new Point3d(20, 10, 60));
        road.add(new Point3d(20, 10, 70));
        road.add(new Point3d(20, 10, 80));
        road.add(new Point3d(20, 10, 90));
        road.add(new Point3d(20, 10, 100));
        road.add(new Point3d(20, 10, 110));
        road.add(new Point3d(20, 10, 120));
        road.add(new Point3d(20, 10, 130));
        road.add(new Point3d(20, 10, 140));
        road.add(new Point3d(20, 10, 150));
        road.add(new Point3d(10, 10, 150));
        road.add(new Point3d(0, 10, 150));
        road.add(new Point3d(-10, 10, 150));
        road.add(new Point3d(-20, 10, 150));
        road.add(new Point3d(-20, 10, 140));
        road.add(new Point3d(-20, 10, 130));
        road.add(new Point3d(-20, 10, 120));
        road.add(new Point3d(-20, 10, 110));
        road.add(new Point3d(-20, 10, 100));
        road.add(new Point3d(-20, 10, 90));
        road.add(new Point3d(-20, 10, 80));
        road.add(new Point3d(-20, 10, 70));
        road.add(new Point3d(-20, 10, 60));
        road.add(new Point3d(-20, 10, 50));
        road.add(new Point3d(-20, 10, 40));
        road.add(new Point3d(-20, 10, 30));
        road.add(new Point3d(-20, 10, 20));
        road.add(new Point3d(-20, 10, 10));

    }
}
