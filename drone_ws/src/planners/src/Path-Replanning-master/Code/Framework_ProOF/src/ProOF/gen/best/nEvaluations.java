/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.best;

import ProOF.com.language.ApproachSingle;


/**
 *
 * @author marcio
 */
public class nEvaluations extends ApproachSingle{
    private static nEvaluations obj = null;
    public static nEvaluations object(){
        if(obj==null){
            obj = new nEvaluations();
        }
        return obj;
    }
    private nEvaluations() {
    }
    
    private long evaluations;
    
    public void update(){
        evaluations++;
    }
    public long value(){
        return evaluations;
    }

    @Override
    public void start() throws Exception {
        evaluations = 0;
    }

    @Override
    public String name() {
        return "Evaluations";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    /*@Override
    public void print(PrintStream out) throws Exception {
        out.printf("evaluations = %d\n", evaluations);
    }*/
}
