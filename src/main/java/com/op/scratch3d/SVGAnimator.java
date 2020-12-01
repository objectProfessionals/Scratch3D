package com.op.scratch3d;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SVGAnimator extends Base {

    private String animDir = hostDir + "animate/";
    int w = 0;
    int h = 0;

    BufferedImage[] obi;
    Graphics2D[] opG;
    double num = 0;
    double angInc = 0;
    double sweepAng = 0;
    String obj = null;
    int frameTime = 1;
    int gifScale = 1;

    public SVGAnimator(int w, int h, int frameTime, double num, double sweepAng, double angInc, String obj) {
        this.w = w;
        this.h = h;
        this.num = num;
        obi = new BufferedImage[(int) num+1];
        opG = new Graphics2D[(int) num+1];
        this.sweepAng = sweepAng;
        this.angInc = angInc;
        this.obj = obj;
        this.frameTime = frameTime;
        init();
    }

    private void init() {
        System.out.println("initialising...");
        int ww = gifScale * (int) (w);
        int hh = gifScale * (int) (h);
        for (int i = 0; i < num+1; i++) {
            obi[i] = new BufferedImage(ww, hh, BufferedImage.TYPE_INT_RGB);
            opG[i] = (Graphics2D) obi[i].getGraphics();
            opG[i].setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
                    RenderingHints.VALUE_ANTIALIAS_OFF);
            opG[i].setColor(Color.WHITE);
            opG[i].fillRect(0, 0, ww, hh);
            opG[i].setStroke(new BasicStroke(gifScale));
        }
    }

    public void addArc(double cx, double cy, double radius, double z, double startAngle, double endAngle) {
        if (z > 0) {
            double st = (startAngle) % 360;
            double ext = (endAngle - startAngle) % 360;
            for (double a = st; a <= st + ext; a = a + angInc) {
                double sweep = 2 * sweepAng;
                double aa = (a -270)+sweepAng;
                int i = (int)((num) * (aa/sweep));
                drawArc(Color.RED, opG[i], (int) (cx - radius), (int) (cy - radius), (int) (radius * 2), (int) (radius * 2), (int) a, (int) -(angInc));
            }
        } else {
            double st = (startAngle) % 360;
            double ext = (endAngle - startAngle) % 360;
            for (double a = st; a <= st + ext; a = a + angInc) {
                double sweep = 2 * sweepAng;
                double aa = (a -90)+sweepAng;
                int i = (int)((num) * (aa/sweep));
                drawArc(Color.BLUE, opG[i], (int) (cx - radius), (int) (cy - radius), (int) (radius * 2), (int) (radius * 2), (int) a, (int) -(angInc));
            }
//
//
//            double st = (-1 * startAngle) % 360;
//            double ext = -1 * (endAngle - startAngle) % 360;
//            for (double a = st; a <= st + ext; a = a + angInc) {
//                int i = (int) ((num - 1) * ((sweepAng + a + 270) / (2 * sweepAng)));
//                drawArc(Color.BLUE, opG[i], (int) (cx - radius), (int) (cy - radius), (int) (radius * 2), (int) (radius * 2), (int) a, (int) (angInc));
//            }
        }
    }

    private void drawArc(Color col, Graphics2D opG, int tlx, int tly, int w, int h, int aSt, int ext) {
        opG.setColor(col);
        opG.drawArc(tlx*gifScale, tly*gifScale, w*gifScale, h*gifScale, (aSt +(ext/2)), ext);
    }

    public static void main(String[] args) throws IOException {
        SVGAnimator svgAnimator = new SVGAnimator(10, 10, 1, 16, 30, 10, "test");
        svgAnimator.addArc(500, 500, 100, 1, 45, 135);
        svgAnimator.save();
    }

    public void save() {
        BufferedImage firstImage = obi[0];

        String out = animDir + obj + ".gif";
        File fOut = new File(out);
        if (fOut.exists()) {
            fOut.delete();
        }

        ImageOutputStream output = null;
        try {
            output = new FileImageOutputStream(fOut);
        } catch (IOException e) {
            System.out.println("error gif "+e);
            e.printStackTrace();
        }

        try {
            GifSequenceWriter writer = new GifSequenceWriter(output,
                    firstImage.getType(), +frameTime + "", true);

            writer.writeToSequence(firstImage);
            for (int i = 1; i < num; i++) {
                BufferedImage nextImage = obi[i];
                writer.writeToSequence(nextImage);
            }

            writer.close();
            output.close();
            System.out.println("saved gif "+out);

        } catch (IOException e) {
            System.out.println("error gif "+e);
            e.printStackTrace();
        }

        //Base.savePNGFile(obi, animDir+obj+".png", 300);
    }
}
