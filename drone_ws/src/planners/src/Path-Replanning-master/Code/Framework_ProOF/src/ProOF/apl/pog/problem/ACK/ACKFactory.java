/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.ACK;
import ProOF.apl.factorys.fRealOperator;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.language.Factory;
import ProOF.gen.best.BestSol;
import ProOF.gen.codification.Real.cReal;
import ProOF.gen.operator.oCrossover;
import ProOF.gen.operator.oInitialization;
import ProOF.gen.operator.oMutation;
import ProOF.opt.abst.problem.meta.Objective;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.codification.Codification;
import ProOF.opt.abst.problem.meta.codification.Operator;
import ProOF.opt.abst.problem.meta.objective.SingleObjective;

/**
 *
 * @author marcio
 */
public class ACKFactory extends Problem<BestSol> {
    
    @Override
    public String name() {
        return "ACK";
    }
    @Override
    public Codification build_codif() throws Exception {
        return new cReal(10);
    }
    @Override
    public Objective build_obj() throws Exception {
        return new SingleObjective() {
            @Override
            public void evaluate(Problem mem, Codification cod) throws Exception {
                //Casting
                cReal codif = (cReal) cod;
                
                double sum1 = 0;
                double sum2 = 0;
                for(int i=0; i<10; i++){
                    //Decoder
                    double xi = codif.X(i, -30, +30);
                    sum1 += xi * xi;
                    sum2 += Math.cos(2*Math.PI*xi);
                }

                //Evaluate
                double fitness =  -20.0*Math.exp(-0.02*Math.sqrt(sum1/10.0)) - Math.exp(sum2/10.0) + 20 + Math.E;

                set(fitness);
            }
            @Override
            public SingleObjective build(Problem mem) throws Exception {
                return this.getClass().newInstance();
            }
        };
    }

    @Override
    public BestSol best() {
        return BestSol.object();
    }
    @Override
    public void services(LinkerApproaches link) throws Exception {
        super.services(link);
        link.add(fRealOperator.obj);
        link.add(new Factory<Operator>() {
            @Override
            public String name() {
                return "ACK Init";
            }
            @Override
            public Operator build(int index) {
                switch(index){
                    case  0: return new oInitialization() {
                        @Override
                        public void initialize(Problem mem, Codification ind) throws Exception {
                            throw new UnsupportedOperationException("Not supported yet.");
                        }
                        @Override
                        public String name() {
                            return "Random Value";
                        }
                    };
                }
                return null;
            }
        });
        link.add(new Factory<Operator>() {
            @Override
            public String name() {
                return "ACK Cross";
            }
            @Override
            public Operator build(int index) {
                switch(index){
                    case  0: return new oCrossover() {
                        @Override
                        public String name() {
                            return "BLX-α";
                        }

                        @Override
                        public void parameters(LinkerParameters win) throws Exception {
                            double alpha = win.Dbl("alpha", 1);
                        }
                        
                        @Override
                        public Codification crossover(Problem mem, Codification ind1, Codification ind2) throws Exception {
                            throw new UnsupportedOperationException("Not supported yet.");
                        }
                    };
                }
                return null;
            }
        });
        link.add(new Factory<Operator>() {
            @Override
            public String name() {
                return "ACK Mut";
            }
            @Override
            public Operator build(int index) {
                switch(index){
                    case  0: return new oMutation() {
                        @Override
                        public String name() {
                            return "Uniform";
                        }
                        @Override
                        public void mutation(Problem mem, Codification ind) throws Exception {
                            throw new UnsupportedOperationException("Not supported yet.");
                        }
                    };
                }
                return null;
            }
        });
    }
    
    
    
    
    
}