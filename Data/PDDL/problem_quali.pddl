(define (problem rover2)

    (:domain 
        rover-domain
    )
    
    (:objects

        region_1 region_2 region_3 region_4 - region

        base_1 base_2 base_3 - base

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

        ;; quanto d0 insumo o drone pode levar
        (= (input-capacity rover1) 1)
        
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

        

)
    
    (:goal
        (and 

            ;(pulverized region_1)  
            ;(pulverized region_2)
            ;(pulverized region_3)
            ;(pulverized region_4)
            
            
            (taken-image region_1)
            ;(taken-image region_2)
            ;(taken-image region_3)
            (taken-image region_4)
            ;(taken-image region_1_photo)
            ;(taken-image region_3_photo)
            ;(taken-image region_4_photo)    
            ;(been-at rover1 base_2)
            (at rover1 region_1)
            )
    )
    
    (:metric 
        minimize (total-time)
    )
)