Number of literals: 33
Constructing lookup tables: [10%] [20%] [30%] [40%] [50%] [60%] [70%] [80%] [90%] [100%]
Post filtering unreachable actions:  [10%] [20%] [30%] [40%] [50%] [60%] [70%] [80%] [90%] [100%]
[01;34mNo analytic limits found, not considering limit effects of goal-only operators[00m
84% of the ground temporal actions in this problem are compression-safe
Initial heuristic = 9.000
b (8.000 | 8.000)b (7.000 | 52.858)b (6.000 | 52.858)b (5.000 | 52.858)b (4.000 | 481.431)b (3.000 | 995.717)b (2.000 | 1138.575)b (1.000 | 1924.291);;;; Solution Found
; States evaluated: 13
; Cost: 0.000
; Time 0.00
0.000: (recharge_input rover1 base_1)  [8.000]
8.001: (pulverize_region rover1 base_1)  [44.857]
8.002: (clean_camera rover1 base_1)  [8.000]
52.859: (go_to rover1 base_1 base_3)  [428.571]
481.432: (go_to rover1 base_3 region_1)  [514.286]
995.718: (take_image rover1 region_1)  [142.857]
1138.575: (go_to rover1 region_1 region_4)  [642.857]
1781.434: (take_image rover1 region_4)  [142.857]
1924.291: (go_to rover1 region_4 region_1)  [642.857]
