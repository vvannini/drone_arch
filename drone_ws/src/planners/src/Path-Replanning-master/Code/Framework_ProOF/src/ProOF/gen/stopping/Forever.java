/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.stopping;


/**
 *
 * @author marcio
 */
public final class Forever extends Stop{
    @Override
    public String name() {
        return "Forever";
    }
    @Override
    public double progress() throws Exception {
        return -1;
    }
    @Override
    public boolean end() throws Exception {
        return false;
    }
}
