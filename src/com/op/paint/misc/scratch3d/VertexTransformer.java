package com.op.paint.misc.scratch3d;

import java.util.ArrayList;

import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.FaceVertex;
import com.owens.oobjloader.builder.VertexGeometric;

public class VertexTransformer {

	private ArrayList<Face> originalFaces;

	public VertexTransformer(ArrayList<Face> originalFaces) {
		this.originalFaces = originalFaces;
	}

	ArrayList<Face> getTransformedFaces(double aDegs, boolean adjustForPerspective, ObjLoader objLoader) {
		ArrayList<Face> tr = new ArrayList<Face>();
		for (Face face : originalFaces) {
			Face newFace = new Face();
			for (FaceVertex fv : face.vertices) {
				FaceVertex fv2 = new FaceVertex();
				VertexGeometric vg = new VertexGeometric(fv.v.x, fv.v.y, fv.v.z);
				fv2.v = transformVertex(vg, aDegs, adjustForPerspective, objLoader);
				newFace.vertices.add(fv2);
			}
			tr.add(newFace);
		}
		return tr;
	}

	VertexGeometric transformVertex(VertexGeometric p1, double aDegs, boolean adjustForPerspective,
			ObjLoader objLoader) {
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
			vga = objLoader.adjustPoint(vg);
		}

		return vga;
	}
}
