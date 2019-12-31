package com.op.scratch3d;

import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.FaceVertex;
import com.owens.oobjloader.builder.VertexGeometric;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import static java.awt.geom.Arc2D.OPEN;

public class RotateScratch3D extends Base {

    private String opDir = hostDir + "output/";
    private String objDir = hostDir + "objFiles/";

    // private String obj = "cubeLow";
    // private String obj = "coneHi";
    // private String obj = "cubeLowWithEdges";
    //private String obj = "VS";
    // private String obj = "heart";
    // private String obj = "DeathStarLow";
    // private String obj = "test-planes";
    // private String obj = "test-z";
    // private String obj = "test-pyramidSq";

    //private String obj = "cube1";
    //private String obj = "cube1b";
    //private String obj = "SW-LowPolyXWing1";
    //private String obj = "cube4-Edges";
    //private String obj = "cube2-Edges";
    private String obj = "Cone4-60";
    //private String obj = "House";
    //private String obj = "SW_DS_1b";
    //private String obj = "textT10";
    //private String obj = "Circles1";
    //private String obj = "SW-tieBest1";
    private String src = "ROTscratch3D-" + obj;
    private boolean saveSVG = true;
    // double dpi = 1000;
    double dpi = 300;
    double mm2in = 25.4;
    double radmm = 47;
    double radArcMaxmm = 40;
    double radArcMinmm = 36;
    private double wmm = radmm * 2;
    private double hmm = radmm * 2;
    private double w = dpi * (wmm / mm2in);
    private double h = dpi * (hmm / mm2in);
    private int cx = (int) (w / 2.0);
    private int cy = (int) (h / 2.0);
    double radArcMax = dpi * (radArcMaxmm / mm2in);
    double radArcMin = dpi * (radArcMinmm / mm2in);
    double middleF = 0.1;
    double unitArc = w * middleF; //dpi * (unitArcmm / mm2in);

    ArrayList<VertexGeometric> allPoints = new ArrayList<VertexGeometric>();
    ArrayList<Face> originalFaces = new ArrayList<Face>();
    double vanZ = 4;
    ObjLoader objLoader = new ObjLoader();
    SvgDrawer svgDrawer = new SvgDrawer(opDir, src, w, h, w * 0.05);
    VertexTransformer vertexTransformer;
    private double angInc = 1;
    private boolean adjustForPerspective = true;
    private boolean occlude = true;
    private boolean drawCentred = true;

    private ArrayList<BufferedImage> obis;
    private ArrayList<Graphics2D> opGs;
    private double obScale = 2;


    private static RotateScratch3D scratch3D = new RotateScratch3D();

    public static void main(String[] args) throws Exception {
        scratch3D.draw();
    }

    private void draw() throws FileNotFoundException, Exception {
        svgDrawer.startSVG(true, true, 1, 0.5, middleF);

        originalFaces = objLoader.loadOBJ(objDir + obj, allPoints);
        vertexTransformer = new VertexTransformer(originalFaces, vanZ);
        initPng();
        drawAllPoints();

        save();
    }

    private void drawAllPoints() {
        String sd1 = svgDrawer.addLine(cx - 10, cy - 10, cx + 10, cy + 10);
        svgDrawer.writeToSVG(sd1);
        String sd2 = svgDrawer.addLine(cx - 10, cy + 10, cx + 10, cy - 10);
        svgDrawer.writeToSVG(sd2);

        if (drawCentred) {
            drawFromOriginals();
        }
        drawFromCircles();

        savePngsForGif();
    }

    private void drawFromOriginals() {
        for (Face face : originalFaces) {
            for (FaceVertex fv : face.vertices) {
                VertexGeometric origVert = fv.v;
                if (adjustForPerspective) {
                    origVert = vertexTransformer.adjustPointForPerspective(origVert);
                }
                double xyzr[] = getArcData(origVert);
                double z = xyzr[2];
                double r = z > -0.5 ? 3 : 1;
                String cir = svgDrawer.addCircleD2(cx + xyzr[0], (cy + xyzr[1]), r);
                svgDrawer.writeToSVG(cir);
            }
        }
    }

    private void drawFromCircles() {
        for (double ang = 0; ang <= 360; ang = ang + angInc) {
            HashMap<VertexGeometric, VertexGeometric> orig2Rot = new HashMap<>();
            ArrayList<Face> newFaces = new ArrayList<Face>();
            for (Face face : originalFaces) {
                Face newFace = new Face();
                for (FaceVertex fv : face.vertices) {
                    FaceVertex newFV = new FaceVertex();
                    VertexGeometric origVert = fv.v;
                    if (adjustForPerspective) {
                        origVert = vertexTransformer.adjustPointForPerspective(origVert);
                    }
                    VertexGeometric newVG = getRotatedVertex(origVert, ang);
                    newFV.v = newVG;
                    newFace.vertices.add(newFV);
                    orig2Rot.put(fv.v, newVG);
                }
                newFaces.add(newFace);
            }

            calcVertices(newFaces, orig2Rot, ang);
        }

        for (VertexGeometric point : allPoints) {
            drawVisibleArcs(point);
        }
    }

    private void calcVertices(ArrayList<Face> newFaces, HashMap<VertexGeometric, VertexGeometric> orig2Rot, double ang) {
        for (VertexGeometric point : allPoints) {
//            if (adjustForPerspective) {
//                point = vertexTransformer.adjustPointForPerspective(point);
//            }
            if (occlude) {
                //VertexGeometric rotated = getRotatedVertex(point, ang);
                VertexGeometric rotated = orig2Rot.get(point);
                boolean visible = objLoader.isVertexVisible(newFaces, rotated);
                point.defs.arcs.add(visible);
            } else {
                point.defs.arcs.add(true);
            }
        }
    }

    private double[] getArcData(VertexGeometric v) {
        double x = unitArc * v.x;
        double y = unitArc * v.y;
        double s = (radArcMax - radArcMin);
        double z = s * (1 + v.z);
        double r = radArcMin + z;

        double[] arr = {x, y, z, r};
        //double[] arr = {v.x, v.y, v.z, v.z};
        return arr;
    }

    private VertexGeometric getRotatedVertex(VertexGeometric v, double ang) {
        double angOff = 0;
        double[] xyzr = getArcData(v);

        double[] arr = svgDrawer.getCircleD2(cx + xyzr[0], cy + xyzr[1], xyzr[3], angOff + ang, angOff + ang + angInc);

        VertexGeometric vg = new VertexGeometric((float) (arr[0]), (float) (h - arr[1]), (float) (xyzr[2]));
        return vg;
    }

    private void drawVisibleArcs(VertexGeometric vg) {
        ArrayList<Boolean> arcs = vg.defs.arcs;
        double startPosAng = vg.defs.startPosAng;

        double arcSt = startPosAng;
        double arcEn = startPosAng + angInc;
        boolean lastArcOn = false;
        for (int i = 0; i < arcs.size(); i++) {
            double arcSt2 = arcSt + angInc;
            double arcEn2 = arcEn - angInc;
            boolean arcOn = arcs.get(i);
            if (arcOn) {
                if ((i == arcs.size() - 1)) {
                    //drawSVGSrc(vg, r, xc, yc, z, startPosAng, arcSt2, arcEn2);
                    drawCircleArc(vg, arcSt2, arcEn2);
                }
                lastArcOn = true;
                arcEn = arcEn + angInc;
            } else {
                if (lastArcOn) {
                    //drawSVGSrc(vg, r, xc, yc, z, startPosAng, arcSt2, arcEn2);
                    drawCircleArc(vg, arcSt2, arcEn2);
                    arcSt = arcEn;
                }
                lastArcOn = false;
                arcEn = arcEn + angInc;
                arcSt = arcSt + angInc;
            }
        }
    }

    private void drawCircleArc(VertexGeometric v, double start, double end) {
        VertexGeometric orig = v;
        if (adjustForPerspective) {
            orig = vertexTransformer.adjustPointForPerspective(v);
        }
        double[] xyzr = getArcData(orig);
        double offAng = 0;

        String circ = svgDrawer.addCircleD2(cx + xyzr[0], cy + xyzr[1], xyzr[3], start + offAng, end + offAng);

        svgDrawer.writeToSVG(circ);

        drawPng(cx + xyzr[0], h - (cy + (xyzr[1])), orig.z, xyzr[3], start + offAng, end + offAng);
    }

    private void drawPng(double x, double y, double z, double r, double st, double en) {
        double ang = st - angInc;
        double d = 1;
        while (ang < en) {
            int n = (int) (ang / angInc);
            Color col = getColorForZ(z);
            double angDiff = ang + (angInc / 2) - d / 2;
            Shape shape = new Arc2D.Double((x - r) / obScale, (y - r) / obScale, (r * 2) / obScale, (r * 2) / obScale, angDiff, d, OPEN);
            opGs.get(n).setColor(col);
            opGs.get(n).draw(shape);
            ang = ang + angInc;
        }
    }

    private Color getColorForZ(double z) {
        float g = 0.2f + (float) ((-z / 2) * 0.7);
        float r = 1 - g;
        if (z > -0.5) {
            return new Color(0, 0, g);
        } else {
            return new Color(g, 0, 0);
        }
        //return Color.BLACK;
    }

    private Color getColorForAng(double ang) {
        float r = (float) (((ang / 3) % 120) / 120);
        float g = (float) (((2 * ang / 3) % 120) / 120);
        float b = (float) (((ang) % 120) / 120);

        //System.out.println("rgb="+r+","+g+","+b);
        return new Color(r, g, b);
    }

    private void save() throws Exception {
        if (saveSVG) {
            svgDrawer.endSVG();
        }
    }

    void initPng() {
        System.out.println("initialising...");
        int ww = (int) (w / obScale);
        int hh = (int) (h / obScale);

        obis = new ArrayList<>();
        opGs = new ArrayList<>();
        for (int i = 0; i < 360 / angInc; i++) {
            BufferedImage obi = new BufferedImage(ww, hh, BufferedImage.TYPE_INT_RGB);
            Graphics2D opG = (Graphics2D) obi.getGraphics();
            opG.setColor(Color.WHITE);
            opG.fillRect(0, 0, ww, hh);
            opG.setColor(Color.BLACK);
            int c = obi.getHeight() / 2;
            opG.rotate(Math.toRadians(angInc * 0.5 - 90 + angInc * i), c, c);
            obis.add(obi);
            opGs.add(opG);
        }
    }

    void savePngsForGif() {
        int i = 0;
        for (BufferedImage obi : obis) {
            double c = obi.getWidth();
            BufferedImage bi = obi.getSubimage((int) (c * 0.25), (int) (c * 0), (int) (c * 0.5), (int) (c * 0.5));
            savePNGFile(bi, opDir + "GIF/" + src + "_" + i + "_OUT.png", dpi);
            i++;
        }
    }
}
