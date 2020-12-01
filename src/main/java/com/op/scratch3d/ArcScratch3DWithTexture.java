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
import java.util.*;

public class ArcScratch3DWithTexture extends ArcScratchBase {

    private String texDir = hostDir + "textures/";
    private BufferedImage bi = null;
    private double ww = 0;
    private double hh = 0;

    private String obj = "CubeHoles";
    //private String obj = "Cube";
    private String textFileSuffix = "Texture";

    private String src = "ARCscratch3DText-" + obj;
    private int imageScanDelta = 10;

    private int totFaceUVs = 0;
    private double gLevel = 0.5;

    private static ArcScratch3DWithTexture scratch3D = new ArcScratch3DWithTexture();

    private Random random = new Random(0);

    /**
     * @param args
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws XPathExpressionException
     */
    public static void main(String[] args) throws Exception {
        // scratch3D.paint();
        scratch3D.drawAll();

    }

    protected void drawAll() throws Exception {
        initBI();
        initAll(obj, src, textFileSuffix);
        getUVPoints(originalFaces);
        drawTransformedFacesForArc();
        save();
    }

    private void initBI() throws IOException {
        bi = ImageIO.read(new File(texDir + obj + textFileSuffix + ".png"));
        ww = bi.getWidth();
        hh = bi.getHeight();
    }


    private void getUVPoints(ArrayList<Face> faces) {
        for (Face face : faces) {
            face.texturePoints.clear();
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
        System.out.println("tto scanned=" + totFaceUVs);

    }

    private void scanAllDots(Face face, Point2D.Double uvA, Point2D.Double uvB, Point2D.Double uvC, Point3D p3dA, Point3D p3dB, Point3D p3dC) {
        TrianglesTest tt = new TrianglesTest();

        Polygon tri = new Polygon();
        Path2D.Double path = new Path2D.Double();
        path.moveTo(uvA.x, uvA.y);
        path.lineTo(uvB.x, uvB.y);
        path.lineTo(uvC.x, uvC.y);
        path.closePath();
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
                    double g = ((double) (col.getRed() + col.getBlue() + col.getGreen())) / (255.0 * 3.0);
                    double rnd = random.nextDouble();
                    boolean paint = (g < gLevel || g < rnd);
                    if (paint) {
                        double xx = ((double) x) / imageWidthHeight;
                        double yy = (imageWidthHeight - (double) y) / imageWidthHeight;
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
                        System.out.println("x,y" + x + "," + y + " " + vg.toString());
                        face.addTexturePoint(tpad);
                    }
                }

            }
        }
    }

    public void drawTransformedFacesForArc() {
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

        for (double a = -sweepAng; a <= sweepAng; a = a + angInc) {
            ArrayList<Face> rotatedFaces = new ArrayList<Face>();
            HashMap<VertexGeometric, VertexGeometric> orig2rot = new HashMap<VertexGeometric, VertexGeometric>();
            HashMap<VertexGeometric, VertexGeometric> textOrig2rot = new HashMap<>();
            HashMap<VertexGeometric, Face> textV2rotFace = new HashMap<>();
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
                    textV2rotFace.put(rotatedvg, rotatedFace);
                }
            }

            for (VertexGeometric origvg : textOrig2rot.keySet()) {
                VertexGeometric rotvg = textOrig2rot.get(origvg);
                boolean clipped = isVertexClipped(rotvg, origvg.defs, a);
                boolean visible = objLoader.isVertexVisibleForVertex(rotatedFaces, textV2rotFace.get(rotvg), rotvg);
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

                System.out.println("c=" + c + "/" + totFaceUVs);
                c++;
            }
        }

        sortUsed(used);

        drawAllUsed(0, "red", used, true);
        drawAllUsed(1, "blue", used, false);

        svgAnimator.save();
    }

    private void sortUsed(ArrayList<VertexGeometric> used) {
        int magz = 1000000;
        int magr = 1000;
        Collections.sort(used,
                new Comparator<VertexGeometric>() {
                    @Override
                    public int compare(VertexGeometric o1, VertexGeometric o2) {
                        int zSplit = (int) (o1.z - o2.z);
                        double r1 = Math.sqrt((o1.defs.cx * o1.defs.cx) + (o1.defs.cy * o1.defs.cy));
                        double r2 = Math.sqrt((o2.defs.cx * o2.defs.cx) + (o2.defs.cy * o2.defs.cy));
                        if (zSplit > 0) {
                            return magz + (int) (r1 * magr - r2 * magr);
                        } else {
                            return -magz - (int) (r1 * magr - r2 * magr);
                        }
                    }
                });
    }

    private void drawAllUsed(int arcCount, String col, ArrayList<VertexGeometric> used, boolean closer) {
        svgDrawer.startSVGPath(arcCount);
        for (VertexGeometric vg : used) {
            if ((closer && vg.z >= 0) || (!closer && vg.z < 0)) {
                ArrayList<Boolean> arcs = vg.defs.arcs;
                drawVisibleArcs(arcs, vg);
            }
        }
        svgDrawer.endSVGPath(col);
    }

    private int drawVisibleArcs(ArrayList<Boolean> arcs, VertexGeometric vg) {
        double st = -sweepAng;
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
            double arcSt2 = arcSt;
            double arcEn2 = arcEn;
            boolean arcOn = arcs.get(i);
            if (arcOn) {
                if ((i == arcs.size() - 1)) {
                    drawSVGSrc(vg, r, xc, yc, z, startPosAng, arcSt2, arcEn2 - angInc);
                    arcCount++;
                }
                lastArcOn = true;
                arcEn = arcEn + angInc;
            } else {
                if (lastArcOn) {
                    drawSVGSrc(vg, r, xc, yc, z, startPosAng, arcSt2, arcEn2 - angInc);
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
}
