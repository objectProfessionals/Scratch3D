package com.op.scratch3d;

public class ScratchArc {
	double xc = 0;
	double yc = 0;
	double r = 0;
	double xtl = 0;
	double ytl = 0;
	double d = 0;
	int angStart = 0;
	int angArcDraw = 0;

	public ScratchArc(double xtl, double ytl, double xc, double yc, double r, double d, int angStart, int angArcDraw) {
		this.xc = xc;
		this.yc = yc;
		this.r = r;
		this.xtl = xtl;
		this.ytl = ytl;
		this.d = d;
		this.angStart = angStart;
		this.angArcDraw = angArcDraw;
	}

	@Override
	public boolean equals(Object obj) {
		ScratchArc arc2 = (ScratchArc) obj;
		return (this.xc == arc2.xc && this.yc == arc2.yc && this.r == arc2.r && this.xtl == arc2.xtl
				&& this.ytl == arc2.ytl && this.d == arc2.d && this.angStart == arc2.angStart
				&& this.angArcDraw == arc2.angArcDraw);
	}
}
