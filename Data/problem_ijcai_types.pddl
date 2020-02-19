(define (problem rover-2)

    (:domain 
        rover-domain
    )
    
    (:objects

        region_1 region_2 region_3 region_4 - region

        base_1 base_2 base_3 - base


        input1 input2 input3 input4 - input
        
        orange_objective1 orange_objective2 orange_objective3 
        green_objective1  green_objective2  green_objective3  
        blue_objective1   blue_objective2  
        purple_objective1 - objective

        plantation_1_photo  region_2_photo - photo
        region_3_photo region_4_photo - photo

        camera1 - camera

        rover1 - rover
    )
    
    (:init
        (at rover1 base_1)
        
        (= (battery-capacity rover1) 100)
        (= (velocity rover1) 7)
        (= (battery-amount rover1) 100)
        (= (recharge-rate-battery rover1) 20) 
        (= (discharge-rate-battery rover1) 0.01) 
        (= (input-amount rover1) 0)
        (= (input-per-flight rover1) 1)

        ;; quanto de cada insumo o drone pode levar
        (= (input-capacity rover1 input1) 10)
        (= (input-capacity rover1 input2) 8)        
        (= (input-capacity rover1 input3) 6)
        (= (input-capacity rover1 input4) 4)

        ;; distancias de região pra região
        (= (distance region_1 region_2) 2800) (= (distance region_2 region_1) 2800)
        (= (distance region_1 region_3) 1900) (= (distance region_3 region_1) 1900)
        (= (distance region_1 region_4) 4500) (= (distance region_4 region_1) 4500)

        (= (distance region_2 region_3) 4400) (= (distance region_3 region_2) 4400)
        (= (distance region_2 region_4) 4200) (= (distance region_4 region_2) 4200)

        (= (distance region_3 region_4) 4200) (= (distance region_4 region_3) 4200)

        ;; distancias de região pra base_

        (= (distance base_1 region_1)  600)   (= (distance region_1 base_1)  600)
        (= (distance base_1 region_2) 2500)   (= (distance region_2 base_1) 2500)
        (= (distance base_1 region_3) 2000)   (= (distance region_3 base_1) 2000)
        (= (distance base_1 region_4) 4500)   (= (distance region_4 base_1) 4500)

        (= (distance base_2 region_1) 2500) (= (distance region_1 base_2) 2500)
        (= (distance base_2 region_2) 5200) (= (distance region_2 base_2) 5200)
        (= (distance base_2 region_3)  900) (= (distance region_3 base_2)  900)
        (= (distance base_2 region_4) 5000) (= (distance region_4 base_2) 5000)
;
        (= (distance base_3 region_1) 3600) (= (distance region_1 base_3) 3600)
        (= (distance base_3 region_2) 3300) (= (distance region_2 base_3) 3300)
        (= (distance base_3 region_3) 3600) (= (distance region_3 base_3) 3600)
        (= (distance base_3 region_4) 1000) (= (distance region_4 base_3) 1000)

        ;; distancias de base_ pra base_

        (= (distance base_1 base_2) 2800) (= (distance base_2 base_1) 2800)
        (= (distance base_1 base_3) 3000) (= (distance base_3 base_1) 3000)

        (= (distance base_2 base_3) 4200) (= (distance base_3 base_2) 4200)

        
        ;; relação de objetivos de foto com regioes
        (is-visible plantation_1_photo region_1)    (is-visible region_2_photo region_2)    
        (is-visible region_3_photo region_3)    (is-visible region_4_photo region_4) 
 

        ;; relação entre regioes e objetivos de pulverização
        (is-in orange_objective1 region_1 )   (is-in orange_objective2 region_2 )   (is-in orange_objective3 region_4 )
        (is-in green_objective1  region_2 )   (is-in green_objective2  region_1 )   (is-in green_objective3  region_3 )
        (is-in blue_objective1   region_3 )   (is-in blue_objective2   region_4 )         
        (is-in purple_objective1 region_2 )

)
    
    (:goal
        (and 

            ;(pulverized input1 orange_objective1)  ; (pulverized input1 orange_objective2)   ;(pulverized input1 orange_objective3)
            ;(pulverized input2 green_objective1 )   ;(pulverized input2 green_objective2 )   ;(pulverized input2 green_objective3 )
            ;(pulverized input3 blue_objective1  )   (pulverized input3 blue_objective2)        
            ;(pulverized input4 purple_objective1)
            

            ;(taken-image plantation_1_photo)
            ;(taken-image region_2_photo)
            ;(taken-image region_3_photo)
            ;(taken-image region_4_photo)    
            
            (at rover1 base_2))
    )
    
    (:metric 
        minimize (total-time)
    )
)