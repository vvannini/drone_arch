/*
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.com;

import ProOF.com.Stream.StreamPlot2D;
import ProOF.com.Stream.StreamProgress;
import ProOF.com.Stream.StreamAdapter;
import ProOF.com.Stream.StreamPrinter;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Stream.StreamOutput;
import ProOF.com.runner.Runner;
import java.awt.Color;
import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;

/**
 *
 * @author marcio
 */
public class Communication {
    public static final String RESULTS = "results";
    
    public static final String PROGRESS = "progress";
    public static final String STREAM = "stream";
    public static final String WRITE = "write";
    public static final String PLOT2D = "plot2d";
    public static final String OUTPUT = "output";
    
    private static LinkedList<String> names = new LinkedList<String>();
    private static LinkerResults results = null;

    
    private Communication() {
    }
    public static void register_name(String name) throws Exception{
        if(names.isEmpty()){//reserved keys
            names.addLast("open");
            names.addLast("close");
            names.addLast("output");
            names.addLast("error");
        }
        if(names.contains(name)){
            throw new Exception(String.format("repeat name = '%s' in %s", name, names));
        }
        names.addLast(name);
    }
    public static StreamPrinter mkPrinter(String mk_name) throws Exception {
        register_name(mk_name);
        return new Printer(STREAM, mk_name); 
    }
    public static StreamOutput mkOutput(String mk_name) throws Exception {
        register_name(mk_name);
        return new Output(OUTPUT, mk_name); 
    }
    public static StreamProgress mkProgress(final String mk_name) throws Exception {
        register_name(mk_name);
        return new Progress(mk_name);
    }
    public static StreamPlot2D mkPlot2D(final String mk_name) throws Exception {
        register_name(mk_name);
        return new Plot2D(mk_name);
    }
    public static LinkerResults restuls() throws Exception {
        if(results==null){
            register_name(RESULTS);
            results = new Results();
        } 
        return results; 
    }
    private static final class Progress extends StreamAdapter implements StreamProgress{
        private double value = -1;
        public Progress(String mk_name) throws Exception {
            super(PROGRESS, mk_name);
        }
        @Override
        public void progress(double value) {
            if(Math.abs(value-this.value)>0.01){
                this.value = value;
                if(!Runner.LOCAL) System.out.printf(Locale.ENGLISH,"#%s$%s\n", mk_name, value);
            }
        }
    }
    
    private static final class Results extends StreamAdapter implements LinkerResults{
        public Results() throws Exception {
            super(WRITE, RESULTS);
        }
        public void writeObj(String type, String name, Object value) throws Exception{
            System.out.printf(Locale.ENGLISH,"#%s$%s$%s$%s\n", RESULTS, type, name, value);
        }
        @Override
        public void writeInt(String name, int value) throws Exception {
            writeObj("int", name, value);
        }
        @Override
        public void writeLong(String name, long value) throws Exception {
            writeObj("long", name, value);
        }
        @Override
        public void writeDbl(String name, double value) throws Exception {
            writeObj("dbl", name, value);
        }
        @Override
        public void writeString(String name, String value) throws Exception {
            writeObj("str", name, value);
        }
        @Override
        public void writeFile(String name, File file) throws Exception {
            writeObj("file", name, file.getCanonicalPath());
        }
        @Override
        public void writeArray(String file, String tag, Collection c) {
            System.out.printf(Locale.ENGLISH,"#%s$%s$%s$%s$", RESULTS, "array", file, tag);
            for(Object a : c){
                System.out.printf(Locale.ENGLISH,"%s;", a);
            }
            System.out.println();
        }
        @Override
        public void writeArray(String file, String tag, Object[] array) {
            System.out.printf(Locale.ENGLISH,"#%s$%s$%s$%s$", RESULTS, "array", file, tag);
            for(Object a : array){
                System.out.printf(Locale.ENGLISH,"%s;", a);
            }
            System.out.println();
        }
        @Override
        public void writeArray(String file, String tag, int[] array) {
            System.out.printf(Locale.ENGLISH,"#%s$%s$%s$%s$", RESULTS, "array", file, tag);
            for(int a : array){
                System.out.printf(Locale.ENGLISH,"%s;", a);
            }
            System.out.println();
        }
        @Override
        public void writeArray(String file, String tag, long[] array) {
            System.out.printf(Locale.ENGLISH,"#%s$%s$%s$%s$", RESULTS, "array", file, tag);
            for(long a : array){
                System.out.printf(Locale.ENGLISH,"%s;", a);
            }
            System.out.println();
        }
        @Override
        public void writeArray(String file, String tag, double[] array) {
            System.out.printf(Locale.ENGLISH,"#%s$%s$%s$%s$", RESULTS, "array", file, tag);
            for(double a : array){
                System.out.printf(Locale.ENGLISH,"%s;", a);
            }
            System.out.println();
        }
    }

    private static final class Printer extends StreamAdapter implements StreamPrinter{
        private LinkedList<String> names = new LinkedList<String>();
        private LinkedList<String> forms = new LinkedList<String>();
        private LinkedList<Object> vals = new LinkedList<Object>();
        private LinkedList<Integer> sizes = new LinkedList<Integer>();
        private boolean frist = true;
        private int number = 0;
        
        public Printer(String type, String mk_name) throws Exception {
            super(type, mk_name);
        }
        public void printObj(String type, String name, String format, Object value){
            if(!Runner.LOCAL){
                System.out.printf(Locale.ENGLISH,"#%s$%s$%s$%s$%s\n", mk_name, type, name, format, value);
            }else{
                printObj(name, format, value);
            }
        }
        
        public void printObj(String name, String format, Object value){
            if(frist){
                if(names.contains(name)){
                    flush();
                }
            }
            names.addLast(name);
            forms.addLast(format);
            vals.addLast(value);
            sizes.addLast(String.format(Locale.ENGLISH,format, value).length());

            if(forms.size()==number){
                flush();
            }
        }
        @Override
        public void flush(){
            if(Runner.LOCAL){
                if(frist){
                    number = names.size();
                    System.out.printf(Locale.ENGLISH, "%"+Math.max(name().length(),6)+"s :", "stream");
                    while(names.size()>0){
                        System.out.print(String.format(Locale.ENGLISH, "%"+sizes.removeFirst()+"s", names.removeFirst()));
                    }
                    System.out.print("\n");
                    frist = false;
                }
                if(vals.size()>0){
                    System.out.printf(Locale.ENGLISH, "%"+Math.max(name().length(),6)+"s :", name());
                    while(forms.size()>0){
                        System.out.print(String.format(Locale.ENGLISH,forms.removeFirst(), vals.removeFirst()));
                    }
                    System.out.print("\n");
                }
                names.clear();
                sizes.clear();
            }
        }
        
        @Override
        public void printInt(String name, int value) {
            printObj("int", name, "%12d", value);
        }
        @Override
        public void printInt(String name, String format, int value) {
            printObj("int", name, format, value);
        }
        @Override
        public void printLong(String name, long value) {
            printObj("long", name, "%12d", value);
        }
        @Override
        public void printLong(String name, String format, long value) {
            printObj("long", name, format, value);
        }
        @Override
        public void printDbl(String name, double value)  {
            printObj("dbl", name, "%12g", value);
        }
        @Override
        public void printDbl(String name, String format, double value)  {
            printObj("dbl", name, format, value);
        }
        @Override
        public void printString(String name, String value)  {
            printObj("str", name, "%12s", value);
        }
        @Override
        public void printString(String name, String format, String value)  {
            printObj("str", name, format, value);
        }
    }
    private static final class Output extends StreamAdapter implements StreamOutput{
        public Output(String type, String mk_name) throws Exception {
            super(type, mk_name);
        }
        @Override
        public void printf(String format, Object... arg) {
            printObj(Locale.ENGLISH, format, arg);
        }
        @Override
        public void printf(Locale l, String format, Object... arg) {
            printObj(l, format, arg);
        }
        @Override
        public void println() {
            printObj(Locale.ENGLISH, "\n");
        }
        @Override
        public void println(String x) {
            printObj(Locale.ENGLISH, "%s", x);
        }
        public void printObj(Locale l, String format, Object ...args){
            if(!Runner.LOCAL){
                System.out.printf(Locale.ENGLISH,"#%s$%s\n", mk_name, String.format(l, format, args));
            }else{
                System.out.printf(l, format, args);
            }
        }
    }
    private static final class Plot2D extends StreamAdapter implements StreamPlot2D{
        public Plot2D(String mk_name) throws Exception {
            super(PLOT2D, mk_name);
        }
        @Override
        public void point(String name, double x, double y, int rgb, String description) throws Exception {
            if(!Runner.LOCAL) System.out.printf(Locale.ENGLISH,"#%s$%s$%s$%g$%g$%d$%s\n", mk_name, "point", name, x, y, rgb, description);
        }
        @Override
        public void clear(String name) throws Exception {
            if(!Runner.LOCAL) System.out.printf(Locale.ENGLISH,"#%s$%s$%s\n", mk_name, "clear", name);
        }
        @Override
        public void background(Color color) {
            if(!Runner.LOCAL) System.out.printf(Locale.ENGLISH,"#%s$%s$%d\n", mk_name, "background", color.getRGB());
        }
        
        @Override
        public void clear(int id) throws Exception {
            clear(""+id);
        }
        @Override
        public void point(int id, double x, double y) throws Exception {
            point(""+id,x,y,0,null);
        }
        @Override
        public void point(String name, double x, double y) throws Exception {
            point(name,x,y,0,null);
        }
        @Override
        public void point(int id, double x, double y, Color color) throws Exception {
            point(""+id,x,y,color.getRGB(),null);
        }
        @Override
        public void point(int id, double x, double y, int rgb) throws Exception {
            point(""+id,x,y,rgb,null);
        }
        @Override
        public void point(String name, double x, double y, Color color) throws Exception {
            point(name,x,y,color.getRGB(),null);
        }
        @Override
        public void point(String name, double x, double y, int rgb) throws Exception {
            point(name,x,y,rgb,null);
        }
        @Override
        public void point(int id, double x, double y, Color color, String description) throws Exception {
            point(""+id,x,y,color.getRGB(),description);
        }
        @Override
        public void point(int id, double x, double y, int rgb, String description) throws Exception {
            point(""+id,x,y,rgb,description);
        }
        @Override
        public void point(String name, double x, double y, Color color, String description) throws Exception {
            point(name,x,y,color.getRGB(),description);
        }
        
    }
    

    
}
