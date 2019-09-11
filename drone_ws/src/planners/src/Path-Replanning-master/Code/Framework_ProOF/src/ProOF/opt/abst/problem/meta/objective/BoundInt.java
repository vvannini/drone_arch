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
public class BoundInt implements Serializable {
    
    public static final BoundInt defalt = new BoundInt();
    
    private BoundInt() {
        this.lb = 0;
        this.ub = 1000000;
    }
    
    
    protected final int lb;
    protected final int ub;

    public BoundInt(int lb, int ub) throws Exception {
        if(lb>=ub){
            throw new Exception(String.format(
                "constructor %s(lb,ub) --> lb>=ub --> %d>=%d", 
                getClass().getName(), lb, ub)
            );
        }
        this.lb = lb;
        this.ub = ub;
    }
    
    public int valid(int value){
        if(value>ub){
            uWarnning.format("value > ub -> %d > %d\n", value, ub);
            return ub;
        }else if(value<lb){
            uWarnning.format("value < lb -> %d < %d\n", value, lb);
            return lb;
        }else{
            return value;
        }
    }
    public int compareTo(double a, double b) {
        if( a < b ){
            return -1;
        }else if( a > b ){
            return +1;
        }else{
            return 0;
        }
    }
}
