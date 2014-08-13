import java.util.TreeSet;

public class KdTree {
    private TreeSet<Point2D> set; // set of 2D points
    private Node root = null; // root of the tree set of Nodes
    private static Point2D point; // nearest point
    private static double min; // nearest distance
    
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
    private Node put(Node node, Point2D p, boolean compX) {
        if (node == null) return new Node(p);     
        
        if (compX) {
            if (node.p.x() <= p.x()) {
                node.rt = put(node.rt, p, false);
            }else {
                node.lb = put(node.lb, p, false);
            }
        }else {
            if (node.p.y() <= p.y()) {
                node.rt = put(node.rt, p, true);
            }else {
                node.lb = put(node.lb, p, true);
            }
        }
        return node;
    }

    // add the point p to the set (if it is not already in the set)    
    public void insert(Point2D p) {
        if (!contains(p)) {
            root = put(root, p, true);
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
    private void drawNode(Node node, RectHV rect, boolean hori) {
        if (node == null) return ;
        // if node is not null, draw the current node
        RectHV lbRect = null;
        RectHV rtRect = null;
        if (hori) {
            // horizontal
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.setPenRadius(.01);
            Point2D l = new Point2D(rect.xmin(), node.p.y());
            Point2D r = new Point2D(rect.xmax(), node.p.y());
            l.drawTo(node.p);
            r.drawTo(node.p);
            lbRect = new RectHV(rect.xmin(), rect.ymin(), rect.xmax(), node.p.y());
            rtRect = new RectHV(rect.xmin(), node.p.y(), rect.xmax(), rect.ymax());
        }else {
            // vertical
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.setPenRadius(.01);
            Point2D b = new Point2D(node.p.x(), rect.ymin());
            Point2D t = new Point2D(node.p.x(), rect.ymax());
            b.drawTo(node.p);
            t.drawTo(node.p);
            lbRect = new RectHV(rect.xmin(), rect.ymin(), node.p.x(), rect.ymax());
            rtRect = new RectHV(node.p.x(), rect.ymin(), rect.xmax(), rect.ymax());
        }
        // draw the left and the right child, vertically
        drawNode(node.lb, lbRect, !hori);
        drawNode(node.rt, rtRect, !hori);
    }

    // draw all of the points to standard draw
    public void draw() {
        RectHV rect = new RectHV(0, 0, 1, 1);
        drawNode(root, rect, false);
    }
    
    // helper method for range, check whether the rect contain the current node
    private void rangeHelper(Stack<Point2D> ret, Node node, RectHV rect, boolean hori) {
        if (node == null) return ;
        if (rect.contains(node.p)) ret.push(node.p); // put the node into the result if contained
        // calculate the left/bottom and the right/top rectangles
        RectHV lbRect = null;
        RectHV rtRect = null;
        if (hori) {
            lbRect = new RectHV(node.rect.xmin(), node.rect.ymin(), node.rect.xmax(), node.p.y());
            rtRect = new RectHV(node.rect.xmin(), node.p.y(), node.rect.xmax(), node.rect.ymax());
        }else {
            lbRect = new RectHV(node.rect.xmin(), node.rect.ymin(), node.p.x(), node.rect.ymax());
            rtRect = new RectHV(node.p.x(), node.rect.ymin(), node.rect.xmax(), node.rect.ymax());
        }       
        // check the left and the right nodes, if child rectangle intersects with rect
        if (node.lb != null && lbRect.intersects(rect)) {
            node.lb.rect = lbRect;
            rangeHelper(ret, node.lb, rect, !hori);
        }
        if (node.rt != null && rtRect.intersects(rect)) {
            node.rt.rect = rtRect;
            rangeHelper(ret, node.rt, rect, !hori);
        }   
    }
    
    // all points in the set that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        Stack<Point2D> ret = new Stack<Point2D>(); // store the points
        if (root == null) return ret;
        root.rect = new RectHV(0, 0, 1, 1);
        rangeHelper(ret, root, rect, false);
        return ret;
    }
    
    // helper method for nearest
    private void nearestHelper(Point2D p, Node node, boolean hori) {
        if (node == null) return ;
        double dist = node.p.distanceSquaredTo(p);
        if (dist < min) {
            point = node.p;
            min = dist;
        }
        if (node.lb == null && node.rt == null) return ;
        
        // calculate the left/bottom and the right/top rectangles
        RectHV lbRect = null;
        RectHV rtRect = null;
        if (hori) {
            lbRect = new RectHV(node.rect.xmin(), node.rect.ymin(), node.rect.xmax(), node.p.y());
            rtRect = new RectHV(node.rect.xmin(), node.p.y(), node.rect.xmax(), node.rect.ymax());
        }else {
            lbRect = new RectHV(node.rect.xmin(), node.rect.ymin(), node.p.x(), node.rect.ymax());
            rtRect = new RectHV(node.p.x(), node.rect.ymin(), node.rect.xmax(), node.rect.ymax());
        }
        if (node.lb != null) node.lb.rect = lbRect;
        if (node.rt != null) node.rt.rect = rtRect;
        
        // nearest algorithm, choose the closer point at first
        if (lbRect.contains(p)) {
            if (node.lb != null) nearestHelper(p, node.lb, !hori);
            if (node.rt != null && rtRect.distanceSquaredTo(p) < min) {
                nearestHelper(p, node.rt, !hori);
            }
        }else {
            if (node.rt != null) nearestHelper(p, node.rt, !hori);
            if (node.lb != null && lbRect.distanceSquaredTo(p) < min) {
                nearestHelper(p, node.lb, !hori);
            }
        }
    }
    
    // a nearest neighbor in the set to p; null if set is empty
    public Point2D nearest(Point2D p) {
        point = null; // reset the global variable at first
        min = Double.MAX_VALUE; // reset the global distance
        if (root == null) return point;
        root.rect = new RectHV(0, 0, 1, 1);
        nearestHelper(p, root, false);
        return point;
    }  
}