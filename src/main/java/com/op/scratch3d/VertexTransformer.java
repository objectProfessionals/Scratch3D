package com.op.scratch3d;

import java.util.ArrayList;

import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.FaceVertex;
import com.owens.oobjloader.builder.VertexGeometric;

public class VertexTransformer {

	private ArrayList<Face> originalFaces;
	double vanishZ = 0;

	public VertexTransformer(ArrayList<Face> originalFaces, double vanishZ) {
		this.vanishZ = vanishZ;
		this.originalFaces = originalFaces;
	}

	ArrayList<Face> getTransformedFacesRotateX(double aDegs, boolean adjustForPerspective) {
		ArrayList<Face> tr = new ArrayList<Face>();
		for (Face face : originalFaces) {
			Face newFace = new Face();
			for (FaceVertex fv : face.vertices) {
				FaceVertex fv2 = new FaceVertex();
				VertexGeometric vg = new VertexGeometric(fv.v.x, fv.v.y, fv.v.z);
				fv2.v = rotateVertexX(vg, aDegs, adjustForPerspective);
				newFace.vertices.add(fv2);
			}
			tr.add(newFace);
		}
		return tr;
	}

	ArrayList<Face> getTransformedFacesRotateY(double aDegs, boolean adjustForPerspective) {
		ArrayList<Face> tr = new ArrayList<Face>();
		for (Face face : originalFaces) {
			Face newFace = new Face();
			for (FaceVertex fv : face.vertices) {
				FaceVertex fv2 = new FaceVertex();
				VertexGeometric vg = new VertexGeometric(fv.v.x, fv.v.y, fv.v.z);
				fv2.v = rotateVertexY(vg, aDegs, adjustForPerspective);
				newFace.vertices.add(fv2);
			}
			tr.add(newFace);
		}
		return tr;
	}

	ArrayList<Face> getTransformedFacesRotateZ(double aDegs, boolean adjustForPerspective) {
		ArrayList<Face> tr = new ArrayList<Face>();
		for (Face face : originalFaces) {
			Face newFace = new Face();
			for (FaceVertex fv : face.vertices) {
				FaceVertex fv2 = new FaceVertex();
				VertexGeometric vg = new VertexGeometric(fv.v.x, fv.v.y, fv.v.z);
				fv2.v = rotateVertexZ(vg, aDegs, adjustForPerspective);
				newFace.vertices.add(fv2);
			}
			tr.add(newFace);
		}
		return tr;
	}

	VertexGeometric rotateVertexX(VertexGeometric p1, double aDegs, boolean adjustForPerspective) {
		double aa = Math.toRadians(aDegs);
		double x = p1.x;
		double y = p1.y;
		double z = p1.z;

		double yzR = Math.sqrt(y * y + z * z);
		double yzAng = Math.atan2(z, y);

		double y2 = yzR * Math.cos(aa + yzAng);
		double x2 = x;
		double z2 = yzR * Math.sin(aa + yzAng);

		VertexGeometric vg = new VertexGeometric((float) x2, (float) y2, (float) z2);
		VertexGeometric vga = vg;
		if (adjustForPerspective) {
			vga = adjustPointForPerspective(vg);
		}

		return vga;
	}

	VertexGeometric rotateVertexY(VertexGeometric p1, double aDegs, boolean adjustForPerspective) {
		double aa = Math.toRadians(aDegs);
		double x = p1.x;
		double y = p1.y;
		double z = p1.z;

		double xzR = Math.sqrt(x * x + z * z);
		double xzAng = Math.atan2(z, x);
		double xzAngDeg = Math.toDegrees(xzAng);
		double xzAngTot = xzAngDeg + aDegs;

		double x2 = xzR * Math.cos(aa + xzAng);
		double y2 = y;
		double z2 = xzR * Math.sin(aa + xzAng);

		VertexGeometric vg = new VertexGeometric((float) x2, (float) y2, (float) z2);
		VertexGeometric vga = vg;
		if (adjustForPerspective) {
			vga = adjustPointForPerspective(vg);
		}

		return vga;
	}

	VertexGeometric rotateVertexZ(VertexGeometric p1, double aDegs, boolean adjustForPerspective) {
		double aa = Math.toRadians(aDegs);
		double x = p1.x;
		double y = p1.y;
		double z = p1.z;

		double xyR = Math.sqrt(x * x + y * y);
		double xyAng = Math.atan2(y, x);
		double xyAngDeg = Math.toDegrees(xyAng);
		double xyAngTot = xyAngDeg + aDegs;

		double x2 = xyR * Math.cos(aa + xyAng);
		double y2 = xyR * Math.sin(aa + xyAng);
		double z2 = z;

		VertexGeometric vg = new VertexGeometric((float) x2, (float) y2, (float) z2);
		VertexGeometric vga = vg;
		if (adjustForPerspective) {
			vga = adjustPointForPerspective(vg);
		}

		return vga;
	}

	VertexGeometric adjustPointForPerspective(VertexGeometric p) {
		double sc = getScaleForPerspectiveAdjusts(p);
		double x = p.x * sc;
		double y = p.y * sc;
		VertexGeometric vg = new VertexGeometric((float) x, (float) y, (float) p.z);
		vg.defs = p.defs;
		return vg;
	}

	double getScaleForPerspectiveAdjusts(VertexGeometric p) {
		double sc = (vanishZ + p.z) / vanishZ;
		return sc;
	}

}
