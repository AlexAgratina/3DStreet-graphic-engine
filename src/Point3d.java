public class Point3d {

    double x = 0;
    double y = 0;
    double z = 0;

    public Point3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point3d project(double distance, int width, int height)
    {
        double x = width/2 + distance * this.x/this.z;
        double y = height/2 + distance * this.y/this.z;
        return new Point3d(x,y,1);
    }

    public void transform(double [][] matrix)
    {
        double nx = x*matrix[0][0] + y*matrix[0][1] + z*matrix[0][2] + matrix[0][3];
        double ny = x*matrix[1][0] + y*matrix[1][1] + z*matrix[1][2] + matrix[1][3];
        double nz = x*matrix[2][0] + y*matrix[2][1] + z*matrix[2][2] + matrix[2][3];
        this.x = nx;
        this.y = ny;
        this.z = nz;
    }

    public Point3d substr(Point3d other)
    {
        return new Point3d(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    public Point3d add(Point3d other)
    {
        return new Point3d(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public Point3d multiply(Point3d other)
    {
        return new Point3d(this.y*other.z - other.y*this.z, this.z*other.x - other.z*this.x, this.x*other.y - other.x*this.y);
    }

    public static double distance(Point3d point1, Point3d point2)
    {
        double dist = Math.pow(point1.x - point2.x, 2) + Math.pow(point1.y - point2.y, 2) + Math.pow(point1.z - point2.z, 2);
        return Math.pow(dist, 0.5);
    }

    public static Point3d middle(Point3d p1, Point3d p2)
    {
        return new Point3d((p1.x+p2.x)/2, (p1.y+p2.y)/2, (p1.z+p2.z)/2);
    }

    @Override
    public String toString() {
        return "Point3d{"+ x +", " + y + ", " + z + "}";
    }
}