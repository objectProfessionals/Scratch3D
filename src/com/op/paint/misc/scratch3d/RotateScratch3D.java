package com.op.paint.misc.scratch3d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.op.paint.services.RendererUtils;
import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.VertexGeometric;

public class RotateScratch3D {

	private String dir = "host/images/out/misc/scratch3d/";
	private String opDir = "../output/";
	private String objDir = "objFiles/";
	private String obj = "tieMini";
	// private String obj = "cubeLow";
	// private String obj = "cubeLowWithEdges";
	// private String obj = "sphereMed";
	// private String obj = "DeathStarLow";
	// private String obj = "test-planes";
	// private String obj = "test-z";
	// private String obj = "test-pyramidSq";

	private String src = "ROTscratch3D-" + obj;
	private static boolean test = false;
	private boolean saveSVG = true;
	// double dpi = 1000;
	double dpi = 300;
	double mm2in = 25.4;
	double scalemm = 30;
	double radArcmm = 20.0;
	private double wmm = scalemm * 4;
	private double hmm = scalemm * 4;
	private double w = dpi * (wmm / mm2in);
	private double h = dpi * (hmm / mm2in);
	double scaleMain = dpi * (scalemm / mm2in);
	private int cx = (int) (w / 2.0);
	private int cy = (int) (h / 2.0);
	double radArc = dpi * (radArcmm / mm2in);

	boolean adjustForPerspective = true;
	double perspectiveAdjustFr = 0.1;
	double perspectiveAdjustBa = 0.1;
	boolean occlude = true;

	double radArcMinF = 0.1;
	double radArcPosZMaxF = 0.5;
	double radArcNegZMaxF = 1;
	double glintAngle = 9;
	double numOfGlints = 90 / glintAngle; // 10
	double totRotAng = 60; // 45;
	double incRotAng = totRotAng / numOfGlints;
	double angArcDrawingF = 1;
	boolean lines = false;
	boolean vGrooved = false;
	boolean preview = false;
	double previewAngle = 0;

	float strokemm = 0.25f;
	float stroke = (float) (dpi * ((strokemm) / mm2in)); // (dpi / 120.0);
	float scratchGreyF = 1f;

	int numArcs = 0;
	private BufferedImage obi;
	private Graphics2D opG;
	ArrayList<Color> cols = new ArrayList<Color>();
	ArrayList<VertexGeometric> allPoints = new ArrayList<VertexGeometric>();
	ArrayList<Face> originalFaces = new ArrayList<Face>();
	ObjLoader objLoader = new ObjLoader();
	SvgDrawer svgDescriber = new SvgDrawer(opDir, src, w, h);
	VertexTransformer vertexTransformer;

	private static RotateScratch3D scratch3D = new RotateScratch3D();

	public static void main(String[] args) throws IOException {
		if (test) {
			scratch3D.testCircles();
		} else {
			scratch3D.draw();
		}
	}

	private void testCircles() throws IOException {
		src = "ROTscratch3D-CIRCLES";
		vGrooved = true;
		lines = false;
		glintAngle = 180;

		initPNG();

		for (double d = 2; d > -2; d = d - 0.1) {
			drawArc(cx, cy, d, 0, false);
		}

		strokemm = 1f;
		stroke = (float) (dpi * ((strokemm / 1.3) / mm2in));
		drawAllScratches();
		save();
	}

	private void draw() throws FileNotFoundException, IOException {
		initPNG();

		svgDescriber.startSVG();

		objLoader.loadOBJ(dir + objDir + obj, allPoints, originalFaces);
		vertexTransformer = new VertexTransformer(originalFaces);
		drawAllPoints();

		save();
	}

	private void drawAllPoints() {
		opG.setFont(new Font("TimesRoman", Font.PLAIN, 50));
		float c = 0;
		for (VertexGeometric p : allPoints) {
			float r = c;
			float g = (c + 0.33f) % 1.0f;
			float b = (c + 0.66f) % 1.0f;
			Color col = new Color(r, g, b);
			cols.add(col);
			c = c + ((float) 1 / allPoints.size());
		}

		if (preview) {
			totRotAng = 90;
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

		drawAllScratches();
		svgDescriber.drawAllScratches();
	}

	private void drawAllScratches() {
		for (float f = 1; f >= 0; f = f - scratchGreyF) {
			drawAllScratches(f);
		}
	}

	private void drawAllScratches(float f) {
		for (ScratchArc arc : svgDescriber.allScratches) {
			drawArc(arc.xtl, arc.ytl, arc.d, arc.angStart, arc.angArcDraw, f);
		}
	}

	private void drawArc(double xtl, double ytl, double d, int angStart, int angArcDraw, float f) {
		Color col = new Color(f, f, f);
		opG.setColor(col);
		opG.setStroke(new BasicStroke(stroke * f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));

		// float g = (1 - f);
		// float str = stroke * 0.5f;
		// int x1 = (int) (xtl + str * f);
		// int y1 = (int) (ytl + str * f);
		// int dd = (int) (d - 2 * (stroke * f));

		int x1 = (int) (xtl);
		int y1 = (int) (ytl);
		int dd = (int) (d);
		opG.drawArc(x1, y1, dd, dd, angStart, angArcDraw);
		numArcs++;
	}

	private void drawPoint(VertexGeometric p1, double aDegs) {
		VertexGeometric p2 = p1;
		ArrayList<Face> faces = vertexTransformer.getTransformedFaces(aDegs, adjustForPerspective, objLoader);
		p2 = vertexTransformer.transformVertex(p1, aDegs, adjustForPerspective, objLoader);

		if (occlude) {
			if (!objLoader.isVertexVisible(faces, p2)) {
				return;
			}
		}

		double xx = cx + scaleMain * p2.x;
		double yy = cy - scaleMain * p2.y;

		drawArc(xx, yy, p2.z, aDegs, true);
	}

	private void drawArc(double x, double y, double z, double aDegs, boolean fromPointOrCenter) {
		double gDegs = 90 * aDegs / totRotAng;
		double angOff = z > 0 ? 270 : 90;
		double angD = z < 0 ? gDegs : -gDegs;

		double angStart = angOff - angD - angArcDrawingF * glintAngle / 2.0;
		double angPoint = (angStart + angArcDrawingF * glintAngle / 2.0) % 360;
		double rad = radArc;
		if (z > 0) {
			rad = (radArc * radArcMinF) + (Math.abs(z)) * radArc * radArcPosZMaxF;
		} else {
			rad = (radArc * radArcMinF) + (Math.abs(z)) * radArc * radArcNegZMaxF;
		}
		double angPointRad = Math.toRadians(angPoint);
		double xc = fromPointOrCenter ? x - rad * Math.cos(angPointRad) : x;
		double yc = fromPointOrCenter ? y + rad * Math.sin(angPointRad) : y;

		int xtl = (int) (xc - rad);
		int ytl = (int) (yc - rad);
		int r = (int) rad;
		int angArcDraw = (int) (angArcDrawingF * glintAngle);

		if (!vGrooved) {
			strokemm = 0.1f;
			stroke = (float) (dpi * ((strokemm) / mm2in)); // (dpi / 120.0);
			opG.setColor(angPoint % 360 > 180 ? Color.RED : Color.BLUE);
			opG.setStroke(new BasicStroke(stroke));
			opG.drawArc(xtl, ytl, r * 2, r * 2, (int) angStart, (int) angArcDraw);
			svgDescriber.saveArc(xtl, ytl, xtl + r, ytl + r, r, r * 2, (int) angStart, (int) angArcDraw);
		} else {
			svgDescriber.saveArc((xc - rad), (yc - rad), xc, yc, r, r * 2, (int) angStart, (int) angArcDraw);
		}

		if (lines) {
			strokemm = 0.1f;
			stroke = (float) (dpi * ((strokemm) / mm2in)); // (dpi / 120.0);
			opG.setColor(Color.GRAY);
			opG.setStroke(new BasicStroke(stroke));
			opG.drawLine((int) xc, (int) yc, (int) x, (int) y);
		}

		// // opG.drawString("" + angRes, x2, y2);
		// opG.fillOval(x - rr, y - rr, rr * 2, rr * 2);
	}

	private void initPNG() throws FileNotFoundException, UnsupportedEncodingException {
		System.out.println("initialising...");
		int ww = (int) (w);
		int hh = (int) (h);
		obi = new BufferedImage(ww, hh, BufferedImage.TYPE_INT_RGB);
		opG = (Graphics2D) obi.getGraphics();

		opG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		opG.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		opG.setColor(Color.WHITE);
		opG.fillRect(0, 0, ww, hh);

		System.out.println("...finished initialising");
	}

	private void save() throws IOException {
		String suff = "-" + strokemm + "-" + totRotAng + "-" + incRotAng + "-" + glintAngle;
		File op1 = new File(opDir + src + "OUT" + suff + ".png");
		try {
			RendererUtils.savePNGFile(obi, op1, dpi);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("numArcs=" + numArcs + " " + " Saved " + op1.getPath());

		if (saveSVG) {
			svgDescriber.endSVG();
		}
	}
}
