package com.op.scratch3d;

import com.owens.oobjloader.builder.VertexGeometric;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;

public class SvgDrawer {

    int numSvgs = 0;
    int svgFileLimit = 2000;
    boolean splitSVG = false;
    PrintWriter writer;
    private int svgFileCount = 1;
    private String opDir = "";
    private String src = "";
    private double w = 0;
    private double h = 0;
    public ArrayList<ScratchArc> allScratches = new ArrayList<ScratchArc>();
    boolean square;
    boolean circle;
    double sqOff = 50;

    double seconds = 0;
    double writeSpeed = 0.25; //25%
    double ppmm = (787.0 / 300.0);
    double pixPerSec = 3 * ppmm / writeSpeed; //25%
    double secondsTravel = 0.2;

    public SvgDrawer(String opDir, String src, double w, double h) {
        this.opDir = opDir;
        this.src = src;
        this.w = w;
        this.h = h;
    }

    public SvgDrawer(String opDir, String src, double w, double h, double off) {
        this.opDir = opDir;
        this.src = src;
        this.w = w;
        this.h = h;
        this.sqOff = off;
    }

    void drawAllScratches() {
        for (ScratchArc arc : allScratches) {
            if (true) {
                drawArcNumOfSVGs(arc.xc, arc.yc, arc.r, arc.angStart, arc.angStart + arc.angArcDraw);
            }
        }
    }

    void saveArc(double xtl, double ytl, double xc, double yc, double r, double d, int angStart, int angArcDraw) {
        ScratchArc arc = new ScratchArc(xtl, ytl, xc, yc, r, d, angStart, angArcDraw);
        if (!allScratches.contains(arc)) {
            allScratches.add(arc);
        }
    }

    public void drawCircle(double xc, double yc, double rad) {
        String c = "<circle cx=\"" + formatD2(xc) + "\" cy=\"" + formatD2(yc) + "\" r=\"" + formatD2(rad) + "\" />";
        writeToSVG(c);
    }

    void drawAndAddArc(double xc, double yc, double rad, double angSt, double angEn) {
        StringBuffer sb = new StringBuffer();
        sb.append(addArc(xc, yc, rad, angSt, angEn));
        writeToSVG(sb);
    }

    void drawAndAddArc(double xc, double yc, double rad, int largeArcFlag, int sweepFlag, double angSt, double angEn) {
        StringBuffer sb = new StringBuffer();
        sb.append(addArc(xc, yc, rad, largeArcFlag, sweepFlag, angSt, angEn));
        writeToSVG(sb);
    }

    void drawArcNumOfSVGs(double xc, double yc, double rad, double angSt, double angEn) {
        // <path d="M10 10 C 20 20, 40 20, 50 10" stroke="black"
        // fill="transparent"/>

        StringBuffer sb = new StringBuffer();
        sb.append(addArc(xc, yc, rad, angSt, angEn));
        // double totalAngle = ((angSt + angEn) / 2) % 360;
        // String col = totalAngle >= 180 ? "red" : "blue";
        writeToSVG(sb);
        split();
        System.out.println("numSvgs = " + numSvgs);
    }

    private void split() {
        if (splitSVG) {
            if (numSvgs % svgFileLimit == 0) {
                if (numSvgs != 0) {
                    endSVG();
                    svgFileCount++;
                }
                startSVG(square, circle);
            }
        }
    }

    void writeToSVG(StringBuffer sb) {
        numSvgs++;
        writer.println(sb.toString());
    }

    void writeToSVG(String s) {
        numSvgs++;
        writer.println(s);
        split();
    }

    public void startSVG(boolean square, boolean circle) {
        startSVG(square, circle, 1, 0.5, 0.125);
    }

    public void startSimpleSVG() {
        try {
            writer = new PrintWriter(opDir + src + "_" + svgFileCount + ".svg", "UTF-8");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        writer.println("<svg width=\"" + ((int) w) + "\" height=\"" + ((int) (h))
                + "\" xmlns=\"http://www.w3.org/2000/svg\">");
        writer.println("");
        writer.println("<path d=\"");
        int sq = 1;
        int ddx = (int) (w - sq);
        int ddy = (int) (h - sq);
        double sq2 = sq * 2;
        writer.println("M" + sq + " " + sq2 + " L" + sq + " " + sq + " L" + sq2 + " " + sq);
        writer.println("M" + (ddx - sq) + " " + sq + " L" + ddx + " " + sq + " L" + ddx + " " + sq2);

        writer.println("M" + sq + " " + (ddy - sq) + " L" + sq + " " + ddy + " L" + sq2 + " " + ddy);
        writer.println("M" + (ddx - sq) + " " + ddy + " L" + ddx + " " + ddy + " L" + ddx + " " + (ddy - sq));
    }

    void startSVG(boolean square, boolean circle, int times, double radFIn, double radFOut) {
        this.square = square;
        this.circle = circle;
        if (splitSVG) {
            // return;
        }
        try {
            writer = new PrintWriter(opDir + src + "_" + svgFileCount + ".svg", "UTF-8");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        writer.println("<svg width=\"" + ((int) w) + "\" height=\"" + ((int) (h))
                + "\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:inkscape=\"http://www.inkscape.org/namespaces/inkscape\">");
        writer.println("");
        int dd = (int) (w - sqOff);
        // writer.println("<rect x=\"" + rad + "\" y=\"" + rad + "\" width=\"" +
        // (w - rad * 2) + "\" height=\""
        // + (h - rad * 2) + "\" rx=\"" + rad + "\" ry=\"" + rad + "\"
        // stroke=\"blue\" fill=\"none\"/>");
        writer.println("<g style=\"display:inline\" inkscape:label=\"" + 0 + "\" id=\"" + 0 + "-layer\" " +
                "inkscape:groupmode=\"layer\">");
        writer.println("<path id=\"path0\" d=\"");
        if (square) {
//            double sqOff2 = sqOff * 2;
//            writer.println("M" + sqOff + " " + sqOff2 + " L" + sqOff + " " + sqOff + " L" + sqOff2 + " " + sqOff);
//            writer.println("M" + (dd - sqOff) + " " + sqOff + " L" + dd + " " + sqOff + " L" + dd + " " + sqOff2);
//
//            writer.println("M" + sqOff + " " + (dd - sqOff) + " L" + sqOff + " " + (dd) + " L" + sqOff2 + " " + dd);
//            writer.println("M" + (dd - sqOff) + " " + dd + " L" + dd + " " + dd + " L" + (dd) + " " + (dd - sqOff));

            dd = (int) w;
            writer.println("M" + 0 + " " + sqOff + " L" + 0 + " " + 0 + " L" + sqOff + " " + 0);
            writer.println("M" + (dd - 0) + " " + 0 + " L" + dd + " " + 0 + " L" + dd + " " + sqOff);

            writer.println("M" + 0 + " " + (dd - 0) + " L" + 0 + " " + (dd) + " L" + sqOff + " " + dd);
            writer.println("M" + (dd - sqOff) + " " + dd + " L" + dd + " " + dd + " L" + (dd) + " " + (dd - sqOff));
        }
        if (circle) {
            for (int i = 0; i < times; i++) {
                writer.println(addCircleD2((int) (w / 2), (int) (h / 2), (int) (w * radFIn), 0, 359));
                writer.println(addCircleD2((int) (w / 2), (int) (h / 2), (int) (w * radFOut), 0, 359));
            }
        }
        endSVGPath("black");

    }

    public void endSimpleSVG() {
        String col = "blue";
        writer.println("\" stroke=\"" + col + "\" fill=\"none\" />");
        writer.println("</svg>");
        writer.close();
        System.out.println("saved svg: " + opDir + src + "_" + svgFileCount + ".svg numSvgs=" + numSvgs);
    }

    public void startSVGPath(int svgNum) {
        writer.println("<g style=\"display:inline\" inkscape:label=\"" + (svgNum + 1) + "\" id=\"" + (svgNum + 1) + "-layer\" " +
                "inkscape:groupmode=\"layer\">");
        writer.println("<path id=\"path" + (svgNum + 1) + "\" d=\"");

    }

    public void endSVGPath() {
        String col = "blue";
        endSVGPath(col);

    }

    public void endSVGPath(String col) {
        writer.println("\" stroke=\"" + col + "\" fill=\"none\" />");
        writer.print("</g>");
        writer.print("");
    }

    public void endSVG() {
        writer.println("</svg>");
        writer.close();
        System.out.println("saved svg: " + opDir + src + "_" + svgFileCount + ".svg numSvgs=" + numSvgs);
        System.out.println("estimated seconds: " + ((int) seconds));
    }

    VertexGeometric polarToCartesian(double centerX, double centerY, double radius, double angleInDegrees) {
        double angleInRadians = Math.toRadians(angleInDegrees);
        float x = (float) (centerX + (radius * Math.cos(angleInRadians)));
        float y = (float) (centerY + (radius * Math.sin(angleInRadians))); // -

        return new VertexGeometric(x, y, 0);
    }

    VertexGeometric polarToCartesian(double centerX, double centerY, double radiusX, double radiusY, double angleInDegrees) {
        double angleInRadians = Math.toRadians(angleInDegrees);
        float radius = (float) ((radiusX * radiusY) / Math.sqrt((radiusX * radiusX) * Math.sin(angleInRadians) * Math.sin(angleInRadians)
                + (radiusY * radiusY) * Math.cos(angleInRadians) * Math.cos(angleInRadians)));
        float x = (float) (centerX + (radiusY * Math.cos(angleInRadians)));
        float y = (float) (centerY + (radiusY * Math.sin(angleInRadians))); // -

        return new VertexGeometric(x, y, 0);
    }

    String addArc(double cx, double cy, double radius, double startAngle, double endAngle) {

        VertexGeometric start = polarToCartesian(cx, cy, radius, startAngle);
        VertexGeometric end = polarToCartesian(cx, cy, radius, endAngle);

        String largeArcFlag = Math.abs(endAngle - startAngle) <= 180 ? "0" : "1";

        String d = "M" + formatD(start.x) + " " + formatD(start.y) + " A " + formatD(radius) + " " + formatD(radius)
                + " 0" + " " + largeArcFlag + " " + "0" + " " + formatD(end.x) + " " + formatD(end.y);

        if ((int) start.x == 38 && (int) start.y == 956) {
            boolean a = false;
        }

        calculateSeconds(radius, Math.abs(startAngle - endAngle));
        return d;
    }

    String addArc(double cx, double cy, double radius, int largeArcFlag, int sweepFlag, double startAngle, double endAngle) {

        VertexGeometric start = polarToCartesian(cx, cy, radius, startAngle);
        VertexGeometric end = polarToCartesian(cx, cy, radius, endAngle);

        String d = "M" + formatD(start.x) + " " + formatD(start.y) + " A " + formatD(radius) + " " + formatD(radius)
                + " 0" + " " + largeArcFlag + " " + sweepFlag + " " + formatD(end.x) + " " + formatD(end.y);

        if ((int) start.x == 38 && (int) start.y == 956) {
            boolean a = false;
        }

        calculateSeconds(radius, Math.abs(startAngle - endAngle));
        return d;
    }

    private void calculateSeconds(double rad, double ext) {
        double c = 2 * Math.PI * rad;
        double angF = ((double) ext) / 360.0;
        double distPixels = (c * angF);
        seconds = seconds + (distPixels / pixPerSec) + secondsTravel;
    }


    String addWeightedLines(double cx, double cy, double radius, double startAngle, double endAngle) {

        double midAngle = startAngle + ((endAngle - startAngle) / 2);
        VertexGeometric mid = polarToCartesian(cx, cy, radius, midAngle);
        double alpha = endAngle - midAngle;

        double scratchLen = radius * Math.tan(Math.toRadians(alpha));

        double dx10 = mid.x + scratchLen * Math.cos(Math.toRadians(90 + midAngle));
        double dy10 = mid.y + scratchLen * Math.sin(Math.toRadians(90 + midAngle));
        double dx20 = mid.x + scratchLen * Math.cos(Math.toRadians(midAngle - 90));
        double dy20 = mid.y + scratchLen * Math.sin(Math.toRadians(midAngle - 90));
        String d = "M" + formatD2(dx10) + " " + formatD2(dy10) + " L " + formatD2(dx20) + " " + formatD2(dy20);

        double c30 = Math.cos(Math.toRadians(30));
        double dx130 = mid.x + c30 * scratchLen * Math.cos(Math.toRadians(90 + midAngle));
        double dy130 = mid.y + c30 * scratchLen * Math.sin(Math.toRadians(90 + midAngle));
        double dx230 = mid.x + c30 * scratchLen * Math.cos(Math.toRadians(midAngle - 90));
        double dy230 = mid.y + c30 * scratchLen * Math.sin(Math.toRadians(midAngle - 90));
        d = d + " M" + formatD2(dx130) + " " + formatD2(dy130) + " L " + formatD2(dx230) + " " + formatD2(dy230);

        double c60 = Math.cos(Math.toRadians(60));
        double dx160 = mid.x + c60 * scratchLen * Math.cos(Math.toRadians(90 + midAngle));
        double dy160 = mid.y + c60 * scratchLen * Math.sin(Math.toRadians(90 + midAngle));
        double dx260 = mid.x + c60 * scratchLen * Math.cos(Math.toRadians(midAngle - 90));
        double dy260 = mid.y + c60 * scratchLen * Math.sin(Math.toRadians(midAngle - 90));
        d = d + " M" + formatD2(dx160) + " " + formatD2(dy160) + " L " + formatD2(dx260) + " " + formatD2(dy260);

        return d;
    }

    String addLines(double cx, double cy, double radius, double startAngle, double endAngle) {

        double midAngle = startAngle + ((endAngle - startAngle) / 2);
        VertexGeometric mid = polarToCartesian(cx, cy, radius, midAngle);
        double alpha = endAngle - midAngle;

        double scratchLen = radius * Math.tan(Math.toRadians(alpha));

        double dx10 = mid.x + scratchLen * Math.cos(Math.toRadians(90 + midAngle));
        double dy10 = mid.y + scratchLen * Math.sin(Math.toRadians(90 + midAngle));
        double dx20 = mid.x + scratchLen * Math.cos(Math.toRadians(midAngle - 90));
        double dy20 = mid.y + scratchLen * Math.sin(Math.toRadians(midAngle - 90));
        String d = "M" + formatD2(dx10) + " " + formatD2(dy10) + " L " + formatD2(dx20) + " " + formatD2(dy20);

        return d;
    }

    String addEllipse(double cx, double cy, double radiusX, double radiusY, double rotAng) {

        String d = "";
        for (double a = 0; a <= 360; a++) {
            double x = radiusX * Math.cos(Math.toRadians(a));
            double y = radiusY * Math.sin(Math.toRadians(a));

            double r = Math.sqrt(x * x + y * y);
            double ang = Math.atan2(y, x);

            double rx = cx + r * Math.cos(ang - Math.toRadians(rotAng));
            double ry = cy + r * Math.sin(ang - Math.toRadians(rotAng));
            if (a == 0) {
                d = "M " + formatD2(rx) + " " + formatD2(ry) + " ";
            } else {
                d = d + "L " + formatD2(rx) + " " + formatD2(ry) + " ";
            }
        }
        return d;
    }

    String addLine(boolean first, double x, double y) {

        String d = "";
        if (first) {
            d = "M " + formatD2(x) + " " + formatD2(y) + " ";
        } else {
            d = d + "L " + formatD2(x) + " " + formatD2(y) + " ";
        }
        return d;
    }

    String addEllipseOLD(double cx, double cy, double radiusX, double radiusY, double rotAng) {

        double dx = radiusX * Math.cos(Math.toRadians(rotAng));
        double dy = radiusX * Math.sin(Math.toRadians(rotAng));
        VertexGeometric start = polarToCartesian(cx, cy, radiusX, radiusY, 90 - rotAng);
        VertexGeometric end = polarToCartesian(cx, cy, radiusX, radiusY, 90 + 345.99 - rotAng);

        String d = "M" + formatD2(start.x) + " " + formatD2(start.y) + " A " + formatD2(radiusX) + " " + formatD2(radiusY)
                + " " + formatD2(-rotAng) + " 1 1 " + formatD2(end.x) + " " + formatD2(end.y);

        return d;
    }

    boolean isClipped(double cx, double cy, double radius, double startAngle) {

        VertexGeometric start = polarToCartesian(cx, cy, radius, startAngle);

        double x1 = start.x;
        double y1 = start.y;
        double d1 = (sqOff);
        double d2 = (w - sqOff);

        if (x1 < d1 || x1 > d2 || y1 < d1 || y1 > d2) {
            // System.out.println("x,y=" + start.x + "," + start.y + " cx=" + cx
            // + " cy=" + cy + " r=" + radius + " s="
            // + (startAngle % 360));
            return true;
        }
        return false;
    }

    public void writeCircle(double cx, double cy, double radius) {
        writer.println(addCircle(cx, cy, radius));
        numSvgs++;
    }

    public void writeCircleD2(double cx, double cy, double radius) {
        writer.println(addCircleD2(cx, cy, radius));
        numSvgs++;
    }

    void writeLine(double x, double y, double x2, double y2) {
        writer.println(addLine(x, y, x2, y2));
        numSvgs++;
    }

    public String addCircle(double cx, double cy, double radius) {
        String largeArc = " 1 ";
        double endAng = 359.99;
        VertexGeometric start = polarToCartesian(cx, cy, radius, endAng);
        VertexGeometric end = polarToCartesian(cx, cy, radius, 0);
        String d = "M" + formatD(start.x) + " " + formatD(start.y) + " A " + formatD(radius) + " " + formatD(radius)
                + " 0" + largeArc + "0 " + formatD(end.x) + " " + formatD(end.y);
        return d;
    }

    public String addCircleD2(double cx, double cy, double radius) {
        String largeArc = " 1 ";
        double endAng = 360;
        VertexGeometric start = polarToCartesian(cx, cy, radius, endAng);
        VertexGeometric end = polarToCartesian(cx, cy, radius, 0);
        String d = "M" + formatD2(start.x) + " " + formatD2(start.y) + " A " + formatD2(radius) + " " + formatD2(radius)
                + " 1" + largeArc + "0 " + formatD2(end.x) + " " + formatD2(end.y);
        return d;
    }

    String addCircleD2(double cx, double cy, double radius, double startAng, double endAng) {
        String largeArc = Math.abs(endAng - startAng) <= 180 ? " 0" : " 1";
        String sweep = " 0 "; //endAng > startAng ? " 0 " : " 1 ";
        VertexGeometric start = polarToCartesian(cx, cy, radius, endAng);
        VertexGeometric end = polarToCartesian(cx, cy, radius, startAng);
        String d = "M" + formatD2(start.x) + " " + formatD2(start.y) + " A " + formatD2(radius) + " " + formatD2(radius)
                + " 1" + largeArc + sweep + formatD2(end.x) + " " + formatD2(end.y);
        return d;
    }

    double[] getCircleD2(double cx, double cy, double radius, double startAng, double endAng) {
        VertexGeometric mid = polarToCartesian(cx, cy, radius, (startAng + endAng) / 2);
        double[] arr = {formatD2(mid.x), formatD2(mid.y)};
        return arr;
    }

    String addLine(double x1, double y1, double x2, double y2) {
        String d = "M" + formatD(x1) + " " + formatD(y1) + " L " + formatD(x2) + " " + formatD(y2) + " ";
        return d;
    }

    String moveTo(double x1, double y1) {
        String d = "M" + formatD(x1) + " " + formatD(y1);
        return d;
    }

    String lineTo(double x2, double y2) {
        String d = " L" + formatD(x2) + " " + formatD(y2) + " ";
        return d;
    }

    private int formatD(double d) {
        return (int) d;
        // return new BigDecimal(d).setScale(0,
        // BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private double formatD2(double d) {
        return new BigDecimal(d).setScale(2,
                BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
