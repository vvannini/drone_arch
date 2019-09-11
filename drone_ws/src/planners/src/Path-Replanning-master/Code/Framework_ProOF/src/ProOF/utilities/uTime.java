/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.utilities;

/**
 *
 * @author marcio
 */
public abstract class uTime {
    private long ti;
    private long tf;
    private double precision;
    
    public uTime() {
        start();
    }
    public final void start(){
        tf = ti = sys_time();
        precision = sys_precision();
    }
    public final double time(){
        tf = sys_time();
        return (tf-ti)/precision;
    }
    protected abstract long sys_time();
    protected abstract double sys_precision();
}
