import java.util.TreeSet;

public class PointSET {
    private TreeSet<Point2D> set;
    
    // construct an empty set of points
    public PointSET() {
        set = new TreeSet<Point2D> ();
    }
    
    // is the set empty?
    public boolean isEmpty() {
        return set.size() == 0;
    }
    
    // number of points in the set
    public int size() {
        return set.size();
    }

    // add the point p to the set (if it is not already in the set)    
    public void insert(Point2D p) {
        if (!contains(p)) set.add(p);
    }

    // does the set contain the point p?
    public boolean contains(Point2D p) {
        if (set.contains(p)) return true;
        return false;
    }

    // draw all of the points to standard draw
    public void draw() {
        for (Point2D point : set) {
            point.draw();
        }
    }
    
    // all points in the set that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        Stack<Point2D> stk = new Stack<Point2D> ();
        for (Point2D point : set) {
            if (rect.contains(point)) stk.push(point);
        }
        return stk;
    }
    
    // a nearest neighbor in the set to p; null if set is empty
    public Point2D nearest(Point2D p) {
        double min = Double.MAX_VALUE;
        Point2D nearst = null;
        for (Point2D point : set) {
            double dist = p.distanceTo(point);
            if (dist < min) {
                min = dist;
                nearst = point;
            }
        }
        return nearst;
    }
    
}