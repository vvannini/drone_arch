/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.gen.linear.vehicle.parts;

import ProOF.apl.UAV.abst.uncertainty.Delta;
import ProOF.apl.UAV.gen.linear.uncertainty.pLinearControlUncertainty;
import ProOF.apl.UAV.gen.linear.LinearControl;
import ProOF.apl.UAV.gen.linear.LinearApproach;
import ProOF.apl.UAV.gen.linear.LinearModel;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.language.Factory;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;

/**
 *
 * @author marcio
 */
public class fLinearControlBound extends Factory<oLinearControlBound>{
    public static final fLinearControlBound obj = new fLinearControlBound();
    @Override
    public String name() {
        return "Control Bound";
    }
    @Override
    public oLinearControlBound build(int index) {  //build the operators
        switch(index){
            case 0: return new DetNorm1UtUB();
            case 1: return new UncNorm1UtUB();
            //case 2: return new DetNorm2UtUB();
            //case 3: return new UncNorm2UtUB();
            //case 4: return new DetRealShapeUt();
            //case 5: return new UncRealShapeUt();
                
        }
        return null;
    }
    private class DetNorm1UtUB extends oLinearControlBound<LinearApproach, LinearModel>{
        @Override
        public String name() {
            return "DetNorm1(Ut)";
        }
        @Override
        public LinearControl[] build_controls(LinearApproach approach, final LinearModel model) throws Exception {
            LinearControl controls[] = new LinearControl[approach.Waypoints()];
            for(int t=0; t<approach.Waypoints(); t++){
                controls[t] = new LinearControl(model.cplex, approach.N()){
                    @Override
                    public IloNumExpr delta() throws Exception {
                        return null;
                    }
                    @Override
                    public IloNumExpr risk() throws Exception {
                        return null;
                    }
                };
                for(int i=0; i<approach.N(); i++){
                    controls[t].u[i].setLB(-approach.maxControl());
                    controls[t].u[i].setUB(+approach.maxControl());
                }
            }
            return controls;
        }
    }
    private class UncNorm1UtUB extends oLinearControlBound<LinearApproach, LinearModel>{
        private pLinearControlUncertainty unc;
        private Delta delta;
        private int Naprox;
        @Override
        public String name() {
            return "UncNorm1(Ut)";
        }
        @Override
        public void services(LinkerApproaches link) throws Exception {
            super.services(link); //To change body of generated methods, choose Tools | Templates.
            unc = link.need(pLinearControlUncertainty.class, unc);
            delta = link.need(Delta.class, delta);
        }
        @Override
        public void parameters(LinkerParameters link) throws Exception {
            super.parameters(link); //To change body of generated methods, choose Tools | Templates.
            Naprox  = link.Int("Control-N-risk", 12, 1, 16, 
                    "Number of picewise restriction to aproximate the "+
                    "inverse of erf(x) to calculate the risk in Norm1 control upper bound");
        }
        @Override
        public LinearControl[] build_controls(LinearApproach approach, final LinearModel model) throws Exception {
            LinearControl controls[] = new LinearControl[approach.Waypoints()];
            for(int t=0; t<approach.Waypoints(); t++){
                final IloNumVar In[] = model.cplex.numVarArray(2*approach.N(), 0, delta.Delta(), "U("+t+").Risk");
                
                final IloNumExpr c[] = new IloNumExpr[2*approach.N()];
                for(int n=0; n<2*approach.N(); n++){
                    int i = n/2;
                    double uncertainty = Math.sqrt(2*(unc.Sigma(t, i, i)));    //vetores orgotonais resultam nessa simplificação
                    c[n] = model.cplex.RiskAllocation(In[n], uncertainty, delta.Delta(), Naprox, "U("+t+").Alloc");
                }
                
                controls[t] = new LinearControl(model.cplex, approach.N()){
                    @Override
                    public IloNumExpr delta() throws Exception {
                        return model.cplex.sum(In);
                    }
                    @Override
                    public IloNumExpr risk() throws Exception {
                        return model.cplex.sum(c);
                    }
                };
                for(int n=0; n<2*approach.N(); n++){
                    int i = n/2;
                    double a = 1-(n%2)*2;
                    model.cplex.addLe(
                        model.cplex.sumArg(model.cplex.prod(a, controls[t].u[i]), c), +approach.maxControl()
                    );
                }
            }
            return controls;
        }
    }
}