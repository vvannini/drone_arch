/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.utilities;

import java.io.PrintStream;

/**
 *
 * @author marcio
 */
public class uUtil {
    public static void Print(PrintStream out, String format, String name, int length){
        out.printf(format, String.format(name+"(%d)", 0));
        for(int i=1; i<length; i++){
            out.printf(" "+format, String.format(name+"(%d)", i));
        }
    }
    public static void Print(PrintStream out, String format, String ...names){
        out.printf(format, names[0]);
        for(int i=1; i<names.length; i++){
            out.printf(" "+format, names[i]);
        }
    }
    public static void Print(PrintStream out, String format, double ...values){
        out.printf(format, values[0]);
        for(int i=1; i<values.length; i++){
            out.printf(" "+format, values[i]);
        }
    }
    public static void Print(PrintStream out, String format, int ...values){
        out.printf(format, values[0]);
        for(int i=1; i<values.length; i++){
            out.printf(" "+format, values[i]);
        }
    }
    public static double decode(long mask[], int bit, double A, double B){
        return (A-B) * (byte)(-((mask[bit/64]<<(bit%64))>>63)) + B;
    }
    public static boolean decode(long mask[], int bit){
        return -((mask[bit/64]<<(bit%64))>>63)==1;
    }
    public static boolean decode(long val, int bit){
        return -((val<<bit)>>63)==1;
    }
    public static boolean decode(int val, int bit){
        return -((val<<bit)>>31)==1;
    }
    public static int decodeInt(long mask[], int bit){
        //return uUtil.decode(mask[bit/64], bit%64);
        return (int) -((mask[bit/64]<<(bit%64))>>63);
    }
    public static int decodeInt(long val, int bit){
        return (int) -((val<<bit)>>63);
    }
    public static int decodeInt(int val, int bit){
        return -((val<<bit)>>31);
    }
    public static int max(int a, int b){
        return  a>b ? a : b;
    }
    public static void Swap(Object[] objs, int a, int b) {
        Object aux = objs[a];
        objs[a] = objs[b];
        objs[b] = aux;
    }
    public static double sum(double ...weigth) {
        double sum = 0;
        for(double w : weigth){
            sum += w;
        }
        return sum;
    }
    public static boolean find(int key, int ...vet){
        for(int v : vet){
            if(v==key){
                return true;
            }
        }
        return false;
    }
    public static int min(Comparable[] array) {
        int min=0;
        for(int i=1; i<array.length; i++){
            if(array[i].compareTo(array[min])<0){
                min = i;
            }
        }
        return min;
    }
    public static int max(Comparable[] array) {
        int max=0;
        for(int i=1; i<array.length; i++){
            if(array[i].compareTo(array[max])>0){
                max = i; 
            }
        }
        return max;
    }
    public static int minInt(int ...array) {
        return array[indexMin(array)];
    }
    public static int maxInt(int ...array) {
        return array[indexMax(array)];
    }
    public static double minDbl(double ...array) {
        return array[indexMin(array)];
    }
    public static double maxDbl(double ...array) {
        return array[indexMax(array)];
    }
    public static int indexMin(int []array, int ...not) {
        int min;
        if(not==null || not.length==0){
            min = 0;
            for(int i=1; i<array.length; i++){
                if(array[i]<array[min]){
                    min = i;
                }
            }
        }else{
            min = -1;
            int i=0;
            while(i<array.length){
                if(!find(i, not)){
                    min = i;
                }
                i++;
            }
            while(i<array.length){
                if(array[i]<array[min] && !find(i, not)){
                    min = i;
                }
                i++;
            }
        }
        return min;
    }
    public static int indexMax(int []array, int ...not) {
        int max;
        if(not==null || not.length==0){
            max = 0;
            for(int i=1; i<array.length; i++){
                if(array[i]>array[max]){
                    max = i;
                }
            }
        }else{
            max = -1;
            int i=0;
            while(i<array.length){
                if(!find(i, not)){
                    max = i;
                }
                i++;
            }
            while(i<array.length){
                if(array[i]>array[max] && !find(i, not)){
                    max = i;
                }
                i++;
            }
        }
        return max;
    }
    public static int indexMin(double []array, int ...not) {
        int min;
        if(not==null || not.length==0){
            min = 0;
            for(int i=1; i<array.length; i++){
                if(array[i]<array[min]){
                    min = i;
                }
            }
        }else{
            min = -1;
            int i=0;
            while(i<array.length){
                if(!find(i, not)){
                    min = i;
                }
                i++;
            }
            while(i<array.length){
                if(array[i]<array[min] && !find(i, not)){
                    min = i;
                }
                i++;
            }
        }
        return min;
    }
    public static int indexMax(double []array, int ...not) {
        int max;
        if(not==null || not.length==0){
            max = 0;
            for(int i=1; i<array.length; i++){
                if(array[i]>array[max]){
                    max = i;
                }
            }
        }else{
            max = -1;
            int i=0;
            while(i<array.length){
                if(!find(i, not)){
                    max = i;
                }
                i++;
            }
            while(i<array.length){
                if(array[i]>array[max] && !find(i, not)){
                    max = i;
                }
                i++;
            }
        }
        return max;
    }
    public static int indexMax(double[] array, boolean[] lotes) {
        int i = 0;
        while(i<array.length && lotes[i]){ 
            i++;
        }
        for(int j = i+1; j<array.length; j++){
            if(!lotes[j] && array[j] > array[i]){
                i = j;
            }
        }
        return i;
    }

    public static double bound(double val, double min, double max) {
        return  val<max ?
                val<min ? min: val :
                max
                ;
    }

    public static double[] decode(double X[], double min[], double max[]){
        double x[] = new double[X.length];
        for(int i=0; i<X.length; i++){
            x[i] = decode(X[i], min[i], max[i]);
        }
        return x;
    }
    public static double[] decode(double X[], double min, double max){
        double x[] = new double[X.length];
        for(int i=0; i<X.length; i++){
            x[i] = decode(X[i], min, max);
        }
        return x;
    }
    public static double decode(double x, double min, double max){
        return min + x*(max-min);
    }
    public static double LE(double a, double b){
        return Math.max(a-b, 0);
    }
    public static double GE(double a, double b){
        return Math.max(b-a,0);
    }

    public static double dist(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1-x2,2) + Math.pow(y1-y2,2));
    }
    public static double SQN2(double x1, double y1, double x2, double y2) {
        return Math.pow(x1-x2,2) + Math.pow(y1-y2,2);
    }

    /**
     * Quadrado da norma-2
     * @param U
     * @return norm-2|U|^2 = sum {u^2 | u in U}
     */
    public static double SQN2(double[] U) {
        double sum = 0;
        for(double u: U){
            sum += u*u;
        }
        return sum;
    }
}
