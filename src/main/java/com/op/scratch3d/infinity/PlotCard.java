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
    private double dpi = 90;
    private double radiusmm = 3;
    private double mm2in = 25.4;
    private double wcmm = 85.60;
    private double hcmm = 53.98;
    private double ledmm = 7.00;
    private double batterymm = 15.75;
    private double wc = (dpi * (wcmm / mm2in));
    private double hc = (dpi * (hcmm / mm2in));
    private double radius = (dpi * (radiusmm / mm2in));
    private double led = (dpi * (ledmm / mm2in));
    private double battery = (dpi * (batterymm / mm2in));
    private PrintWriter writer;
    private double numLedsHoriz = 7;
    private double numLedsVert = 5;
    private int numCuts = 1;

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
        writer.println("<svg width=\"" + wc + "\" height=\"" + hc + "\" xmlns=\"http://www.w3.org/2000/svg\">");

    }


    private void drawAll() {
        writer.println("<path d=\"");
        for (int i = 0; i < numCuts; i++) {
            drawCard();

            drawHole(false);
            //drawHoleGaps(true);
        }

        //drawHoleTags(true);

        //drawHoleTags(false);

        writer.println("\" stroke=\"black\" fill=\"none\" />");
    }

    private void drawHoleTags(boolean front) {
        double ledCornerD = Math.sin(Math.PI / 4) * led;
        double yBorder = (hc - (numLedsVert * led) - (2 * ledCornerD)) / 2;
        double xBorder = yBorder;

        double tagLenmm = 5;
        double tagLen = (dpi * (tagLenmm / mm2in));
        double tagWidmm = 2.5;
        double tagWid = (dpi * (tagWidmm / mm2in));
        double tagGap = led - tagWid;

        double tagWid45x = Math.cos(Math.PI / 4) * tagWid;
        double tagWid45y = Math.sin(Math.PI / 4) * tagWid;
        double tagLen45x = Math.cos(Math.PI / 4) * tagLen;
        double tagLen45y = Math.sin(Math.PI / 4) * tagLen;
        double tagGap45x = Math.cos(Math.PI / 4) * (led - tagWid);
        double tagGap45y = Math.sin(Math.PI / 4) * (led - tagWid);

        if (front) {
            drawLine(xBorder, yBorder + ledCornerD, xBorder + tagLen45x, yBorder + ledCornerD + tagLen45y);
            addLine(xBorder + tagLen45x + tagWid45x, yBorder + ledCornerD + tagLen45y - tagWid45y);
            addLine(xBorder + tagWid45x, yBorder + ledCornerD - tagWid45y);
            addLine(xBorder + tagWid45x, yBorder + ledCornerD - tagWid45y);
            addLine(xBorder + ledCornerD, yBorder);
            for (double i = 0; i < numLedsHoriz; i++) {
                //addLedLine(xBorder + ledCornerD + i * led, yBorder);
                addLine(xBorder + ledCornerD + i * led, yBorder + tagLen);
                addLine(xBorder + ledCornerD + tagWid + i * led, yBorder + tagLen);
                addLine(xBorder + ledCornerD + tagWid + i * led, yBorder);
                addLine(xBorder + ledCornerD + tagWid + i * led, yBorder);
                addLine(xBorder + ledCornerD + (i + 1) * led, yBorder);
            }
            addLine(xBorder + ledCornerD + (numLedsHoriz) * led - tagLen45x, yBorder + tagLen45y);
            addLine(xBorder + ledCornerD + (numLedsHoriz) * led - tagLen45x + tagWid45x, yBorder + tagLen45y + tagWid45y);
            addLine(xBorder + ledCornerD + (numLedsHoriz) * led + tagWid45x, yBorder + tagWid45y);
            for (double i = 0; i < numLedsVert; i++) {
                //addLedLine(xBorder + 2 * ledCornerD + (numLedsHoriz) * led, yBorder + ledCornerD + i * led);
                addLine(xBorder + 2 * ledCornerD + (numLedsHoriz) * led, yBorder + ledCornerD + i * led);
                addLine(xBorder + 2 * ledCornerD + (numLedsHoriz) * led - tagLen, yBorder + ledCornerD + i * led);
                addLine(xBorder + 2 * ledCornerD + (numLedsHoriz) * led - tagLen, yBorder + ledCornerD + i * led + tagWid);
                addLine(xBorder + 2 * ledCornerD + (numLedsHoriz) * led, yBorder + ledCornerD + i * led + tagWid);
                addLine(xBorder + 2 * ledCornerD + (numLedsHoriz) * led, yBorder + ledCornerD + (i + 1) * led);
            }
            addLine(xBorder + 2 * ledCornerD + (numLedsHoriz) * led - tagLen45x, yBorder + ledCornerD + ((numLedsVert) * led) - tagLen45y);
            addLine(xBorder + 2 * ledCornerD + (numLedsHoriz) * led - tagLen45x - tagWid45x, yBorder + ledCornerD + ((numLedsVert) * led) - tagLen45y + tagWid45y);
            addLine(xBorder + 2 * ledCornerD + (numLedsHoriz) * led - tagWid45x, yBorder + ledCornerD + ((numLedsVert) * led) + tagWid45y);
            for (double i = numLedsHoriz; i > 0; i--) {
                addLine(xBorder + ledCornerD + i * led, yBorder + 2 * ledCornerD + ((numLedsVert) * led));
                addLine(xBorder + ledCornerD + i * led, yBorder + 2 * ledCornerD + ((numLedsVert) * led) - tagLen);
                addLine(xBorder + ledCornerD + i * led - tagWid, yBorder + 2 * ledCornerD + ((numLedsVert) * led) - tagLen);
                addLine(xBorder + ledCornerD + i * led - tagWid, yBorder + 2 * ledCornerD + ((numLedsVert) * led));
                addLine(xBorder + ledCornerD + (i - 1) * led, yBorder + 2 * ledCornerD + ((numLedsVert) * led));
            }
            addLine(xBorder + ledCornerD + tagLen45x, yBorder + 2 * ledCornerD + ((numLedsVert) * led) - tagLen45y);
            addLine(xBorder + ledCornerD + tagLen45x - tagWid45x, yBorder + 2 * ledCornerD + ((numLedsVert) * led) - tagLen45y - tagWid45y);
            addLine(xBorder + ledCornerD - tagWid45x, yBorder + 2 * ledCornerD + ((numLedsVert) * led) - tagWid45y);
            for (double i = numLedsVert; i > 0; i--) {
                addLine(xBorder, yBorder + ledCornerD + i * led);
                addLine(xBorder + tagLen, yBorder + ledCornerD + i * led);
                addLine(xBorder + tagLen, yBorder + ledCornerD + i * led - tagWid);
                addLine(xBorder, yBorder + ledCornerD + i * led - tagWid);
                addLine(xBorder, yBorder + ledCornerD + (i - 1) * led);
            }

        } else {
            drawLine(xBorder, yBorder + ledCornerD, xBorder + tagGap45x, yBorder + ledCornerD - tagGap45y);
            addLine(xBorder + tagGap45x + tagLen45x, yBorder + ledCornerD - tagGap45y + tagLen45y);
            addLine(xBorder + tagGap45x + tagLen45x + tagWid45x, yBorder + ledCornerD - tagGap45y + tagLen45y - tagWid45y);
            addLine(xBorder + ledCornerD, yBorder);
            for (double i = 0; i < numLedsHoriz; i++) {
                //addLedLine(xBorder + ledCornerD + i * led, yBorder);
                addLine(xBorder + ledCornerD + tagGap + i * led, yBorder);
                addLine(xBorder + ledCornerD + tagGap + i * led, yBorder + tagLen);
                addLine(xBorder + ledCornerD + tagGap + tagWid + i * led, yBorder + tagLen);
                addLine(xBorder + ledCornerD + tagGap + tagWid + i * led, yBorder);
                addLine(xBorder + ledCornerD + tagGap + tagWid + i * led, yBorder);
            }
            addLine(xBorder + ledCornerD + (numLedsHoriz) * led + tagGap45x, yBorder + tagGap45y);
            addLine(xBorder + ledCornerD + (numLedsHoriz) * led + tagGap45x - tagLen45x, yBorder + tagGap45y + tagLen45y);
            addLine(xBorder + ledCornerD + (numLedsHoriz) * led + tagGap45x - tagLen45x + tagWid45x, yBorder + tagGap45y + tagLen45y + tagWid45y);
            for (double i = 0; i < numLedsVert; i++) {
                //addLedLine(xBorder + 2 * ledCornerD + (numLedsHoriz) * led, yBorder + ledCornerD + i * led);
                addLine(xBorder + 2 * ledCornerD + (numLedsHoriz) * led, yBorder + ledCornerD + i * led);
                addLine(xBorder + 2 * ledCornerD + (numLedsHoriz) * led, yBorder + ledCornerD + tagGap + i * led);
                addLine(xBorder + 2 * ledCornerD + (numLedsHoriz) * led - tagLen, yBorder + ledCornerD + tagGap + i * led);
                addLine(xBorder + 2 * ledCornerD + (numLedsHoriz) * led - tagLen, yBorder + ledCornerD + tagGap + i * led + tagWid);
                addLine(xBorder + 2 * ledCornerD + (numLedsHoriz) * led, yBorder + ledCornerD + tagGap + i * led + tagWid);
            }
            addLine(xBorder + 2 * ledCornerD + (numLedsHoriz) * led - tagGap45x, yBorder + ledCornerD + ((numLedsVert) * led) + tagGap45y);
            addLine(xBorder + 2 * ledCornerD + (numLedsHoriz) * led - tagGap45x - tagLen45x, yBorder + ledCornerD + ((numLedsVert) * led) + tagGap45y - tagLen45y);
            addLine(xBorder + 2 * ledCornerD + (numLedsHoriz) * led - tagGap45x - tagLen45x - tagWid45x, yBorder + ledCornerD + ((numLedsVert) * led) + tagGap45y - tagLen45y + tagWid45y);
            addLine(xBorder + ledCornerD + (numLedsHoriz) * led, yBorder + 2 * ledCornerD + ((numLedsVert) * led));
            for (double i = numLedsHoriz; i > 0; i--) {
                addLine(xBorder + ledCornerD + i * led - tagGap, yBorder + 2 * ledCornerD + ((numLedsVert) * led));
                addLine(xBorder + ledCornerD + i * led - tagGap, yBorder + 2 * ledCornerD + ((numLedsVert) * led) - tagLen);
                addLine(xBorder + ledCornerD + i * led - tagGap - tagWid, yBorder + 2 * ledCornerD + ((numLedsVert) * led) - tagLen);
                addLine(xBorder + ledCornerD + i * led - tagGap - tagWid, yBorder + 2 * ledCornerD + ((numLedsVert) * led));
            }
            addLine(xBorder + ledCornerD - tagGap45x, yBorder + 2 * ledCornerD + ((numLedsVert) * led) - tagGap45y);
            addLine(xBorder + ledCornerD - tagGap45x + tagLen45x, yBorder + 2 * ledCornerD + ((numLedsVert) * led) - tagGap45y - tagLen45y);
            addLine(xBorder + ledCornerD - tagGap45x + tagLen45x - tagWid45x, yBorder + 2 * ledCornerD + ((numLedsVert) * led) - tagGap45y - tagLen45y - tagWid45y);
            addLine(xBorder + ledCornerD - tagGap45x - tagWid45x, yBorder + 2 * ledCornerD + ((numLedsVert) * led) - tagGap45y - tagWid45y);
            for (double i = numLedsVert; i > 0; i--) {
                addLine(xBorder, yBorder + ledCornerD - tagGap + i * led);
                addLine(xBorder + tagLen, yBorder + ledCornerD - tagGap + i * led);
                addLine(xBorder + tagLen, yBorder + ledCornerD - tagGap + i * led - tagWid);
                addLine(xBorder, yBorder + ledCornerD + i * led - tagGap - tagWid);
            }

        }


        drawAligns();
        drawBattery(xBorder, yBorder);
        drawSwitch();

    }

    private void drawHoleGaps(boolean fix) {
        double ledCornerD = Math.sin(Math.PI / 4) * led;
        double yBorder = (hc - (numLedsVert * led) - (2 * ledCornerD)) / 2;
        double xBorder = yBorder;

        //3mm > 3.5mm
        double tagmm = 3.5;
        double tag = (dpi * (tagmm / mm2in));
        double tagDepmm = 1.5;
        double tagDep = (dpi * (tagDepmm / mm2in));
        double tagGapmm = (ledmm - tagmm) / 2;
        double tagGap = (dpi * (tagGapmm / mm2in));
        double tag45x = Math.cos(Math.PI / 4) * (tag);
        double tag45y = Math.sin(Math.PI / 4) * (tag);
        double tagGap45x = Math.cos(Math.PI / 4) * (tagGap);
        double tagGap45y = Math.sin(Math.PI / 4) * (tagGap);
        double tagDep45x = Math.cos(Math.PI / 4) * (tagDep);
        double tagDep45y = Math.sin(Math.PI / 4) * (tagDep);

        double x = 0;
        double y = 0;
        drawLine(x = xBorder, y = yBorder + ledCornerD, x = x + tagGap45x, y = y - tagGap45y);

        addLine(x = x - tagDep45x, y = y - tagDep45y);
        addLine(x = x + tag45x, y = y - tag45y);
        addLine(x = x + tagDep45x, y = y + tagDep45y);
        addLine(x = x + tagGap45x, y = y - tagGap45y);

        for (double i = 0; i < numLedsHoriz; i++) {
            addLine(x = x + tagGap, y = y);
            addLine(x = x, y = y - tagDep);
            addLine(x = x + tag, y = y);
            addLine(x = x, y = y + tagDep);
            addLine(x = x + tagGap, y = y);
        }
        addLine(x = x + tagGap45x, y = y + tagGap45y);
        addLine(x = x + tagDep45x, y = y - tagDep45y);
        addLine(x = x + tag45x, y = y + tag45y);
        addLine(x = x - tagDep45x, y = y + tagDep45y);
        addLine(x = x + tagGap45x, y = y + tagGap45y);

        for (double i = 0; i < numLedsVert; i++) {
            addLine(x = x, y = y + tagGap);
            addLine(x = x + tagDep, y = y);
            addLine(x = x, y = y + tag);
            addLine(x = x - tagDep, y = y);
            addLine(x = x, y = y + tagGap);
        }
        addLine(x = x - tagGap45x, y = y + tagGap45y);
        addLine(x = x + tagDep45x, y = y + tagDep45y);
        addLine(x = x - tag45x, y = y + tag45y);
        addLine(x = x - tagDep45x, y = y - tagDep45y);
        addLine(x = x - tagGap45x, y = y + tagGap45y);

        for (double i = 0; i < numLedsHoriz; i++) {
            addLine(x = x - tagGap, y = y);
            addLine(x = x, y = y + tagDep);
            addLine(x = x - tag, y = y);
            addLine(x = x, y = y - tagDep);
            addLine(x = x - tagGap, y = y);
        }
        addLine(x = x - tagGap45x, y = y - tagGap45y);
        addLine(x = x - tagDep45x, y = y + tagDep45y);
        addLine(x = x - tag45x, y = y - tag45y);
        addLine(x = x + tagDep45x, y = y - tagDep45y);
        addLine(x = x - tagGap45x, y = y - tagGap45y);

        for (double i = 0; i < numLedsVert; i++) {
            addLine(x = x, y = y - tagGap);
            addLine(x = x - tagDep, y = y);
            addLine(x = x, y = y - tag);
            addLine(x = x + tagDep, y = y);
            addLine(x = x, y = y - tagGap);
        }

        //drawAligns();
        drawBattery(xBorder, yBorder);
        drawSwitch();
    }

    private void drawHole(boolean fix) {
        double ledCornerD = Math.sin(Math.PI / 4) * led;
        double yBorder = (hc - (numLedsVert * led) - (2 * ledCornerD)) / 2;
        double xBorder = yBorder;

        drawLine(xBorder, yBorder + ledCornerD, xBorder + ledCornerD, yBorder);
        for (double i = 0; i <= numLedsHoriz; i++) {
            addLine(xBorder + ledCornerD + i * led, yBorder);
        }

        addLine(xBorder + 2 * ledCornerD + (numLedsHoriz) * led, yBorder + ledCornerD);
        for (double i = 0; i <= numLedsVert; i++) {
            addLine(xBorder + 2 * ledCornerD + (numLedsHoriz) * led, yBorder + ledCornerD + i * led);
        }

        addLine(xBorder + ledCornerD + (numLedsHoriz) * led, yBorder + 2 * ledCornerD + ((numLedsVert) * led));
        for (double i = numLedsHoriz - 1; i >= 0; i--) {
            addLine(xBorder + ledCornerD + i * led, yBorder + 2 * ledCornerD + ((numLedsVert) * led));
        }

        addLine(xBorder, yBorder + ledCornerD + ((numLedsVert) * led));
        for (double i = numLedsVert - 1; i >= 0; i--) {
            addLine(xBorder, yBorder + ledCornerD + i * led);
        }

        if (fix) {
            drawAligns();
        }

        drawBattery(xBorder, yBorder);
        drawSwitch();

    }

    private void drawAligns() {
        double alignRad = radius / 2;
        double off = radius * 1.25;
        double as = 180;
        double ae = -179;
        drawArc(off, off, alignRad, as, ae, 1, 0);
        drawArc(wc - off, off, alignRad, as, ae, 1, 0);
        drawArc(wc - off, hc - off, alignRad, as, ae, 1, 0);
        drawArc(off, hc - off, alignRad, as, ae, 1, 0);
    }

    private void drawSwitch() {
        double bwmm = 4.1;
        double bhmm = 6.7;
        double bdmm = 1.9;

        double bw = (dpi * (bwmm / mm2in));
        double bh = (dpi * (bhmm / mm2in));
        double bd = (dpi * (bdmm / mm2in));

        double offy = hc * 0.25;
        drawLine(wc, offy, wc - bw, offy);
        addLine(wc - bw, bh + offy);
        addLine(wc, bh + offy);
    }

    private void drawBattery(double xBorder, double yBorder) {

        double rad = battery / 2;
        //double midx = xBorder*2 + holew +(wc - xBorder*2 - holew)/2;
        double midx = wc - rad;
        double midy = hc - yBorder * 2 - rad;
        double angOverDeg = 10;
        double angs = Math.toRadians(90 - angOverDeg);

        drawLine(wc, midy - rad * Math.sin(angs), midx + rad * Math.cos(angs), midy - rad * Math.sin(angs));
        addArc(midx, midy, rad, 90 - angOverDeg, 1, 0);
        addLine(wc, midy + rad * Math.sin(angs));
        //addLedLine(wc, midy + rad * Math.sin(ange));
    }


    private void drawCard() {
        drawLine(radius, 0, wc - radius, 0);
        addArc(wc - radius, radius, radius, 0, 0, 1);
        addLine(wc, hc - radius);
        addArc(wc - radius, hc - radius, radius, 90, 0, 1);
        addLine(radius, hc);
        addArc(radius, hc - radius, radius, 180, 0, 1);
        addLine(0, radius);
        addArc(radius, radius, radius, 270, 0, 1);
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

    private void addArc(double cx, double cy, double radius, double ae, int large, int sweep) {
        double[] end = polarToCartesian(cx, cy, radius, ae);
        String d = " A " + round(radius, 2) + " " + round(radius, 2)
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
