package finalprojectclipping;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class FinalProjectClipping extends Frame{
    //Width and Height of window
    static int WIDTH = 1000;
    static int HEIGHT = 600;

    public static void main(String[] args){
        new FinalProjectClipping();
    }

    FinalProjectClipping(){        //boiler-plate windowadapter, frame set-up
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e){
                System.exit(0);
            }
        });
        setSize(WIDTH, HEIGHT);
        add("Center", new CvCGHW3());
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        setVisible(true);
    }
}

class CvCGHW3 extends Canvas{
    Poly poly = null;       //create null polygon
    float rWidth = 10.0F;   //rWidth, rHeight, and pixelSize setup
    float rHeight = 7.5F;
    float pixelSize;
    int x0;                 //x0, y0 cenX, cenY setup for coordinates
    int y0;
    int cenX;
    int cenY;
    boolean ready = true;       //boolean ready will indicate status of drawing polygon
    CvCGHW3(){
        addMouseListener(new MouseAdapter() {       //mouse adapter
            public void mousePressed(MouseEvent evt){        //when mousePressed
                //save x and y coordinates from mouse
                int x = evt.getX();
                int y = evt.getY();
                if (ready){      //if ready
                    poly = new Poly();      //create new polygon
                    x0 = x;                 //set x0 and y0
                    y0 = y;
                    ready = false;          //invert ready
                }
                if (poly.size() > 0 && Math.abs(x - x0) < 3     //if the size is bigger than 0 and the difference of x is 
                    && Math.abs(y - y0) < 3)        //less than three and the difference of y is less than 3
                    ready = true;       //ready is true
                else        //else
                    poly.addVertex(new Point2D(fx(x), fy(y)));      //create a new vertex passing x and y
                repaint();      //repaint the canvas
            }
        });
    }

    void initgr(){      //initgr function
        Dimension d = getSize();       //find size
        int maxX = d.width - 1;        //set maxX and maxY
        int maxY = d.height - 1;
        pixelSize = Math.max(rWidth / maxX, rHeight / maxY);        //set pixelSize
        cenX = maxX / 2;     //set cenX and cenY
        cenY = maxY / 2;
    }

    //boilerplate float-to-int and int-to-float functions
    int iX(float x){
        return Math.round(cenX + x / pixelSize);
    }

    int iY(float y){
        return Math.round(cenY - y / pixelSize);
    }

    float fx(int x){
        return (x - cenX) * pixelSize;
    }

    float fy(int y){
        return (cenY - y) * pixelSize;
    }

    //drawLine function
    void drawLine(Graphics g, float xP, float yP, float xQ, float yQ){
        g.drawLine(iX(xP), iY(yP), iX(xQ), iY(yQ));
    }

    void drawPoly(Graphics g, Poly poly){
        int n = poly.size();        //set n equal to vertex size of poly
        if (n == 0)                 //if 0 size
            return;             //return, doing nothing
        Point2D a = poly.vertexAt(n - 1);       //set a to last point in polygon
        for (int i = 0; i < n; i++){        //for all vertices in polygon
            Point2D b = poly.vertexAt(i);       //set b to current poly
            drawLine(g, a.x, a.y, b.x, b.y);        //draw line from a to b
            a = b;      //set a to b, incrementing the process
        }
    }

    public void paintClipRect(Graphics g, float xMini, float xMaxi, float yMini, float yMaxi){      //this void will paint the clipping rectangle according to max values
        g.setColor(Color.blue);      //set color
        drawLine(g, xMini, yMini, xMaxi, yMini);        //drawLines
        drawLine(g, xMaxi, yMini, xMaxi, yMaxi);
        drawLine(g, xMaxi, yMaxi, xMini, yMaxi);
        drawLine(g, xMini, yMaxi, xMini, yMini);
    }

    public void paint(Graphics g){
        initgr();       //run initgr
        float xMini = -rWidth / 3;       //set xMin, xMax, yMin, yMax values 
        float xMaxi = rWidth / 3;
        float yMini = -rHeight / 3;
        float yMaxi = rHeight / 3;
        paintClipRect(g, xMini, xMaxi, yMini, yMaxi);       //paint clipping rectangle
        g.setColor(Color.black);        //set g back to black
        if (poly == null)       //if a null polygon
            return;     //simply return, doing nothing
        int n = poly.size();        //set n to polygon vertex size
        if (n == 0)         //if no vertices
            return;     //simply return, doing nothing
        Point2D a = poly.vertexAt(0);       //a is first vertex
        if (!ready){         //if not ready
            g.drawRect(iX(a.x) - 1, iY(a.y) - 1, 6, 6);     //small rectangle around first vertex
            for (int i = 1; i < n; i++){        //for each vertex in the polygon after the first
                Point2D b = poly.vertexAt(i);       //find the vertex
                drawLine(g, a.x, a.y, b.x, b.y);    //drawLine from a to b
                a = b;      //increment the process
            }
        }
        else{     //else if ready, this is where dotted line will be created
            Graphics2D g0 = (Graphics2D)g.create();     //create new 2D graphics element from g
            Stroke stroke = new BasicStroke(0, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 99, new float[] {1}, 0);       //new stroke representing dotted line
            g0.setStroke(stroke);           //set stroke of g0
            g0.setColor(Color.black);       //g0 is black
            drawPoly(g0, poly);             //draw the polygon
            poly.clip(g, xMini, yMini, xMaxi, yMaxi);      //clip the polygon passing the boundary values
            g.setColor(Color.black);        //set the color of g to black
            drawPoly(g, poly);              //draw the clipped polygon
            int pointNum = poly.v.size();        //find number of points in the polygon using vertex size
            int[] xPointNum = new int[pointNum];      //arrays for x and y points of polygon
            int[] yPointNum = new int[pointNum];
            for(int k=0;k<pointNum;k++){        //for each int in pointNum
                xPointNum[k] = iX(poly.v.get(k).x);       //get float values for x and y
                yPointNum[k] = iY(poly.v.get(k).y);
            }
            g.setColor(Color.blue);     //set the color of the clipped polygon to blue
            g.fillPolygon(xPointNum, yPointNum, pointNum);      //fill the polygon with the point arrays and pointNum
            }
        }
    }

//The following is the Poly class from the textbook
class Poly{
    Vector<Point2D> v = new Vector<Point2D>();

    void addVertex(Point2D p){
        v.addElement(p);
    }

    int size(){
        return v.size();
    }

    Point2D vertexAt(int i){
        return (Point2D) v.elementAt(i);
    }

    void clip(Graphics g, float xMini, float yMini, float xMaxi, float yMaxi){ 
        // Sutherland-Hodgman polygon clipping:
        Poly poly1 = new Poly();
        int n;
        Point2D a, b;
        boolean aIns, bIns; // whether A or b is on the same
                            // side as the rectangle

        // Clip against x == xMaxi:
        if ((n = size()) == 0)
            return;
        b = vertexAt(n - 1);
        for (int i = 0; i < n; i++){
            a = b;
            b = vertexAt(i);
            aIns = a.x <= xMaxi;
            bIns = b.x <= xMaxi;
            if (aIns != bIns)
                poly1.addVertex(new Point2D(xMaxi, a.y + (b.y - a.y)
                    * (xMaxi - a.x) / (b.x - a.x)));
            if (bIns)
                poly1.addVertex(b);
        }
        v = poly1.v;
        poly1 = new Poly();

        // Clip against x == xMini:
        if ((n = size()) == 0)
            return;
        b = vertexAt(n - 1);
        for (int i = 0; i < n; i++){
            a = b;
            b = vertexAt(i);
            aIns = a.x >= xMini;
            bIns = b.x >= xMini;
            if (aIns != bIns)
                poly1.addVertex(new Point2D(xMini, a.y + (b.y - a.y)
                    * (xMini - a.x) / (b.x - a.x)));
            if (bIns)
                poly1.addVertex(b);
        }
        v = poly1.v;
        poly1 = new Poly();
        
        // Clip against y == yMaxi:
        if ((n = size()) == 0)
                return;
        b = vertexAt(n - 1);
        for (int i = 0; i < n; i++){
            a = b;
            b = vertexAt(i);
            aIns = a.y <= yMaxi;
            bIns = b.y <= yMaxi;
            if (aIns != bIns)
                poly1.addVertex(new Point2D(a.x + (b.x - a.x) * (yMaxi - a.y)
                    / (b.y - a.y), yMaxi));
            if (bIns)
                poly1.addVertex(b);
        }
        v = poly1.v;
        poly1 = new Poly();

        // Clip against y == yMini:
        if ((n = size()) == 0)
                return;
        b = vertexAt(n - 1);
        for (int i = 0; i < n; i++){
            a = b;
            b = vertexAt(i);
            aIns = a.y >= yMini;
            bIns = b.y >= yMini;
            if (aIns != bIns)
                poly1.addVertex(new Point2D(a.x + (b.x - a.x) * (yMini - a.y)
                    / (b.y - a.y), yMini));
            if (bIns)
                poly1.addVertex(b);
        }
        v = poly1.v;
        poly1 = new Poly();
    }
}
//points in logical coordinates
class Point2D{
    float x, y;
    Point2D(float x, float y){
        this.x = x;
        this.y = y;
    }
}
