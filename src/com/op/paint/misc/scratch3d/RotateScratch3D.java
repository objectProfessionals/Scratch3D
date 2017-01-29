package com.op.paint.misc.scratch3d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import com.op.paint.services.RendererUtils;
import com.owens.oobjloader.builder.Build;
import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.FaceVertex;
import com.owens.oobjloader.builder.VertexGeometric;

public class RotateScratch3D {

	private String dir = "host/images/out/misc/scratch3d/";
	private String opDir = "../output/";
	private String objDir = "objFiles/";
	// private String obj = "tieFullReduced";
	// private String obj = "cubeLow";
	// private String obj = "cubeLowWithEdges";
	// private String obj = "sphereMed";
	private String obj = "DeathStarLow";
	// private String obj = "test-planes";
	// private String obj = "test-z";
	// private String obj = "test-pyramidSq";
	private String src = "ROTscratch3D-" + obj;
	private static boolean test = false;
	private boolean saveSVG = true;
	private int svgFileCount = 1;
	// double dpi = 1000;
	double dpi = 300;
	double mm2in = 25.4;

	double scalemm = 30;
	double radArcmm = 20.0;

	boolean adjustForPerspective = true;
	double perspectiveAdjustFr = 0.1;
	double perspectiveAdjustBa = 0.1;
	boolean occlude = true;

	private double wmm = scalemm * 3;
	private double hmm = scalemm * 3;
	private double w = dpi * (wmm / mm2in);
	private double h = dpi * (hmm / mm2in);

	double scaleMain = dpi * (scalemm / mm2in);
	double radArc = dpi * (radArcmm / mm2in);
	double radArcMinF = 0.1;
	double radArcPosZMaxF = 0.5;
	double radArcNegZMaxF = 1;
	double glintAngle = 9;
	double numOfGlints = 90 / glintAngle; // 10
	double totRotAng = 30; // 45;
	double incRotAng = totRotAng / numOfGlints;
	double angArcDrawingF = 1;
	boolean lines = false;
	boolean vGrooved = false;
	boolean preview = false;
	double previewAngle = 0;
	double vanishZ = 5;

	private int cx = (int) (w / 2.0);
	private int cy = (int) (h / 2.0);

	float strokemm = 0.25f;
	float stroke = (float) (dpi * ((strokemm) / mm2in)); // (dpi / 120.0);
	float scratchGreyF = 1f;

	int numArcs = 0;
	int numSvgs = 0;
	int svgFileLimit = 5000;
	boolean splitSVG = false;
	private BufferedImage obi;
	private Graphics2D opG;
	ArrayList<Color> cols = new ArrayList<Color>();
	ArrayList<VertexGeometric> allPoints = new ArrayList<VertexGeometric>();
	ArrayList<ScratchArc> allScratches = new ArrayList<ScratchArc>();
	ArrayList<Face> originalFaces = new ArrayList<Face>();
	PrintWriter writer;
	private static RotateScratch3D scratch3D = new RotateScratch3D();

	/**
	 * @param args
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
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

		loadOBJ();
		drawAllPoints();

		save();
	}

	private void loadOBJ() throws FileNotFoundException, IOException {
		opG.setFont(new Font("TimesRoman", Font.PLAIN, 50));
		Build builder = new Build();

		Parse obj0 = new Parse(builder, dir + objDir + obj + ".obj");
		ArrayList<VertexGeometric> v0 = obj0.builder.getVertices();
		for (VertexGeometric v : v0) {
			// Point3f p = new Point3f(v.x, -v.z, v.y);
			VertexGeometric vg = new VertexGeometric(v.x, -v.z, v.y);
			allPoints.add(vg);

		}
		originalFaces = obj0.builder.getFaces();
	}

	private void drawAllPoints() {
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
	}

	private void drawAllScratches() {
		for (float f = 1; f >= 0; f = f - scratchGreyF) {
			for (ScratchArc arc : allScratches) {
				drawArc(arc.xtl, arc.ytl, arc.d, arc.angStart, arc.angArcDraw, f);
				if (saveSVG) {
					drawArcSVG(arc.xc, arc.yc, arc.r, arc.angStart, arc.angStart + arc.angArcDraw);
				}
			}
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

	private double getScaleForAdjusts(VertexGeometric p) {
		double sc = (vanishZ + p.z) / vanishZ;
		return sc;
	}

	private void drawPoint(VertexGeometric p1, double aDegs) {
		VertexGeometric p2 = p1;
		ArrayList<Face> faces = getTransformedFaces(aDegs);
		p2 = transformVertex(p1, aDegs);

		if (occlude) {
			if (!isVertexVisible(faces, p2)) {
				return;
			}
		}

		double xx = cx + scaleMain * p2.x;
		double yy = cy - scaleMain * p2.y;

		drawArc(xx, yy, p2.z, aDegs, true);
	}

	private ArrayList<Face> getTransformedFaces(double aDegs) {
		ArrayList<Face> tr = new ArrayList<Face>();
		for (Face face : originalFaces) {
			Face newFace = new Face();
			for (FaceVertex fv : face.vertices) {
				FaceVertex fv2 = new FaceVertex();
				VertexGeometric vg = new VertexGeometric(fv.v.x, -fv.v.z, fv.v.y);
				fv2.v = transformVertex(vg, aDegs);
				newFace.vertices.add(fv2);
			}
			tr.add(newFace);
		}
		return tr;
	}

	private VertexGeometric transformVertex(VertexGeometric p1, double aDegs) {
		double aa = Math.toRadians(aDegs);
		double x = p1.x;
		double y = p1.y;
		double z = p1.z;

		double xzR = Math.sqrt(x * x + z * z);
		double xzAng = Math.atan2(-z, x);
		double xzAngDeg = Math.toDegrees(xzAng);
		double xzAngTot = xzAngDeg + aDegs;

		double x2 = xzR * Math.cos(aa + xzAng);
		double y2 = y;
		double z2 = -xzR * Math.sin(aa + xzAng);

		VertexGeometric vg = new VertexGeometric((float) x2, (float) y2, (float) z2);
		VertexGeometric vga = vg;
		if (adjustForPerspective) {
			vga = adjustPoint(vg);
		}

		return vga;
	}

	private boolean isVertexVisible(ArrayList<Face> faces, VertexGeometric p) {
		ArrayList<Face> all = getAllNonJoiningFaces(faces, p);
		ArrayList<Face> allFiltered = getAllFacesWithLargerZ(all, p);

		return !anyFacesHavePointInside(allFiltered, p);
	}

	private boolean anyFacesHavePointInside(ArrayList<Face> allFiltered, VertexGeometric p) {
		for (Face face : allFiltered) {
			if (isPointContained(face, p)) {
				return true;
			}
		}
		return false;
	}

	private boolean isPointContained(Face face, VertexGeometric p) {
		Path2D path = new Path2D.Double();
		int i = 0;
		for (FaceVertex fv : face.vertices) {
			if (i == 0) {
				path.moveTo(fv.v.x, fv.v.y);
			} else {
				path.lineTo(fv.v.x, fv.v.y);
			}
			i++;
		}
		path.closePath();

		return path.contains(p.x, p.y);
	}

	private ArrayList<Face> getAllFacesWithLargerZ(ArrayList<Face> all, VertexGeometric p) {
		ArrayList<Face> filtered = new ArrayList<Face>();
		for (Face face : all) {
			boolean toAdd = false;
			for (FaceVertex fv : face.vertices) {
				if (fv.v.z > p.z) {
					toAdd = true;
				}
			}
			if (toAdd) {
				filtered.add(face);
			}
		}
		return filtered;
	}

	private ArrayList<Face> getAllNonJoiningFaces(ArrayList<Face> faces, VertexGeometric p) {
		ArrayList<Face> non = new ArrayList<Face>();
		for (Face face : faces) {
			boolean toAdd = true;
			for (FaceVertex fv : face.vertices) {
				if (equals(fv.v, p)) {
					toAdd = false;
				}
			}
			if (toAdd) {
				non.add(face);
			}
		}
		return non;
	}

	private boolean equals(VertexGeometric v, VertexGeometric p) {
		return (equals(v.x, p.x)) && (equals(v.y, p.y)) && (equals(v.z, p.z));
	}

	private boolean equals(float x, float x2) {
		return (x - x2) < 0.001;
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
			saveArc(xtl, ytl, xtl + r, ytl + r, r, r * 2, (int) angStart, (int) angArcDraw);
		} else {
			saveArc((xc - rad), (yc - rad), xc, yc, r, r * 2, (int) angStart, (int) angArcDraw);
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

	private void saveArc(double xtl, double ytl, double xc, double yc, double r, double d, int angStart,
			int angArcDraw) {
		ScratchArc arc = new ScratchArc(xtl, ytl, xc, yc, r, d, angStart, angArcDraw);
		if (!allScratches.contains(arc)) {
			allScratches.add(arc);
		}
	}

	private void drawArcSVG(double xc, double yc, double rad, double angSt, double angEn) {
		// <path d="M10 10 C 20 20, 40 20, 50 10" stroke="black"
		// fill="transparent"/>

		StringBuffer sb = new StringBuffer();
		sb.append(describeArc(xc, yc, rad, angSt, angEn));
		// double ang = ((angSt + angEn) / 2) % 360;
		// String col = ang >= 180 ? "red" : "blue";
		if (splitSVG) {
			if (numSvgs % svgFileLimit == 0) {
				if (numSvgs != 0) {
					endSVG();
					svgFileCount++;
				}
				startSVG();
			}
		}
		addToSVG(sb);
		numSvgs++;
		System.out.println("numSvgs = " + numSvgs);
	}

	VertexGeometric polarToCartesian(double centerX, double centerY, double radius, double angleInDegrees) {
		double angleInRadians = Math.toRadians(angleInDegrees);
		float x = (float) (centerX + (radius * Math.cos(angleInRadians)));
		float y = (float) (centerY - (radius * Math.sin(angleInRadians)));

		return new VertexGeometric(x, y, 0);
	}

	String describeArc(double cx, double cy, double radius, double startAngle, double endAngle) {

		VertexGeometric start = polarToCartesian(cx, cy, radius, startAngle);
		VertexGeometric end = polarToCartesian(cx, cy, radius, endAngle);

		String largeArcFlag = endAngle - startAngle <= 180 ? "0" : "1";

		String d = "M" + formatD(start.x) + " " + formatD(start.y) + " A " + formatD(radius) + " " + formatD(radius)
				+ " 0" + " " + largeArcFlag + " " + "0" + " " + formatD(end.x) + " " + formatD(end.y);

		return d;
	}

	private int formatD(double d) {
		return (int) d;
		// return new BigDecimal(d).setScale(0,
		// BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	private ArrayList<VertexGeometric> adjustPoints(ArrayList<VertexGeometric> points) {
		ArrayList<VertexGeometric> points2 = new ArrayList<VertexGeometric>();
		for (VertexGeometric p : points) {
			VertexGeometric vg = adjustPoint(p);
			points2.add(vg);
		}
		return points2;
	}

	private VertexGeometric adjustPoint(VertexGeometric p) {
		double sc = getScaleForAdjusts(p);
		double x = p.x * sc;
		double y = p.y * sc;
		VertexGeometric vg = new VertexGeometric((float) x, (float) y, (float) p.z);
		return vg;
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

		if (!splitSVG) {
			startSVG();
		}
		System.out.println("...finished initialising");
	}

	private void addToSVG(StringBuffer sb) {
		writer.println(sb.toString());
	}

	private void startSVG() {
		try {
			writer = new PrintWriter(opDir + "3dScratch" + obj + "_" + svgFileCount + ".svg", "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.println("<svg width=\"" + ((int) w) + "\" height=\"" + ((int) (h))
				+ "\" xmlns=\"http://www.w3.org/2000/svg\">");
		writer.println("");
		int rad = 50;
		// writer.println("<rect x=\"" + rad + "\" y=\"" + rad + "\" width=\"" +
		// (w - rad * 2) + "\" height=\""
		// + (h - rad * 2) + "\" rx=\"" + rad + "\" ry=\"" + rad + "\"
		// stroke=\"blue\" fill=\"none\"/>");
		writer.println("<path d=\"");
		writer.println("M" + rad + " " + rad + " L" + (w - rad) + " " + rad + " L" + (w - rad) + " " + (h - rad) + " L"
				+ (rad) + " " + (h - rad) + " Z");
	}

	private void endSVG() {
		String col = "blue";
		writer.println("\" stroke=\"" + col + "\" fill=\"none\" />");
		writer.println("<!-- radmm = " + scalemm + " -->");
		writer.println("<!-- adjustFr = " + perspectiveAdjustFr + " -->");
		writer.println("<!-- adjustBa = " + perspectiveAdjustBa + " -->");

		writer.println("</svg>");
		writer.close();
		System.out.println("saved svg: " + opDir + "3dScratch" + obj + "_" + svgFileCount + ".svg");
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
		System.out.println("numArcs=" + numArcs + " numSVGs=" + numSvgs + " " + " Saved " + op1.getPath());

		if (saveSVG) {
			endSVG();
		}
	}
}
