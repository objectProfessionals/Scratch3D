package com.op.scratch3d;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class LineHole extends Base {

    private static LineHole averagePaint = new LineHole();

    private String dir = hostDir + "pinHole/";
    private String ipFile = "Virga2";
    private String opFilePre = "HOLE";
    private int w = 0;
    private int h = 0;
    private Type type = Type.LINE;

    private double scale = 12;
    private int radStart = 0;
    private double radMin = 0;
    private double lowThreshold = 30.0;//300
    SvgDrawer svgDescriber;

    private Path2D.Double path = new Path2D.Double();
    private ArrayList<Circle> circles = new ArrayList<Circle>();
    private int varianceF = 2; // 2

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        averagePaint.draw();

    }

    public void draw() throws IOException {
        File ip = new File(dir + ipFile + ".jpg");
        BufferedImage bi = ImageIO.read(ip);
        w = bi.getWidth();
        h = bi.getHeight();

        svgDescriber = new SvgDrawer(dir, ipFile + opFilePre, w, h);
        svgDescriber.startSVG(false, false);

        radStart = (int) (((double) w) / scale);
        radMin = radStart / scale;

        paintVariance(bi, 0, 0, w, h, radStart);

        save();
    }

    private void paintVariance(BufferedImage bi, int x1, int y1, int ww,
                               int hh, int rad) {
        for (int y = y1; y < y1 + hh - rad; y = y + 2 * rad) {
            for (int x = x1; x < x1 + ww - rad; x = x + 2 * rad) {
                paintOne(bi, rad, x, y);
            }
        }
    }

    private void paintOne(BufferedImage bi, int rad, int x, int y) {
        double var = getSD(bi, x, y, rad);
        if (var < lowThreshold) {
            paintOneType(bi, x, y, rad);
        } else {
            if (rad > radMin) {
                paintVariance(bi, x, y, rad * 2, rad * 2, rad / varianceF);
            } else {
                paintOneType(bi, x, y, rad);
            }
        }
    }

    private void paintOneType(BufferedImage bi, int x, int y, int rrad) {
        int dia = rrad * 2;
        System.out.println("x,y,r=" + x + "," + y + "," + rrad);

        if (x + dia >= bi.getWidth() || y + dia >= bi.getHeight() || dia <= 0) {
            return;
        }
        BufferedImage sub = bi.getSubimage(x, y, 2 * rrad, 2 * rrad);
        int r = (int) meanValue(sub, 0);
        int g = (int) meanValue(sub, 1);
        int b = (int) meanValue(sub, 2);
        Color c = new Color(r, g, b);
        //opG.setColor(c);
        if (type == Type.CIRCLE) {
            drawCircle(x, y, rrad, c);
        } else if (type == Type.LINE) {
            drawLine(x, y, rrad, c);
        } else if (type == Type.RECT) {
            //opG.setColor(getAlphaColor(c, 225));
            int border = rrad / 5;
//            opG.fillRoundRect(x + border, y + border, 2 * (rrad - border),
//                    2 * (rrad - border), border * 4, border * 4);

        }
    }


    private void drawCircle(int x, int y, int rrad, Color c) {
//        opG.setColor(getAlphaColor(c, 225));
//        opG.fillOval(x, y, 2 * rrad, 2 * rrad);
        double rad = 1 - (((c.getRed() + c.getGreen() + c.getBlue()) / 3.0) / 255.0);
        svgDescriber.writeCircleD2(x + rrad, y + rrad, rrad * rad);
    }

    private void drawLine(int x, int y, int rrad, Color c) {
        //opG.setColor(getAlphaColor(c, 225));
        double r = rrad;
        Path2D p = new Path2D.Double();
        p.moveTo(r, r * 2);
        p.lineTo(r, 0);
        AffineTransform tr = new AffineTransform();
        AffineTransform mv = AffineTransform.getTranslateInstance(x, y);
        double f = ((c.getRed() + c.getBlue() + c.getGreen()) / 3.0) / 255.0;
        AffineTransform ro = AffineTransform.getRotateInstance(Math.PI * f, r, r);
        tr.concatenate(mv);
        tr.concatenate(ro);
        p.transform(tr);
        //opG.setStroke(new BasicStroke(5));
        //opG.draw(p);
        PathIterator pathIterator = p.getPathIterator(null);
        float coords[] = {0, 0, 0, 0};
        double x1 = 0;
        double y1 = 0;
        double x2 = 0;
        double y2 = 0;
        int i =0;
        while (!pathIterator.isDone()) {
            pathIterator.currentSegment(coords);

            if (i==0) {
                x1  = coords[0];
                y1  = coords[1];
            } else {
                x2  = coords[0];
                y2  = coords[1];
            }
            pathIterator.next();
            i++;
        }

        svgDescriber.writeLine(x1, y1, x2, y2);
    }

    private Color getAlphaColor(Color orig, int alpha) {
        Color newC = new Color(orig.getRed(), orig.getGreen(), orig.getBlue(),
                alpha);
        return newC;
    }

    private void save() throws IOException {
        svgDescriber.endSVG();
    }

    private double getSD(BufferedImage bi, int x, int y, int rad) {
        // HexShape hex = new HexShape(rad, rad, rad);
        int dia = rad * 2;
        if (x + dia >= bi.getWidth() || y + dia >= bi.getHeight() || dia <= 0) {
            return 255;
        }
        BufferedImage sub = bi.getSubimage(x, y, dia, dia);
        // double varRed = getVariance(sub, 0);
        // double varGreen = getVariance(sub, 1);
        // double varBlue = getVariance(sub, 2);
        // System.out.println("x,y=" + x + "," + y + " rgb=" + varRed + ":"
        // + varGreen + ":" + varBlue);
        double varGrey = getVariance(sub, 4);
        return varGrey;
    }

    public double getVariance(BufferedImage image, int ind) {
        double mean = meanValue(image, ind);
        double sumOfDiff = 0.0;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int[] arr = getRGBAG(image, x, y);
                double colour = arr[ind] - mean;
                sumOfDiff += Math.pow(colour, 2);
            }
        }
        return sumOfDiff / ((image.getWidth() * image.getHeight()) - 1);
    }

    private double meanValue(BufferedImage image, int ind) {
        double tot = 0;
        double c = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                tot = tot + getRGBAG(image, x, y)[ind];
                c++;
            }
        }
        return tot / c;
    }

    public int[] getRGBAG(BufferedImage image, int x, int y) {
        int rgb = image.getRGB(x, y);
        int aa = (rgb >>> 24) & 0x000000FF;
        int r = (rgb >>> 16) & 0x000000FF;
        int g = (rgb >>> 8) & 0x000000FF;
        int b = (rgb >>> 0) & 0x000000FF;
        int grey = (r + g + b) / 3;

        int[] arr = {r, g, b, aa, grey};
        return arr;
    }

    /* Red Standard Deviation */
    public double standardDeviationRed(BufferedImage image, int ind) {
        return Math.sqrt(getVariance(image, ind));
    }

    private class Circle implements Comparable<Circle> {
        private int n = 0;
        private double x = 0;
        private double y = 0;
        private double rad = 0;
        private Color c;

        Circle(int n, double x, double y, double rad, Color c) {
            this.n = n;
            this.x = x;
            this.y = y;
            this.rad = rad;
            this.c = c;
        }

        @Override
        public int compareTo(Circle c) {
            //return (int) (this.rad - c.rad);
            return (int) (this.n - c.n);
        }
    }

    public enum Type {
        CIRCLE("CIR"),
        RECT("RECT"),
        LINE("LINE");

        final String name;

        Type(String name) {
            this.name = name;
        }
    }
}
