; Auto-generated. Do not edit!


(cl:in-package decision_support-msg)


;//! \htmlinclude newMission.msg.html

(cl:defclass <newMission> (roslisp-msg-protocol:ros-message)
  ((option
    :reader option
    :initarg :option
    :type cl:fixnum
    :initform 0)
   (nWaypoints
    :reader nWaypoints
    :initarg :nWaypoints
    :type cl:integer
    :initform 0)
   (waypoints
    :reader waypoints
    :initarg :waypoints
    :type (cl:vector mavros_msgs-msg:Waypoint)
   :initform (cl:make-array 0 :element-type 'mavros_msgs-msg:Waypoint :initial-element (cl:make-instance 'mavros_msgs-msg:Waypoint))))
)

(cl:defclass newMission (<newMission>)
  ())

(cl:defmethod cl:initialize-instance :after ((m <newMission>) cl:&rest args)
  (cl:declare (cl:ignorable args))
  (cl:unless (cl:typep m 'newMission)
    (roslisp-msg-protocol:msg-deprecation-warning "using old message class name decision_support-msg:<newMission> is deprecated: use decision_support-msg:newMission instead.")))

(cl:ensure-generic-function 'option-val :lambda-list '(m))
(cl:defmethod option-val ((m <newMission>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader decision_support-msg:option-val is deprecated.  Use decision_support-msg:option instead.")
  (option m))

(cl:ensure-generic-function 'nWaypoints-val :lambda-list '(m))
(cl:defmethod nWaypoints-val ((m <newMission>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader decision_support-msg:nWaypoints-val is deprecated.  Use decision_support-msg:nWaypoints instead.")
  (nWaypoints m))

(cl:ensure-generic-function 'waypoints-val :lambda-list '(m))
(cl:defmethod waypoints-val ((m <newMission>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader decision_support-msg:waypoints-val is deprecated.  Use decision_support-msg:waypoints instead.")
  (waypoints m))
(cl:defmethod roslisp-msg-protocol:serialize ((msg <newMission>) ostream)
  "Serializes a message object of type '<newMission>"
  (cl:write-byte (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'option)) ostream)
  (cl:write-byte (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'nWaypoints)) ostream)
  (cl:write-byte (cl:ldb (cl:byte 8 8) (cl:slot-value msg 'nWaypoints)) ostream)
  (cl:write-byte (cl:ldb (cl:byte 8 16) (cl:slot-value msg 'nWaypoints)) ostream)
  (cl:write-byte (cl:ldb (cl:byte 8 24) (cl:slot-value msg 'nWaypoints)) ostream)
  (cl:let ((__ros_arr_len (cl:length (cl:slot-value msg 'waypoints))))
    (cl:write-byte (cl:ldb (cl:byte 8 0) __ros_arr_len) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 8) __ros_arr_len) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 16) __ros_arr_len) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 24) __ros_arr_len) ostream))
  (cl:map cl:nil #'(cl:lambda (ele) (roslisp-msg-protocol:serialize ele ostream))
   (cl:slot-value msg 'waypoints))
)
(cl:defmethod roslisp-msg-protocol:deserialize ((msg <newMission>) istream)
  "Deserializes a message object of type '<newMission>"
    (cl:setf (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'option)) (cl:read-byte istream))
    (cl:setf (cl:ldb (cl:byte 8 0) (cl:slot-value msg 'nWaypoints)) (cl:read-byte istream))
    (cl:setf (cl:ldb (cl:byte 8 8) (cl:slot-value msg 'nWaypoints)) (cl:read-byte istream))
    (cl:setf (cl:ldb (cl:byte 8 16) (cl:slot-value msg 'nWaypoints)) (cl:read-byte istream))
    (cl:setf (cl:ldb (cl:byte 8 24) (cl:slot-value msg 'nWaypoints)) (cl:read-byte istream))
  (cl:let ((__ros_arr_len 0))
    (cl:setf (cl:ldb (cl:byte 8 0) __ros_arr_len) (cl:read-byte istream))
    (cl:setf (cl:ldb (cl:byte 8 8) __ros_arr_len) (cl:read-byte istream))
    (cl:setf (cl:ldb (cl:byte 8 16) __ros_arr_len) (cl:read-byte istream))
    (cl:setf (cl:ldb (cl:byte 8 24) __ros_arr_len) (cl:read-byte istream))
  (cl:setf (cl:slot-value msg 'waypoints) (cl:make-array __ros_arr_len))
  (cl:let ((vals (cl:slot-value msg 'waypoints)))
    (cl:dotimes (i __ros_arr_len)
    (cl:setf (cl:aref vals i) (cl:make-instance 'mavros_msgs-msg:Waypoint))
  (roslisp-msg-protocol:deserialize (cl:aref vals i) istream))))
  msg
)
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql '<newMission>)))
  "Returns string type for a message object of type '<newMission>"
  "decision_support/newMission")
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql 'newMission)))
  "Returns string type for a message object of type 'newMission"
  "decision_support/newMission")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql '<newMission>)))
  "Returns md5sum for a message object of type '<newMission>"
  "1625ea9210013ed87a1752191f2182af")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql 'newMission)))
  "Returns md5sum for a message object of type 'newMission"
  "1625ea9210013ed87a1752191f2182af")
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql '<newMission>)))
  "Returns full string definition for message of type '<newMission>"
  (cl:format cl:nil "uint8 option~%uint32 nWaypoints~%mavros_msgs/Waypoint[] waypoints~%================================================================================~%MSG: mavros_msgs/Waypoint~%# Waypoint.msg~%#~%# ROS representation of MAVLink MISSION_ITEM~%# See mavlink documentation~%~%~%~%# see enum MAV_FRAME~%uint8 frame~%uint8 FRAME_GLOBAL = 0~%uint8 FRAME_LOCAL_NED = 1~%uint8 FRAME_MISSION = 2~%uint8 FRAME_GLOBAL_REL_ALT = 3~%uint8 FRAME_LOCAL_ENU = 4~%~%# see enum MAV_CMD and CommandCode.msg~%uint16 command~%~%bool is_current~%bool autocontinue~%# meaning of this params described in enum MAV_CMD~%float32 param1~%float32 param2~%float32 param3~%float32 param4~%float64 x_lat~%float64 y_long~%float64 z_alt~%~%~%"))
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql 'newMission)))
  "Returns full string definition for message of type 'newMission"
  (cl:format cl:nil "uint8 option~%uint32 nWaypoints~%mavros_msgs/Waypoint[] waypoints~%================================================================================~%MSG: mavros_msgs/Waypoint~%# Waypoint.msg~%#~%# ROS representation of MAVLink MISSION_ITEM~%# See mavlink documentation~%~%~%~%# see enum MAV_FRAME~%uint8 frame~%uint8 FRAME_GLOBAL = 0~%uint8 FRAME_LOCAL_NED = 1~%uint8 FRAME_MISSION = 2~%uint8 FRAME_GLOBAL_REL_ALT = 3~%uint8 FRAME_LOCAL_ENU = 4~%~%# see enum MAV_CMD and CommandCode.msg~%uint16 command~%~%bool is_current~%bool autocontinue~%# meaning of this params described in enum MAV_CMD~%float32 param1~%float32 param2~%float32 param3~%float32 param4~%float64 x_lat~%float64 y_long~%float64 z_alt~%~%~%"))
(cl:defmethod roslisp-msg-protocol:serialization-length ((msg <newMission>))
  (cl:+ 0
     1
     4
     4 (cl:reduce #'cl:+ (cl:slot-value msg 'waypoints) :key #'(cl:lambda (ele) (cl:declare (cl:ignorable ele)) (cl:+ (roslisp-msg-protocol:serialization-length ele))))
))
(cl:defmethod roslisp-msg-protocol:ros-message-to-list ((msg <newMission>))
  "Converts a ROS message object to a list"
  (cl:list 'newMission
    (cl:cons ':option (option msg))
    (cl:cons ':nWaypoints (nWaypoints msg))
    (cl:cons ':waypoints (waypoints msg))
))
