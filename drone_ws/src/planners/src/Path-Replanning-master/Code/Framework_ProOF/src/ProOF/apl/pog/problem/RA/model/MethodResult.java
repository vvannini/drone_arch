package ProOF.apl.pog.problem.RA.model;

import ProOF.apl.pog.problem.RA.solvers.iRAModel;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.language.Approach;
import java.util.ArrayList;

/**
 * Modelo que contem uma solucao desejada para o PSSLSAN. Este modelo sera
 * construido com base na codificacao da melhor solucao, bem como o resultado
 * atingido pelo modelo reduzido.
 *
 * @author andre
 */
public class MethodResult extends Approach {

    public static MethodResult obj = new MethodResult();
    private RAInstance inst;
    /**
     * Estoque utilizado.
     */
    public int Iit[][];
    /**
     * Quantidade produzida.
     */
    public int Qit[][];
    /**
     * Backlog utilizado.
     */
    public int Bit[][];
    /**
     * Tempo de overtime gasto.
     */
    public double Ot[];
    /**
     * Sequencia de producao(Lista).
     */
    public ArrayList<Integer> Yt[];
    /**
     * Tempo de Setup gasto.
     */
    public double STt[];
    public double objective;
    public double LB;
    public double custoSetup;
    public double hold;
    public double overtime;
    public double backlog;

    @Override
    public String name() {
        return "Method Result";
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public void parameters(LinkerParameters link) throws Exception {
    }

    @Override
    public void load() throws Exception {
    }

    @Override
    public void services(LinkerApproaches link) throws Exception {
        this.inst = link.need(RAInstance.class, inst);
    }

    @Override
    public void start() throws Exception {
        this.Iit = new int[inst.N][inst.T];
        this.Qit = new int[inst.N][inst.T];
        this.Bit = new int[inst.N][inst.T];
        this.STt = new double[inst.T];
        this.Ot = new double[inst.T];
        this.Yt = new ArrayList[inst.T];
    }

    @Override
    public boolean validation(LinkerValidations link) throws Exception {
        return true;
    }
    public void setYt(ArrayList<Integer> Yt[]){
        for(int t = 0; t < inst.T; t++){
            this.Yt[t] = new ArrayList<Integer>(Yt[t]);
        }
    }
    @Override
    public void results(LinkerResults link) throws Exception {
        //imprimindo resultados
        System.out.println("-------------------------[Iit]----------------------");
        for (int i = 0; i < inst.N; i++) {
            for (int t = 0; t < inst.T; t++) {
                System.out.printf("%d;", Iit[i][t]);
            }
            System.out.println();
        }
        System.out.println("----------------------------------------------------");

        System.out.println("-------------------------[Bjt]----------------------");
        for (int i = 0; i < inst.N; i++) {
            for (int t = 0; t < inst.T; t++) {
                System.out.printf("%d;", Bit[i][t]);
            }
            System.out.println();
        }
        System.out.println("----------------------------------------------------");

        System.out.println("-------------------------[Qjt]----------------------");
        for (int i = 0; i < inst.N; i++) {
            for (int t = 0; t < inst.T; t++) {
                System.out.printf("%d;", Qit[i][t]);
            }
            System.out.println();
        }
        System.out.println("----------------------------------------------------");

        System.out.println("-------------------------[Ot]-----------------------");
        for (int t = 0; t < inst.T; t++) {
            System.out.printf("%g;", Ot[t]);
        }
        System.out.println();
        System.out.println("----------------------------------------------------");
        System.out.println("-------------------------[STt]----------------------");
        for (int t = 0; t < inst.T; t++) {
            System.out.printf("%g;", STt[t]);
        }
        System.out.println();
        System.out.println("----------------------------------------------------");
        
        //codificacao
        System.out.println("-------------------------[Yt]-----------------------");
        for (int t = 0; t < inst.T; t++) {
            for(int i : Yt[t]){
                System.out.printf("%d;", i);
            }
            System.out.println();
        }        
        System.out.println("----------------------------------------------------");
        
        //Resumo dos valores alcancados
        link.writeDbl("Objetivo", this.objective + custoSetup);
        link.writeDbl("LB", this.LB + custoSetup);
        link.writeDbl("SetupCost", this.custoSetup);
        link.writeDbl("Estoque", this.hold);
        link.writeDbl("OverTime", this.overtime);
        link.writeDbl("Backlog", this.backlog);
        //resumo impresso na tela
        System.out.println(String.format("%10s : %6g", "Objetivo", this.objective + custoSetup));
        System.out.println(String.format("%10s : %6g", "LB", this.LB + custoSetup));
        System.out.println(String.format("%10s : %6g", "Setup Cost", this.custoSetup));
        System.out.println(String.format("%10s : %6g", "Estoque", this.hold));
        System.out.println(String.format("%10s : %6g", "Overtime", this.overtime));
        System.out.println(String.format("%10s : %6g", "Backlog", this.backlog));
    }
}
