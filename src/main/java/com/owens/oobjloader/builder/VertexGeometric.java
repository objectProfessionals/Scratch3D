package com.owens.oobjloader.builder;

import java.util.Objects;

public class VertexGeometric implements Comparable<VertexGeometric> {

	public float x = 0;
	public float y = 0;
	public float z = 0;
	public ArcScratchDefs defs;

	public VertexGeometric(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public String toString() {
		if (null == this) {
			return "null";
		} else {
			return x + "," + y + "," + z;
		}
	}

	@Override
	public boolean equals(Object o) {
		VertexGeometric vg = (VertexGeometric) o;
		return (this.toString().equals(vg.toString()));
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public int compareTo(VertexGeometric o) {
		return (int) z;
	}
}