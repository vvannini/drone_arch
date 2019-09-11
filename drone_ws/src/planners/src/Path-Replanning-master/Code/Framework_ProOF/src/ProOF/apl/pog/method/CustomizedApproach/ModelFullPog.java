/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.method.CustomizedApproach;

import static ProOF.apl.pog.method.CustomizedApproach.AddRestrictions.ID_VARIABLE;
import ProOF.com.Linker.LinkerResults;

/**
 *
 * @author marcio
 */
public class ModelFullPog extends Abstraction {

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
        
        for(int t=0; t<inst.T; t++){
            Model toFix = full;
            
            full = new Model("Model-FULL"+t, inst, selectOBJ(), ID_VARIABLE, selectREST(), null);
            
            if(toFix!=null){
                for(int s=0; s<t; s++){
                    for(int j=0; j<2; j++){
                        full.Ut[s][j].setLB(toFix.cplex.getValue(toFix.Ut[s][j]));
                        full.Ut[s][j].setUB(toFix.cplex.getValue(toFix.Ut[s][j]));
                    }
                    for(int j=0; j<4; j++){
                        full.MUt[s][j].setLB(toFix.cplex.getValue(toFix.MUt[s][j]));
                        full.MUt[s][j].setUB(toFix.cplex.getValue(toFix.MUt[s][j]));
                    }
                }
                inst.DELTA += toFix.RiskAlocation()[t-1];
            }
            full.execute(execTime, epGap, threads);
            if (full.isFeasible()) {
                full.save(t);
                rote = full.rote();
            } else {
                System.err.println("Problem not solved [1]");
                status = "not solved[1]";
                break;
                //JOptionPane.showMessageDialog(null, "not solved [1]", "nontrivial cost bounds", JOptionPane.INFORMATION_MESSAGE);
            }
        }
            
        time = System.currentTimeMillis() - time;
    }

    @Override
    public void results(LinkerResults link) throws Exception {
        full.results(link); 
    }
}
