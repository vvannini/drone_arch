/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.parameter;

/**
 *
 * @author marcio
 */
public class SingleValue<Type> {
    protected final String name;
    protected final String description;
    protected Type value;

    public SingleValue(String name) {
        this(name, null);
    }
    public SingleValue(String name, String description) {
        this.name = name;
        this.description = description;
    }
    @Override
    public String toString() {
        return String.format("%s = %s", name, value);
    }
    public Type value() {
        return value;
    }
    
    public int Int() {
        return (Integer)value;
    }
    public long Long() {
        return (Long)value;
    }
    public double Dbl() {
        return (Double)value;
    }
}
