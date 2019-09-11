/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Formatter;
import java.util.Locale;
import java.util.Scanner;

/**
 *
 * @author Márcio
 */
public final class uIO {

    private static final String S1 = ",";
    private static final String S2 = "/";
    private static final String S3 = "*";

    

    private uIO() {
    }
    
    public static String ReadTrueName(Scanner sc) {
        while(sc.hasNextLine()){
            if(sc.nextLine().equals("<TrueName>")){
                return sc.nextLine();
            }
        }
        return null;
    }
    public static String ReadTrueNameWithOutExt(Scanner sc, String ext) {
        while(sc.hasNextLine()){
            if(sc.nextLine().equals("<TrueName>")){
                return sc.nextLine().replaceAll("."+ext, "");
            }
        }
        return null;
    }
    
    //----------------------METODOS STATICOS PUBLICOS----------------------------

    public static void ReadAllSgl(String dirNome, String arqNome, Object... obj) throws FileNotFoundException, Exception {
        Scanner input = new Scanner(new File(dirNome + arqNome));
        ReadAllSgl(input, obj);
        input.close();
    }

    public static void ReadAllSgl(Scanner input, Object... obj) throws FileNotFoundException, Exception {
        for (int i = 0; i < obj.length; i++) {
            Object o = obj[i];
            if (o == null) {
                input.nextLine();
                input.nextLine();
            } else if (o instanceof Integer) {
                o = ReadInt(input);
            } else if (o instanceof Float) {
                o = ReadFloat(input);
            } else if (o instanceof Double) {
                o = ReadDouble(input);
            } else if (o instanceof Integer[]) {
                o = ReadVectorInt(input);
            } else if (o instanceof Float[]) {
                o = ReadVectorFloat(input);
            } else if (o instanceof Double[]) {
                o = ReadVectorDouble(input);
            } else if (o instanceof Integer[][]) {
                o = ReadMatrixInt(input);
            } else if (o instanceof Float[][]) {
                o = ReadMatrixFloat(input);
            } else if (o instanceof Double[][]) {
                o = ReadMatrixDouble(input);
            } else if (o instanceof Integer[][][]) {
                o = ReadCubeInt(input);
            } else if (o instanceof Float[][][]) {
                o = ReadCubeFloat(input);
            } else if (o instanceof Double[][][]) {
                o = ReadCubeDouble(input);
            } else {
                throw new Exception("Object not known");
            }
            obj[i] = o;
        }
    }

    public static void WriteAllSgl(Formatter output, Object... obj) throws FileNotFoundException, Exception {
        String name = null;
        for (Object o : obj) {
            if (o == null) {
                output.format("%s\n", "<null>");
                output.format("%s\n", o);
            } else if (o instanceof String) {
                name = (String) o;
            } else if (o instanceof int[]) {
                WriteVectorInt(output, (int[]) o, name != null ? name : "<int[]>");
            } else if (o instanceof float[]) {
                WriteVectorFloat(output, (float[]) o, name != null ? name : "<float[]>");
            } else if (o instanceof double[]) {
                WriteVectorDouble(output, (double[]) o, name != null ? name : "<double[]>");
            } else if (o instanceof int[][]) {
                WriteMatrixInt(output, (int[][]) o, name != null ? name : "<int[][]>");
            } else if (o instanceof float[][]) {
                WriteMatrixFloat(output, (float[][]) o, name != null ? name : "<float[][]>");
            } else if (o instanceof double[][]) {
                WriteMatrixDouble(output, (double[][]) o, name != null ? name : "<double[][]>");
            } else if (o instanceof int[][][]) {
                WriteCubeInt(output, (int[][][]) o, name != null ? name : "<int[][][]>");
            } else if (o instanceof float[][][]) {
                WriteCubeFloat(output, (float[][][]) o, name != null ? name : "<float[][][]>");
            } else if (o instanceof double[][][]) {
                WriteCubeDouble(output, (double[][][]) o, name != null ? name : "<double[][][]>");
            } else if (o instanceof Integer) {
                WriteInt(output, (Integer) o, name != null ? name : "<Integer>");
            } else if (o instanceof Float) {
                WriteFloat(output, (Float) o, name != null ? name : "<Float>");
            } else if (o instanceof Double) {
                WriteDouble(output, (Double) o, name != null ? name : "<Double>");
            } else if (o instanceof Integer[]) {
                WriteVectorInt(output, (Integer[]) o, name != null ? name : "<Integer[]>");
            } else if (o instanceof Float[]) {
                WriteVectorFloat(output, (Float[]) o, name != null ? name : "<Float[]>");
            } else if (o instanceof Double[]) {
                WriteVectorDouble(output, (Double[]) o, name != null ? name : "<Double[]>");
            } else if (o instanceof Integer[][]) {
                WriteMatrixInt(output, (Integer[][]) o, name != null ? name : "<Integer[][]>");
            } else if (o instanceof Float[][]) {
                WriteMatrixFloat(output, (Float[][]) o, name != null ? name : "<Float[][]>");
            } else if (o instanceof Double[][]) {
                WriteMatrixDouble(output, (Double[][]) o, name != null ? name : "<Double[][]>");
            } else if (o instanceof Integer[][][]) {
                WriteCubeInt(output, (Integer[][][]) o, name != null ? name : "<Integer[][][]>");
            } else if (o instanceof Float[][][]) {
                WriteCubeFloat(output, (Float[][][]) o, name != null ? name : "<Float[][][]>");
            } else if (o instanceof Double[][][]) {
                WriteCubeDouble(output, (Double[][][]) o, name != null ? name : "<Double[][][]>");
            } else {
                throw new Exception("Object not known");
            }
        }
    }
    //--------------------------- Bool -------------------------------------------

    public static void WriteVetorBool(Formatter output, boolean valores[], String msg) {
        output.format("%s\n", msg);
        output.format("%s\n", toString(valores));
    }

    public static void WriteVetorBool(Formatter output, Boolean valores[], String msg) {
        output.format("%s\n", msg);
        output.format("%s\n", toString(valores));
    }

    public static boolean ReadBool(Scanner input) {
        input.nextLine();
        return Boolean.parseBoolean(input.nextLine());
    }

    //--------------------------- Int -------------------------------------------
    public static void WriteInt(Formatter output, int valor, String msg) {
        output.format("%s\n", msg);
        output.format("%s\n", valor);
    }

    public static int ReadInt(Scanner input) {
        input.nextLine();
        return Integer.parseInt(input.nextLine());
    }

    public static void WriteVectorInt(Formatter output, int valores[], String msg) {
        output.format("%s\n", msg);
        output.format("%s\n", toString(valores));
    }

    public static void WriteVectorInt(Formatter output, Integer valores[], String msg) {
        output.format("%s\n", msg);
        output.format("%s\n", toString(valores));
    }

    public static int[] ReadVectorInt(Scanner input) {
        input.nextLine();
        return toVectorInt(input.nextLine());
    }

    public static void WriteMatrixInt(Formatter output, int valores[][], String msg) {
        output.format("%s\n", msg);
        output.format("%s\n", toString(valores));
    }

    public static void WriteMatrixInt(Formatter output, Integer valores[][], String msg) {
        output.format("%s\n", msg);
        output.format("%s\n", toString(valores));
    }

    public static int[][] ReadMatrixInt(Scanner input) {
        input.nextLine();
        return toMatrixInt(input.nextLine());
    }

    public static void WriteCubeInt(Formatter output, int valores[][][], String msg) {
        output.format("%s\n", msg);
        output.format("%s\n", toString(valores));
    }

    public static void WriteCubeInt(Formatter output, Integer valores[][][], String msg) {
        output.format("%s\n", msg);
        output.format("%s\n", toString(valores));
    }

    public static int[][][] ReadCubeInt(Scanner input) {
        input.nextLine();
        return toCubeInt(input.nextLine());
    }

    //--------------------------- float -------------------------------------------
    public static void WriteFloat(Formatter output, float valor, String msg) {
        output.format("%s\n", msg);
        output.format("%s\n", valor);
    }

    public static float ReadFloat(Scanner input) {
        input.nextLine();
        return Float.parseFloat(input.nextLine());
    }

    public static void WriteVectorFloat(Formatter output, float valores[], String msg) {
        output.format("%s\n", msg);
        output.format("%s\n", toString(valores));
    }

    public static void WriteVectorFloat(Formatter output, Float valores[], String msg) {
        output.format("%s\n", msg);
        output.format("%s\n", toString(valores));
    }

    public static float[] ReadVectorFloat(Scanner input) {
        input.nextLine();
        return toVectorFloat(input.nextLine());
    }

    public static void WriteMatrixFloat(Formatter output, float valores[][], String msg) {
        output.format("%s\n", msg);
        output.format("%s\n", toString(valores));
    }

    public static void WriteMatrixFloat(Formatter output, Float valores[][], String msg) {
        output.format("%s\n", msg);
        output.format("%s\n", toString(valores));
    }

    public static float[][] ReadMatrixFloat(Scanner input) {
        input.nextLine();
        return toMatrixFloat(input.nextLine());
    }

    public static void WriteCubeFloat(Formatter output, float valores[][][], String msg) {
        output.format("%s\n", msg);
        output.format("%s\n", toString(valores));
    }

    public static void WriteCubeFloat(Formatter output, Float valores[][][], String msg) {
        output.format("%s\n", msg);
        output.format("%s\n", toString(valores));
    }

    public static float[][][] ReadCubeFloat(Scanner input) {
        input.nextLine();
        return toCubeFloat(input.nextLine());
    }

    //--------------------------- double -------------------------------------------
    public static void WriteDouble(Formatter output, double valor, String msg) {
        output.format("%s\n", msg);
        output.format("%s\n", valor);
    }

    public static double ReadDouble(Scanner input) {
        input.nextLine();
        return Double.parseDouble(input.nextLine());
    }

    public static void WriteVectorDouble(Formatter output, double valores[], String msg) {
        output.format("%s\n", msg);
        output.format("%s\n", toString(valores));
    }

    public static void WriteVectorDouble(Formatter output, Double valores[], String msg) {
        output.format("%s\n", msg);
        output.format("%s\n", toString(valores));
    }

    public static double[] ReadVectorDouble(Scanner input) {
        input.nextLine();
        return toVectorDouble(input.nextLine());
    }

    public static void WriteMatrixDouble(Formatter output, double valores[][], String msg) {
        output.format("%s\n", msg);
        output.format("%s\n", toString(valores));
    }

    public static void WriteMatrixDouble(Formatter output, Double valores[][], String msg) {
        output.format("%s\n", msg);
        output.format("%s\n", toString(valores));
    }

    public static double[][] ReadMatrixDouble(Scanner input) {
        input.nextLine();
        return toMatrixDouble(input.nextLine());
    }

    public static void WriteCubeDouble(Formatter output, double valores[][][], String msg) {
        output.format("%s\n", msg);
        output.format("%s\n", toString(valores));
    }

    public static void WriteCubeDouble(Formatter output, Double valores[][][], String msg) {
        output.format("%s\n", msg);
        output.format("%s\n", toString(valores));
    }

    public static double[][][] ReadCubeDouble(Scanner input) {
        input.nextLine();
        return toCubeDouble(input.nextLine());
    }

    public static void WriteHiperDouble(Formatter output, double valores[][][][], String msg) {
        output.format("%s\n", msg);
        output.format("%s\n", toString(valores));
    }

    public static void WriteHiperDouble(Formatter output, Double valores[][][][], String msg) {
        output.format("%s\n", msg);
        output.format("%s\n", toString(valores));
    }

    public static double[][][][] ReadHiperDouble(Scanner input) {
        input.nextLine();
        return toHiperDouble(input.nextLine());
    }

    //----------------------METODOS STATICOS PRIVADOS----------------------------
    //----------------------------Int-------------------------------
    public static int[] toVectorInt(String line) {
        String[] sa = line.split(",");
        int[] result = new int[sa.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = Integer.parseInt(sa[i]);
        }
        return result;
    }

    private static int[][] toMatrixInt(String line) {
        String[] sa = line.split("/");
        int[][] result = new int[sa.length][];
        for (int i = 0; i < result.length; i++) {
            result[i] = toVectorInt(sa[i]);
        }
        return result;
    }

    private static int[][][] toCubeInt(String line) {
        String[] sa = line.split("[*]");
        int[][][] result = new int[sa.length][][];
        for (int i = 0; i < result.length; i++) {
            result[i] = toMatrixInt(sa[i]);
        }
        return result;
    }

    private static int[][][][] toHiperInt(String line) {
        String[] sa = line.split("[:]");
        int[][][][] result = new int[sa.length][][][];
        for (int i = 0; i < result.length; i++) {
            result[i] = toCubeInt(sa[i]);
        }
        return result;
    }

    //--------------------------float---------------------------
    private static float[] toVectorFloat(String line) {
        String[] sa = line.split(",");
        float[] result = new float[sa.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = Float.parseFloat(sa[i]);
        }
        return result;
    }

    private static float[][] toMatrixFloat(String line) {
        String[] sa = line.split("/");
        float[][] result = new float[sa.length][];
        for (int i = 0; i < result.length; i++) {
            result[i] = toVectorFloat(sa[i]);
        }
        return result;
    }

    private static float[][][] toCubeFloat(String line) {
        String[] sa = line.split("[*]");
        float[][][] result = new float[sa.length][][];
        for (int i = 0; i < result.length; i++) {
            result[i] = toMatrixFloat(sa[i]);
        }
        return result;
    }
    //--------------------------double---------------------------

    public static double[] toVectorDouble(String line) {
        String[] sa = line.split(",");
        double[] result = new double[sa.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = Double.parseDouble(sa[i]);
        }
        return result;
    }

    private static double[][] toMatrixDouble(String line) {
        String[] sa = line.split("/");
        double[][] result = new double[sa.length][];
        for (int i = 0; i < result.length; i++) {
            result[i] = toVectorDouble(sa[i]);
        }
        return result;
    }

    private static double[][][] toCubeDouble(String line) {
        String[] sa = line.split("[*]");
        double[][][] result = new double[sa.length][][];
        for (int i = 0; i < result.length; i++) {
            result[i] = toMatrixDouble(sa[i]);
        }
        return result;
    }

    private static double[][][][] toHiperDouble(String line) {
        String[] sa = line.split("[:]");
        double[][][][] result = new double[sa.length][][][];
        for (int i = 0; i < result.length; i++) {
            result[i] = toCubeDouble(sa[i]);
        }
        return result;
    }

    //----------------------------Bool-------------------------------
    private static String toString(boolean valor) {
        return "" + (valor ? 1 : 0);
    }

    private static String toString(boolean valores[]) {
        String Resp = "" + toString(valores[0]);
        for (int i = 1; i < valores.length; i++) {
            Resp += S1 + toString(valores[i]);
        }
        return Resp;
    }

    private static String toString(boolean valores[][]) {
        String Resp = "" + toString(valores[0]);
        for (int i = 1; i < valores.length; i++) {
            Resp += S2 + toString(valores[i]);
        }
        return Resp;
    }

    private static String toString(boolean valores[][][]) {
        String Resp = "" + toString(valores[0]);
        for (int i = 1; i < valores.length; i++) {
            Resp += S3 + toString(valores[i]);
        }
        return Resp;
    }
    //----------------------------Bool-------------------------------

    private static String toString(Boolean valor) {
        return "" + (valor ? 1 : 0);
    }

    private static String toString(Boolean valores[]) {
        String Resp = "" + toString(valores[0]);
        for (int i = 1; i < valores.length; i++) {
            Resp += S1 + toString(valores[i]);
        }
        return Resp;
    }

    private static String toString(Boolean valores[][]) {
        String Resp = "" + toString(valores[0]);
        for (int i = 1; i < valores.length; i++) {
            Resp += S2 + toString(valores[i]);
        }
        return Resp;
    }

    private static String toString(Boolean valores[][][]) {
        String Resp = "" + toString(valores[0]);
        for (int i = 1; i < valores.length; i++) {
            Resp += S3 + toString(valores[i]);
        }
        return Resp;
    }

    //----------------------------Int-------------------------------
    private static String toString(int valor) {
        return "" + valor;
    }

    private static String toString(int valores[]) {
        String Resp = "" + toString(valores[0]);
        for (int i = 1; i < valores.length; i++) {
            Resp += S1 + toString(valores[i]);
        }
        return Resp;
    }

    private static String toString(int valores[][]) {
        String Resp = "" + toString(valores[0]);
        for (int i = 1; i < valores.length; i++) {
            Resp += S2 + toString(valores[i]);
        }
        return Resp;
    }

    private static String toString(int valores[][][]) {
        String Resp = "" + toString(valores[0]);
        for (int i = 1; i < valores.length; i++) {
            Resp += S3 + toString(valores[i]);
        }
        return Resp;
    }

    private static String toString(Integer valor) {
        return "" + valor;
    }

    private static String toString(Integer valores[]) {
        String Resp = "" + toString(valores[0]);
        for (int i = 1; i < valores.length; i++) {
            Resp += S1 + toString(valores[i]);
        }
        return Resp;
    }

    private static String toString(Integer valores[][]) {
        String Resp = "" + toString(valores[0]);
        for (int i = 1; i < valores.length; i++) {
            Resp += S2 + toString(valores[i]);
        }
        return Resp;
    }

    private static String toString(Integer valores[][][]) {
        String Resp = "" + toString(valores[0]);
        for (int i = 1; i < valores.length; i++) {
            Resp += S3 + toString(valores[i]);
        }
        return Resp;
    }
    //---------------------------Float----------------------------

    private static String toString(float valor) {
        return "" + valor;
    }

    private static String toString(float valores[]) {
        String Resp = "" + toString(valores[0]);
        for (int i = 1; i < valores.length; i++) {
            Resp += S1 + toString(valores[i]);
        }
        return Resp;
    }

    private static String toString(float valores[][]) {
        String Resp = "" + toString(valores[0]);
        for (int i = 1; i < valores.length; i++) {
            Resp += S2 + toString(valores[i]);
        }
        return Resp;
    }

    private static String toString(float valores[][][]) {
        String Resp = "" + toString(valores[0]);
        for (int i = 1; i < valores.length; i++) {
            Resp += S3 + toString(valores[i]);
        }
        return Resp;
    }

    private static String toString(Float valor) {
        return "" + valor;
    }

    private static String toString(Float valores[]) {
        String Resp = "" + toString(valores[0]);
        for (int i = 1; i < valores.length; i++) {
            Resp += S1 + toString(valores[i]);
        }
        return Resp;
    }

    private static String toString(Float valores[][]) {
        String Resp = "" + toString(valores[0]);
        for (int i = 1; i < valores.length; i++) {
            Resp += S2 + toString(valores[i]);
        }
        return Resp;
    }

    private static String toString(Float valores[][][]) {
        String Resp = "" + toString(valores[0]);
        for (int i = 1; i < valores.length; i++) {
            Resp += S3 + toString(valores[i]);
        }
        return Resp;
    }
    //------------------------------double------------------------

    private static String toString(double valor) {
        return "" + valor;
    }

    private static String toString(double valores[]) {
        String Resp = "" + toString(valores[0]);
        for (int i = 1; i < valores.length; i++) {
            Resp += S1 + toString(valores[i]);
        }
        return Resp;
    }

    private static String toString(double valores[][]) {
        String Resp = "" + toString(valores[0]);
        for (int i = 1; i < valores.length; i++) {
            Resp += S2 + toString(valores[i]);
        }
        return Resp;
    }

    private static String toString(double valores[][][]) {
        String Resp = "" + toString(valores[0]);
        for (int i = 1; i < valores.length; i++) {
            Resp += S3 + toString(valores[i]);
        }
        return Resp;
    }

    private static String toString(double valores[][][][]) {
        String Resp = "" + toString(valores[0]);
        for (int i = 1; i < valores.length; i++) {
            Resp += ":" + toString(valores[i]);
        }
        return Resp;
    }

    private static String toString(Double valor) {
        return "" + valor;
    }

    private static String toString(Double valores[]) {
        String Resp = "" + toString(valores[0]);
        for (int i = 1; i < valores.length; i++) {
            Resp += S1 + toString(valores[i]);
        }
        return Resp;
    }

    private static String toString(Double valores[][]) {
        String Resp = "" + toString(valores[0]);
        for (int i = 1; i < valores.length; i++) {
            Resp += S2 + toString(valores[i]);
        }
        return Resp;
    }

    private static String toString(Double valores[][][]) {
        String Resp = "" + toString(valores[0]);
        for (int i = 1; i < valores.length; i++) {
            Resp += S3 + toString(valores[i]);
        }
        return Resp;
    }

    private static String toString(Double valores[][][][]) {
        String Resp = "" + toString(valores[0]);
        for (int i = 1; i < valores.length; i++) {
            Resp += ":" + toString(valores[i]);
        }
        return Resp;
    }

    //----------------------Formato GAMS----------------------
    public static void WriteAllGams(Formatter output, Object... obj) throws FileNotFoundException, Exception {
        for (int i = 0; i < obj.length; i++) {
            if (obj[i] == null) {
                output.format("%s\n", "//null");
            } else if (obj[i] instanceof String) {
                String cmd = (String) obj[i];
                if (cmd.substring(0, 3).contains("Set")) {
                    String S[] = cmd.split(":");
                    int vo = (Integer) obj[++i];
                    int vf = (Integer) obj[++i];
                    WriteSetGams(output, S[0], S[1], vo, vf);
                } else if (cmd.substring(0, 6).contains("Scalar")) {
                    if (obj[i + 1] instanceof Integer) {
                        output.format("%s\n\t/%d/;\n", cmd, (Integer) obj[++i]);
                    } else if (obj[i + 1] instanceof Double) {
                        output.format("%s\n\t/%g/;\n", cmd, (Double) obj[++i]);
                    } else {
                        throw new Exception("int or double espected but (" + obj[i + 1] + ") found");
                    }
                } else if (cmd.substring(0, 9).contains("Parameter")) {
                    String S[] = cmd.split(":");
                    if (obj[i + 1] instanceof int[]) {
                        WriteParameterIntGams(output, (int[]) obj[++i], S[0], S[1]);
                    } else if (obj[i + 1] instanceof double[]) {
                        WriteParameterDoubleGams(output, (double[]) obj[++i], S[0], S[1]);
                    } else {
                        throw new Exception("int[] or double[] espected but (" + obj[i + 1] + ") found");
                    }
                } else if (cmd.substring(0, 5).contains("Table")) {
                    String S[] = cmd.split(":");
                    if (obj[i + 1] instanceof int[][]) {
                        WriteTableIntGams(output, (int[][]) obj[++i], S[0], S[1], S[2]);
                    } else if (obj[i + 1] instanceof double[][]) {
                        WriteTableDoubleGams(output, (double[][]) obj[++i], S[0], S[1], S[2]);
                    } else if (obj[i + 1] instanceof int[][][]) {
                        WriteTableIntGams(output, (int[][][]) obj[++i], S[0], S[1], S[2], S[3]);
                    } else if (obj[i + 1] instanceof double[][][]) {
                        WriteTableDoubleGams(output, (double[][][]) obj[++i], S[0], S[1], S[2], S[3]);
                    } else if (obj[i + 1] instanceof double[][][][]) {
                        WriteTableDoubleGams(output, (double[][][][]) obj[++i], S[0], S[1], S[2], S[3], S[4]);
                    } else {
                        throw new Exception("int[][] or double[][] or int[][][] or double[][][] or double [][][][] espected but (" + obj[i + 1] + ") found");
                    }
                } else {
                    throw new Exception("String (" + cmd + ") not known");
                }
            } else {
                throw new Exception("Object (" + obj[i] + ") not known");
            }
        }
    }

    public static void WriteSetGams(Formatter output, String titulo, String Indice, int vo, int vf) {
        output.format("%s\n", titulo);
        if (vf > vo) {
            output.format("\t/ %s%d * %s%d /;\n", Indice, vo, Indice, vf);
        } else {
            output.format("\t/ %s%d /;\n", Indice, vo);
        }
    }

    public static void WriteParameterDoubleGams(Formatter output, double valores[], String titulo, String indide) {
        output.format("%s\n", titulo);
        if (valores.length == 1) {
            output.format("\t/ %s%d %s /;\n", indide, 1, valores[0]);
        } else {
            output.format("\t/ ");
            for (int i = 0; i < valores.length; i++) {
                if (i == 0) {
                    output.format("%s%d %s\n", indide, i + 1, valores[i]);
                } else if (i == valores.length - 1) {
                    output.format("\t  %s%d %s", indide, i + 1, valores[i]);
                } else {
                    output.format("\t  %s%d %s\n", indide, i + 1, valores[i]);
                }
            }
            output.format(" /;\n");
        }
    }

    public static void WriteTableDoubleGams(Formatter output, double valores[][], String titulo, String I, String J) {
        int size_max = 0;
        for (int i = 0; i < valores.length; i++) {
            for (int j = 0; j < valores[0].length; j++) {
                String temp = String.valueOf(valores[i][j]);
                if (temp.length() > size_max) {
                    size_max = temp.length();
                }
            }
        }
        for (int j = 0; j < valores[0].length; j++) {
            String temp = String.format("%s%d", J, j + 1);
            if (temp.length() > size_max) {
                size_max = temp.length();
            }
        }
        final int size = size_max > 12 ? 12 : size_max;

        output.format("%s\n", titulo);
        output.format("\t\t\t");
        for (int j = 0; j < valores[0].length; j++) {
            String s = String.format("%s%d", J, j + 1);
            while (s.length() < size) {
                s += " ";
            }
            output.format("%s ", s);
        }
        output.format("\n");
        for (int i = 0; i < valores.length; i++) {
            output.format("\t\t%s%d\t", I, i + 1);
            for (int j = 0; j < valores[0].length; j++) {
                String temp = ToString(size, valores[i][j]);
                output.format("%s ", temp);
            }
            if (i == valores.length - 1) {
                output.format(";\n");
            } else {
                output.format("\n");
            }
        }
    }

    public static void WriteTableDoubleGams(Formatter output, double valores[][][], String titulo, String I, String J, String K) {
        int size_max = 0;
        for (int i = 0; i < valores.length; i++) {
            for (int j = 0; j < valores[0].length; j++) {
                for (int k = 0; k < valores[0][0].length; k++) {
                    String temp = String.valueOf(valores[i][j][k]);
                    if (temp.length() > size_max) {
                        size_max = temp.length();
                    }
                }
            }
        }
        for (int k = 0; k < valores[0][0].length; k++) {
            String temp = String.format("%s%d", K, k + 1);
            if (temp.length() > size_max) {
                size_max = temp.length();
            }
        }
        final int size = size_max > 12 ? 12 : size_max;

        output.format("%s\n", titulo);
        output.format("\t\t\t");
        for (int k = 0; k < valores[0][0].length; k++) {
            String s = String.format("%s%d", K, k + 1);
            while (s.length() < size) {
                s += " ";
            }
            output.format("%s ", s);
        }
        output.format("\n");
        for (int i = 0; i < valores.length; i++) {
            for (int j = 0; j < valores[0].length; j++) {
                output.format("\t\t%s%d.%s%d\t", I, i + 1, J, j + 1);
                for (int k = 0; k < valores[0][0].length; k++) {
                    String temp = ToString(size, valores[i][j][k]);
                    output.format("%s ", temp);
                }
                if (i == valores.length - 1 && j == valores[0].length - 1) {
                    output.format(";\n");
                } else {
                    output.format("\n");
                }
            }
        }
    }

    public static void WriteTableDoubleGams(Formatter output, double valores[][][][], String titulo, String I, String J, String K, String T) {
        int size_max = 0;
        for (int i = 0; i < valores.length; i++) {
            for (int j = 0; j < valores[0].length; j++) {
                for (int k = 0; k < valores[0][0].length; k++) {
                    for (int t = 0; t < valores[0][0][0].length; t++) {
                        String temp = String.valueOf(valores[i][j][k][t]);
                        if (temp.length() > size_max) {
                            size_max = temp.length();
                        }
                    }
                }
            }
        }
        for (int t = 0; t < valores[0][0][0].length; t++) {
            String temp = String.format("%s%d", T, t + 1);
            if (temp.length() > size_max) {
                size_max = temp.length();
            }
        }
        final int size = size_max > 12 ? 12 : size_max;

        output.format("%s\n", titulo);
        output.format("\t%11s\t", "");
        for (int t = 0; t < valores[0][0][0].length; t++) {
            String s = String.format("%s%d", T, t + 1);
            while (s.length() < size) {
                s += " ";
            }
            output.format("%s ", s);
        }
        output.format("\n");
        for (int i = 0; i < valores.length; i++) {
            for (int j = 0; j < valores[0].length; j++) {
                for (int k = 0; k < valores[0][0].length; k++) {
                    String s = String.format("%s%d.%s%d.%s%d", I, i + 1, J, j + 1, K, k + 1);
                    output.format("\t%11s\t", s);
                    for (int t = 0; t < valores[0][0][0].length; t++) {
                        String temp = ToString(size, valores[i][j][k][t]);
                        output.format("%s ", temp);
                    }
                    if (i == valores.length - 1 && j == valores[0].length - 1 && k == valores[0][0].length - 1) {
                        output.format(";\n");
                    } else {
                        output.format("\n");
                    }
                }
            }
        }
    }

    public static String ToString(int size, double valor) {
        String temp = String.valueOf(valor);
        if (temp.length() > size) {
            temp = temp.substring(0, size);
        } else {
            while (temp.length() < size) {
                temp += " ";
            }
        }
        return temp;
    }

    public static void WriteParameterIntGams(Formatter output, int valores[], String titulo, String indide) {
        output.format("%s\n", titulo);
        if (valores.length == 1) {
            output.format("\t/ %s%d %s /;\n", indide, 1, valores[0]);
        } else {
            output.format("\t/ ");
            for (int i = 0; i < valores.length; i++) {
                if (i == 0) {
                    output.format("%s%d %s\n", indide, i + 1, valores[i]);
                } else if (i == valores.length - 1) {
                    output.format("\t  %s%d %s", indide, i + 1, valores[i]);
                } else {
                    output.format("\t  %s%d %s\n", indide, i + 1, valores[i]);
                }
            }
            output.format(" /;\n");
        }
    }

    public static void WriteTableIntGams(Formatter output, int valores[][], String titulo, String I, String J) {
        int size_max = 0;
        for (int i = 0; i < valores.length; i++) {
            for (int j = 0; j < valores[0].length; j++) {
                String temp = String.valueOf(valores[i][j]);
                if (temp.length() > size_max) {
                    size_max = temp.length();
                }
            }
        }
        for (int j = 0; j < valores[0].length; j++) {
            String temp = String.format("%s%d", J, j + 1);
            if (temp.length() > size_max) {
                size_max = temp.length();
            }
        }
        final int size = size_max > 12 ? 12 : size_max;

        output.format("%s\n", titulo);
        output.format("\t\t\t");
        for (int j = 0; j < valores[0].length; j++) {
            String s = String.format("%s%d", J, j + 1);
            while (s.length() < size) {
                s += " ";
            }
            output.format("%s ", s);
        }
        output.format("\n");
        for (int i = 0; i < valores.length; i++) {
            output.format("\t\t%s%d\t", I, i + 1);
            for (int j = 0; j < valores[0].length; j++) {
                String temp = ToString(size, valores[i][j]);
                output.format("%s ", temp);
            }
            if (i == valores.length - 1) {
                output.format(";\n");
            } else {
                output.format("\n");
            }
        }
    }

    public static void WriteTableIntGams(Formatter output, int valores[][][], String titulo, String I, String J, String K) {
        int size_max = 0;
        for (int i = 0; i < valores.length; i++) {
            for (int j = 0; j < valores[0].length; j++) {
                for (int k = 0; k < valores[0][0].length; k++) {
                    String temp = String.valueOf(valores[i][j][k]);
                    if (temp.length() > size_max) {
                        size_max = temp.length();
                    }
                }
            }
        }
        for (int k = 0; k < valores[0][0].length; k++) {
            String temp = String.format("%s%d", K, k + 1);
            if (temp.length() > size_max) {
                size_max = temp.length();
            }
        }
        final int size = size_max > 12 ? 12 : size_max;

        output.format("%s\n", titulo);
        output.format("\t\t\t");
        for (int k = 0; k < valores[0][0].length; k++) {
            String s = String.format("%s%d", K, k + 1);
            while (s.length() < size) {
                s += " ";
            }
            output.format("%s ", s);
        }
        output.format("\n");
        for (int i = 0; i < valores.length; i++) {
            for (int j = 0; j < valores[0].length; j++) {
                output.format("\t\t%s%d.%s%d\t", I, i + 1, J, j + 1);
                for (int k = 0; k < valores[0][0].length; k++) {
                    String temp = ToString(size, valores[i][j][k]);
                    output.format("%s ", temp);
                }
                if (i == valores.length - 1 && j == valores[0].length - 1) {
                    output.format(";\n");
                } else {
                    output.format("\n");
                }
            }
        }
    }

    //============================= MODELO OPL ===================================
    public static void WriteAllOpl(Formatter output, Object... obj) throws FileNotFoundException, Exception {
        for (int i = 0; i < obj.length; i++) {
            if (obj[i] == null) {
                //output.format("%s\n", "<null>");
                //output.format("%s\n", o);
            } else if (obj[i] instanceof String) {
                String cmd = (String) obj[i];
                Object o = obj[++i];
                if (o instanceof Integer) {
                    output.format("%s%s;\n", cmd, (Integer) o);
                } else if (obj[i] instanceof Double) {
                    output.format("%s%s;\n", cmd, (Double) o);
                } else if (o instanceof int[]) {
                    output.format("%s%s;\n", cmd, toStringOpl((int[]) o));
                } else if (o instanceof double[]) {
                    output.format("%s%s;\n", cmd, toStringOpl((double[]) o));
                } else if (o instanceof int[][]) {
                    output.format("%s%s;\n", cmd, toStringOpl((int[][]) o));
                } else if (o instanceof double[][]) {
                    output.format("%s%s;\n", cmd, toStringOpl((double[][]) o));
                } else if (o instanceof int[][][]) {
                    output.format("%s%s;\n", cmd, toStringOpl((int[][][]) o));
                } else if (o instanceof double[][][]) {
                    output.format("%s%s;\n", cmd, toStringOpl((double[][][]) o));
                } else if (o instanceof int[][][][]) {
                    output.format("%s%s;\n", cmd, toStringOpl((int[][][][]) o));
                } else if (o instanceof double[][][][]) {
                    output.format("%s%s;\n", cmd, toStringOpl((double[][][][]) o));
                } else {
                    throw new Exception("Object not known");
                }
            } else {
                throw new Exception("Object not known");
            }
        }
    }

    private static String nextCMD2(Scanner input) {
        String S = "";
        while (input.hasNextLine() && !S.contains(";")) {
            S = S + input.nextLine();
        }
        //System.out.println(S);
        return S;
    }

    public static void Ignore(Scanner input) {
        String S = nextCMD2(input);
    }

    public static int ReadIntOpl(Scanner input) {
        String S = nextCMD2(input);
        S = S.split("=")[1];
        S = S.replace(";", "");
        S = S.replace(" ", "");
        int v = Integer.parseInt(S);
        return v;
    }

    public static double ReadDoubleOpl(Scanner input) {
        String S = nextCMD2(input);
        S = S.split("=")[1];
        S = S.replaceFirst(";", "");
        S = S.replace(" ", "");
        double v = Double.parseDouble(S);
        return v;
    }

    public static double[] ReadVectorDoubleOpl(Scanner input) {
        String S = nextCMD2(input);

        S = S.split(" = ")[1];
        S = S.replaceFirst(";", "");
        return ReadVectorDoubleOpl(S);
    }

    public static int[] ReadVectorIntOpl(Scanner input) {
        String S = nextCMD2(input);

        S = S.split(" = ")[1];
        S = S.replaceFirst(";", "");
        return ReadVectorIntOpl(S);
    }

    public static double[][] ReadMatrixDoubleOpl(Scanner input) {
        String S = nextCMD2(input);
        S = S.split(" = ")[1];
        S = S.replaceFirst(";", "");
        return ReadMatrixDoubleOpl(S);
    }

    public static int[][] ReadMatrixIntOpl(Scanner input) {
        String S = nextCMD2(input);
        S = S.split(" = ")[1];
        S = S.replaceFirst(";", "");
        return ReadMatrixIntOpl(S);
    }

    public static double[][][] ReadCubeDoubleOpl(Scanner input) {
        String S = nextCMD2(input);
        S = S.split(" = ")[1];
        S = S.replaceFirst(";", "");
        return ReadCubeDoubleOpl(S);
    }

    public static int[][][] ReadCubeIntOpl(Scanner input) {
        String S = nextCMD2(input);
        S = S.split(" = ")[1];
        S = S.replaceFirst(";", "");
        return ReadCubeIntOpl(S);
    }

    public static double[][][][] ReadHiperDoubleOpl(Scanner input) {
        String S = nextCMD2(input);
        S = S.split(" = ")[1];
        S = S.replaceFirst(";", "");
        return ReadHiperDoubleOpl(S);
    }

    public static int[][][][] ReadHiperIntOpl(Scanner input) {
        String S = nextCMD2(input);
        S = S.split(" = ")[1];
        S = S.replaceFirst(";", "");
        return ReadHiperIntOpl(S);
    }

    private static String replaceVectorOpl(String S) {
        S = S.replace("\t", "");
        S = S.replace("[ ", "[");
        S = S.replace(" [", "[");
        S = S.replace("] ", "]");
        S = S.replace(" ]", "]");
        S = S.replace(", ", ",");
        S = S.replace(" ,", ",");

        S = S.replace("[", "");
        S = S.replace("]", "");

        S = S.replace(" ", ",");
        return S;
    }

    /**
     * [ num1 num2 num3]
     */
    private static double[] ReadVectorDoubleOpl(String S) {
        S = replaceVectorOpl(S);
        return toVectorDouble(S);
    }

    private static int[] ReadVectorIntOpl(String S) {
        S = replaceVectorOpl(S);
        return toVectorInt(S);
    }

    private static String replaceMatrixOpl(String S) {
        S = S.replace("\t", "");
        S = S.replace("[ ", "[");
        S = S.replace(" [", "[");
        S = S.replace("] ", "]");
        S = S.replace(" ]", "]");
        S = S.replace(", ", ",");
        S = S.replace(" ,", ",");

        S = S.replace("[[", "");
        S = S.replace("]]", "");

        S = S.replace("], [", "/");
        S = S.replace("] [", "/");
        S = S.replace("],[", "/");
        S = S.replace("][", "/");

        S = S.replace(" ", ",");
        return S;
    }

    private static double[][] ReadMatrixDoubleOpl(String S) {
        S = replaceMatrixOpl(S);
        return toMatrixDouble(S);
    }

    private static int[][] ReadMatrixIntOpl(String S) {
        S = replaceMatrixOpl(S);
        return toMatrixInt(S);
    }

    private static String replaceCubeOpl(String S) {
        S = S.replace("\t", "");
        S = S.replace("[ ", "[");
        S = S.replace(" [", "[");
        S = S.replace("] ", "]");
        S = S.replace(" ]", "]");
        S = S.replace(", ", ",");
        S = S.replace(" ,", ",");

        S = S.replace("[[[", "");
        S = S.replace("]]]", "");

        S = S.replace("]], [[", "*");
        S = S.replace("]] [[", "*");
        S = S.replace("]],[[", "*");
        S = S.replace("]][[", "*");

        S = S.replace("] [", "/");
        S = S.replace("],[", "/");
        S = S.replace("][", "/");

        S = S.replace(" ", ",");
        return S;
    }

    private static double[][][] ReadCubeDoubleOpl(String S) {
        S = replaceCubeOpl(S);
        return toCubeDouble(S);
    }

    private static int[][][] ReadCubeIntOpl(String S) {
        S = replaceCubeOpl(S);
        return toCubeInt(S);
    }

    private static String replaceHiperOpl(String S) {
        S = S.replace("\t", "");
        S = S.replace("[ ", "[");
        S = S.replace(" [", "[");
        S = S.replace("] ", "]");
        S = S.replace(" ]", "]");
        S = S.replace(", ", ",");
        S = S.replace(" ,", ",");

        S = S.replace("[[[[", "");
        S = S.replace("]]]]", "");

        S = S.replace("]]], [[[", ":");
        S = S.replace("]]] [[[", ":");
        S = S.replace("]]],[[[", ":");
        S = S.replace("]]][[[", ":");

        S = S.replace("]], [[", "*");
        S = S.replace("]] [[", "*");
        S = S.replace("]],[[", "*");
        S = S.replace("]][[", "*");

        S = S.replace("] [", "/");
        S = S.replace("],[", "/");
        S = S.replace("][", "/");

        S = S.replace(" ", ",");
        return S;
    }

    private static double[][][][] ReadHiperDoubleOpl(String S) {
        S = replaceHiperOpl(S);
        return toHiperDouble(S);
    }

    private static int[][][][] ReadHiperIntOpl(String S) {
        S = replaceHiperOpl(S);
        return toHiperInt(S);
    }

    public static void WriteIntOpl(Formatter output, int num, String name) {
        output.format("%s = %d;\n", name, num);
    }

    public static void WriteDoubleOpl(Formatter output, double num, String name) {
        output.format("%s = %s;\n", name, num);
    }

    public static void WriteVectorDoubleOpl(Formatter output, double[] M, String name) {
        output.format("%s = %s;\n", name, toStringOpl(M));
    }

    public static void WriteMatrixDoubleOpl(Formatter output, double[][] M, String name) {
        output.format("%s = %s;\n", name, toStringOpl(M));
    }

    public static void WriteCubeDoubleOpl(Formatter output, double[][][] M, String name) {
        output.format("%s = %s;\n", name, toStringOpl(M));
    }

    public static void WriteVectorIntOpl(Formatter output, int[] M, String name) {
        output.format("%s = %s;\n", name, toStringOpl(M));
    }

    public static void WriteMatrixIntOpl(Formatter output, int[][] M, String name) {
        output.format("%s = %s;\n", name, toStringOpl(M));
    }

    public static void WriteCubeIntOpl(Formatter output, int[][][] M, String name) {
        output.format("%s = %s;\n", name, toStringOpl(M));
    }

    private static String toStringOpl(double[] M) {
        String S = "[" + M[0];
        for (int i = 1; i < M.length; i++) {
            S += " " + M[i];
        }
        return S + "]";
    }

    private static String toStringOpl(double[][] M) {
        String S = "[" + toStringOpl(M[0]);
        for (int i = 1; i < M.length; i++) {
            S += "," + toStringOpl(M[i]);
        }
        return S + "]";
    }

    private static String toStringOpl(double[][][] M) {
        String S = "[" + toStringOpl(M[0]);
        for (int i = 1; i < M.length; i++) {
            S += "\n" + toStringOpl(M[i]);
        }
        return S + "]";
    }

    private static String toStringOpl(double[][][][] M) {
        String S = "[" + toStringOpl(M[0]);
        for (int i = 1; i < M.length; i++) {
            S += "\n" + toStringOpl(M[i]);
        }
        return S + "]";
    }

    private static String toStringOpl(int[] M) {
        String S = "[" + M[0];
        for (int i = 1; i < M.length; i++) {
            S += " " + M[i];
        }
        return S + "]";
    }

    private static String toStringOpl(int[][] M) {
        String S = "[" + toStringOpl(M[0]);
        for (int i = 1; i < M.length; i++) {
            S += "," + toStringOpl(M[i]);
        }
        return S + "]";
    }

    private static String toStringOpl(int[][][] M) {
        String S = "[" + toStringOpl(M[0]);
        for (int i = 1; i < M.length; i++) {
            S += "\n" + toStringOpl(M[i]);
        }
        return S + "]";
    }

    private static String toStringOpl(int[][][][] M) {
        String S = "[" + toStringOpl(M[0]);
        for (int i = 1; i < M.length; i++) {
            S += "\n" + toStringOpl(M[i]);
        }
        return S + "]";
    }

    //============================= MODELO AMPL ===================================
    public static void WriteIntSetAmpl(Formatter output, String name, int min, int max) {
        output.format("set %s:=", name);
        for (int i = min; i <= max; i++) {
            output.format(" %d", i);
        }
        output.format(";\n");
    }

    public static void WriteIntAmpl(Formatter output, String name, int val) {
        output.format("param %s:= %d;\n", name, val);
    }

    public static void WriteDoubleAmpl(Formatter output, String name, double val) {
        output.format(Locale.US, "param %s:= %g;\n", name, val);
    }

    public static void WriteIntVectorAmpl(Formatter output, String name, int val[]) {
        output.format("param %s:=", name);
        for (int i = 0; i < val.length; i++) {
            output.format("\n%d\t%d", i + 1, val[i]);
        }
        output.format(";\n");
    }

    public static void WriteDoubleVectorAmpl(Formatter output, String name, double val[]) {
        output.format("param %s:=", name);
        for (int i = 0; i < val.length; i++) {
            output.format(Locale.US, "\n%d\t%g", i + 1, val[i]);
        }
        output.format(";\n");
    }

    public static void WriteIntMatrixAmpl(Formatter output, String name, int val[][]) {
        output.format("param %s:", name);
        for (int i = 1; i <= val.length; i++) {
            output.format("\t%d", i);
        }
        output.format(":=");
        for (int i = 0; i < val.length; i++) {
            output.format("\n%d", i + 1);
            for (int j = 0; j < val[i].length; j++) {
                output.format("\t%d", val[i][j]);
            }
        }
        output.format(";\n");
    }

    public static void WriteDoubleMatrixAmpl(Formatter output, String name, double val[][]) {
        output.format("param %s:", name);
        for (int i = 1; i <= val.length; i++) {
            output.format("\t%d", i);
        }
        output.format(":=");
        for (int i = 0; i < val.length; i++) {
            output.format("\n%d", i + 1);
            for (int j = 0; j < val[i].length; j++) {
                output.format(Locale.US, "\t%g", val[i][j]);
            }
        }
        output.format(";\n");
    }

    //===============================Outros Metodos====================
    public static double[][] Transpose(double A[][]) {
        double B[][] = new double[A[0].length][A.length];
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[i].length; j++) {
                B[j][i] = A[i][j];
            }
        }
        return B;
    }
}
