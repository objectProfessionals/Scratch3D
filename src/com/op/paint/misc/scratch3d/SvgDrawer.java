package com.op.paint.misc.scratch3d;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.owens.oobjloader.builder.VertexGeometric;

public class SvgDrawer {

	int numSvgs = 0;
	int svgFileLimit = 2000;
	boolean splitSVG = false;
	PrintWriter writer;
	private int svgFileCount = 1;
	private String opDir = "";
	private String src = "";
	private double w = 0;
	private double h = 0;
	public ArrayList<ScratchArc> allScratches = new ArrayList<ScratchArc>();
	boolean square;
	boolean circle;
	int sqOff = 50;

	public SvgDrawer(String opDir, String src, double w, double h) {
		this.opDir = opDir;
		this.src = src;
		this.w = w;
		this.h = h;
	}

	void drawAllScratches() {
		for (ScratchArc arc : allScratches) {
			if (true) {
				drawArcNumOfSVGs(arc.xc, arc.yc, arc.r, arc.angStart, arc.angStart + arc.angArcDraw);
			}
		}
	}

	void saveArc(double xtl, double ytl, double xc, double yc, double r, double d, int angStart, int angArcDraw) {
		ScratchArc arc = new ScratchArc(xtl, ytl, xc, yc, r, d, angStart, angArcDraw);
		if (!allScratches.contains(arc)) {
			allScratches.add(arc);
		}
	}

	void drawAndAddArc(double xc, double yc, double rad, double angSt, double angEn) {
		StringBuffer sb = new StringBuffer();
		sb.append(addArc(xc, yc, rad, angSt, angEn));
		writeToSVG(sb);
	}

	void drawArcNumOfSVGs(double xc, double yc, double rad, double angSt, double angEn) {
		// <path d="M10 10 C 20 20, 40 20, 50 10" stroke="black"
		// fill="transparent"/>

		StringBuffer sb = new StringBuffer();
		sb.append(addArc(xc, yc, rad, angSt, angEn));
		// double ang = ((angSt + angEn) / 2) % 360;
		// String col = ang >= 180 ? "red" : "blue";
		writeToSVG(sb);
		split();
		System.out.println("numSvgs = " + numSvgs);
	}

	private void split() {
		if (splitSVG) {
			if (numSvgs % svgFileLimit == 0) {
				if (numSvgs != 0) {
					endSVG();
					svgFileCount++;
				}
				startSVG(square, circle);
			}
		}
	}

	void writeToSVG(StringBuffer sb) {
		numSvgs++;
		writer.println(sb.toString());
	}

	void writeToSVG(String s) {
		numSvgs++;
		writer.println(s);
		split();
	}

	void startSVG(boolean square, boolean circle) {
		this.square = square;
		this.circle = circle;
		if (splitSVG) {
			// return;
		}
		try {
			writer = new PrintWriter(opDir + src + "_" + svgFileCount + ".svg", "UTF-8");
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
		int dd = (int) (w - sqOff);
		// writer.println("<rect x=\"" + rad + "\" y=\"" + rad + "\" width=\"" +
		// (w - rad * 2) + "\" height=\""
		// + (h - rad * 2) + "\" rx=\"" + rad + "\" ry=\"" + rad + "\"
		// stroke=\"blue\" fill=\"none\"/>");
		writer.println("<path d=\"");
		if (square) {
			// writer.println("M" + sqOff + " " + sqOff + " L" + dd + " " +
			// sqOff + " L" + dd + " " + dd + " L" + sqOff
			// + " " + dd + " Z");

			double sqOff2 = sqOff * 2;
			writer.println("M" + sqOff + " " + sqOff2 + " L" + sqOff + " " + sqOff + " L" + sqOff2 + " " + sqOff);
			writer.println("M" + (dd - sqOff) + " " + sqOff + " L" + dd + " " + sqOff + " L" + dd + " " + sqOff2);

			writer.println("M" + sqOff + " " + (dd - sqOff) + " L" + sqOff + " " + (dd) + " L" + sqOff2 + " " + dd);
			writer.println("M" + (dd - sqOff) + " " + dd + " L" + dd + " " + dd + " L" + (dd) + " " + (dd - sqOff));
		} else if (circle) {
			writer.println(addCircle((int) (w / 2), (int) (h / 2), (int) (w * 0.5)));
			writer.println(addCircle((int) (w / 2), (int) (h / 2), (int) (w * 0.1)));
		}

	}

	void endSVG() {
		String col = "blue";
		writer.println("\" stroke=\"" + col + "\" fill=\"none\" />");
		// writer.println("<!-- radmm = " + scalemm + " -->");
		// writer.println("<!-- adjustFr = " + perspectiveAdjustFr + " -->");
		// writer.println("<!-- adjustBa = " + perspectiveAdjustBa + " -->");

		writer.println("</svg>");
		writer.close();
		System.out.println("saved svg: " + opDir + src + "_" + svgFileCount + ".svg numSvgs=" + numSvgs);
	}

	VertexGeometric polarToCartesian(double centerX, double centerY, double radius, double angleInDegrees) {
		double angleInRadians = Math.toRadians(angleInDegrees);
		float x = (float) (centerX + (radius * Math.cos(angleInRadians)));
		float y = (float) (centerY + (radius * Math.sin(angleInRadians))); // -

		return new VertexGeometric(x, y, 0);
	}

	String addArc(double cx, double cy, double radius, double startAngle, double endAngle) {

		VertexGeometric start = polarToCartesian(cx, cy, radius, startAngle);
		VertexGeometric end = polarToCartesian(cx, cy, radius, endAngle);

		String largeArcFlag = Math.abs(endAngle - startAngle) <= 180 ? "0" : "1";

		String d = "M" + formatD(start.x) + " " + formatD(start.y) + " A " + formatD(radius) + " " + formatD(radius)
				+ " 0" + " " + largeArcFlag + " " + "0" + " " + formatD(end.x) + " " + formatD(end.y);

		if ((int) start.x == 38 && (int) start.y == 956) {
			boolean a = false;
		}

		return d;
	}

	boolean isClipped(double cx, double cy, double radius, double startAngle) {

		VertexGeometric start = polarToCartesian(cx, cy, radius, startAngle);

		double x1 = start.x;
		double y1 = start.y;
		double d1 = (sqOff);
		double d2 = (w - sqOff);

		if (x1 < d1 || x1 > d2 || y1 < d1 || y1 > d2) {
			// System.out.println("x,y=" + start.x + "," + start.y + " cx=" + cx
			// + " cy=" + cy + " r=" + radius + " s="
			// + (startAngle % 360));
			return true;
		}
		return false;
	}

	String addCircle(double cx, double cy, double radius) {

		String largeArc = " 1 ";
		VertexGeometric start = polarToCartesian(cx, cy, radius, 359.99);
		VertexGeometric end = polarToCartesian(cx, cy, radius, 0);
		String d = "M" + formatD(start.x) + " " + formatD(start.y) + " A " + formatD(radius) + " " + formatD(radius)
				+ " 0" + largeArc + "0 " + formatD(end.x) + " " + formatD(end.y);
		return d;
	}

	String addLine(double x1, double y1, double x2, double y2) {
		String d = "M" + formatD(x1) + " " + formatD(y1) + " L " + formatD(x2) + " " + formatD(y2) + " ";
		return d;
	}

	String moveTo(double x1, double y1) {
		String d = "M" + formatD(x1) + " " + formatD(y1);
		return d;
	}

	String lineTo(double x2, double y2) {
		String d = " L" + formatD(x2) + " " + formatD(y2) + " ";
		return d;
	}

	private int formatD(double d) {
		return (int) d;
		// return new BigDecimal(d).setScale(0,
		// BigDecimal.ROUND_HALF_UP).doubleValue();
	}
}
