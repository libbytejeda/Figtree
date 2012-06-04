package figtree.treeviewer.painters;

import figtree.treeviewer.TreePane;
import jebl.evolution.graphs.Node;
import jebl.evolution.trees.RootedTree;
import jebl.evolution.trees.Tree;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

/**
 * @author Andrew Rambaut
 * @version $Id: NodeShapePainter.java 536 2006-11-21 16:10:24Z rambaut $
 */
public class NodeShapePainter extends NodePainter {


    public static final String FIXED = "fixed";
    public static final double SIZE = 10.0;


    public enum NodeShape {
        CIRCLE("Circle"),
        RECTANGLE("Rectangle");

        NodeShape(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String toString() {
            return name;
        }

        private final String name;
    }

    public NodeShapePainter() {

        nodeShape = new Ellipse2D.Double(0,0,1,1);
        setupAttributes(null);
    }

    public void setupAttributes(Collection<? extends Tree> trees) {
        java.util.Set<String> attributeNames = new TreeSet<String>();
        attributeNames.add(FIXED);

        if (trees != null) {
            for (Tree tree : trees) {
                for (Node node : tree.getNodes()) {
                    for (String name : node.getAttributeNames()) {
                        if (!name.startsWith("!")) {
                            Object attr = node.getAttribute(name);
                            if (attr instanceof Object[]) {
                                Object[] array = (Object[])attr;
                                if (array.length == 2 &&
                                        array[0] instanceof Double &&
                                        array[1] instanceof Double) {
                                    attributeNames.add(name);
                                }
                            }
                        }
                    }
                }
            }
        }

        this.attributeNames = new String[attributeNames.size()];
        attributeNames.toArray(this.attributeNames);

        fireAttributesChanged();
    }

    public void setTreePane(TreePane treePane) {
        this.treePane = treePane;
    }

    public Shape getNodeShape() {
        return nodeShape;
    }

    public Rectangle2D calibrate(Graphics2D g2, Node node) {
        RootedTree tree = treePane.getTree();

        nodeShape = null;

        Line2D shapePath = treePane.getTreeLayoutCache().getNodeShapePath(node);
        if (shapePath != null) {

            double size = tree.getHeight(node) * 0.75;

            boolean hasShape = false;

            if (sizeAttribute != null && !sizeAttribute.equals(FIXED)) {
                Object value = node.getAttribute(sizeAttribute);
                if (value != null) {
                    if (value != null ) {
                        if (value instanceof Number) {
                            size = ((Number)value).doubleValue();
                        } else {
                            size = Double.parseDouble(value.toString());
                        }
                        hasShape = true;
                    } else {
                        // todo - warn the user somehow?
                    }
                }

            } else {
                hasShape = true;
            }

            if (hasShape) {
                // x1,y1 is the node point
                double x1 = shapePath.getX1();
                double y1 = shapePath.getY1();

                // x2,y2 is 1.0 units higher than the node
                double x2 = shapePath.getX2();
                double y2 = shapePath.getY2();

                nodeShape = new Ellipse2D.Double(x1, y1, size, size);

            }
        }

        if (nodeShape == null) {
            return new Rectangle2D.Double(0,0,0,0);
        }

        return nodeShape.getBounds2D();
    }

    public double getPreferredWidth() {
        return 1.0;
    }

    public double getPreferredHeight() {
        return 1.0;
    }

    public double getHeightBound() {
        return 1.0;
    }

    /**
     * The bounds define the shape of the nodeBar so just draw it
     * @param g2
     * @param node
     */
    public void paint(Graphics2D g2, Node node, Point2D nodePoint) {
//        if (nodeShape != null) {

            nodeShape = new Ellipse2D.Double(nodePoint.getX() - (SIZE / 2), nodePoint.getY() - (SIZE / 2), SIZE, SIZE);

            g2.setPaint(Color.blue);
            g2.fill(nodeShape);

            g2.setPaint(Color.black);
            g2.setStroke(new BasicStroke(0.5F));

            g2.draw(nodeShape);
//        }

    }

    /**
     * The bounds define the shape of the nodeBar so just draw it
     * @param g2
     * @param node
     * @param justification
     * @param bounds
     */
    public void paint(Graphics2D g2, Node node, Justification justification, Rectangle2D bounds) {
        throw new UnsupportedOperationException("This version of paint is not used in NodeShapePainter");
    }

    public String[] getAttributeNames() {
        return attributeNames;
    }

    public String getSizeAttribute() {
        return sizeAttribute;
    }

    public void setSizeAttribute(String sizeAttribute) {
        this.sizeAttribute = sizeAttribute;
        firePainterChanged();
    }

    public String getColourAttribute() {
        return colourAttribute;
    }

    public void setColourAttribute(String colourAttribute) {
        this.colourAttribute = colourAttribute;
        firePainterChanged();
    }

    private Shape nodeShape;

    private String sizeAttribute = null;

    private String colourAttribute = null;
    private String[] attributeNames;

    private TreePane treePane;
}
