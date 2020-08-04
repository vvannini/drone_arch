Number of literals: 347
Constructing lookup tables: [10%] [20%] [30%] [40%] [50%] [60%] [70%] [80%] [90%] [100%]
Post filtering unreachable actions:  [10%] [20%] [30%] [40%] [50%] [60%] [70%] [80%] [90%] [100%]
[01;34mNo analytic limits found, not considering limit effects of goal-only operators[00m
5% of the ground temporal actions in this problem are compression-safe
Initial heuristic = 45.000
b (44.000 | 0.000)b (43.000 | 355.084)b (42.000 | 355.084)b (40.000 | 640.799)b (38.000 | 1029.686)b (37.000 | 1029.686)b (36.000 | 1029.687)b (35.000 | 1350.518)b (34.000 | 1350.519)
Resorting to best-first search
b (44.000 | 0.000)b (43.000 | 355.084)b (42.000 | 355.084)b (40.000 | 640.799)b (38.000 | 1029.686)b (37.000 | 1029.686)b (36.000 | 1029.687)b (34.000 | 1315.400)b (31.000 | 1350.519)b (30.000 | 1935.857)b (28.000 | 2221.570)b (27.000 | 2415.417)b (26.000 | 2415.417)b (24.000 | 2462.779)b (23.000 | 2999.211)b (22.000 | 2999.211)b (21.000 | 2999.212)b (19.000 | 3284.926)b (18.000 | 3455.126)b (17.000 | 3455.126)b (16.000 | 3455.127)b (14.000 | 3740.840)b (13.000 | 4756.176)b (12.000 | 5112.257)b (11.000 | 5112.257)b (9.000 | 5164.208)b (8.000 | 5771.629)b (7.000 | 5771.629)b (6.000 | 5771.630)b (4.000 | 6057.343)b (3.000 | 6642.236)b (2.000 | 6642.236);;;; Solution Found
; States evaluated: 999
; Cost: 6927.950
; Time 8.64
0.000: (set_next_region region_2)  [0.000]
0.001: (go_to_picture base_1 region_2)  [355.083]
355.085: (take_image region_2)  [285.714]
355.085: (set_next_region region_10)  [0.000]
640.800: (go_to_picture region_2 region_10)  [388.886]
1029.686: (take_image region_10)  [285.714]
1029.687: (set_next_region region_11)  [0.000]
1315.401: (go_to_picture region_10 region_11)  [35.117]
1350.518: (take_image region_11)  [285.714]
1350.519: (set_next_region region_6)  [0.000]
1636.233: (go_to_picture region_11 region_6)  [299.623]
1636.234: (need_battery)  [1.000]
1935.856: (take_image region_6)  [285.714]
1935.857: (set_next_region region_12)  [0.000]
2221.571: (go_to_base region_6 base_4)  [193.845]
2415.418: (recharge_battery base_4)  [47.361]
2462.780: (go_to_picture base_4 region_12)  [536.431]
2999.211: (take_image region_12)  [285.714]
2999.212: (set_next_region region_9)  [0.000]
3284.927: (go_to_picture region_12 region_9)  [170.199]
3455.126: (take_image region_9)  [285.714]
3455.127: (set_next_region region_8)  [0.000]
3740.841: (go_to_picture region_9 region_8)  [729.620]
3740.842: (need_battery)  [1.000]
4470.461: (take_image region_8)  [285.714]
4470.462: (set_next_region region_1)  [0.000]
4756.177: (go_to_base region_8 base_4)  [356.080]
5112.258: (recharge_battery base_4)  [51.950]
5164.209: (go_to_picture base_4 region_1)  [607.420]
5771.629: (take_image region_1)  [285.714]
5771.630: (set_next_region region_4)  [0.000]
6057.344: (go_to_picture region_1 region_4)  [584.891]
6642.236: (take_image region_4)  [285.714]
