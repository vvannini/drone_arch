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
public final class uWarnning{
    
    private static PrintStream output = System.out;

    private uWarnning() {}
    
    public static void start(PrintStream out) {
        output = out;
    }

    
    public static void format(String format, Object... args) {
        if(output!=null){
            output.format(format, args);
        }
    }
    public static void printf(String format, Object... args) {
        if(output!=null){
            output.printf(format, args);
        }
    }
    public static void print(Object obj) {
        if(output!=null){
            output.print(obj);
        }
    }
    public static void print(String s) {
        if(output!=null){
            output.print(s);
        }
    }
    public static void println() {
        if(output!=null){
            output.println();
        }
    }
    public static void println(Object x) {
        if(output!=null){
            output.println(x);
        }
    }
    public static void println(String x) {
        if(output!=null){
            output.println(x);
        }
    }
}
