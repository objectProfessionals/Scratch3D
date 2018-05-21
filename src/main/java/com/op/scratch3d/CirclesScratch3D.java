package com.op.scratch3d;

import com.owens.oobjloader.builder.VertexGeometric;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class CirclesScratch3D extends Base {
    private String opDir = hostDir + "output/";
    private String objDir = hostDir + "objFiles/";

    private String obj = "T2";
    //private String obj = "T";

    private String src = "CIRSscratch3D-" + obj;
    double dpi = 90;
    double mm2in = 25.4;
    double scalemm = 100;
    private double wmm = scalemm * 3;
    private double hmm = scalemm * 3;
    private double w = dpi * (wmm / mm2in);
    private double h = dpi * (hmm / mm2in);
    private double cx = (w / 2.0);
    private double cy = (h / 2.0);
    private BufferedImage ibi;

    ArrayList<VertexGeometric> allPoints = new ArrayList<VertexGeometric>();
    double vanZ = 5;
    ObjLoader objLoader = new ObjLoader();
    SvgDrawer svgDrawer = new SvgDrawer(opDir, src, w, h);

    private double radSc = 50;
    private int totRotAng = 360;
    private double numFrames = 12;
    private double incRotAng = totRotAng / numFrames; // 6;
    private double arcAngHalf = incRotAng / 2;//3

    private static CirclesScratch3D scratch3D = new CirclesScratch3D();

    public static void main(String[] args) throws IOException {
        scratch3D.draw();
    }

    private void draw() throws FileNotFoundException, IOException {
        svgDrawer.startSVG(false, true, 1, 0.125, 0.5);


        initAllPoints();

        drawAllPoints();

        svgDrawer.endSVG();
    }

    private void initAllPoints() throws IOException {
        ibi = ImageIO.read(new File(objDir + "Circles" + obj + ".jpg"));
        float ww = ibi.getWidth();
        float hh = ibi.getHeight();
        for (int y = 0; y<ibi.getHeight(); y++) {
            for (int x = 0; x<ibi.getWidth(); x++) {
                int rgb = ibi.getRGB(x, y);
                double r = (rgb >> 16) & 0x000000FF;
                double g = (rgb >> 8) & 0x000000FF;
                double b = (rgb) & 0x000000FF;
                double c = (((r + g + b) / 3.0));
                if (c < 10) {
                    VertexGeometric v1 = new VertexGeometric(((float)x-(ww/2))/ww, 1f - ((float)y)/hh, 1);
                    allPoints.add(v1);
                }
            }
        }
    }

    private void drawAllPoints() {
        String sd1 = svgDrawer.addLine(cx - 10, cy - 10, cx + 10, cy + 10);
        svgDrawer.writeToSVG(sd1);
        String sd2 = svgDrawer.addLine(cx - 10, cy + 10, cx + 10, cy - 10);
        svgDrawer.writeToSVG(sd2);

        for (VertexGeometric p : allPoints) {
            drawCircle(p.x, p.y, p.z);
        }
    }

    private void drawCircle(double x, double y, double z) {
        double R = w * 0.5;
        double r = w * 0.125;
        double ccx = w * 0.5;
        double ccy = w * 0.5;

        double d = R*(1-y)*0.25;
        double r1 = (R + r) / 2;
        double dd = d;
        double r2 = r1 + ((dd - d) / 2);

        double a = Math.PI*0.5 - (Math.PI*0.5 * x);
        double cr = r2 - r - dd;

        double cx = cr * Math.cos(a);
        double cy = cr * Math.sin(a);

        String sb = svgDrawer.addCircleD2(ccx + cx, ccy-cy, r2);
        svgDrawer.writeToSVG(sb);
    }
}
