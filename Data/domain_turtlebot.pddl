(define (domain rover-domain)

    (:requirements :durative-actions :fluents :duration-inequalities)

    (:functions
    	
    	;; Variavel q controla bateria
        (battery-amount ?rover)
        ;; quantidade de insumo
        (input-amount ?rover)
        ;;velocidade de carregar a bateria
        (recharge-rate-battery ?rover)
        ;;velocidade de descarregar a bateria
        (discharge-rate-battery ?rover)
        ;;capacidade maxima bateria
        (battery-capacity ?rover)
        ;;capacidade maxima de insumo
        (input-capacity ?rover ?input)
        ;;quantidade de insumo por voo
        (input-per-flight ?rover)
        ;;velocidade de reabastecer o insumo
        (recharge-rate-input ?rover ?input)
        ;;distancia entre regioes
        (distance ?from-region ?to-region)
        ;;velocidade
        (velocity ?rover)
    )

    (:predicates
    	;;relacão do objetivo de foto com a região
        (is-visible ?objective ?region)  
        ;;relacao do objetivo de pulverização com a regiao
        (is-in ?objective ?region)
        ;;se ja visitou
        (been-at ?rover ?region)
        ;;se esta carregando um insumo
        (carry ?rover ?input)  
        ;;esta em uma regiao
        (at ?rover ?region)
        ;;se é uma base de carregar
        (is-recharging-dock ?region)
   ;******************
        (is-dropping-dock ?region)
        ;; se pode pulverizar
        (can-spray ?drone)
        ;;se pode carregar/descarregar
        (can-recharge ?drone)

        ;se já tirou a foto
        (taken-image ?objective)
        ;se pulverizou
        (pulverized ?input ?objective)

        (objective ?objective)
        (region ?region)    
        (input ?input) 
        (rover ?rover)
        (usable ?payload)
        (payload ?payload)   
        (base ?ragion)          
    )
         
    (:durative-action go-to
        :parameters 
            (?rover
             ?from-region 
             ?to-region)
        
        :duration 
            (= ?duration (/ (distance ?from-region ?to-region)
                			(velocity ?rover))
            )
        
        :condition
            (and 
                (at start (rover ?rover)) 
                (at start (region ?from-region)) 
                (at start (region ?to-region)) 
                (at start (at ?rover ?from-region)) 
                (at start (can-spray ?rover))
                (at start (> (battery-amount ?rover) 
                	(*
                		(/
                			(distance ?from-region ?to-region)
                			(velocity ?rover)
                		)
                		(discharge-rate-battery ?rover)
                	)
                	)
                )
            )
                
        :effect
            (and 
                (at end (at ?rover ?to-region))
                (at end (been-at ?rover ?to-region))
                (at start (not (at ?rover ?from-region))) 
                (at start (decrease (battery-amount ?rover) 
			                	(*
			                		(/
			                			(distance ?from-region ?to-region)
			                			(velocity ?rover)
			                		)
			                		(discharge-rate-battery ?rover)
			                	)
                		)
                )
            )
    )

    (:durative-action recharge-input
        :parameters 
            (?rover 
             ?input 
             ?region)
        
        :duration 
            (= ?duration 8)
        
        :condition
            (and 
            	;(at start (can-recharge ?drone))
                (at start (rover ?rover)) 
                (at start (region ?region)) 
                (over all (at ?rover ?region)) 
                (over all (base ?region)) 
         ;*********************************************************
         		
                ;;no tem insumo quantidade de insumo = 0
                (at start (= (input-amount ?rover) 0))
                ;; não estou carregando nenhum insumo 
               ;; (at start (not (carry ?rover ?input)))
            )
                
        :effect
            (and 
            	;; eu estou com o insumo
                (at end (carry ?rover ?input))  
                ;; qquantidade de insumo por voo = 0              
                ;(at start (assign (input-per-flight ?rover) 0))        ;; drone carregando insumo   
                (at start (assign (input-amount ?rover) (input-capacity ?rover ?input)))
                (at end (can-spray ?rover))
            )    
    )

    (:durative-action discharge-input
        :parameters 
            (?rover 
             ?input 
             ?region)
        
        :duration 
            (= ?duration 8)
        
        :condition
            (and 
                (at start (rover ?rover)) 
                (at start (region ?region)) 
                (over all (at ?rover ?region)) 
                (over all (base ?region)) 
                ;(at start (= (input-per-flight ?rover) 0))
                (at start (> (input-amount ?rover) 0))
                (at start (carry ?rover ?input))
			)                
        :effect
            (and 
            	;(at start (not (can-recharge ?drone)))
                (at end (not (carry ?rover ?input)))                
                (at start (assign (input-amount ?rover) 0))  
                ;(at end (can-recharge)) 
            )

    )

    (:durative-action clean-camera
        :parameters 
            (?rover
             ?region
             ?payload)
        
        :duration 
            (= ?duration 8)
        
        :condition
            (and 
                (at start (rover ?rover)) 
                (at start (region ?region)) 
                (at start (base ?region))
                (at start (payload ?payload))
                (over all (at ?rover ?region)) 
            )
                
        :effect
                (at start (usable ?payload))                
    )

    (:durative-action pulverize-region
        :parameters 
            (?rover
             ?input
             ?region
             ?objective
             ?payload)
        
        :duration 
            (= ?duration 100)
        
        :condition
            (and
                (at start (rover ?rover))
                (at start (can-spray ?rover))
                (at start (input ?input))
                (at start (is-in ?objective ?region))
                (at start (region ?region))
                (at start (payload ?payload))
                (at start (at ?rover ?region))
                (at start (carry ?rover ?input))
                (at start (> (battery-amount ?rover) 2)))
        
        :effect 
            (and 
            	(at start (not (can-spray ?rover)))
                (at start (not (usable ?payload)))
                (at end (pulverized ?input ?objective))
                (at start (decrease (battery-amount ?rover) 2))
                (at end (decrease (input-amount ?rover) 1))
                (at end (can-spray ?rover))
            )
      
    )

    (:durative-action take_image
        :parameters 
            (?rover
             ?objective 
             ?region
             ?payload)
            
        :duration 
            (= ?duration 7)
            
        :condition
            (and 
                (at start (rover ?rover))
                (at start (objective ?objective))
                (at start (region ?region))
                (at start (payload ?payload))
                (at start (usable ?payload))
                (at start (at ?rover ?region))
                (over all (at ?rover ?region))
                (over all (is-visible ?objective ?region))
                (at start (> (battery-amount ?rover) 1)))
     
        :effect
            (and 
                (at end (taken-image ?objective))
                (at start (decrease (battery-amount ?rover) 1)))            
    )
    
    (:durative-action recharge-battery
        :parameters 
            (?rover
             ?region)
        
        :duration 
            (= ?duration 
                (/ (- (battery-capacity ?rover) (battery-amount ?rover)) (recharge-rate-battery ?rover)))
        
        :condition
            (and 
                (at start (rover ?rover)) 
                (at start (region ?region)) 
                (at start (at ?rover ?region))
                (over all (base ?region)) 
                (at start (is-recharging-dock ?region)) 
                (at start (< (battery-amount ?rover) 80))
            )
     
        :effect
            (and
            	(at start (can-recharge ?drone))
            	(at end 
                (increase (battery-amount ?rover) 
                    (* ?duration (recharge-rate-battery ?rover))))
            )
    )
)