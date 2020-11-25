package com.op.scratch3d;

import com.owens.oobjloader.builder.*;
import javafx.geometry.Point3D;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class ArcScratch3DWithTexture extends Base {

    private String opDir = hostDir + "output/";
    private String objDir = hostDir + "objFiles/";
    private String texDir = hostDir + "textures/";
    private String animDir = hostDir + "animate/";
    private Graphics2D opG = null;
    private BufferedImage bi = null;
    private double ww = 0;
    private double hh = 0;

    // private String obj = "earth";
    // private String obj = "cubeLow";
    // private String obj = "cubeEdgeCut";
    // private String obj = "cubeCuts";
    // private String obj = "cubeHi";
    // private String obj = "spheres";
    // private String obj = "Falcon";
    // private String obj = "cubeLowWithEdges";
    // private String obj = "cubeHiStraight";
    // private String obj = "SW_DS";
    // private String obj = "tieFull";
    // private String obj = "spikey";
    // private String obj = "KD-Hinge";
    // private String obj = "KD-SphereHoles";
    // private String obj = "KD-Torus-Limpet";
    // private String obj = "KD-Shell";
    // private String obj = "cubeSpheresB";
    // private String obj = "coneHi";
    // private String obj = "sphereMed";
    // private String obj = "cubeHoleMax";
    // private String obj = "hook";
    // private String obj = "cubeHole2";
    // private String obj = "test-planes";
    // private String obj = "test-z";
    // private String obj = "test-pyramidSq";
    //private String obj = "pyramid";
    //private String obj = "CubeT";
    //private String obj = "TieLPT";
    //private String obj = "CubeWaves";
    //private String obj = "TorusWaves";
    private String obj = "CubeHoles";
    private String textFileSuffix = "";

    double sqOffF = 0.01;
    boolean doClipToSqOff = true;
    boolean selectedOnly = false;
    boolean adjustForPerspective = true;
    boolean occlude = true;

    private String src = "ARCscratch3DText-" + obj;
    double dpi = 200;
    double mm2in = 25.4;
    double scalemm = 100;
    double scaleMain = dpi * (scalemm / mm2in);

    private double wmm = scalemm;
    private double hmm = scalemm;
    private double w = dpi * (wmm / mm2in);
    private double h = dpi * (hmm / mm2in);

    double sweepAng = 45;
    double totalAngle = sweepAng * 2;
    double num = 20;
    double angInc = totalAngle / (num);
    //double sf = 0.65; //0.5 side cube, sweepAng=15
    double sf = 0.35; //1 side cube
    double greyLevel = 50;
    private int imageScanDelta = 10;
    boolean includeVerts = false;

    private int cx = (int) (w / 2.0);
    private int cy = (int) (h / 2.0);
    int totFaceUVs =0;

    ArrayList<VertexGeometric> allPoints = new ArrayList<VertexGeometric>();
    ArrayList<Face> originalFaces = new ArrayList<Face>();
    ArrayList<VertexGeometric> selectedVerts = new ArrayList<VertexGeometric>();
    public HashMap<String, ArrayList<Face>> groups;

    private static ArcScratch3DWithTexture scratch3D = new ArcScratch3DWithTexture();

    double vanZ = 10;
    double minRadF = 0.05;

    ObjLoader objLoader = new ObjLoader();
    SvgDrawer svgDrawer = new SvgDrawer(opDir, src, w, h, w * sqOffF);
    boolean savePNG = false;
    private VertexTransformer vertexTransformer;

    /**
     * @param args
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws XPathExpressionException
     */
    public static void main(String[] args) throws Exception {
        // scratch3D.paint();
        scratch3D.loadOBJ();

    }

    private void loadOBJ() throws Exception {
        init();
        // allPoints = adjustPoints(allPoints);
        originalFaces = objLoader.loadOBJ(objDir + obj, allPoints);
        vertexTransformer = new VertexTransformer(originalFaces, vanZ);
        getUVPoints(originalFaces);

        if (selectedOnly) {
            selectedVerts = objLoader.loadOBJSelectedVerts(objDir + obj + "_sel");
        }

        groups = objLoader.groups;
        drawAllPoints();
        save();

    }

    private void getUVPoints(ArrayList<Face> faces) {
        for (Face face : faces) {
            double uv[][] = {{0, 0}, {0, 0}, {0, 0}};
            double xyz[][] = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
            uv[0][0] = face.vertices.get(0).t.u;
            uv[0][1] = face.vertices.get(0).t.v;
            Point2D.Double uvA = new Point2D.Double(uv[0][0], uv[0][1]);
            xyz[0][0] = face.vertices.get(0).v.x;
            xyz[0][1] = face.vertices.get(0).v.y;
            xyz[0][2] = face.vertices.get(0).v.z;
            Point3D p3dA = new Point3D(xyz[0][0], xyz[0][1], xyz[0][2]);

            uv[1][0] = face.vertices.get(1).t.u;
            uv[1][1] = face.vertices.get(1).t.v;
            Point2D.Double uvB = new Point2D.Double(uv[1][0], uv[1][1]);
            xyz[1][0] = face.vertices.get(1).v.x;
            xyz[1][1] = face.vertices.get(1).v.y;
            xyz[1][2] = face.vertices.get(1).v.z;
            Point3D p3dB = new Point3D(xyz[1][0], xyz[1][1], xyz[1][2]);

            uv[2][0] = face.vertices.get(2).t.u;
            uv[2][1] = face.vertices.get(2).t.v;
            Point2D.Double uvC = new Point2D.Double(uv[2][0], uv[2][1]);
            xyz[2][0] = face.vertices.get(2).v.x;
            xyz[2][1] = face.vertices.get(2).v.y;
            xyz[2][2] = face.vertices.get(2).v.z;
            Point3D p3dC = new Point3D(xyz[2][0], xyz[2][1], xyz[2][2]);

            scanAllDots(face, uvA, uvB, uvC, p3dA, p3dB, p3dC);
            totFaceUVs = totFaceUVs + face.texturePoints.size();
        }
        System.out.println("tto scanned="+totFaceUVs);

    }

    private void scanAllDots(Face face, Point2D.Double uvA, Point2D.Double uvB, Point2D.Double uvC, Point3D p3dA, Point3D p3dB, Point3D p3dC) {
        TrianglesTest tt = new TrianglesTest();

        Polygon tri = new Polygon();
        Path2D.Double path = new Path2D.Double();
        path.moveTo(uvA.x, uvA.y);
        path.lineTo(uvB.x, uvB.y);
        path.lineTo(uvC.x, uvC.y);
        path.closePath();
        double www = path.getBounds2D().getWidth();
        double hhh = path.getBounds2D().getHeight();
        double imageWidthHeight = (double) ww;
        tri.reset();
        tri.addPoint((int) (uvA.x * imageWidthHeight), (int) (hh - uvA.y * imageWidthHeight));
        tri.addPoint((int) (uvB.x * imageWidthHeight), (int) (hh - uvB.y * imageWidthHeight));
        tri.addPoint((int) (uvC.x * imageWidthHeight), (int) (hh - uvC.y * imageWidthHeight));
        Rectangle rect = tri.getBounds();

        for (int y = rect.y; y < rect.y + rect.height; y = y + imageScanDelta) {
            for (int x = rect.x; x < rect.x + rect.width; x = x + imageScanDelta) {
                Point2D p1 = new Point2D.Double(x, y);
                if (tri.contains(p1)) {
                    int rgb = bi.getRGB(x, y);
                    Color col = new Color(rgb);
                    if (col.getRed() < greyLevel) {
//                        double xx = ((double) x - (double) (rect.x)) / rect.width;
//                        double yy = ((double) y - (double) (rect.y)) / rect.height;
//                        Point2D.Double p2 = new Point2D.Double(www * xx, hhh * yy);

                        //Point2D.Double p2 = new Point2D.Double(xx, yy);

                        double xx = ((double) x)/imageWidthHeight;
                        double yy = (imageWidthHeight - (double) y)/imageWidthHeight;
                        Point2D.Double p2 = new Point2D.Double(xx, yy);

                        Point3D p3d = tt.get3DPoint(uvA, uvB, uvC, p2, p3dA, p3dB, p3dC);
                        TexturePointArcDefs tpad = new TexturePointArcDefs();
                        tpad.p3d = p3d;
                        tpad.p2d = p2;

                        VertexGeometric vg = new VertexGeometric((float) p3d.getX(), (float) p3d.getY(), (float) p3d.getZ());

                        double sc = 1;
                        if (adjustForPerspective) {
                            sc = vertexTransformer.getScaleForPerspectiveAdjusts(vg);
                        }
                        double xxx = vg.x * sc;
                        double yyy = vg.y * sc;

                        // double x = vg.x;
                        // double y = vg.y;
                        double zzz = vg.z;
                        double rad = (minRadF) + (1 - minRadF) * (Math.abs(zzz));

                        if (vg.defs == null) {
                            vg.defs = new ArcScratchDefs();
                            vg.defs.cx = xxx;
                            // outward = 270 = '-'
                            vg.defs.cy = zzz > 0 ? yyy + rad : yyy - rad;
                            vg.defs.r = rad;
                            vg.defs.startPosAng = zzz > 0 ? 270 : 90;
                        }

                        tpad.vg = vg;
                        System.out.println("x,y"+x+","+y+" "+vg.toString());
                        face.addTexturePoint(tpad);
                    }
                }

            }
        }
    }

    private void drawAllPoints() {
        for (Face face : originalFaces) {
            for (FaceVertex fv : face.vertices) {
                VertexGeometric vg = fv.v;
                // vg = vertexTransformer.adjustPointForPerspective(vg);
                double sc = 1;
                if (adjustForPerspective) {
                    sc = vertexTransformer.getScaleForPerspectiveAdjusts(vg);
                }
                double x = vg.x * sc;
                double y = vg.y * sc;

                // double x = vg.x;
                // double y = vg.y;
                double z = vg.z;
                double rad = (minRadF) + (1 - minRadF) * (Math.abs(z));

                if (vg.defs == null) {
                    vg.defs = new ArcScratchDefs();
                    vg.defs.cx = x;
                    // outward = 270 = '-'
                    vg.defs.cy = z > 0 ? y + rad : y - rad;
                    vg.defs.r = rad;
                    vg.defs.startPosAng = z > 0 ? 270 : 90;
                }
            }
            for (TexturePointArcDefs tpad : face.texturePoints) {
                VertexGeometric vg = tpad.vg;
                // vg = vertexTransformer.adjustPointForPerspective(vg);
                double sc = 1;
                if (adjustForPerspective) {
                    sc = vertexTransformer.getScaleForPerspectiveAdjusts(vg);
                }
                double x = vg.x * sc;
                double y = vg.y * sc;

                // double x = vg.x;
                // double y = vg.y;
                double z = vg.z;
                double rad = (minRadF) + (1 - minRadF) * (Math.abs(z));

                if (vg.defs == null) {
                    vg.defs = new ArcScratchDefs();
                    vg.defs.cx = x;
                    // outward = 270 = '-'
                    vg.defs.cy = z > 0 ? y + rad : y - rad;
                    vg.defs.r = rad;
                    vg.defs.startPosAng = z > 0 ? 270 : 90;
                }
            }
        }

        ArrayList<VertexGeometric> orig = new ArrayList<VertexGeometric>();
        for (double a = -totalAngle / 2; a <= totalAngle / 2; a = a + angInc) {
            ArrayList<Face> rotatedFaces = new ArrayList<Face>();
            HashMap<VertexGeometric, VertexGeometric> orig2rot = new HashMap<VertexGeometric, VertexGeometric>();
            HashMap<VertexGeometric, VertexGeometric> textOrig2rot = new HashMap<VertexGeometric, VertexGeometric>();
            for (Face face : originalFaces) {
                Face rotatedFace = new Face();
                for (FaceVertex fv : face.vertices) {
                    VertexGeometric origv = fv.v;
                    ArcScratchDefs def = origv.defs;
                    double cx = def.cx;
                    double cy = def.cy;
                    double r = def.r;
                    double st = def.startPosAng;
                    double resAng = st + a;
                    double resAngRads = Math.toRadians(resAng);
                    double resx = cx + r * Math.cos(resAngRads);
                    double resy = cy + r * Math.sin(resAngRads);

                    FaceVertex rotatedfv = new FaceVertex();
                    VertexGeometric rotatedvg = new VertexGeometric((float) (resx), (float) (resy), origv.z);
                    rotatedfv.v = rotatedvg;
                    rotatedFace.vertices.add(rotatedfv);
                    orig2rot.put(origv, rotatedvg);
                    orig.add(origv);
                }
                rotatedFaces.add(rotatedFace);

                for (TexturePointArcDefs tpad : face.texturePoints) {
                    VertexGeometric origv = tpad.vg;
                    ArcScratchDefs def = origv.defs;
                    double cx = def.cx;
                    double cy = def.cy;
                    double r = def.r;
                    double st = def.startPosAng;
                    double resAng = st + a;
                    double resAngRads = Math.toRadians(resAng);
                    double resx = cx + r * Math.cos(resAngRads);
                    double resy = cy + r * Math.sin(resAngRads);

                    VertexGeometric rotatedvg = new VertexGeometric((float) (resx), (float) (resy), origv.z);
                    tpad.rotatedvg = rotatedvg;
                    textOrig2rot.put(origv, rotatedvg);
                    orig.add(origv);

                }
            }

            for (VertexGeometric origvg : textOrig2rot.keySet()) {
                VertexGeometric rotvg = textOrig2rot.get(origvg);
                boolean clipped = isVertexClipped(origvg.defs, a);
                boolean visible = objLoader.isTextureVertexVisible(rotatedFaces, rotvg);
                if ((!occlude && (doClipToSqOff && !clipped))) {
                    origvg.defs.arcs.add(true);
                } else if ((!occlude && (doClipToSqOff && clipped))) {
                    origvg.defs.arcs.add(false);
                } else if ((doClipToSqOff && clipped) || !visible) {
                    origvg.defs.arcs.add(false);
                } else {
                    origvg.defs.arcs.add(true);
                }
            }

            if(includeVerts) {
                for (VertexGeometric origvg : orig2rot.keySet()) {
                    VertexGeometric rotvg = orig2rot.get(origvg);
                    boolean clipped = isVertexClipped(origvg.defs, a);
                    boolean visible = objLoader.isVertexVisible(rotatedFaces, rotvg);
                    if ((!occlude && (doClipToSqOff && !clipped))) {
                        origvg.defs.arcs.add(true);
                    } else if ((!occlude && (doClipToSqOff && clipped))) {
                        origvg.defs.arcs.add(false);
                    } else if ((doClipToSqOff && clipped) || !visible) {
                        origvg.defs.arcs.add(false);
                    } else {
                        origvg.defs.arcs.add(true);
                    }
                }
            }

        } // all angs

        int c = 0;
        ArrayList<VertexGeometric> used = new ArrayList<VertexGeometric>();
        int arcCount = 0;
        boolean ended = false;
        for (Face face : originalFaces) {
            for (TexturePointArcDefs tpad : face.texturePoints) {
                VertexGeometric vg = tpad.vg;
                if (used.contains(vg)) {
                    continue;
                }
                if (selectedOnly && !selectedVertsContains(vg)) {
                    continue;
                }
                used.add(vg);

                System.out.println("c=" + c +"/"+totFaceUVs);
                c++;
            }
        }

        sortUsed(used);

        drawAllUsed(0, "red", used, true);
        drawAllUsed(1, "blue", used, false);
    }

    private void sortUsed(ArrayList<VertexGeometric> used) {
        int magz = 1000000;
        int magr = 1000;
        Collections.sort(used,
                new Comparator<VertexGeometric>(){
                    @Override
                    public int compare(VertexGeometric o1, VertexGeometric o2) {
                        int zSplit = (int)(o1.z - o2.z);
                        double r1 = Math.sqrt((o1.defs.cx * o1.defs.cx) +(o1.defs.cy * o1.defs.cy));
                        double r2 = Math.sqrt((o2.defs.cx * o2.defs.cx) +(o2.defs.cy * o2.defs.cy));
                        if (zSplit > 0) {
                            return magz + (int)(r1*magr-r2*magr);
                        } else {
                            return -magz -(int)(r1*magr-r2*magr);
                        }
                    }
                });
    }

    private void drawAllUsed(int arcCount, String col, ArrayList<VertexGeometric> used, boolean closer) {
        svgDrawer.startSVGPath(arcCount);
        for (VertexGeometric vg : used) {
            if ((closer && vg.z >= 0) || (!closer && vg.z <0)) {
                ArrayList<Boolean> arcs = vg.defs.arcs;
                drawVisibleArcs(arcs, vg);
            }
        }
        svgDrawer.endSVGPath(col);
    }

    private int drawVisibleArcs(ArrayList<Boolean> arcs, VertexGeometric vg) {
        double st = -totalAngle / 2;
        double ss = scaleMain * sf;
        double r = vg.defs.r * ss;
        // r = 0.25 * ss + 0.75 * vg.defs.r * ss;
        double xc = vg.defs.cx * ss;
        double yc = vg.defs.cy * ss;
        double z = vg.z;
        double startPosAng = vg.defs.startPosAng;

        double arcSt = st;
        double arcEn = st + angInc;
        boolean lastArcOn = false;

        int arcCount = 0;
        for (int i = 0; i < arcs.size(); i++) {
            double arcSt2 = arcSt + angInc;
            double arcEn2 = arcEn - angInc;
            boolean arcOn = arcs.get(i);
            if (arcOn) {
                if ((i == arcs.size() - 1)) {
                    drawSVGSrc(vg, r, xc, yc, z, startPosAng, arcSt2, arcEn2);
                    arcCount++;
                }
                lastArcOn = true;
                arcEn = arcEn + angInc;
            } else {
                if (lastArcOn) {
                    drawSVGSrc(vg, r, xc, yc, z, startPosAng, arcSt2, arcEn2);
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

    private boolean isVertexClipped(ArcScratchDefs defs, double a) {
        double ss = scaleMain * sf;
        double r = defs.r * ss;
        double xc = defs.cx * ss;
        double yc = defs.cy * ss;
        double st = defs.startPosAng;

        double g = 180;
        double y = -yc;
        double s = (st - a) + g;
        return svgDrawer.isClipped(cx + xc, cy + y, r, s - angInc);
    }

    private void drawVisibleArcs1(ArrayList<Boolean> arcs, VertexGeometric vg) {
        double st = -totalAngle / 2;
        double ss = scaleMain * sf;
        double r = vg.defs.r * ss;
        // r = 0.25 * ss + 0.75 * vg.defs.r * ss;
        double xc = vg.defs.cx * ss;
        double yc = vg.defs.cy * ss;
        double z = vg.z;
        double startPosAng = vg.defs.startPosAng;

        boolean started = true;
        boolean startedArc = true;
        double stAng = st;
        double enAng = st;
        for (int i = 0; i < arcs.size(); i++) {
            boolean arcOnOff = arcs.get(i);
            if (i == arcs.size() - 1 && startedArc) {
                drawSVGSrc(vg, r, xc, yc, z, startPosAng, stAng, enAng);
            }

            if (!started) {
                if (!arcOnOff) {
                    if (startedArc) {
                        drawSVGSrc(vg, r, xc, yc, z, startPosAng, stAng, enAng);
                        stAng = enAng + angInc;
                        enAng = enAng + angInc;
                        startedArc = false;
                    } else {
                        stAng = enAng;
                        enAng = enAng + angInc;
                    }
                } else {
                    startedArc = true;
                    enAng = enAng + angInc;
                }
            } else {
                started = false;
                startedArc = false;
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

    private void drawSVGSrc(VertexGeometric vg, double r, double xc, double yc, double z, double startPosAng,
                            double stAng, double enAng) {
        double g = 180;
        double y = -yc;
        double s = (startPosAng - stAng) + g;
        double e = (startPosAng - enAng) + g;
        int zz = ((int) (2 * z)) + 2;
        svgDrawer.drawAndAddArc(cx + xc, cy + y, r, s, e);
        // for (int i = 0; i < zz; i++) {
        // svgDescriber.drawAndAddArc(cx + xc, cy + y, r, s, e);
        // System.out.println("z=" + z + " zz=" + zz);
        // }
    }

    private void drawArc(VertexGeometric p1, int c) {
        VertexGeometric p2 = p1;

        double x = p2.x;
        double y = p2.y;
        double z = p2.z * 0.5;
        double rad = scaleMain * 0.25 * (Math.abs(z));
        double xx = cx + x * scaleMain;
        double yy = cy + y * scaleMain;

        double midA = p1.z <= 0 ? 270 : 90;
        double angSt = midA - totalAngle / 2;
        double angEn = angSt + totalAngle;

        double aa = angInc;

        if (p2.defs == null) {
            p2.defs = new ArcScratchDefs();
            p2.defs.cx = xx - rad;
            p2.defs.cy = yy - rad;
            p2.defs.r = rad;
        }

        for (double a = angSt; a < angSt + totalAngle; a = a + aa) {
            int xtl = (int) (xx - rad);
            int ytl = (int) (yy - rad);
            int r = (int) rad;
            int d = (int) rad * 2;
            int aStart = (int) (a);
            int aEn = (int) (aa);
            svgDrawer.saveArc(xtl, ytl, xtl + r, ytl + r, r, d, aStart, aEn);
        }

        boolean svg = true;
        if (svg) {
            svgDrawer.drawAndAddArc(xx, h - yy, rad, angSt, angEn);
        }
    }

    private void init() throws IOException {
        System.out.println("initialising...");

        bi = ImageIO.read(new File(texDir + obj + textFileSuffix+".png"));
        opG = (Graphics2D) bi.getGraphics();
        ww = bi.getWidth();
        hh = bi.getHeight();

        svgDrawer.startSVG(true, false);

        System.out.println("...finished initialising");
    }

    private void save() throws Exception {
        svgDrawer.endSVG();
    }
}
