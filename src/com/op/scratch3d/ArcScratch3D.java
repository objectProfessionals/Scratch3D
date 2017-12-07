package com.op.scratch3d;

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

public class ArcScratch3D extends Base{

	private String opDir = hostDir+"output/";
	private String objDir = hostDir+"objFiles/";

	// private String obj = "earth";
	//private String obj = "cubeLow";
	// private String obj = "cubeEdgeCut";
	// private String obj = "cubeCuts";
	// private String obj = "cubeHi";
	// private String obj = "spheres";
	// private String obj = "Falcon";
	// private String obj = "cubeLowWithEdges";
	// private String obj = "cubeHiStraight";
	// private String obj = "DeathStar";
	// private String obj = "tieFull";
	// private String obj = "spikey";
	//private String obj = "KD-SphereHoles";
	// private String obj = "spheres10";
	// private String obj = "coneHi";
	// private String obj = "sphereMed";
	// private String obj = "cubeHoleMax";
	// private String obj = "hook";
	// private String obj = "cubeHole2";
	// private String obj = "test-planes";
	// private String obj = "test-z";
	// private String obj = "test-pyramidSq";
//	private String obj = "KD-TorusKnot2";
	//private String obj = "KD-NJoint";
	//private String obj = "SW_Vader";
	//private String obj = "SW_Trooper";
	private String obj = "SW_tieBest2";

	boolean doClip = true;
	boolean selectedOnly = false;

	private String src = "ARCscratch3D-" + obj;
	double dpi = 300;
	double mm2in = 25.4;
	double scalemm = 30;
	double scaleMain = dpi * (scalemm / mm2in);
	double sf = 1.1;//1.1

	private double wmm = scalemm * 3;
	private double hmm = scalemm * 3;
	private double w = dpi * (wmm / mm2in);
	private double h = dpi * (hmm / mm2in);

	double ang = 90;
	double num = 20;
	double angInc = ang / (4 * num);

	private int cx = (int) (w / 2.0);
	private int cy = (int) (h / 2.0);

	ArrayList<VertexGeometric> allPoints = new ArrayList<VertexGeometric>();
	ArrayList<Face> originalFaces = new ArrayList<Face>();
	ArrayList<VertexGeometric> selectedVerts = new ArrayList<VertexGeometric>();
	public HashMap<String, ArrayList<Face>> groups;

	private static ArcScratch3D scratch3D = new ArcScratch3D();

	double vanZ = 10;
	double minRadF = 0.95;

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
		originalFaces = objLoader.loadOBJ(objDir + obj, allPoints);
		vertexTransformer = new VertexTransformer(originalFaces, vanZ);

		if (selectedOnly) {
			selectedVerts = objLoader.loadOBJSelectedVerts(objDir + obj + "_sel");
		}

		groups = objLoader.groups;
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
		for (Face face : originalFaces) {
			for (FaceVertex fv : face.vertices) {
				VertexGeometric vg = fv.v;
				// vg = vertexTransformer.adjustPointForPerspective(vg);
				double sc = vertexTransformer.getScaleForPerspectiveAdjusts(vg);
				double x = vg.x * sc;
				double y = vg.y * sc;

				// double x = vg.x;
				// double y = vg.y;
				double z = vg.z;
				double rad = (1 - minRadF) + minRadF * (Math.abs(z));

				if (vg.defs == null) {
					vg.defs = new ArcScratchDefs();
					vg.defs.cx = x;
					// outward = 270 = '-'
					vg.defs.cy = z > 0 ? y + rad : y - rad;
					vg.defs.r = rad;
					vg.defs.startPosAng = z > 0 ? 270 : 90;
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
				boolean clipped = isVertexClipped(rotvg, origvg.defs, a);
				boolean visible = objLoader.isVertexVisible(rotatedFaces, rotvg);
				if ((doClip && clipped) || !visible) {
					origvg.defs.arcs.add(false);
				} else {
					origvg.defs.arcs.add(true);
				}
			}
		} // all angs

		ArrayList<VertexGeometric> used = new ArrayList<VertexGeometric>();
		for (Face face : originalFaces) {
			for (FaceVertex fv : face.vertices) {
				VertexGeometric vg = fv.v;
				if (used.contains(vg)) {
					continue;
				}
				if (selectedOnly && !selectedVertsContains(vg)) {
					continue;
				}
				used.add(vg);

				ArrayList<Boolean> arcs = vg.defs.arcs;
				drawVisibleArcs(arcs, vg);
			}
		}
	}

	private void drawVisibleArcs(ArrayList<Boolean> arcs, VertexGeometric vg) {
		double st = -ang / 2;
		double ss = scaleMain * sf;
		double r = vg.defs.r * ss;
		// r = 0.25 * ss + 0.75 * vg.defs.r * ss;
		double xc = vg.defs.cx * ss;
		double yc = vg.defs.cy * ss;
		double z = vg.z;
		double startPosAng = vg.defs.startPosAng;

		double arcSt = st;
		double arcEn = st + angInc;
		boolean lastArcOn = false;
		for (int i = 0; i < arcs.size(); i++) {
			double arcSt2 = arcSt + angInc;
			double arcEn2 = arcEn - angInc;
			boolean arcOn = arcs.get(i);
			if (arcOn) {
				if ((i == arcs.size() - 1)) {
					drawSVGSrc(vg, r, xc, yc, z, startPosAng, arcSt2, arcEn2);
				}
				lastArcOn = true;
				arcEn = arcEn + angInc;
			} else {
				if (lastArcOn) {
					drawSVGSrc(vg, r, xc, yc, z, startPosAng, arcSt2, arcEn2);
					arcSt = arcEn;
				}
				lastArcOn = false;
				arcEn = arcEn + angInc;
				arcSt = arcSt + angInc;
			}
		}
	}

	private boolean isVertexClipped(VertexGeometric vg, ArcScratchDefs defs, double a) {
		double ss = scaleMain * sf;
		double r = defs.r * ss;
		double xc = defs.cx * ss;
		double yc = defs.cy * ss;
		double st = defs.startPosAng;

		double g = 180;
		double y = -yc;
		double s = (st - a) + g;
		return svgDescriber.isClipped(cx + xc, cy + y, r, s - angInc);
	}

	private void drawVisibleArcs1(ArrayList<Boolean> arcs, VertexGeometric vg) {
		double st = -ang / 2;
		double ss = scaleMain * sf;
		double r = vg.defs.r * ss;
		// r = 0.25 * ss + 0.75 * vg.defs.r * ss;
		double xc = vg.defs.cx * ss;
		double yc = vg.defs.cy * ss;
		double z = vg.z;
		double startPosAng = vg.defs.startPosAng;

		boolean started = true;
		boolean startedArc = true;
		double stAng = st;
		double enAng = st;
		for (int i = 0; i < arcs.size(); i++) {
			boolean arcOnOff = arcs.get(i);
			if (i == arcs.size() - 1 && startedArc) {
				drawSVGSrc(vg, r, xc, yc, z, startPosAng, stAng, enAng);
			}

			if (!started) {
				if (!arcOnOff) {
					if (startedArc) {
						drawSVGSrc(vg, r, xc, yc, z, startPosAng, stAng, enAng);
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
	}

	private boolean selectedVertsContains(VertexGeometric vg) {
		for (VertexGeometric v : selectedVerts) {
			if (objLoader.equals(vg, v)) {
				return true;
			}
		}
		return false;
	}

	private void drawSVGSrc(VertexGeometric vg, double r, double xc, double yc, double z, double startPosAng,
			double stAng, double enAng) {
		double g = 180;
		double y = -yc;
		double s = (startPosAng - stAng) + g;
		double e = (startPosAng - enAng) + g;
		svgDescriber.drawAndAddArc(cx + xc, cy + y, r, s, e);
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
