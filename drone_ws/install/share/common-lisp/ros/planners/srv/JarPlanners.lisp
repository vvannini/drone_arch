; Auto-generated. Do not edit!


(cl:in-package planners-srv)


;//! \htmlinclude JarPlanners-request.msg.html

(cl:defclass <JarPlanners-request> (roslisp-msg-protocol:ros-message)
  ((pedido
    :reader pedido
    :initarg :pedido
    :type cl:integer
    :initform 0))
)

(cl:defclass JarPlanners-request (<JarPlanners-request>)
  ())

(cl:defmethod cl:initialize-instance :after ((m <JarPlanners-request>) cl:&rest args)
  (cl:declare (cl:ignorable args))
  (cl:unless (cl:typep m 'JarPlanners-request)
    (roslisp-msg-protocol:msg-deprecation-warning "using old message class name planners-srv:<JarPlanners-request> is deprecated: use planners-srv:JarPlanners-request instead.")))

(cl:ensure-generic-function 'pedido-val :lambda-list '(m))
(cl:defmethod pedido-val ((m <JarPlanners-request>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader planners-srv:pedido-val is deprecated.  Use planners-srv:pedido instead.")
  (pedido m))
(cl:defmethod roslisp-msg-protocol:serialize ((msg <JarPlanners-request>) ostream)
  "Serializes a message object of type '<JarPlanners-request>"
  (cl:let* ((signed (cl:slot-value msg 'pedido)) (unsigned (cl:if (cl:< signed 0) (cl:+ signed 18446744073709551616) signed)))
    (cl:write-byte (cl:ldb (cl:byte 8 0) unsigned) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 8) unsigned) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 16) unsigned) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 24) unsigned) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 32) unsigned) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 40) unsigned) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 48) unsigned) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 56) unsigned) ostream)
    )
)
(cl:defmethod roslisp-msg-protocol:deserialize ((msg <JarPlanners-request>) istream)
  "Deserializes a message object of type '<JarPlanners-request>"
    (cl:let ((unsigned 0))
      (cl:setf (cl:ldb (cl:byte 8 0) unsigned) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 8) unsigned) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 16) unsigned) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 24) unsigned) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 32) unsigned) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 40) unsigned) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 48) unsigned) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 56) unsigned) (cl:read-byte istream))
      (cl:setf (cl:slot-value msg 'pedido) (cl:if (cl:< unsigned 9223372036854775808) unsigned (cl:- unsigned 18446744073709551616))))
  msg
)
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql '<JarPlanners-request>)))
  "Returns string type for a service object of type '<JarPlanners-request>"
  "planners/JarPlannersRequest")
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql 'JarPlanners-request)))
  "Returns string type for a service object of type 'JarPlanners-request"
  "planners/JarPlannersRequest")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql '<JarPlanners-request>)))
  "Returns md5sum for a message object of type '<JarPlanners-request>"
  "4227327d0bce5bc51aba1221199a837b")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql 'JarPlanners-request)))
  "Returns md5sum for a message object of type 'JarPlanners-request"
  "4227327d0bce5bc51aba1221199a837b")
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql '<JarPlanners-request>)))
  "Returns full string definition for message of type '<JarPlanners-request>"
  (cl:format cl:nil "int64 pedido~%~%~%"))
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql 'JarPlanners-request)))
  "Returns full string definition for message of type 'JarPlanners-request"
  (cl:format cl:nil "int64 pedido~%~%~%"))
(cl:defmethod roslisp-msg-protocol:serialization-length ((msg <JarPlanners-request>))
  (cl:+ 0
     8
))
(cl:defmethod roslisp-msg-protocol:ros-message-to-list ((msg <JarPlanners-request>))
  "Converts a ROS message object to a list"
  (cl:list 'JarPlanners-request
    (cl:cons ':pedido (pedido msg))
))
;//! \htmlinclude JarPlanners-response.msg.html

(cl:defclass <JarPlanners-response> (roslisp-msg-protocol:ros-message)
  ((result
    :reader result
    :initarg :result
    :type cl:string
    :initform ""))
)

(cl:defclass JarPlanners-response (<JarPlanners-response>)
  ())

(cl:defmethod cl:initialize-instance :after ((m <JarPlanners-response>) cl:&rest args)
  (cl:declare (cl:ignorable args))
  (cl:unless (cl:typep m 'JarPlanners-response)
    (roslisp-msg-protocol:msg-deprecation-warning "using old message class name planners-srv:<JarPlanners-response> is deprecated: use planners-srv:JarPlanners-response instead.")))

(cl:ensure-generic-function 'result-val :lambda-list '(m))
(cl:defmethod result-val ((m <JarPlanners-response>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader planners-srv:result-val is deprecated.  Use planners-srv:result instead.")
  (result m))
(cl:defmethod roslisp-msg-protocol:serialize ((msg <JarPlanners-response>) ostream)
  "Serializes a message object of type '<JarPlanners-response>"
  (cl:let ((__ros_str_len (cl:length (cl:slot-value msg 'result))))
    (cl:write-byte (cl:ldb (cl:byte 8 0) __ros_str_len) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 8) __ros_str_len) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 16) __ros_str_len) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 24) __ros_str_len) ostream))
  (cl:map cl:nil #'(cl:lambda (c) (cl:write-byte (cl:char-code c) ostream)) (cl:slot-value msg 'result))
)
(cl:defmethod roslisp-msg-protocol:deserialize ((msg <JarPlanners-response>) istream)
  "Deserializes a message object of type '<JarPlanners-response>"
    (cl:let ((__ros_str_len 0))
      (cl:setf (cl:ldb (cl:byte 8 0) __ros_str_len) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 8) __ros_str_len) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 16) __ros_str_len) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 24) __ros_str_len) (cl:read-byte istream))
      (cl:setf (cl:slot-value msg 'result) (cl:make-string __ros_str_len))
      (cl:dotimes (__ros_str_idx __ros_str_len msg)
        (cl:setf (cl:char (cl:slot-value msg 'result) __ros_str_idx) (cl:code-char (cl:read-byte istream)))))
  msg
)
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql '<JarPlanners-response>)))
  "Returns string type for a service object of type '<JarPlanners-response>"
  "planners/JarPlannersResponse")
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql 'JarPlanners-response)))
  "Returns string type for a service object of type 'JarPlanners-response"
  "planners/JarPlannersResponse")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql '<JarPlanners-response>)))
  "Returns md5sum for a message object of type '<JarPlanners-response>"
  "4227327d0bce5bc51aba1221199a837b")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql 'JarPlanners-response)))
  "Returns md5sum for a message object of type 'JarPlanners-response"
  "4227327d0bce5bc51aba1221199a837b")
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql '<JarPlanners-response>)))
  "Returns full string definition for message of type '<JarPlanners-response>"
  (cl:format cl:nil "string result~%~%~%"))
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql 'JarPlanners-response)))
  "Returns full string definition for message of type 'JarPlanners-response"
  (cl:format cl:nil "string result~%~%~%"))
(cl:defmethod roslisp-msg-protocol:serialization-length ((msg <JarPlanners-response>))
  (cl:+ 0
     4 (cl:length (cl:slot-value msg 'result))
))
(cl:defmethod roslisp-msg-protocol:ros-message-to-list ((msg <JarPlanners-response>))
  "Converts a ROS message object to a list"
  (cl:list 'JarPlanners-response
    (cl:cons ':result (result msg))
))
(cl:defmethod roslisp-msg-protocol:service-request-type ((msg (cl:eql 'JarPlanners)))
  'JarPlanners-request)
(cl:defmethod roslisp-msg-protocol:service-response-type ((msg (cl:eql 'JarPlanners)))
  'JarPlanners-response)
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql 'JarPlanners)))
  "Returns string type for a service object of type '<JarPlanners>"
  "planners/JarPlanners")