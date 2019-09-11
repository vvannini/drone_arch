/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.parameter;

/**
 *
 * @author marcio
 */
public class SingleLong extends SingleValue<Long> {
    protected final long init;
    protected final long min;
    protected final long max;

    public SingleLong(String name, long init) {
        this(name, init, 0, 1000000000, null);
    }
    public SingleLong(String name, long init, long min, long max, String description) {
        super(name, description);
        this.init = init;
        this.min = min;
        this.max = max;
    }
}
