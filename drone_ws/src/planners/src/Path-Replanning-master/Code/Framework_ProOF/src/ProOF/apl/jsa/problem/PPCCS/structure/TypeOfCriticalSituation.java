/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ProOF.apl.jsa.problem.PPCCS.structure;

/**
 * Tipos de Situações Criticas possíveis do VANT.
 * @author jesimar
 */
public enum TypeOfCriticalSituation {
    
    /**
     * Falhas: 
     * 
     * NOTHING - Sem falhas Associadas ao VANT
     * 
     * FAIL_ENGINE - Falha Motor:
     * Altura: 152 Metros = 500 pés
     * Tempo de Voo: 2.5 minutos
     * 
     * FAIL_BATTERY - Falha Bateria: 
     * Altura: 152 Metros = 500 pés
     * Tempo de Voo: 5.0 minutos
     * 
     * FAIL_TURN_LEFT_ONLY - Falha Girar Direita: 
     * Altura: 152 Metros = 500 pés
     * Tempo de Voo: 5.0 minutos
     * Girar somente para esquerda
     * 
     * FAIL_TURN_RIGHT_ONLY - Falha Girar Esquerda: 
     * Altura: 152 Metros = 500 pés
     * Tempo de Voo: 5.0 minutos
     * Girar somente para esquerda
     * 
     * FAIL_AIRCRAFT_INTRUDER - Não é Falha apenas uma Aeronave se Aproxima
     * Altura: 152 Metros = 500 pés     
     * Distância da Aeronava intrusa: 5 Km
     * Não implementado ainda.
     */
    
    NOTHING, FAIL_ENGINE, FAIL_BATTERY, FAIL_TURN_LEFT_ONLY, 
    FAIL_TURN_RIGHT_ONLY, FAIL_AIRCRAFT_INTRUDER;
    
    public static TypeOfCriticalSituation getCriticalSituation(int value){        
        switch (value){
            case 0: return TypeOfCriticalSituation.NOTHING;
            case 1: return TypeOfCriticalSituation.FAIL_ENGINE;
            case 2: return TypeOfCriticalSituation.FAIL_BATTERY;
            case 3: return TypeOfCriticalSituation.FAIL_TURN_LEFT_ONLY;
            case 4: return TypeOfCriticalSituation.FAIL_TURN_RIGHT_ONLY;
            case 5: return TypeOfCriticalSituation.FAIL_AIRCRAFT_INTRUDER;
            default: return TypeOfCriticalSituation.NOTHING;
        }        
    }
    
}
