package com.op.paint.misc.scratch3d;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.owens.oobjloader.builder.VertexGeometric;

public class SvgDrawer {

	int numSvgs = 0;
	int svgFileLimit = 5000;
	boolean splitSVG = false;
	PrintWriter writer;
	private int svgFileCount = 1;
	private String opDir = "";
	private String src = "";
	private double w = 0;
	private double h = 0;
	public ArrayList<ScratchArc> allScratches = new ArrayList<ScratchArc>();

	public SvgDrawer(String opDir, String src, double w, double h) {
		this.opDir = opDir;
		this.src = src;
		this.w = w;
		this.h = h;
	}

	void drawAllScratches() {
		for (ScratchArc arc : allScratches) {
			if (true) {
				drawArcSVG(arc.xc, arc.yc, arc.r, arc.angStart, arc.angStart + arc.angArcDraw);
			}
		}
	}

	void saveArc(double xtl, double ytl, double xc, double yc, double r, double d, int angStart, int angArcDraw) {
		ScratchArc arc = new ScratchArc(xtl, ytl, xc, yc, r, d, angStart, angArcDraw);
		if (!allScratches.contains(arc)) {
			allScratches.add(arc);
		}
	}

	void drawArcSVG(double xc, double yc, double rad, double angSt, double angEn) {
		// <path d="M10 10 C 20 20, 40 20, 50 10" stroke="black"
		// fill="transparent"/>

		StringBuffer sb = new StringBuffer();
		sb.append(drawArc(xc, yc, rad, angSt, angEn));
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

	void addToSVG(StringBuffer sb) {
		writer.println(sb.toString());
	}

	void addToSVG(String s) {
		writer.println(s);
	}

	void startSVG() {
		if (splitSVG) {
			return;
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
		int rad = 50;
		int dd = (int) (w - rad);
		// writer.println("<rect x=\"" + rad + "\" y=\"" + rad + "\" width=\"" +
		// (w - rad * 2) + "\" height=\""
		// + (h - rad * 2) + "\" rx=\"" + rad + "\" ry=\"" + rad + "\"
		// stroke=\"blue\" fill=\"none\"/>");
		writer.println("<path d=\"");
		writer.println(
				"M" + rad + " " + rad + " L" + dd + " " + rad + " L" + dd + " " + dd + " L" + rad + " " + dd + " Z");
	}

	void endSVG() {
		String col = "blue";
		writer.println("\" stroke=\"" + col + "\" fill=\"none\" />");
		// writer.println("<!-- radmm = " + scalemm + " -->");
		// writer.println("<!-- adjustFr = " + perspectiveAdjustFr + " -->");
		// writer.println("<!-- adjustBa = " + perspectiveAdjustBa + " -->");

		writer.println("</svg>");
		writer.close();
		System.out.println("saved svg: " + opDir + src + "_" + svgFileCount + ".svg");
	}

	VertexGeometric polarToCartesian(double centerX, double centerY, double radius, double angleInDegrees) {
		double angleInRadians = Math.toRadians(angleInDegrees);
		float x = (float) (centerX + (radius * Math.cos(angleInRadians)));
		float y = (float) (centerY - (radius * Math.sin(angleInRadians)));

		return new VertexGeometric(x, y, 0);
	}

	String drawArc(double cx, double cy, double radius, double startAngle, double endAngle) {

		VertexGeometric start = polarToCartesian(cx, cy, radius, startAngle);
		VertexGeometric end = polarToCartesian(cx, cy, radius, endAngle);

		String largeArcFlag = endAngle - startAngle <= 180 ? "0" : "1";

		String d = "M" + formatD(start.x) + " " + formatD(start.y) + " A " + formatD(radius) + " " + formatD(radius)
				+ " 0" + " " + largeArcFlag + " " + "0" + " " + formatD(end.x) + " " + formatD(end.y);

		return d;
	}

	String drawLine(double x1, double y1, double x2, double y2) {

		String d = "M" + formatD(x1) + " " + formatD(y1) + " L " + formatD(x2) + " " + formatD(y2) + " ";

		return d;
	}

	private int formatD(double d) {
		return (int) d;
		// return new BigDecimal(d).setScale(0,
		// BigDecimal.ROUND_HALF_UP).doubleValue();
	}
}
