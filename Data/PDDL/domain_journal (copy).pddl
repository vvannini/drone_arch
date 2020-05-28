(define (domain quali)

    (:requirements :typing :durative-actions :fluents :duration-inequalities :strips  :disjunctive-preconditions :action-costs)

    (:types
        rover
        region - object
        base - region)

    (:functions
    	
    	;; Variavel q controla bateria em porcentagem
        (battery-amount ?rover - rover)
        ;; quantidade de insumo
        (input-amount ?rover - rover)
        ;;velocidade de carregar a bateria em porcentagem por segundos
        (recharge-rate-battery ?rover - rover)
        ;;velocidade de descarregar a bateria
        (discharge-rate-battery ?rover - rover)
        ;;capacidade maxima bateria
        (battery-capacity ?rover - rover)
        ;;capacidade maxima de insumo
        (input-capacity ?rover - rover)
        ; ;;quantidade de insumo por voo
        ; (input-per-flight ?rover - rover)
        ;;velocidade de reabastecer o insumo
        (recharge-rate-input ?rover - rover)
        ;;distancia entre regioes em metros
        (distance ?from-region - region ?to-region - region)
        ;;velocidade em m/s
        (velocity ?rover - rover)

        (total-goals)
        (goals-achived)
    )

    (:predicates
    	; ;;relacão do objetivo de foto com a região
     ;    (is-visible ?objective - objective ?region - region)  
     ;    ;;relacao do objetivo de pulverização com a regiao
     ;    (is-in ?objective - objective ?region - region)
        ;;se ja visitou
        (been-at ?rover - rover ?region - region)
        ;;se esta carregando um insumo
        (carry ?rover - rover)  
        ;;esta em uma regiao
        (at ?rover - rover ?region - region)
        ;;se é uma base de carregar
        (is-recharging-dock ?region - region)
        ;; se pode pulverizar
        (can-spray ?rover - rover)
        ;;se pode carregar/descarregar
        (can-recharge ?rover - rover)

        ;se já tirou a foto
        (taken-image ?region - region)
        ;se pulverizou
        (pulverized ?region - region)
        (can-go ?rover - rover)
        (can-take-pic ?rover - rover)
        (its-not-base ?region - region)
        (hw-ready ?rover - rover ?from - region ?to - region)

        (can-go-to-base ?rover - rover)
        (has-pulverize-goal)
        (has-picture-goal)
        (at-move)

        
    )

    (:durative-action clean_camera
        :parameters 
            (?rover - rover
             ?region - base)
        
        :duration 
            (= ?duration 8)
        
        :condition
            (and 
                (over all (at ?rover ?region)) 
            )
                
        :effect
                (at start (can-take-pic ?rover))                
    )
        

    (:durative-action go_to
        :parameters 
            (?rover - rover
             ?from-region - region 
             ?to-region - region)
        
        :duration ;(= ?duration 1)
            (= ?duration (/ (distance ?from-region ?to-region)
                			(velocity ?rover))
            )
        
        :condition
            (and 
                (over all (its-not-base ?to-region))
                (over all (can-go ?rover))
                (at start (at ?rover ?from-region)) 
                (at start (hw-ready ?rover ?from-region ?to-region))
                ; (at start (> (battery-amount ?rover) 
                ; 	(*
                ; 		(/
                ; 			(distance ?from-region ?to-region)
                ; 			(velocity ?rover)
                ; 		)
                ; 		(discharge-rate-battery ?rover)
                ; 	)
                ; 	)
                ; )
            )
                
        :effect
            (and 
                (at end (not (at-move)))
                (at end (at ?rover ?to-region))
                (at end (been-at ?rover ?to-region))
                (at start (at-move))
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
                ;(at start (increase (total-cost) 1))
            )
    )

    (:durative-action need_battery
        :parameters (?rover - rover)
        :duration (= ?duration 1)
        :condition (and (at start (< (battery-amount ?rover) 15)))
        :effect 
        	(and 
	        	(at start (can-go-to-base ?rover))
	        	; (at start (not (hw-ready ?rover)))
        	)
    )

    (:durative-action need_input
        :parameters (?rover - rover)
        :duration (= ?duration 1)
        :condition (and (at start (= (input-amount ?rover) 0))
                        (over all (has-pulverize-goal)))
        :effect (and (at start (can-go-to-base ?rover)))
    )

    ; (:durative-action need-to-clean-camera
    ;     :parameters (?rover - rover)
    ;     :duration (= ?duration 1)
    ;     :condition (and (at start (= (input-amount ?rover) 0))
    ;                     (over all (has-pulverize-goal)))
    ;     :effect (and (at start (can-go-to-base ?rover)))
    ; )

    (:durative-action has_all_goals_achived
        :parameters (?rover - rover)
        :duration (= ?duration 1)
        :condition (and (at start (= (total-goals) (goals-achived))))
        :effect (and (at start (can-go-to-base ?rover)))
    )


    (:durative-action go_to_base
        :parameters 
            (?rover - rover
             ?from-region - region 
             ?to-region - base)
        
        :duration ;(= ?duration 1)
            (= ?duration (/ (distance ?from-region ?to-region)
                            (velocity ?rover))
            )
        
        :condition
            (and 
                (over all (can-go ?rover))
                (at start (at ?rover ?from-region)) 
                (at start (can-go-to-base ?rover))
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
                (at end (not (can-go-to-base ?rover)))
                ;(at start (increase (total-cost) 1))
            )
    )

    (:durative-action recharge_input
        :parameters 
            (?rover - rover
             ?region - base )
        
        :duration 
            (= ?duration 8)
        
        :condition
            (and 
            	
                (over all (at ?rover ?region)) 
                
                ;;no tem insumo quantidade de insumo = 0
                (at start (= (input-amount ?rover) 0))
               
            )
                
        :effect
            (and 
            	;; eu estou com o insumo
                (at end (carry ?rover))   
                (at start (assign (input-amount ?rover) (input-capacity ?rover)))
                (at end (can-spray ?rover))
            )    
    )

    

    (:durative-action pulverize_region
        :parameters 
            (?rover - rover
             ?region - region
             )
        
        :duration 
            (= ?duration (/ 314
                         (velocity ?rover)))
        
        :condition
            (and
                (at start (can-spray ?rover))
                (at start (at ?rover ?region))
                (over all (at ?rover ?region))
                (at start (carry ?rover))
                (at start (> (input-amount ?rover) 0))
                (at start (> (battery-amount ?rover)  
                                (*
                                    (/
                                        314
                                        (velocity ?rover)
                                    )
                                    (discharge-rate-battery ?rover)
                                )
                        ))   
            )
        :effect 
            (and 
                (at start (not (can-go ?rover)))
            	(at start (not (can-spray ?rover)))
                (at start (not (can-take-pic ?rover)))
                (at end (pulverized ?region))
                (at start (decrease (battery-amount ?rover) 
                                (*
                                    (/
                                        314
                                        (velocity ?rover)
                                    )
                                    (discharge-rate-battery ?rover)
                                )
                        )
                )                
                (at end (decrease (input-amount ?rover) 1))
                (at end (can-spray ?rover))
                (at end (can-go ?rover))
                (at end (increase (goals-achived) 1))

            )
      
    )

    (:durative-action take_image
        :parameters 
            (?rover - rover
             ?region - region)
            
        :duration 
            (= ?duration (/ 1000
                         (velocity ?rover)))
            
        :condition
            (and 
                (at start (can-take-pic ?rover))
                (at start (at ?rover ?region))
                (over all (at ?rover ?region))
                (at start (> (battery-amount ?rover) 
                                (*
                                    (/
                                        1000
                                        (velocity ?rover)
                                    )
                                    (discharge-rate-battery ?rover)
                                )
                        )
                ) 
            )
     
        :effect
            (and 
                (at start (not (can-go ?rover)))
                (at end (taken-image ?region))
                (at end (decrease (battery-amount ?rover) 
                                                (*
                                                    (/
                                                        1000
                                                        (velocity ?rover)
                                                    )
                                                    (discharge-rate-battery ?rover)
                                                )
                                        )
                                )
                (at end (can-go ?rover)) 
                (at end (increase (goals-achived) 1))
                ;(at end (not (can-go-to-base ?rover)))

            )            
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

                (at start (at ?rover ?region))
                (over all (at ?rover ?region))
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
    (:durative-action hw_is_ready
    	:parameters 
    		(?rover - rover
    			?from - region
    			?to - region
             )
    	:duration 
    		(= ?duration 1)
    	:condition 
    		(and
	    		;(at start (> (battery-amount ?rover) 20))
	    		(at start (> (battery-amount ?rover) 
		                	(*
		                		(/
		                			(distance ?from ?to)
		                			(velocity ?rover)
		                		)
		                		(discharge-rate-battery ?rover)
		                	)
		         ))
    		)
    	:effect 
    		(and
    			(at start (hw-ready ?rover ?from ?to))
    		)
    		
    )

)