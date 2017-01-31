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
	private String obj = "cubeLow";
	// private String obj = "cubeLowWithEdges";
	// private String obj = "sphereMed";
	// private String obj = "DeathStarLow";
	// private String obj = "test-planes";
	// private String obj = "test-z";
	// private String obj = "test-pyramidSq";

	private String src = "CIRscratch3D-" + obj;
	double dpi = 300;
	double mm2in = 25.4;
	double scalemm = 30;
	double radArcmm = 20.0;
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

		objLoader.loadOBJ(dir + objDir + obj, allPoints, originalFaces);
		vertexTransformer = new VertexTransformer(originalFaces);

		drawAllPoints();

		svgDescriber.endSVG();
	}

	private void drawAllPoints() {
		for (VertexGeometric p : allPoints) {
			for (double a = 0; a < totRotAng; a = a + incRotAng) {
				drawPoint(p, a);
			}
		}

		svgDescriber.drawAllScratches();

	}

	private void drawPoint(VertexGeometric p1, double aDegs) {
		VertexGeometric p2 = p1;
		boolean adjustForPerspective = false;
		ArrayList<Face> faces = vertexTransformer.getTransformedFaces(aDegs, adjustForPerspective, objLoader);
		p2 = vertexTransformer.transformVertex(p1, aDegs, adjustForPerspective, objLoader);

		// if (occlude) {
		// if (!objLoader.isVertexVisible(faces, p2)) {
		// return;
		// }
		// }

		double xx = scaleMain * p2.x;
		double yy = scaleMain * p2.y;

		drawArc(xx, yy, p2.z, aDegs, true);
	}

	private void drawArc(double x, double y, float z, double aDegs, boolean b) {
		double d = 10;
		double r = Math.sqrt(x * x + y * y);
		double angD = Math.atan2(y, x);
		double resAng = Math.toRadians(Math.toDegrees(angD) + aDegs);
		double x2 = r * Math.cos(resAng);
		double y2 = r * Math.sin(resAng);

		String sb = svgDescriber.drawLine(cx + x, cy + y, cx + x2, cy + y2);
		svgDescriber.addToSVG(sb);

	}

}
