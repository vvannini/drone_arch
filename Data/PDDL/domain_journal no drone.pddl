(define (domain quali)

    (:requirements :typing :durative-actions :fluents :duration-inequalities :strips  :disjunctive-preconditions :action-costs)

    (:types
        region - object
        base - region)

    (:functions
        
        ;; Variavel q controla bateria em porcentagem
        (battery-amount)
        ;; quantidade de insumo
        (input-amount)
        ;;velocidade de carregar a bateria em porcentagem por segundos
        (recharge-rate-battery)
        ;;velocidade de descarregar a bateria
        (discharge-rate-battery)
        ;;capacidade maxima bateria
        (battery-capacity)
        ;;capacidade maxima de insumo
        (input-capacity)
        ; ;;quantidade de insumo por voo
        ; (input-per-flight ?rover - rover)
        ;;velocidade de reabastecer o insumo
        (recharge-rate-input)
        ;;distancia entre regioes em metros
        (distance ?from-region - region ?to-region - region)
        ;;velocidade em m/s
        (velocity)

        (total-goals)
        (goals-achived)
    )

    (:predicates
        ; ;;relacão do objetivo de foto com a região
     ;    (is-visible ?objective - objective ?region - region)  
     ;    ;;relacao do objetivo de pulverização com a regiao
     ;    (is-in ?objective - objective ?region - region)
        ;;se ja visitou
        (been-at ?region - region)
        ;;se esta carregando um insumo
        (carry)  
        ;;esta em uma regiao
        (at ?region - region)
        ;;se é uma base de carregar
        (is-recharging-dock ?region - region)
        ;; se pode pulverizar
        (can-spray)
        ;;se pode carregar/descarregar
        (can-recharge)

        ;se já tirou a foto
        (taken-image ?region - region)
        ;se pulverizou
        (pulverized ?region - region)
        (can-go)
        (can-take-pic)
        (its-not-base ?region - region)
        (hw-ready)

        (can-go-to-base)
        (has-pulverize-goal)
        (has-picture-goal)
        (at-move)

        
    )

    (:durative-action clean_camera
        :parameters 
            (?region - base)
        
        :duration 
            (= ?duration 8)
        
        :condition
            (and 
                (over all (at ?region)) 
            )
                
        :effect
                (at start (can-take-pic))                
    )
    
    (:durative-action recharge_input
        :parameters 
             (?region - base )
        
        :duration 
            (= ?duration 8)
        
        :condition
            (and 
                
                (over all (at ?region)) 
                
                ;;no tem insumo quantidade de insumo = 0
                (at start (= (input-amount) 0))
               
            )
                
        :effect
            (and 
                ;; eu estou com o insumo
                (at end (carry))   
                (at start (assign (input-amount) (input-capacity)))
                (at end (can-spray))
            )    
    )

    (:durative-action go_to
        :parameters 
            (?from-region - region 
             ?to-region - region)
        
        :duration ;(= ?duration 1)
            (= ?duration (/ (distance ?from-region ?to-region)
                            (velocity))
            )
        
        :condition
            (and 
                (over all (its-not-base ?to-region))
                (over all (can-go))
                (at start (at  ?from-region)) 
                (at start (hw-ready))
                ; (at start (> (battery-amount ?rover) 
                ;   (*
                ;       (/
                ;           (distance ?from-region ?to-region)
                ;           (velocity ?rover)
                ;       )
                ;       (discharge-rate-battery ?rover)
                ;   )
                ;   )
                ; )
            )
                
        :effect
            (and 
                (at end (not (at-move)))
                (at end (at ?to-region))
                (at end (been-at ?to-region))
                (at start (at-move))
                (at start (not (at ?from-region))) 
                (at start (decrease (battery-amount) 
                                (*
                                    (/
                                        (distance ?from-region ?to-region)
                                        (velocity)
                                    )
                                    (discharge-rate-battery)
                                )
                        )
                )
                ;(at start (increase (total-cost) 1))
            )
    )

    (:durative-action need_battery
        :parameters ()
        :duration (= ?duration 1)
        :condition (and (at start (< (battery-amount) 15)))
        :effect 
            (and 
                (at start (can-go-to-base))
                ; (at start (not (hw-ready ?rover)))
            )
    )

    (:durative-action need_input
        :parameters ()
        :duration (= ?duration 1)
        :condition (and (at start (= (input-amount) 0))
                        (over all (has-pulverize-goal)))
        :effect (and (at start (can-go-to-base)))
    )

    ; (:durative-action need-to-clean-camera
    ;     :parameters (?rover - rover)
    ;     :duration (= ?duration 1)
    ;     :condition (and (at start (= (input-amount ?rover) 0))
    ;                     (over all (has-pulverize-goal)))
    ;     :effect (and (at start (can-go-to-base ?rover)))
    ; )

    (:durative-action has_all_goals_achived
        :parameters ()
        :duration (= ?duration 1)
        :condition (and (at start (= (total-goals) (goals-achived))))
        :effect (and (at start (can-go-to-base)))
    )


    (:durative-action go_to_base
        :parameters 
            (?from-region - region 
             ?to-region - base)
        
        :duration ;(= ?duration 1)
            (= ?duration (/ (distance ?from-region ?to-region)
                            (velocity))
            )
        
        :condition
            (and 
                (over all (can-go))
                (at start (at ?from-region)) 
                (at start (can-go-to-base))
            )
                
        :effect
            (and 
                (at end (at ?to-region))
                (at end (been-at ?to-region))
                (at start (not (at ?from-region))) 
                (at start (decrease (battery-amount) 
                                (*
                                    (/
                                        (distance ?from-region ?to-region)
                                        (velocity)
                                    )
                                    (discharge-rate-battery)
                                )
                        )
                )
                (at end (not (can-go-to-base)))
                ;(at start (increase (total-cost) 1))
            )
    )


    (:durative-action pulverize_region
        :parameters 
            (?region - region)
        
        :duration 
            (= ?duration (/ 314
                         (velocity)))
        
        :condition
            (and
                (at start (can-spray))
                (at start (at ?region))
                (over all (at ?region))
                (at start (carry))
                (at start (> (input-amount) 0))
                (at start (> (battery-amount)  
                                (*
                                    (/
                                        314
                                        (velocity)
                                    )
                                    (discharge-rate-battery)
                                )
                        ))   
            )
        :effect 
            (and 
                (at start (not (can-go)))
                (at start (not (can-spray)))
                (at start (not (can-take-pic)))
                (at end (pulverized ?region))
                (at start (decrease (battery-amount) 
                                (*
                                    (/
                                        314
                                        (velocity)
                                    )
                                    (discharge-rate-battery)
                                )
                        )
                )                
                (at end (decrease (input-amount) 1))
                (at end (can-spray))
                (at end (can-go))
                (at end (increase (goals-achived) 1))

            )
      
    )

    (:durative-action take_image
        :parameters 
            (?region - region)
            
        :duration 
            (= ?duration (/ 1000
                         (velocity)))
            
        :condition
            (and 
                (at start (can-take-pic))
                (at start (at ?region))
                (over all (at ?region))
                (at start (> (battery-amount) 
                                (*
                                    (/
                                        1000
                                        (velocity)
                                    )
                                    (discharge-rate-battery)
                                )
                        )
                ) 
            )
     
        :effect
            (and 
                (at start (not (can-go)))
                (at end (taken-image ?region))
                (at end (decrease (battery-amount) 
                                                (*
                                                    (/
                                                        1000
                                                        (velocity)
                                                    )
                                                    (discharge-rate-battery)
                                                )
                                        )
                                )
                (at end (can-go)) 
                (at end (increase (goals-achived) 1))
                ;(at end (not (can-go-to-base ?rover)))

            )            
    )
    
    (:durative-action recharge_battery
        :parameters 
            (?region - base
            )        
        :duration 
            (= ?duration 
                (/ (- (battery-capacity) (battery-amount)) (recharge-rate-battery)))
        
        :condition
            (and 

                (at start (at ?region))
                (over all (at ?region))
                (at start (< (battery-amount) 80))
            )
     
        :effect
            (and
                (at start (can-recharge))
                (at end 
                (increase (battery-amount) 
                    (* ?duration (recharge-rate-battery))))
            )
    )
    (:durative-action hw_is_ready
        :parameters 
            ()
        :duration 
            (= ?duration 1)
        :condition 
            (and
                ; (from-region ?from)
                ; (to-region ?to)
                (at start (> (battery-amount) 20))
                ; (at start (> (battery-amount) 
             ;              (*
             ;                  (/
             ;                      (distance ?from ?to)
             ;                      (velocity)
             ;                  )
             ;                  (discharge-rate-battery)
             ;              )
             ;     ))
            )
        :effect 
            (and
                (at start (hw-ready))
            )
            
    )

)