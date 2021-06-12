package com.op.scratch3d.sounds;

import com.op.scratch3d.Base;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static java.lang.Math.abs;

public class PlotVolumePNG extends Base {
    private static final String scName = "ILoveYouVirga500"; // "hello20";
    //private static final String scName = "ILoveYouMax1"; // "hello20";
    //private static final String scName = "ILoveYou20"; // "hello20";
    //private static final String scName = "sine100-500-0.3s"; // "hello20";
    private static final String opFileName = "SoundGrey" + scName; // "hello20";
    private String opDir = hostDir + "output/";
    private String ipDir = hostDir + "sounds/";
    private static final String outFileExt = ".png";
    private String outFile = opDir+opFileName+outFileExt;
    private double volAmp = 1;
    private static PlotVolumePNG tester;
    private WaveFileReader reader;
    private double dpi = 300;
    private double mm2in = 25.4;
    private double wmm = 0;//LINEAR
    private double hmm = 10;
    private int w = (int) (dpi * (wmm / mm2in));
    private int h = (int) (dpi * (hmm / mm2in));
    private int cx = w / 2;
    private int cy = h / 2;
    private double bordermm = 20;
    private double max = -1;
    private double min = 1000000;
    BufferedImage ibi;
    Graphics2D opg;
    BufferedImage obi;

    public static void main(String[] args) throws Exception, FontFormatException {
        tester = new PlotVolumePNG();
        tester.createWavData();
        tester.setupWholeImage();
        tester.drawVolume();
        tester.savePNG();
    }

    protected void setupWholeImage() throws IOException {
        System.out.println("Creating...");

        obi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        opg = (Graphics2D) obi.getGraphics();
        opg.setColor(Color.WHITE);
        opg.fillRect(0, 0, w, h);
    }

    private void createWavData() {
        System.out.println("reading " + scName);
        String src = ipDir;
        reader = new WaveFileReader(src + scName + ".wav");
        for (int i = 0; i < reader.getData()[0].length; i++) {
            double r = reader.getData()[0][i];
            r = getVolume(r);
            if (r > max) {
                max = r;
            }
            if (r < min) {
                min = r;
            }
        }
        w = reader.getDataLen();
        System.out.println(min + ":" + max);
    }

    private void drawVolume() {
        System.out.println("Drawing " + scName + "...");
        int end = (int) (reader.getDataLen());
        for (int i = 1; i < end - 1; i = i + 1) {
            Point2D p = new Point2D.Double(i, getPercentVolume(i));
            drawLine(p);
        }
    }

    private void drawLine(Point2D p) {
        double pc = getPercentVolume((int)p.getX());
        float g = (float) pc;
        opg.setColor(new Color(g, g, g));
        opg.fillRect((int)p.getX(), 0, 1, h);
    }

    private double getPercentVolume(int i) {
        double val = 0;
        val = reader.getData()[0][i];
        double ret = (-min + val) / (max - min);
        // System.out.println(i + ":" + val);
        return ret;
    }

    private double getVolume(double val) {
        if (val < 0) {
            return -Math.pow(-val, volAmp);
        }
        return Math.pow(val, volAmp);
    }

    private void savePNG() {
        savePNGFile(obi, outFile, dpi);
    }
}
