package com.op.scratch3d;

import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.VertexGeometric;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class RotateScratch3DSimple extends Base {

    private String opDir = hostDir + "output/";
    private String objDir = hostDir + "objFiles/";

    // private String obj = "tieMini";
    // private String obj = "cubeLow";
    // private String obj = "coneHi";
    // private String obj = "cubeLowWithEdges";
    //private String obj = "VS";
    // private String obj = "heart";
    // private String obj = "DeathStarLow";
    // private String obj = "test-planes";
    // private String obj = "test-z";
    // private String obj = "test-pyramidSq";

    private String obj = "cube1";
    private String src = "ROTscratch3D-" + obj;
    private boolean saveSVG = true;
    // double dpi = 1000;
    double dpi = 300;
    double mm2in = 25.4;
    double radArcmm = 40;
    double radArcMinmm = 20;
    double unitArcmm = 10.0;
    private double wmm = radArcmm * 2;
    private double hmm = radArcmm * 2;
    private double w = dpi * (wmm / mm2in);
    private double h = dpi * (hmm / mm2in);
    private int cx = (int) (w / 2.0);
    private int cy = (int) (h / 2.0);
    double radArcMax = dpi * (radArcmm / mm2in);
    double radArcMin = dpi * (radArcMinmm / mm2in);
    double middleF = 0.1;
    double unitArc = w * middleF; //dpi * (unitArcmm / mm2in);

    ArrayList<VertexGeometric> allPoints = new ArrayList<VertexGeometric>();
    ArrayList<Face> originalFaces = new ArrayList<Face>();
    double vanZ = 5;
    ObjLoader objLoader = new ObjLoader();
    SvgDrawer svgDrawer = new SvgDrawer(opDir, src, w, h, w*0.05);
    VertexTransformer vertexTransformer;

    private static RotateScratch3DSimple scratch3D = new RotateScratch3DSimple();

    public static void main(String[] args) throws IOException {
        scratch3D.draw();
    }

    private void draw() throws FileNotFoundException, IOException {
        svgDrawer.startSVG(true, true, 1, 0.5, middleF);

        originalFaces = objLoader.loadOBJ(objDir + obj, allPoints);
        vertexTransformer = new VertexTransformer(originalFaces, vanZ);
        drawAllPoints();

        save();
    }

    private void drawAllPoints() {
        String sd1 = svgDrawer.addLine(cx - 10, cy - 10, cx + 10, cy + 10);
        svgDrawer.writeToSVG(sd1);
        String sd2 = svgDrawer.addLine(cx - 10, cy + 10, cx + 10, cy - 10);
        svgDrawer.writeToSVG(sd2);
        int i = 0;
        for (VertexGeometric p : allPoints) {
            drawCircle(allPoints.get(i));
            i++;
        }
    }

    private void drawCircle(VertexGeometric v) {
        double s = (radArcMax - radArcMin)/7;
        double x = unitArc * v.x;
        double y = unitArc * v.y;
        double r = radArcMin + s * v.z;

        String circ = svgDrawer.addCircleD2(cx + x, cy + y, r);
            svgDrawer.writeToSVG(circ);
        }


    private void save() throws IOException {
        if (saveSVG) {
            svgDrawer.endSVG();
        }
    }
}
