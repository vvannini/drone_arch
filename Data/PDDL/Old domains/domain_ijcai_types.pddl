(define (domain rover-domain)

    (:requirements :typing :durative-actions :fluents :duration-inequalities :strips  :disjunctive-preconditions)

    (:types
        rover
        input
        region
        objective
        camera - object
        photo - objective
        base - region)

    (:functions
    	
    	;; Variavel q controla bateria
        (battery-amount ?rover - rover)
        ;; quantidade de insumo
        (input-amount ?rover - rover)
        ;;velocidade de carregar a bateria
        (recharge-rate-battery ?rover - rover)
        ;;velocidade de descarregar a bateria
        (discharge-rate-battery ?rover - rover)
        ;;capacidade maxima bateria
        (battery-capacity ?rover - rover)
        ;;capacidade maxima de insumo
        (input-capacity ?rover - rover ?input - input)
        ;;quantidade de insumo por voo
        (input-per-flight ?rover - rover)
        ;;velocidade de reabastecer o insumo
        (recharge-rate-input ?rover - rover ?input - input)
        ;;distancia entre regioes
        (distance ?from-region - region ?to-region - region)
        ;;velocidade
        (velocity ?rover - rover)
    )

    (:predicates
    	;;relacão do objetivo de foto com a região
        (is-visible ?objective - objective ?region - region)  
        ;;relacao do objetivo de pulverização com a regiao
        (is-in ?objective - objective ?region - region)
        ;;se ja visitou
        (been-at ?rover - rover ?region - region)
        ;;se esta carregando um insumo
        (carry ?rover - rover ?input - input)  
        ;;esta em uma regiao
        (at ?rover - rover ?region - region)
        ;;se é uma base de carregar
        (is-recharging-dock ?region - region)
   ;******************
       ; (is-dropping-dock ?region - region)
        ;; se pode pulverizar
        (can-spray ?rover - rover)
        ;;se pode carregar/descarregar
        (can-recharge ?rover - rover)

        ;se já tirou a foto
        (taken-image ?objective - objective)
        ;se pulverizou
        (pulverized ?input - input ?objective - objective)

        ;(objective ?objective)
        ;(region ?region)    
        ;(input ?input) 
        ;(rover ?rover)
        (usable ?camera - camera)
        ;(camera ?camera)   
        ;(base ?region)       
        ;(photo ?photo)   
    )
         
    (:durative-action go_to
        :parameters 
            (?rover - rover
             ?from-region - region 
             ?to-region - region)
        
        :duration (= ?duration 1)
            ; (= ?duration (/ (distance ?from-region ?to-region)
            ;     			(velocity ?rover))
            ; )
        
        :condition
            (and 
                ;(at start (rover ?rover)) 
                ;(at start (region ?from-region)) 
                ;(at start (region ?to-region)) 
                (at start (at ?rover ?from-region)) 
                ;(at start (can-spray ?rover))
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

    (:durative-action recharge_input
        :parameters 
            (?rover - rover
             ?input - input
             ?region - base )
        
        :duration 
            (= ?duration 8)
        
        :condition
            (and 
            	;(at start (can-recharge ?drone))
                ;(at start (rover ?rover)) 
                ;(at start (region ?region)) 
                (over all (at ?rover ?region)) 
                ;(over all (base ?region)) 
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

    (:durative-action discharge_input
        :parameters 
            (?rover - rover 
             ?input - input
             ?region - base)
        
        :duration 
            (= ?duration 8)
        
        :condition
            (and 
                ;(at start (rover ?rover)) 
                ;(at start (region ?region)) 
                ;(over all (at ?rover ?region)) 
                ;(over all (base ?region)) 
                ;(at start (= (input-per-flight ?rover) 0))
                (at start (> (input-amount ?rover) -1))
                ;(at start (carry ?rover ?input))
			)                
        :effect
            (and 
            	;(at start (not (can-recharge ?drone)))
                (at end (not (carry ?rover ?input)))                
                (at start (assign (input-amount ?rover) 0))  
                ;(at end (can-recharge)) 

            )

    )

    (:durative-action clean_camera
        :parameters 
            (?rover - rover
             ?region - base
             ?camera - camera)
        
        :duration 
            (= ?duration 8)
        
        :condition
            (and 
                ;(at start (rover ?rover)) 
                ;(at start (region ?region)) 
                ;(at start (base ?region))
                ;(at start (camera ?camera))
                (over all (at ?rover ?region)) 
            )
                
        :effect
                (at start (usable ?camera))                
    )

    (:durative-action pulverize_region
        :parameters 
            (?rover - rover
             ?input - input
             ?region - region
             ?objective - objective
             ?camera - camera)
        
        :duration 
            (= ?duration 100)
        
        :condition
            (and
                ;(at start (rover ?rover))
                (at start (can-spray ?rover))
                ;(at start (input ?input))
                (at start (is-in ?objective ?region))
                ;(at start (region ?region))
                ;(at start (camera ?camera))
                (at start (at ?rover ?region))
                (at start (carry ?rover ?input))
                (at start (> (battery-amount ?rover) 2)))
        
        :effect 
            (and 
            	(at start (not (can-spray ?rover)))
                (at start (not (usable ?camera)))
                (at end (pulverized ?input ?objective))
                (at start (decrease (battery-amount ?rover) 2))
                (at end (decrease (input-amount ?rover) 1))
                (at end (can-spray ?rover))
            )
      
    )

    (:durative-action take_image
        :parameters 
            (?rover - rover
             ?photo - photo
             ?region - region
             ?camera - camera)
            
        :duration 
            (= ?duration 7)
            
        :condition
            (and 
                ;(at start (rover ?rover))
                ;(at start (photo ?photo))
                ;(at start (region ?region))
                ;(at start (camera ?camera))
                (at start (usable ?camera))
                (at start (at ?rover ?region))
                (over all (at ?rover ?region))
                (over all (is-visible ?photo ?region))
                (at start (> (battery-amount ?rover) 1)))
     
        :effect
            (and 
                (at end (taken-image ?photo))
                (at start (decrease (battery-amount ?rover) 1)))            
    )
    
    (:durative-action recharge_battery
        :parameters 
            (?rover - rover
             ?region - base
            )        
        :duration 
            (= ?duration 
                (/ (- (battery-capacity ?rover) (battery-amount ?rover)) (recharge-rate-battery ?rover)))
        
        :condition
            (and 
                ;(at start (rover ?rover)) 
                ;(at start (region ?region)) 
                (at start (at ?rover ?region))
                ;(over all (base ?region)) 
                ;(at start (is-recharging-dock ?region)) 
                (at start (< (battery-amount ?rover) 80))
            )
     
        :effect
            (and
            	(at start (can-recharge ?rover))
            	(at end 
                (increase (battery-amount ?rover) 
                    (* ?duration (recharge-rate-battery ?rover))))
            )
    )
)