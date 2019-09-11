/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.parameter;

/**
 *
 * @author marcio
 */
public class SingleDouble extends SingleValue<Double> {
    protected final double init;
    protected final double min;
    protected final double max;

    public SingleDouble(String name, double init) {
        this(name, init, 1e-6, 1e+6, null);
    }
    public SingleDouble(String name, double init, double min, double max, String description) {
        super(name, description);
        this.init = init;
        this.min = min;
        this.max = max;
    }
}
