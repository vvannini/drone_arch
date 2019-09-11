/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.MatrixDinamicsCplex;
import ProOF.com.Stream.StreamPrinter;
import ProOF.opt.abst.problem.meta.objective.BoundDbl;
import ProOF.opt.abst.problem.meta.objective.SingleObjective;
import java.util.Locale;

/**
 *
 * @author marcio
 */
public class MDObjectiveCpx extends SingleObjective<MDFactoryCpx, MDCodificationCpx, MDObjectiveCpx> {
    private static BoundDbl bound = new BoundDbl(0, 1e99, 1e-3);
    public MDObjectiveCpx() throws Exception {
        super(bound);
    }
    
    private double obj1;
    private double obj2;
    private double obj3;
    private double obj4;
    private double obj5;
    @Override
    public void evaluate(MDFactoryCpx prob, MDCodificationCpx codif) throws Exception {
        obj1 = codif.R;// * 1000;
        obj2 = prob.solver.solve(codif.A, codif.B, codif.C, codif.R);
        
        obj5 = 0;
        obj3 = 0;
        obj4 = 0;
        double S[] = new double[prob.inst.S+codif.R];
        for(int s=0; s<prob.inst.S; s++){
            S[s] = prob.inst.states[s][0];
        }
        double peso = 1;
        for(int t=1; t<prob.inst.T; t++){
            double sum[] = new double[S.length];
            for(int s=0; s<S.length; s++){
                for(int j=0; j<S.length; j++){
                    sum[s] += codif.A[s][j] * S[j];
                }
            }
            for(int s=0; s<S.length; s++){
                for(int i=0; i<prob.inst.I; i++){
                    sum[s] += codif.B[s][i] * prob.inst.inputs[i][t-1];
                }
            }
            System.arraycopy(sum, 0, S, 0, S.length);
            for(int s=0; s<prob.inst.S; s++){
                obj3 += (S[s]-prob.inst.states[s][t])*(S[s]-prob.inst.states[s][t])/peso;
                obj5 = Math.max(obj5, (S[s]-prob.inst.states[s][t])*(S[s]-prob.inst.states[s][t])/peso);
            }
            if(t%100==0){
                for(int s=0; s<prob.inst.S; s++){
                    S[s] = prob.inst.states[s][t];
                }
                peso = 1;
            }
            if(obj3>1e9){
                obj4 += (prob.inst.T-t)*1e9;
                break;
            }
            //peso += 1;
        }
        obj2 = obj2*1e4;
        obj3 = obj3/prob.inst.T;
        //obj3 = obj3/Math.min(100,prob.inst.T);
        /*
        if(obj2<0.0001){
            obj3 = 0;
            for(int i=0; i<codif.A.length; i++){
                for(int j=0; j<codif.A[i].length; j++){
                    double v = Math.abs(codif.A[i][j]);
                    obj3 += 1e3*(Math.exp(v)-1)/(1+Math.exp(v));
                }
                for(int j=0; j<codif.B[i].length; j++){
                    double v = Math.abs(codif.B[i][j]);
                    obj3 += 1e3*(Math.exp(v)-1)/(1+Math.exp(v));
                }
            }
        }else{
            obj3 = 1e3*(codif.A.length*(codif.A.length+codif.A.length));
        }*/
            
        
        double fitness = obj1+obj2+obj3+obj4+obj5;
        set(fitness);
    }

    @Override
    public void copy(MDFactoryCpx prob, MDObjectiveCpx source) throws Exception {
        super.copy(prob, source);
        this.obj1 = source.obj1;
        this.obj2 = source.obj2;
        this.obj3 = source.obj3;
        this.obj4 = source.obj4;
        this.obj5 = source.obj5;
        
    }
    
    @Override
    public MDObjectiveCpx build(MDFactoryCpx mem) throws Exception {
        return new MDObjectiveCpx();
    }

    @Override
    public void printer(MDFactoryCpx prob, StreamPrinter com, MDCodificationCpx codif) throws Exception {
        super.printer(prob, com, codif);
        com.printDbl("extra", obj1);
        com.printDbl("solver", obj2);;
        com.printDbl("acumulado", obj3);
        com.printDbl("max", obj5);
        com.printDbl("penality", obj4);
        
        System.out.printf("--------------------------------[R = %d],[A|B]-------------------------------\n", codif.R);
        System.out.printf("objective = %g\n", abs_value());
        for(int i=0; i<codif.A.length; i++){
            System.out.printf("[ ");
            for(int j=0; j<codif.A[i].length; j++){
                System.out.printf("%10g ", Math.abs(codif.A[i][j])<0.0001 ? 0 : codif.A[i][j]);
            }
            System.out.printf(" | ");
            for(int j=0; j<codif.B[i].length; j++){
                System.out.printf("%10g ", Math.abs(codif.B[i][j])<0.0001 ? 0 : codif.B[i][j]);
            }
            System.out.printf(" ]\n");
        }
        System.out.printf("---------------------------------------------------------------------------\n", codif.R);
        for(int i=0; i<codif.A.length; i++){
            for(int j=0; j<codif.A[i].length; j++){
                System.out.printf(Locale.ENGLISH, "%g;", i>=codif.R+prob.inst.S || j>=codif.R+prob.inst.S ? 0 : codif.A[i][j]);
            }
            for(int j=0; j<codif.B[i].length; j++){
                System.out.printf(Locale.ENGLISH, "%g;", i>=codif.R+prob.inst.S ? 0 : codif.B[i][j]);
            }
            System.out.printf("\n");
        }
    }
    /*
    ==========================================================================================
        double fitness = codif.R * 1000;
        double S[] = new double[prob.inst.S+codif.R];
        for(int s=0; s<prob.inst.S; s++){
            S[s] = prob.inst.states[s][0];
        }
        for(int t=1; t<prob.inst.T; t++){
            double sum[] = new double[S.length];
            for(int s=0; s<S.length; s++){
                for(int j=0; j<S.length; j++){
                    sum[s] += codif.A[s][j] * S[j];
                }
            }
            for(int s=0; s<S.length; s++){
                for(int i=0; i<prob.inst.I; i++){
                    sum[s] += codif.B[s][i] * prob.inst.inputs[i][t-1];
                }
            }
            System.arraycopy(sum, 0, S, 0, S.length);
            for(int s=0; s<S.length; s++){
                fitness += (S[s]-prob.inst.states[s][t])*(S[s]-prob.inst.states[s][t]);
            }
            if(fitness>1e9){
                fitness += (prob.inst.T-t)*1e9;
                break;
            }
        }
        set(fitness);
     ==========================================================================================
        double fitness = codif.R * 1000;
        double S[] = new double[prob.inst.S+codif.R];
        for(int s=0; s<prob.inst.S; s++){
            S[s] = prob.inst.states[s][0];
        }
        for(int t=1; t<prob.inst.T; t++){
            double sum[] = new double[S.length];
            for(int s=0; s<S.length; s++){
                for(int j=0; j<S.length; j++){
                    sum[s] += codif.A[s][j] * S[j];
                }
            }
            for(int s=0; s<S.length; s++){
                for(int i=0; i<prob.inst.I; i++){
                    sum[s] += codif.B[s][i] * prob.inst.inputs[i][t-1];
                }
            }
            System.arraycopy(sum, 0, S, 0, S.length);
            for(int s=0; s<prob.inst.S; s++){
                fitness += (S[s]-prob.inst.states[s][t])*(S[s]-prob.inst.states[s][t]);
            }
            for(int s=0; s<prob.inst.S; s++){
                S[s] = prob.inst.states[s][t];
            }
            if(fitness>1e9){
                fitness += (prob.inst.T-t)*1e9;
                break;
            }
        }
        set(fitness);
     */
}
