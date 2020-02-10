Warnings encountered when parsing Domain/Problem File

Errors: 0, warnings: 5
/home/vannini/drone_arch/Data/domain_ijcai.pddl: line: 281: Warning: Undeclared variable symbol: ?drone
/home/vannini/drone_arch/Data/problem_c1.pddl: line: 94: Warning: Undeclared symbol: region1_photo
/home/vannini/drone_arch/Data/problem_c1.pddl: line: 94: Warning: Undeclared symbol: region2_photo
/home/vannini/drone_arch/Data/problem_c1.pddl: line: 94: Warning: Undeclared symbol: region3_photo
/home/vannini/drone_arch/Data/problem_c1.pddl: line: 94: Warning: Undeclared symbol: region4_photo
Number of literals: 83
Constructing lookup tables: [10%] [20%] [30%] [40%] [50%] [60%] [70%] [80%] [90%] [100%]
Post filtering unreachable actions:  [10%] [20%] [30%] [40%] [50%] [60%] [70%] [80%] [90%] [100%]
[01;34mNo analytic limits found, not considering limit effects of goal-only operators[00m
52% of the ground temporal actions in this problem are compression-safe
Initial heuristic = 23.000
b (22.000 | 8.000)b (21.000 | 8.001)b (20.000 | 8.001)b (18.000 | 8.001)b (17.000 | 93.716)b (16.000 | 100.717)b (15.000 | 193.718)b (14.000 | 693.720)b (13.000 | 693.720)b (12.000 | 1144.578)b (11.000 | 1551.580)b (10.000 | 1951.580)b (9.000 | 2515.725)b (8.000 | 2601.441)b (7.000 | 3394.157)b (6.000 | 3822.729)
Resorting to best-first search
b (22.000 | 8.000)b (21.000 | 8.000)b (20.000 | 8.000)b (18.000 | 8.000)b (17.000 | 93.715)b (16.000 | 100.716)b (15.000 | 193.717)b (14.000 | 693.719)b (13.000 | 693.719)b (12.000 | 1144.577)b (11.000 | 1423.007)b (10.000 | 1823.007)b (9.000 | 2172.865)b (8.000 | 2672.865)b (8.000 | 2601.438)b (6.000 | 2922.723)b (6.000 | 2908.438)b (5.000 | 2930.724)b (4.000 | 3116.441)b (3.000 | 3116.441)b (2.000 | 3216.442)b (1.000 | 3216.442);;;; Solution Found
; States evaluated: 1398
; Cost: 3302.157
; Time 7.74
0.000: (recharge-input rover1 input1 base1)  [8.000]
0.000: (clean-camera rover1 base1 camera1)  [8.000]
0.000: (recharge-battery rover1 base1)  [5.000]
8.001: (go-to rover1 base1 region1)  [85.714]
93.716: (take_image rover1 region1_photo region1 camera1)  [7.000]
93.717: (pulverize-region rover1 input1 region1 orange_objective1 camera1)  [100.000]
193.718: (go-to rover1 region1 region2)  [400.000]
593.719: (pulverize-region rover1 input1 region2 orange_objective2 camera1)  [100.000]
693.720: (go-to rover1 region2 base1)  [357.143]
1050.863: (clean-camera rover1 base1 camera1)  [8.000]
1058.863: (go-to rover1 base1 region2)  [357.143]
1416.007: (take_image rover1 region2_photo region2 camera1)  [7.000]
1423.007: (go-to rover1 region2 region4)  [600.000]
2023.008: (take_image rover1 region4_photo region4 camera1)  [7.000]
2030.008: (go-to rover1 region4 region3)  [600.000]
2630.009: (take_image rover1 region3_photo region3 camera1)  [7.000]
2637.009: (go-to rover1 region3 base1)  [285.714]
2922.723: (discharge-input rover1 input1 base1)  [8.000]
2922.724: (recharge-input rover1 input2 base1)  [8.000]
2930.725: (go-to rover1 base1 region1)  [85.714]
3016.441: (pulverize-region rover1 input2 region1 green_objective1 camera1)  [100.000]
3116.442: (pulverize-region rover1 input2 region1 green_objective2 camera1)  [100.000]
3216.443: (go-to rover1 region1 base1)  [85.714]
