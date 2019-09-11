/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.PPCCS.util;

import ProOF.apl.UAV.PPCCS.data.FunctionObjective;

/**
 *
 * @author jesimar
 */
public class ConfigSimulation {
    
    //----------------------------Print-------------------------------//
    /*Printa informações da simulação*/
    public static final boolean PRINT = false;
    
    /*Printa informações para debug do modelo*/
    public static final boolean DEBUG_MODEL = true;
    
    /*Printa informações para debug da instancia*/
    public static final boolean DEBUG_INSTANCE = false;
    
    //----------------------------Paint-------------------------------//
    /*Plota o gráfico da simulação*/
    public static boolean PLOT = false;
    
    /*Plota as normas das regiões*/
    public static final boolean IS_PAINT_NORM = true;    
    
    public static final boolean IS_PAINT_TIME = false;
    
    public static final boolean IS_PAINT_HEIGHT = false;        
        
    //----------------------------SAVE-------------------------------//
    
    /*Salva um txt com a melhor rota final encontrada*/
    public static final boolean IS_SAVE_ROUTE_FINAL = true;
    
    /*Salva uma imagem com a melhor rota final encontrada*/
    public static final boolean IS_SAVE_IMAGE_SOLUTION_FINAL = false;
    
    /*Salva o modelo matematico nos metodos exatos usados*/
    public static final boolean IS_SAVE_MODEL_MATH = false;
    
    public static final boolean IS_SAVE_GIF = false;    
        
    //----------------------------Animation-------------------------------//    
    /*Exibir animação da evolução dos individuos [Deixa a simulação mais lenta]*/
    public static final boolean IS_SEE_ANIMATION = false;
    
    /*Tempo para exibir animação da evolução dos individuos*/
    public static final int TIME_SEE_ANIMATION = 1;
    
    /*Plotar o gráfico da rota a cada progresso do fitness*/
    public static final boolean IS_SEE_PLOT_FOR_PROGRESS = false;
    
    //----------------------------Path-------------------------------//
    /*Caminho de onde são salvos as imagens*/
    public static final String PATH_IMG = "/media/jesimar/Workspace/Work/results/";            
    
    public static final FunctionObjective FUNCTION = FunctionObjective.FUNCTION_2;
}
