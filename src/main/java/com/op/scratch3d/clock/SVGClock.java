package com.op.scratch3d.clock;

import com.op.scratch3d.Base;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

public class SVGClock extends Base {
    private static final SVGClock svgClock = new SVGClock();

    String opDir = hostDir + "clock/";
    String file = "clock";
    double mm2in = 25.4;
    int dpi = 50; //300
    double totZIn = 1.5;
    double spindleZIn = 0.65;
    double baseZIn = totZIn - spindleZIn;
    double win = 8;
    double hin = win;
    double w = win * dpi;
    double h = hin * dpi;
    double rad = w / 2;
    double cx = w / 2;
    double cy = h / 2;
    double hoursOutRad = rad * 0.8;
    double hoursInRad = rad * 0.7;
    double hoursRad = rad * 0.075;

    double clockWHin = 2.3;
    double clockCornerRin = 0.2;
    double spindleRin = 0.2;

    double clockWH = dpi * clockWHin;
    double clockCornerR = dpi * clockCornerRin;
    double spindleR = dpi * spindleRin;

    double cardThIn = 0.04;
    int totSheets = (int) (totZIn / cardThIn);

    PrintWriter writer;

    public static void main(String[] args) throws Exception {
        svgClock.draw();
    }

    private void draw() {

        initSVG();
        startSvg();
        for (int svgNum = 0; svgNum < totSheets; svgNum++) {
            startPath(svgNum);
            double zIn = (double) ((svgNum + 1) * cardThIn);
            addOuter();
            if (totSheets - svgNum <= 12) {
                int firstCutHourNum = (totSheets - svgNum);
                for (double a = 360; a > 0; a = a - 30) {
                    int hourNum = (int) (a / 30);
                    if (hourNum >= firstCutHourNum) {
                        drawHours(a - 90);
                    }
                }
            }

            if (zIn < baseZIn) {
                drawBase(svgNum);
            } else {
                drawSpindle(svgNum);
            }
            endPath();
        }
        endSVG();
    }

    private void drawSpindle(int svgNum) {
        double[] start1 = polarToCartesian(cx, cy, spindleR, 0);
        double[] end1 = polarToCartesian(cx, cy, spindleR, 180);
        String c1 = "M" + formatD(start1[0]) + " " + formatD(start1[1]) + " A " + formatD(clockCornerR) + " " + formatD(clockCornerR)
                + " 0 0 1 " + formatD(end1[0]) + " " + formatD(end1[1]);
        String c2 = "A " + formatD(spindleR) + " " + formatD(spindleR)
                + " 0 0 1 " + formatD(start1[0]) + " " + formatD(start1[1]);

        writer.println(c1);
        writer.println(c2);
    }

    private void drawBase(int svgNum) {
        double ctrX = cx + clockWH / 2 - clockCornerR;
        double ctrY = cy - clockWH / 2 + clockCornerR;
        double[] startTR = polarToCartesian(ctrX, ctrY, clockCornerR, -90);
        double[] endTR = polarToCartesian(ctrX, ctrY, clockCornerR, 0);
        String ctr = "M" + formatD(startTR[0]) + " " + formatD(startTR[1]) + " A " + formatD(clockCornerR) + " " + formatD(clockCornerR)
                + " 0 0 1 " + formatD(endTR[0]) + " " + formatD(endTR[1]);

        double cbrX = cx + clockWH / 2 - clockCornerR;
        double cbrY = cy + clockWH / 2 - clockCornerR;
        double[] startBR = polarToCartesian(cbrX, cbrY, clockCornerR, 0);
        double[] endBR = polarToCartesian(cbrX, cbrY, clockCornerR, 90);
        String er = "L" + formatD(startBR[0]) + " " + formatD(startBR[1]);
        String cbr = "A " + formatD(clockCornerR) + " " + formatD(clockCornerR)
                + " 0 0 1 " + formatD(endBR[0]) + " " + formatD(endBR[1]);

        double cblX = cx - clockWH / 2 + clockCornerR;
        double cblY = cy + clockWH / 2 - clockCornerR;
        double[] startBL = polarToCartesian(cblX, cblY, clockCornerR, 90);
        double[] endBL = polarToCartesian(cblX, cblY, clockCornerR, 180);
        String eb = "L" + formatD(startBL[0]) + " " + formatD(startBL[1]);
        String cbl = "A " + formatD(clockCornerR) + " " + formatD(clockCornerR)
                + " 0 0 1 " + formatD(endBL[0]) + " " + formatD(endBL[1]);

        double ctlX = cx - clockWH / 2 + clockCornerR;
        double ctlY = cy - clockWH / 2 + clockCornerR;
        double[] startTL = polarToCartesian(ctlX, ctlY, clockCornerR, 180);
        double[] endTL = polarToCartesian(ctlX, ctlY, clockCornerR, 270);
        String el = "L" + formatD(startTL[0]) + " " + formatD(startTL[1]);
        String ctl = "A " + formatD(clockCornerR) + " " + formatD(clockCornerR)
                + " 0 0 1 " + formatD(endTL[0]) + " " + formatD(endTL[1]);

        String et = "L" + formatD(startTR[0]) + " " + formatD(startTR[1]);

        writer.println(ctr);
        writer.println(er);
        writer.println(cbr);
        writer.println(eb);
        writer.println(cbl);
        writer.println(el);
        writer.println(ctl);
        writer.println(et);
    }

    private void addOuter() {
        double[] startO = polarToCartesian(cx, cy, rad, 0);
        double[] endO = polarToCartesian(cx, cy, rad, 180);
        String c1 = "M" + formatD(startO[0]) + " " + formatD(startO[1]) + " A " + formatD(hoursRad) + " " + formatD(hoursRad)
                + " 0 0 1 " + formatD(endO[0]) + " " + formatD(endO[1]);
        String c2 = "A " + formatD(hoursRad) + " " + formatD(hoursRad)
                + " 0 0 1 " + formatD(startO[0]) + " " + formatD(startO[1]);

        writer.println(c1);
        writer.println(c2);
    }

    void drawHours(double angle) {
        double angR = Math.toRadians(angle);

        double ox = cx + hoursOutRad * Math.cos(angR);
        double oy = cy + hoursOutRad * Math.sin(angR);

        double startAngle = angle - 90;
        double endAngle = angle + 90;

        double[] startO = polarToCartesian(ox, oy, hoursRad, startAngle);
        double[] endO = polarToCartesian(ox, oy, hoursRad, endAngle);
        String largeArcFlag = Math.abs(endAngle - startAngle) <= 180 ? "0" : "1";
        String oc = "M" + formatD(startO[0]) + " " + formatD(startO[1]) + " A " + formatD(hoursRad) + " " + formatD(hoursRad)
                + " 0" + " " + largeArcFlag + " " + "1" + " " + formatD(endO[0]) + " " + formatD(endO[1]);

        double ix = cx + hoursInRad * Math.cos(angR);
        double iy = cy + hoursInRad * Math.sin(angR);
        double[] startI = polarToCartesian(ix, iy, hoursRad, endAngle);
        double[] endI = polarToCartesian(ix, iy, hoursRad, startAngle);
        String ic = "A " + formatD(hoursRad) + " " + formatD(hoursRad)
                + " 0" + " " + largeArcFlag + " " + "1" + " " + formatD(endI[0]) + " " + formatD(endI[1]);


        String cr = "L" + formatD(startI[0]) + " " + formatD(startI[1]);
        String cl = "L" + formatD(startO[0]) + " " + formatD(startO[1]);

        writer.println(oc);
        writer.println(cr);
        writer.println(ic);
        writer.println(cl);
    }

    double[] polarToCartesian(double centerX, double centerY, double radius, double angleInDegrees) {
        double angleInRadians = Math.toRadians(angleInDegrees);
        double x = (centerX + (radius * Math.cos(angleInRadians)));
        double y = (centerY + (radius * Math.sin(angleInRadians)));

        double arr[] = {x, y};

        return arr;
    }

    public void initSVG() {
        try {
            writer = new PrintWriter(opDir + file + "_WHOLE.svg", "UTF-8");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void startSvg() {
        writer.println("<svg width=\"" + ((int) w) + "\" height=\"" + ((int) (h))
                + "\" xmlns=\"http://www.w3.org/2000/svg\">");
        writer.println("");
    }

    private void startPath(int svgNum) {
        writer.println("<path id=\"" + svgNum + "\" d=\"");
    }

    private void endPath() {
        String col = "blue";
        writer.println("\" stroke=\"" + col + "\" fill=\"none\" />");
    }

    private void addCircle(int cx, int cy, int r) {
        writer.println("<circle cx=\"" + cx + "\" cy=\"" + cy + "\" r=\"" + r + "\" stroke=\"black\" stroke-width=\"1\" fill=\"none\"/>");
    }

    public void endSVG() {
        writer.println("</svg>");
        writer.close();
        System.out.println("saved svg : " + opDir + file + "_WHOLE.svg");
    }

    private double formatD(double d) {
        return new BigDecimal(d).setScale(2,
                BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
