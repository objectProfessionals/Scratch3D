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

	// private String obj = "tieMini";
	// private String obj = "cubeLow";
	private String obj = "cubeLowWithEdges";
	// private String obj = "coneHi";
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

	private double wmm = scalemm * 3;
	private double hmm = scalemm * 3;
	private double w = dpi * (wmm / mm2in);
	private double h = dpi * (hmm / mm2in);

	double ang = 60;

	private int cx = (int) (w / 2.0);
	private int cy = (int) (h / 2.0);

	ArrayList<VertexGeometric> allPoints = new ArrayList<VertexGeometric>();
	ArrayList<Face> originalFaces = new ArrayList<Face>();

	private static ArcScratch3D scratch3D = new ArcScratch3D();

	double vanZ = 10;
	ObjLoader objLoader = new ObjLoader(vanZ);
	SvgDrawer svgDescriber = new SvgDrawer(opDir, src, w, h);
	boolean savePNG = false;

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
		// allPoints = adjustPoints(allPoints);
		originalFaces = objLoader.loadOBJ(dir + objDir + obj, allPoints);

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

	private void drawArc(VertexGeometric p1, int c) {
		VertexGeometric p = objLoader.adjustPoint(p1);
		double x = p.x;
		double y = p.y;
		double z = p.z * 0.5;
		double rad = scaleMain * 1 * (Math.abs(z));
		double xx = cx + x * scaleMain;
		double yy = cy + y * scaleMain;

		double midA = p1.z > 0 ? 270 : 90;
		double angSt = midA - ang / 2;
		double angEn = angSt + ang;

		double aa = ang / 10;

		double xf = 0;
		double yf = 0;
		for (double a = angSt; a < angSt + ang; a = a + aa) {
			int xtl = (int) (xx - rad);
			int ytl = (int) (yy - rad);
			int r = (int) rad;
			int d = (int) rad * 2;
			int aStart = (int) (a);
			int aEn = (int) (aa);
			svgDescriber.saveArc(xtl, ytl, xtl + r, ytl + r, r, d, aStart, aEn);

			double aRads = Math.toRadians(a + aa);
			xf = xx + rad * Math.cos(aRads);
			yf = yy - rad * Math.sin(aRads);
		}

		boolean svg = true;
		if (svg) {
			svgDescriber.drawAndAddArc(xx, h - yy, rad, angSt, angEn);
		}
	}

	private void init() throws FileNotFoundException, UnsupportedEncodingException {
		System.out.println("initialising...");

		svgDescriber.startSVG(true, false);

		System.out.println("...finished initialising");
	}

	private void save() throws Exception {
		svgDescriber.endSVG();
	}
}
