/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.opt.abst.problem;

/**
 *
 * @author marcio
 */
public abstract class Val {
    public final String title;
    protected final double value;
    public Val(String title, double value) {
        this.title = title;
        this.value = value;
    }
    public abstract double percent(double val);
}
