/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.method;

import ProOF.gen.operator.gCrossover;
import ProOF.gen.operator.gMutation;
import ProOF.gen.operator.oCrossover;
import ProOF.gen.operator.oInitialization;
import ProOF.gen.operator.oMutation;
import ProOF.apl.factorys.fStop;
import ProOF.apl.factorys.fProblem;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.opt.abst.run.MetaHeuristic;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.Solution;
import ProOF.gen.stopping.Stop;
import ProOF.utilities.uTournament;


/**
 *
 * @author marcio
 */
public class GeneticAlgorithm extends MetaHeuristic{
    private int pop_size;
    private int tour_size;
    private double mut_rate;
    
    private Problem problem;
    private Stop stop;
    private oInitialization init;
    private oCrossover[] cross;
    private oMutation[] mut;
    //private cIteration generations;

    public GeneticAlgorithm() {
        //generations = cIteration.object();
    }
    @Override
    public String name() {
        return "Genetic Algorithm";
    }
    @Override
    public void parameters(LinkerParameters win) throws Exception {
        pop_size  = win.Int("population size",  100,  10, 10000);
        tour_size = win.Int("tournament size",    3,   2, 16   );
        mut_rate  = win.Dbl("mutation rate"  , 0.10,   0, 1    );
    }
    @Override
    public void services(LinkerApproaches com) throws Exception {
        //com.add(generations);
        problem = com.get(fProblem.obj, problem);
        stop    = com.get(fStop.obj, stop);
        init    = com.need(oInitialization.class, init);
        cross   = com.needs(oCrossover.class, new oCrossover[1]);
        mut     = com.needs(oMutation.class, new oMutation[1]);       
    }
    @Override
    public void execute() throws Exception {
        //Definie gerenciadores para os operadores
        gCrossover op_cross = new gCrossover(problem, cross);
        gMutation op_mut = new gMutation(problem, mut);
        
        //Declara a população e faz alocação de mémoria
        Solution pop[] = new Solution[pop_size];
        for(int i=0; i<pop.length; i++){
            pop[i] = problem.build_sol();
        }
        
        //Cria o gerenciado de seleção
        uTournament tour = new uTournament(pop, tour_size);
        
        //Inicia e avalia a população;
        for(Solution ind : pop){
            init.initialize(problem, ind.codif());
            problem.evaluate(ind);
        }
        do{
            //for(int i=0; i<pop.length; i++){
                //Seleção
                int p1 = tour.select();
                int p2 = tour.select();
                
                //Crossover
                Solution child = op_cross.crossover(pop[p1], pop[p2]);
                
                //Mutação
                if(Math.random() < mut_rate){
                    op_mut.mutation(child);
                }
                
                //Avaliação
                problem.evaluate(child);
                
                //Inserir: substitui o pior pai
                int worse = pop[p1].GT(pop[p2]) ? p1 : p2;
                pop[worse] = child;
            //}
            //Atualiza o contador de gerações
            //generations.iteration();
        }while(!stop.end());
    }
    @Override
    public void results(LinkerResults win) throws Exception {
        //win.writeLong("generations", generations.value());
    }
}
