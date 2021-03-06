(define (problem task)
(:domain rover-domain)
(:objects
    kenny - rover
    input1 - input
    plantation_1 plantation_2 plantation_3 plantation_4 plantation_5 p2_pulverize_1 p3_pulverize_1 p3_pulverize_2 p5_pulverize_1 p5_pulverize_2 p5_pulverize_3 p5_pulverize_4 - region
    p2_pulverize_1_objective p3_pulverize_1_objective p3_pulverize_2_objective p5_pulverize_1_objective p5_pulverize_2_objective p5_pulverize_3_objective p5_pulverize_4_objective - objective
    camera1 - camera
    plantation_1_photo plantation_2_photo plantation_3_photo plantation_4_photo plantation_5_photo - photo
    base_1 base_2 base_3 - base
)
(:init
    (is-visible plantation_1_photo plantation_1)
    (is-visible plantation_1_photo plantation_2)
    (is-visible plantation_1_photo plantation_3)
    (is-visible plantation_1_photo plantation_4)
    (is-visible plantation_1_photo plantation_5)
    (is-visible plantation_2_photo plantation_1)
    (is-visible plantation_2_photo plantation_2)
    (is-visible plantation_2_photo plantation_3)
    (is-visible plantation_2_photo plantation_4)
    (is-visible plantation_2_photo plantation_5)
    (is-visible plantation_3_photo plantation_1)
    (is-visible plantation_3_photo plantation_2)
    (is-visible plantation_3_photo plantation_3)
    (is-visible plantation_3_photo plantation_4)
    (is-visible plantation_3_photo plantation_5)
    (is-visible plantation_4_photo plantation_1)
    (is-visible plantation_4_photo plantation_2)
    (is-visible plantation_4_photo plantation_3)
    (is-visible plantation_4_photo plantation_4)
    (is-visible plantation_4_photo plantation_5)
    (is-visible plantation_5_photo plantation_1)
    (is-visible plantation_5_photo plantation_2)
    (is-visible plantation_5_photo plantation_3)
    (is-visible plantation_5_photo plantation_4)
    (is-visible plantation_5_photo plantation_5)
    (is-visible p2_pulverize_1_objective p2_pulverize_1)
    (is-visible p2_pulverize_1_objective p3_pulverize_1)
    (is-visible p2_pulverize_1_objective p3_pulverize_2)
    (is-visible p2_pulverize_1_objective p5_pulverize_1)
    (is-visible p2_pulverize_1_objective p5_pulverize_2)
    (is-visible p2_pulverize_1_objective p5_pulverize_3)
    (is-visible p2_pulverize_1_objective p5_pulverize_4)
    (is-visible p3_pulverize_1_objective p2_pulverize_1)
    (is-visible p3_pulverize_1_objective p3_pulverize_1)
    (is-visible p3_pulverize_1_objective p3_pulverize_2)
    (is-visible p3_pulverize_1_objective p5_pulverize_1)
    (is-visible p3_pulverize_1_objective p5_pulverize_2)
    (is-visible p3_pulverize_1_objective p5_pulverize_3)
    (is-visible p3_pulverize_1_objective p5_pulverize_4)
    (is-visible p3_pulverize_2_objective p2_pulverize_1)
    (is-visible p3_pulverize_2_objective p3_pulverize_1)
    (is-visible p3_pulverize_2_objective p3_pulverize_2)
    (is-visible p3_pulverize_2_objective p5_pulverize_1)
    (is-visible p3_pulverize_2_objective p5_pulverize_2)
    (is-visible p3_pulverize_2_objective p5_pulverize_3)
    (is-visible p3_pulverize_2_objective p5_pulverize_4)
    (is-visible p5_pulverize_1_objective p2_pulverize_1)
    (is-visible p5_pulverize_1_objective p3_pulverize_1)
    (is-visible p5_pulverize_1_objective p3_pulverize_2)
    (is-visible p5_pulverize_1_objective p5_pulverize_1)
    (is-visible p5_pulverize_1_objective p5_pulverize_2)
    (is-visible p5_pulverize_1_objective p5_pulverize_3)
    (is-visible p5_pulverize_1_objective p5_pulverize_4)
    (is-visible p5_pulverize_2_objective p2_pulverize_1)
    (is-visible p5_pulverize_2_objective p3_pulverize_1)
    (is-visible p5_pulverize_2_objective p3_pulverize_2)
    (is-visible p5_pulverize_2_objective p5_pulverize_1)
    (is-visible p5_pulverize_2_objective p5_pulverize_2)
    (is-visible p5_pulverize_2_objective p5_pulverize_3)
    (is-visible p5_pulverize_2_objective p5_pulverize_4)
    (is-visible p5_pulverize_3_objective p2_pulverize_1)
    (is-visible p5_pulverize_3_objective p3_pulverize_1)
    (is-visible p5_pulverize_3_objective p3_pulverize_2)
    (is-visible p5_pulverize_3_objective p5_pulverize_1)
    (is-visible p5_pulverize_3_objective p5_pulverize_2)
    (is-visible p5_pulverize_3_objective p5_pulverize_3)
    (is-visible p5_pulverize_3_objective p5_pulverize_4)
    (is-visible p5_pulverize_4_objective p2_pulverize_1)
    (is-visible p5_pulverize_4_objective p3_pulverize_1)
    (is-visible p5_pulverize_4_objective p3_pulverize_2)
    (is-visible p5_pulverize_4_objective p5_pulverize_1)
    (is-visible p5_pulverize_4_objective p5_pulverize_2)
    (is-visible p5_pulverize_4_objective p5_pulverize_3)
    (is-visible p5_pulverize_4_objective p5_pulverize_4)



















    (= (distance plantation_1 plantation_2) 2129.9)
    (= (distance plantation_1 plantation_3) 1605.06)
    (= (distance plantation_1 plantation_4) 2047.12)
    (= (distance plantation_1 plantation_5) 2955.09)
    (= (distance plantation_1 p2_pulverize_1) 2338.85)
    (= (distance plantation_1 p3_pulverize_1) 1892.68)
    (= (distance plantation_1 p3_pulverize_2) 1845.13)
    (= (distance plantation_1 p5_pulverize_1) 3168.58)
    (= (distance plantation_1 p5_pulverize_2) 3488.59)
    (= (distance plantation_1 p5_pulverize_3) 3367.3)
    (= (distance plantation_1 p5_pulverize_4) 3543.82)
    (= (distance plantation_1 base_1) 2162.53)
    (= (distance plantation_1 base_2) 889.922)
    (= (distance plantation_1 base_3) 4110.86)
    (= (distance plantation_2 plantation_1) 2129.9)
    (= (distance plantation_2 plantation_3) 1353.17)
    (= (distance plantation_2 plantation_4) 2619.16)
    (= (distance plantation_2 plantation_5) 861.122)
    (= (distance plantation_2 p2_pulverize_1) 319.891)
    (= (distance plantation_2 p3_pulverize_1) 1500)
    (= (distance plantation_2 p3_pulverize_2) 1959.09)
    (= (distance plantation_2 p5_pulverize_1) 1061.57)
    (= (distance plantation_2 p5_pulverize_2) 1361.1)
    (= (distance plantation_2 p5_pulverize_3) 1241.05)
    (= (distance plantation_2 p5_pulverize_4) 1421.6)
    (= (distance plantation_2 base_1) 1242.79)
    (= (distance plantation_2 base_2) 1822.55)
    (= (distance plantation_2 base_3) 1996.97)
    (= (distance plantation_3 plantation_1) 1605.06)
    (= (distance plantation_3 plantation_2) 1353.17)
    (= (distance plantation_3 plantation_4) 1266.33)
    (= (distance plantation_3 plantation_5) 1850.87)
    (= (distance plantation_3 p2_pulverize_1) 1328.21)
    (= (distance plantation_3 p3_pulverize_1) 310.723)
    (= (distance plantation_3 p3_pulverize_2) 619.709)
    (= (distance plantation_3 p5_pulverize_1) 2059.63)
    (= (distance plantation_3 p5_pulverize_2) 2426.55)
    (= (distance plantation_3 p5_pulverize_3) 2307.89)
    (= (distance plantation_3 p5_pulverize_4) 2597.66)
    (= (distance plantation_3 base_1) 2287.23)
    (= (distance plantation_3 base_2) 812.939)
    (= (distance plantation_3 base_3) 3167.44)
    (= (distance plantation_4 plantation_1) 2047.12)
    (= (distance plantation_4 plantation_2) 2619.16)
    (= (distance plantation_4 plantation_3) 1266.33)
    (= (distance plantation_4 plantation_5) 3030.97)
    (= (distance plantation_4 p2_pulverize_1) 2570.79)
    (= (distance plantation_4 p3_pulverize_1) 1172.62)
    (= (distance plantation_4 p3_pulverize_2) 675.888)
    (= (distance plantation_4 p5_pulverize_1) 3223.98)
    (= (distance plantation_4 p5_pulverize_2) 3590.19)
    (= (distance plantation_4 p5_pulverize_3) 3478.65)
    (= (distance plantation_4 p5_pulverize_4) 3795.84)
    (= (distance plantation_4 base_1) 3475.81)
    (= (distance plantation_4 base_2) 1245.07)
    (= (distance plantation_4 base_3) 4347.43)
    (= (distance plantation_5 plantation_1) 2955.09)
    (= (distance plantation_5 plantation_2) 861.122)
    (= (distance plantation_5 plantation_3) 1850.87)
    (= (distance plantation_5 plantation_4) 3030.97)
    (= (distance plantation_5 p2_pulverize_1) 616.845)
    (= (distance plantation_5 p3_pulverize_1) 1860.06)
    (= (distance plantation_5 p3_pulverize_2) 2358.02)
    (= (distance plantation_5 p5_pulverize_1) 217.592)
    (= (distance plantation_5 p5_pulverize_2) 576.985)
    (= (distance plantation_5 p5_pulverize_3) 457.122)
    (= (distance plantation_5 p5_pulverize_4) 766.686)
    (= (distance plantation_5 base_1) 1874.81)
    (= (distance plantation_5 base_2) 2513.23)
    (= (distance plantation_5 base_3) 1320.47)
    (= (distance p2_pulverize_1 plantation_1) 2338.85)
    (= (distance p2_pulverize_1 plantation_2) 319.891)
    (= (distance p2_pulverize_1 plantation_3) 1328.21)
    (= (distance p2_pulverize_1 plantation_4) 2570.79)
    (= (distance p2_pulverize_1 plantation_5) 616.845)
    (= (distance p2_pulverize_1 p3_pulverize_1) 1410)
    (= (distance p2_pulverize_1 p3_pulverize_2) 1896.85)
    (= (distance p2_pulverize_1 p5_pulverize_1) 832.332)
    (= (distance p2_pulverize_1 p5_pulverize_2) 1171.59)
    (= (distance p2_pulverize_1 p5_pulverize_3) 1048.68)
    (= (distance p2_pulverize_1 p5_pulverize_4) 1292.23)
    (= (distance p2_pulverize_1 base_1) 1543.69)
    (= (distance p2_pulverize_1 base_2) 1920.62)
    (= (distance p2_pulverize_1 base_3) 1870.93)
    (= (distance p3_pulverize_1 plantation_1) 1892.68)
    (= (distance p3_pulverize_1 plantation_2) 1500)
    (= (distance p3_pulverize_1 plantation_3) 310.723)
    (= (distance p3_pulverize_1 plantation_4) 1172.62)
    (= (distance p3_pulverize_1 plantation_5) 1860.06)
    (= (distance p3_pulverize_1 p2_pulverize_1) 1410)
    (= (distance p3_pulverize_1 p3_pulverize_2) 498.006)
    (= (distance p3_pulverize_1 p5_pulverize_1) 2056.45)
    (= (distance p3_pulverize_1 p5_pulverize_2) 2423.9)
    (= (distance p3_pulverize_1 p5_pulverize_3) 2310.45)
    (= (distance p3_pulverize_1 p5_pulverize_4) 2623.83)
    (= (distance p3_pulverize_1 base_1) 2531.53)
    (= (distance p3_pulverize_1 base_2) 1056.65)
    (= (distance p3_pulverize_1 base_3) 3178.81)
    (= (distance p3_pulverize_2 plantation_1) 1845.13)
    (= (distance p3_pulverize_2 plantation_2) 1959.09)
    (= (distance p3_pulverize_2 plantation_3) 619.709)
    (= (distance p3_pulverize_2 plantation_4) 675.888)
    (= (distance p3_pulverize_2 plantation_5) 2358.02)
    (= (distance p3_pulverize_2 p2_pulverize_1) 1896.85)
    (= (distance p3_pulverize_2 p3_pulverize_1) 498.006)
    (= (distance p3_pulverize_2 p5_pulverize_1) 2553.67)
    (= (distance p3_pulverize_2 p5_pulverize_2) 2920.82)
    (= (distance p3_pulverize_2 p5_pulverize_3) 2807.89)
    (= (distance p3_pulverize_2 p5_pulverize_4) 3121.78)
    (= (distance p3_pulverize_2 base_1) 2895.66)
    (= (distance p3_pulverize_2 base_2) 955.274)
    (= (distance p3_pulverize_2 base_3) 3676.42)
    (= (distance p5_pulverize_1 plantation_1) 3168.58)
    (= (distance p5_pulverize_1 plantation_2) 1061.57)
    (= (distance p5_pulverize_1 plantation_3) 2059.63)
    (= (distance p5_pulverize_1 plantation_4) 3223.98)
    (= (distance p5_pulverize_1 plantation_5) 217.592)
    (= (distance p5_pulverize_1 p2_pulverize_1) 832.332)
    (= (distance p5_pulverize_1 p3_pulverize_1) 2056.45)
    (= (distance p5_pulverize_1 p3_pulverize_2) 2553.67)
    (= (distance p5_pulverize_1 p5_pulverize_2) 368.096)
    (= (distance p5_pulverize_1 p5_pulverize_3) 254.975)
    (= (distance p5_pulverize_1 p5_pulverize_4) 595.698)
    (= (distance p5_pulverize_1 base_1) 1997.34)
    (= (distance p5_pulverize_1 base_2) 2730.13)
    (= (distance p5_pulverize_1 base_3) 1123.54)
    (= (distance p5_pulverize_2 plantation_1) 3488.59)
    (= (distance p5_pulverize_2 plantation_2) 1361.1)
    (= (distance p5_pulverize_2 plantation_3) 2426.55)
    (= (distance p5_pulverize_2 plantation_4) 3590.19)
    (= (distance p5_pulverize_2 plantation_5) 576.985)
    (= (distance p5_pulverize_2 p2_pulverize_1) 1171.59)
    (= (distance p5_pulverize_2 p3_pulverize_1) 2423.9)
    (= (distance p5_pulverize_2 p3_pulverize_2) 2920.82)
    (= (distance p5_pulverize_2 p5_pulverize_1) 368.096)
    (= (distance p5_pulverize_2 p5_pulverize_3) 122.909)
    (= (distance p5_pulverize_2 p5_pulverize_4) 298.514)
    (= (distance p5_pulverize_2 base_1) 2130.83)
    (= (distance p5_pulverize_2 base_2) 3085.49)
    (= (distance p5_pulverize_2 base_3) 759.465)
    (= (distance p5_pulverize_3 plantation_1) 3367.3)
    (= (distance p5_pulverize_3 plantation_2) 1241.05)
    (= (distance p5_pulverize_3 plantation_3) 2307.89)
    (= (distance p5_pulverize_3 plantation_4) 3478.65)
    (= (distance p5_pulverize_3 plantation_5) 457.122)
    (= (distance p5_pulverize_3 p2_pulverize_1) 1048.68)
    (= (distance p5_pulverize_3 p3_pulverize_1) 2310.45)
    (= (distance p5_pulverize_3 p3_pulverize_2) 2807.89)
    (= (distance p5_pulverize_3 p5_pulverize_1) 254.975)
    (= (distance p5_pulverize_3 p5_pulverize_2) 122.909)
    (= (distance p5_pulverize_3 p5_pulverize_4) 355.216)
    (= (distance p5_pulverize_3 base_1) 2045.94)
    (= (distance p5_pulverize_3 base_2) 2963.02)
    (= (distance p5_pulverize_3 base_3) 868.773)
    (= (distance p5_pulverize_4 plantation_1) 3543.82)
    (= (distance p5_pulverize_4 plantation_2) 1421.6)
    (= (distance p5_pulverize_4 plantation_3) 2597.66)
    (= (distance p5_pulverize_4 plantation_4) 3795.84)
    (= (distance p5_pulverize_4 plantation_5) 766.686)
    (= (distance p5_pulverize_4 p2_pulverize_1) 1292.23)
    (= (distance p5_pulverize_4 p3_pulverize_1) 2623.83)
    (= (distance p5_pulverize_4 p3_pulverize_2) 3121.78)
    (= (distance p5_pulverize_4 p5_pulverize_1) 595.698)
    (= (distance p5_pulverize_4 p5_pulverize_2) 298.514)
    (= (distance p5_pulverize_4 p5_pulverize_3) 355.216)
    (= (distance p5_pulverize_4 base_1) 2009.55)
    (= (distance p5_pulverize_4 base_2) 3209.98)
    (= (distance p5_pulverize_4 base_3) 579.705)
    (= (distance base_1 plantation_1) 2162.53)
    (= (distance base_1 plantation_2) 1242.79)
    (= (distance base_1 plantation_3) 2287.23)
    (= (distance base_1 plantation_4) 3475.81)
    (= (distance base_1 plantation_5) 1874.81)
    (= (distance base_1 p2_pulverize_1) 1543.69)
    (= (distance base_1 p3_pulverize_1) 2531.53)
    (= (distance base_1 p3_pulverize_2) 2895.66)
    (= (distance base_1 p5_pulverize_1) 1997.34)
    (= (distance base_1 p5_pulverize_2) 2130.83)
    (= (distance base_1 p5_pulverize_3) 2045.94)
    (= (distance base_1 p5_pulverize_4) 2009.55)
    (= (distance base_1 base_2) 2362.58)
    (= (distance base_1 base_3) 2447.31)
    (= (distance base_2 plantation_1) 889.922)
    (= (distance base_2 plantation_2) 1822.55)
    (= (distance base_2 plantation_3) 812.939)
    (= (distance base_2 plantation_4) 1245.07)
    (= (distance base_2 plantation_5) 2513.23)
    (= (distance base_2 p2_pulverize_1) 1920.62)
    (= (distance base_2 p3_pulverize_1) 1056.65)
    (= (distance base_2 p3_pulverize_2) 955.274)
    (= (distance base_2 p5_pulverize_1) 2730.13)
    (= (distance base_2 p5_pulverize_2) 3085.49)
    (= (distance base_2 p5_pulverize_3) 2963.02)
    (= (distance base_2 p5_pulverize_4) 3209.98)
    (= (distance base_2 base_1) 2362.58)
    (= (distance base_2 base_3) 3789.59)
    (= (distance base_3 plantation_1) 4110.86)
    (= (distance base_3 plantation_2) 1996.97)
    (= (distance base_3 plantation_3) 3167.44)
    (= (distance base_3 plantation_4) 4347.43)
    (= (distance base_3 plantation_5) 1320.47)
    (= (distance base_3 p2_pulverize_1) 1870.93)
    (= (distance base_3 p3_pulverize_1) 3178.81)
    (= (distance base_3 p3_pulverize_2) 3676.42)
    (= (distance base_3 p5_pulverize_1) 1123.54)
    (= (distance base_3 p5_pulverize_2) 759.465)
    (= (distance base_3 p5_pulverize_3) 868.773)
    (= (distance base_3 p5_pulverize_4) 579.705)
    (= (distance base_3 base_1) 2447.31)
    (= (distance base_3 base_2) 3789.59)


)
(:goal (and
    (pulverized input1 p2_pulverize_1_objective)
    ;(pulverized input1 p3_pulverize_1_objective)
    ;(pulverized input1 p3_pulverize_2_objective)
    ;(pulverized input1 p5_pulverize_1_objective)
    ;(pulverized input1 p5_pulverize_2_objective)
    ;(pulverized input1 p5_pulverize_3_objective)
    ;(pulverized input1 p5_pulverize_4_objective)
    (taken-image plantation_1_photo)
    ;(taken-image plantation_2_photo)
    ;(taken-image plantation_3_photo)
    ;(taken-image plantation_4_photo)
    ;(taken-image plantation_5_photo)
))
(:metric 
        minimize (total-time)
    )
)
