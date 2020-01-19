package com.op.scratch3d;

import java.io.FileNotFoundException;
import java.io.IOException;

public class SpiroScratch3D extends Base {
    private String opDir = hostDir + "output/";

    private String obj = "TestCircles";

    private String src = "SPIRscratch3D-" + obj;
    double dpi = 300;
    double mm2in = 25.4;
    double scalemm = 100;
    private double wmm = scalemm;
    private double hmm = scalemm;
    private double w = dpi * (wmm / mm2in);
    private double h = dpi * (hmm / mm2in);
    private double cx = (w / 2.0);
    private double cy = (h / 2.0);
    double scaleMain = dpi * (scalemm / mm2in);
    double scaleObject = 0.3;

    SvgDrawer svgDrawer = new SvgDrawer(opDir, src, w, h);

    private double radSc = 10;
    private int totRotAng = 360;
    private double numFrames = 15;

    private static SpiroScratch3D scratch3D = new SpiroScratch3D();

    public static void main(String[] args) throws IOException {
        scratch3D.draw();
    }

    private void draw() throws FileNotFoundException, IOException {
        svgDrawer.startSVG(false, true, 1, 0.1, 0.5);

        //drawSpirograph();
        drawTestCircles();

        svgDrawer.endSVG();
    }

    private void drawTestCircles() {
        double r1=w*0.1;
        double num = 20;
        for (double i=1; i< 10; i++) {
            double r = r1 +  (i/num)*w*(0.5-0.1);
            String c = svgDrawer.addCircleD2(cx, cy, r, 0, 359.9);
            svgDrawer.writeToSVG(c);
        }

        for (double i=10; i< 20; i++) {
            double r = r1 +  (i/num)*w*(0.5-0.1);
            String c = svgDrawer.addCircleD2(cx, cy, r, 359.9, 0);
            svgDrawer.writeToSVG(c);
        }
    }

    private void drawSpirograph() {
        //https://www.desmos.com/calculator/coh8jngjlx
        double r = 0.5 * w;
        double d = r * 0.3; // inner wheel rad point
        double b = r * 0.17; //inner wheel rad
        double a = r * -0.75; // outer wheel rad
        double numTurns = 17;
        double firstX = 0;
        double firstY = 0;
        int c = 0;
        double fr = 500;
        for (double t = 0; t < (numTurns * Math.PI * 2); t = t + (Math.PI / fr)) {
            boolean first = (t == 0);
            double angRad = ((a + b) * t) / b;
            double x = (a + b) * Math.cos(t) + (d * Math.cos(angRad));
            double y = (a + b) * Math.sin(t) + (d * Math.sin(angRad));
            if (first) {
                firstX = x;
                firstY = y;
            }
            if (!first && equals(firstX, x) && equals(firstY, y) && equals(angRad, 0)) {
                System.out.println("c=" + c);
                break;
            }
            String sb = svgDrawer.addLine(first, cx + x, cy + y);
            svgDrawer.writeToSVG(sb);
            c++;
        }
    }

    private boolean equals(double x, double x2) {
        return Math.abs(x - x2) < w * 0.005;
    }

}
