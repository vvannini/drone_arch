Number of literals: 306
Constructing lookup tables: [10%] [20%] [30%] [40%] [50%] [60%] [70%] [80%] [90%] [100%]
Post filtering unreachable actions:  [10%] [20%] [30%] [40%] [50%] [60%] [70%] [80%] [90%] [100%]
[01;34mNo analytic limits found, not considering limit effects of goal-only operators[00m
2% of the ground temporal actions in this problem are compression-safe
Initial heuristic = 31.000
b (27.000 | 8.000)b (26.000 | 715.581)b (25.000 | 715.581)b (24.000 | 1413.839)b (23.000 | 1413.839)b (22.000 | 1749.589)b (21.000 | 1749.589)b (20.000 | 2077.005)b (18.000 | 2109.417)b (17.000 | 2590.569)b (16.000 | 3372.827)b (15.000 | 3855.507)b (14.000 | 3863.507)b (12.000 | 3889.745)b (11.000 | 4500.830)b (10.000 | 4663.395)b (8.000 | 5112.771)b (7.000 | 5112.771)b (6.000 | 5120.771)b (5.000 | 6398.499)b (4.000 | 6398.499)b (2.000 | 6447.535)b (1.000 | 7610.115);;;; Solution Found
; States evaluated: 222
; Cost: 7610.115
; Time 0.64
0.000: (recharge_input kenny base_1)  [8.000]
8.001: (go_to kenny base_1 region_1)  [707.580]
715.582: (go_to kenny region_1 region_2)  [698.257]
1413.840: (go_to kenny region_2 region_5)  [335.749]
1413.841: (need_battery kenny)  [1.000]
1749.590: (go_to_base kenny region_5 base_4)  [319.414]
2069.005: (recharge_input kenny base_4)  [8.000]
2069.006: (recharge_battery kenny base_4)  [40.412]
2109.418: (go_to kenny base_4 region_3)  [302.658]
2412.077: (go_to kenny region_3 region_7)  [178.492]
2590.570: (go_to kenny region_7 region_10)  [782.257]
3372.828: (need_input kenny)  [1.000]
3372.829: (go_to_base kenny region_10 base_4)  [482.677]
3855.507: (recharge_input kenny base_4)  [8.000]
3855.508: (recharge_battery kenny base_4)  [34.237]
3889.746: (go_to kenny base_4 region_6)  [283.560]
4173.306: (go_to kenny region_6 region_9)  [327.523]
4500.831: (go_to kenny region_9 region_11)  [162.564]
4663.396: (need_input kenny)  [1.000]
4663.397: (go_to_base kenny region_11 base_4)  [449.374]
5112.771: (recharge_input kenny base_4)  [8.000]
5120.772: (go_to kenny base_4 region_8)  [445.794]
5120.773: (need_battery kenny)  [1.000]
5566.568: (go_to kenny region_8 region_4)  [282.825]
5849.394: (go_to_base kenny region_4 base_4)  [549.106]
6398.500: (recharge_battery kenny base_4)  [49.034]
6447.536: (go_to kenny base_4 region_12)  [626.146]
7073.682: (need_input kenny)  [1.000]
7073.683: (go_to_base kenny region_12 base_4)  [536.431]
