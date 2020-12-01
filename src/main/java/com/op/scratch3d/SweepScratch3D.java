package com.op.scratch3d;

import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.VertexGeometric;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class SweepScratch3D extends Base {
    private String opDir = hostDir + "output/";
    private String objDir = hostDir + "objFiles/";

    private String obj = "SweepCube";

    private String src = "SWEEPscratch3D-" + obj;
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
    double scaleObject = 0.1;

    ArrayList<VertexGeometric> allPoints = new ArrayList<VertexGeometric>();
    ArrayList<Face> originalFaces = new ArrayList<Face>();
    double vanZ = 5;
    ObjLoader objLoader = new ObjLoader();
    SvgDrawer svgDrawer = new SvgDrawer(opDir, src, w, h);
    VertexTransformer vertexTransformer;
    ArrayList<VertexGeometric> selectedVerts = new ArrayList<VertexGeometric>();

    private int totRotAng = 360;
    private double numFrames = 360;
    private double incRotAng = totRotAng / numFrames; // 6;
    boolean adjustForPerspective = false;
    boolean occlude = false;
    boolean selectedOnly = true;

    private static SweepScratch3D scratch3D = new SweepScratch3D();

    public static void main(String[] args) throws IOException {
        scratch3D.draw();
    }

    private void draw() throws FileNotFoundException, IOException {
        svgDrawer.startSVG(false, true, 1, 0.125, 0.5);

        originalFaces = objLoader.loadOBJ(objDir + obj, allPoints);
        vertexTransformer = new VertexTransformer(originalFaces, vanZ);
        if (selectedOnly) {
            selectedVerts = objLoader.loadOBJSelectedVerts(objDir + obj + "_sel");
        }

        drawAllPointsAsContinuosLines();

        svgDrawer.endSVG();
    }

    private void drawAllPointsAsContinuosLines() {
        String sd1 = svgDrawer.addLine(cx - 10, cy - 10, cx + 10, cy + 10);
        svgDrawer.writeToSVG(sd1);
        String sd2 = svgDrawer.addLine(cx - 10, cy + 10, cx + 10, cy - 10);
        svgDrawer.writeToSVG(sd2);

        for (VertexGeometric p : allPoints) {
            for (double a = 0; a < totRotAng; a = a + incRotAng) {
                drawPoint(p, a);
            }
        }
    }

    private void drawPoint(VertexGeometric p1, double aDegs) {
        if (selectedOnly && !selectedVertsContains(p1)) {
            return;
        }
        VertexGeometric p2  = vertexTransformer.rotateVertexZ(p1, aDegs, adjustForPerspective);
        VertexGeometric p3 = vertexTransformer.rotateVertexX(p2, 45, adjustForPerspective);
        if (occlude) {
            ArrayList<Face> faces = vertexTransformer.getTransformedFacesRotateZ(aDegs, adjustForPerspective);
            if (!objLoader.isVertexVisibleForVertex(faces, p3)) {
                return;
            }
        }



        double zOff = 0;
        double xx = p3.x;
        double yy = p3.y;
        double zz = p3.z + zOff;

        drawPointsAsSingleLine(xx, yy, zz, aDegs);

    }

    private void drawPointsAsSingleLine(double x, double y, double z, double aDegs) {
        double sc = scaleMain * 0.1;
        double xd = x * sc;
        double yd = y * sc;

        double a = Math.toRadians(90 + aDegs);

        double radToC = scaleMain * 1;
        double xP = xd + radToC * Math.cos(a);
        double yP = yd + radToC * Math.sin(a);

        double rad2 = Math.sqrt(xP * xP + yP * yP);
        double ang2 = Math.toDegrees(Math.atan2(yP, xP));
        double ang2res = Math.toRadians(ang2);

        double xP2 = rad2 * Math.cos(ang2res);
        double yP2 = rad2 * Math.sin(ang2res);

        ArrayList<Double> blocks = new ArrayList<>();
        blocks.add(0D);
        //blocks.add(30D);
        //blocks.add(60D);
        //blocks.add(90D);
        //blocks.add(330D);
        if (blocks.contains(aDegs)) {
            String sb = svgDrawer.moveTo(cx + xP2, cy - yP2);
            svgDrawer.writeToSVG(sb);
        } else {
            String sb = svgDrawer.lineTo(cx + xP2, cy - yP2);
            svgDrawer.writeToSVG(sb);
        }
    }

    private void drawPoint(double x, double y, float z, double aDegs, boolean b) {
        double sc = scaleMain * 0.2;

        double xd = x * sc;
        double yd = y * sc;

        double radToC = scaleMain * 1.1;
        double x2 = xd;
        double y2 = radToC + yd;
        double rad2 = Math.sqrt(x2 * x2 + y2 * y2);
        double ang2 = Math.toDegrees(Math.atan2(y2, x2));

        double a = Math.toRadians(aDegs + ang2);
        double xOff = rad2 * Math.cos(a);
        double yOff = rad2 * Math.sin(a);

        if (aDegs == 0) {
            String sb = svgDrawer.moveTo(cx + xOff, cy - yOff);
            svgDrawer.writeToSVG(sb);
        } else {
            String sb = svgDrawer.lineTo(cx + xOff, cy - yOff);
            svgDrawer.writeToSVG(sb);
        }
    }

    private boolean selectedVertsContains(VertexGeometric vg) {
        for (VertexGeometric v : selectedVerts) {
            if (objLoader.equals(vg, v)) {
                return true;
            }
        }
        return false;
    }

}
