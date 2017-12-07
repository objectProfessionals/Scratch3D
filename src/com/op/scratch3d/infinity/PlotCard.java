package com.op.scratch3d.infinity;

import com.op.scratch3d.Base;
import com.op.scratch3d.sounds.WaveFileReader;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

public class PlotCard extends Base {
    private static final String opFileName = "card";
    private String opDir = hostDir + "output/";
    private String ipDir = hostDir + "sounds/";
    private static PlotCard tester;
    private WaveFileReader reader;
    private double dpi = 300;
    private double radiusmm = 3;
    private double mm2in = 25.4;
    private double wmm = 85.60;
    private double hmm = 53.98;
    private double ledmm = 7.00;
    private double batterymm = 16.00;
    private int w = (int) (dpi * (wmm / mm2in));
    private int h = (int) (dpi * (hmm / mm2in));
    private int radius = (int) (dpi * (radiusmm / mm2in));
    private int led = (int) (dpi * (ledmm / mm2in));
    private int battery = (int) (dpi * (batterymm / mm2in));
    private int cx = w / 2;
    private int cy = h / 2;
    private PrintWriter writer;

    public static void main(String[] args) throws Exception, FontFormatException {
        tester = new PlotCard();
        tester.draw();
    }

    private void draw() throws FileNotFoundException, UnsupportedEncodingException {
        init();
        drawAll();
        saveSVG();
    }

    private void init() throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("Creating...");
        String src = opDir;
        writer = new PrintWriter(src + opFileName + ".svg", "UTF-8");
        writer.println("<svg width=\"" + w + "\" height=\"" + h + "\" xmlns=\"http://www.w3.org/2000/svg\">");

    }


    private void drawAll() {
        writer.println("<path d=\"");

        drawCard();

        drawHole();

        writer.println("\" stroke=\"black\" fill=\"none\" />");
    }

    private void drawHole() {
        double numLedsHoriz = 10;
        double numLedsVert = 7;
        double holew = led *numLedsHoriz;
        double holeh = led *numLedsVert;
        double yBorder = (h - (numLedsVert * led))/2;
        double xBorder = (w - (numLedsHoriz * led))/2;

        boolean squareHole = true;

        if (squareHole) {
            holew = holeh;
            xBorder = yBorder;
            numLedsHoriz = numLedsVert;
        }

        double ledCornerD = Math.sin(Math.PI / 4)*led;
        drawLine(xBorder, yBorder+ledCornerD, xBorder+ledCornerD, yBorder);
        drawLine(xBorder+ledCornerD, yBorder, xBorder+holew-ledCornerD, yBorder);

        drawLine(xBorder+holew-ledCornerD, yBorder, xBorder+holew, yBorder+ledCornerD);
        drawLine(xBorder+holew, yBorder+ledCornerD, xBorder+holew, yBorder+holeh-ledCornerD);

        drawLine(xBorder+holew, yBorder+holeh-ledCornerD, xBorder+holew-ledCornerD, yBorder+holeh);
        drawLine(xBorder+holew-ledCornerD, yBorder+holeh, xBorder+ledCornerD, yBorder+holeh);

        drawLine(xBorder+ledCornerD, yBorder+holeh, xBorder, yBorder+holeh-ledCornerD);
        drawLine(xBorder, yBorder+holeh-ledCornerD, xBorder, yBorder+ledCornerD);

        double mark = led*0.1;
        double hrem = (holeh - ((led*(numLedsVert-2)) + (2 * ledCornerD)))/2;
        for (double i = 0; i<numLedsVert-1; i++) {
            drawLine(xBorder, hrem+yBorder+ledCornerD+led*i, xBorder-mark, hrem+yBorder+ledCornerD+led*i);
            drawLine(xBorder+holew, hrem+yBorder+ledCornerD+led*i, xBorder+holew+mark, hrem+yBorder+ledCornerD+led*i);
        }

        double wrem = (holew - ((led*(numLedsHoriz-2)) + (2 * ledCornerD)))/2;
        for (double i = 0; i<numLedsHoriz-1; i++) {
            drawLine(wrem+xBorder+ledCornerD+led*i, yBorder, wrem+xBorder+ledCornerD+led*i, yBorder-mark);
            drawLine(wrem+xBorder+ledCornerD+led*i, yBorder+holeh, wrem+xBorder+ledCornerD+led*i, yBorder+holeh+mark);
        }

        if (squareHole) {
            drawBattery(holew, holeh, xBorder, yBorder);
        }

    }

    private void drawBattery(double holew, double holeh, double xBorder, double yBorder) {

        double midx = xBorder*2 + holew +(w - xBorder*3 - holew)/2;
        double midy = (h - yBorder - holeh)/2;
        drawArc(midx, midy, battery/2, 100, 350, 1, 0);
    }


    private void drawCard() {
        drawLine(radius, 0, w - radius, 0);
        drawArc(w - radius, radius, radius, 0, -90, 0, 0);

        drawLine(w, radius, w, h-radius);
        drawArc(w - radius, h-radius, radius, 90, 0, 0, 0);

        drawLine(w-radius, h, radius, h);
        drawArc(radius, h-radius, radius, 90, 180, 0, 1);

        drawLine(0, h-radius, 0, radius);
        drawArc(radius, radius, radius, 270, 180, 0, 0);
    }

    private void drawLine(double xs, double ys, double xe, double ye) {

        String sb = "M " + round(xs, 2) + " " + round(ys, 2) + " L " + round(xe, 2) + " " + round(ye, 2) + " ";
        writer.println(sb);
    }

    private void drawArc(double cx, double cy, double radius, double as, double ae, int large, int sweep) {

        double[] start = polarToCartesian(cx, cy, radius, as);
        double[] end = polarToCartesian(cx, cy, radius, ae);
        String d = "M" + round(start[0], 2) + " " + round(start[1], 2) + " A " + round(radius, 2) + " " + round(radius, 2)
                + " 0 "+large+" "+sweep +" " + round(end[0], 2) + " " + round(end[1], 2);
        writer.println(d);
    }

    double[] polarToCartesian(double centerX, double centerY, double radius, double angleInDegrees) {
        double angleInRadians = Math.toRadians(angleInDegrees);
        double x = (float) (centerX + (radius * Math.cos(angleInRadians)));
        double y = (float) (centerY + (radius * Math.sin(angleInRadians))); // -

        double[] arr = {x, y};
        return arr;
    }


    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private void drawLineTo(double xe, double ye) {
        String sb = " L " + xe + " " + ye + " ";
        writer.println(sb);
    }

    private void saveSVG() {
        writer.println("</svg>");
        writer.close();
    }

    private void printFileInfo(String fFile1) {
        Date now = new Date();
        System.out.println("Saved " + fFile1 + " @" + now);
    }
}
