package com.op.scratch3d;

import com.owens.oobjloader.builder.ArcScratchDefs;
import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.FaceVertex;
import com.owens.oobjloader.builder.VertexGeometric;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CirclesRotateScratch3D extends Base {

    private String opDir = hostDir + "output/";
    private String objDir = hostDir + "objFiles/";

    //private String obj = "cube1";
    //private String obj = "CubeWalls2";
    //private String obj = "CubeHoles1";
    private String obj = "CubeNumbers1";
    //private String obj = "cone1";
    private String src = "CIRSROTscratch3D-" + obj;
    private boolean saveSVG = true;
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
    private int numSamples = (int) (360 / angInc);
    private boolean adjustForPerspective = false;
    private boolean occlude = true;
    private boolean drawBits = true;

    private double objectCenterRad = w * 0.4;
    private double scale = 50;
    private int numPaths = 0;
    private ArrayList<BufferedImage> obis = new ArrayList<>();
    private ArrayList<Graphics2D> opGs = new ArrayList<>();
    private boolean drawForGif = false;
    private String animDir = hostDir + "animate/";
    private int frameTime = 50;
    private double glintAng = 2;

    private static CirclesRotateScratch3D scratch3D = new CirclesRotateScratch3D();

    public static void main(String[] args) throws Exception {
        scratch3D.draw();
    }

    private void draw() throws FileNotFoundException, Exception {
        objLoader.equalsTol = 0.0001;
        svgDrawer.startSVG(false, true, 1, 0.1, 0.5);

        originalFaces = objLoader.loadOBJ(objDir + obj, allPoints);
        vertexTransformer = new VertexTransformer(originalFaces, vanZ);
        if (drawForGif) {
            initPngs();
        }
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

        if (drawBits) {
            angInc = 1;
            //drawByPointsBit(-90);

            for (double a = 0; a < 360; a = a + 30) {
                drawByPointsBit(a);
            }

//            svgDrawer.endSVGPath("red");
//            svgDrawer.startSVGPath(2);
//            drawPoints();

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
            ArrayList<ArcScratchDefs> visiblesAllPointsByAng = collectedDataByPoints(-a);
            ang2visiblesByAng.put(a, visiblesAllPointsByAng);
        }
        int v = 0;
        for (VertexGeometric vg : allPoints) {
            System.out.println("point=" + v + " of " + allPoints.size());
            double st = 180;
            double arcSt = st;
            double arcEn = st + angInc;
            boolean lastArcOn = false;

            for (double angInd = 0; angInd < 360; angInd = angInd + angInc) {
                ArrayList<ArcScratchDefs> defsPerAng = ang2visiblesByAng.get(angInd);
                ArcScratchDefs defs = defsPerAng.get(v);

                double r = defs.r;
                double xc = defs.cx;
                double yc = defs.cy;
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
                double yy = yc + r * Math.sin(ang);

                VertexGeometric p1 = new VertexGeometric((float) xx, (float) yy, (float) vg.z);

                fv2.v = p1;
                newFace.vertices.add(fv2);
            }
            rotatedFaces.add(newFace);
        }
        int i = 0;

        ArrayList<ArcScratchDefs> allDefs = new ArrayList<>();
        for (VertexGeometric vg : allPoints) {

            double x = vg.x * scale;
            double y = vg.y * scale;
            double z = getScaleForPerspectiveAdjusts(vg) * scale;
            double r = objectCenterRad + z;

            double xc = cx + x;
            double yc = cy + y;
            double ang = Math.toRadians(a);
            double xx = xc + r * Math.cos(ang);
            double yy = yc + r * Math.sin(ang);

            VertexGeometric p1 = new VertexGeometric((float) xx, (float) yy, (float) vg.z);

            ArcScratchDefs defs = new ArcScratchDefs();
            defs.cx = x;
            defs.cy = y;
            defs.r = r;
            defs.visible = (!occlude || (occlude && objLoader.isVertexVisibleForVertex(rotatedFaces, p1)));
            allDefs.add(defs);
            if (!occlude || (occlude && objLoader.isVertexVisibleForVertex(rotatedFaces, p1))) {
                double s = a + angInc / 2;
                double e = s - angInc;
//                drawSVGSrc(vg, r, x, y, s, e, i);
            } else {
            }
        }

        return allDefs;
    }

    private void drawByPointsBit(double a) {
        double aa = a;
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
                double ang = Math.toRadians(aa);
                double xx = xc + r * Math.cos(ang);
                double yy = yc - r * Math.sin(ang);

                VertexGeometric p1 = new VertexGeometric((float) xx, (float) yy, (float) vg.z);

                fv2.v = p1;
                newFace.vertices.add(fv2);
            }
            rotatedFaces.add(newFace);
        }
        int i = 0;
        for (VertexGeometric vg : allPoints) {
            System.out.println("point=" + i + " of " + allPoints.size());

            double x = vg.x * scale;
            double y = vg.y * scale;
            double z = getScaleForPerspectiveAdjusts(vg) * scale;
            double r = objectCenterRad + z;

            double xc = cx + x;
            double yc = cy + y;
            double ang = Math.toRadians(aa);
            double xx = xc + r * Math.cos(ang);
            double yy = yc - r * Math.sin(ang);

            VertexGeometric p1 = new VertexGeometric((float) xx, (float) yy, (float) vg.z);

            if (!occlude || (occlude && objLoader.isVertexVisibleForVertex(rotatedFaces, p1))) {
                double s =  a - angInc / 2;
                double e = s + angInc;
                drawSVGSrcBit(vg, r, x, y, s, e, i);
            } else {
                int ii = 0;
            }

        }
    }

    double getScaleForPerspectiveAdjusts(VertexGeometric p) {
        double sc = (p.z + 1) / 2;
        return sc;
    }

    double getScaleForPerspectiveAdjustsOLD(VertexGeometric p) {
        double sc = (vanZ - p.z) / vanZ;
        return sc;
    }

    private int drawVisibleArcs(boolean[] visibles, VertexGeometric vg) {
        double x = vg.x * scale;
        double y = vg.y * scale;
        double z = getScaleForPerspectiveAdjusts(vg) * scale;
        double r = objectCenterRad + z;

        double st = -90;
        double arcSt = st;
        double arcEn = st + angInc;
        boolean lastArcOn = false;

        int arcCount = 0;
        for (int i = 0; i < visibles.length; i++) {
            double arcSt2 = arcSt;
            double arcEn2 = arcEn;
            boolean arcOn = visibles[i];
            if (arcOn) {
                if (drawForGif) {
                    drawOnPNG(cx + x, cy - y, r, arcSt2, arcSt2 + angInc, i);
                }
                if ((i == visibles.length - 1)) {
                    drawSVGSrc(vg, r, x, y, arcSt2, arcEn2 - angInc, i);
                    arcCount++;
                }
                lastArcOn = true;
                arcEn = arcEn + angInc;
            } else {
                if (lastArcOn) {
                    drawSVGSrc(vg, r, x, y, arcSt2, arcEn2 - angInc, i);
                    arcCount++;
                    arcSt = arcEn;
                }
                lastArcOn = false;
                arcEn = arcEn + angInc;
                arcSt = arcSt + angInc;
            }
        }

        return arcCount;
    }

    void drawSVGSrc(VertexGeometric vg, double r, double xc, double yc, double stAng, double enAng, int frame) {
        double g = 0;
        double s = (stAng) + g;
        double e = (enAng) + g;
        double x = cx + xc;
        double y = cy - yc;
        int largeArcFlag = (e-s) <= 180 ? 0 : 1;
        int sweepFlag = (e-s) >= 270 ? 0 : 0;
        svgDrawer.drawAndAddArc(x, y, r, largeArcFlag, sweepFlag, s, e + 0.1);
    }

    void drawSVGSrcBit(VertexGeometric vg, double r, double xc, double yc, double stAng, double enAng, int frame) {
        double g = 0;
        double s = (stAng) + g;
        double e = (enAng) + g;
//        svgDrawer.drawAndAddArc(cx + xc, cy - yc, r, s, e + 0.1);

        int largeArcFlag = (e-s) <= 180 ? 0 : 1;
        int sweepFlag = 1; //(e-s) > 270 ? 0 : 1;
        svgDrawer.drawAndAddArc(cx+xc, cy-yc, r, largeArcFlag, sweepFlag, s, e + 0.1);
    }

    private void drawOnPNG(double xc, double yc, double rad, double angSt, double angEn, int frame) {
        Graphics2D opG = opGs.get(frame);
        opG.setColor(Color.BLACK);
        int x1 = (int) (xc - rad);
        int y1 = (int) (yc - rad);
        int dd = (int) (rad * 2);
        double midAng = (angEn - angSt) / 2;
        int angOff = (int) (90 + angInc * frame - angInc / 2);
        int st = (int) (midAng - (glintAng / 2));
        int en = (int) (glintAng / 2);
        opG.drawArc(x1, y1, dd, dd, angOff + st, en);
    }

    private void save() throws Exception {
        if (saveSVG) {
            svgDrawer.endSVG();
        }
        if (drawForGif) {
            savePngsForGif();
            saveAsGIF();
        }
    }

    void savePngsForGif() {
        int i = 0;
        ArrayList<BufferedImage> subs = new ArrayList<>();
        for (BufferedImage obi : obis) {
            double cx = w / 2;
            double cy = (h / 2) - objectCenterRad;
            double d = scale * 5;
            BufferedImage bi = obi.getSubimage((int) (cx - (d / 2)), (int) (cy - (d / 2)), (int) (d), (int) (d));
            subs.add(bi);
            savePNGFile(obi, opDir + "GIF/" + src + "_" + i + "_OUT.png", dpi);
            i++;
        }
        obis = new ArrayList<>();
        for (BufferedImage sub : subs) {
            obis.add(sub);
        }
    }

    public void saveAsGIF() {
        BufferedImage firstImage = obis.get(0);

        String out = animDir + obj + ".gif";
        File fOut = new File(out);
        if (fOut.exists()) {
            fOut.delete();
        }

        ImageOutputStream output = null;
        try {
            output = new FileImageOutputStream(fOut);
        } catch (IOException e) {
            System.out.println("error gif " + e);
            e.printStackTrace();
        }

        try {
            GifSequenceWriter writer = new GifSequenceWriter(output,
                    firstImage.getType(), +frameTime + "", true);

            writer.writeToSequence(firstImage);
            for (int i = 0; i < numSamples + 1; i++) {
                BufferedImage nextImage = obis.get(i);
                writer.writeToSequence(nextImage);
            }

            writer.close();
            output.close();
            System.out.println("saved gif " + out);

        } catch (IOException e) {
            System.out.println("error gif " + e);
            e.printStackTrace();
        }

        //Base.savePNGFile(obi, animDir+obj+".png", 300);
    }

    private void initPngs() {
        System.out.println("initialising...");
        int ww = (int) w;
        int hh = (int) h;
        obis = new ArrayList<>();
        opGs = new ArrayList<>();
        if (drawForGif) {
            for (int i = 0; i < numSamples + 1; i++) {
                BufferedImage obi = new BufferedImage(ww, hh, BufferedImage.TYPE_INT_RGB);
                Graphics2D opG = (Graphics2D) obi.getGraphics();
                opG.setColor(Color.WHITE);
                opG.fillRect(0, 0, ww, hh);
                opG.setColor(Color.BLACK);
                int c = obi.getHeight() / 2;
                double ang = angInc * i;
                opG.rotate(Math.toRadians(ang), c, c);
                obis.add(obi);
                opGs.add(opG);
            }
        }
        System.out.println("initialised");
    }

}
