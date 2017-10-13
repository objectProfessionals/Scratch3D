package com.op.scratch3d.sounds;

import com.op.scratch3d.Base;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

public class PlotVolumeSpiral extends Base {
    private static final String SPIRAL_LINE = "SpiralLine";
    private static final String SPIRAL = "Spiral";
    private static final String CIRCLE = "circle";
    private static final String LINEAR = "linear";
    private static final String typeName = SPIRAL_LINE;
    private static final String scName = "ILoveYou20"; // "hello20";
    private static final String opFileName = typeName + scName; // "hello20";
    private String dir = "host/images/out/misc/scratch3d/";
    private String opDir = "../output/";
    private static final String outFileExt = ".png";
    private float fontSize = 3;
    private double volAmp = 1;
    private static PlotVolumeSpiral tester;
    private WaveFileReader reader;
    private double dpi = 300;
    // private double dpi = 75;
    private double mm2in = 25.4;
    private double wmm = 100;
    private double hmm = 100;
    private int w = (int) (dpi * (wmm / mm2in));
    private int h = (int) (dpi * (hmm / mm2in));
    private int cx = w / 2;
    private int cy = h / 2;
    private boolean addBorder = true;
    private double bordermm = 20;
    private double border = (dpi * (bordermm / mm2in));
    private double max = -1;
    private double min = 1000000;
    private BufferedImage opImage;
    private Graphics2D opG;
    private PrintWriter writer;
    private String cut = "";
    private String cutO = "";

    public static void main(String[] args) throws Exception, FontFormatException {
        tester = new PlotVolumeSpiral();
        tester.setupWholeImage();
        tester.createWavData();
        tester.drawVolume();
        tester.saveImage();
    }

    protected void setupWholeImage() throws IOException {
        System.out.println("Creating...");
        opImage = createBufferedImage(w, h);
        opG = (Graphics2D) opImage.getGraphics();
        opG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        opG.setColor(Color.WHITE);
        opG.fillRect(0, 0, w, h);
        opG.setColor(Color.WHITE);

        Font currentFont = opG.getFont();
        Font newFont = currentFont.deriveFont(currentFont.getSize() * fontSize);
        opG.setFont(newFont);

        String src = opDir;

        writer = new PrintWriter(src + "SPIRAL.svg", "UTF-8");
        writer.println("<svg width=\"" + w + "\" height=\"" + h + "\" xmlns=\"http://www.w3.org/2000/svg\">");
        writer.println("");
        writer.println("<path d=\"");

    }

    private void createWavData() {
        System.out.println("reading " + scName);
        String src = opDir;
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
        for (int i = 0; i < end - 1; i = i + 1) {
            p.add(new Point2D.Double(i, getAverage(i)));
        }
        if (typeName.equals(SPIRAL)) {
            drawAsSpiral(p);
        } else if (typeName.equals(SPIRAL_LINE)) {
            drawAsSpiralLine(p);
        } else if (typeName.equals(CIRCLE)) {
            drawAsCircle(p);
        } else if (typeName.equals(LINEAR)) {
            drawAsLinear(p);
        }
    }

    private void drawAsSpiral(ArrayList<Point2D> ps) {
        opG.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, null, 0f));
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
        opG.setStroke(new BasicStroke(10f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, null, 0f));
        opG.setColor(Color.RED);
        opG.fillOval(xc - rr, yc - rr, rr * 2, rr * 2);
        opG.drawString("  " + scName, xc, yc);

        opG.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, null, 0f));

        ArrayList<Point2D.Double> in = new ArrayList<Point2D.Double>();
        ArrayList<Point2D.Double> out = new ArrayList<Point2D.Double>();
        int ms = 0;
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
            if (Math.abs(yy) > 0.5) {
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

        opG.setColor(Color.WHITE);
        opG.fill(path);

        opG.setStroke(new BasicStroke(10f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, null, 0f));
        opG.setColor(Color.RED);
        // opG.drawOval(0, 0, w, h);
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

    private void drawLine(double xs, double ys, double xe, double ye) {
        String sb = "M " + xs + " " + ys + " L " + xe + " " + ye + " ";
        writer.println(sb);
    }

    private void drawAsSpiralLine(ArrayList<Point2D> ps) {
        opG.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, null, 0f));
        int y = 0;
        int x = 0;
        int xc = w / 2;
        int yc = h / 2;
        double frr = -0.05;
        double afr = -0.024;
        double amplHeightmm = 0.5;
        double amplHeight = (dpi * (amplHeightmm / mm2in));
        double rsmm = 45;
        double rimm = 0;
        double rs = (dpi * (rsmm / mm2in));
        double ri = (dpi * (rimm / mm2in));
        double re = rs + amplHeight + ri;
        double ang = 0;

        int rr = 10;
        opG.setStroke(new BasicStroke(10f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, null, 0f));
        opG.setColor(Color.RED);
        opG.fillOval(xc - rr, yc - rr, rr * 2, rr * 2);
        opG.drawString("  " + scName, xc, yc);

        opG.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, null, 0f));

        ArrayList<Point2D.Double> in = new ArrayList<Point2D.Double>();
        ArrayList<Point2D.Double> out = new ArrayList<Point2D.Double>();

        int cr = 10;
        String sd1 = addLine(cx - cr, cy - cr, cx + cr, cy + cr);
        writer.println(sd1);
        String sd2 = addLine(cx - cr, cy + cr, cx + cr, cy - cr);
        writer.println(sd2);

        int ms = 0;
        for (int i = 0; i < ps.size(); i = i + 1) {
            Point2D p1 = ps.get(i);
            double vol = p1.getY();
            double yy = (-min + vol) / (max - min);
            double rF = (0.5 - yy) * amplHeight;
            // double rF = yy * radsep;

            double angDeg = Math.toRadians(ang);
            double xs = xc + Math.cos(angDeg) * rs;
            double x1 = xc + Math.cos(angDeg) * (rs - (rF / 2.0));
            double x2 = xc + Math.cos(angDeg) * (rs + (rF / 2.0));
            double xe = xc + Math.cos(angDeg) * re;

            double ys = yc + Math.sin(angDeg) * rs;
            double y1 = xc + Math.sin(angDeg) * (rs - (rF / 2.0));
            double y2 = yc + Math.sin(angDeg) * (rs + (rF / 2.0));
            double ye = yc + Math.sin(angDeg) * re;

            in.add(new Point2D.Double(x1, y1));
            out.add(new Point2D.Double(x2, y2));

            if (ms == 799) {
                opG.drawLine((int) xs, (int) ys, (int) xe, (int) ye);
            }

            drawSpiralLineCut(i, x1, y1);

            double rrr = amplHeight * 4;
            double xx1 = xc + Math.cos(angDeg) * (rs - rrr);
            double xx2 = xc + Math.cos(angDeg) * (rs + rrr);
            double yy1 = xc + Math.sin(angDeg) * (rs - rrr);
            double yy2 = xc + Math.sin(angDeg) * (rs + rrr);
            // drawSpiralCut(xx1, yy1, true);
            // drawSpiralCut(xx2, yy2, false);

            double cir = 2 * Math.PI * rs;
            double fr = (360.0 / cir);
            ang = ang + fr + afr;

            System.out.println("ang=" + ang);
            // frr = frr + 0.0001;
            rs = rs + fr * frr;
            re = re + fr * frr;
            ms = (ms + 1) % 800;
        }

        // writer.println(" Z ");

        Path2D path = new Path2D.Double();
        for (Point2D.Double p : in) {
            if (path.getCurrentPoint() == null) {
                path.moveTo(p.x, p.y);
            } else {
                path.lineTo(p.x, p.y);
            }
        }

        opG.setColor(Color.BLACK);
        opG.draw(path);

        opG.setStroke(new BasicStroke(10f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, null, 0f));
        opG.setColor(Color.RED);
        opG.drawOval(0, 0, w, h);
        System.out.println(x + ":" + y);
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
        opG.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, null, 0f));
        int y = 0;
        int x = 0;
        int xc = w / 2;
        int yc = h / 2;
        double radsep = dpi / 3.0;
        double rs = dpi * 10.0 / 3.0;
        double rd = radsep / 5;
        double ang = 0;

        int rr = 10;
        opG.setStroke(new BasicStroke(10f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, null, 0f));
        opG.setColor(Color.RED);
        opG.fillOval(xc - rr, yc - rr, rr * 2, rr * 2);
        opG.drawString("  " + scName, xc, yc);

        opG.setStroke(new BasicStroke((float) (dpi / 4), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, null, 0f));
        opG.setColor(Color.BLACK);
        opG.drawOval((int) (xc - rs), (int) (yc - rs), (int) (rs * 2), (int) (rs * 2));

        opG.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, null, 0f));
        opG.setColor(Color.RED);

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
            double x2 = xc + Math.cos(angDeg) * (rs + (rF / 2.0));

            double y1 = xc + Math.sin(angDeg) * (rs - (rF / 2.0));
            double y2 = xc + Math.sin(angDeg) * (rs + (rF / 2.0));

            in.add(new Point2D.Double(x1, y1));

            out.add(new Point2D.Double(x2, y2));

            if (ms == 799) {
                double xs = xc + Math.cos(angDeg) * (rs - rd);
                double xe = xc + Math.cos(angDeg) * (rs + rd);
                double ys = yc + Math.sin(angDeg) * (rs - rd);
                double ye = yc + Math.sin(angDeg) * (rs + rd);
                opG.drawLine((int) xs, (int) ys, (int) xe, (int) ye);
            }

            double fr = (360.0 / 8000.0);
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

        opG.setColor(Color.WHITE);
        opG.fill(path);

        opG.setStroke(new BasicStroke(10f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, null, 0f));
        opG.setColor(Color.RED);
        // opG.drawOval(0, 0, w, h);
        System.out.println(x + ":" + y);
    }

    private void drawAsLinear(ArrayList<Point2D> ps) {
        opG.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, null, 0f));
        int y = 0;
        int x = 0;
        int xc = 0;
        double radsep = dpi / 4.0;
        double yc = radsep * 2.0;

        opG.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, null, 0f));
        opG.setColor(Color.RED);
        opG.drawString("  " + scName, 50, 50);

        opG.setStroke(new BasicStroke((float) (dpi / 4), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, null, 0f));
        opG.setColor(Color.BLACK);
        opG.drawLine(0, (int) yc, w, (int) yc);

        opG.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, null, 0f));
        opG.setColor(Color.WHITE);
        int ms = 0;
        for (int i = 0; i < ps.size(); i = i + 1) {
            Point2D p1 = ps.get(i);
            double vol = p1.getY();
            double yy = (-min + vol) / (max - min);
            double rF = (0.5 - yy) * radsep;

            double x1 = x;
            double x2 = x;

            double y1 = yc - (rF / 2.0);
            double y2 = yc + (rF / 2.0);

            opG.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
            if (x == w) {
                x = 0;
                yc = yc + radsep * 2.0;
                opG.setStroke(new BasicStroke((float) (dpi / 4), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f,
                        null, 0f));
                opG.setColor(Color.BLACK);
                opG.drawLine(0, (int) yc, w, (int) yc);
                opG.drawLine(0, (int) yc, w, (int) yc);

                opG.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, null, 0f));
                opG.setColor(Color.WHITE);
            }
            if (ms == 799) {
                opG.setColor(Color.RED);
                double y11 = yc - (radsep);
                double y21 = yc + (radsep);
                opG.drawLine((int) x1, (int) y11, (int) x2, (int) y21);
                opG.setColor(Color.WHITE);
            }

            ms = (ms + 1) % 800;
            x++;
        }

        opG.setStroke(new BasicStroke(10f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, null, 0f));
        opG.setColor(Color.RED);
        // opG.drawOval(0, 0, w, h);
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

    protected void saveImage() throws Exception {
        BufferedImage opImage3 = opImage;
        if (addBorder) {
            int ww = (int) (((double) w) + border);
            int hh = (int) (((double) h) + border);
            int wd = (int) (border);
            int hd = (int) (border);
            BufferedImage opImage2 = createBufferedImage(ww, hh);
            Graphics2D opG2 = (Graphics2D) opImage2.getGraphics();
            opG2.setColor(Color.WHITE);
            opG2.fillRect(0, 0, ww, hh);
            opG2.drawImage(opImage, null, wd, hd);
            opImage3 = opImage2;
        }
        System.out.println("Saving...");
        String src = opDir;
        if (outFileExt.equals(".png")) {
            savePNGFile(opImage3, src + opFileName + outFileExt, dpi);
        } else {
            saveJPGFile(opImage3, src + opFileName + outFileExt, dpi, 1);
        }
        opG.dispose();
        printFileInfo(src + opFileName + outFileExt);

        writer.println("\" stroke=\"blue\" fill=\"none\" />");

        // writer.print(cut.substring(0, cut.length() - 4));
        // writer.println("\" stroke=\"red\" fill=\"none\" />");
        //
        // writer.print(cutO.substring(0, cutO.length() - 4));
        // writer.println("\" stroke=\"red\" fill=\"none\" />");

        writer.println("</svg>");
        writer.close();
    }

    private void printFileInfo(String fFile1) {
        Date now = new Date();
        System.out.println("Saved " + fFile1 + " @" + now);
    }
}
