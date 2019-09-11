/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.method.CustomizedApproach;

import ProOF.com.Linker.LinkerResults;
import java.io.PrintStream;
import java.util.Locale;



/**
 *
 * @author marcio
 */
public class CustomizedApproach extends Abstraction {
    private double upper;
    private double lower;
    private Model frr;
    private Model frt;
    private Model fra1;
    private Model fra2;
    
    @Override
    public String name() {
        return "Customized Approach";
    }

    @Override
    public void execute() throws Exception {
//        inst.plot(String.format("%s - %6.2f %%", "teste", 100.0), null);
//        inst.save();
        time = System.currentTimeMillis();
        upper = Integer.MAX_VALUE;
        lower = Integer.MIN_VALUE;
        rote = null;
        status = "ok";
        frr = selectFRR("FRR");
        frr.execute(execTime, epGap, threads);
        if (frr.isFeasible()) {
            lower = frr.lower();
            fra1 = selectFRA("FRA1", frr);
            fra1.execute(execTime, epGap, threads);
            if (fra1.isFeasible()) {
                upper = fra1.upper();
                rote = fra1.rote();
            } else {
                frt = selectFRT("FRT");
                frt.execute(execTime, epGap, threads);
                if (frt.isFeasible()) {
                    upper = frt.upper();
                    fra2 = selectFRA("FRA2", frt);
                    fra2.execute(execTime, epGap, threads);
                    if (fra2.isFeasible()) {
                        upper = fra2.upper();
                        rote = fra2.rote();
                    } else {
                        status = "not solved[3]";
                        System.err.println("Problem not solved [3]");
                        upper = Integer.MAX_VALUE;
                        //JOptionPane.showMessageDialog(null, "not solved [3]", "nontrivial cost bounds", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    upper = Integer.MAX_VALUE;
                    status = "not solved[2]";
                    System.err.println("Problem not solved [2]");
                    //JOptionPane.showMessageDialog(null, "not solved [2]", "nontrivial cost bounds", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } else {
            lower = Integer.MAX_VALUE;
            System.err.println("Problem not solved [1]");
            status = "not solved[1]";
            //JOptionPane.showMessageDialog(null, "not solved [1]", "nontrivial cost bounds", JOptionPane.INFORMATION_MESSAGE);
        }
        time = System.currentTimeMillis() - time;
    }

    @Override
    public void results(LinkerResults link) throws Exception {
        link.writeString("CA-status", status);
        link.writeDbl("CA-upper", upper);
        link.writeDbl("CA-lower", lower);
        link.writeDbl("CA-time", time/1000.0);
        
         if (inst.online) {
            PrintStream output = new PrintStream("./rote.txt");
            output.println("<is feasible>");
            if (rote == null) {
                output.println(false);
            } else {
                output.println(true);
                output.println("<T>");
                output.println(inst.T);
                output.println("<waypoints: id  x   y   z>");
                for (int t = 1; t < inst.T + 1; t++) {
                    output.printf(Locale.ENGLISH, "%d\t%g\t%g\t%g\n", t-1, rote[t][0], rote[t][2], 8000.0);
                }
            }
            output.close();
        }
        
        if (frr != null && frr.isFeasible()) {
            frr.results(link);
        }
        if (frt != null && frt.isFeasible()) {
            frt.results(link);
        }
        if (fra1 != null && fra1.isFeasible()) {
            fra1.results(link);
        }
        if (fra2 != null && fra2.isFeasible()) {
            fra2.results(link);
        }
    }
}
