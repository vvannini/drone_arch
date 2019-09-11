/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ProOF.apl.jsa.method.sga;

import ProOF.apl.factorys.fProblem;
import ProOF.apl.factorys.fStop;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.gen.operator.Crossover;
import ProOF.gen.operator.Initialization;
import ProOF.gen.operator.Mutation;
import ProOF.gen.stopping.CountIteration;
import ProOF.gen.stopping.Stop;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.Solution;
import ProOF.opt.abst.run.MetaHeuristic;
import ProOF.utilities.uTournament;
import java.util.Random;

/**
 * Classe que modela um algoritmo genético. 
 * Independente de problema associado.
 * @author Jesimar
 */
public class SimpleGeneticAlgorithm extends MetaHeuristic{

    private Problem problem;
    private Stop stop;
    private Initialization init;
    private Crossover crossover;
    private Mutation mutation;
    
    private CountIteration iter;
    
    private int rateCrossover;
    private int rateMutation;
    private int sizeTournament;         
    private int sizePopulation;
    
    @Override
    public String name() {
        return "jSGA_PPCCS";
    }
    
    @Override
    public void services(LinkerApproaches link) throws Exception {
        iter = link.add(CountIteration.obj);
        problem = link.get(fProblem.obj, problem);
        stop = link.get(fStop.obj, stop);
        init = link.add(Initialization.obj);
        crossover = link.add(Crossover.obj);
        mutation = link.add(Mutation.obj);        
    }

    @Override
    public void parameters(LinkerParameters link) throws Exception {
        super.parameters(link);
        sizePopulation = link.Int("Size Population", 100, 1, 100000);
        sizeTournament = link.Int("Size Tournament",   3, 1, 32);
        rateCrossover = link.Int("Rate Crossover",    70, 0, 100);
        rateCrossover = link.Int("Rate Mutation",     10, 0, 100);
    }

    @Override
    public void execute() throws Exception {
        //Cria população de individuos
        Solution pop[] = new Solution[sizePopulation];        
        for (int i = 0 ; i < sizePopulation ; i++){
            pop[i] = problem.build_sol();
        }
        
        //Inicializa individuos
        for (int i = 0 ; i < sizePopulation ; i++){
            init.initialize(pop[i]);
        }
        
        //Avalia individuos (população)
        for (int i = 0 ; i < sizePopulation ; i++){
            problem.evaluate(pop[i]);
        }
        
        //Repita até criterio de parada ser atingido
        while (!stop.end()){                        
            uTournament tournament = new uTournament(pop, sizeTournament);
            Solution filho = null;
            int rnd1 = (int)(new Random().nextDouble() * 100);  
            int idPior;
            if (rnd1 < rateCrossover){
                //Seleciona dois Pais (Torneio)                
                int idPai1 = tournament.select();
                int idPai2 = tournament.select();

                //Individuo Filho = Aplica Cruzamento dos Pais
                filho = crossover.crossover(pop[idPai1], pop[idPai2]);
                
                idPior = pop[idPai1].GE(pop[idPai2]) ? idPai1 : idPai2;                
            }else{
                //Seleciona um Pai (Torneio)
                int idPai1 = tournament.select();
                //Individuo Filho = Aplica Cruzamento dos Pais
                filho = pop[idPai1].clone(problem);                
                
                idPior = idPai1;
            }         
            //Aplica Mutação no Filho
            int rnd2 = (int)(new Random().nextDouble() * 100);  
            if (rnd2 < rateMutation){
                mutation.mutation(filho);
            }
            
            //Avalia Individuo Filho
            problem.evaluate(filho);            
            //Coloca filho no lugar do pior pai                
            pop[idPior] = filho; 
            
            iter.iteration();
        }
    }
    
    /**
     * Pseudocodigo: 
     * 
     * Cria população de individuos;
     * Inicializa individuos;
     * Avalia individuos (população);
     * Repita até criterio de parada ser atingido{
     *      Seleciona dois Pais (Torneio);
     *      Individuo Filho = Aplica Cruzamento dos Pais;
     *      Aplica Mutação no Filho;
     *      Avalia Individuo Filho;
     *      Coloca filho no lugar do pior pai;         
     * }         
     */
}
