package com.op.scratch3d;

import com.owens.oobjloader.builder.ArcScratchDefs;
import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.FaceVertex;
import com.owens.oobjloader.builder.VertexGeometric;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ArcScratch3D extends ArcScratchBase {

    // private String obj = "earth";
    //private String obj = "cubeLow";
    //private String obj = "cubeEdgeCut";
    //private String obj = "CubeCentered";
    //private String obj = "cubeCuts";
    //private String obj = "cubeHi";
    // private String obj = "spheres";
    // private String obj = "Falcon";
    // private String obj = "cubeLowWithEdges";
    // private String obj = "cubeHiStraight";
    // private String obj = "DeathStar";
    // private String obj = "tieFull";
    // private String obj = "spikey";
    //private String obj = "KD-SphereHoles";
    // private String obj = "spheres10";
    // private String obj = "coneHi";
    // private String obj = "sphereMed";
    // private String obj = "cubeHoleMax";
    // private String obj = "hook";
    // private String obj = "cubeHole2";
    // private String obj = "test-planes";
    // private String obj = "test-z";
    // private String obj = "test-pyramidSq";
    //private String obj = "KD-TorusKnot1";
    //private String obj = "KD-TorusKnot3";
    //private String obj = "KD-TorusKnotHi";
    //private String obj = "TorusKnot3-256";
    //private String obj = "KD-Spikey";
    //private String obj = "SP-Tetrahedron";
    //private String obj = "SP-Cube";
    //private String obj = "SP-Octahedron";
    //private String obj = "SP-Dodecahedron";
    //private String obj = "SP-Icosahedron";
    //private String obj = "KD-SPH_STRIP";
    //private String obj = "KD-Icosphere";
    //private String obj = "KD-Ripple";
    //private String obj = "KD-NJoint";
    //private String obj = "SW_Vader";
    //private String obj = "SW_Trooper";
    //private String obj = "SW-TieFull";
    //private String obj = "MetaBall";
    //private String obj = "TEXT-ILU";
    //private String obj = "KD-TorusKnot";
    //private String obj = "SW-LowPolyTie3";
    //private String obj = "SW-Falcon6";
    //private String obj = "SW-Tie";
    //private String obj = "SW-TieLow";
    //private String obj = "SW-TrooperPlane";
    //private String obj = "Gear";
    //private String obj = "pyramid";
    //private String obj = "CubeT";
    private String obj = "Cube";

    private String src = "ARCscratch3D-" + obj;

    private static ArcScratch3D scratch3D = new ArcScratch3D();

    /**
     * @param args
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws XPathExpressionException
     */
    public static void main(String[] args) throws Exception {
        scratch3D.drawAll();
    }

    protected void drawAll() throws Exception {
        initAll(obj, src, "");
        drawTransformedFacesForArc();
        save();
    }

    public void drawTransformedFacesForArc() {
        for (Face face : originalFaces) {
            for (FaceVertex fv : face.vertices) {
                VertexGeometric vg = fv.v;
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
            }

            for (VertexGeometric origvg : orig2rot.keySet()) {
                VertexGeometric rotvg = orig2rot.get(origvg);
                boolean clipped = isVertexClipped(rotvg, origvg.defs, a);
                boolean visible = doClipToSqOff && objLoader.isVertexVisibleForVertex(rotatedFaces, rotvg);
                if ((!occlude && (!clipped))) {
                    origvg.defs.arcs.add(true);
                } else if ((!occlude && (clipped))) {
                    origvg.defs.arcs.add(false);
                } else if ((clipped) || !visible) {
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
            for (FaceVertex fv : face.vertices) {
                VertexGeometric vg = fv.v;
                if (used.contains(vg)) {
                    continue;
                }
                if (selectedOnly && !selectedVertsContains(vg)) {
                    continue;
                }
                used.add(vg);

                ArrayList<Boolean> arcs = vg.defs.arcs;

                if (c % maxSVGsPerPath == 0) {
                    svgDrawer.startSVGPath(arcCount);
                    ended = false;
                    arcCount++;
                }
                drawVisibleArcs(arcs, vg);
                if (c % maxSVGsPerPath == maxSVGsPerPath - 1) {
                    svgDrawer.endSVGPath();
                    ended = true;
                }

                System.out.println("c=" + c);
                c++;
            }
        }
        if (!ended) {
            svgDrawer.endSVGPath();
        }
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
