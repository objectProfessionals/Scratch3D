package com.op.scratch3d;

import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.VertexGeometric;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class LinearRotateScratch3D extends Base {

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
    //private String obj = "cube1a";
    //private String obj = "cube1b";
    //private String obj = "SW-LowPolyXWing2A";
    //private String obj = "SW-Tie1A";
    //private String obj = "SW-Tie1B";
    //private String obj = "House";
    //private String obj = "SW-LowPolyXWing1";
    //private String obj = "Cube4-Edges";
    //private String obj = "cube2-Edges";
    //private String obj = "Cone4-60";
    //private String obj = "TextHeart";
    //private String obj = "Dodecahedron";
    //private String obj = "House";
    //private String obj = "SW_DS_1b";
    //private String obj = "textT10";
    //private String obj = "Circles1";
    //private String obj = "SW-tieBest1";
    //private String obj = "cube1";
    private String obj = "CubeWalls";
    //private String obj = "CubeHoles1";
    //private String obj = "CubeNumbers";
    //private String obj = "cone1";
    private String src = "LINROTscratch3D-" + obj;
    private boolean saveSVG = true;
    // double dpi = 1000;
    double dpi = 300;
    double mm2in = 25.4;
    double radmm = 47;
    double radArcMaxmm = 41;
    double radArcMinmm = 30;
    private double wmm = radmm * 2;
    private double hmm = radmm * 2;
    private double w = dpi * (wmm / mm2in);
    private double h = dpi * (hmm / mm2in);
    private int cx = (int) (w / 2.0);
    private int cy = (int) (h / 2.0);
    double radArcMax = dpi * (radArcMaxmm / mm2in);
    double radArcMin = dpi * (radArcMinmm / mm2in);

    ArrayList<VertexGeometric> allPoints = new ArrayList<VertexGeometric>();
    ArrayList<Face> originalFaces = new ArrayList<Face>();
    double vanZ = 5;
    ObjLoader objLoader = new ObjLoader();
    SvgDrawer svgDrawer = new SvgDrawer(opDir, src, w, h, w * 0.05);
    VertexTransformer vertexTransformer;
    private double sepFac = 5;

    private double angInc = 5;
    private double radFac = 50;
    private double numPng = 360 / angInc; //20;
    private boolean adjustForPerspective = true;
    private boolean occlude = true;
    private boolean dots = false;
    private boolean arcs = true;
    private double angArc = 10;
    private boolean byAngles = true;
    private boolean drawForGif = false;
    boolean selectedOnly = false;
    ArrayList<VertexGeometric> selectedVerts = new ArrayList<VertexGeometric>();

    private ArrayList<BufferedImage> obis;
    private ArrayList<Graphics2D> opGs;
    private double obScale = 2;
    private double objectCenterRad = w * 0.4;
    private double scale = 60;
    private int numPaths = 0;

    private static LinearRotateScratch3D scratch3D = new LinearRotateScratch3D();

    public static void main(String[] args) throws Exception {
        scratch3D.draw();
    }

    private void draw() throws FileNotFoundException, Exception {
        objLoader.equalsTol = 0.0001;
        svgDrawer.startSVG(false, true, 1, 0.1, 0.5);

        originalFaces = objLoader.loadOBJ(objDir + obj, allPoints);
        if (selectedOnly) {
            selectedVerts = objLoader.loadOBJSelectedVerts(objDir + obj + "_sel");
        }
        vertexTransformer = new VertexTransformer(originalFaces, vanZ);
        initPng();
        drawAll();

        save();
    }

    private void drawAll() {
        svgDrawer.startSVGPath(1);
        System.out.println("drawAllPoints started...");
        String sd1 = svgDrawer.addLine(cx - 10, cy - 10, cx + 10, cy + 10);
        svgDrawer.writeToSVG(sd1);
        String sd2 = svgDrawer.addLine(cx - 10, cy + 10, cx + 10, cy - 10);
        svgDrawer.writeToSVG(sd2);
        svgDrawer.endSVGPath("red");

        //drawFromOriginals();

        svgDrawer.startSVGPath(2);
        if (byAngles) {
            drawByAngles();
        } else {
            drawByPoints();
        }
        svgDrawer.endSVGPath("black");

        savePngsForGif();
        System.out.println("drawAllPoints finished, numPaths = " + numPaths);
    }

    private void drawByAngles() {
        for (double a = 0; a < 360; a = a + angInc) {
            System.out.println("ang=" + a);
            for (VertexGeometric p1 : allPoints) {
                ArrayList<Face> faces = vertexTransformer.getTransformedFacesRotateY(a, adjustForPerspective);
                VertexGeometric p2 = vertexTransformer.rotateVertexY(p1, a, adjustForPerspective);

                if (!occlude || (occlude && objLoader.isVertexVisibleForVertex(faces, p2))) {
                    draw(p2, a);
                }
            }
        }
    }

    private void drawByPoints() {
        int i = 0;
        for (VertexGeometric p1 : allPoints) {
            System.out.println("point=" + i + " of " + allPoints.size());
            for (double a = 0; a < 360; a = a + angInc) {
                ArrayList<Face> faces = vertexTransformer.getTransformedFacesRotateY(a, adjustForPerspective);
                VertexGeometric p2 = vertexTransformer.rotateVertexY(p1, a, adjustForPerspective);

                if (!occlude || (occlude && objLoader.isVertexVisibleForVertex(faces, p2))) {
                    draw(p2, a);
                }
            }
            i++;
        }
    }

    private void draw(VertexGeometric p2, double a) {
        if (dots) {
            drawVisibleDot(p2, a);
        } else {
            if (arcs) {
                drawVisibleArcs(p2, a);
            } else {
                drawVisibleLinears(p2, a);
            }
        }
        numPaths++;
    }

    private void drawVisibleDot(VertexGeometric vg, double ang) {
        double x = vg.x * scale;
        double y = vg.y * scale;
        double z = vg.z * scale;
        double rr = 2;

        double angRad = Math.toRadians(ang);
        double angRad2 = Math.toRadians(ang - 90);
        double ccx = cx + objectCenterRad * Math.cos(angRad2);
        double ccy = cy + objectCenterRad * Math.sin(angRad2);

        String lin1 = svgDrawer.addCircleD2(ccx, ccy, rr * 5, 0, 355);
        svgDrawer.writeToSVG(lin1);

        double xyAngRad = Math.atan2(y, x);
        double r = Math.sqrt(x * x + y * y);
        double xx = r * Math.cos(xyAngRad - angRad);
        double yy = r * Math.sin(xyAngRad - angRad);

        String lin = svgDrawer.addCircleD2(ccx + xx, ccy - yy, rr, 0, 355);
        svgDrawer.writeToSVG(lin);

    }

    private void drawVisibleLinears(VertexGeometric vg, double ang) {
        double x = vg.x * scale;
        double y = vg.y * scale;
        double z = vg.z * scale;
        double rSepF = 1 - (1.5 * scale + z) / (3 * scale);

        double len = 2;
        double sep = sepFac * rSepF;
        double c1 = sep - len * 2;
        double c2 = sep - len;

        double angRad = Math.toRadians(ang);
        double angRad2 = Math.toRadians(ang - 90);
        double ccx = cx + objectCenterRad * Math.cos(angRad2);
        double ccy = cy + objectCenterRad * Math.sin(angRad2);

        double xyAngRad = Math.atan2(y, x);
        double r = Math.sqrt(x * x + y * y);
        double xx = r * Math.cos(xyAngRad - angRad);
        double yy = r * Math.sin(xyAngRad - angRad);

        double xx1 = ccx + xx + c1 * Math.cos(angRad);
        double yy1 = ccy - yy + c1 * Math.sin(angRad);
        double xx2 = ccx + xx + c2 * Math.cos(angRad);
        double yy2 = ccy - yy + c2 * Math.sin(angRad);
        String lin = svgDrawer.addLine(xx1, yy1, xx2, yy2);
        svgDrawer.writeToSVG(lin);

        double xx11 = ccx + xx - c2 * Math.cos(angRad);
        double yy11 = ccy - yy - c2 * Math.sin(angRad);
        double xx22 = ccx + xx - c1 * Math.cos(angRad);
        double yy22 = ccy - yy - c1 * Math.sin(angRad);
        String lin2 = svgDrawer.addLine(xx11, yy11, xx22, yy22);
        svgDrawer.writeToSVG(lin2);
    }

    private void drawVisibleArcs(VertexGeometric vg, double ang) {
        double x = vg.x * scale;
        double y = vg.y * scale;
        double z = vg.z * scale;
        double rSepF = 0.1 + (1.5 * scale + z) / (3 * scale);

        double rad = radFac * rSepF;

        double angRad = Math.toRadians(ang);
        double angRad2 = Math.toRadians(ang - 90);
        double ccx = cx + objectCenterRad * Math.cos(angRad2);
        double ccy = cy + objectCenterRad * Math.sin(angRad2);

        double xyAngRad = Math.atan2(y, x);
        double r = Math.sqrt(x * x + y * y);
        double xx = r * Math.cos(xyAngRad - angRad);
        double yy = r * Math.sin(xyAngRad - angRad);

        double xx1 = ccx + xx - rad * Math.cos(angRad2);
        double yy1 = ccy - yy - rad * Math.sin(angRad2);
        double angIncOff = (angInc / 2);
        //double angIncOff = (angArc / 2);
        String lin = svgDrawer.addArc(xx1, yy1, rad, ang - angIncOff - 90, ang + angIncOff - 90);
        svgDrawer.writeToSVG(lin);
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
        if (drawForGif) {
            for (int i = 0; i < numPng; i++) {
                BufferedImage obi = new BufferedImage(ww, hh, BufferedImage.TYPE_INT_RGB);
                Graphics2D opG = (Graphics2D) obi.getGraphics();
                opG.setColor(Color.WHITE);
                opG.fillRect(0, 0, ww, hh);
                opG.setColor(Color.BLACK);
                int c = obi.getHeight() / 2;
                double angI = 360 / numPng;
                double ang = angI * 0.5 - 90 + angI * i;
                opG.rotate(Math.toRadians(ang), c, c);
                obis.add(obi);
                opGs.add(opG);
            }
        }
        System.out.println("initialised");
    }

    void savePngsForGif() {
        if (drawForGif) {
            int i = 0;
            for (BufferedImage obi : obis) {
                double c = obi.getWidth();
                BufferedImage bi = obi.getSubimage((int) (c * 0.25), (int) (c * 0), (int) (c * 0.5), (int) (c * 0.5));
                savePNGFile(bi, opDir + "GIF/" + src + "_" + i + "_OUT.png", dpi);
                i++;
            }
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
