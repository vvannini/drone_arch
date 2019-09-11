/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.opt.abst.problem;

/**
 *
 * @author marcio
 */
public class Gap extends Val{
    public Gap(String title, double optimal) {
        super(title, optimal);
    }
    @Override
    public double percent(double val){
        return (val-value)*100.0/(Math.abs(val)+1e-10);
    }
}
