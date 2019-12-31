package com.op.scratch3d;

import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.VertexGeometric;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class CirclesScratch3D extends Base {
    private String opDir = hostDir + "output/";
    private String objDir = hostDir + "objFiles/";

    private String obj = "CirsHeart";
    //private String obj = "T";

    private String src = "CIRSscratch3D-" + obj;
    double dpi = 90;
    double mm2in = 25.4;
    double scalemm = 100;
    private double wmm = scalemm * 3;
    private double hmm = scalemm * 3;
    private double w = dpi * (wmm / mm2in);
    private double h = dpi * (hmm / mm2in);
    private double cx = (w / 2.0);
    private double cy = (h / 2.0);
    private BufferedImage ibi;

    ArrayList<VertexGeometric> allPoints = new ArrayList<VertexGeometric>();
    ArrayList<Face> originalFaces = new ArrayList<Face>();
    double vanZ = 5;
    ObjLoader objLoader = new ObjLoader();
    SvgDrawer svgDrawer = new SvgDrawer(opDir, src, w, h);
    VertexTransformer vertexTransformer;

    double num = 180;
    private double radSc = 50;
    private int totRotAng = 360;
    private double numFrames = 12;
    private double incRotAng = totRotAng / numFrames; // 6;
    private double arcAngHalf = incRotAng / 2;//3

    private static CirclesScratch3D scratch3D = new CirclesScratch3D();

    public static void main(String[] args) throws IOException {
        scratch3D.draw();
    }

    private void draw() throws FileNotFoundException, IOException {
        svgDrawer.startSVG(false, true, 1, 0.125, 0.5);


        initAllPoints();

        drawAllPoints();

        svgDrawer.endSVG();
    }

    private void initAllPoints() throws IOException {
//        VertexGeometric v1 = new VertexGeometric(0, 1, 0);
//        allPoints.add(v1);
//        VertexGeometric v2 = new VertexGeometric(0, -1, 0);
//        allPoints.add(v2);
//        VertexGeometric v3 = new VertexGeometric(1, 0, 0);
//        allPoints.add(v3);
//        VertexGeometric v4 = new VertexGeometric(-1, 0, 0);
//        allPoints.add(v4);

//        double rad = 1;
//        for(double a = 0; a < 360; a = a + 30) {
//            float x = (float) (rad * Math.cos(Math.toRadians(a)));
//            float y = (float) (rad * Math.sin(Math.toRadians(a)));
//            VertexGeometric v = new VertexGeometric(x, y, 0);
//            allPoints.add(v);
//        }

        allPoints = new ArrayList<>();
        originalFaces = objLoader.loadOBJ(objDir + obj, allPoints);
        vertexTransformer = new VertexTransformer(originalFaces, vanZ);

    }

    private void drawAllPoints() {
        String sd1 = svgDrawer.addLine(cx - 10, cy - 10, cx + 10, cy + 10);
        svgDrawer.writeToSVG(sd1);
        String sd2 = svgDrawer.addLine(cx - 10, cy + 10, cx + 10, cy - 10);
        svgDrawer.writeToSVG(sd2);

        for (VertexGeometric p : allPoints) {
            drawCircle(p.x, p.y, p.z);
        }
    }

    private void drawCircle(double x, double y, double z) {
        double angD = 360 / num;
        double rIn = w * 0.125;
        double rOut = w * 0.5;
        double rad = 2 * (rOut - rIn) / 2;
        double a = 1;
        double b = 0.5;//0.5
        double c = 0.01;//0.1
        double offx = -0.25 * x;
        double offy = -0.25 * y;
        double ox = offx * rad;
        double oy = offy * rad;
        double scx = 1;
        double scy = 1;

        double offAng = Math.atan2(x, y);

        for (double ang = 0; ang < 360; ang = ang + angD) {
            double angRad = Math.toRadians(ang);
            double yFr = 1; //0.5 + 0.5 * Math.abs(Math.cos(angRad));
            double xx = rad * (a * Math.cos(angRad));
            double yy = rad * yFr * ((b + c * Math.cos(angRad)) * Math.sin(angRad));
            double r = Math.sqrt(xx * xx + yy * yy);
            double theta = -Math.PI * 0.5 + Math.atan2(yy, xx) - offAng;
            double xxx = scx * r * Math.cos(theta);
            double yyy = scy * r * Math.sin(theta);
            if (ang == 0) {
                String sb = svgDrawer.moveTo(cx + xxx + ox, cy + yyy + oy);
                svgDrawer.writeToSVG(sb);
            } else {
                String sb = svgDrawer.lineTo(cx + xxx + ox, cy + yyy + oy);
                svgDrawer.writeToSVG(sb);
            }
        }

    }

    private void drawCircle2(double x, double y, double z) {
        double angD = 2 * Math.PI / 180.0;
        double rIn = w * 0.125;
        double rOut = w * 0.5;
        for (double ang = 0; ang < Math.PI; ang = ang + angD) {
            double aFr = ang / (Math.PI);
            double a = -ang - Math.PI * 0.5;
            double a1 = a + angD / 2;
            double a2 = a - angD / 2;

            double rad = rOut - (rOut - rIn) * aFr;

            if (ang == 0) {
                double xx = rad * Math.cos(a1);
                double yy = rad * Math.sin(a1);
                String sb = svgDrawer.moveTo(cx + xx, cy + yy);
                svgDrawer.writeToSVG(sb);
                xx = rad * Math.cos(a2);
                yy = rad * Math.sin(a2);
                sb = svgDrawer.lineTo(cx + xx, cy + yy);
                svgDrawer.writeToSVG(sb);
            } else {
                double xx = rad * Math.cos(a1);
                double yy = rad * Math.sin(a1);
                String sb = svgDrawer.lineTo(cx + xx, cy + yy);
                svgDrawer.writeToSVG(sb);
                xx = rad * Math.cos(a2);
                yy = rad * Math.sin(a2);
                sb = svgDrawer.lineTo(cx + xx, cy + yy);
                svgDrawer.writeToSVG(sb);
            }
        }

    }
}
