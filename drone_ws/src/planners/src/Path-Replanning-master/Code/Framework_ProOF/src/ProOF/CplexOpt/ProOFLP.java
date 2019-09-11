/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ProOF.CplexOpt;

import ProOF.CplexExtended.CplexExtended;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.language.Approach;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex.UnknownObjectException;

/**
 *
 * @author marcio
 */
public class ProOFLP extends Approach{
    
    public final CplexExtended cpx;
    
    public ProOFLP() throws IloException {
        cpx = new CplexExtended();
    }
    
    @Override
    public String name() {
        return "ProOF-LP";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void services(LinkerApproaches link) throws Exception {
        
    }
    @Override
    public void parameters(LinkerParameters link) throws Exception {
        
    }
    @Override
    public void load() throws Exception {
        
    }
    @Override
    public void start() throws Exception {
        
    }
    @Override
    public boolean validation(LinkerValidations link) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void results(LinkerResults link) throws Exception {
        
    }
    
    public IloNumVar[] numVarArray(int d1, double min, double max, String name) throws IloException{
        return cpx.numVarArray(d1, min, max, name);
    }
    public IloNumVar[][] numVarArray(int d1, int d2, double min, double max, String name) throws IloException{
        return cpx.numVarArray(d1, d2, min, max, name);
    }
    public IloNumVar[][][] numVarArray(int d1, int d2, int d3, double min, double max, String name) throws IloException{
        return cpx.numVarArray(d1, d2, d3, min, max, name);
    }
    public IloNumVar[][][][] numVarArray(int d1, int d2, int d3, int d4, double min, double max, String name) throws IloException{
        return cpx.numVarArray(d1, d2, d3, d4, min, max, name);
    }
    public IloNumVar[][][][][] numVarArray(int d1, int d2, int d3, int d4, int d5, double min, double max, String name) throws IloException{
        return cpx.numVarArray(d1, d2, d3, d4, d5, min, max, name);
    }
    public IloIntVar[] intVarArray(int d1, int min, int max, String name) throws IloException{
        return cpx.intVarArray(d1, min, max, name);
    }
    public IloIntVar[][] intVarArray(int d1, int d2, int min, int max, String name) throws IloException{
        return cpx.intVarArray(d1, d2, min, max, name);
    }
    public IloIntVar[][][] intVarArray(int d1, int d2, int d3, int min, int max, String name) throws IloException{
        return cpx.intVarArray(d1, d2, d3, min, max, name);
    }
    public IloIntVar[][][][] intVarArray(int d1, int d2, int d3, int d4, int min, int max, String name) throws IloException{
        return cpx.intVarArray(d1, d2, d3, d4, min, max, name);
    }
    public IloIntVar[] boolVarArray(int d1, String name) throws IloException{
        return cpx.boolVarArray(d1, name);
    }
    public IloIntVar[][] boolVarArray(int d1, int d2, String name) throws IloException{
        return cpx.boolVarArray(d1, d2, name);
    }
    public IloIntVar[][][] boolVarArray(int d1, int d2, int d3, String name) throws IloException{
        return cpx.boolVarArray(d1, d2, d3, name);
    }
    public IloIntVar[][][][] boolVarArray(int d1, int d2, int d3, int d4, String name) throws IloException{
        return cpx.boolVarArray(d1, d2, d3, d4, name);
    }
    public IloIntVar[][][][][] boolVarArray(int d1, int d2, int d3, int d4, int d5, String name) throws IloException{
        return cpx.boolVarArray(d1, d2, d3, d4, d5, name);
    }

    public IloNumVar[][] numVarArray(int d1, int d2, double min, double max) throws IloException{
        return cpx.numVarArray(d1, d2, min, max);
    }
    public IloNumVar[][][] numVarArray(int d1, int d2, int d3, double min, double max) throws IloException{
        return cpx.numVarArray(d1, d2, d3, min, max);
    }
    public IloNumVar[][][][] numVarArray(int d1, int d2, int d3, int d4,double min, double max) throws IloException{
        return cpx.numVarArray(d1, d2, d3, d4, min, max);
    }
    public IloNumVar[][][][][] numVarArray(int d1, int d2, int d3, int d4, int d5, double min, double max) throws IloException{
        return cpx.numVarArray(d1, d2, d3, d4, d5, min, max);
    }
    public double[][] getValues(IloNumVar V[][]) throws UnknownObjectException, IloException{
        return cpx.getValues(V);
    }
    public double[][][] getValues(IloNumVar V[][][]) throws UnknownObjectException, IloException{
        return cpx.getValues(V);
    }
    public double[][][][] getValues(IloNumVar V[][][][]) throws UnknownObjectException, IloException{
        return cpx.getValues(V);
    }
    public IloNumExpr Sum(IloNumExpr M[]) throws IloException{
        return cpx.Sum(M);
    }
    public IloNumExpr Sum(IloNumExpr M[][]) throws IloException{
        return cpx.Sum(M);
    }
    public IloNumExpr Sum(IloNumExpr M[][][]) throws IloException{
        return cpx.Sum(M);
    }
    public IloNumExpr Sum(IloNumExpr M[][][][]) throws IloException{
        return cpx.Sum(M);
    }

    public void addSubject(Object ...obj) throws IloException{
        cpx.addSubject(obj);
    }
}
