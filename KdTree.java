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

    // add the point p to the set (if it is not already in the set)    
    public void insert(Point2D p) {
        if (!contains(p)) {
            RectHV rect = new RectHV(0, 0, 1, 1);
            root = put(root, rect, p, true);
            set.add(p);
        }
    }

    // does the set contain the point p?
    public boolean contains(Point2D p) {
        return search(root, p, true);
    }

    // draw all of the points to standard draw
    public void draw() {

    }
    
    // all points in the set that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        
    }
    
    // a nearest neighbor in the set to p; null if set is empty
    public Point2D nearest(Point2D p) {

    }  
}