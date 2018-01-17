package com.op.scratch3d;

import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.VertexGeometric;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class CircleScratch3D extends Base {
    private String opDir = hostDir + "output/";
    private String objDir = hostDir + "objFiles/";

    // private String obj = "tie";
    // private String obj = "cubeHiSq";
    // private String obj = "Heart";
    // private String obj = "cubeHiEdges";
    //private String obj = "cubeLow";
    private String obj = "cubeHi";
    //private String obj = "cylinderHi";
    //private String obj = "001";
    // private String obj = "cubeTris";
    // private String obj = "coneLowWithEdges";
    // private String obj = "sphereMed";
    // private String obj = "DeathStar";
    // private String obj = "test-planes";
    // private String obj = "test-z";
    // private String obj = "test-pyramidSq";
    //private String obj = "KD-Triangle";

    private String src = "CIRscratch3D-" + obj;
    double dpi = 90;
    double mm2in = 25.4;
    double scalemm = 100;
    private double wmm = scalemm * 3;
    private double hmm = scalemm * 3;
    private double w = dpi * (wmm / mm2in);
    private double h = dpi * (hmm / mm2in);
    private double cx = (int) (w / 2.0);
    private double cy = (int) (h / 2.0);
    double scaleMain = dpi * (scalemm / mm2in);
    double scaleObject = 0.3;

    ArrayList<VertexGeometric> allPoints = new ArrayList<VertexGeometric>();
    ArrayList<Face> originalFaces = new ArrayList<Face>();
    double vanZ = 5;
    ObjLoader objLoader = new ObjLoader();
    SvgDrawer svgDrawer = new SvgDrawer(opDir, src, w, h);
    VertexTransformer vertexTransformer;

    private int totRotAng = 360;
    private double incRotAng = 30; // 6;
    private double arcAngHalf = 15;
    private double arcAngHalf2 = 10;
    boolean adjustForPerspective = false;
    boolean occlude = true;
    boolean multi = false;

    private static CircleScratch3D scratch3D = new CircleScratch3D();

    public static void main(String[] args) throws IOException {
        scratch3D.draw();
    }

    private void draw() throws FileNotFoundException, IOException {
        svgDrawer.startSVG(false, true, 1, 0.5, 0.125);

        originalFaces = objLoader.loadOBJ(objDir + obj, allPoints);
        vertexTransformer = new VertexTransformer(originalFaces, vanZ);

        //drawAllPoints();
        // drawAllPointsAsContinuosLines();
        drawSpirograph();

        svgDrawer.endSVG();
    }

    private void drawSpirograph() {
        double rad = w*0.4;
        for (double d = 0; d<5; d++) {
            double rx = 0.5 + 0.5*(d/4);
            double ry = 1 - 0.5*(d/4);
            String sb = svgDrawer.addArc(cx, cy, rad*rx, rad*ry, 0, 359.9);
            svgDrawer.writeToSVG(sb);
        }

    }

    private void drawAllPoints() {
        String sd1 = svgDrawer.addLine(cx - 10, cy - 10, cx + 10, cy + 10);
        svgDrawer.writeToSVG(sd1);
        String sd2 = svgDrawer.addLine(cx - 10, cy + 10, cx + 10, cy - 10);
        svgDrawer.writeToSVG(sd2);

        for (double a = 0; a < totRotAng; a = a + incRotAng) {
            for (VertexGeometric p : allPoints) {
                drawPoint(p, a);
            }
            // if (a > 60)
            // break;
        }

        // svgDrawer.drawAllScratches();

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
            // if (a > 60)
            // break;
        }

        // svgDrawer.drawAllScratches();

    }

    private void drawPoint(VertexGeometric p1, double aDegs) {
        VertexGeometric p2 = p1;
        p2 = vertexTransformer.transformVertex(p1, aDegs, adjustForPerspective);
        if (occlude) {
            ArrayList<Face> faces = vertexTransformer.getTransformedFaces(aDegs, adjustForPerspective);
            if (!objLoader.isVertexVisible(faces, p2)) {
                return;
            }
        }

        // if (occlude) {
        // if (!objLoader.isVertexVisible(faces, p2)) {
        // return;
        // }
        // }

        double zOff = 5;
        double xx = p2.x;
        double yy = p2.y;
        double zz = p2.z + zOff;

        if (multi) {
            double d = 0.025;
            if (zz > 0.5) {
                drawArc(xx + d, yy + d, zz + d, aDegs);
                drawArc(xx - d, yy + d, zz + d, aDegs);
                drawArc(xx + d, yy - d, zz + d, aDegs);
                drawArc(xx - d, yy - d, zz + d, aDegs);
                drawArc(xx + d, yy - d, zz - d, aDegs);
                drawArc(xx - d, yy - d, zz - d, aDegs);

                drawArc(xx, yy, zz, aDegs);
            } else if (zz > 0) {
                drawArc(xx + d, yy + d, zz + d, aDegs);
                drawArc(xx - d, yy + d, zz + d, aDegs);
                drawArc(xx + d, yy - d, zz + d, aDegs);
                drawArc(xx - d, yy - d, zz + d, aDegs);

                drawArc(xx, yy, zz, aDegs);
            } else if (zz > -0.5) {
                drawArc(xx + d, yy + d, zz + d, aDegs);
                drawArc(xx - d, yy + d, zz + d, aDegs);

                drawArc(xx, yy, zz, aDegs);
            } else {
                drawArc(xx, yy, zz, aDegs);
            }
        } else {
            drawArc(xx, yy, zz, aDegs);
        }

    }

    private void drawArc(double x, double y, double z, double aDegs) {
        double sc = scaleMain * scaleObject;

        double xd = x * sc;
        double yd = y * sc;

        double radToC = scaleMain * 1.0;
        double x2 = xd;
        double y2 = radToC + yd;
        double rad2 = Math.sqrt(x2 * x2 + y2 * y2);
        double ang2 = Math.toDegrees(Math.atan2(y2, x2));

        double a = Math.toRadians(aDegs + ang2);
        double xP2 = rad2 * Math.cos(a);
        double yP2 = rad2 * Math.sin(a);

        double zOff = 0.05;
        double rad = scaleMain * zOff + Math.abs(scaleMain * zOff * (z));

        double offAng = z > 0 ? 90 : 270;
        double aa = Math.toRadians(offAng + aDegs);
        double xc = rad * Math.cos(aa);
        double yc = rad * Math.sin(aa);

        double xOff = cx - (xP2 + xc);
        double yOff = cy - (yP2 + yc);

        double a1 = offAng + aDegs + arcAngHalf;
        double a2 = offAng + aDegs - arcAngHalf;
        String sb = svgDrawer.addArc(xOff, yOff, rad, a1, a2);
        svgDrawer.writeToSVG(sb);

//		double a1 = offAng + aDegs + arcAngHalf+arcAngHalf2;
//		double a2= offAng + aDegs + arcAngHalf-arcAngHalf2;
//		String sb = svgDrawer.addArc(xOff, yOff, rad, a1, a2);
//		svgDrawer.writeToSVG(sb);
//
//		//double a2 = offAng + aDegs - arcAngHalf;
//		a1 = offAng + aDegs - arcAngHalf+arcAngHalf2;
//		a2= offAng + aDegs - arcAngHalf-arcAngHalf2;
//		sb = svgDrawer.addArc(xOff, yOff, rad, a1, a2);
//		svgDrawer.writeToSVG(sb);


    }

    private void drawArc2(double x, double y, float z, double aDegs, boolean b) {
        double sc = scaleMain * 0.1;
        double xd = x * sc;
        double yd = y * sc;

        double a = Math.toRadians(90 + aDegs);

        double radToC = scaleMain;
        double xc1 = radToC * Math.cos(a);
        double yc1 = radToC * Math.sin(a);

        double oAng = Math.toDegrees(Math.atan2(yd, xd));
        double ooAng = oAng + aDegs;
        double rrr = Math.sqrt(xd * xd + yd * yd);
        double xxd = rrr * Math.cos(ooAng);
        double yyd = rrr * Math.sin(ooAng);

        double xP = xxd + xc1;
        double yP = yyd + yc1;

        double rad2 = Math.sqrt(xP * xP + yP * yP);
        double ang2 = Math.toDegrees(Math.atan2(yP, xP));
        double ang2res = Math.toRadians(ang2 + aDegs);

        double xP2 = rad2 * Math.cos(ang2res);
        double yP2 = rad2 * Math.sin(ang2res);

        double zOff = 0.1;
        double rad = scaleMain * zOff + scaleMain * zOff * (Math.abs(z));

        double aa = Math.toRadians(270 + aDegs);
        double xc = rad * Math.cos(aa);
        double yc = rad * Math.sin(aa);

        double xOff = cx + (xP2 + xc);
        double yOff = cy - (yP2 + yc);

        double a1 = 270 + aDegs - arcAngHalf;
        double a2 = 270 + aDegs + arcAngHalf;

        String sb = svgDrawer.addArc(xOff, yOff, rad, a1, a2);
        svgDrawer.writeToSVG(sb);
    }

    private void drawArcOLD(double x, double y, float z, double aDegs, boolean b) {
        double sc = scaleMain * 0.25;
        double radC = scaleMain * 0.5;
        double xd = x * sc;
        double yd = y * sc;
        double radd = 50 + 10 * z;
        double a = Math.toRadians(90 + aDegs);
        double xc = radd * Math.cos(a);
        double yc = radd * Math.sin(a);

        double xOff = cx + radC * Math.cos(a) + xd + xc;
        double yOff = cy - radC * Math.sin(a) - yd - yc;

        boolean inwards = false;
        double a1 = inwards ? 90 + aDegs + arcAngHalf : 270 + aDegs - arcAngHalf;
        double a2 = inwards ? 90 + aDegs - arcAngHalf : 270 + aDegs + arcAngHalf;

        String sb = svgDrawer.addArc(xOff, yOff, radd, a1, a2);
        svgDrawer.writeToSVG(sb);
    }

    private void drawPointsAsSingleLine(double x, double y, float z, double aDegs) {
        double sc = scaleMain * 0.1;
        double xd = x * sc;
        double yd = y * sc;

        double a = Math.toRadians(90 + aDegs);

        double radToC = scaleMain * 0.75;
        double xP = xd + radToC * Math.cos(a);
        double yP = yd + radToC * Math.sin(a);

        double rad2 = Math.sqrt(xP * xP + yP * yP);
        double ang2 = Math.toDegrees(Math.atan2(yP, xP));
        double ang2res = Math.toRadians(ang2 + aDegs);

        double xP2 = rad2 * Math.cos(ang2res);
        double yP2 = rad2 * Math.sin(ang2res);

        if (aDegs == 0) {
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

}
