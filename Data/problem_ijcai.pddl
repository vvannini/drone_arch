(define (problem rover-2)

    (:domain 
        rover-domain
    )
    
    (:objects

        region1 region2 region3 region4 

        base1 base2 base3 


        input1 input2 input3 input4 
        
        orange_objective1 orange_objective2 orange_objective3
        green_objective1  green_objective2  green_objective3 
        blue_objective1   blue_objective2 
        purple_objective1

        camera1 

        rover1
    )
    
    (:init
        (rover rover1)
        ;(payload rover1 camera)
        (at rover1 base1)

        (payload camera1)

        (= (battery-capacity rover1) 100)
        (= (velocity rover1) 7)
        (= (battery-amount rover1) 0)
        (= (recharge-rate-battery rover1) 20) 
        (= (discharge-rate-battery rover1) 0.01) 
        (= (input-amount rover1) 0)
        (= (input-per-flight rover1) 1)
        ;(can-recharge drone1)

        ;; quanto de cada insumo o drone pode levar
        (= (input-capacity rover1 input1) 10)
        (= (input-capacity rover1 input2) 8)        
        (= (input-capacity rover1 input3) 6)
        (= (input-capacity rover1 input4) 4)

        ;; distancias de região pra região
        (= (distance region1 region2) 2800) (= (distance region2 region1) 2800)
        (= (distance region1 region3) 1900) (= (distance region3 region1) 1900)
        (= (distance region1 region4) 4500) (= (distance region4 region1) 4500)

        (= (distance region2 region3) 4400) (= (distance region3 region2) 4400)
        (= (distance region2 region4) 4200) (= (distance region4 region2) 4200)

        (= (distance region3 region4) 4200) (= (distance region4 region3) 4200)

        ;; distancias de região pra base

        (= (distance base1 region1)  600)   (= (distance region1 base1)  600)
        (= (distance base1 region2) 2500)   (= (distance region2 base1) 2500)
        (= (distance base1 region3) 2000)   (= (distance region3 base1) 2000)
        (= (distance base1 region4) 4500)   (= (distance region4 base1) 4500)

        (= (distance base2 region1) 2500) (= (distance region1 base2) 2500)
        (= (distance base2 region2) 5200) (= (distance region2 base2) 5200)
        (= (distance base2 region3)  900) (= (distance region3 base2)  900)
        (= (distance base2 region4) 5000) (= (distance region4 base2) 5000)

        (= (distance base3 region1) 3600) (= (distance region1 base3) 3600)
        (= (distance base3 region2) 3300) (= (distance region2 base3) 3300)
        (= (distance base3 region3) 3600) (= (distance region3 base3) 3600)
        (= (distance base3 region4) 1000) (= (distance region4 base3) 1000)

        ;; distancias de base pra base

        (= (distance base1 base2) 2800) (= (distance base2 base1) 2800)
        (= (distance base1 base3) 3000) (= (distance base3 base1) 3000)

        (= (distance base2 base3) 4200) (= (distance base3 base2) 4200)

        


        ;; regioes q podem ir de uma para outra
        (region region1)    (region region2)    (region region3)    (region region4) 
        (region base1)      (region base2)      (region base3)

        ;; definindo as bases

        (base base1)    (base base2)    (base base3)

        ;; objetivos de foto
        (objective region1_photo)    (objective region2_photo)    (objective region3_photo)    (objective region4_photo)

        ;; relação de objetivos de foto com regioes
        (is-visible region1_photo region1)    (is-visible region2_photo region2)    
        (is-visible region3_photo region3)    (is-visible region4_photo region4) 
 
        ;; tipos de insumos
        (input input1) (input input2) (input input3) (input input4)
        

        ;; objetivos de pulverização
        (objective orange_objective1)   (objective orange_objective2)   (objective orange_objective3)
        (objective green_objective1 )   (objective green_objective2 )   (objective green_objective3 )
        (objective blue_objective1  )   (objective blue_objective2)        
        (objective purple_objective1)
        

        ;; relação entre regioes e objetivos de pulverização
        (is-in orange_objective1 region1 )   (is-in orange_objective2 region2 )   (is-in orange_objective3 region4 )
        (is-in green_objective1  region1 )   (is-in green_objective2  region1 )   (is-in green_objective3  region3 )
        (is-in blue_objective1   region3 )   (is-in blue_objective2   region4 )         
        (is-in purple_objective1 region2 )

        ;(is-in input1 base1) (is-in input1 base2) (is-in input1 base3) 
        ;(is-in input2 base1) (is-in input2 base2) (is-in input2 base3)
        ;(is-in input3 base1) (is-in input3 base2) (is-in input3 base3)
        ;(is-in input4 base1) (is-in input4 base2) (is-in input4 base3)

        
        (is-recharging-dock base1)  (is-recharging-dock base2)  (is-recharging-dock base3)
        (is-dropping-dock base1)  (is-dropping-dock base2)  (is-dropping-dock base3)
    )
    
    (:goal
        (and 

            (pulverized input1 orange_objective1)   (pulverized input1 orange_objective2)   ;(pulverized input1 orange_objective3)
            (pulverized input2 green_objective1 )   (pulverized input2 green_objective2 )   ;(pulverized input2 green_objective3 )
            ;(pulverized input3 blue_objective1  )   (pulverized input3 blue_objective2)        
            ;(pulverized input4 purple_objective1)
            

            (taken-image region1_photo)
            (taken-image region2_photo)
            (taken-image region3_photo)
            (taken-image region4_photo)    
            
            (at rover1 base1))
    )
    
    (:metric 
        minimize (total-time)
    )
)