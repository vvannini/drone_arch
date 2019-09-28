;; Auto-generated. Do not edit!


(when (boundp 'decision_support::newMission)
  (if (not (find-package "DECISION_SUPPORT"))
    (make-package "DECISION_SUPPORT"))
  (shadow 'newMission (find-package "DECISION_SUPPORT")))
(unless (find-package "DECISION_SUPPORT::NEWMISSION")
  (make-package "DECISION_SUPPORT::NEWMISSION"))

(in-package "ROS")
;;//! \htmlinclude newMission.msg.html
(if (not (find-package "MAVROS_MSGS"))
  (ros::roseus-add-msgs "mavros_msgs"))


(defclass decision_support::newMission
  :super ros::object
  :slots (_option _nWaypoints _waypoints ))

(defmethod decision_support::newMission
  (:init
   (&key
    ((:option __option) 0)
    ((:nWaypoints __nWaypoints) 0)
    ((:waypoints __waypoints) (let (r) (dotimes (i 0) (push (instance mavros_msgs::Waypoint :init) r)) r))
    )
   (send-super :init)
   (setq _option (round __option))
   (setq _nWaypoints (round __nWaypoints))
   (setq _waypoints __waypoints)
   self)
  (:option
   (&optional __option)
   (if __option (setq _option __option)) _option)
  (:nWaypoints
   (&optional __nWaypoints)
   (if __nWaypoints (setq _nWaypoints __nWaypoints)) _nWaypoints)
  (:waypoints
   (&rest __waypoints)
   (if (keywordp (car __waypoints))
       (send* _waypoints __waypoints)
     (progn
       (if __waypoints (setq _waypoints (car __waypoints)))
       _waypoints)))
  (:serialization-length
   ()
   (+
    ;; uint8 _option
    1
    ;; uint32 _nWaypoints
    4
    ;; mavros_msgs/Waypoint[] _waypoints
    (apply #'+ (send-all _waypoints :serialization-length)) 4
    ))
  (:serialize
   (&optional strm)
   (let ((s (if strm strm
              (make-string-output-stream (send self :serialization-length)))))
     ;; uint8 _option
       (write-byte _option s)
     ;; uint32 _nWaypoints
       (write-long _nWaypoints s)
     ;; mavros_msgs/Waypoint[] _waypoints
     (write-long (length _waypoints) s)
     (dolist (elem _waypoints)
       (send elem :serialize s)
       )
     ;;
     (if (null strm) (get-output-stream-string s))))
  (:deserialize
   (buf &optional (ptr- 0))
   ;; uint8 _option
     (setq _option (sys::peek buf ptr- :char)) (incf ptr- 1)
   ;; uint32 _nWaypoints
     (setq _nWaypoints (sys::peek buf ptr- :integer)) (incf ptr- 4)
   ;; mavros_msgs/Waypoint[] _waypoints
   (let (n)
     (setq n (sys::peek buf ptr- :integer)) (incf ptr- 4)
     (setq _waypoints (let (r) (dotimes (i n) (push (instance mavros_msgs::Waypoint :init) r)) r))
     (dolist (elem- _waypoints)
     (send elem- :deserialize buf ptr-) (incf ptr- (send elem- :serialization-length))
     ))
   ;;
   self)
  )

(setf (get decision_support::newMission :md5sum-) "1625ea9210013ed87a1752191f2182af")
(setf (get decision_support::newMission :datatype-) "decision_support/newMission")
(setf (get decision_support::newMission :definition-)
      "uint8 option
uint32 nWaypoints
mavros_msgs/Waypoint[] waypoints
================================================================================
MSG: mavros_msgs/Waypoint
# Waypoint.msg
#
# ROS representation of MAVLink MISSION_ITEM
# See mavlink documentation



# see enum MAV_FRAME
uint8 frame
uint8 FRAME_GLOBAL = 0
uint8 FRAME_LOCAL_NED = 1
uint8 FRAME_MISSION = 2
uint8 FRAME_GLOBAL_REL_ALT = 3
uint8 FRAME_LOCAL_ENU = 4

# see enum MAV_CMD and CommandCode.msg
uint16 command

bool is_current
bool autocontinue
# meaning of this params described in enum MAV_CMD
float32 param1
float32 param2
float32 param3
float32 param4
float64 x_lat
float64 y_long
float64 z_alt

")



(provide :decision_support/newMission "1625ea9210013ed87a1752191f2182af")


