import java.util.TreeSet;

public class KdTree {
    private TreeSet<Point2D> set; // set of 2D points
    private Node root; // root of the tree set of Nodes
    
    private static class Node {
        private Point2D p; // the point
        private RectHV rect; // the axis-aligned rectangle corresponding to this node
        private Node lb; // the left/bottom subtree
        private Node rt; // the right/top subtree
        
        // constructor
        private Node(Point2D p) {
            this.p = p;
        }
    }
    
    // construct an empty set of points/nodes
    public KdTree() {
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
    
    // helper method to put point into tree set whose root is node and return the new node
    // compX is the parameter means to compare items horizontally
    private Node put(Node node, RectHV rectangle, Point2D p, boolean compX) {
        if (node == null) {
            Node n = new Node(p);
            n.rect = rectangle;
            return n;
        }
        
        if (compX) {
            if (node.p.x() <= p.x()) {
                RectHV rect = new RectHV(node.p.x(), node.rect.ymin(), node.rect.xmax(), node.rect.ymax());
                node.rt = put(node.rt, rect, p, false);
            }else {
                RectHV rect = new RectHV(node.rect.xmin(), node.rect.ymin(), node.p.x(), node.rect.ymax());
                node.lb = put(node.lb, rect, p, false);
            }
        }else {
            if (node.p.y() <= p.y()) {
                RectHV rect = new RectHV(node.rect.xmin(), node.p.y(), node.rect.xmax(), node.rect.ymax());
                node.rt = put(node.rt, rect, p, true);
            }else {
                RectHV rect = new RectHV(node.rect.xmin(), node.rect.ymin(), node.rect.xmax(), node.p.y());
                node.lb = put(node.lb, rect, p, true);
            }
        }
        return node;
    }

    // add the point p to the set (if it is not already in the set)    
    public void insert(Point2D p) {
        if (!contains(p)) {
            RectHV rect = new RectHV(0, 0, 1, 1);
            root = put(root, rect, p, true);
            set.add(p);
        }
    }
    
    // helper method to search the node
    // compX is the parameter means to compare items horizontally
    private boolean search(Node node, Point2D p, boolean compX) {
        if (node == null) return false;
        if (node.p.equals(p)) return true;
        // if not equal, go to the subtree and compare
        if (compX) {
            if (node.p.x() <= p.x()) {
                return search(node.rt, p, false);
            }else {
                return search(node.lb, p, false);
            }
        }else {
            if (node.p.y() <= p.y()) {
                return search(node.rt, p, true);
            }else {
                return search(node.lb, p, true);
            }
        }
    }

    // does the set contain the point p?
    public boolean contains(Point2D p) {
        return search(root, p, true);
    }
    
    // helper method to draw Kdtree, if hori is true, draw horizontal line
    public void drawNode(Node node, boolean hori) {
        if (node == null) return ;
        // if node is not null, draw the current node
        RectHV rect = node.rect;
        if (hori) {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.setPenRadius(.01);
            Point2D l = new Point2D(rect.xmin(), node.p.y());
            Point2D r = new Point2D(rect.xmax(), node.p.y());
            l.drawTo(node.p);
            r.drawTo(node.p);
        }else {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.setPenRadius(.01);
            Point2D b = new Point2D(node.p.x(), rect.ymin());
            Point2D t = new Point2D(node.p.x(), rect.ymax());
            b.drawTo(node.p);
            t.drawTo(node.p);
        }
        // draw the left and the right child, vertically
        drawNode(node.lb, !hori);
        drawNode(node.rt, !hori);
    }

    // draw all of the points to standard draw
    public void draw() {
        drawNode(root, false);
    }
    
    // all points in the set that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        Stack<Point2D> ret = new Stack<Point2D>(); // store the points
        Stack<Node> stk = new Stack<Node>(); // store the unchecked node
        stk.push(root);
        while (!stk.isEmpty()) {
            Node node = stk.pop(); 
            if (rect.contains(node.p)) ret.push(node.p); // push the 2d point in the result
            if (rect.intersects(node.lb.rect)) stk.push(node.lb); // push left child to the stack
            if (rect.intersects(node.rt.rect)) stk.push(node.rt); // push right child to the stack  
        }
        return ret;
    }
    
    // a nearest neighbor in the set to p; null if set is empty
    public Point2D nearest(Point2D p) {
        if (set.size() == 0) return null;
        Point2D point = null;
        double min = Double.MAX_VALUE;
        Stack<Node> stk = new Stack<Node>(); // store the unchecked node
        stk.push(root);
        while (!stk.isEmpty()) {
            Node node = stk.pop();
            double dist = node.p.distanceSquaredTo(p);
            if (dist < min) {
                min = dist;
                point = node.p;
            }
            double left = node.lb.rect.distanceSquaredTo(p);
            double right = node.rt.rect.distanceSquaredTo(p);
            if (left < min) stk.push(node.lb);
            if (right < min) stk.push(node.rt);
        }
        return point;
    }  
}