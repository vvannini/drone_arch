/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.FuncB;

import ProOF.com.language.ApproachParameter;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.Linker.LinkerParameters;
import java.io.PrintStream;

/**
 *
 * @author marcio
 */
public class FuncBParameter extends ApproachParameter{
    private double min_x;
    private double max_x;

    
    @Override
    public String name() {
        return "Function-v2-Param";
    }

    /*@Override
    public void print(PrintStream out) throws Exception {
        out.printf("min_x = %g\n", min_x());
        out.printf("max_x = %g\n", max_x());
    }*/
    
    @Override
    public void parameters(LinkerParameters win) throws Exception {
        min_x = win.Dbl("min(x)", -1, -5, +5);
        max_x = win.Dbl("max(x)", +2, -5, +5);
    }

    @Override
    public boolean validation(LinkerValidations win) throws Exception {
        if(min_x >= max_x){
            //win.error("min(x)");
            //win.warning("max(x)");
            win.error(0);
            win.error(1);
            //win.warning(1);
            return false;
        }
        return true;
    }
    
    public double min_x(){
        return min_x;
    }
    public double max_x(){
        return max_x;
    }
    
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
