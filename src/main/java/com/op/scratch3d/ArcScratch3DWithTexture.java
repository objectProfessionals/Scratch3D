package com.op.scratch3d;

public class ArcScratch3DWithTexture {

}
//package com.op.scratch3d;
//
//import java.awt.Color;
//import java.awt.Graphics2D;
//import java.awt.Polygon;
//import java.awt.Rectangle;
//import java.awt.geom.Path2D;
//import java.awt.geom.Point2D;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import javax.imageio.ImageIO;
//import javax.vecmath.Point2d;
//import javax.vecmath.Point3d;
//import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.xpath.XPathExpressionException;
//
//import org.xml.sax.SAXException;
//
//import com.owens.oobjloader.builder.ArcScratchDefs;
//import com.owens.oobjloader.builder.Face;
//import com.owens.oobjloader.builder.FaceVertex;
//import com.owens.oobjloader.builder.TexturePointArcDefs;
//import com.owens.oobjloader.builder.VertexGeometric;
//
//public class ArcScratch3DWithTexture extends Base{
//
//    private String dir = "host/images/out/misc/scratch3d/";
//    private String opDir = "../output/";
//    private String objDir = "objFiles/";
//    private String texDir = "textures/";
//    private Graphics2D opG = null;
//    private BufferedImage bi = null;
//    private double ww = 0;
//    private double hh = 0;
//
//    // private String obj = "earth";
//    // private String obj = "cubeLow";
//    // private String obj = "cubeEdgeCut";
//    // private String obj = "cubeCuts";
//    // private String obj = "cubeHi";
//    // private String obj = "spheres";
//    // private String obj = "Falcon";
//    // private String obj = "cubeLowWithEdges";
//    // private String obj = "cubeHiStraight";
//    // private String obj = "SW_DS";
//    // private String obj = "tieFull";
//    // private String obj = "spikey";
//    // private String obj = "KD-Hinge";
//    // private String obj = "KD-SphereHoles";
//    // private String obj = "KD-Torus-Limpet";
//    // private String obj = "KD-Shell";
//    // private String obj = "cubeSpheresB";
//    // private String obj = "coneHi";
//    // private String obj = "sphereMed";
//    // private String obj = "cubeHoleMax";
//    // private String obj = "hook";
//    // private String obj = "cubeHole2";
//    // private String obj = "test-planes";
//    // private String obj = "test-z";
//    // private String obj = "test-pyramidSq";
//    private String obj = "CubeSimple";
//
//    boolean doClip = false;
//    boolean selectedOnly = false;
//    boolean adjustForPerspective = true;
//    boolean occlude = true;
//
//    private String src = "ARCscratch3D-" + obj;
//    double dpi = 300;
//    double mm2in = 25.4;
//    double scalemm = 30;
//    double scaleMain = dpi * (scalemm / mm2in);
//    double sf = 1.1;
//
//    private double wmm = scalemm * 3;
//    private double hmm = scalemm * 3;
//    private double w = dpi * (wmm / mm2in);
//    private double h = dpi * (hmm / mm2in);
//
//    double sweepAng = 45;
//    double ang = sweepAng * 2;
//    double num = 10;
//    double angInc = ang / (num);
//
//    private int cx = (int) (w / 2.0);
//    private int cy = (int) (h / 2.0);
//
//    ArrayList<VertexGeometric> allPoints = new ArrayList<VertexGeometric>();
//    ArrayList<Face> originalFaces = new ArrayList<Face>();
//    ArrayList<VertexGeometric> selectedVerts = new ArrayList<VertexGeometric>();
//    public HashMap<String, ArrayList<Face>> groups;
//
//    private static ArcScratch3DWithTexture scratch3D = new ArcScratch3DWithTexture();
//
//    double vanZ = 5;
//    double minRadF = 0.99;
//
//    ObjLoader objLoader = new ObjLoader();
//    SvgDrawer svgDescriber = new SvgDrawer(opDir, src, w, h);
//    boolean savePNG = false;
//    private VertexTransformer vertexTransformer;
//
//    /**
//     * @param args
//     * @throws SAXException
//     * @throws ParserConfigurationException
//     * @throws IOException
//     * @throws XPathExpressionException
//     */
//    public static void main(String[] args) throws Exception {
//        // scratch3D.paint();
//        scratch3D.loadOBJ();
//
//    }
//
//    private void loadOBJ() throws Exception {
//        init();
//        // allPoints = adjustPoints(allPoints);
//        originalFaces = objLoader.loadOBJ(dir + objDir + obj, allPoints);
//        vertexTransformer = new VertexTransformer(originalFaces, vanZ);
//        getUVPoints(originalFaces);
//
//        if (selectedOnly) {
//            selectedVerts = objLoader.loadOBJSelectedVerts(dir + objDir + obj + "_sel");
//        }
//
//        groups = objLoader.groups;
//        drawAllPoints();
//        save();
//
//    }
//
//    private void getUVPoints(ArrayList<Face> faces) {
//        for (Face face : faces) {
//            double uv[][] = { { 0, 0 }, { 0, 0 }, { 0, 0 } };
//            double xyz[][] = { { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 } };
//            uv[0][0] = face.vertices.get(0).t.u;
//            uv[0][1] = face.vertices.get(0).t.v;
//            Point2d A = new Point2d(uv[0]);
//            xyz[0][0] = face.vertices.get(0).v.x;
//            xyz[0][1] = face.vertices.get(0).v.y;
//            xyz[0][2] = face.vertices.get(0).v.z;
//            Point3d a = new Point3d(xyz[0]);
//
//            uv[1][0] = face.vertices.get(1).t.u;
//            uv[1][1] = face.vertices.get(1).t.v;
//            Point2d B = new Point2d(uv[1]);
//            xyz[1][0] = face.vertices.get(1).v.x;
//            xyz[1][1] = face.vertices.get(1).v.y;
//            xyz[1][2] = face.vertices.get(1).v.z;
//            Point3d b = new Point3d(xyz[1]);
//
//            uv[2][0] = face.vertices.get(2).t.u;
//            uv[2][1] = face.vertices.get(2).t.v;
//            Point2d C = new Point2d(uv[2]);
//            xyz[2][0] = face.vertices.get(2).v.x;
//            xyz[2][1] = face.vertices.get(2).v.y;
//            xyz[2][2] = face.vertices.get(2).v.z;
//            Point3d c = new Point3d(xyz[2]);
//
//            scanAllDots(face, A, B, C, a, b, c);
//        }
//
//    }
//
//    private void scanAllDots(Face face, Point2d A, Point2d B, Point2d C, Point3d a, Point3d b, Point3d c) {
//        TrianglesTest tt = new TrianglesTest();
//
//        Polygon tri = new Polygon();
//        Path2D.Double path = new Path2D.Double();
//        path.moveTo(A.x, A.y);
//        path.lineTo(B.x, B.y);
//        path.lineTo(C.x, C.y);
//        path.closePath();
//        double www = path.getBounds2D().getWidth();
//        double hhh = path.getBounds2D().getHeight();
//        double d = (double) ww;
//        tri.reset();
//        tri.addPoint((int) (A.x * d), (int) (hh - A.y * d));
//        tri.addPoint((int) (B.x * d), (int) (hh - B.y * d));
//        tri.addPoint((int) (C.x * d), (int) (hh - C.y * d));
//        Rectangle rect = tri.getBounds();
//
//        int delta = 4;
//        for (int y = rect.y; y < rect.y + rect.height; y = y + delta) {
//            for (int x = rect.x; x < rect.x + rect.width; x = x + delta) {
//                Point2D p1 = new Point2D.Double(x, y);
//                if (tri.contains(p1)) {
//                    int rgb = bi.getRGB(x, y);
//                    Color col = new Color(rgb);
//                    if (col.equals(Color.BLACK)) {
//                        double xx = ((double) x - (double) (rect.x)) / rect.width;
//                        double yy = ((double) y - (double) (rect.y)) / rect.height;
//                        Point2d p2 = new Point2d(www * xx, hhh * yy);
//                        Point3d p3d = tt.get3DPoint(A, B, C, p2, a, b, c);
//                        TexturePointArcDefs tpad = new TexturePointArcDefs();
//                        tpad.p3d = p3d;
//                        tpad.p2d = p2;
//
//                        VertexGeometric vg = new VertexGeometric((float) p3d.x, (float) p3d.y, (float) p3d.z);
//                        double sc = 1;
//                        if (adjustForPerspective) {
//                            sc = vertexTransformer.getScaleForPerspectiveAdjusts(vg);
//                        }
//
//                        vg.defs = new ArcScratchDefs();
//                        double xxx = vg.x * sc;
//                        double yyy = vg.y * sc;
//                        double zzz = vg.z;
//                        double rad = (1 - minRadF) + minRadF * (Math.abs(zzz));
//                        vg.defs.cx = xxx;
//                        // outward = 270 = '-'
//                        vg.defs.cy = zzz > 0 ? yyy + rad : yyy - rad;
//                        vg.defs.r = rad;
//                        vg.defs.startPosAng = zzz > 0 ? 270 : 90;
//
//                        tpad.vg = vg;
//                        face.addTexturePoint(tpad);
//                    }
//                }
//
//            }
//        }
//    }
//
//    private void drawAllPoints() {
//        if (occlude) {
//            drawTransformedFacesForArc();
//        }
//
//    }
//
//    public void drawTransformedFacesForArc() {
//        for (Face face : originalFaces) {
//            for (FaceVertex fv : face.vertices) {
//                VertexGeometric vg = fv.v;
//                double sc = 1;
//                if (adjustForPerspective) {
//                    sc = vertexTransformer.getScaleForPerspectiveAdjusts(vg);
//                }
//
//                if (vg.defs == null) {
//                    vg.defs = new ArcScratchDefs();
//                    double x = vg.x * sc;
//                    double y = vg.y * sc;
//                    double z = vg.z;
//                    double rad = (1 - minRadF) + minRadF * (Math.abs(z));
//                    vg.defs.cx = x;
//                    // outward = 270 = '-'
//                    vg.defs.cy = z > 0 ? y + rad : y - rad;
//                    vg.defs.r = rad;
//                    vg.defs.startPosAng = z > 0 ? 270 : 90;
//                }
//            }
//        }
//
//        ArrayList<VertexGeometric> orig = new ArrayList<VertexGeometric>();
//        for (double a = -ang / 2; a <= ang / 2; a = a + angInc) {
//            ArrayList<Face> rotatedFaces = new ArrayList<Face>();
//            HashMap<VertexGeometric, VertexGeometric> orig2rot = new HashMap<VertexGeometric, VertexGeometric>();
//            for (Face face : originalFaces) {
//                Face rotatedFace = new Face();
//                for (FaceVertex fv : face.vertices) {
//                    VertexGeometric origv = fv.v;
//                    ArcScratchDefs def = origv.defs;
//                    double cx = def.cx;
//                    double cy = def.cy;
//                    double r = def.r;
//                    double st = def.startPosAng;
//                    double resAng = st + a;
//                    double resAngRads = Math.toRadians(resAng);
//                    double resx = cx + r * Math.cos(resAngRads);
//                    double resy = cy + r * Math.sin(resAngRads);
//
//                    FaceVertex rotatedfv = new FaceVertex();
//                    VertexGeometric rotatedvg = new VertexGeometric((float) (resx), (float) (resy), origv.z);
//                    rotatedfv.v = rotatedvg;
//                    rotatedFace.vertices.add(rotatedfv);
//                    orig2rot.put(origv, rotatedvg);
//                    // orig.add(origv);
//                }
//                rotatedFaces.add(rotatedFace);
//
//                for (TexturePointArcDefs tpad : face.texturePoints) {
//                    VertexGeometric origv = tpad.vg;
//                    ArcScratchDefs def = origv.defs;
//                    double cx = def.cx;
//                    double cy = def.cy;
//                    double r = def.r;
//                    double st = def.startPosAng;
//                    double resAng = st + a;
//                    double resAngRads = Math.toRadians(resAng);
//                    double resx = cx + r * Math.cos(resAngRads);
//                    double resy = cy + r * Math.sin(resAngRads);
//
//                    VertexGeometric rotatedvg = new VertexGeometric((float) (resx), (float) (resy), origv.z);
//                    tpad.rotatedvg = rotatedvg;
//                    orig2rot.put(origv, rotatedvg);
//                    orig.add(origv);
//
//                }
//            }
//
//            for (VertexGeometric origvg : orig2rot.keySet()) {
//                VertexGeometric rotvg = orig2rot.get(origvg);
//                boolean clipped = isVertexClipped(origvg.defs, a);
//                boolean visible = objLoader.isVertexVisible(rotatedFaces, rotvg);
//                if ((doClip && clipped) || !visible) {
//                    origvg.defs.arcs.add(false);
//                } else {
//                    origvg.defs.arcs.add(true);
//                }
//            }
//        } // all angs
//
//        ArrayList<VertexGeometric> used = new ArrayList<VertexGeometric>();
//        for (VertexGeometric vg : orig) {
//            if (used.contains(vg)) {
//                continue;
//            }
//            if (selectedOnly && !selectedVertsContains(vg)) {
//                continue;
//            }
//            used.add(vg);
//
//            ArrayList<Boolean> arcs = vg.defs.arcs;
//            drawVisibleArcs(arcs, vg);
//        }
//    }
//
//    private void drawVisibleArcs(ArrayList<Boolean> arcs, VertexGeometric vg) {
//        double st = -ang / 2;
//        double ss = scaleMain * sf;
//        double r = vg.defs.r * ss;
//        // r = 0.25 * ss + 0.75 * vg.defs.r * ss;
//        double xc = vg.defs.cx * ss;
//        double yc = vg.defs.cy * ss;
//        double z = vg.z;
//        double startPosAng = vg.defs.startPosAng;
//
//        double arcSt = st;
//        double arcEn = st + angInc;
//        boolean lastArcOn = false;
//        for (int i = 0; i < arcs.size(); i++) {
//            double arcSt2 = arcSt + angInc;
//            double arcEn2 = arcEn - angInc;
//            boolean arcOn = arcs.get(i);
//            if (arcOn) {
//                if ((i == arcs.size() - 1)) {
//                    drawSVGSrc(vg, r, xc, yc, z, startPosAng, arcSt2, arcEn2);
//                }
//                lastArcOn = true;
//                arcEn = arcEn + angInc;
//            } else {
//                if (lastArcOn) {
//                    drawSVGSrc(vg, r, xc, yc, z, startPosAng, arcSt2, arcEn2);
//                    arcSt = arcEn;
//                }
//                lastArcOn = false;
//                arcEn = arcEn + angInc;
//                arcSt = arcSt + angInc;
//            }
//        }
//    }
//
//    private boolean isVertexClipped(ArcScratchDefs defs, double a) {
//        double ss = scaleMain * sf;
//        double r = defs.r * ss;
//        double xc = defs.cx * ss;
//        double yc = defs.cy * ss;
//        double st = defs.startPosAng;
//
//        double g = 180;
//        double y = -yc;
//        double s = (st - a) + g;
//        return svgDescriber.isClipped(cx + xc, cy + y, r, s - angInc);
//    }
//
//    private void drawVisibleArcs1(ArrayList<Boolean> arcs, VertexGeometric vg) {
//        double st = -ang / 2;
//        double ss = scaleMain * sf;
//        double r = vg.defs.r * ss;
//        // r = 0.25 * ss + 0.75 * vg.defs.r * ss;
//        double xc = vg.defs.cx * ss;
//        double yc = vg.defs.cy * ss;
//        double z = vg.z;
//        double startPosAng = vg.defs.startPosAng;
//
//        boolean started = true;
//        boolean startedArc = true;
//        double stAng = st;
//        double enAng = st;
//        for (int i = 0; i < arcs.size(); i++) {
//            boolean arcOnOff = arcs.get(i);
//            if (i == arcs.size() - 1 && startedArc) {
//                drawSVGSrc(vg, r, xc, yc, z, startPosAng, stAng, enAng);
//            }
//
//            if (!started) {
//                if (!arcOnOff) {
//                    if (startedArc) {
//                        drawSVGSrc(vg, r, xc, yc, z, startPosAng, stAng, enAng);
//                        stAng = enAng + angInc;
//                        enAng = enAng + angInc;
//                        startedArc = false;
//                    } else {
//                        stAng = enAng;
//                        enAng = enAng + angInc;
//                    }
//                } else {
//                    startedArc = true;
//                    enAng = enAng + angInc;
//                }
//            } else {
//                started = false;
//                startedArc = false;
//            }
//        }
//    }
//
//    private boolean selectedVertsContains(VertexGeometric vg) {
//        for (VertexGeometric v : selectedVerts) {
//            if (objLoader.equals(vg, v)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private void drawSVGSrc(VertexGeometric vg, double r, double xc, double yc, double z, double startPosAng,
//                            double stAng, double enAng) {
//        double g = 180;
//        double y = -yc;
//        double s = (startPosAng - stAng) + g;
//        double e = (startPosAng - enAng) + g;
//        int zz = ((int) (2 * z)) + 2;
//        svgDescriber.drawAndAddArc(cx + xc, cy + y, r, s, e);
//        // for (int i = 0; i < zz; i++) {
//        // svgDescriber.drawAndAddArc(cx + xc, cy + y, r, s, e);
//        // System.out.println("z=" + z + " zz=" + zz);
//        // }
//    }
//
//    private void drawArc(VertexGeometric p1, int c) {
//        VertexGeometric p2 = p1;
//
//        double x = p2.x;
//        double y = p2.y;
//        double z = p2.z * 0.5;
//        double rad = scaleMain * 0.25 * (Math.abs(z));
//        double xx = cx + x * scaleMain;
//        double yy = cy + y * scaleMain;
//
//        double midA = p1.z <= 0 ? 270 : 90;
//        double angSt = midA - ang / 2;
//        double angEn = angSt + ang;
//
//        double aa = angInc;
//
//        if (p2.defs == null) {
//            p2.defs = new ArcScratchDefs();
//            p2.defs.cx = xx - rad;
//            p2.defs.cy = yy - rad;
//            p2.defs.r = rad;
//        }
//
//        for (double a = angSt; a < angSt + ang; a = a + aa) {
//            int xtl = (int) (xx - rad);
//            int ytl = (int) (yy - rad);
//            int r = (int) rad;
//            int d = (int) rad * 2;
//            int aStart = (int) (a);
//            int aEn = (int) (aa);
//            svgDescriber.saveArc(xtl, ytl, xtl + r, ytl + r, r, d, aStart, aEn);
//        }
//
//        boolean svg = true;
//        if (svg) {
//            svgDescriber.drawAndAddArc(xx, h - yy, rad, angSt, angEn);
//        }
//    }
//
//    private void init() throws IOException {
//        System.out.println("initialising...");
//
//        bi = ImageIO.read(new File(dir + texDir + obj + ".png"));
//        opG = (Graphics2D) bi.getGraphics();
//        ww = bi.getWidth();
//        hh = bi.getHeight();
//
//        svgDescriber.startSVG(true, false);
//
//        System.out.println("...finished initialising");
//    }
//
//    private void save() throws Exception {
//        svgDescriber.endSVG();
//    }
//}
