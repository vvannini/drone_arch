Number of literals: 32
Constructing lookup tables: [10%] [20%] [30%] [40%] [50%] [60%] [70%] [80%] [90%] [100%]
Post filtering unreachable actions:  [10%] [20%] [30%] [40%] [50%] [60%] [70%] [80%] [90%] [100%]
[01;34mNo analytic limits found, not considering limit effects of goal-only operators[00m
86% of the ground temporal actions in this problem are compression-safe
Initial heuristic = 19.000
b (18.000 | 1.000)b (17.000 | 9.001)b (16.000 | 9.001)b (15.000 | 17.002)b (14.000 | 25.003)b (13.000 | 33.004)b (12.000 | 41.005)b (11.000 | 134.006)b (10.000 | 134.006)b (9.000 | 134.006)b (8.000 | 234.007)b (7.000 | 234.007)b (6.000 | 234.007)b (5.000 | 334.008)b (4.000 | 334.008)b (3.000 | 334.008)b (2.000 | 434.009)b (1.000 | 434.009);;;; Solution Found
; States evaluated: 167
; Cost: 434.009
; Time 0.14
0.000: (go_to rover1 base_1 base_2)  [1.000]
1.001: (recharge_input rover1 base_2)  [8.000]
1.001: (clean_camera rover1 base_2)  [8.000]
9.001: (go_to rover1 base_2 region_1)  [1.000]
10.002: (take_image rover1 region_1)  [7.000]
17.002: (go_to rover1 region_1 region_2)  [1.000]
18.003: (take_image rover1 region_2)  [7.000]
25.003: (go_to rover1 region_2 region_3)  [1.000]
26.004: (take_image rover1 region_3)  [7.000]
33.004: (go_to rover1 region_3 region_4)  [1.000]
34.005: (take_image rover1 region_4)  [7.000]
34.006: (pulverize_region rover1 region_4)  [100.000]
41.005: (go_to rover1 region_4 base_2)  [1.000]
42.006: (go_to rover1 base_2 region_1)  [1.000]
134.007: (pulverize_region rover1 region_1)  [100.000]
134.008: (go_to rover1 region_1 base_2)  [1.000]
135.008: (recharge_input rover1 base_2)  [8.000]
143.008: (go_to rover1 base_2 region_2)  [1.000]
234.008: (pulverize_region rover1 region_2)  [100.000]
234.009: (go_to rover1 region_2 base_2)  [1.000]
235.009: (recharge_input rover1 base_2)  [8.000]
333.008: (go_to rover1 base_2 region_3)  [1.000]
334.009: (pulverize_region rover1 region_3)  [100.000]
334.010: (go_to rover1 region_3 base_2)  [1.000]
