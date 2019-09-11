/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.codification.FunctionMulti;

import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.language.Approach;
import ProOF.opt.abst.problem.meta.objective.BoundDbl;
import ProOF.utilities.uUtil;

/**
 *
 * @author marcio
 */
public abstract class RealMulti extends Approach{
    private final boolean has_constraints;
    private double penality;
    
    public RealMulti(){
        boolean flag = false;
        try{
            G(null);
        }catch(Exception ex){
            flag = true;
        }
        this.has_constraints = flag;
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
        if(has_constraints){
            penality = link.Dbl("g-penality", 1e6, 0, 1e9);
        }
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
    
    public final double evaluate(int goal, double X[]) throws Exception{
        return F(goal, X);
    }
    
    public final double constrant( double X[]) throws Exception{
        if(has_constraints){
            return penality*G(X);
        }else{
            return 0;
        }
    }
    
    @Override
    public abstract String name();
    
    public abstract int size() throws Exception;
    public abstract int goals() throws Exception;
    
    protected abstract double F(int goal, double X[]) throws Exception;
    
    protected double G(double[] X) throws Exception {
        return 0;
    }
    
    private BoundDbl[] bounds = null;
    public BoundDbl[] bounds() throws Exception {
        if(bounds==null){
            bounds = new BoundDbl[goals()];
            for(int i=0; i<goals(); i++){
                bounds[i] = BoundDbl.defalt;
            }
        }
        return bounds;
    }
    
    public static double[] decode(double X[], double min[], double max[]){
        return uUtil.decode(X, min, max);
    }
    public static double[] decode(double X[], double min, double max){
        return uUtil.decode(X, min, max);
    }
    public static double decode(double x, double min, double max){
        return uUtil.decode(x, min, max);
    }
    
    public static double LE(double a, double b){
        return uUtil.LE(a,b);
    }
    public static double GE(double a, double b){
        return uUtil.GE(a,b);
    }
}
