package com.op.scratch3d;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PinHole extends Base {
    private static PinHole tester = new PinHole();
    private final String ipFileName = "Virga3";
    private final String opFileName = ipFileName + "PIN";
    private String opDir = hostDir + "pinHole/";

    SvgDrawer svgDescriber;
    private BufferedImage ibi;
    private Graphics2D ipG;
    private int w = 0;
    private int h = 0;

    public static void main(String[] args) throws Exception, FontFormatException {
        tester.draw();
    }

    private void draw() throws Exception {
        setupImages();

        //drawPins();
        drawRandomPins();

        save();
    }

    private void drawRandomPins() {
        int num = 5000; //100000;
        int rad = 1;
        int grey = 127;
        ArrayList<Point> points = new ArrayList<>();
        for (int i=0; i< num; i++) {
            int x = (int)(Math.random()* (double)w);
            int y = (int)(Math.random()* (double)h);
            Point p0 = new Point(x-1, y-1);
            Point p1 = new Point(x, y-1);
            Point p2 = new Point(x+1, y-1);
            Point p3 = new Point(x-1, y);
            Point p = new Point(x, y);
            Point p5 = new Point(x+1, y);
            Point p6 = new Point(x-1, y+1);
            Point p7 = new Point(x, y+1);
            Point p8 = new Point(x+1, y+1);

            if (points.contains(p0)
                    || points.contains(p1)
                    || points.contains(p2)
                    || points.contains(p3)
                    || points.contains(p)
                    || points.contains(p5)
                    || points.contains(p6)
                    || points.contains(p7)
                    || points.contains(p8)) {
                continue;
            }
            int rgb = ibi.getRGB(x,y);
            double r = (rgb >> 16) & 0x000000FF;
            double g = (rgb >> 8) & 0x000000FF;
            double b = (rgb) & 0x000000FF;
            double c = (((r + g + b) / 3.0));
            if (c < grey) {
                //svgDescriber.writeCircle(x,y,rad);
                points.add(p);
            }

        }

        Collections.sort(points, new Comparator<Point>() {
            @Override
            public int compare(Point o1, Point o2) {
                int xd = o2.x - o1.x;
                int yd = o2.y - o1.y;
                return (int) (xd + w * yd);
            }
        });
        for (Point p : points) {
            svgDescriber.writeLine(p.x,p.y,p.x+rad, p.y+rad);
        }
    }

    private void drawPins() {
        int step = 10;
        int rad = 1;
        int grey = 150;
        for (int j = 0; j < h; j=j+step) {
            for (int i = 0; i < w; i=i+step) {
                int rgb = ibi.getRGB(i, j);
                double r = (rgb >> 16) & 0x000000FF;
                double g = (rgb >> 8) & 0x000000FF;
                double b = (rgb) & 0x000000FF;
                double c = (((r + g + b) / 3.0));
                if (c < grey) {
                    svgDescriber.writeCircle(i,j,rad);
                }
            }
        }
    }

    private void setupImages() throws IOException {
        System.out.println("initialising...");
        ibi = ImageIO.read(new File(opDir + ipFileName + ".jpg"));
        ipG = (Graphics2D) ibi.getGraphics();

        w = ibi.getWidth();
        h = ibi.getHeight();

        svgDescriber = new SvgDrawer(opDir, opFileName, w, h);
        svgDescriber.startSVG(false, false);

    }

    void save() throws Exception {
        File op1 = new File(opDir + opFileName);
        svgDescriber.endSVG();
        System.out.println("Saved " + op1.getPath());
    }
}
