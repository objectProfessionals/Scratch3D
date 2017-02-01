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
	// private String obj = "cubeLow";
	private String obj = "cubeLowWithEdges";
	// private String obj = "sphereMed";
	// private String obj = "DeathStarLow";
	// private String obj = "test-planes";
	// private String obj = "test-z";
	// private String obj = "test-pyramidSq";

	private String src = "CIRscratch3D-" + obj;
	double dpi = 300;
	double mm2in = 25.4;
	double scalemm = 25;
	double radArcmm = 35.0;
	private double wmm = scalemm * 4;
	private double hmm = scalemm * 4;
	private double w = dpi * (wmm / mm2in);
	private double h = dpi * (hmm / mm2in);
	private int cx = (int) (w / 2.0);
	private int cy = (int) (h / 2.0);
	double scaleMain = dpi * (scalemm / mm2in);
	double radArc = dpi * (radArcmm / mm2in);

	ArrayList<VertexGeometric> allPoints = new ArrayList<VertexGeometric>();
	ArrayList<Face> originalFaces = new ArrayList<Face>();
	ObjLoader objLoader = new ObjLoader();
	SvgDrawer svgDescriber = new SvgDrawer(opDir, src, w, h);
	VertexTransformer vertexTransformer;

	private int totRotAng = 360;
	private double incRotAng = 10;

	private static CircleScratch3D scratch3D = new CircleScratch3D();

	public static void main(String[] args) throws IOException {
		scratch3D.draw();
	}

	private void draw() throws FileNotFoundException, IOException {
		svgDescriber.startSVG();

		originalFaces = objLoader.loadOBJ(dir + objDir + obj, allPoints);
		vertexTransformer = new VertexTransformer(originalFaces);

		drawAllPoints();

		svgDescriber.endSVG();
	}

	private void drawAllPoints() {
		String sd1 = svgDescriber.drawLine(cx - 10, cy - 10, cx + 10, cy + 10);
		svgDescriber.addToSVG(sd1);
		String sd2 = svgDescriber.drawLine(cx - 10, cy + 10, cx + 10, cy - 10);
		svgDescriber.addToSVG(sd2);

		for (VertexGeometric p : allPoints) {
			for (double a = 0; a < totRotAng; a = a + incRotAng) {
				drawPoint(p, a);
			}
			// break;
		}

		svgDescriber.drawAllScratches();

	}

	private void drawPoint(VertexGeometric p1, double aDegs) {
		VertexGeometric p2 = p1;
		boolean adjustForPerspective = false;
		boolean occlude = true;
		p2 = vertexTransformer.transformVertex(p1, aDegs, adjustForPerspective, objLoader);
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

		drawArc(xx, yy, p2.z, aDegs, true);
	}

	private void drawArc(double x, double y, float z, double aDegs, boolean b) {
		double sc = scaleMain * 0.25;
		double a = Math.toRadians(aDegs);
		double xd = x * sc;
		double yd = y * sc;
		double angD = Math.atan2(yd, xd);
		double resAng = angD + a;

		double rad = Math.sqrt(xd * xd + yd * yd);

		double xx = rad * Math.cos(resAng);
		double yy = rad * Math.sin(resAng);

		double ccx = xx + cx;
		double ccy = yy + cy;

		double r = 0.5 * cx + (0.1 * radArc * z);

		// String sb = svgDescriber.drawLine(x1, y1, x2, y2);
		double arcAngHalf = incRotAng * 0.1;
		boolean inwards = false;
		double a1 = inwards ? 90 + aDegs + arcAngHalf : 270 + aDegs - arcAngHalf;
		double a2 = inwards ? 90 + aDegs - arcAngHalf : 270 + aDegs + arcAngHalf;

		String sb = svgDescriber.drawArc(ccx, ccy, r, a1, a2);
		svgDescriber.addToSVG(sb);

	}

}
