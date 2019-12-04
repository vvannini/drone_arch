;; Auto-generated. Do not edit!


(when (boundp 'planners::GA_Planner)
  (if (not (find-package "PLANNERS"))
    (make-package "PLANNERS"))
  (shadow 'GA_Planner (find-package "PLANNERS")))
(unless (find-package "PLANNERS::GA_PLANNER")
  (make-package "PLANNERS::GA_PLANNER"))
(unless (find-package "PLANNERS::GA_PLANNERREQUEST")
  (make-package "PLANNERS::GA_PLANNERREQUEST"))
(unless (find-package "PLANNERS::GA_PLANNERRESPONSE")
  (make-package "PLANNERS::GA_PLANNERRESPONSE"))

(in-package "ROS")





(defclass planners::GA_PlannerRequest
  :super ros::object
  :slots (_origin_lat _origin_long _origin_alt _destination_lat _destination_long _destination_alt _map_id ))

(defmethod planners::GA_PlannerRequest
  (:init
   (&key
    ((:origin_lat __origin_lat) 0.0)
    ((:origin_long __origin_long) 0.0)
    ((:origin_alt __origin_alt) 0.0)
    ((:destination_lat __destination_lat) 0.0)
    ((:destination_long __destination_long) 0.0)
    ((:destination_alt __destination_alt) 0.0)
    ((:map_id __map_id) 0)
    )
   (send-super :init)
   (setq _origin_lat (float __origin_lat))
   (setq _origin_long (float __origin_long))
   (setq _origin_alt (float __origin_alt))
   (setq _destination_lat (float __destination_lat))
   (setq _destination_long (float __destination_long))
   (setq _destination_alt (float __destination_alt))
   (setq _map_id (round __map_id))
   self)
  (:origin_lat
   (&optional __origin_lat)
   (if __origin_lat (setq _origin_lat __origin_lat)) _origin_lat)
  (:origin_long
   (&optional __origin_long)
   (if __origin_long (setq _origin_long __origin_long)) _origin_long)
  (:origin_alt
   (&optional __origin_alt)
   (if __origin_alt (setq _origin_alt __origin_alt)) _origin_alt)
  (:destination_lat
   (&optional __destination_lat)
   (if __destination_lat (setq _destination_lat __destination_lat)) _destination_lat)
  (:destination_long
   (&optional __destination_long)
   (if __destination_long (setq _destination_long __destination_long)) _destination_long)
  (:destination_alt
   (&optional __destination_alt)
   (if __destination_alt (setq _destination_alt __destination_alt)) _destination_alt)
  (:map_id
   (&optional __map_id)
   (if __map_id (setq _map_id __map_id)) _map_id)
  (:serialization-length
   ()
   (+
    ;; float32 _origin_lat
    4
    ;; float32 _origin_long
    4
    ;; float32 _origin_alt
    4
    ;; float32 _destination_lat
    4
    ;; float32 _destination_long
    4
    ;; float32 _destination_alt
    4
    ;; int32 _map_id
    4
    ))
  (:serialize
   (&optional strm)
   (let ((s (if strm strm
              (make-string-output-stream (send self :serialization-length)))))
     ;; float32 _origin_lat
       (sys::poke _origin_lat (send s :buffer) (send s :count) :float) (incf (stream-count s) 4)
     ;; float32 _origin_long
       (sys::poke _origin_long (send s :buffer) (send s :count) :float) (incf (stream-count s) 4)
     ;; float32 _origin_alt
       (sys::poke _origin_alt (send s :buffer) (send s :count) :float) (incf (stream-count s) 4)
     ;; float32 _destination_lat
       (sys::poke _destination_lat (send s :buffer) (send s :count) :float) (incf (stream-count s) 4)
     ;; float32 _destination_long
       (sys::poke _destination_long (send s :buffer) (send s :count) :float) (incf (stream-count s) 4)
     ;; float32 _destination_alt
       (sys::poke _destination_alt (send s :buffer) (send s :count) :float) (incf (stream-count s) 4)
     ;; int32 _map_id
       (write-long _map_id s)
     ;;
     (if (null strm) (get-output-stream-string s))))
  (:deserialize
   (buf &optional (ptr- 0))
   ;; float32 _origin_lat
     (setq _origin_lat (sys::peek buf ptr- :float)) (incf ptr- 4)
   ;; float32 _origin_long
     (setq _origin_long (sys::peek buf ptr- :float)) (incf ptr- 4)
   ;; float32 _origin_alt
     (setq _origin_alt (sys::peek buf ptr- :float)) (incf ptr- 4)
   ;; float32 _destination_lat
     (setq _destination_lat (sys::peek buf ptr- :float)) (incf ptr- 4)
   ;; float32 _destination_long
     (setq _destination_long (sys::peek buf ptr- :float)) (incf ptr- 4)
   ;; float32 _destination_alt
     (setq _destination_alt (sys::peek buf ptr- :float)) (incf ptr- 4)
   ;; int32 _map_id
     (setq _map_id (sys::peek buf ptr- :integer)) (incf ptr- 4)
   ;;
   self)
  )

(defclass planners::GA_PlannerResponse
  :super ros::object
  :slots (_wp_path ))

(defmethod planners::GA_PlannerResponse
  (:init
   (&key
    ((:wp_path __wp_path) "")
    )
   (send-super :init)
   (setq _wp_path (string __wp_path))
   self)
  (:wp_path
   (&optional __wp_path)
   (if __wp_path (setq _wp_path __wp_path)) _wp_path)
  (:serialization-length
   ()
   (+
    ;; string _wp_path
    4 (length _wp_path)
    ))
  (:serialize
   (&optional strm)
   (let ((s (if strm strm
              (make-string-output-stream (send self :serialization-length)))))
     ;; string _wp_path
       (write-long (length _wp_path) s) (princ _wp_path s)
     ;;
     (if (null strm) (get-output-stream-string s))))
  (:deserialize
   (buf &optional (ptr- 0))
   ;; string _wp_path
     (let (n) (setq n (sys::peek buf ptr- :integer)) (incf ptr- 4) (setq _wp_path (subseq buf ptr- (+ ptr- n))) (incf ptr- n))
   ;;
   self)
  )

(defclass planners::GA_Planner
  :super ros::object
  :slots ())

(setf (get planners::GA_Planner :md5sum-) "4d22e4fe344ad31e2831d2deed99337f")
(setf (get planners::GA_Planner :datatype-) "planners/GA_Planner")
(setf (get planners::GA_Planner :request) planners::GA_PlannerRequest)
(setf (get planners::GA_Planner :response) planners::GA_PlannerResponse)

(defmethod planners::GA_PlannerRequest
  (:response () (instance planners::GA_PlannerResponse :init)))

(setf (get planners::GA_PlannerRequest :md5sum-) "4d22e4fe344ad31e2831d2deed99337f")
(setf (get planners::GA_PlannerRequest :datatype-) "planners/GA_PlannerRequest")
(setf (get planners::GA_PlannerRequest :definition-)
      "float32 origin_lat
float32 origin_long
float32 origin_alt
float32 destination_lat
float32 destination_long
float32 destination_alt
int32 map_id
---
string wp_path

")

(setf (get planners::GA_PlannerResponse :md5sum-) "4d22e4fe344ad31e2831d2deed99337f")
(setf (get planners::GA_PlannerResponse :datatype-) "planners/GA_PlannerResponse")
(setf (get planners::GA_PlannerResponse :definition-)
      "float32 origin_lat
float32 origin_long
float32 origin_alt
float32 destination_lat
float32 destination_long
float32 destination_alt
int32 map_id
---
string wp_path

")



(provide :planners/GA_Planner "4d22e4fe344ad31e2831d2deed99337f")


