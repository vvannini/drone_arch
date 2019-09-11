/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.codification.FunctionSingle;

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
public abstract class RealSingle extends Approach{
    private final boolean has_constraints;
    private double penality;
    public RealSingle(){
        boolean flag = true;
        try{
            G(null);
        }catch(UnsupportedOperationException ex){
            flag = false;
        }catch(Exception ex){
            flag = false;
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
    
    public double evaluate(double X[]) throws Exception{
        if(has_constraints){
            return F(X) + penality*G(X);
        }else{
            return F(X);
        }
    }
    
    @Override
    public abstract String name();
    public abstract int size() throws Exception;
    /**
     * Define the objective function here
     * @param X vector of continuous variables in the range [0 , 1]. Use the function decode to change for the true interval [a, b]
     * @return the total cost must be returned
     * @throws Exception 
     */
    protected abstract double F(double X[]) throws Exception;
    /**
     * Define de subjects of the domine variables X
     * @param X vector of continuous variables in the range [0 , 1]. Use the function decode to change for the true interval [a, b]
     * @return one distance metric with de violation must be returned. (We recommend using 1-norm or 2-norm)
     * @throws Exception 
     */
    protected double G(double[] X) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public BoundDbl bound() {
        return BoundDbl.defalt;
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
