package com.op.paint.misc.scratch3d;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.VertexGeometric;

public class CircleScratch3D {
	private String dir = "host/images/out/misc/scratch3d/";
	private String opDir = "../output/";
	private String objDir = "objFiles/";
	// private String obj = "tie";
	// private String obj = "cubeHiSq";
	// private String obj = "coneLow";
	// private String obj = "cubeHiEdges";
	// private String obj = "cubeLow";
	private String obj = "cubeLowWithEdges";
	// private String obj = "coneLowWithEdges";
	// private String obj = "sphereMed";
	// private String obj = "DeathStarLow";
	// private String obj = "test-planes";
	// private String obj = "test-z";
	// private String obj = "test-pyramidSq";

	private String src = "CIRscratch3D-" + obj;
	double dpi = 300;
	double mm2in = 25.4;
	double scalemm = 25;
	double radArcmm = 27.5;
	private double wmm = scalemm * 3;
	private double hmm = scalemm * 3;
	private double w = dpi * (wmm / mm2in);
	private double h = dpi * (hmm / mm2in);
	private int cx = (int) (w / 2.0);
	private int cy = (int) (h / 2.0);
	double scaleMain = dpi * (scalemm / mm2in);
	double radArc = dpi * (radArcmm / mm2in);

	ArrayList<VertexGeometric> allPoints = new ArrayList<VertexGeometric>();
	ArrayList<Face> originalFaces = new ArrayList<Face>();
	double vanZ = 3;
	ObjLoader objLoader = new ObjLoader(vanZ);
	SvgDrawer svgDrawer = new SvgDrawer(opDir, src, w, h);
	VertexTransformer vertexTransformer;

	private int totRotAng = 360;
	private double incRotAng = 1;
	private double arcAngHalf = 10;

	private static CircleScratch3D scratch3D = new CircleScratch3D();

	public static void main(String[] args) throws IOException {
		scratch3D.draw();
	}

	private void draw() throws FileNotFoundException, IOException {
		svgDrawer.startSVG(false, true);

		originalFaces = objLoader.loadOBJ(dir + objDir + obj, allPoints);
		vertexTransformer = new VertexTransformer(originalFaces);

		drawAllPoints();

		svgDrawer.endSVG();
	}

	private void drawAllPoints() {
		String sd1 = svgDrawer.addLine(cx - 10, cy - 10, cx + 10, cy + 10);
		svgDrawer.writeToSVG(sd1);
		String sd2 = svgDrawer.addLine(cx - 10, cy + 10, cx + 10, cy - 10);
		svgDrawer.writeToSVG(sd2);
		String sd3 = svgDrawer.addCircle(cx, cy, w * 0.5);
		svgDrawer.writeToSVG(sd3);
		String sd4 = svgDrawer.addCircle(cx, cy, w * 0.1);
		svgDrawer.writeToSVG(sd4);

		for (VertexGeometric p : allPoints) {
			for (double a = 0; a <= totRotAng; a = a + incRotAng) {
				drawPoint(p, a);
			}
			// break;
		}

		svgDrawer.drawAllScratches();

	}

	private void drawPoint(VertexGeometric p1, double aDegs) {
		VertexGeometric p2 = p1;
		boolean adjustForPerspective = false;
		boolean occlude = false;
		p2 = vertexTransformer.transformVertex(p1, -aDegs, adjustForPerspective, objLoader);
		if (occlude) {
			ArrayList<Face> faces = vertexTransformer.getTransformedFaces(aDegs, adjustForPerspective, objLoader);
			if (!objLoader.isVertexVisible(faces, p2)) {
				return;
			}
		}

		// if (occlude) {
		// if (!objLoader.isVertexVisible(faces, p2)) {
		// return;
		// }
		// }

		double xx = p2.x;
		double yy = p2.y;

		// drawArc(xx, yy, p2.z, aDegs, true);
		drawPoint(xx, yy, p2.z, aDegs, true);
	}

	private void drawArc(double x, double y, float z, double aDegs, boolean b) {
		double sc = scaleMain * 0.1;
		double xd = x * sc;
		double yd = y * sc;

		double a = Math.toRadians(90 + aDegs);

		double radToC = scaleMain;
		double xc1 = radToC * Math.cos(a);
		double yc1 = radToC * Math.sin(a);

		double oAng = Math.toDegrees(Math.atan2(yd, xd));
		double ooAng = oAng + aDegs;
		double rrr = Math.sqrt(xd * xd + yd * yd);
		double xxd = rrr * Math.cos(ooAng);
		double yyd = rrr * Math.sin(ooAng);

		double xP = xxd + xc1;
		double yP = yyd + yc1;

		double rad2 = Math.sqrt(xP * xP + yP * yP);
		double ang2 = Math.toDegrees(Math.atan2(yP, xP));
		double ang2res = Math.toRadians(ang2 + aDegs);

		double xP2 = rad2 * Math.cos(ang2res);
		double yP2 = rad2 * Math.sin(ang2res);

		double zOff = 0.1;
		double rad = scaleMain * zOff + scaleMain * zOff * (Math.abs(z));

		double aa = Math.toRadians(270 + aDegs);
		double xc = rad * Math.cos(aa);
		double yc = rad * Math.sin(aa);

		double xOff = cx + (xP2 + xc);
		double yOff = cy - (yP2 + yc);

		double a1 = 270 + aDegs - arcAngHalf;
		double a2 = 270 + aDegs + arcAngHalf;

		String sb = svgDrawer.addArc(xOff, yOff, rad, a1, a2);
		svgDrawer.writeToSVG(sb);
	}

	private void drawArcOLD(double x, double y, float z, double aDegs, boolean b) {
		double sc = scaleMain * 0.25;
		double radC = scaleMain * 0.5;
		double xd = x * sc;
		double yd = y * sc;
		double radd = 50 + 10 * z;
		double a = Math.toRadians(90 + aDegs);
		double xc = radd * Math.cos(a);
		double yc = radd * Math.sin(a);

		double xOff = cx + radC * Math.cos(a) + xd + xc;
		double yOff = cy - radC * Math.sin(a) - yd - yc;

		boolean inwards = false;
		double a1 = inwards ? 90 + aDegs + arcAngHalf : 270 + aDegs - arcAngHalf;
		double a2 = inwards ? 90 + aDegs - arcAngHalf : 270 + aDegs + arcAngHalf;

		String sb = svgDrawer.addArc(xOff, yOff, radd, a1, a2);
		svgDrawer.writeToSVG(sb);
	}

	private void drawPoint1(double x, double y, float z, double aDegs, boolean b) {
		double sc = scaleMain * 0.1;
		double xd = x * sc;
		double yd = y * sc;

		double a = Math.toRadians(90 + aDegs);

		double radToC = scaleMain * 0.75;
		double xP = xd + radToC * Math.cos(a);
		double yP = yd + radToC * Math.sin(a);

		double rad2 = Math.sqrt(xP * xP + yP * yP);
		double ang2 = Math.toDegrees(Math.atan2(yP, xP));
		double ang2res = Math.toRadians(ang2 + aDegs);

		double xP2 = rad2 * Math.cos(ang2res);
		double yP2 = rad2 * Math.sin(ang2res);

		if (aDegs == 0) {
			String sb = svgDrawer.moveTo(cx + xP2, cy - yP2);
			svgDrawer.writeToSVG(sb);
		} else {
			String sb = svgDrawer.lineTo(cx + xP2, cy - yP2);
			svgDrawer.writeToSVG(sb);
		}
	}

	private void drawPoint(double x, double y, float z, double aDegs, boolean b) {
		double sc = scaleMain * 0.2;

		double xd = x * sc;
		double yd = y * sc;

		double radToC = scaleMain * 1.1;
		double x2 = xd;
		double y2 = radToC + yd;
		double rad2 = Math.sqrt(x2 * x2 + y2 * y2);
		double ang2 = Math.toDegrees(Math.atan2(y2, x2));

		double a = Math.toRadians(aDegs + ang2);
		double xOff = rad2 * Math.cos(a);
		double yOff = rad2 * Math.sin(a);

		if (aDegs == 0) {
			String sb = svgDrawer.moveTo(cx + xOff, cy - yOff);
			svgDrawer.writeToSVG(sb);
		} else {
			String sb = svgDrawer.lineTo(cx + xOff, cy - yOff);
			svgDrawer.writeToSVG(sb);
		}
	}

}
