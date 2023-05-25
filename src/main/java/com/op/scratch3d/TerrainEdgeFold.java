package com.op.scratch3d;

import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.FaceVertex;
import com.owens.oobjloader.builder.VertexGeometric;
import com.owens.oobjloader.builder.VertexNormal;
import javafx.geometry.Point3D;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class TerrainEdgeFold extends Base {

    private String opDir = hostDir + "terrain/";

    BufferedImage obi;
    private Graphics2D opG;

    //private String obj = "paper";
    private String obj = "PlaneTris";
    //private String obj = "cube";
    private String src = "Terrain-" + obj;
    double dpi = 95.99999977;
    double mm2in = 25.4;
    double scalemm = 200;

    private double wmm = scalemm * 1;
    private double hmm = scalemm * 1;
    private double w = dpi * (wmm / mm2in);
    private double h = dpi * (hmm / mm2in);

    private int dmW = 20; // dot matrix side length
    private int dmH = 20; // dot matrix side length
    private VertexGeometric[][] all = new VertexGeometric[dmW][dmH];

    private static TerrainEdgeFold terrain = new TerrainEdgeFold();

    ObjLoader objLoader = new ObjLoader();
    SvgDrawer svgDescriber = new SvgDrawer(opDir, src, w, h);
    private ArrayList<Face> originalFaces = new ArrayList<Face>();
    private ArrayList<VertexGeometric> allPoints = new ArrayList<VertexGeometric>();
    boolean reverse = true;

    /**
     * @param args
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws XPathExpressionException
     */
    public static void main(String[] args) throws Exception {
        terrain.drawAll();
    }

    private void drawAll() throws Exception {
        init();
        drawAllPoints();
        save();

    }

    private void drawAllPoints() {
        ArrayList<Line3D> top = new ArrayList<>();
        ArrayList<Line3D> bot = new ArrayList<>();

        HashMap<Line3D, ArrayList<Face>> edge2Face = getEdge2Faces();
        for (Line3D edge : edge2Face.keySet()) {
            ArrayList<Face> connectedFaces = edge2Face.get(edge);
            Face con = connectedFaces.get(0);
            Face face = connectedFaces.get(1);
            con.calculateTriangleNormal();

            VertexGeometric conCen = getCentroid(con);
            VertexGeometric faceCen = getCentroid(face);
            int v = doesCornerNormalIntersectFaceNormal(conCen, con.faceNormal, faceCen, face.faceNormal);
            boolean same = v == 0;
            boolean converging = v > 0;

            if (!same) {
                if (converging && !top.contains(edge) && !bot.contains(edge)) {
                    top.add(edge);
                }
                if (!converging && !top.contains(edge) && !bot.contains(edge)) {
                    bot.add(edge);
                }
            }
        }

        drawEdges(top, bot, edge2Face);
    }

    private HashMap<Line3D, ArrayList<Face>> getEdge2Faces() {
        HashMap<Line3D, ArrayList<Face>> edge2Faces = new HashMap<>();
        ArrayList<Line3D> allEdges = new ArrayList<>();
        for (Face face : originalFaces) {
            face.calculateTriangleNormal();
            ArrayList<Face> connectedFaces = getConnectedFaces(face);
            ArrayList<VertexGeometric> uncon = getUnconnectedVerts(face, connectedFaces);

            for (Face con : connectedFaces) {
                con.calculateTriangleNormal();

                VertexGeometric[] edge = {null, null};
                int i = 0;
                for (FaceVertex cfv : con.vertices) {
                    for (FaceVertex ofv : face.vertices) {
                        if (cfv.v.equals(ofv.v)) {
                            edge[i] = cfv.v;
                            i++;
                        }
                    }

                }
                if (i == 2) {
                    Line3D line3D = getLine3D(edge);
                    if (edge2Faces.get(line3D) == null) {
                        edge2Faces.put(line3D, new ArrayList<>());
                    }
                    if (!allEdges.contains(line3D)) {
                        allEdges.add(line3D);
                        edge2Faces.get(line3D).add(face);
                        edge2Faces.get(line3D).add(con);
                    }
                }
            }
        }

        ArrayList<Line3D> linesToRemove = new ArrayList<>();
        for (Line3D line3D : edge2Faces.keySet()) {
            if (edge2Faces.get(line3D).isEmpty()) {
                linesToRemove.add(line3D);
            }
        }

        for (Line3D line3D : linesToRemove) {
            edge2Faces.remove(line3D);
        }
        return edge2Faces;
    }

    private Line3D getLine3D(VertexGeometric[] edge) {
        Line3D line3D = new Line3D();
        line3D.p1 = new Point3D(edge[0].x, edge[0].y, edge[0].z);
        line3D.p2 = new Point3D(edge[1].x, edge[1].y, edge[1].z);

        return line3D;
    }

    private boolean contains(ArrayList<VertexGeometric[]> all, VertexGeometric[] edge) {
        for (VertexGeometric[] as : all) {
            int cont = 0;
            for (VertexGeometric a : as) {
                if (objLoader.equals(a, edge[0]) || objLoader.equals(a, edge[1])) {
                    cont++;
                }
            }
            if (cont == 2) {
                return true;
            }
        }

        return false;
    }

    private VertexGeometric getCentroid(Face face) {
        double x1 = face.vertices.get(0).v.x;
        double y1 = face.vertices.get(0).v.y;
        double z1 = face.vertices.get(0).v.z;
        double x2 = face.vertices.get(1).v.x;
        double y2 = face.vertices.get(1).v.y;
        double z2 = face.vertices.get(1).v.z;
        double x3 = face.vertices.get(2).v.x;
        double y3 = face.vertices.get(2).v.y;
        double z3 = face.vertices.get(2).v.z;
        float xn = (float) ((x1 + x2 + x3) / 3.0);
        float yn = (float) ((y1 + y2 + y3) / 3);
        float zn = (float) ((z1 + z2 + z3) / 3);
        return new VertexGeometric(xn, yn, zn);
    }

    private int doesCornerNormalIntersectFaceNormal(VertexGeometric corner, VertexNormal cnorm, VertexGeometric fvg, VertexNormal fnorm) {
        double len = 10000;
        double x1 = corner.x;
        double y1 = corner.y;
        double z1 = corner.z;
        double x2 = x1 + cnorm.x * len;
        double y2 = y1 + cnorm.y * len;
        double z2 = z1 + cnorm.z * len;
        Line2D line2Dxz = new Line2D.Double(x1, z1, x2, z2);
        Line2D line2Dyz = new Line2D.Double(y1, z1, y2, z2);

        double ang = determineAngleBetweenNormals(cnorm, fnorm);
        if (Math.abs(ang) < 0.1) {
            return 0;
        }

        double xx1 = fvg.x;
        double yy1 = fvg.y;
        double zz1 = fvg.z;
        double xx2 = xx1 + fnorm.x * len;
        double yy2 = yy1 + fnorm.y * len;
        double zz2 = zz1 + fnorm.z * len;
        Line2D line2DDxz = new Line2D.Double(xx1, zz1, xx2, zz2);
        Line2D line2DDyz = new Line2D.Double(yy1, zz1, yy2, zz2);

        int i = 0;
        if (line2Dxz.intersectsLine(line2DDxz)) {
            i++;
        }
        if (line2Dyz.intersectsLine(line2DDyz)) {
            i++;
        }

        return i > 0 ? 1 : -1;
    }

    private double determineAngleBetweenNormals(VertexNormal cnorm, VertexNormal fnorm) {
        Point3D pc = new Point3D(cnorm.x, cnorm.y, cnorm.z);
        Point3D pf = new Point3D(fnorm.x, fnorm.y, fnorm.z);
        return angle(pc, pf);
    }

    private VertexGeometric getFaceVG(Face con, ArrayList<VertexGeometric> uncon) {
        for (FaceVertex fv : con.vertices) {
            for (VertexGeometric vg : uncon) {
                if (objLoader.equals(fv.v, vg)) {
                    return fv.v;
                }
            }
        }

        return null;
    }

    private ArrayList<VertexGeometric> getUnconnectedVerts(Face face, ArrayList<Face> connectedFaces) {
        objLoader.equalsTol = 0.1;
        ArrayList<VertexGeometric> vgs = new ArrayList<>();
        ArrayList<VertexGeometric> all = new ArrayList<>();
        for (FaceVertex fv1 : face.vertices) {
            all.add(fv1.v);
        }

        for (Face c : connectedFaces) {
            for (FaceVertex fv : c.vertices) {
                vgs.add(fv.v);
            }
        }

        vgs.removeAll(all);
        return vgs;
    }

    private void drawEdges(ArrayList<Line3D> top, ArrayList<Line3D> bot, HashMap<Line3D, ArrayList<Face>> edge2Face) {
        opG.setColor(Color.BLACK);
        drawPngEdge(top);
        svgDescriber.startSVGPath(1);
        drawSVGEdge(false, top, edge2Face);
        svgDescriber.endSVGPath("black");

        opG.setColor(Color.RED);
        drawPngEdge(bot);
        svgDescriber.startSVGPath(2);
        drawSVGEdge(true, bot, edge2Face);
        svgDescriber.endSVGPath("red");
    }

    private void drawSVGEdge(boolean reverseX, ArrayList<Line3D> lines, HashMap<Line3D, ArrayList<Face>> edge2Face) {
        ArrayList<Line2D> orderedLines = new ArrayList<>();
        for (Line3D line3D : lines) {
            ArrayList<Face> faces = edge2Face.get(line3D);

            int i = 0;
            double u1 = 0;
            double v1 = 0;
            double u2 = 0;
            double v2 = 0;
            for (FaceVertex fv : faces.get(0).vertices) {
                if (equals(line3D.p1, fv.v)) {
                    u1 = fv.t.u;
                    v1 = fv.t.v;
                }
            }
            for (FaceVertex fv : faces.get(1).vertices) {
                if (equals(line3D.p2, fv.v)) {
                    u2 = fv.t.u;
                    v2 = fv.t.v;
                }
            }
            orderedLines.add(new Line2D.Double(u1, v1, u2, v2));
        }

        //orderLinesByConnecting(orderedLines);
        for (Line2D line2D : orderedLines) {
            drawSVGUV(line2D.getX1(), line2D.getY1(), line2D.getX2(), line2D.getY2(), reverseX);
        }
    }

    private void orderLinesByConnecting(ArrayList<Line2D> lines) {
        Collections.sort(lines, new Comparator<Line2D>() {
            @Override
            public int compare(Line2D o1, Line2D o2) {

                boolean a2tob1 = equalPoint(o1.getP2(), o2.getP1());
                boolean a1tob2 = equalPoint(o1.getP1(), o2.getP2());
                boolean a1tob1 = equalPoint(o1.getP1(), o2.getP1());
                boolean a2tob2 = equalPoint(o1.getP2(), o2.getP2());
                if (a2tob1 || a1tob2) {
                    return 1;
                }

                return -1;
            }

        });
    }

    private boolean equalPoint(Point2D p1, Point2D p2) {
        double tol = 0.01;
        double x1 = p1.getX();
        double y1 = p1.getY();
        double x2 = p2.getX();
        double y2 = p2.getY();

        boolean p12 = Math.abs(x2-x1) < tol && Math.abs(y2-y1) < tol;
        return p12;
    }

    private boolean equals(Point3D p1, VertexGeometric v) {
        VertexGeometric v1 = new VertexGeometric((float) p1.getX(), (float) p1.getY(), (float) p1.getZ());
        return objLoader.equals(v1, v);
    }

    private void drawSVGUV(double u1, double v1, double u2, double v2, boolean reverseX) {
        double m = w;
        int x1 = (int) (m * u1);
        int y1 = (int) (m - m * v1);
        int x2 = (int) (m * u2);
        int y2 = (int) (m - m * v2);
        if (reverseX) {
            x1 = (int) (m - m * u1);
            x2 = (int) (m - m * u2);
        }
        svgDescriber.writeLine(x1, y1, x2, y2);
    }

    private void drawPngEdge(ArrayList<Line3D> bot) {
        int cx = (int) (w / 2);
        int cy = (int) (h / 2);
        double m = 30;
        for (Line3D edge : bot) {
            Point3D p1 = new Point3D(edge.p1.getX(), edge.p1.getY(), edge.p1.getZ());
            Point3D p2 = new Point3D(edge.p2.getX(), edge.p2.getY(), edge.p2.getZ());
            int x1 = (int) (m * p1.getX());
            int y1 = (int) (m * p1.getY());
            int x2 = (int) (m * p2.getX());
            int y2 = (int) (m * p2.getY());
            int y3 = (int) (h - (cy + y1));
            int y4 = (int) (h - (cy + y2));
            opG.drawLine(cx + x1, y3, cx + x2, y4);
        }
    }

    public double angle(Point3D p, Point3D q) {
        double x1 = q.getX();
        double z1 = q.getZ();
        double y1 = q.getY();
        double x2 = p.getX();
        double z2 = p.getZ();
        double y2 = p.getY();
        double var13 = (x2 * x1 + z2 * z1 + y2 * y1) / Math.sqrt((x2 * x2 + z2 * z2 + y2 * y2) * (x1 * x1 + z1 * z1 + y1 * y1));
        if (var13 > 1.0D) {
            return 0.0D;
        } else {
            double m = (z1 < 0 && z2 >= 0) || (z1 > 0 && z2 <= 0) ? -1 : 1;
            return var13 < -1.0D ? m * Math.toDegrees(Math.acos(var13)) : m * Math.toDegrees(Math.acos(var13));
        }
    }

    private ArrayList<Face> getConnectedFaces(Face face) {
        ArrayList<Face> connected = new ArrayList<>();
        for (Face originalFace : originalFaces) {
            int f = 0;
            for (FaceVertex ofv : originalFace.vertices) {
                VertexGeometric ovg = ofv.v;
                for (FaceVertex fv : face.vertices) {
                    if (fv.v.equals(ovg)) {
                        f++;
                    }
                }
                if (f == 2) {
                    if (!originalFace.equals(face) && !connected.contains(originalFace)) {
                        connected.add(originalFace);
                    }
                }
            }
        }
        return connected;
    }

    private void init() throws IOException {
        System.out.println("initialising...");
        int ww = (int) w;
        int hh = (int) h;
        originalFaces = objLoader.loadOBJ(opDir + obj, allPoints);
        obi = new BufferedImage(ww, hh, BufferedImage.TYPE_INT_RGB);
        opG = (Graphics2D) obi.getGraphics();
        opG.setColor(Color.WHITE);
        opG.fillRect(0, 0, ww, hh);
        opG.setColor(Color.BLACK);

        svgDescriber.startSVG(true, false);

        System.out.println("...finished initialising");
    }

    private void save() throws Exception {
        svgDescriber.endSVG();
        savePNGFile(obi, opDir + src + "_OUT.png", dpi);
    }
}
