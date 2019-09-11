/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.utilities;

import java.util.Formatter;
import java.util.Locale;
import java.util.Scanner;

/**
 *
 * @author Márcio
 */
public final class uTxtIO {

    private uTxtIO() {
    }
    //--------------------------- Int -------------------------------------------
    public static void WriteInt(Formatter output, int value, String msg) {
        output.format(Locale.ENGLISH, "%s\n", msg);
        output.format(Locale.ENGLISH, "%s\n", value);
    }

    public static int ReadInt(Scanner input) {
        input.nextLine();
        return Integer.parseInt(input.nextLine());
    }

    public static void WriteVectorInt(Formatter output, int values[], String msg) {
        output.format(Locale.ENGLISH, "%s\n", msg);
        for(int v: values){
            output.format(Locale.ENGLISH, "%s ", v);
        }
    }

    public static void WriteVectorInt(Formatter output, Integer values[], String msg) {
        output.format(Locale.ENGLISH, "%s\n", msg);
        for(int v: values){
            output.format(Locale.ENGLISH, "%s ", v);
        }
    }

    public static int[] ReadVectorInt(Scanner input, int d1) {
        input.nextLine();
        int values[] = new int[d1];
        for(int i=0; i<d1; i++){
            values[i] = input.nextInt();
        }
        input.nextLine();
        return values;
    }

    public static void WriteMatrixInt(Formatter output, int values[][], String msg) {
        output.format(Locale.ENGLISH, "%s\n", msg);
        for(int rows[]: values){
            for(int v: rows){
                output.format(Locale.ENGLISH, "%s ", v);
            }
            output.format(Locale.ENGLISH, "\n");
        }
    }

    public static void WriteMatrixInt(Formatter output, Integer values[][], String msg) {
        output.format(Locale.ENGLISH, "%s\n", msg);
        for(Integer rows[]: values){
            for(int v: rows){
                output.format(Locale.ENGLISH, "%s ", v);
            }
            output.format(Locale.ENGLISH, "\n");
        }
    }

    public static int[][] ReadMatrixInt(Scanner input, int d1, int d2) {
        input.nextLine();
        int values[][] = new int[d1][d2];
        for(int i=0; i<d1; i++){
            for(int j=0; j<d2; j++){
                values[i][j] = input.nextInt();
            }
        }
        input.nextLine();
        return values;
    }
    //--------------------------- double -------------------------------------------
    public static void WriteDouble(Formatter output, double value, String msg) {
        output.format(Locale.ENGLISH, "%s\n", msg);
        output.format(Locale.ENGLISH, "%s\n", value);
    }

    public static double ReadDouble(Scanner input) {
        input.nextLine();
        return Double.parseDouble(input.nextLine());
    }

    public static void WriteVectorDouble(Formatter output, double values[], String msg) {
        output.format(Locale.ENGLISH, "%s\n", msg);
        for(double v: values){
            output.format(Locale.ENGLISH, "%s ", v);
        }
    }

    public static void WriteVectorDouble(Formatter output, Double values[], String msg) {
        output.format(Locale.ENGLISH, "%s\n", msg);
        for(double v: values){
            output.format(Locale.ENGLISH, "%s ", v);
        }
    }

    public static double[] ReadVectorDouble(Scanner input, int d1) {
        input.nextLine();
        double values[] = new double[d1];
        for(int i=0; i<d1; i++){
            values[i] = Double.parseDouble(input.next());
        }
        input.nextLine();
        return values;
    }

    public static void WriteMatrixDouble(Formatter output, double values[][], String msg) {
        output.format(Locale.ENGLISH, "%s\n", msg);
        for(double rows[]: values){
            for(double v: rows){
                output.format(Locale.ENGLISH, "%s ", v);
            }
            output.format(Locale.ENGLISH, "\n");
        }
    }

    public static void WriteMatrixDouble(Formatter output, Double values[][], String msg) {
        output.format(Locale.ENGLISH, "%s\n", msg);
        for(Double rows[]: values){
            for(double v: rows){
                output.format(Locale.ENGLISH, "%s ", v);
            }
            output.format(Locale.ENGLISH, "\n");
        }
    }

    public static double[][] ReadMatrixDouble(Scanner input, int d1, int d2) {
        input.nextLine();
        double values[][] = new double[d1][d2];
        for(int i=0; i<d1; i++){
            for(int j=0; j<d2; j++){
                values[i][j] = Double.parseDouble(input.next());
            }
        }
        input.nextLine();
        return values;
    }

    
}
