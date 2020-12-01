package com.op.scratch3d;

import com.owens.oobjloader.builder.ArcScratchDefs;
import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.VertexGeometric;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class ArcScratchBase extends Base {
    String opDir = hostDir + "output/";
    String objDir = hostDir + "objFiles/";

    double sqOffF = 0.01;
    double dpi = 200;
    double mm2in = 25.4;
    double scalemm = 100; //127;
    double scaleMain = dpi * (scalemm / mm2in);
    double wmm = scalemm;
    double hmm = scalemm;
    double w = dpi * (wmm / mm2in);
    double h = dpi * (hmm / mm2in);
    int cx = (int) (w / 2.0);
    int cy = (int) (h / 2.0);
    int maxSVGsPerPath = 500;
    double vanZ = 5;//10
    double minRadF = 0.05;
    boolean doClipToSqOff = true;
    boolean selectedOnly = false;
    boolean adjustForPerspective = false;
    boolean occlude = true;

    double sf = 0.45;//1.1
    double sweepAng = 30;
    double totalAngle = sweepAng * 2;
    double num = 20;//20
    double angInc = totalAngle / num;

    ArrayList<VertexGeometric> allPoints = new ArrayList<VertexGeometric>();
    ArrayList<Face> originalFaces = new ArrayList<Face>();
    ArrayList<VertexGeometric> selectedVerts = new ArrayList<VertexGeometric>();
    HashMap<String, ArrayList<Face>> groups;
    ObjLoader objLoader = new ObjLoader();
    VertexTransformer vertexTransformer;
    SvgDrawer svgDrawer = null;
    SVGAnimator svgAnimator = null;


    void initAll(String obj, String src, String texSuff) throws Exception {
        svgDrawer = new SvgDrawer(opDir, src, w, h, w * sqOffF);
        svgAnimator = new SVGAnimator((int) w, (int) h, 10, num, sweepAng, angInc, obj+texSuff);

        System.out.println("initialising...");
        svgDrawer.startSVG(true, false);
        System.out.println("...finished initialising");

        originalFaces = objLoader.loadOBJ(objDir + obj, allPoints);
        vertexTransformer = new VertexTransformer(originalFaces, vanZ);

        if (selectedOnly) {
            selectedVerts = objLoader.loadOBJSelectedVerts(objDir + obj + "_sel");
        }

        groups = objLoader.groups;
    }

    protected abstract void drawTransformedFacesForArc();

    boolean isVertexClipped(VertexGeometric vg, ArcScratchDefs defs, double a) {
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

    boolean selectedVertsContains(VertexGeometric vg) {
        for (VertexGeometric v : selectedVerts) {
            if (objLoader.equals(vg, v)) {
                return true;
            }
        }
        return false;
    }

    void drawSVGSrc(VertexGeometric vg, double r, double xc, double yc, double z, double startPosAng,
                    double stAng, double enAng) {
        double g = 180;
        double y = -yc;
        double s = (startPosAng - stAng) + g;
        double e = (startPosAng - enAng) + g;
        svgDrawer.drawAndAddArc(cx + xc, cy + y, r, s, e);
        svgAnimator.addArc(cx + xc, cy + y, r, z, startPosAng + stAng, startPosAng + enAng);
    }

    void save() {
        svgDrawer.endSVG();
        svgAnimator.save();
    }

}
