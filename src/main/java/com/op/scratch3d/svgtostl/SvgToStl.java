package com.op.scratch3d.svgtostl;

import com.op.scratch3d.Base;
import org.apache.batik.parser.DefaultPointsHandler;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PointsHandler;
import org.apache.batik.parser.PointsParser;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class SvgToStl extends Base {
    private String dir = hostDir + "output/";
    private String svg = dir + "SimpleCircle.svg";
    int w = 0;
    int h = 0;

    BufferedImage[] obi;
    Graphics2D[] opG;

    public static final SvgToStl svgToStl = new SvgToStl();

    public static void main(String[] argv) throws Exception {
        List points = svgToStl.extractPoints();


        int i = 0;
    }


    public List extractPoints() throws ParseException, FileNotFoundException {
        final LinkedList points = new LinkedList();
        PointsParser pp = new PointsParser();
        PointsHandler ph = new DefaultPointsHandler() {
            public void point(float x, float y) throws ParseException {
                Point2D p = new Point2D.Float(x, y);
                points.add(p);
            }
        };
        pp.setPointsHandler(ph);
        InputStream is = new FileInputStream(svg);
        pp.parse(is, null);
        return points;
    }
}
