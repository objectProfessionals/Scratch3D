package com.op.paint.misc.scratch3d;

import java.awt.geom.Path2D;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import com.owens.oobjloader.builder.Build;
import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.FaceVertex;
import com.owens.oobjloader.builder.VertexGeometric;

public class ObjLoader {
	double vanishZ = 10;

	public ArrayList<Face> loadOBJ(String dirAndFile, ArrayList<VertexGeometric> allPoints)
			throws FileNotFoundException, IOException {
		Build builder = new Build();

		Parse obj0 = new Parse(builder, dirAndFile + ".obj");
		ArrayList<VertexGeometric> v0 = obj0.builder.getVertices();
		for (VertexGeometric v : v0) {
			// Point3f p = new Point3f(v.x, -v.z, v.y);
			VertexGeometric vg = new VertexGeometric(v.x, -v.z, v.y);
			allPoints.add(vg);

		}
		ArrayList<Face> originalFaces = obj0.builder.getFaces();
		return originalFaces;
	}

	ArrayList<VertexGeometric> adjustPoints(ArrayList<VertexGeometric> points) {
		ArrayList<VertexGeometric> points2 = new ArrayList<VertexGeometric>();
		for (VertexGeometric p : points) {
			VertexGeometric vg = adjustPoint(p);
			points2.add(vg);
		}
		return points2;
	}

	VertexGeometric adjustPoint(VertexGeometric p) {
		double sc = getScaleForAdjusts(p);
		double x = p.x * sc;
		double y = p.y * sc;
		VertexGeometric vg = new VertexGeometric((float) x, (float) y, (float) p.z);
		return vg;
	}

	double getScaleForAdjusts(VertexGeometric p) {
		double sc = (vanishZ + p.z) / vanishZ;
		return sc;
	}

	boolean isVertexVisible(ArrayList<Face> faces, VertexGeometric p) {
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

}
