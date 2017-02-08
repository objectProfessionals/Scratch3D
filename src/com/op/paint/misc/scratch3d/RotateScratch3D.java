package com.op.paint.misc.scratch3d;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.VertexGeometric;

public class RotateScratch3D {

	private String dir = "host/images/out/misc/scratch3d/";
	private String opDir = "../output/";
	private String objDir = "objFiles/";
	// private String obj = "tieMini";
	// private String obj = "cubeLow";
	// private String obj = "coneHi";
	// private String obj = "cubeLowWithEdges";
	private String obj = "VS";
	// private String obj = "heart";
	// private String obj = "DeathStarLow";
	// private String obj = "test-planes";
	// private String obj = "test-z";
	// private String obj = "test-pyramidSq";

	private String src = "ROTscratch3D-" + obj;
	private boolean saveSVG = true;
	// double dpi = 1000;
	double dpi = 300;
	double mm2in = 25.4;
	double scalemm = 25;
	double radArcmm = 10.0;
	private double wmm = scalemm * 5;
	private double hmm = scalemm * 5;
	private double w = dpi * (wmm / mm2in);
	private double h = dpi * (hmm / mm2in);
	double scaleMain = dpi * (scalemm / mm2in);
	private int cx = (int) (w / 2.0);
	private int cy = (int) (h / 2.0);
	double radArc = dpi * (radArcmm / mm2in);

	double radArcMinF = 0.1;
	double glintAngle = 60;
	double numOfGlints = 10; // 90 / glintAngle;
	double totRotAng = 30; // 45;
	double incRotAng = totRotAng / numOfGlints;
	boolean preview = false;
	double previewAngle = 0;

	int numArcs = 0;
	ArrayList<VertexGeometric> allPoints = new ArrayList<VertexGeometric>();
	ArrayList<Face> originalFaces = new ArrayList<Face>();
	double vanZ = 5;
	ObjLoader objLoader = new ObjLoader();
	SvgDrawer svgDrawer = new SvgDrawer(opDir, src, w, h);
	VertexTransformer vertexTransformer;

	private static RotateScratch3D scratch3D = new RotateScratch3D();

	public static void main(String[] args) throws IOException {
		scratch3D.draw();
	}

	private void draw() throws FileNotFoundException, IOException {
		svgDrawer.startSVG(true, false);

		originalFaces = objLoader.loadOBJ(dir + objDir + obj, allPoints);
		vertexTransformer = new VertexTransformer(originalFaces, vanZ);
		drawAllPoints();

		save();
	}

	private void drawAllPoints() {
		String sd1 = svgDrawer.addLine(cx - 10, cy - 10, cx + 10, cy + 10);
		svgDrawer.writeToSVG(sd1);
		String sd2 = svgDrawer.addLine(cx - 10, cy + 10, cx + 10, cy - 10);
		svgDrawer.writeToSVG(sd2);
		if (preview) {
			int i = 0;
			for (VertexGeometric p : allPoints) {
				drawPoint(allPoints.get(i), previewAngle);
				i++;
			}
		} else {
			int i = 0;
			for (VertexGeometric p : allPoints) {
				for (double a = 0; a >= -totRotAng; a = a - incRotAng) {
					drawPoint(allPoints.get(i), a);
				}
				i++;
			}

			i = 0;
			for (VertexGeometric p : allPoints) {
				for (double a = 0; a <= totRotAng; a = a + incRotAng) {
					drawPoint(allPoints.get(i), a);
				}
				i++;
			}
		}
	}

	private void drawPoint(VertexGeometric p1, double aDegs) {
		boolean adjustForPerspective = true;
		boolean occlude = false;
		VertexGeometric p2 = p1;
		ArrayList<Face> faces = vertexTransformer.getTransformedFaces(aDegs, adjustForPerspective, objLoader);
		p2 = vertexTransformer.transformVertex(p1, aDegs, adjustForPerspective, objLoader);

		if (occlude) {
			if (!objLoader.isVertexVisible(faces, p2)) {
				return;
			}
		}

		drawArc(p2, aDegs);
	}

	private void drawArc(VertexGeometric p2, double aDegs) {
		double x = cx + scaleMain * p2.x;
		double y = cy - scaleMain * p2.y;
		double z = p2.z;

		double gDegs = 90 * aDegs / totRotAng;
		double angOff = z > 0 ? 270 : 90;
		double angD = z > 0 ? -gDegs : gDegs;

		double angStart = angOff - angD - glintAngle / 2.0;
		double angPoint = (angStart + glintAngle / 2.0) % 360;
		double rad = radArc * 0.5 + radArc * z * 0.25;

		double angPointRad = Math.toRadians(angPoint);
		double xc = x + rad * Math.cos(angPointRad);
		double yc = y - rad * Math.sin(angPointRad);

		svgDrawer.drawAndAddArc(xc, yc, rad, angStart, angStart + glintAngle);
	}

	private void save() throws IOException {
		if (saveSVG) {
			svgDrawer.endSVG();
		}
	}
}
