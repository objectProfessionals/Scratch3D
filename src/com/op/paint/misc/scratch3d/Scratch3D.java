package com.op.paint.misc.scratch3d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
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
import com.owens.oobjloader.builder.VertexGeometric;

public class Scratch3D {

	private String dir = "host/images/out/misc/scratch3d/";
	// private String obj = "tieFighterLow";
	private String obj = "sphereLow";
	// private String obj = "cubeLow";
	// private String obj = "test-planes";
	// private String obj = "test-z";
	// private String obj = "test-pyramidSq";
	private String src = "scratch3D-" + obj;
	double dpi = 600;
	double mm2in = 25.4;
	double scalemm = 30;
	double scaleMain = dpi * (scalemm / mm2in);

	private double wmm = scalemm * 4;
	private double hmm = scalemm * 4;
	private double w = dpi * (wmm / mm2in);
	private double h = dpi * (hmm / mm2in);

	double adjustFr = 1;
	double adjustBa = 1;
	double scArcfr = 0.2;
	double scArcBa = 0.1;
	double ang = 170;

	private int cx = (int) (w / 2.0);
	private int cy = (int) (h / 2.0);
	private double zoomFfr = 0.25;// 0.25
	private double zoomFba = 0.5;// 0.5

	float strokemm = 0.25f;
	float stroke = (float) (dpi * (strokemm / mm2in));

	private BufferedImage obi;
	private Graphics2D opG;
	ArrayList<Color> cols = new ArrayList<Color>();
	ArrayList<VertexGeometric> allPoints = new ArrayList<VertexGeometric>();

	PrintWriter writer;
	private static Scratch3D scratch3D = new Scratch3D();

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
		opG.setStroke(new BasicStroke(stroke));
		opG.setFont(new Font("TimesRoman", Font.PLAIN, 50));
		Build builder = new Build();

		Parse obj0 = new Parse(builder, dir + obj + ".obj");
		ArrayList<VertexGeometric> v0 = obj0.builder.getVertices();
		for (VertexGeometric v : v0) {
			VertexGeometric p = new VertexGeometric(v.x, v.y, v.z);
			allPoints.add(p);
		}

		allPoints = adjustPoints(allPoints);
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
		double sc = p.z > 0 ? p.z * adjustFr : p.z * adjustBa;
		sc = sc == 0 ? 1 : sc;
		sc = Math.abs(sc);
		return sc;

	}

	private double getScaleArcs(VertexGeometric p) {
		double sc = p.z > 0 ? -p.z * scArcfr : p.z * scArcBa;
		sc = sc == 0 ? 1 : 1 + sc;
		sc = Math.abs(sc);
		return sc;

	}

	private void drawArc(VertexGeometric p1, int c) {
		double x = p1.x;
		double y = p1.y;
		double z = p1.z;
		double radF = 1 + Math.abs(z);
		double rad = scaleMain * radF * 0.5 * (z > 0 ? zoomFfr : zoomFba);
		double xx = cx + x * scaleMain;
		double yy1 = cy - y * scaleMain;
		int rr = (int) stroke * 3;

		double yy = z > 0 ? yy1 - rad : yy1 + rad;
		Color col = getRandomColor();
		// col = Color.BLACK;
		opG.setColor(col);
		// fillSpotAndText(xx, yy1, rr / 2, c + "", false, true);

		fillLabelAndSpot(xx, yy, rr, c + "", false, true);

		// opG.drawLine((int) xx, (int) yy, (int) xx, (int) yy1);

		double midA = p1.z > 0 ? 270 : 90;
		double angSt = midA - ang / 2;
		double angEn = angSt + ang;

		double aa = ang / 10;

		int i = 0;
		double xf = 0;
		double yf = 0;
		boolean arc = true;
		boolean lines = false;
		for (double a = angSt; a < angSt + ang; a = a + aa) {
			// col = cols.get(i);
			// opG.setColor(col);
			if (arc) {
				opG.drawArc((int) (xx - rad), (int) (yy - rad), (int) rad * 2,
						(int) rad * 2, (int) (a), (int) (aa));
			}

			double aRads = Math.toRadians(a + aa);
			xf = xx + rad * Math.cos(aRads);
			yf = yy - rad * Math.sin(aRads);
			// fillLabelAndSpot(xf, yf, rrr, null);
			i++;
		}
		// fillLabelAndSpot(xf, yf, rr / 2, c + "", true, true);

		// opG.drawArc((int) (xx - rad), (int) (yy - rad), (int) rad * 2,
		// (int) rad * 2, (int) (angSt), (int) (ang));

		if (lines) {
			double angRads = Math.toRadians(angSt);
			xf = xx + rad * Math.cos(angRads);
			yf = yy - rad * Math.sin(angRads);

			opG.drawLine((int) (xx), (int) (yy), (int) (xf), (int) (yf));
			// fillLabelAndSpot(xf, yf, rrr, c + "");

			angRads = Math.toRadians(angEn);
			xf = xx + rad * Math.cos(angRads);
			yf = yy - rad * Math.sin(angRads);
			opG.drawLine((int) (xx), (int) (yy), (int) (xf), (int) (yf));

		}

		boolean svg = true;
		if (svg) {
			// drawArcSVG(xx, yy, rad, angSt, angEn);
			drawArcSVG(xx, h - yy, rad, angSt, angEn);
		}
	}

	private Color getRandomColor() {
		double brightest = 0.75;
		float r = (float) (Math.random() * brightest);
		float g = (float) (Math.random() * brightest);
		float b = (float) (Math.random() * brightest);
		return new Color(r, g, b);
	}

	private void fillLabelAndSpot(double xx, double yy, int rr, String string,
			boolean label, boolean spot) {
		if (spot) {
			opG.fillOval((int) (xx - rr), (int) (yy - rr), (int) rr * 2,
					(int) rr * 2);
		}
		if (label && string != null) {
			drawString(string, (int) xx, (int) yy);
		}
	}

	private void drawArcSVG(double xx, double yy, double rad, double angSt,
			double angEn) {
		// <path d="M10 10 C 20 20, 40 20, 50 10" stroke="black"
		// fill="transparent"/>
		double strokeWidth = 1; // 0.00125;
		StringBuffer sb = new StringBuffer();
		sb.append("<path d=\"");
		sb.append(describeArc(xx, yy, rad, angSt, angEn));
		sb.append("\" stroke=\"blue\" fill=\"none\" style=\"stroke-width:"
				+ strokeWidth + ";\"/>");
		writer.println(sb.toString());

	}

	private void drawString(String string, int xx, int yy) {
		opG.drawString(string, xx + 20, yy - 20);

	}

	VertexGeometric polarToCartesian(double centerX, double centerY,
			double radius, double angleInDegrees) {
		double angleInRadians = Math.toRadians(angleInDegrees);
		float x = (float) (centerX + (radius * Math.cos(angleInRadians)));
		float y = (float) (centerY + (radius * Math.sin(angleInRadians)));

		return new VertexGeometric(x, y, 0);
	}

	String describeArc(double x, double y, double radius, double startAngle,
			double endAngle) {

		VertexGeometric start = polarToCartesian(x, y, radius, endAngle);
		VertexGeometric end = polarToCartesian(x, y, radius, startAngle);

		String largeArcFlag = endAngle - startAngle <= 180 ? "0" : "1";

		String d = "M " + start.x + " " + start.y + " A " + radius + " "
				+ radius + " 0" + " " + largeArcFlag + " " + "0" + " " + end.x
				+ " " + end.y + " ";

		return d;
	}

	private ArrayList<VertexGeometric> adjustPoints(
			ArrayList<VertexGeometric> points) {
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

	private void init() throws FileNotFoundException,
			UnsupportedEncodingException {
		System.out.println("initialising...");
		int ww = (int) (w);
		int hh = (int) (h);
		obi = new BufferedImage(ww, hh, BufferedImage.TYPE_INT_RGB);
		opG = (Graphics2D) obi.getGraphics();
		opG.setColor(Color.WHITE);
		opG.fillRect(0, 0, ww, hh);

		writer = new PrintWriter(dir + "3dScratch" + obj + ".svg", "UTF-8");
		writer.println("<svg width=\"" + ((int) w) + "\" height=\""
				+ ((int) (h * 1.25))
				+ "\" xmlns=\"http://www.w3.org/2000/svg\">");
		writer.println("");

		System.out.println("...finished initialising");
	}

	private void save() throws Exception {
		File op1 = new File(dir + src + "OUT" + ".png");
		RendererUtils.savePNGFile(obi, op1, 600);
		System.out.println("Saved " + op1.getPath());

		writer.println("<!-- radmm = " + scalemm + " -->");
		writer.println("<!-- adjustFr = " + adjustFr + " -->");
		writer.println("<!-- adjustBa = " + adjustBa + " -->");
		writer.println("<!-- scArcfr = " + scArcfr + " -->");
		writer.println("<!-- scArcBa = " + scArcBa + " -->");
		writer.println("<!-- ang = " + ang + " -->");

		writer.println("</svg>");
		writer.close();
	}
}
