/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ProOF.apl.jsa.problem.PPCCS.util;

/**
 *
 * @author jesimar
 */
public class ConfigSimulation {
    
    //----------------------------Print-------------------------------//
    /*Printa informações da simulação*/
    public static final boolean PRINT = false;
    
    /*Printa informações para debug*/
    public static final boolean DEBUG = false;
    
    //----------------------------Paint-------------------------------//
    /*Plota o gráfico da simulação*/
    public static boolean PLOT = false;
    
    /*Inverte o eixo em Y. (Tem um Problema)*/
    public static final boolean INVERT_Y = false;
    
    /*Plota as normas das regiões*/
    public static final boolean IS_PAINT_NORM = false;
    
    /*Pinta informações da simulação*/
    public static final boolean IS_PAINT_INFORMATION = true;
    
    /*Pinta as legendas do mapa*/
    public static final boolean IS_PAINT_LEGEND = true;
    
    /*Pinta um ponto no centro das regiões*/
    public static final boolean IS_PAINT_CENTER_REGIONS = false;
    
    /*Pinta as linhas de fundo do mapa*/
    public static final boolean IS_PAINT_LINES_BACKGROUND = false;        
    
//    public static final boolean IS_PAINT_TIME = false;
    
//    public static final boolean IS_PAINT_NIVEL_MAP = true; 
    
    public static final boolean PAINT_MULT_ROUTE = false;
        
    //----------------------------SAVE-------------------------------//
    
    /*Salva um txt com a melhor rota final encontrada*/
    public static final boolean IS_SAVE_ROUTE_FINAL = true;
    
    /*Salva uma imagem com a melhor rota final encontrada*/
    public static final boolean IS_SAVE_IMAGE_SOLUTION_FINAL = false;
    
    /*Salva uma imagem com a melhor rota final encontrada*/
    public static final boolean IS_SAVE_OBJECT_NEW_ROUTE = false;
    
    /*Salva o modelo matematico nos metodos exatos usados*/
//    public static final boolean IS_SAVE_MODEL_MATH = false;
    
    public static final boolean IS_SAVE_GIF = false;
        
    //----------------------------Animation-------------------------------//    
    /*Exibir animação da evolução dos individuos [Deixa a simulação mais lenta]*/
    public static final boolean IS_SEE_ANIMATION = false;
    
    /*Tempo para exibir animação da evolução dos individuos*/
    public static final int TIME_SEE_ANIMATION = 1;
    
    /*Plotar o gráfico da rota a cada progresso do fitness*/
    public static final boolean IS_SEE_PLOT_FOR_PROGRESS = true;
    
    /*Plotar o gráfico da rota a cada avaliação da função*/
    public static final boolean IS_SEE_PLOT_FOR_EVALUATE = false;
    
    //----------------------------Path-------------------------------//
    /*Caminho de onde são salvos as imagens*/
    public static final String PATH_IMG = "/media/jesimar/Workspace/Work/results/";  
    
    //----------------------------Format-------------------------------//
    /*Formato para impressão das coordenadas geograficas*/
    public static final String FORMAT_GEOGRAPHIC = "%dº%d'%d\"%c";
    
    /*formato para impressão das coordenadas cartezianas*/
    public static final String FORMAT_CARTESIAN = "%1.0f m";
        
    //-------------------------Config Simulation---------------------------//
    /*Quantidade de tipos de regiões [n, p, b]*/
    public static int AMOUNT_REGION = 3;
    
    /*Quantidade de regiões do tipo N [zona não navegavel]*/
    public static int AMOUNT_REGION_N = 2;
    
    /*Quantidade de regiões do tipo P [zona penalizadora]*/
    public static int AMOUNT_REGION_P = 3;
    
    /*Quantidade de regiões do tipo B [zona bonificadora]*/
    public static int AMOUNT_REGION_B = 5;
    
    /*Custo associado a região do tipo N [zona não navegavel]*/
    public static final double CUST_REGION_N = 1.e5;
    
    /*Custo associado a região do tipo P [zona penalizadora]*/
    public static final double CUST_REGION_P = 8000;
    
    /*Custo associado a região do tipo B [zona bonificadora]*/
    public static final double CUST_REGION_B = -2000;
    
}
