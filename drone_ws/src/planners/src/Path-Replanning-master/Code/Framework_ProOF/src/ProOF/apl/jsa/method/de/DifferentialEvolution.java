/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ProOF.apl.jsa.method.de;

import ProOF.apl.factorys.fProblem;
import ProOF.apl.factorys.fStop;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.gen.operator.Initialization;
import ProOF.gen.stopping.Stop;
import ProOF.gen.stopping.CountIteration;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.Solution;
import ProOF.opt.abst.run.MetaHeuristic;
import ProOF.utilities.uTournament;
import java.util.Random;

/**
 * Classe que modela um algoritmo Differential Evolution. 
 * Independente de problema associado.
 * @author Jesimar
 */
public class DifferentialEvolution extends MetaHeuristic{

    private Problem problem;
    private Stop stop;
    private Initialization init;
    private Recombine recombine;
    private Disturbance disturbance;
    private Difference difference;    
    private CountIteration iter;
    
    private int rateCrossover;
    private int weightDifferential;
    private int sizeTournament;         
    private int sizePopulation;
    
    @Override
    public String name() {
        return "jDE_PPCCS";
    }
    
    @Override
    public void services(LinkerApproaches link) throws Exception {
        iter = link.add(CountIteration.obj);
        problem = link.get(fProblem.obj, problem);
        stop = link.get(fStop.obj, stop);
        init = link.add(Initialization.obj);
        recombine = link.add(Recombine.obj);
        disturbance = link.add(Disturbance.obj);        
        difference = link.add(Difference.obj);  
    }

    @Override
    public void parameters(LinkerParameters link) throws Exception {
        super.parameters(link);
        sizePopulation = link.Int("Size Population", 100, 1, 100000);
        sizeTournament = link.Int("Size Tournament",   3, 1, 32);
        rateCrossover = link.Int("Rate Crossover",    70, 0, 100);
        weightDifferential = link.Int("Weight Differential",     50, 0, 100);
    }

    @Override
    public void execute() throws Exception {
        long timeInitial = System.currentTimeMillis();
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
        int i = 0;
//        int generation = 0;
        while (!stop.end()){
            uTournament tournament = new uTournament(pop, sizeTournament);            
            
            int idIndTarget = tournament.select();
            int idInd1;
            int idInd2;
            int idInd3;
            do{
                idInd1 = new Random().nextInt(sizePopulation);
            } while (idInd1 == i);
            do{
                idInd2 = new Random().nextInt(sizePopulation);
            } while (idInd2 == i || idInd2 == idInd1);
            do{
                idInd3 = new Random().nextInt(sizePopulation);            
            } while (idInd3 == i || idInd3 == idInd2 || idInd3 == idInd1);
            
            Solution indTarget = pop[idIndTarget];
            Solution ind1 = pop[idInd1];
            Solution ind2 = pop[idInd2];
            Solution ind3 = pop[idInd3];
            
            Solution indDifference = difference.difference(ind1, ind2);
            Solution indMutation = disturbance.disturbance(ind3, indDifference, weightDifferential);
            Solution indCrossover = recombine.recombine(indMutation, indTarget, rateCrossover);
            problem.evaluate(ind1);
            problem.evaluate(ind2);
            problem.evaluate(ind3);
            problem.evaluate(indTarget);
            problem.evaluate(indCrossover);
            int idPior = pop[idInd1].GE(pop[idInd2]) ? idInd1 : idInd2;
            idPior = pop[idPior].GE(pop[idInd3]) ? idPior : idInd3;
            idPior = pop[idPior].GE(pop[idIndTarget]) ? idPior : idIndTarget;
            pop[idPior] = indCrossover;               
            
//            if (i + 1 == sizePopulation){
//                int idBest = 0;
//                for (int j = 1; j < sizePopulation ; j++){
//                    idBest = pop[idBest].LE(pop[j]) ? idBest : j;
//                }
//                long timeFinal = System.currentTimeMillis();
//                long timeActual = timeFinal - timeInitial;
//                System.out.println("#generation$" + generation);
//                System.out.println("#time$" + timeActual);
//                System.out.println("#objective$" + pop[idBest]);
//                generation++;
//            }
            
            i = (i + 1) % sizePopulation;
            iter.iteration();
        }
        System.out.println("Finishing DE");
    }   
    
    /**
     * Algoritmo Evolução Diferencial.
        1. Inicializar parâmetros;
        2. Inicializar população inicial randomicamente;
        3. Avaliar população;
        4. Repetir até que critério de parada seja satisfeito;
            4.1 Selecionar randomicamente 3 indivíduos;
            4.2 Aplicar fator de diferenciação, mutação, crossover;
            4.3 Comparar o indivíduo atual com o indivíduo gerado e selecionar
                o que possui melhor fitness para população atual;
            4.4 Avaliar o indivíduo selecionado;
            4.5 Selecionar nova população;
        5. Fim (da repetição).
     */
}
