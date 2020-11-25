package com.op.scratch3d.export;

import com.op.scratch3d.Base;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

public class ObjExporter extends Base {
    private static final ObjExporter exporter = new ObjExporter();

    String opDir = hostDir + "exports/";
    String file = "wall";
    double mm2in = 25.4;
    PrintWriter writer;
    double w = 9;
    double h = 14;
    double d = 20;
    int cubeNum = 0;

    public static void main(String[] args) throws Exception {
        exporter.draw();
    }

    private void draw() {
        init();
        start();

        drawAll();

        end();
    }

    private void drawAll() {
        for (int z=0; z<d; z++) {
            for (int y=0; y<h; y++) {
                for (int x=0; x<w; x++) {
                    addCube(1, x, y, z);
                }
            }
        }
    }

    public void init() {
        try {
            writer = new PrintWriter(opDir + file + ".obj", "UTF-8");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void start() {
        writer.println("# cube.obj");
        writer.println("#");
        writer.println("");
    }

    public void end() {
        writer.close();
        System.out.println("saved svg : " + opDir + file + ".obj");
    }

    private double formatD(double d) {
        return new BigDecimal(d).setScale(2,
                BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private void addCube(double size, double x, double y, double z) {
        writer.println("o cube"+cubeNum);
        writer.println("");
        writer.println("v " + formatD((-0.500000 * size) + x) + " " + formatD((-0.500000*size) + y) + " " + formatD((0.500000*size) + z));
        writer.println("v " + formatD((0.500000 * size) + x) + " " + formatD((-0.500000*size) + y) + " " + formatD((0.500000*size) + z));
        writer.println("v " + formatD((-0.500000 * size) + x) + " " + formatD((0.500000*size) + y) + " " + formatD((0.500000*size) + z));
        writer.println("v " + formatD((0.500000 * size) + x) + " " + formatD((0.500000*size) + y) + " " + formatD((0.500000*size) + z));

        writer.println("v " + formatD((-0.500000 * size) + x) + " " + formatD((0.500000*size) + y) + " " + formatD((-0.500000*size) + z));
        writer.println("v " + formatD((0.500000 * size) + x) + " " + formatD((0.500000*size) + y) + " " + formatD((-0.500000*size) + z));
        writer.println("v " + formatD((-0.500000 * size) + x) + " " + formatD((-0.500000*size) + y) + " " + formatD((-0.500000*size) + z));
        writer.println("v " + formatD((0.500000 * size) + x) + " " + formatD((-0.500000*size) + y) + " " + formatD((-0.500000*size) + z));

//        writer.println("");
//        writer.println("vt 0.000000 0.000000");
//        writer.println("vt 1.000000 0.000000");
//        writer.println("vt 0.000000 1.000000");
//        writer.println("vt 1.000000 1.000000");
//        writer.println("");
//        writer.println("vn 0.000000 0.000000 1.000000");
//        writer.println("vn 0.000000 1.000000 0.000000");
//        writer.println("vn 0.000000 0.000000 -1.000000");
//        writer.println("vn 0.000000 -1.000000 0.000000");
//        writer.println("vn 1.000000 0.000000 0.000000");
//        writer.println("vn -1.000000 0.000000 0.000000");
//        writer.println("");
        writer.println("g cube"+cubeNum);
//        writer.println("usemtl cube"+cubeNum);
        int ss = cubeNum * 6;
        int st = cubeNum * 8;

//        writer.println("s "+(ss+1));
        writer.println("f "+(st+1)+"/"+(st+1)+"/"+(st+1)+" "+(st+2)+"/"+(st+2)+"/"+(st+1)+" "+(st+3)+"/"+(st+3)+"/"+(st+1));

        writer.println("f "+(st+3)+"/"+(st+3)+"/"+(st+1)+" "+(st+2)+"/"+(st+2)+"/"+(st+1)+" "+(st+4)+"/"+(st+4)+"/"+(st+1)+"");
//        writer.println("s "+(ss+2));
        writer.println("f "+(st+3)+"/"+(st+1)+"/"+(st+2)+" "+(st+4)+"/"+(st+2)+"/"+(st+2)+" "+(st+5)+"/"+(st+3)+"/"+(st+2)+"");
        writer.println("f "+(st+5)+"/"+(st+3)+"/"+(st+2)+" "+(st+4)+"/"+(st+2)+"/"+(st+2)+" "+(st+6)+"/"+(st+4)+"/"+(st+2)+"");
//        writer.println("s "+(ss+3));
        writer.println("f "+(st+5)+"/"+(st+4)+"/"+(st+3)+" "+(st+6)+"/"+(st+3)+"/"+(st+3)+" "+(st+7)+"/"+(st+2)+"/"+(st+3)+"");
        writer.println("f "+(st+7)+"/"+(st+2)+"/"+(st+3)+" "+(st+6)+"/"+(st+3)+"/"+(st+3)+" "+(st+8)+"/"+(st+1)+"/"+(st+3)+"");
//        writer.println("s "+(ss+4));
        writer.println("f "+(st+7)+"/"+(st+1)+"/"+(st+4)+" "+(st+8)+"/"+(st+2)+"/"+(st+4)+" "+(st+1)+"/"+(st+3)+"/"+(st+4)+"");
        writer.println("f "+(st+1)+"/"+(st+3)+"/"+(st+4)+" "+(st+8)+"/"+(st+2)+"/"+(st+4)+" "+(st+2)+"/"+(st+4)+"/"+(st+4)+"");
//        writer.println("s "+(ss+5));
        writer.println("f "+(st+2)+"/"+(st+1)+"/"+(st+5)+" "+(st+8)+"/"+(st+2)+"/"+(st+5)+" "+(st+4)+"/"+(st+3)+"/"+(st+5)+"");
        writer.println("f "+(st+4)+"/"+(st+3)+"/"+(st+5)+" "+(st+8)+"/"+(st+2)+"/"+(st+5)+" "+(st+6)+"/"+(st+4)+"/"+(st+5)+"");
//        writer.println("s "+(ss+6));
        writer.println("f "+(st+7)+"/"+(st+1)+"/"+(st+6)+" "+(st+1)+"/"+(st+2)+"/"+(st+6)+" "+(st+5)+"/"+(st+3)+"/"+(st+6)+"");
        writer.println("f "+(st+5)+"/"+(st+3)+"/"+(st+6)+" "+(st+1)+"/"+(st+2)+"/"+(st+6)+" "+(st+3)+"/"+(st+4)+"/"+(st+6)+"");
        cubeNum++;
    }
}
