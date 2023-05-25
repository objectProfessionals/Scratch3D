package com.op.scratch3d;

import javafx.geometry.Point3D;

public class Line3D {

    Point3D p1;
    Point3D p2;

    double tol = 0.01;

    @Override
    public boolean equals(Object o) {
        Line3D line3D = (Line3D)o;
        boolean eqx1 = equals(this.p1.getX(), line3D.p1.getX());
        boolean eqy1 = equals(this.p1.getY(), line3D.p1.getY());
        boolean eqz1 = equals(this.p1.getZ(), line3D.p1.getZ());
        boolean eqx2 = equals(this.p2.getX(), line3D.p2.getX());
        boolean eqy2 = equals(this.p2.getY(), line3D.p2.getY());
        boolean eqz2 = equals(this.p2.getZ(), line3D.p2.getZ());

        if (eqx1 && eqx2 && eqy1 && eqy2 && eqz1 && eqz2) {
            return true;
        }

        eqx1 = equals(this.p2.getX(), line3D.p1.getX());
        eqy1 = equals(this.p2.getY(), line3D.p1.getY());
        eqz1 = equals(this.p2.getZ(), line3D.p1.getZ());

        eqx2 = equals(this.p1.getX(), line3D.p2.getX());
        eqy2 = equals(this.p1.getY(), line3D.p2.getY());
        eqz2 = equals(this.p1.getZ(), line3D.p2.getZ());
        if (eqx1 && eqx2 && eqy1 && eqy2 && eqz1 && eqz2) {
            return true;
        }

        return false;
    }

    private boolean equals(double x1, double x2) {
        return (Math.abs(x2-x1))<tol;
    }
}
