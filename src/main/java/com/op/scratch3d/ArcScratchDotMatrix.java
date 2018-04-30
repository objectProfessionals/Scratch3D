package com.op.scratch3d;

import com.owens.oobjloader.builder.ArcScratchDefs;
import com.owens.oobjloader.builder.VertexGeometric;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ArcScratchDotMatrix extends Base {

    private String opDir = hostDir + "output/";
    private String fontsDir = hostDir + "fonts/";

    private String message1 = "SEQ";

    private String obj = message1;
    private BufferedImage ibi;
    private Graphics2D ipG;

    private String src = "ARCscratchDM-" + obj;
    double dpi = 90;
    double mm2in = 25.4;
    double scalemm = 127;
    double scaleMain = dpi * (scalemm / mm2in);
    double sf = 0.1;//1.1

    private double wmm = scalemm * 3;
    private double hmm = scalemm * 3;
    private double w = dpi * (wmm / mm2in);
    private double h = dpi * (hmm / mm2in);

    double ang = 180;
    double num = 6;//20
    double angInc = ang / num;

    private int dmW = 20; // dot matrix side length
    private int dmH = 20; // dot matrix side length
    private VertexGeometric[][] all = new VertexGeometric[dmW][dmH];

    private static ArcScratchDotMatrix scratchDM = new ArcScratchDotMatrix();

    ObjLoader objLoader = new ObjLoader();
    SvgDrawer svgDescriber = new SvgDrawer(opDir, src, w, h);
    private VertexTransformer vertexTransformer;

    /**
     * @param args
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws XPathExpressionException
     */
    public static void main(String[] args) throws Exception {
        // scratchDM.paint();
        scratchDM.loadOBJ();

    }

    private void loadOBJ() throws Exception {
        init();
        drawAllPoints();
        save();

    }

    private void drawAllPoints() {
        for (int y = 0; y < dmH; y++) {
            for (int x = 0; x < dmW; x++) {
                drawVisibleArcs(all[x][y]);
            }
        }

    }

    private void drawVisibleArcs(VertexGeometric vg) {
        ArrayList<Boolean> arcs = vg.defs.arcs;
        double st = -ang / 2;
        double r = 30;

        double xc = vg.defs.cx * r*2;
        double yc = vg.defs.cy * r*2;
        double xOff = (w-(4*r*(num+1)))/2;
        double yOff = -200+(h-(4*r*(num+1)))/2;
        double z = vg.z;
        double startPosAng = vg.defs.startPosAng;

        double arcSt = st;
        double arcEn = st + angInc;
        boolean lastArcOn = false;
        for (int i = 0; i < arcs.size(); i++) {
            boolean arcOn = arcs.get(i);
            if (arcOn) {
                if ((i == arcs.size() - 1)) {
                    drawSVGS(r, xc + xOff, yc + yOff, z, startPosAng, arcSt, arcEn);
                }
                lastArcOn = true;
                arcEn = arcEn + angInc;
            } else {
                if (lastArcOn) {
                    drawSVGS(r, xc + xOff, yc + yOff, z, startPosAng, arcSt, arcEn);
                    arcSt = arcEn;
                }
                lastArcOn = false;
                arcEn = arcEn + angInc;
                arcSt = arcSt + angInc;
            }
        }
    }

    private void drawSVGS(double r, double xc, double yc, double z, double startPosAng,
                            double stAng, double enAng) {
        double g = 180;
        double y = -yc;
        double s = (startPosAng - stAng) + g;
        double e = (startPosAng - enAng) + g;
        svgDescriber.drawAndAddArc(xc, yc, r, s, e);
    }

    private void init() throws IOException {
        System.out.println("initialising...");

        ibi = ImageIO.read(new File(fontsDir + "/" + message1 + ".jpg"));
        int w = 1;
        int h = 1;
        int arcRad = 1;


        int scale = 1;
        for (int y = 0; y < dmH; y++) {
            for (int x = 0; x < dmW; x++) {
                VertexGeometric vg = new VertexGeometric(x, y, 0);
                vg.defs = new ArcScratchDefs();
                vg.defs.cx = x * scale;
                vg.defs.cy = y + scale;
                vg.defs.r = arcRad;
                vg.defs.startPosAng = 90;
                all[x][y] = vg;
            }
        }

        int wF = 20;
        for (int i = 0; i < 6; i++) {
            for (int y = 0; y < dmH; y++) {
                for (int x = 0; x < dmW; x++) {
                    int xx = (x * w) + (i * wF);
                    int yy = y * h;
                    int rgb = ibi.getRGB(xx, yy);
                    double r = (rgb >> 16) & 0x000000FF;
                    double g = (rgb >> 8) & 0x000000FF;
                    double b = (rgb) & 0x000000FF;
                    double c = (((r + g + b) / 3.0));

                    if (c < 100) {
                        all[x][y].defs.arcs.add(true);
                    } else {
                        all[x][y].defs.arcs.add(false);
                    }
                }
            }
        }


        svgDescriber.startSVG(true, false);

        System.out.println("...finished initialising");
    }

    private void save() throws Exception {
        svgDescriber.endSVG();
    }
}
