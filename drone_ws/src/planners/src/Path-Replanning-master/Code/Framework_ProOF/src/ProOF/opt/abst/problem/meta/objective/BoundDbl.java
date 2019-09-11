/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.opt.abst.problem.meta.objective;

import ProOF.utilities.uWarnning;
import java.io.Serializable;

/**
 *
 * @author marcio
 */
public class BoundDbl implements Serializable{
    
    public static final BoundDbl defalt = new BoundDbl();
    
    private BoundDbl() {
        this.lb = -1e8;
        this.ub = 1e8;
        this.precision = 1e-6;
        this.name = "objective";
        this.format_name = "%12s";
        this.format_value = "%12g";
    }
    
    
    protected final double lb;
    protected final double ub;
    protected final double precision;
    protected final String name;
    protected final String format_name;
    protected final String format_value;

    public BoundDbl(double lb, double ub, double precision) {
        this.lb = lb;
        this.ub = ub;
        this.precision = precision;
        this.name = "objective";
        this.format_name = "%12s";
        this.format_value = "%12g";
    }
    public BoundDbl(
        double lb, double ub, double precision, String name, int col
    ) throws Exception {
        if(lb>=ub){
            throw new Exception(String.format(
                "constructor %s --> lb>=ub --> %g>=%g", 
                getClass().getName(), lb, ub)
            );
        }
        this.lb = lb;
        this.ub = ub;
        this.precision = precision;
        this.name = name;
        this.format_name = "%"+col+"s";
        this.format_value = "%"+col+"g";
    }
    
    public double valid(double value){
        if(value>ub){
            uWarnning.format("value > ub -> %g > %g\n", value, ub);
            return ub;
        }else if(value<lb){
            uWarnning.format("value < lb -> %g < %g\n", value, lb);
            return lb;
        }else{
            return value;
        }
    }
    /**
     * @return the relative value, interval [0 , 1]
     */
    public final double relative(double value){
        return (value-lb)/(ub-lb);
    }
    
    public boolean EQ(double a, double  b){
        if(Math.abs(b-a)<precision){
            return true;
        }
        return false;
    }
    public boolean GT(double a, double  b){
        if(a-precision>b){
            return true;
        }
        return false;
    }
    public boolean LT(double a, double  b){
        if(a+precision<b){
            return true;
        }
        return false;
    }
    public int compareTo(double a, double b) {
        if(LT(a, b)){
            return -1;
        }else if(GT(a, b)){
            return +1;
        }else{
            return 0;
        }
    }
    public String print_title() throws Exception{
        return String.format(format_name, name);
    }
    public String print_value(double value) throws Exception{
        return String.format(format_value, value);
    }
}
