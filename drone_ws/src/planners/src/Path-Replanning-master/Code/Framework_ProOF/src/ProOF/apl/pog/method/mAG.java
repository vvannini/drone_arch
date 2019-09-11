/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.method;

import ProOF.apl.factorys.fStop;
import ProOF.apl.factorys.fProblem;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.language.Run;
import ProOF.gen.operator.cPopulation;
import ProOF.gen.operator.hImprovement;
import ProOF.opt.abst.problem.meta.Problem;


/**
 *
 * @author marcio
 */
public class mAG extends Run{
    private Problem prob;
    //private aStop stop;
    private cPopulation init;
    private hImprovement cross;
    @Override
    public String name() {
        return "AG sem stop";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void services(LinkerApproaches win) throws Exception {
        prob = win.get(fProblem.obj, prob);
        //stop = win.get(fStop.obj, stop);
        init = win.need(cPopulation.class, init);
        cross = win.need(hImprovement.class, cross);
    }
    @Override
    public void parameters(LinkerParameters win) throws Exception {
        int tam_pop = win.Int("Nº of Individuals", 10);
        //double taxa_mut = win.Dbl("taxa de mutacao", 0.05);
        //File filename = win.File("Instances for AG",null,"txt");
    }
    @Override
    public boolean validation(LinkerValidations win) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void load() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void start() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void results(LinkerResults win) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void execute() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
