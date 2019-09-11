/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.dynamic;

/**
 *
 * @author marcio
 */
public abstract class DyState<S extends DyState, C> {
    public final S next;
    public final C control;

    public DyState(S next, C control) {
        this.next = next;
        this.control = control;
    }
    
    public abstract boolean feasible() throws Exception;
    public abstract boolean equal(S o) throws Exception;
}
