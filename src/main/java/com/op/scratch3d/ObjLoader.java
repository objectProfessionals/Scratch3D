package com.op.scratch3d;

import com.owens.oobjloader.builder.Build;
import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.FaceVertex;
import com.owens.oobjloader.builder.VertexGeometric;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ObjLoader {

    public HashMap<String, ArrayList<Face>> groups;

    public ObjLoader() {
    }

    public ArrayList<Face> loadOBJ(String dirAndFile, ArrayList<VertexGeometric> allPoints)
            throws FileNotFoundException, IOException {
        Build builder = new Build();

        Parse obj0 = new Parse(builder, dirAndFile + ".obj");
        groups = obj0.builder.getGroups();

//        allPoints.addAll(obj0.builder.getVertices());

        ArrayList<VertexGeometric> v0 = obj0.builder.getVertices();
        for (VertexGeometric v : v0) {
            // Point3f p = new Point3f(v.x, -v.z, v.y);
            VertexGeometric vg = new VertexGeometric(v.x, v.y, v.z);
            allPoints.add(vg);

        }
        ArrayList<Face> originalFaces = obj0.builder.getFaces();
        return originalFaces;
    }

    public ArrayList<VertexGeometric> loadOBJSelectedVerts(String dirAndFile)
            throws FileNotFoundException, IOException {
        Build builder = new Build();

        Parse obj0 = new Parse(builder, dirAndFile + ".obj");

        ArrayList<VertexGeometric> allPoints = new ArrayList<VertexGeometric>();
        ArrayList<VertexGeometric> v0 = obj0.builder.getVertices();
        for (VertexGeometric v : v0) {
            // Point3f p = new Point3f(v.x, -v.z, v.y);
            VertexGeometric vg = new VertexGeometric(v.x, v.y, v.z);
            allPoints.add(vg);

        }
        return allPoints;
    }

    boolean isVertexVisible(ArrayList<Face> faces, VertexGeometric p) {
        ArrayList<Face> all = getAllNonJoiningFaces(faces, p);
        ArrayList<Face> allFiltered = getAllFacesWithLargerZ(all, p);

        return !anyFacesHavePointInside(allFiltered, p);
    }

    private boolean anyFacesHavePointInside(ArrayList<Face> allFiltered, VertexGeometric p) {
        for (Face face : allFiltered) {
            if (isPointContained(face, p) && isPZBehind(face, p)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPZBehind(Face face, VertexGeometric p) {
        if (face.vertices.size() != 3) {
            return true;
        }
        double zT = getZOnTriangle(face.vertices.get(0).v, face.vertices.get(1).v, face.vertices.get(2).v, p.x, p.y);
        return (zT >= p.z);
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

        double insideF = 0.999;
        Point2D pp = new Point2D.Double(p.x * insideF, p.y * insideF);
        return path.contains(pp);
    }

    double getZOnTriangle(VertexGeometric p1, VertexGeometric p2, VertexGeometric p3, double x, double y) {
        double det = (p2.y - p3.y) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.y - p3.y);

        double l1 = ((p2.y - p3.y) * (x - p3.x) + (p3.x - p2.x) * (y - p3.y)) / det;
        double l2 = ((p3.y - p1.y) * (x - p3.x) + (p1.x - p3.x) * (y - p3.y)) / det;
        double l3 = 1.0f - l1 - l2;

        return l1 * p1.z + l2 * p2.z + l3 * p3.z;
    }

    private ArrayList<Face> getAllFacesWithLargerZ(ArrayList<Face> all, VertexGeometric p) {
        ArrayList<Face> filtered = new ArrayList<Face>();
        for (Face face : all) {
            boolean toAdd = false;
            for (FaceVertex fv : face.vertices) {
                if (fv.v.z > p.z) {
                    toAdd = true;
                    break;
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
                    // if (fv.v.equals(p)) {
                    toAdd = false;
                    break;
                }
            }
            if (toAdd) {
                non.add(face);
            }
        }
        return non;
    }

    public boolean equals(VertexGeometric v, VertexGeometric p) {
        return (equals(v.x, p.x)) && (equals(v.y, p.y)) && (equals(v.z, p.z));
    }

    private boolean equals(float x, float x2) {
        return Math.abs(x - x2) < 0.001; //0.001;
    }

}
