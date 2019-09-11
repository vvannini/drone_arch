/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.stopping;

import ProOF.com.language.ApproachSingle;


/**
 *
 * @author marcio
 */
public class CountIteration extends ApproachSingle{
    public final static CountIteration obj = new CountIteration();

    private CountIteration() {
    
    }
    
    private long iterations;
    
    public void iteration(){
        iterations++;
    }
    public long value(){
        return iterations;
    }
    @Override
    public String name() {
        return "CountIterations";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void start() throws Exception {
        iterations = 0;
    }
}
