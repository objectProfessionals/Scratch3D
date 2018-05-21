package com.op.scratch3d;

import java.io.FileNotFoundException;
import java.io.IOException;

public class SpiroScratch3D extends Base {
    private String opDir = hostDir + "output/";

    private String obj = "Spiral";

    private String src = "SPIRscratch3D-" + obj;
    double dpi = 90;
    double mm2in = 25.4;
    double scalemm = 100;
    private double wmm = scalemm * 3;
    private double hmm = scalemm * 3;
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
        svgDrawer.startSVG(false, true, 1, 0.125, 0.5);

        drawSpirograph();

        svgDrawer.endSVG();
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
