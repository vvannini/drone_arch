(define (problem task)
(:domain journal)
(:objects
    region_1 region_2 region_3 region_4 region_5 region_6 region_7 region_8 region_9 region_10 region_11 region_12 - region
    base_1 base_2 base_3 base_4 - base
)
(:init


    (at base_1)


    (nto region_1)
    (nto region_2)
    (nto region_3)
    (nto region_4)
    (nto region_5)
    (nto region_6)
    (nto region_7)
    (nto region_8)
    (nto region_9)
    (nto region_10)
    (nto region_11)
    (nto region_12)
    (nto base_1)
    (nto base_2)
    (nto base_3)
    (nto base_4)





    (can-go)

    (can-take-pic)

    (its-not-base region_1)
    (its-not-base region_2)
    (its-not-base region_3)
    (its-not-base region_4)
    (its-not-base region_5)
    (its-not-base region_6)
    (its-not-base region_7)
    (its-not-base region_8)
    (its-not-base region_9)
    (its-not-base region_10)
    (its-not-base region_11)
    (its-not-base region_12)


    (picture-goal region_1)
    (picture-goal region_2)
    (picture-goal region_4)
    (picture-goal region_6)
    (picture-goal region_8)
    (picture-goal region_9)
    (picture-goal region_10)
    (picture-goal region_11)
    (picture-goal region_12)




    (has-picture-goal)


    (= (battery-amount) 100)

    (= (input-amount) 0)

    (= (recharge-rate-battery) 2.142)

    (= (discharge-rate-battery) 0.042)

    (= (battery-capacity) 100)

    (= (input-capacity) 3)


    (= (distance region_1 region_2) 2129.9)
    (= (distance region_1 region_3) 1605.06)
    (= (distance region_1 region_4) 2047.12)
    (= (distance region_1 region_5) 2955.09)
    (= (distance region_1 region_6) 2338.85)
    (= (distance region_1 region_7) 1892.68)
    (= (distance region_1 region_8) 1845.13)
    (= (distance region_1 region_9) 3168.58)
    (= (distance region_1 region_10) 3488.59)
    (= (distance region_1 region_11) 3367.3)
    (= (distance region_1 region_12) 3543.82)
    (= (distance region_1 base_1) 2162.53)
    (= (distance region_1 base_2) 2178.52)
    (= (distance region_1 base_3) 4110.86)
    (= (distance region_1 base_4) 2125.97)
    (= (distance region_2 region_1) 2129.9)
    (= (distance region_2 region_3) 1353.17)
    (= (distance region_2 region_4) 2619.16)
    (= (distance region_2 region_5) 861.122)
    (= (distance region_2 region_6) 319.891)
    (= (distance region_2 region_7) 1500)
    (= (distance region_2 region_8) 1959.09)
    (= (distance region_2 region_9) 1061.57)
    (= (distance region_2 region_10) 1361.1)
    (= (distance region_2 region_11) 1241.05)
    (= (distance region_2 region_12) 1421.6)
    (= (distance region_2 base_1) 1242.79)
    (= (distance region_2 base_2) 1211.57)
    (= (distance region_2 base_3) 1996.97)
    (= (distance region_2 base_4) 842.368)
    (= (distance region_3 region_1) 1605.06)
    (= (distance region_3 region_2) 1353.17)
    (= (distance region_3 region_4) 1266.33)
    (= (distance region_3 region_5) 1850.87)
    (= (distance region_3 region_6) 1328.21)
    (= (distance region_3 region_7) 310.723)
    (= (distance region_3 region_8) 619.709)
    (= (distance region_3 region_9) 2059.63)
    (= (distance region_3 region_10) 2426.55)
    (= (distance region_3 region_11) 2307.89)
    (= (distance region_3 region_12) 2597.66)
    (= (distance region_3 base_1) 2287.23)
    (= (distance region_3 base_2) 2273.41)
    (= (distance region_3 base_3) 3167.44)
    (= (distance region_3 base_4) 745.302)
    (= (distance region_4 region_1) 2047.12)
    (= (distance region_4 region_2) 2619.16)
    (= (distance region_4 region_3) 1266.33)
    (= (distance region_4 region_5) 3030.97)
    (= (distance region_4 region_6) 2570.79)
    (= (distance region_4 region_7) 1172.62)
    (= (distance region_4 region_8) 675.888)
    (= (distance region_4 region_9) 3223.98)
    (= (distance region_4 region_10) 3590.19)
    (= (distance region_4 region_11) 3478.65)
    (= (distance region_4 region_12) 3795.84)
    (= (distance region_4 base_1) 3475.81)
    (= (distance region_4 base_2) 3468.09)
    (= (distance region_4 base_3) 4347.43)
    (= (distance region_4 base_4) 1921.87)
    (= (distance region_5 region_1) 2955.09)
    (= (distance region_5 region_2) 861.122)
    (= (distance region_5 region_3) 1850.87)
    (= (distance region_5 region_4) 3030.97)
    (= (distance region_5 region_6) 616.845)
    (= (distance region_5 region_7) 1860.06)
    (= (distance region_5 region_8) 2358.02)
    (= (distance region_5 region_9) 217.592)
    (= (distance region_5 region_10) 576.985)
    (= (distance region_5 region_11) 457.122)
    (= (distance region_5 region_12) 766.686)
    (= (distance region_5 base_1) 1874.81)
    (= (distance region_5 base_2) 1835.54)
    (= (distance region_5 base_3) 1320.47)
    (= (distance region_5 base_4) 1117.95)
    (= (distance region_6 region_1) 2338.85)
    (= (distance region_6 region_2) 319.891)
    (= (distance region_6 region_3) 1328.21)
    (= (distance region_6 region_4) 2570.79)
    (= (distance region_6 region_5) 616.845)
    (= (distance region_6 region_7) 1410)
    (= (distance region_6 region_8) 1896.85)
    (= (distance region_6 region_9) 832.332)
    (= (distance region_6 region_10) 1171.59)
    (= (distance region_6 region_11) 1048.68)
    (= (distance region_6 region_12) 1292.23)
    (= (distance region_6 base_1) 1543.69)
    (= (distance region_6 base_2) 1510.35)
    (= (distance region_6 base_3) 1870.93)
    (= (distance region_6 base_4) 678.459)
    (= (distance region_7 region_1) 1892.68)
    (= (distance region_7 region_2) 1500)
    (= (distance region_7 region_3) 310.723)
    (= (distance region_7 region_4) 1172.62)
    (= (distance region_7 region_5) 1860.06)
    (= (distance region_7 region_6) 1410)
    (= (distance region_7 region_8) 498.006)
    (= (distance region_7 region_9) 2056.45)
    (= (distance region_7 region_10) 2423.9)
    (= (distance region_7 region_11) 2310.45)
    (= (distance region_7 region_12) 2623.83)
    (= (distance region_7 base_1) 2531.53)
    (= (distance region_7 base_2) 2514.6)
    (= (distance region_7 base_3) 3178.81)
    (= (distance region_7 base_4) 749.63)
    (= (distance region_8 region_1) 1845.13)
    (= (distance region_8 region_2) 1959.09)
    (= (distance region_8 region_3) 619.709)
    (= (distance region_8 region_4) 675.888)
    (= (distance region_8 region_5) 2358.02)
    (= (distance region_8 region_6) 1896.85)
    (= (distance region_8 region_7) 498.006)
    (= (distance region_8 region_9) 2553.67)
    (= (distance region_8 region_10) 2920.82)
    (= (distance region_8 region_11) 2807.89)
    (= (distance region_8 region_12) 3121.78)
    (= (distance region_8 base_1) 2895.66)
    (= (distance region_8 base_2) 2883.57)
    (= (distance region_8 base_3) 3676.42)
    (= (distance region_8 base_4) 1246.28)
    (= (distance region_9 region_1) 3168.58)
    (= (distance region_9 region_2) 1061.57)
    (= (distance region_9 region_3) 2059.63)
    (= (distance region_9 region_4) 3223.98)
    (= (distance region_9 region_5) 217.592)
    (= (distance region_9 region_6) 832.332)
    (= (distance region_9 region_7) 2056.45)
    (= (distance region_9 region_8) 2553.67)
    (= (distance region_9 region_10) 368.096)
    (= (distance region_9 region_11) 254.975)
    (= (distance region_9 region_12) 595.698)
    (= (distance region_9 base_1) 1997.34)
    (= (distance region_9 base_2) 1957)
    (= (distance region_9 base_3) 1123.54)
    (= (distance region_9 base_4) 1321.36)
    (= (distance region_10 region_1) 3488.59)
    (= (distance region_10 region_2) 1361.1)
    (= (distance region_10 region_3) 2426.55)
    (= (distance region_10 region_4) 3590.19)
    (= (distance region_10 region_5) 576.985)
    (= (distance region_10 region_6) 1171.59)
    (= (distance region_10 region_7) 2423.9)
    (= (distance region_10 region_8) 2920.82)
    (= (distance region_10 region_9) 368.096)
    (= (distance region_10 region_11) 122.909)
    (= (distance region_10 region_12) 298.514)
    (= (distance region_10 base_1) 2130.83)
    (= (distance region_10 base_2) 2089.48)
    (= (distance region_10 base_3) 759.465)
    (= (distance region_10 base_4) 1689.37)
    (= (distance region_11 region_1) 3367.3)
    (= (distance region_11 region_2) 1241.05)
    (= (distance region_11 region_3) 2307.89)
    (= (distance region_11 region_4) 3478.65)
    (= (distance region_11 region_5) 457.122)
    (= (distance region_11 region_6) 1048.68)
    (= (distance region_11 region_7) 2310.45)
    (= (distance region_11 region_8) 2807.89)
    (= (distance region_11 region_9) 254.975)
    (= (distance region_11 region_10) 122.909)
    (= (distance region_11 region_12) 355.216)
    (= (distance region_11 base_1) 2045.94)
    (= (distance region_11 base_2) 2004.74)
    (= (distance region_11 base_3) 868.773)
    (= (distance region_11 base_4) 1572.81)
    (= (distance region_12 region_1) 3543.82)
    (= (distance region_12 region_2) 1421.6)
    (= (distance region_12 region_3) 2597.66)
    (= (distance region_12 region_4) 3795.84)
    (= (distance region_12 region_5) 766.686)
    (= (distance region_12 region_6) 1292.23)
    (= (distance region_12 region_7) 2623.83)
    (= (distance region_12 region_8) 3121.78)
    (= (distance region_12 region_9) 595.698)
    (= (distance region_12 region_10) 298.514)
    (= (distance region_12 region_11) 355.216)
    (= (distance region_12 base_1) 2009.55)
    (= (distance region_12 base_2) 1968.22)
    (= (distance region_12 base_3) 579.705)
    (= (distance region_12 base_4) 1877.51)
    (= (distance base_1 region_1) 2162.53)
    (= (distance base_1 region_2) 1242.79)
    (= (distance base_1 region_3) 2287.23)
    (= (distance base_1 region_4) 3475.81)
    (= (distance base_1 region_5) 1874.81)
    (= (distance base_1 region_6) 1543.69)
    (= (distance base_1 region_7) 2531.53)
    (= (distance base_1 region_8) 2895.66)
    (= (distance base_1 region_9) 1997.34)
    (= (distance base_1 region_10) 2130.83)
    (= (distance base_1 region_11) 2045.94)
    (= (distance base_1 region_12) 2009.55)
    (= (distance base_1 base_2) 41.4312)
    (= (distance base_1 base_3) 2447.31)
    (= (distance base_1 base_4) 2029.45)
    (= (distance base_2 region_1) 2178.52)
    (= (distance base_2 region_2) 1211.57)
    (= (distance base_2 region_3) 2273.41)
    (= (distance base_2 region_4) 3468.09)
    (= (distance base_2 region_5) 1835.54)
    (= (distance base_2 region_6) 1510.35)
    (= (distance base_2 region_7) 2514.6)
    (= (distance base_2 region_8) 2883.57)
    (= (distance base_2 region_9) 1957)
    (= (distance base_2 region_10) 2089.48)
    (= (distance base_2 region_11) 2004.74)
    (= (distance base_2 region_12) 1968.22)
    (= (distance base_2 base_1) 41.4312)
    (= (distance base_2 base_3) 2407.1)
    (= (distance base_2 base_4) 2003.83)
    (= (distance base_3 region_1) 4110.86)
    (= (distance base_3 region_2) 1996.97)
    (= (distance base_3 region_3) 3167.44)
    (= (distance base_3 region_4) 4347.43)
    (= (distance base_3 region_5) 1320.47)
    (= (distance base_3 region_6) 1870.93)
    (= (distance base_3 region_7) 3178.81)
    (= (distance base_3 region_8) 3676.42)
    (= (distance base_3 region_9) 1123.54)
    (= (distance base_3 region_10) 759.465)
    (= (distance base_3 region_11) 868.773)
    (= (distance base_3 region_12) 579.705)
    (= (distance base_3 base_1) 2447.31)
    (= (distance base_3 base_2) 2407.1)
    (= (distance base_3 base_4) 2438.41)
    (= (distance base_4 region_1) 2125.97)
    (= (distance base_4 region_2) 842.368)
    (= (distance base_4 region_3) 745.302)
    (= (distance base_4 region_4) 1921.87)
    (= (distance base_4 region_5) 1117.95)
    (= (distance base_4 region_6) 678.459)
    (= (distance base_4 region_7) 749.63)
    (= (distance base_4 region_8) 1246.28)
    (= (distance base_4 region_9) 1321.36)
    (= (distance base_4 region_10) 1689.37)
    (= (distance base_4 region_11) 1572.81)
    (= (distance base_4 region_12) 1877.51)
    (= (distance base_4 base_1) 2029.45)
    (= (distance base_4 base_2) 2003.83)
    (= (distance base_4 base_3) 2438.41)

    (= (velocity) 3.5)

    (= (picture-path-len region_1) 1000)
    (= (picture-path-len region_2) 1000)
    (= (picture-path-len region_4) 1000)
    (= (picture-path-len region_6) 1000)
    (= (picture-path-len region_8) 1000)
    (= (picture-path-len region_9) 1000)
    (= (picture-path-len region_10) 1000)
    (= (picture-path-len region_11) 1000)
    (= (picture-path-len region_12) 1000)


    (= (total-goals) 9)

    (= (goals-achived) 0)

)
(:goal (and
    (taken-image region_1)
    (taken-image region_2)
    (taken-image region_4)
    (taken-image region_6)
    (taken-image region_8)
    (taken-image region_9)
    (taken-image region_10)
    (taken-image region_11)
    (taken-image region_12)
))
(:metric minimize (total-time))
)
