/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.GCILT.withZyt;

import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.language.Approach;
import ProOF.gen.best.BestSol;
import ProOF.opt.abst.problem.meta.Problem;

/**
 *
 * @author marcio
 */
public abstract class aGCILTModel extends Approach{
    
    protected GCILTWProblem gcilt;
    protected BestSol best;
    
    public abstract double initialize(GCILTCodification codif) throws Exception;
    public abstract double execute(GCILTCodification codif) throws Exception;
    public abstract void set(boolean print);

    @Override
    public void services(LinkerApproaches win) throws Exception {
        gcilt = win.need(Problem.class, gcilt);
        best = win.need(BestSol.class, best);
    }
    @Override
    public void parameters(LinkerParameters win) throws Exception {
        
    }
    @Override
    public boolean validation(LinkerValidations com) throws Exception {
        return true;
    }
    @Override
    public void load() throws Exception {
        
    }
    @Override
    public void start() throws Exception {
        
    }
    
    
    @Override
    public void results(LinkerResults win) throws Exception {
        set(true);
        best.ind().evaluate(gcilt);
    }
    
    
    protected static void clear(double[] src) {
	for (int i = 0; i < src.length; i++) {
	    src[i] = 0.0;
	}
    }

    protected static void clear(double[][] src) {
	for (int i = 0; i < src.length; i++) {
	    clear(src[i]);
	}
    }

    protected static void clear(double[][][] src) {
	for (int i = 0; i < src.length; i++) {
	    clear(src[i]);
	}
    }

    protected static void clear(double[][][][] src) {
	for (int i = 0; i < src.length; i++) {
	    clear(src[i]);
	}
    }
    protected static void clear(int[][][][] src) {
	for (int i = 0; i < src.length; i++) {
	    clear(src[i]);
	}
    }
    protected static void clear(int[][][] src) {
	for (int i = 0; i < src.length; i++) {
	    clear(src[i]);
	}
    }
    protected static void clear(int[][] src) {
	for (int i = 0; i < src.length; i++) {
	    clear(src[i]);
	}
    }
    protected static void clear(int[] src) {
	for (int i = 0; i < src.length; i++) {
	    src[i] = 0;
	}
    }
    protected static void clear(boolean[][][][] src) {
        clear(src, false);
    }
    protected static void clear(boolean[][][] src) {
        clear(src, false);
    }
    protected static void clear(boolean[][] src) {
        clear(src, false);
    }
    protected static void clear(boolean[] src) {
        clear(src, false);
    }
    protected static void clear(boolean[][][][] src, boolean flag) {
	for (int i = 0; i < src.length; i++) {
	    clear(src[i], flag);
	}
    }
    protected static void clear(boolean[][][] src, boolean flag) {
	for (int i = 0; i < src.length; i++) {
	    clear(src[i], flag);
	}
    }
    protected static void clear(boolean[][] src, boolean flag) {
	for (int i = 0; i < src.length; i++) {
	    clear(src[i], flag);
	}
    }
    protected static void clear(boolean[] src, boolean flag) {
	for (int i = 0; i < src.length; i++) {
	    src[i] = flag;
	}
    }
}
