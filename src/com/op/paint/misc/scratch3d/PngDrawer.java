package com.op.paint.misc.scratch3d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.op.paint.services.RendererUtils;

public class PngDrawer {
	private BufferedImage obi;
	private Graphics2D opG;
	ArrayList<Color> cols = new ArrayList<Color>();
	double dpi = 300;
	double mm2in = 25.4;
	float strokemm = 0.25f;
	float stroke = (float) (dpi * (strokemm / mm2in));
	private String src = "";
	private String opDir = "";
	private String obj;
	private double w = 0;
	private double h = 0;

	public PngDrawer(String opDir, String obj, String src, double w, double h) {
		this.opDir = opDir;
		this.obj = obj;
		this.src = src;
		this.w = w;
		this.h = h;
	}

	void init() throws Exception {
		opG.setStroke(new BasicStroke(stroke));
		opG.setFont(new Font("TimesRoman", Font.PLAIN, 50));
	}

	void drawArc() {
		Color col = getRandomColor();
		// col = Color.BLACK;
		opG.setColor(col);

	}

	private Color getRandomColor() {
		double brightest = 0.75;
		float r = (float) (Math.random() * brightest);
		float g = (float) (Math.random() * brightest);
		float b = (float) (Math.random() * brightest);
		return new Color(r, g, b);
	}

	public void drawArc(int xtl, int ytl, int d, int d2, int aStart, int aEn) {
		opG.drawArc(xtl, ytl, d, d, aStart, aEn);
	}

	public void drawLine(int xx, int yy, int xf, int yf) {
		opG.drawLine((int) (xx), (int) (yy), (int) (xf), (int) (yf));
	}

	public void drawString(String string, int xx, int yy) {
		opG.drawString(string, xx + 20, yy - 20);

	}

	void initPng() throws FileNotFoundException, UnsupportedEncodingException {
		System.out.println("initialising...");
		int ww = (int) (w);
		int hh = (int) (h);
		obi = new BufferedImage(ww, hh, BufferedImage.TYPE_INT_RGB);
		opG = (Graphics2D) obi.getGraphics();
		opG.setColor(Color.WHITE);
		opG.fillRect(0, 0, ww, hh);
	}

	void save() throws Exception {
		File op1 = new File(opDir + src + "OUT.png");
		RendererUtils.savePNGFile(obi, op1, 600);
		System.out.println("Saved " + op1.getPath());
	}
}
