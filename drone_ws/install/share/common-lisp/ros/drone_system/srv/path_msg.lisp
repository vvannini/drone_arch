; Auto-generated. Do not edit!


(cl:in-package drone_system-srv)


;//! \htmlinclude path_msg-request.msg.html

(cl:defclass <path_msg-request> (roslisp-msg-protocol:ros-message)
  ((option
    :reader option
    :initarg :option
    :type cl:string
    :initform ""))
)

(cl:defclass path_msg-request (<path_msg-request>)
  ())

(cl:defmethod cl:initialize-instance :after ((m <path_msg-request>) cl:&rest args)
  (cl:declare (cl:ignorable args))
  (cl:unless (cl:typep m 'path_msg-request)
    (roslisp-msg-protocol:msg-deprecation-warning "using old message class name drone_system-srv:<path_msg-request> is deprecated: use drone_system-srv:path_msg-request instead.")))

(cl:ensure-generic-function 'option-val :lambda-list '(m))
(cl:defmethod option-val ((m <path_msg-request>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader drone_system-srv:option-val is deprecated.  Use drone_system-srv:option instead.")
  (option m))
(cl:defmethod roslisp-msg-protocol:serialize ((msg <path_msg-request>) ostream)
  "Serializes a message object of type '<path_msg-request>"
  (cl:let ((__ros_str_len (cl:length (cl:slot-value msg 'option))))
    (cl:write-byte (cl:ldb (cl:byte 8 0) __ros_str_len) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 8) __ros_str_len) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 16) __ros_str_len) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 24) __ros_str_len) ostream))
  (cl:map cl:nil #'(cl:lambda (c) (cl:write-byte (cl:char-code c) ostream)) (cl:slot-value msg 'option))
)
(cl:defmethod roslisp-msg-protocol:deserialize ((msg <path_msg-request>) istream)
  "Deserializes a message object of type '<path_msg-request>"
    (cl:let ((__ros_str_len 0))
      (cl:setf (cl:ldb (cl:byte 8 0) __ros_str_len) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 8) __ros_str_len) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 16) __ros_str_len) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 24) __ros_str_len) (cl:read-byte istream))
      (cl:setf (cl:slot-value msg 'option) (cl:make-string __ros_str_len))
      (cl:dotimes (__ros_str_idx __ros_str_len msg)
        (cl:setf (cl:char (cl:slot-value msg 'option) __ros_str_idx) (cl:code-char (cl:read-byte istream)))))
  msg
)
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql '<path_msg-request>)))
  "Returns string type for a service object of type '<path_msg-request>"
  "drone_system/path_msgRequest")
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql 'path_msg-request)))
  "Returns string type for a service object of type 'path_msg-request"
  "drone_system/path_msgRequest")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql '<path_msg-request>)))
  "Returns md5sum for a message object of type '<path_msg-request>"
  "c48136865ebee6f8ec8dec3519522f22")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql 'path_msg-request)))
  "Returns md5sum for a message object of type 'path_msg-request"
  "c48136865ebee6f8ec8dec3519522f22")
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql '<path_msg-request>)))
  "Returns full string definition for message of type '<path_msg-request>"
  (cl:format cl:nil "string option~%~%~%"))
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql 'path_msg-request)))
  "Returns full string definition for message of type 'path_msg-request"
  (cl:format cl:nil "string option~%~%~%"))
(cl:defmethod roslisp-msg-protocol:serialization-length ((msg <path_msg-request>))
  (cl:+ 0
     4 (cl:length (cl:slot-value msg 'option))
))
(cl:defmethod roslisp-msg-protocol:ros-message-to-list ((msg <path_msg-request>))
  "Converts a ROS message object to a list"
  (cl:list 'path_msg-request
    (cl:cons ':option (option msg))
))
;//! \htmlinclude path_msg-response.msg.html

(cl:defclass <path_msg-response> (roslisp-msg-protocol:ros-message)
  ((path
    :reader path
    :initarg :path
    :type cl:string
    :initform ""))
)

(cl:defclass path_msg-response (<path_msg-response>)
  ())

(cl:defmethod cl:initialize-instance :after ((m <path_msg-response>) cl:&rest args)
  (cl:declare (cl:ignorable args))
  (cl:unless (cl:typep m 'path_msg-response)
    (roslisp-msg-protocol:msg-deprecation-warning "using old message class name drone_system-srv:<path_msg-response> is deprecated: use drone_system-srv:path_msg-response instead.")))

(cl:ensure-generic-function 'path-val :lambda-list '(m))
(cl:defmethod path-val ((m <path_msg-response>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader drone_system-srv:path-val is deprecated.  Use drone_system-srv:path instead.")
  (path m))
(cl:defmethod roslisp-msg-protocol:serialize ((msg <path_msg-response>) ostream)
  "Serializes a message object of type '<path_msg-response>"
  (cl:let ((__ros_str_len (cl:length (cl:slot-value msg 'path))))
    (cl:write-byte (cl:ldb (cl:byte 8 0) __ros_str_len) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 8) __ros_str_len) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 16) __ros_str_len) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 24) __ros_str_len) ostream))
  (cl:map cl:nil #'(cl:lambda (c) (cl:write-byte (cl:char-code c) ostream)) (cl:slot-value msg 'path))
)
(cl:defmethod roslisp-msg-protocol:deserialize ((msg <path_msg-response>) istream)
  "Deserializes a message object of type '<path_msg-response>"
    (cl:let ((__ros_str_len 0))
      (cl:setf (cl:ldb (cl:byte 8 0) __ros_str_len) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 8) __ros_str_len) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 16) __ros_str_len) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 24) __ros_str_len) (cl:read-byte istream))
      (cl:setf (cl:slot-value msg 'path) (cl:make-string __ros_str_len))
      (cl:dotimes (__ros_str_idx __ros_str_len msg)
        (cl:setf (cl:char (cl:slot-value msg 'path) __ros_str_idx) (cl:code-char (cl:read-byte istream)))))
  msg
)
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql '<path_msg-response>)))
  "Returns string type for a service object of type '<path_msg-response>"
  "drone_system/path_msgResponse")
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql 'path_msg-response)))
  "Returns string type for a service object of type 'path_msg-response"
  "drone_system/path_msgResponse")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql '<path_msg-response>)))
  "Returns md5sum for a message object of type '<path_msg-response>"
  "c48136865ebee6f8ec8dec3519522f22")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql 'path_msg-response)))
  "Returns md5sum for a message object of type 'path_msg-response"
  "c48136865ebee6f8ec8dec3519522f22")
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql '<path_msg-response>)))
  "Returns full string definition for message of type '<path_msg-response>"
  (cl:format cl:nil "string path~%~%~%"))
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql 'path_msg-response)))
  "Returns full string definition for message of type 'path_msg-response"
  (cl:format cl:nil "string path~%~%~%"))
(cl:defmethod roslisp-msg-protocol:serialization-length ((msg <path_msg-response>))
  (cl:+ 0
     4 (cl:length (cl:slot-value msg 'path))
))
(cl:defmethod roslisp-msg-protocol:ros-message-to-list ((msg <path_msg-response>))
  "Converts a ROS message object to a list"
  (cl:list 'path_msg-response
    (cl:cons ':path (path msg))
))
(cl:defmethod roslisp-msg-protocol:service-request-type ((msg (cl:eql 'path_msg)))
  'path_msg-request)
(cl:defmethod roslisp-msg-protocol:service-response-type ((msg (cl:eql 'path_msg)))
  'path_msg-response)
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql 'path_msg)))
  "Returns string type for a service object of type '<path_msg>"
  "drone_system/path_msg")