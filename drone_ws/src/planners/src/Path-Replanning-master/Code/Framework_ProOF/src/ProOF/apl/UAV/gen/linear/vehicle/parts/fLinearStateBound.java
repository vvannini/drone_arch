/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.gen.linear.vehicle.parts;

import ProOF.apl.UAV.abst.uncertainty.Delta;
import ProOF.apl.UAV.gen.linear.uncertainty.pLinearStateUncertainty;
import ProOF.apl.UAV.gen.linear.LinearState;
import ProOF.apl.UAV.gen.linear.LinearApproach;
import ProOF.apl.UAV.gen.linear.LinearModel;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.language.Factory;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;

/**
 *
 * @author marcio
 */
public class fLinearStateBound extends Factory<oLinearStateBound>{
    public static final fLinearStateBound obj = new fLinearStateBound();
    @Override
    public String name() {
        return "State Bound";
    }
    @Override
    public oLinearStateBound build(int index) {  //build the operators
        switch(index){
            case 0: return new DetNorm1VtUB();
            case 1: return new UncNorm1VtUB();
            //case 2: return new DetNorm2VtUB();
            //case 3: return new UncNorm2VtUB();
            //case 2: return new DetFixedWings();
            //case 5: return new UncFixedWings();
            //case 6: return new DetRealShapeVt();
            //case 7: return new UncRealShapeVt();
        }
        return null;
    }
    private class DetNorm1VtUB extends oLinearStateBound<LinearApproach, LinearModel>{
        @Override
        public String name() {
            return "DetNorm1(Vt)";
        }
        @Override
        public LinearState[] build_states(LinearApproach approach, final LinearModel model) throws Exception {
            LinearState states[] = new LinearState[approach.Waypoints()+1];
            for(int t=0; t<approach.Waypoints()+1; t++){
                states[t] = new LinearState(model.cplex, approach.N(),t){
                    @Override
                    public IloNumExpr delta() throws IloException {
                        return null;
                    }
                    @Override
                    public IloNumExpr risk() throws IloException {
                        return null;
                    }
                };
                for(int i=approach.N(); i<2*approach.N(); i++){
                    states[t].x[i].setLB(-approach.maxVelocity());
                    states[t].x[i].setUB(+approach.maxVelocity());
                }
            }
            return states;
        }
    }
    private class UncNorm1VtUB extends oLinearStateBound<LinearApproach, LinearModel>{
        private pLinearStateUncertainty unc;
        private Delta delta;
        private int Naprox;
        @Override
        public String name() {
            return "UncNorm1(Vt)";
        }
        @Override
        public void services(LinkerApproaches link) throws Exception {
            super.services(link); //To change body of generated methods, choose Tools | Templates.
            unc = link.need(pLinearStateUncertainty.class, unc);
            delta = link.need(Delta.class, delta);
        }
        @Override
        public void parameters(LinkerParameters link) throws Exception {
            super.parameters(link); //To change body of generated methods, choose Tools | Templates.
            Naprox  = link.Int("Velocity-N-risk", 12, 1, 16, 
                    "Number of picewise restriction to aproximate the "+
                    "inverse of erf(x) to calculate the risk in Norm1 velocity upper bound");
        }
        @Override
        public LinearState[] build_states(LinearApproach approach, final LinearModel model) throws Exception {
            LinearState states[] = new LinearState[approach.Waypoints()+1];
            for(int t=0; t<approach.Waypoints()+1; t++){
                final IloNumVar In[] = model.cplex.numVarArray(2*approach.N(), 0, delta.Delta(), "V("+t+").Risk");
                final IloNumExpr c[] = new IloNumExpr[2*approach.N()];
                for(int n=0; n<2*approach.N(); n++){
                    int i = approach.N()+n/2;
                    double uncertainty = Math.sqrt(2*(unc.Sigma(t, i, i)));    //vetores orgotonais resultam nessa simplificação
                    c[n] = model.cplex.RiskAllocation(In[n], uncertainty, delta.Delta(), Naprox, "V("+t+").Alloc");
                }
                
                states[t] = new LinearState(model.cplex, approach.N(),t){
                    @Override
                    public IloNumExpr delta() throws IloException {
                        return model.cplex.sum(In);
                    }
                    @Override
                    public IloNumExpr risk() throws IloException {
                        return model.cplex.sum(c);
                    }
                };
                for(int n=0; n<2*approach.N(); n++){
                    int i = approach.N()+n/2;
                    double a = 1-(n%2)*2;
                    model.cplex.addLe(
                        model.cplex.sumArg(model.cplex.prod(a, states[t].x[i]), c), +approach.maxVelocity()
                    );
                }
            }
            return states;
        }
    }

//    private class DetFixedWings extends oLinearStateBound<LinearApproach, LinearModel>{
//        private double minVelocity;
//        private int Naprox;
//        
//        @Override
//        public String name() {
//            return "DetFixedWings";
//        }
//        
//        @Override
//        public void parameters(LinkerParameters link) throws Exception {
//            super.parameters(link); //To change body of generated methods, choose Tools | Templates.
//            minVelocity = link.Dbl("Min-velocity", 2.0, 1e-5, 1e6);
//            Naprox = link.Int("N-aprox-minV", 16, 4, 64);
//        }
//        
//        @Override
//        public LinearState[] build_states(LinearApproach approach, final LinearModel model) throws Exception {
//            LinearState states[] = new LinearState[approach.Waypoints()+1];
//            for(int t=0; t<approach.Waypoints()+1; t++){
//                states[t] = new LinearState(model.cplex, approach.N(),t){
//                    @Override
//                    public IloNumExpr delta() throws IloException {
//                        return null;
//                    }
//                    @Override
//                    public IloNumExpr risk() throws IloException {
//                        return null;
//                    }
//                }???;
//                //a(n) * v(t) <= Vmax		todo(t,n)
//                for(int n=0; n<Naprox; n++){
//                    model.cplex.addLe(model.cplex.sum(model.cplex.prod(ax(n), states[t].x[2]), model.cplex.prod(ay(n), states[t].x[3])), approach.maxVelocity());
//                }
//                for(int i=approach.N(); i<2*approach.N(); i++){
//                    states[t].x[i].setLB(-approach.maxVelocity());
//                    states[t].x[i].setUB(+approach.maxVelocity());
//                }
//            }
//            return states;
//        }
//        private double ax(int n){
//            return Math.cos(2*n*Math.PI/Naprox);
//        }
//        private double ay(int n){
//            return Math.sin(2*n*Math.PI/Naprox);
//        }
//        private double cx(int n){
//            return -ay(n);
//        }
//        private double cy(int n){
//            return ax(n);
//        }
//    }
}
