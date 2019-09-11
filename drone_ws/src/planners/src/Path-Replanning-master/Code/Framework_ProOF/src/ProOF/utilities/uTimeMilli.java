/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.utilities;

/**
 *
 * @author marcio
 */
public class uTimeMilli extends uTime{
    @Override
    protected long sys_time() {
        return System.currentTimeMillis();
    }
    @Override
    protected double sys_precision() {
        return 1e3;
    }
}
