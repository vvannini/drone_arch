; Auto-generated. Do not edit!


(cl:in-package drone_system-srv)


;//! \htmlinclude PathMsg-request.msg.html

(cl:defclass <PathMsg-request> (roslisp-msg-protocol:ros-message)
  ((a
    :reader a
    :initarg :a
    :type cl:integer
    :initform 0))
)

(cl:defclass PathMsg-request (<PathMsg-request>)
  ())

(cl:defmethod cl:initialize-instance :after ((m <PathMsg-request>) cl:&rest args)
  (cl:declare (cl:ignorable args))
  (cl:unless (cl:typep m 'PathMsg-request)
    (roslisp-msg-protocol:msg-deprecation-warning "using old message class name drone_system-srv:<PathMsg-request> is deprecated: use drone_system-srv:PathMsg-request instead.")))

(cl:ensure-generic-function 'a-val :lambda-list '(m))
(cl:defmethod a-val ((m <PathMsg-request>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader drone_system-srv:a-val is deprecated.  Use drone_system-srv:a instead.")
  (a m))
(cl:defmethod roslisp-msg-protocol:serialize ((msg <PathMsg-request>) ostream)
  "Serializes a message object of type '<PathMsg-request>"
  (cl:let* ((signed (cl:slot-value msg 'a)) (unsigned (cl:if (cl:< signed 0) (cl:+ signed 18446744073709551616) signed)))
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
(cl:defmethod roslisp-msg-protocol:deserialize ((msg <PathMsg-request>) istream)
  "Deserializes a message object of type '<PathMsg-request>"
    (cl:let ((unsigned 0))
      (cl:setf (cl:ldb (cl:byte 8 0) unsigned) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 8) unsigned) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 16) unsigned) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 24) unsigned) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 32) unsigned) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 40) unsigned) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 48) unsigned) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 56) unsigned) (cl:read-byte istream))
      (cl:setf (cl:slot-value msg 'a) (cl:if (cl:< unsigned 9223372036854775808) unsigned (cl:- unsigned 18446744073709551616))))
  msg
)
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql '<PathMsg-request>)))
  "Returns string type for a service object of type '<PathMsg-request>"
  "drone_system/PathMsgRequest")
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql 'PathMsg-request)))
  "Returns string type for a service object of type 'PathMsg-request"
  "drone_system/PathMsgRequest")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql '<PathMsg-request>)))
  "Returns md5sum for a message object of type '<PathMsg-request>"
  "f16097f93022db785b2cc9436c158893")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql 'PathMsg-request)))
  "Returns md5sum for a message object of type 'PathMsg-request"
  "f16097f93022db785b2cc9436c158893")
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql '<PathMsg-request>)))
  "Returns full string definition for message of type '<PathMsg-request>"
  (cl:format cl:nil "int64 a~%~%~%"))
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql 'PathMsg-request)))
  "Returns full string definition for message of type 'PathMsg-request"
  (cl:format cl:nil "int64 a~%~%~%"))
(cl:defmethod roslisp-msg-protocol:serialization-length ((msg <PathMsg-request>))
  (cl:+ 0
     8
))
(cl:defmethod roslisp-msg-protocol:ros-message-to-list ((msg <PathMsg-request>))
  "Converts a ROS message object to a list"
  (cl:list 'PathMsg-request
    (cl:cons ':a (a msg))
))
;//! \htmlinclude PathMsg-response.msg.html

(cl:defclass <PathMsg-response> (roslisp-msg-protocol:ros-message)
  ((b
    :reader b
    :initarg :b
    :type cl:integer
    :initform 0))
)

(cl:defclass PathMsg-response (<PathMsg-response>)
  ())

(cl:defmethod cl:initialize-instance :after ((m <PathMsg-response>) cl:&rest args)
  (cl:declare (cl:ignorable args))
  (cl:unless (cl:typep m 'PathMsg-response)
    (roslisp-msg-protocol:msg-deprecation-warning "using old message class name drone_system-srv:<PathMsg-response> is deprecated: use drone_system-srv:PathMsg-response instead.")))

(cl:ensure-generic-function 'b-val :lambda-list '(m))
(cl:defmethod b-val ((m <PathMsg-response>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader drone_system-srv:b-val is deprecated.  Use drone_system-srv:b instead.")
  (b m))
(cl:defmethod roslisp-msg-protocol:serialize ((msg <PathMsg-response>) ostream)
  "Serializes a message object of type '<PathMsg-response>"
  (cl:let* ((signed (cl:slot-value msg 'b)) (unsigned (cl:if (cl:< signed 0) (cl:+ signed 18446744073709551616) signed)))
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
(cl:defmethod roslisp-msg-protocol:deserialize ((msg <PathMsg-response>) istream)
  "Deserializes a message object of type '<PathMsg-response>"
    (cl:let ((unsigned 0))
      (cl:setf (cl:ldb (cl:byte 8 0) unsigned) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 8) unsigned) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 16) unsigned) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 24) unsigned) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 32) unsigned) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 40) unsigned) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 48) unsigned) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 56) unsigned) (cl:read-byte istream))
      (cl:setf (cl:slot-value msg 'b) (cl:if (cl:< unsigned 9223372036854775808) unsigned (cl:- unsigned 18446744073709551616))))
  msg
)
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql '<PathMsg-response>)))
  "Returns string type for a service object of type '<PathMsg-response>"
  "drone_system/PathMsgResponse")
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql 'PathMsg-response)))
  "Returns string type for a service object of type 'PathMsg-response"
  "drone_system/PathMsgResponse")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql '<PathMsg-response>)))
  "Returns md5sum for a message object of type '<PathMsg-response>"
  "f16097f93022db785b2cc9436c158893")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql 'PathMsg-response)))
  "Returns md5sum for a message object of type 'PathMsg-response"
  "f16097f93022db785b2cc9436c158893")
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql '<PathMsg-response>)))
  "Returns full string definition for message of type '<PathMsg-response>"
  (cl:format cl:nil "int64 b~%~%~%"))
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql 'PathMsg-response)))
  "Returns full string definition for message of type 'PathMsg-response"
  (cl:format cl:nil "int64 b~%~%~%"))
(cl:defmethod roslisp-msg-protocol:serialization-length ((msg <PathMsg-response>))
  (cl:+ 0
     8
))
(cl:defmethod roslisp-msg-protocol:ros-message-to-list ((msg <PathMsg-response>))
  "Converts a ROS message object to a list"
  (cl:list 'PathMsg-response
    (cl:cons ':b (b msg))
))
(cl:defmethod roslisp-msg-protocol:service-request-type ((msg (cl:eql 'PathMsg)))
  'PathMsg-request)
(cl:defmethod roslisp-msg-protocol:service-response-type ((msg (cl:eql 'PathMsg)))
  'PathMsg-response)
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql 'PathMsg)))
  "Returns string type for a service object of type '<PathMsg>"
  "drone_system/PathMsg")