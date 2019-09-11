/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.gen.codification.real;



/**
 *
 * @author marcio
 */
public abstract class aRealSingleObj implements ifunction{
    public abstract String name();
    public abstract int size();
    public double decode(double x, double min, double max){
        return min + x*(max-min);
    }
    @Override
    public abstract double F(double ...X);
}
