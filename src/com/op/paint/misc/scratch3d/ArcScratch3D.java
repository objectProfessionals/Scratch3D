package com.op.paint.misc.scratch3d;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import com.owens.oobjloader.builder.ArcScratchDefs;
import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.FaceVertex;
import com.owens.oobjloader.builder.VertexGeometric;

public class ArcScratch3D {

	private String dir = "host/images/out/misc/scratch3d/";
	private String opDir = "../output/";
	private String objDir = "objFiles/";

	// private String obj = "tieMini";
	private String obj = "cubeLow";
	// private String obj = "cubeEdgeCut";
	// private String obj = "cubeLowWithEdges";
	// private String obj = "cubeHiStraight";
	// private String obj = "DeathStar";
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
	double num = 10;
	double angInc = ang / (2 * num);

	private int cx = (int) (w / 2.0);
	private int cy = (int) (h / 2.0);

	ArrayList<VertexGeometric> allPoints = new ArrayList<VertexGeometric>();
	ArrayList<Face> originalFaces = new ArrayList<Face>();

	private static ArcScratch3D scratch3D = new ArcScratch3D();

	double vanZ = 10;
	ObjLoader objLoader = new ObjLoader();
	SvgDrawer svgDescriber = new SvgDrawer(opDir, src, w, h);
	boolean savePNG = false;
	private VertexTransformer vertexTransformer;

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
		vertexTransformer = new VertexTransformer(originalFaces, vanZ);

		drawAllPoints();
		save();

	}

	private void drawAllPoints() {
		boolean adjustForPerspective = false;
		boolean occlude = true;
		if (occlude) {
			drawTransformedFacesForArc(adjustForPerspective);
		}

	}

	public void drawTransformedFacesForArc(boolean adjustForPerspective) {
		double sf = 0.5;
		for (Face face : originalFaces) {
			for (FaceVertex fv : face.vertices) {
				VertexGeometric vg = fv.v;
				// vg = vertexTransformer.adjustPointForPerspective(vg);

				double x = vg.x;
				double y = vg.y;
				double z = vg.z * 0.5;
				double rad = sf * scaleMain * (Math.abs(z));
				double xx = x * scaleMain * sf;
				double yy = y * scaleMain * sf;

				if (vg.defs == null) {
					vg.defs = new ArcScratchDefs();
					vg.defs.cx = xx;
					vg.defs.cy = z < 0 ? -rad : rad;
					vg.defs.r = rad;
					vg.defs.startPosAng = z < 0 ? 90 : 270;
				}
			}
		}

		for (double a = -ang / 2; a <= ang / 2; a = a + angInc) {
			ArrayList<Face> rotatedFaces = new ArrayList<Face>();
			HashMap<VertexGeometric, VertexGeometric> orig2rot = new HashMap<VertexGeometric, VertexGeometric>();
			for (Face face : originalFaces) {
				Face rotatedFace = new Face();
				for (FaceVertex fv : face.vertices) {
					VertexGeometric origv = fv.v;
					ArcScratchDefs def = origv.defs;
					double cx = def.cx;
					double cy = def.cy;
					double r = def.r;
					double st = def.startPosAng;
					double resAng = st + a;
					double resAngRads = Math.toRadians(resAng);
					double resx = cx + r * Math.cos(resAngRads);
					double resy = cy + r * Math.sin(resAngRads);

					FaceVertex rotatedfv = new FaceVertex();
					VertexGeometric rotatedvg = new VertexGeometric((float) (resx), (float) (resy), origv.z);
					rotatedfv.v = rotatedvg;
					rotatedFace.vertices.add(rotatedfv);
					orig2rot.put(origv, rotatedvg);
				}
				rotatedFaces.add(rotatedFace);
			}

			for (VertexGeometric origvg : orig2rot.keySet()) {
				VertexGeometric rotvg = orig2rot.get(origvg);
				if (!objLoader.isVertexVisible(rotatedFaces, rotvg)) {
					origvg.defs.arcs.add(false);
				} else {
					origvg.defs.arcs.add(true);
				}
			}
		} // all angs

		double st = -ang / 2;
		for (Face face : originalFaces) {
			for (FaceVertex fv : face.vertices) {
				VertexGeometric vg = fv.v;
				double xc = vg.defs.cx;
				double yc = vg.defs.cy;
				double r = vg.defs.r;
				double startPosAng = vg.defs.startPosAng;

				ArrayList<Boolean> arcs = vg.defs.arcs;
				boolean started = true;
				boolean startedArc = true;
				double stAng = st;
				double enAng = st;
				for (int i = 0; i < arcs.size(); i++) {
					boolean arcOnOff = arcs.get(i);
					if (i == arcs.size() - 1 && startedArc) {
						double s = startPosAng + stAng;
						double e = startPosAng + enAng;
						svgDescriber.drawAndAddArc(cx + xc, cy + yc, r, s, e);
					}

					if (!started) {
						if (!arcOnOff) {
							if (startedArc) {
								double s = startPosAng + stAng;
								double e = startPosAng + enAng;
								svgDescriber.drawAndAddArc(cx + xc, cy + yc, r, s, e);
								stAng = enAng + angInc;
								enAng = enAng + angInc;
								startedArc = false;
							} else {
								stAng = enAng;
								enAng = enAng + angInc;
							}
						} else {
							startedArc = true;
							enAng = enAng + angInc;
						}
					} else {
						started = false;
						startedArc = false;
					}
				}
				// if (noParts) {
				// svgDescriber.drawAndAddArc(cx + xc, cy + yc, r, startPosAng +
				// st, startPosAng + st + aa);
				// }
			}
		}

	}

	private void drawArc(VertexGeometric p1, int c) {
		VertexGeometric p2 = p1;

		double x = p2.x;
		double y = p2.y;
		double z = p2.z * 0.5;
		double rad = scaleMain * 0.25 * (Math.abs(z));
		double xx = cx + x * scaleMain;
		double yy = cy + y * scaleMain;

		double midA = p1.z <= 0 ? 270 : 90;
		double angSt = midA - ang / 2;
		double angEn = angSt + ang;

		double aa = angInc;

		if (p2.defs == null) {
			p2.defs = new ArcScratchDefs();
			p2.defs.cx = xx - rad;
			p2.defs.cy = yy - rad;
			p2.defs.r = rad;
		}

		for (double a = angSt; a < angSt + ang; a = a + aa) {
			int xtl = (int) (xx - rad);
			int ytl = (int) (yy - rad);
			int r = (int) rad;
			int d = (int) rad * 2;
			int aStart = (int) (a);
			int aEn = (int) (aa);
			svgDescriber.saveArc(xtl, ytl, xtl + r, ytl + r, r, d, aStart, aEn);
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
