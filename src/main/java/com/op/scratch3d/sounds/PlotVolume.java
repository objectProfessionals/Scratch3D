package com.op.scratch3d.sounds;

import com.op.scratch3d.Base;
import org.apache.batik.ext.awt.geom.PathLength;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static java.lang.Math.abs;

public class PlotVolume extends Base {
    private static final String SPIRAL_LINE = "SpiralLine";
    private static final String SPIRAL_LINE_SCORE = "SpiralLineScore";
    private static final String SPIRAL = "Spiral";
    private static final String CIRCLE = "circle";
    private static final String LINEAR = "linear";
    private static final String LINEAR_SCORE = "score";
    private static final String LINEAR_MULTI_SCORE = "mscore";
    private static final String LINEAR_CUTS = "cuts";
    private static final String typeName = LINEAR_MULTI_SCORE;
    //private static final String scName = "ILoveYouLow500Hz"; // "hello20";
    private static final String scName = "ILoveYouMax1"; // "hello20";
    //private static final String scName = "ILoveYou20"; // "hello20";
    //private static final String scName = "sine100-500-0.3s"; // "hello20";
    private static final String opFileName = typeName + scName; // "hello20";
    private String opDir = hostDir + "output/";
    private String ipDir = hostDir + "sounds/";
    private static final String outFileExt = ".png";
    private float fontSize = 3;
    private double volAmp = 1;
    private static PlotVolume tester;
    private WaveFileReader reader;
    //private double dpi = 300;
    private double dpi = 90;
    private double mm2in = 25.4;
    //    private double wmm = 200;
//    private double hmm = 200;
    private double wmm = 10;//LINEAR
    private double hmm = 300;
    private int w = (int) (dpi * (wmm / mm2in));
    private int h = (int) (dpi * (hmm / mm2in));
    private int cx = w / 2;
    private int cy = h / 2;
    private boolean addBorder = true;
    private double bordermm = 20;
    private double border = (dpi * (bordermm / mm2in));
    private double max = -1;
    private double min = 1000000;
    private PrintWriter writer;
    private String cut = "";
    private String cutO = "";

    public static void main(String[] args) throws Exception, FontFormatException {
        tester = new PlotVolume();
        tester.setupWholeImage();
        tester.createWavData();
        tester.drawVolume();
        tester.saveSVG();
    }

    protected void setupWholeImage() throws IOException {
        System.out.println("Creating...");
        String src = opDir;

        writer = new PrintWriter(src + opFileName + ".svg", "UTF-8");
        writer.println("<svg width=\"" + w + "\" height=\"" + h + "\" xmlns=\"http://www.w3.org/2000/svg\">");

    }

    private void createWavData() {
        System.out.println("reading " + scName);
        String src = ipDir;
        reader = new WaveFileReader(src + scName + ".wav");
        for (int i = 0; i < reader.getData()[0].length; i++) {
            double r = reader.getData()[0][i];
            r = getVolume(r);
            if (r > max) {
                max = r;
            }
            if (r < min) {
                min = r;
            }
        }
        System.out.println(min + ":" + max);
    }

    private void drawVolume() {
        System.out.println("Drawing " + scName + "...");
        int end = (int) (reader.getDataLen());
        ArrayList<Point2D> p = new ArrayList<Point2D>();
        for (int i = 1; i < end - 1; i = i + 1) {
            //p.add(new Point2D.Double(i, getAverage(i)));
            p.add(new Point2D.Double(i, getPeaksVolume(i)));
        }
        if (typeName.equals(SPIRAL)) {
            drawAsSpiral(p);
        } else if (typeName.equals(SPIRAL_LINE)) {
            drawAsSpiralLine(p);
        } else if (typeName.equals(SPIRAL_LINE_SCORE)) {
            drawAsSpiralLineScore(p);
        } else if (typeName.equals(CIRCLE)) {
            drawAsCircle(p);
        } else if (typeName.equals(LINEAR)) {
            //drawAsLinear(p);
            drawAsLineSVG(p);
        } else if (typeName.equals(LINEAR_CUTS)) {
            drawAsLineCutsSVG(p);
        } else if (typeName.equals(LINEAR_SCORE)) {
            drawAsLinearScore(p);
        } else if (typeName.equals(LINEAR_MULTI_SCORE)) {
            drawAsLinearMultiScore(p);
        }
    }

    private void drawAsLinearScore(ArrayList<Point2D> p) {
        writer.println("<path d=\"");
        double hf = ((double) h) / ((double) (p.size()));
        drawLine(0, 0, w, 0);

        double inc = 2;
        int count = 0;
        for (double i = 1; i < p.size(); i = i + 2) {
            Point2D p1 = p.get((int) i);
            double vol = p1.getY();
            double volf = (-min + vol) / (max - min);

            double y = i * hf;
            double dd = 10;

            int num = (int) ((1 - volf) * dd) - (int) (dd / 2);
            num = num > 0 ? num : 0;
            count = count + num;
            System.out.println("num=" + num);
            for (int j = 0; j < num; j++) {
                drawLine(0, y, w, y);
            }
        }

        System.out.println("count=" + count);
        writer.println("\" stroke=\"black\" fill=\"none\" />");
    }

    private void drawAsLinearMultiScore(ArrayList<Point2D> p) {
        int bits = 8;
        double inc = 2;
        double hf = ((double) h) / ((double) (p.size()));
        double strmm = 0.25;
        double str = (dpi * (strmm / mm2in));
        int allcount = 0;
        for (int n = 1; n <= bits; n++) {
            writer.println("<path id=\"path" + n + "\" d=\"");
            drawLine(0, 0, w, 0);

            int count = 0;
            for (double i = 1; i < p.size(); i = i + inc) {
                Point2D p1 = p.get((int) i);
                double vol = p1.getY();
                double volf = (-min + vol) / (max - min);
                double ampl = Math.abs(volf - 0.5);
                double y = i * hf;
                double dd = 1 + bits * 2;

                int num = (int) ((ampl) * dd);
                //num = num > 0 ? num : 0;
                if (num == n) {
                    double l = (bits-n)*0.5;
                    count = count + num;
                    System.out.println("num=" + num + ", y=" + y);
                    drawLine(l, y, w-(2*l), y);
                } else {
                    //System.out.println("****num=" + num);
                }
            }

            drawLine(0, h, w, h);

            double op = ((double) n) / ((double) bits);
            System.out.println("count(" + n + ")=" + count);
            writer.println("\" stroke=\"black\" stroke-width=\"" + str + "\" stroke-opacity=\"" + op + "\" fill=\"none\" />");
            allcount = allcount + count;
        }
        System.out.println("allcount=" + allcount);
    }

    private void drawAsLineSVG(ArrayList<Point2D> p) {
        writer.println("<path d=\"");
        double amplHeightmm = 0.5; //25;
        double amplHeight = (dpi * (amplHeightmm / mm2in));

        double widthmm = (wmm - amplHeightmm * 2) * 0.9;
        double width = (dpi * (widthmm / mm2in));
        double edge = width;

        double hf = ((double) h) / ((double) (p.size()));
        drawLine(w, 0, w, 0);

        double inc = 2;
        for (double i = 1; i < p.size(); i = i + 2) {
            Point2D p1 = p.get((int) i);
            double vol = p1.getY();
            double volf = (-min + vol) / (max - min);
            double wf = amplHeight * volf;
            double y = i * hf;
            drawLineTo(edge + wf, y);
        }
//        drawLineTo(w, h);
//        drawLineTo(0, h);
//        drawLineTo(0, 0);

        writer.println("\" stroke=\"black\" fill=\"none\" />");
    }

    private void drawAsLineCutsSVG(ArrayList<Point2D> p) {
        writer.println("<path d=\"");
        double amplHeightmm = 5; //25;
        double amplHeight = (dpi * (amplHeightmm / mm2in));

        double widthmm = (wmm - amplHeightmm * 2) * 0.9;
        double width = (dpi * (widthmm / mm2in));
        double edge = width;

        double hf = ((double) h) / ((double) (p.size()));
        drawLine(w, 0, w, 0);

        double inc = 2;
        for (double i = 1; i < p.size(); i = i + 2) {
            Point2D p1 = p.get((int) i);
            double vol = p1.getY();
            double volf = (-min + vol) / (max - min);
            double wf = amplHeight * volf;
            double y = i * hf;
            drawLine(edge, y, edge + wf, y);
        }
        writer.println("\" stroke=\"black\" fill=\"none\" />");
    }

    private void drawAsLinearSVGLowWithD(ArrayList<Point2D> ps) {
        writer.println("<path d=\"");
        int ms = 0;
        double lastVol = 0;
        int c = 0;
        double mx = Math.pow(2, 15);

        HashMap<Integer, Integer> hm = new HashMap();
        for (int i = 0; i < ps.size(); i = i + 1) {
            Point2D p1 = ps.get(i);
            double y = p1.getY();
            double yy = (-min + y) / (max - min);
            if (lastVol > 0 && y < 0 || lastVol < 0 && y > 0) {
                double pp = (Math.abs(y) / mx);
                int num = 1 + (int) (pp * 50);
                if (Math.abs(yy) > 0.1) {
                    int pos = (int) ((double) h * i / 8000);
                    for (int n = 1; n < num; n++) {
                        drawLine(0, pos, 100, pos);
                    }
                    hm.put(c, num);
                    c++;
                }
            }

            lastVol = y;
        }
        writer.println("\" stroke=\"black\" fill=\"none\" />");
        int count = 0;
        for (int i : hm.keySet()) {
            System.out.println("c=" + i + ":" + hm.get(i));
            count = count + hm.get(i);
        }
        System.out.println("count=" + count);

    }

    private void drawAsLinearSVGLow(ArrayList<Point2D> ps) {
        writer.println("<path d=\"");
        int ms = 0;
        double lastVol = 0;
        int c = 0;
        for (int i = 0; i < ps.size(); i = i + 1) {
            Point2D p1 = ps.get(i);
            double y = p1.getY();
            double yy = (-min + y) / (max - min);
            if (lastVol > 0 && y < 0 || lastVol < 0 && y > 0) {
                if (Math.abs(y) > 10) {
                    int pos = (int) ((double) h * i / 8000);
                    drawLine(0, pos, 100, pos);
                    c++;
                }
            }

            lastVol = y;
        }
        writer.println("\" stroke=\"black\" fill=\"none\" />");
        System.out.println("c=" + c);

    }

    private void drawAsLinearSVG(ArrayList<Point2D> ps) {
        int ms = 0;
        double lastVol = 0;
        double lastyy = 0;
        writer.println("");
        String[] cols = {"blue", "red", "green", "cyan", "magenta"};

        double ww = 200;

        double mx = Math.pow(2, 15);
        for (double k = 0; k < 5; k++) {
            int c = 0;
            writer.println("<path d=\"");
            double dVol = (k) / 5.0;
            for (double i = 0; i < ps.size(); i = i + 1) {
                int pos = (int) ((double) h * i / 8000);
                double sec = i / 8000;
                Point2D p1 = ps.get((int) i);
                double vol = p1.getY();
                double volPos = p1.getY() > 0 ? p1.getY() : 0;
                double yy = volPos / (mx);

                double x = ww * abs(yy) / (dVol + 0.2);

                if (abs(yy) > dVol && abs(yy) < dVol + 0.2) {
                    double vDiff = abs(yy) - abs(lastVol);
                    if (abs(yy) > 0.15) {
                        drawLine(ww - x, pos, ww + x, pos);
                        c++;
                    }
                }
                if (lastyy < 0 && yy > 0 || lastyy > 0 && yy < 0) {
                    lastyy = 0;
                    lastVol = 0;
                } else {
                    lastyy = vol;
                    lastVol = yy;
                }
            }
            if (c > 0) {
            }
            int kk = (int) (20 * k / 5);
            //String col = "#" + kk + kk + kk;
            String col = cols[(int) k];
            //writer.println("\" stroke=\""+col+"\" fill=\"none\" />");
            writer.println("\" stroke=\"" + col + "\" fill=\"none\" />");
            //writer.println("\" stroke=\"black\" fill=\"none\" />");
            System.out.println("tot=" + c + "; col=" + col);
        }

    }

    private void drawAsSpiral(ArrayList<Point2D> ps) {
        int y = 0;
        int x = 0;
        int xc = w / 2;
        int yc = h / 2;
        double frr = 0.15;
        int radsep = 100;
        double rs = 350;
        double re = rs + radsep;
        double ang = 0;

        int rr = 10;

        ArrayList<Point2D.Double> in = new ArrayList<Point2D.Double>();
        ArrayList<Point2D.Double> out = new ArrayList<Point2D.Double>();
        int ms = 0;
        writer.println("<path d=\"");
        for (int i = 0; i < ps.size(); i = i + 1) {
            Point2D p1 = ps.get(i);
            double vol = p1.getY();
            double yy = (-min + vol) / (max - min);
            double rF = (0.5 - yy) * radsep;
            // double rF = yy * radsep;

            double angDeg = Math.toRadians(ang);
            double xs = xc + Math.cos(angDeg) * rs;
            double x1 = xc + Math.cos(angDeg) * (rs - (rF / 2.0));
            double x2 = xc + Math.cos(angDeg) * (rs + (rF / 2.0));
            double xe = xc + Math.cos(angDeg) * re;

            double ys = yc + Math.sin(angDeg) * rs;
            double y1 = xc + Math.sin(angDeg) * (rs - (rF / 2.0));
            double y2 = xc + Math.sin(angDeg) * (rs + (rF / 2.0));
            double ye = yc + Math.sin(angDeg) * re;

            in.add(new Point2D.Double(x1, y1));

            out.add(new Point2D.Double(x2, y2));

            if (ms == 799) {
                // opG.drawLine((int) xs, (int) ys, (int) xe, (int) ye);
            }

            double rrr = radsep / 8;
            double xx1 = xc + Math.cos(angDeg) * (rs - rrr);
            double xx2 = xc + Math.cos(angDeg) * (rs + rrr);
            double yy1 = xc + Math.sin(angDeg) * (rs - rrr);
            double yy2 = xc + Math.sin(angDeg) * (rs + rrr);
            if (abs(yy) > 0.5) {
                drawLine(xx1, yy1, xx2, yy2);
            }
            drawSpiralCut(xx1, yy1, true);
            drawSpiralCut(xx2, yy2, false);

            double cir = 2 * Math.PI * rs;
            double fr = (360.0 / cir);
            ang = ang + fr + 0.05;

            System.out.println("ang=" + ang);
            // frr = frr + 0.0001;
            rs = rs + fr * frr;
            re = re + fr * frr;
            ms = (ms + 1) % 800;
        }
        writer.println("\" stroke=\"black\" fill=\"none\" />");

        Path2D path = new Path2D.Double();
        for (Point2D.Double p : in) {
            if (path.getCurrentPoint() == null) {
                path.moveTo(p.x, p.y);
            } else {
                path.lineTo(p.x, p.y);
            }
        }
        for (int i = out.size() - 1; i >= 0; i--) {
            Point2D.Double p = out.get(i);
            path.lineTo(p.x, p.y);
        }

        System.out.println(x + ":" + y);
    }

    private void drawSpiralCut(double x1, double y1, boolean inner) {
        double xx = new BigDecimal(x1).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        double yy = new BigDecimal(y1).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (inner) {
            if (cut.isEmpty()) {
                cut = "<path d=\"M " + xx + " " + yy + " L ";
            } else {
                String c1 = xx + " " + yy + " L \r\n";
                cut = cut + " " + c1;
            }
        } else {
            if (cutO.isEmpty()) {
                cutO = "<path d=\"M " + xx + " " + yy + " L ";
            } else {
                String c1 = xx + " " + yy + " L \r\n";
                cutO = cutO + " " + c1;
            }
        }
    }

    private void drawLineORIG(double xs, double ys, double xe, double ye) {
        String sb = "M " + xs + " " + ys + " L " + xe + " " + ye + " ";
        writer.println(sb);
    }

    private void drawLine(double xs, double ys, double xe, double ye) {

        String sb = "M " + round(xs, 2) + " " + round(ys, 2) + " L " + round(xe, 2) + " " + round(ye, 2) + " ";
        writer.println(sb);
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

    private void drawAsSpiralLine(ArrayList<Point2D> ps) {
        int y = 0;
        int x = 0;
        int xc = w / 2;
        int yc = h / 2;
        int inc = 1;
        double frr = -0.113; //-0.15;
        double afr = -0.05; //-0.024;
        double amplHeightmm = 0.25;
        double amplHeight = (dpi * (amplHeightmm / mm2in));
        double cutHeightmm = 3;
        double cutHeight = (dpi * (cutHeightmm / mm2in));
        double radStartmm = wmm * 4 / 10;
        double radStart = (dpi * (radStartmm / mm2in));
        double ang = 0;
        ArrayList<Point2D.Double> in = new ArrayList<Point2D.Double>();
        Point2D.Double first = null;
        Point2D.Double last = null;

        writer.println("<path d=\"");
        int ms = 0;
        for (int i = 0; i < ps.size(); i = i + inc) {
            Point2D p1 = ps.get(i);
            double vol = p1.getY();
            double yy = (-min + vol) / (max - min);
            double rF = (0.5 - yy) * amplHeight;

            double angDeg = Math.toRadians(ang);
            double x1 = xc + Math.cos(angDeg) * (radStart - (rF / 2.0));
            double y1 = xc + Math.sin(angDeg) * (radStart - (rF / 2.0));

            drawSpiralLineCut(i, x1, y1);
            if (first == null) {
                first = new Point2D.Double(x1, y1);
            }
            last = new Point2D.Double(x1, y1);

            double rrr = cutHeight;
            double xx1 = xc + Math.cos(angDeg) * (radStart - rrr);
            double xx2 = xc + Math.cos(angDeg) * (radStart + rrr);
            double yy1 = xc + Math.sin(angDeg) * (radStart - rrr);
            double yy2 = xc + Math.sin(angDeg) * (radStart + rrr);
            in.add(new Point2D.Double(xx1, yy1));

            double cir = 2 * Math.PI * radStart;
            double fr = (360.0 / cir);
            ang = ang + fr + afr;

            System.out.println("ang=" + ang);
            // frr = frr + 0.0001;
            radStart = radStart + fr * frr;
            ms = (ms + 1) % 800;
        }

        drawSpiralLineCut(1, last.x, last.y);
        Path2D.Double path = new Path2D.Double();
        for (int i = in.size() - 1; i > 0; i = i - inc) {
            Point2D.Double p = in.get(i);
            if (path.getCurrentPoint() == null) {
                path.moveTo(p.x, p.y);
            } else {
                path.lineTo(p.x, p.y);
            }
            drawSpiralLineCut(i, p.x, p.y);
        }
        drawSpiralLineCut(1, first.x, first.y);
        writer.println("\" stroke=\"black\" fill=\"none\" />");

        System.out.println(x + ":" + y);

        PathLength pl = new PathLength(path);
        double len = pl.lengthOfPath();
        double lenmm = mm2in * (len / dpi);
        System.out.println("**** LENpx=" + len + "LENmm=" + lenmm);

    }

    private void drawAsSpiralLineScore(ArrayList<Point2D> ps) {
        int y = 0;
        int x = 0;
        int xc = w / 2;
        int yc = h / 2;
        int inc = 1;
        double frr = -0.13;//13; //-0.15;
        double afr = -0.005; //-0.024;
        double amplHeightmm = 0.25;
        double amplHeight = (dpi * (amplHeightmm / mm2in));
        double cutHeightmm = 1.75;
        double cutHeight = (dpi * (cutHeightmm / mm2in));
        double radStartmm = wmm * 4 / 10;
        double radStart = (dpi * (radStartmm / mm2in));
        double ang = 0;
        ArrayList<Point2D.Double> in = new ArrayList<Point2D.Double>();
        ArrayList<Point2D.Double> out = new ArrayList<Point2D.Double>();
        Point2D.Double first = null;
        Point2D.Double last = null;

        writer.println("<path d=\"");
        int ms = 0;
        for (int i = 0; i < ps.size(); i = i + inc) {
            Point2D p1 = ps.get(i);
            double vol = p1.getY();
            double yy = (-min + vol) / (max - min);
            double rF = (0.5 - yy) * amplHeight;

            double angDeg = Math.toRadians(ang);
            double x1 = xc + Math.cos(angDeg) * (radStart - (rF / 2.0));
            double y1 = xc + Math.sin(angDeg) * (radStart - (rF / 2.0));

            if (first == null) {
                first = new Point2D.Double(x1, y1);
                drawLine(x1, y1, x1, y1);
            } else {
                drawLineTo(x1, y1);
            }
            last = new Point2D.Double(x1, y1);

            double rrr = cutHeight;
            double xx1 = xc + Math.cos(angDeg) * (radStart - rrr);
            double xx2 = xc + Math.cos(angDeg) * (radStart + rrr);
            double yy1 = xc + Math.sin(angDeg) * (radStart - rrr);
            double yy2 = xc + Math.sin(angDeg) * (radStart + rrr);
            in.add(new Point2D.Double(xx1, yy1));
            out.add(new Point2D.Double(xx2, yy2));

            double cir = 2 * Math.PI * radStart;
            double fr = (360.0 / cir);
            ang = ang + fr + afr;

            System.out.println("ang=" + ang);
            // frr = frr + 0.0001;
            radStart = radStart + fr * frr;
            ms = (ms + 1) % 800;
        }

        writer.println("\" stroke=\"black\" fill=\"none\" />");

        writer.println("<path d=\"");
        Path2D.Double path = new Path2D.Double();
        Point2D.Double pL = out.get(out.size() - 1);
        for (int i = in.size() - 1; i > 0; i = i - inc) {
            Point2D.Double p = in.get(i);
            if (path.getCurrentPoint() == null) {
                path.moveTo(p.x, p.y);
                drawLine(pL.x, pL.y, p.x, p.y);
            } else {
                path.lineTo(p.x, p.y);
            }
            drawLineTo(p.x, p.y);
        }

        path = new Path2D.Double();
        for (int i = 0; i < out.size(); i = i + inc) {
            Point2D.Double p = out.get(i);
            if (path.getCurrentPoint() == null) {
                drawLineTo(p.x, p.y);
                path.moveTo(p.x, p.y);
            } else {
                path.lineTo(p.x, p.y);
            }
            drawLineTo(p.x, p.y);
        }
        writer.println("\" stroke=\"black\" fill=\"none\" />");

        PathLength pl = new PathLength(path);
        double len = pl.lengthOfPath();
        double lenmm = mm2in * (len / dpi);
        System.out.println("**** LENpx=" + len + "LENmm=" + lenmm);

    }

    String addLine(double x1, double y1, double x2, double y2) {
        String d = "M" + x1 + " " + y1 + " L " + x2 + " " + y2 + " ";
        return d;
    }

    private void drawSpiralLineCut(int i, double x1, double y1) {
        if (i == 0) {
            String sb = "M " + x1 + " " + y1;
            writer.println(sb);
        } else {
            String sb = " L " + x1 + " " + y1 + " ";
            writer.println(sb);
        }
    }

    private void drawAsCircle(ArrayList<Point2D> ps) {
        int y = 0;
        int x = 0;
        int xc = w / 2;
        int yc = h / 2;
        double ampF = 0.005;
        double radsep = xc * ampF;
        double rs = xc * 0.8;
        double outerOff = xc * 0.2;
        double rd = radsep;
        double ang = 0;

        writer.println("<path d=\"");
        ArrayList<Point2D.Double> in = new ArrayList<Point2D.Double>();
        ArrayList<Point2D.Double> out = new ArrayList<Point2D.Double>();
        int ms = 0;
        for (int i = 0; i < ps.size(); i = i + 1) {
            Point2D p1 = ps.get(i);
            double vol = p1.getY();
            double yy = (-min + vol) / (max - min);
            double rF = (0.5 - yy) * radsep;

            double angDeg = Math.toRadians(ang);
            double x1 = xc + Math.cos(angDeg) * (rs - (rF / 2.0));
            double x2 = xc + Math.cos(angDeg) * (rs + outerOff);

            double y1 = xc + Math.sin(angDeg) * (rs - (rF / 2.0));
            double y2 = xc + Math.sin(angDeg) * (rs + outerOff);

            in.add(new Point2D.Double(x1, y1));
            out.add(new Point2D.Double(x2, y2));

            double fr = (360.0 / (double) (ps.size()));
            ang = ang + fr;

            ms = (ms + 1) % 800;
        }

        Path2D path = new Path2D.Double();
        for (Point2D.Double p : in) {
            if (path.getCurrentPoint() == null) {
                path.moveTo(p.x, p.y);
            } else {
                path.lineTo(p.x, p.y);
            }
        }
        for (int i = out.size() - 1; i >= 0; i--) {
            Point2D.Double p = out.get(i);
            path.lineTo(p.x, p.y);
        }

        for (int i = 0; i < in.size() - 1; i = i + 1) {
            Point2D.Double p = in.get(i);
            if (i == 0) {
                path.moveTo(p.x, p.y);
                drawLine(p.x, p.y, p.x, p.y);
            } else {
                drawLineTo(p.x, p.y);
            }
        }

        writer.println(addCircle(xc, yc, rs + outerOff));
        writer.println(addCircle(xc, yc, rs + outerOff));
        writer.println(addCircle(xc, yc, rs + outerOff));
        writer.println(addCircle(xc, yc, rs + outerOff));

        double crRad = xc / 10;
        double pRad = xc / 20;
        drawLine(xc, yc - crRad, xc, yc + crRad);
        drawLine(xc - crRad, yc, xc + crRad, yc);
        drawLineTo(xc + crRad - pRad, yc + pRad);
        drawLineTo(xc + crRad - pRad, yc - pRad);
        drawLineTo(xc + crRad, yc);

        writer.println("\" stroke=\"black\" fill=\"none\" />");
        System.out.println(x + ":" + y);
    }

    public String addCircle(double cx, double cy, double radius) {
        String largeArc = " 1 ";
        double endAng = 359.99;
        double[] start = polarToCartesian(cx, cy, radius, endAng);
        double[] end = polarToCartesian(cx, cy, radius, 0);
        String d = "M" + round2(start[0]) + " " + round2(start[1]) + " A " + round2(radius) + " " + round2(radius)
                + " 0" + largeArc + "0 " + round2(end[0]) + " " + round2(end[1]);
        return d;
    }

    double[] polarToCartesian(double centerX, double centerY, double radius, double angleInDegrees) {
        double angleInRadians = Math.toRadians(angleInDegrees);
        double x = (centerX + (radius * Math.cos(angleInRadians)));
        double y = (centerY + (radius * Math.sin(angleInRadians))); // -

        double arr[] = {x, y};
        return arr;
    }

    public double round2(double value) {
        return round(value, 2);
    }


    private void drawAsLinear(ArrayList<Point2D> ps) {
        int y = 0;
        int x = 0;
        int xc = 0;
        double radsep = dpi / 4.0;
        double yc = radsep * 2.0;

        int ms = 0;
        writer.println("<path d=\"");
        for (int i = 0; i < ps.size(); i = i + 1) {
            Point2D p1 = ps.get(i);
            double vol = p1.getY();
            double yy = (-min + vol) / (max - min);
            double rF = (0.5 - yy) * radsep;

            double x1 = x;
            double x2 = x;

            double y1 = yc - (rF / 2.0);
            double y2 = yc + (rF / 2.0);

            drawLine((int) x1, (int) y1, (int) x2, (int) y2);
            if (x == w) {
                x = 0;
                yc = yc + radsep * 2.0;
                drawLine(0, (int) yc, w, (int) yc);
                drawLine(0, (int) yc, w, (int) yc);
            }
            if (ms == 799) {
                double y11 = yc - (radsep);
                double y21 = yc + (radsep);
                drawLine((int) x1, (int) y11, (int) x2, (int) y21);
            }

            ms = (ms + 1) % 800;
            x++;
        }

        writer.println("\" stroke=\"black\" fill=\"none\" />");
        System.out.println(x + ":" + y);
    }

    private Color getCol(Point2D p1) {
        double y = p1.getY();
        double yy = (-min + y) / (max - min);
        double wh = Color.BLACK.getRGB();
        int cc = (int) (yy * wh);
        // Color colo = new Color(cc);
        // System.out.println(x + ":" + cc);
        cc = (int) (yy * 255);
        Color colo = new Color(cc, cc, cc);
        return colo;
    }

    private Color getColor(Point2D p1, int x) {
        double y = p1.getY();
        double yy = (-min + y) / (max - min);
        int cc = (int) (yy * 255);
        Color colo = new Color(cc, cc, cc);
        System.out.println(x + ":" + cc);
        return colo;
    }

    private double getPeaksVolume(int i) {
        double val0 = reader.getData()[0][i - 1];
        double val = reader.getData()[0][i];
        double val1 = reader.getData()[0][i + 1];
        if ((val0 < val && val > val1) || (val0 > val && val < val1)) {
            double value = getVolume(val);
            return value;
        }

        return 0;
    }

    private double getAverage(int i) {
        double val = 0;
        val = reader.getData()[0][i];
        val = getVolume(val);
        double ret = (-min + val) / (max - min);
        // System.out.println(i + ":" + val);
        return val;
    }

    private double getVolume(double val) {
        if (val < 0) {
            return -Math.pow(-val, volAmp);
        }
        return Math.pow(val, volAmp);
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
