package com.op.scratch3d;

import javafx.geometry.Point3D;

import java.awt.geom.Point2D;

public class TrianglesTest {
    public static final void main(String[] args) {
        Point2D.Double A = new Point2D.Double(0, 0);
        Point2D.Double B = new Point2D.Double(0, 1);
        Point2D.Double C = new Point2D.Double(1, 0);
        Point2D.Double p = new Point2D.Double(0.5, 0.5);
        Point3D a = new Point3D(-1, -1, 1);
        Point3D b = new Point3D(-1, -1, -1);
        Point3D c = new Point3D(1, -1, 1);
        Point3D np = new TrianglesTest().get3DPoint(A, B, C, p, a, b, c);
        System.out.println(a + "," + b + "," + c + ":" + np);
    }

    Point3D get3DPoint(Point2D.Double A, Point2D.Double B, Point2D.Double C, Point2D.Double p, Point3D a, Point3D b, Point3D c) {
        double[] bary = get2DBary(A, B, C, p);
        double bary1 = bary[0];
        double bary2 = bary[1];
        double bary3 = bary[2];
        double ax = bary1 * a.getX();
        double ay = bary1 * a.getY();
        double az = bary1 * a.getZ();
        double bx = bary2 * b.getX();
        double by = bary2 * b.getY();
        double bz = bary2 * b.getZ();
        double cx = bary3 * c.getX();
        double cy = bary3 * c.getY();
        double cz = bary3 * c.getZ();
        Point3D p1 = new Point3D(ax + bx + cx, ay + by + cy, az + bz + cz);
        return p1;
    }

    double[] get2DBary(Point2D.Double A, Point2D.Double B, Point2D.Double C, Point2D.Double p) {
        double area = getArea2D(A, B, C);
        double areau = getArea2D(B, C, p);
        double areav = getArea2D(A, C, p);        // double areaw = getArea2D(A, B, p);
        double fu = areau / area;
        double fv = areav / area;
        double fw = 1 - fv - fu;
        double[] arr = {fu, fv, fw};
        return arr;
    }

    double getArea2D(Point2D.Double A, Point2D.Double B, Point2D.Double C) {
        double a = distance(B, C);
        double b = distance(A, C);
        double c = distance(A, B);
        double p = 0.5 * (a + b + c);
        double area = Math.sqrt(p * (p - a) * (p - b) * (p - c));
        return area;
    }

    double distance(Point2D.Double p1, Point2D.Double p2) {
        double a = (p2.getX() - p1.getX());
        double b = (p2.getY() - p1.getY());
        return Math.sqrt(a * a + b * b);
    }

    double getArea3D(Point3D A, Point3D B, Point3D C) {
        double a = B.distance(C);
        double b = A.distance(C);
        double c = A.distance(B);
        double p = 0.5 * (a + b + c);
        double area = Math.sqrt(p * (p - a) * (p - b) * (p - c));
        return area;
    }
}