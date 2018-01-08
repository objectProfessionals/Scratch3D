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

public class PlotLeds extends Base {
    private static final String opFileName = "leds";
    private String opDir = hostDir + "output/";
    private static PlotLeds tester;
    private WaveFileReader reader;
    private double dpi = 90;
    private double mm2in = 25.4;
    private double wmm = 10;
    private double ledmm = 5.00;
    private double offxmm = 2;
    private double num = 10;//28;
    private double hmm = ledmm * (num+1);
    private double w = (dpi * (wmm / mm2in));
    private double h = (dpi * (hmm / mm2in));
    private double led = (dpi * (ledmm / mm2in));
    private PrintWriter writer;

    public static void main(String[] args) throws Exception, FontFormatException {
        tester = new PlotLeds();
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

        drawOutline();
        drawCuts();
        //drawCut();
        writer.println("\" stroke=\"black\" fill=\"none\" />");
    }

    private void drawCut() {
        double offx = (dpi * (offxmm / mm2in));
        double offy = led / 2.0;

        drawLine(offx, -offy, offx, offy);

        for (double i = 0; i < num/2; i++) {
            double d = i * led * 2;
            addLine(offx, offy + d + led / 2);
            addLine(w - offx, offy + d + led / 2);
            addLine(w - offx, offy + d + 3 * led / 2);
            addLine(offx, offy + d + 3 * led / 2);
        }

        addLine(offx, offy  + num*4 * led / 2);
    }

    private void drawCuts() {
        double offx = (dpi * (offxmm / mm2in));
        double offy = led / 2.0;

        drawLine(offx, -offy, offx, offy);

        double num = (double) h / (led*2);
        for (double i = 0; i < num; i++) {
            double d = i * led * 2;
            addLine(offx, offy + d + led / 2);
            addLine(w - offx, offy + d + led / 2);
            addLine(w - offx, offy + d + 3 * led / 2);
            addLine(offx, offy + d + 3 * led / 2);
        }
        double d = num * led * 2;
        addLine(offx, offy + d + led / 2);

    }

    private void drawOutline() {
        drawLine(0, 0, w, 0);
        addLine(w, h);
        addLine(0, h);
        addLine(0, 0);
    }

    private void drawLine(double xs, double ys, double xe, double ye) {
        String sb = "M " + round(xs, 2) + " " + round(ys, 2) + " L " + round(xe, 2) + " " + round(ye, 2) + " ";
        writer.println(sb);
    }

    private void addLine(double xe, double ye) {
        String sb = " L " + round(xe, 2) + " " + round(ye, 2) + " ";
        writer.println(sb);
    }

    private void drawArc(double cx, double cy, double radius, double as, double ae, int large, int sweep) {

        double[] start = polarToCartesian(cx, cy, radius, as);
        double[] end = polarToCartesian(cx, cy, radius, ae);
        String d = "M" + round(start[0], 2) + " " + round(start[1], 2) + " A " + round(radius, 2) + " " + round(radius, 2)
                + " 0 " + large + " " + sweep + " " + round(end[0], 2) + " " + round(end[1], 2);
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
