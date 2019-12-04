; Auto-generated. Do not edit!


(cl:in-package planners-srv)


;//! \htmlinclude GA_Planner-request.msg.html

(cl:defclass <GA_Planner-request> (roslisp-msg-protocol:ros-message)
  ((origin_lat
    :reader origin_lat
    :initarg :origin_lat
    :type cl:float
    :initform 0.0)
   (origin_long
    :reader origin_long
    :initarg :origin_long
    :type cl:float
    :initform 0.0)
   (origin_alt
    :reader origin_alt
    :initarg :origin_alt
    :type cl:float
    :initform 0.0)
   (destination_lat
    :reader destination_lat
    :initarg :destination_lat
    :type cl:float
    :initform 0.0)
   (destination_long
    :reader destination_long
    :initarg :destination_long
    :type cl:float
    :initform 0.0)
   (destination_alt
    :reader destination_alt
    :initarg :destination_alt
    :type cl:float
    :initform 0.0)
   (map_id
    :reader map_id
    :initarg :map_id
    :type cl:integer
    :initform 0))
)

(cl:defclass GA_Planner-request (<GA_Planner-request>)
  ())

(cl:defmethod cl:initialize-instance :after ((m <GA_Planner-request>) cl:&rest args)
  (cl:declare (cl:ignorable args))
  (cl:unless (cl:typep m 'GA_Planner-request)
    (roslisp-msg-protocol:msg-deprecation-warning "using old message class name planners-srv:<GA_Planner-request> is deprecated: use planners-srv:GA_Planner-request instead.")))

(cl:ensure-generic-function 'origin_lat-val :lambda-list '(m))
(cl:defmethod origin_lat-val ((m <GA_Planner-request>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader planners-srv:origin_lat-val is deprecated.  Use planners-srv:origin_lat instead.")
  (origin_lat m))

(cl:ensure-generic-function 'origin_long-val :lambda-list '(m))
(cl:defmethod origin_long-val ((m <GA_Planner-request>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader planners-srv:origin_long-val is deprecated.  Use planners-srv:origin_long instead.")
  (origin_long m))

(cl:ensure-generic-function 'origin_alt-val :lambda-list '(m))
(cl:defmethod origin_alt-val ((m <GA_Planner-request>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader planners-srv:origin_alt-val is deprecated.  Use planners-srv:origin_alt instead.")
  (origin_alt m))

(cl:ensure-generic-function 'destination_lat-val :lambda-list '(m))
(cl:defmethod destination_lat-val ((m <GA_Planner-request>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader planners-srv:destination_lat-val is deprecated.  Use planners-srv:destination_lat instead.")
  (destination_lat m))

(cl:ensure-generic-function 'destination_long-val :lambda-list '(m))
(cl:defmethod destination_long-val ((m <GA_Planner-request>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader planners-srv:destination_long-val is deprecated.  Use planners-srv:destination_long instead.")
  (destination_long m))

(cl:ensure-generic-function 'destination_alt-val :lambda-list '(m))
(cl:defmethod destination_alt-val ((m <GA_Planner-request>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader planners-srv:destination_alt-val is deprecated.  Use planners-srv:destination_alt instead.")
  (destination_alt m))

(cl:ensure-generic-function 'map_id-val :lambda-list '(m))
(cl:defmethod map_id-val ((m <GA_Planner-request>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader planners-srv:map_id-val is deprecated.  Use planners-srv:map_id instead.")
  (map_id m))
(cl:defmethod roslisp-msg-protocol:serialize ((msg <GA_Planner-request>) ostream)
  "Serializes a message object of type '<GA_Planner-request>"
  (cl:let ((bits (roslisp-utils:encode-single-float-bits (cl:slot-value msg 'origin_lat))))
    (cl:write-byte (cl:ldb (cl:byte 8 0) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 8) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 16) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 24) bits) ostream))
  (cl:let ((bits (roslisp-utils:encode-single-float-bits (cl:slot-value msg 'origin_long))))
    (cl:write-byte (cl:ldb (cl:byte 8 0) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 8) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 16) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 24) bits) ostream))
  (cl:let ((bits (roslisp-utils:encode-single-float-bits (cl:slot-value msg 'origin_alt))))
    (cl:write-byte (cl:ldb (cl:byte 8 0) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 8) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 16) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 24) bits) ostream))
  (cl:let ((bits (roslisp-utils:encode-single-float-bits (cl:slot-value msg 'destination_lat))))
    (cl:write-byte (cl:ldb (cl:byte 8 0) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 8) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 16) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 24) bits) ostream))
  (cl:let ((bits (roslisp-utils:encode-single-float-bits (cl:slot-value msg 'destination_long))))
    (cl:write-byte (cl:ldb (cl:byte 8 0) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 8) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 16) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 24) bits) ostream))
  (cl:let ((bits (roslisp-utils:encode-single-float-bits (cl:slot-value msg 'destination_alt))))
    (cl:write-byte (cl:ldb (cl:byte 8 0) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 8) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 16) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 24) bits) ostream))
  (cl:let* ((signed (cl:slot-value msg 'map_id)) (unsigned (cl:if (cl:< signed 0) (cl:+ signed 4294967296) signed)))
    (cl:write-byte (cl:ldb (cl:byte 8 0) unsigned) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 8) unsigned) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 16) unsigned) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 24) unsigned) ostream)
    )
)
(cl:defmethod roslisp-msg-protocol:deserialize ((msg <GA_Planner-request>) istream)
  "Deserializes a message object of type '<GA_Planner-request>"
    (cl:let ((bits 0))
      (cl:setf (cl:ldb (cl:byte 8 0) bits) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 8) bits) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 16) bits) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 24) bits) (cl:read-byte istream))
    (cl:setf (cl:slot-value msg 'origin_lat) (roslisp-utils:decode-single-float-bits bits)))
    (cl:let ((bits 0))
      (cl:setf (cl:ldb (cl:byte 8 0) bits) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 8) bits) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 16) bits) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 24) bits) (cl:read-byte istream))
    (cl:setf (cl:slot-value msg 'origin_long) (roslisp-utils:decode-single-float-bits bits)))
    (cl:let ((bits 0))
      (cl:setf (cl:ldb (cl:byte 8 0) bits) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 8) bits) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 16) bits) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 24) bits) (cl:read-byte istream))
    (cl:setf (cl:slot-value msg 'origin_alt) (roslisp-utils:decode-single-float-bits bits)))
    (cl:let ((bits 0))
      (cl:setf (cl:ldb (cl:byte 8 0) bits) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 8) bits) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 16) bits) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 24) bits) (cl:read-byte istream))
    (cl:setf (cl:slot-value msg 'destination_lat) (roslisp-utils:decode-single-float-bits bits)))
    (cl:let ((bits 0))
      (cl:setf (cl:ldb (cl:byte 8 0) bits) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 8) bits) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 16) bits) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 24) bits) (cl:read-byte istream))
    (cl:setf (cl:slot-value msg 'destination_long) (roslisp-utils:decode-single-float-bits bits)))
    (cl:let ((bits 0))
      (cl:setf (cl:ldb (cl:byte 8 0) bits) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 8) bits) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 16) bits) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 24) bits) (cl:read-byte istream))
    (cl:setf (cl:slot-value msg 'destination_alt) (roslisp-utils:decode-single-float-bits bits)))
    (cl:let ((unsigned 0))
      (cl:setf (cl:ldb (cl:byte 8 0) unsigned) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 8) unsigned) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 16) unsigned) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 24) unsigned) (cl:read-byte istream))
      (cl:setf (cl:slot-value msg 'map_id) (cl:if (cl:< unsigned 2147483648) unsigned (cl:- unsigned 4294967296))))
  msg
)
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql '<GA_Planner-request>)))
  "Returns string type for a service object of type '<GA_Planner-request>"
  "planners/GA_PlannerRequest")
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql 'GA_Planner-request)))
  "Returns string type for a service object of type 'GA_Planner-request"
  "planners/GA_PlannerRequest")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql '<GA_Planner-request>)))
  "Returns md5sum for a message object of type '<GA_Planner-request>"
  "4d22e4fe344ad31e2831d2deed99337f")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql 'GA_Planner-request)))
  "Returns md5sum for a message object of type 'GA_Planner-request"
  "4d22e4fe344ad31e2831d2deed99337f")
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql '<GA_Planner-request>)))
  "Returns full string definition for message of type '<GA_Planner-request>"
  (cl:format cl:nil "float32 origin_lat~%float32 origin_long~%float32 origin_alt~%float32 destination_lat~%float32 destination_long~%float32 destination_alt~%int32 map_id~%~%~%"))
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql 'GA_Planner-request)))
  "Returns full string definition for message of type 'GA_Planner-request"
  (cl:format cl:nil "float32 origin_lat~%float32 origin_long~%float32 origin_alt~%float32 destination_lat~%float32 destination_long~%float32 destination_alt~%int32 map_id~%~%~%"))
(cl:defmethod roslisp-msg-protocol:serialization-length ((msg <GA_Planner-request>))
  (cl:+ 0
     4
     4
     4
     4
     4
     4
     4
))
(cl:defmethod roslisp-msg-protocol:ros-message-to-list ((msg <GA_Planner-request>))
  "Converts a ROS message object to a list"
  (cl:list 'GA_Planner-request
    (cl:cons ':origin_lat (origin_lat msg))
    (cl:cons ':origin_long (origin_long msg))
    (cl:cons ':origin_alt (origin_alt msg))
    (cl:cons ':destination_lat (destination_lat msg))
    (cl:cons ':destination_long (destination_long msg))
    (cl:cons ':destination_alt (destination_alt msg))
    (cl:cons ':map_id (map_id msg))
))
;//! \htmlinclude GA_Planner-response.msg.html

(cl:defclass <GA_Planner-response> (roslisp-msg-protocol:ros-message)
  ((wp_path
    :reader wp_path
    :initarg :wp_path
    :type cl:string
    :initform ""))
)

(cl:defclass GA_Planner-response (<GA_Planner-response>)
  ())

(cl:defmethod cl:initialize-instance :after ((m <GA_Planner-response>) cl:&rest args)
  (cl:declare (cl:ignorable args))
  (cl:unless (cl:typep m 'GA_Planner-response)
    (roslisp-msg-protocol:msg-deprecation-warning "using old message class name planners-srv:<GA_Planner-response> is deprecated: use planners-srv:GA_Planner-response instead.")))

(cl:ensure-generic-function 'wp_path-val :lambda-list '(m))
(cl:defmethod wp_path-val ((m <GA_Planner-response>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader planners-srv:wp_path-val is deprecated.  Use planners-srv:wp_path instead.")
  (wp_path m))
(cl:defmethod roslisp-msg-protocol:serialize ((msg <GA_Planner-response>) ostream)
  "Serializes a message object of type '<GA_Planner-response>"
  (cl:let ((__ros_str_len (cl:length (cl:slot-value msg 'wp_path))))
    (cl:write-byte (cl:ldb (cl:byte 8 0) __ros_str_len) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 8) __ros_str_len) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 16) __ros_str_len) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 24) __ros_str_len) ostream))
  (cl:map cl:nil #'(cl:lambda (c) (cl:write-byte (cl:char-code c) ostream)) (cl:slot-value msg 'wp_path))
)
(cl:defmethod roslisp-msg-protocol:deserialize ((msg <GA_Planner-response>) istream)
  "Deserializes a message object of type '<GA_Planner-response>"
    (cl:let ((__ros_str_len 0))
      (cl:setf (cl:ldb (cl:byte 8 0) __ros_str_len) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 8) __ros_str_len) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 16) __ros_str_len) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 24) __ros_str_len) (cl:read-byte istream))
      (cl:setf (cl:slot-value msg 'wp_path) (cl:make-string __ros_str_len))
      (cl:dotimes (__ros_str_idx __ros_str_len msg)
        (cl:setf (cl:char (cl:slot-value msg 'wp_path) __ros_str_idx) (cl:code-char (cl:read-byte istream)))))
  msg
)
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql '<GA_Planner-response>)))
  "Returns string type for a service object of type '<GA_Planner-response>"
  "planners/GA_PlannerResponse")
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql 'GA_Planner-response)))
  "Returns string type for a service object of type 'GA_Planner-response"
  "planners/GA_PlannerResponse")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql '<GA_Planner-response>)))
  "Returns md5sum for a message object of type '<GA_Planner-response>"
  "4d22e4fe344ad31e2831d2deed99337f")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql 'GA_Planner-response)))
  "Returns md5sum for a message object of type 'GA_Planner-response"
  "4d22e4fe344ad31e2831d2deed99337f")
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql '<GA_Planner-response>)))
  "Returns full string definition for message of type '<GA_Planner-response>"
  (cl:format cl:nil "string wp_path~%~%~%~%"))
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql 'GA_Planner-response)))
  "Returns full string definition for message of type 'GA_Planner-response"
  (cl:format cl:nil "string wp_path~%~%~%~%"))
(cl:defmethod roslisp-msg-protocol:serialization-length ((msg <GA_Planner-response>))
  (cl:+ 0
     4 (cl:length (cl:slot-value msg 'wp_path))
))
(cl:defmethod roslisp-msg-protocol:ros-message-to-list ((msg <GA_Planner-response>))
  "Converts a ROS message object to a list"
  (cl:list 'GA_Planner-response
    (cl:cons ':wp_path (wp_path msg))
))
(cl:defmethod roslisp-msg-protocol:service-request-type ((msg (cl:eql 'GA_Planner)))
  'GA_Planner-request)
(cl:defmethod roslisp-msg-protocol:service-response-type ((msg (cl:eql 'GA_Planner)))
  'GA_Planner-response)
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql 'GA_Planner)))
  "Returns string type for a service object of type '<GA_Planner>"
  "planners/GA_Planner")