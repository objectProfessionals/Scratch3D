package com.op.scratch3d.holes;

import com.op.scratch3d.Base;
import com.op.scratch3d.SvgDrawer;

import java.io.IOException;

public class HolesDotMatrix extends Base {

    private String opDir = hostDir + "pinHole/";
    double dpi = 72;
    double mm2in = 25.4;
    double bordermm = 5;
    double hmm = 150;
    double wmm = 70;
    double sepmm = 5;
    double radmm = 0.1;
    private String src = "HolesDM-" + wmm + "-" + hmm + "-" + radmm;

    private double w = dpi * (wmm / mm2in);
    private double h = dpi * (hmm / mm2in);
    private double sep = dpi * (sepmm / mm2in);
    private double rad = dpi * (radmm / mm2in);
    private double border = dpi * (bordermm / mm2in);

    private static HolesDotMatrix scratchDM = new HolesDotMatrix();

    SvgDrawer svgDescriber = new SvgDrawer(opDir, src, w, h);

    public static void main(String[] args) throws Exception {
        scratchDM.drawAll();

    }

    private void drawAll() throws Exception {
        init();
        drawAllPoints();
        save();

    }

    private void drawAllPoints() {
        for (double y = border; y <= h - border*0.9; y = y + sep) {
            for (double x = border; x <= w - border*0.9; x = x + sep) {
                drawHole(x, y);
            }
        }
    }

    private void drawHole(double x, double y) {
        svgDescriber.writeCircleD2(x, y, rad);
    }

    private void init() throws IOException {

        svgDescriber.startSimpleSVG();

        System.out.println("...finished initialising");
    }

    private void save() throws Exception {
        svgDescriber.endSimpleSVG();
    }
}
