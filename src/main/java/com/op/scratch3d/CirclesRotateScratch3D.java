package com.op.scratch3d;

import com.owens.oobjloader.builder.ArcScratchDefs;
import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.FaceVertex;
import com.owens.oobjloader.builder.VertexGeometric;

import java.io.FileNotFoundException;
import java.util.*;

public class CirclesRotateScratch3D extends Base {

    private String opDir = hostDir + "output/";
    private String objDir = hostDir + "objFiles/";

    //private String obj = "cube1";
    private String obj = "CubeEdges";
    //private String obj = "CubeHoles1";
    //private String obj = "CubeWalls2";
    //private String obj = "CubeNumbers1";
    //private String obj = "Letter-S";
    //private String obj = "SW-Falcon6";
    //private String obj = "cone1";
    private String src = "CIRSROTscratch3D-" + obj;
    // double dpi = 1000;
    //double dpi = 3.779527559055118;
    double dpi = 300;
    double mm2in = 25.4;
    double radmm = 50;
    private double wmm = radmm * 2;
    private double hmm = radmm * 2;
    private double w = dpi * (wmm / mm2in);
    private double h = dpi * (hmm / mm2in);
    private int cx = (int) (w / 2.0);
    private int cy = (int) (h / 2.0);

    ArrayList<VertexGeometric> allPoints = new ArrayList<VertexGeometric>();
    ArrayList<Face> originalFaces = new ArrayList<Face>();
    double vanZ = 10;
    ObjLoader objLoader = new ObjLoader();
    SvgDrawer svgDrawer = new SvgDrawer(opDir, src, w, h, w * 0.05);
    VertexTransformer vertexTransformer;

    private double angInc = 1;
    private boolean occlude = true;
    private boolean drawBits = false;

    private double objectCenterRad = w* 0.375; //w * 0.4;
    private double scale = w * 0.045; //w*0.075; //45;
    private int numPaths = 0;

    private static CirclesRotateScratch3D scratch3D = new CirclesRotateScratch3D();

    public static void main(String[] args) throws Exception {
        scratch3D.draw();
    }

    private void draw() throws FileNotFoundException, Exception {
        objLoader.equalsTol = 0.0001;
        svgDrawer.startSVG(false, true, 1, 0.1, 0.5, false);

        originalFaces = objLoader.loadOBJ(objDir + obj, allPoints);
        orderAllPointsByZIncreasing(allPoints);
        vertexTransformer = new VertexTransformer(originalFaces, vanZ);

        drawAll();

        save();
    }

    private void drawAll() {
//        svgDrawer.startSVGPath(1);
        System.out.println("drawAllPoints started...");
        String sd1 = svgDrawer.addLine(cx - 10, cy - 10, cx + 10, cy + 10);
        svgDrawer.writeToSVG(sd1);
        String sd2 = svgDrawer.addLine(cx - 10, cy + 10, cx + 10, cy - 10);
        svgDrawer.writeToSVG(sd2);
//        svgDrawer.endSVGPath("red");

        //drawFromOriginals();

//        svgDrawer.startSVGPath(2);
//
        if (drawBits) {
            angInc = 2;
            //drawByPointsBit(-90);

            for (double a = 0; a < 360; a = a + 15) {
                drawByPointsBit(a);
            }

        } else {
            //drawByPoints(89);
            drawPoints();
        }

        svgDrawer.endSVGPath("black");

        System.out.println("drawAllPoints finished, numPaths = " + numPaths);
    }

    private void drawPoints() {
        Map<Double, ArrayList<ArcScratchDefs>> ang2visiblesByAng = new HashMap<>();
        for (double a = 0; a < 360; a = a + angInc) {
            ArrayList<ArcScratchDefs> visiblesAllPointsByAng = collectedDataByPoints(a);
            ang2visiblesByAng.put(a, visiblesAllPointsByAng);
        }
        int v = 0;
        for (VertexGeometric vg : allPoints) {
            System.out.println("point=" + v + " of " + allPoints.size());
            double st = 0;
            double arcSt = st;
            double arcEn = st + angInc;
            boolean lastArcOn = false;

            ArrayList<ArcScratchDefs> defsPerPoint1 = ang2visiblesByAng.get(0.0);
            ArcScratchDefs defs1 = defsPerPoint1.get(v);
            double r = defs1.r;
            double xc = defs1.cx;
            double yc = defs1.cy;
            for (double angInd = 0; angInd < 360; angInd = angInd + angInc) {
                ArrayList<ArcScratchDefs> defsPerPoint = ang2visiblesByAng.get(angInd);
                ArcScratchDefs defs = defsPerPoint.get(v);

                boolean arcOn = defs.visible;

                double arcSt2 = arcSt;
                double arcEn2 = arcEn;
                if (arcOn) {
                    if ((angInd == 360 - angInc)) {
                        drawSVGSrc(vg, r, xc, yc, arcSt2, arcEn2, 0);
                        //drawSVGSrc(vg, r, xc, yc, z, startPosAng, arcSt2, arcEn2 - angInc);
                    }
                    lastArcOn = true;
                    arcEn = arcEn + angInc;
                } else {
                    if (lastArcOn) {
                        drawSVGSrc(vg, r, xc, yc, arcSt2, arcEn2-angInc, 0);
                        //drawSVGSrc(vg, r, xc, yc, z, startPosAng, arcSt2, arcEn2 - angInc);
                        arcSt = arcEn;
                    }
                    lastArcOn = false;
                    arcEn = arcEn + angInc;
                    arcSt = arcSt + angInc;
                }
            }
            v++;
        }
    }

    private ArrayList<ArcScratchDefs> collectedDataByPoints(double a) {
        ArrayList<Face> rotatedFaces = getAllRotatedFacesByAngle(a);

        ArrayList<ArcScratchDefs> allDefs = new ArrayList<>();
        for (VertexGeometric point : allPoints) {
            VertexGeometric rotatedPoint = rotatePointByAngle(point, a);
            double[] xyzr = getPointsScaled(point);

            ArcScratchDefs defs = new ArcScratchDefs();
            defs.cx = xyzr[0];
            defs.cy = xyzr[1];
            defs.r = xyzr[3];
            defs.visible = (!occlude || (occlude && objLoader.isVertexVisibleForVertex(rotatedFaces, rotatedPoint)));
            allDefs.add(defs);
//            if (!occlude || (occlude && objLoader.isVertexVisibleForVertex(rotatedFaces, p1))) {
//                double s = a + angInc / 2;
//                double e = s - angInc;
////                drawSVGSrc(vg, r, x, y, s, e, i);
//            } else {
//            }
        }

        return allDefs;
    }

    private void drawByPointsBit(double a) {
        double aa = a;
        ArrayList<Face> rotatedFaces = getAllRotatedFacesByAngle(a);
        int i = 0;
        for (VertexGeometric vg : allPoints) {
            System.out.println("point=" + i + " of " + allPoints.size());

            VertexGeometric p1 = rotatePointByAngle(vg, a);
            double[] ps = getPointsScaled(vg);

            if (!occlude || (occlude && objLoader.isVertexVisibleForVertex(rotatedFaces, p1))) {
                double s =  a - angInc / 2;
                double e = s + angInc;
                drawSVGSrc(vg, ps[3], ps[0], ps[1], s, e, i);
            }
            i++;
        }
    }

    private double[] getPointsScaled(VertexGeometric vg) {
        double x = vg.x * scale;
        double y = vg.y * scale;
        double z = getScaleForPerspectiveAdjusts(vg) * scale;
        double r = objectCenterRad + z;

        double[] arr = {x, y, z, r};
        return arr;
    }

    private VertexGeometric rotatePointByAngle(VertexGeometric vg, double angle) {
        double x = vg.x * scale;
        double y = vg.y * scale;
        double z = getScaleForPerspectiveAdjusts(vg) * scale;
        double r = objectCenterRad + z;

        double xc = cx + x;
        double yc = cy + y;
        double ang = Math.toRadians(angle);
        double xx = xc + r * Math.cos(ang);
        double yy = yc - r * Math.sin(ang);

        VertexGeometric p1 = new VertexGeometric((float) xx, (float) yy, (float) vg.z);
        return p1;
    }
    private ArrayList<Face> getAllRotatedFacesByAngle(double a) {
        ArrayList<Face> rotatedFaces = new ArrayList<Face>();
        for (Face face : originalFaces) {
            Face newFace = new Face();
            for (FaceVertex fv : face.vertices) {
                FaceVertex fv2 = new FaceVertex();
                VertexGeometric vg = new VertexGeometric(fv.v.x, fv.v.y, fv.v.z);

                double x = vg.x * scale;
                double y = vg.y * scale;
                double z = getScaleForPerspectiveAdjusts(vg) * scale;
                double r = objectCenterRad + z;

                double xc = cx + x;
                double yc = cy + y;
                double ang = Math.toRadians(a);
                double xx = xc + r * Math.cos(ang);
                double yy = yc - r * Math.sin(ang);

                VertexGeometric p1 = new VertexGeometric((float) xx, (float) yy, (float) vg.z);

                fv2.v = p1;
                newFace.vertices.add(fv2);
            }
            rotatedFaces.add(newFace);
        }

        return rotatedFaces;
    }

    double getScaleForPerspectiveAdjusts(VertexGeometric p) {
        double sc = (p.z + 1) / 2;
        return sc;
    }

    void drawSVGSrc(VertexGeometric vg, double r, double xc, double yc, double stAng, double enAng, int frame) {
        double g = 0;
        double s = (stAng) + g;
        double e = (enAng) + g;
        double x = cx + xc;
        double y = cy - yc;
        int largeArcFlag = (e-s) <= 180 ? 0 : 1;
        int sweepFlag = 1; //(e-s) >= 270 ? 0 : 0;
        svgDrawer.drawAndAddArcD2(x, y, r, largeArcFlag, sweepFlag, s, e - 0.01);
    }

    private void save() throws Exception {
        svgDrawer.endSVG();
    }

}
