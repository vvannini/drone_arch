/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.method.CustomizedApproach;

import ProOF.com.Linker.LinkerResults;

/**
 *
 * @author marcio
 */
public class ModelFull extends Abstraction {

    private Model full;

    @Override
    public String name() {
        return "PPDCP - Model Full";
    }

    @Override
    public void execute() throws Exception {
        time = System.currentTimeMillis();
        rote = null;
        status = "ok";
        
        full = selectModelFull("Model-FULL");
        full.execute(execTime, epGap, threads);
        
        if (full.isFeasible()) {
            rote = full.rote();
        } else {
            System.err.println("Problem not solved [1]");
            status = "not solved[1]";
            //JOptionPane.showMessageDialog(null, "not solved [1]", "nontrivial cost bounds", JOptionPane.INFORMATION_MESSAGE);
        }
        time = System.currentTimeMillis() - time;
    }

    @Override
    public void results(LinkerResults link) throws Exception {
        full.results(link); 
    }
}
