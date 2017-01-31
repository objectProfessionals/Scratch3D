package com.op.paint.misc.scratch3d;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.VertexGeometric;

public class ArcScratch3D {

	private String dir = "host/images/out/misc/scratch3d/";
	private String opDir = "../output/";
	private String objDir = "objFiles/";

	private String obj = "tieMini";
	// private String obj = "cubeLow";
	// private String obj = "cubeLowWithEdges";
	// private String obj = "sphereMed";
	// private String obj = "DeathStarFront";
	// private String obj = "test-planes";
	// private String obj = "test-z";
	// private String obj = "test-pyramidSq";

	private String src = "ARCscratch3D-" + obj;
	double dpi = 300;
	double mm2in = 25.4;
	double scalemm = 30;
	double scaleMain = dpi * (scalemm / mm2in);

	private double wmm = scalemm * 4;
	private double hmm = scalemm * 4;
	private double w = dpi * (wmm / mm2in);
	private double h = dpi * (hmm / mm2in);

	double ang = 90;

	private int cx = (int) (w / 2.0);
	private int cy = (int) (h / 2.0);

	ArrayList<VertexGeometric> allPoints = new ArrayList<VertexGeometric>();
	ArrayList<Face> originalFaces = new ArrayList<Face>();

	private static ArcScratch3D scratch3D = new ArcScratch3D();

	ObjLoader objLoader = new ObjLoader();
	SvgDrawer svgDescriber = new SvgDrawer(opDir, src, w, h);
	PngDrawer pngDrawer = new PngDrawer(opDir, obj, src, w, h);
	double vanishZ = 5;

	/**
	 * @param args
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	public static void main(String[] args) throws Exception {
		// scratch3D.paint();
		scratch3D.loadOBJ();

	}

	private void loadOBJ() throws Exception {
		init();
		pngDrawer.init();
		// allPoints = adjustPoints(allPoints);
		objLoader.loadOBJ(dir + objDir + obj, allPoints, originalFaces);

		drawAllPoints();
		save();

	}

	private void drawAllPoints() {
		int c = 0;
		for (VertexGeometric p : allPoints) {
			drawArc(p, c);
			c++;
		}
	}

	private double getScaleForAdjusts(VertexGeometric p) {
		double sc = (vanishZ + p.z) / vanishZ;
		return sc;
	}

	private void drawArc(VertexGeometric p1, int c) {
		VertexGeometric p = objLoader.adjustPoint(p1);
		double x = p.x;
		double y = p.y;
		double z = p.z * 0.5;
		double rad = scaleMain * 1 * (Math.abs(z));
		double xx = cx + x * scaleMain;
		double yy = cy + y * scaleMain;

		// fillSpotAndText(xx, yy1, rr / 2, c + "", false, true);

		// fillLabelAndSpot(xx, yy, rr, c + "", false, true);

		// opG.drawLine((int) xx, (int) yy, (int) xx, (int) yy1);

		double midA = p1.z > 0 ? 270 : 90;
		double angSt = midA - ang / 2;
		double angEn = angSt + ang;

		double aa = ang / 10;

		double xf = 0;
		double yf = 0;
		boolean arc = true;
		boolean lines = false;
		for (double a = angSt; a < angSt + ang; a = a + aa) {
			// col = cols.get(i);
			// opG.setColor(col);
			if (arc) {
				int xtl = (int) (xx - rad);
				int ytl = (int) (yy - rad);
				int r = (int) rad;
				int d = (int) rad * 2;
				int aStart = (int) (a);
				int aEn = (int) (aa);
				pngDrawer.drawArc(xtl, ytl, d, d, aStart, aEn);
				svgDescriber.saveArc(xtl, ytl, xtl + r, ytl + r, r, d, aStart, aEn);
			}

			double aRads = Math.toRadians(a + aa);
			xf = xx + rad * Math.cos(aRads);
			yf = yy - rad * Math.sin(aRads);
			// fillLabelAndSpot(xf, yf, rrr, null);
		}
		// fillLabelAndSpot(xf, yf, rr / 2, c + "", true, true);

		// opG.drawArc((int) (xx - rad), (int) (yy - rad), (int) rad * 2,
		// (int) rad * 2, (int) (angSt), (int) (ang));

		if (lines) {
			double angRads = Math.toRadians(angSt);
			xf = xx + rad * Math.cos(angRads);
			yf = yy - rad * Math.sin(angRads);

			pngDrawer.drawLine((int) (xx), (int) (yy), (int) (xf), (int) (yf));
			// fillLabelAndSpot(xf, yf, rrr, c + "");

			angRads = Math.toRadians(angEn);
			xf = xx + rad * Math.cos(angRads);
			yf = yy - rad * Math.sin(angRads);
			pngDrawer.drawLine((int) (xx), (int) (yy), (int) (xf), (int) (yf));

		}

		boolean svg = true;
		if (svg) {
			// drawArcSVG(xx, yy, rad, angSt, angEn);
			svgDescriber.drawArcSVG(xx, h - yy, rad, angSt, angEn);
		}
	}

	private void fillLabelAndSpot(double xx, double yy, int rr, String string, boolean label, boolean spot) {
		if (spot) {
			// pngDrawer.fillOval((int) (xx - rr), (int) (yy - rr), (int) rr *
			// 2, (int) rr * 2);
		}
		if (label && string != null) {
			drawString(string, (int) xx, (int) yy);
		}
	}

	private void drawString(String string, int xx, int yy) {
		pngDrawer.drawString(string, xx + 20, yy - 20);

	}

	VertexGeometric polarToCartesian(double centerX, double centerY, double radius, double angleInDegrees) {
		double angleInRadians = Math.toRadians(angleInDegrees);
		float x = (float) (centerX + (radius * Math.cos(angleInRadians)));
		float y = (float) (centerY + (radius * Math.sin(angleInRadians)));

		return new VertexGeometric(x, y, 0);
	}

	String describeArc(double x, double y, double radius, double startAngle, double endAngle) {

		VertexGeometric start = polarToCartesian(x, y, radius, endAngle);
		VertexGeometric end = polarToCartesian(x, y, radius, startAngle);

		String largeArcFlag = endAngle - startAngle <= 180 ? "0" : "1";

		String d = "M " + start.x + " " + start.y + " A " + radius + " " + radius + " 0" + " " + largeArcFlag + " "
				+ "0" + " " + end.x + " " + end.y + " ";

		return d;
	}

	private ArrayList<VertexGeometric> adjustPoints(ArrayList<VertexGeometric> points) {
		ArrayList<VertexGeometric> points2 = new ArrayList<VertexGeometric>();
		for (VertexGeometric p : points) {
			double sc = getScaleForAdjusts(p);
			double x = p.x * sc;
			double y = p.y * sc;
			VertexGeometric vg = new VertexGeometric((float) x, (float) y, p.z);
			points2.add(vg);
		}
		return points2;
	}

	private void init() throws FileNotFoundException, UnsupportedEncodingException {
		System.out.println("initialising...");
		pngDrawer.initPng();

		svgDescriber.startSVG();

		System.out.println("...finished initialising");
	}

	private void save() throws Exception {
		pngDrawer.save();

		// if (saveSVG) {
		if (true) {
			svgDescriber.endSVG();
		}

	}
}
