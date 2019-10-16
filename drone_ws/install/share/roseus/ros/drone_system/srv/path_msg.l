;; Auto-generated. Do not edit!


(when (boundp 'drone_system::path_msg)
  (if (not (find-package "DRONE_SYSTEM"))
    (make-package "DRONE_SYSTEM"))
  (shadow 'path_msg (find-package "DRONE_SYSTEM")))
(unless (find-package "DRONE_SYSTEM::PATH_MSG")
  (make-package "DRONE_SYSTEM::PATH_MSG"))
(unless (find-package "DRONE_SYSTEM::PATH_MSGREQUEST")
  (make-package "DRONE_SYSTEM::PATH_MSGREQUEST"))
(unless (find-package "DRONE_SYSTEM::PATH_MSGRESPONSE")
  (make-package "DRONE_SYSTEM::PATH_MSGRESPONSE"))

(in-package "ROS")





(defclass drone_system::path_msgRequest
  :super ros::object
  :slots (_option ))

(defmethod drone_system::path_msgRequest
  (:init
   (&key
    ((:option __option) "")
    )
   (send-super :init)
   (setq _option (string __option))
   self)
  (:option
   (&optional __option)
   (if __option (setq _option __option)) _option)
  (:serialization-length
   ()
   (+
    ;; string _option
    4 (length _option)
    ))
  (:serialize
   (&optional strm)
   (let ((s (if strm strm
              (make-string-output-stream (send self :serialization-length)))))
     ;; string _option
       (write-long (length _option) s) (princ _option s)
     ;;
     (if (null strm) (get-output-stream-string s))))
  (:deserialize
   (buf &optional (ptr- 0))
   ;; string _option
     (let (n) (setq n (sys::peek buf ptr- :integer)) (incf ptr- 4) (setq _option (subseq buf ptr- (+ ptr- n))) (incf ptr- n))
   ;;
   self)
  )

(defclass drone_system::path_msgResponse
  :super ros::object
  :slots (_path ))

(defmethod drone_system::path_msgResponse
  (:init
   (&key
    ((:path __path) "")
    )
   (send-super :init)
   (setq _path (string __path))
   self)
  (:path
   (&optional __path)
   (if __path (setq _path __path)) _path)
  (:serialization-length
   ()
   (+
    ;; string _path
    4 (length _path)
    ))
  (:serialize
   (&optional strm)
   (let ((s (if strm strm
              (make-string-output-stream (send self :serialization-length)))))
     ;; string _path
       (write-long (length _path) s) (princ _path s)
     ;;
     (if (null strm) (get-output-stream-string s))))
  (:deserialize
   (buf &optional (ptr- 0))
   ;; string _path
     (let (n) (setq n (sys::peek buf ptr- :integer)) (incf ptr- 4) (setq _path (subseq buf ptr- (+ ptr- n))) (incf ptr- n))
   ;;
   self)
  )

(defclass drone_system::path_msg
  :super ros::object
  :slots ())

(setf (get drone_system::path_msg :md5sum-) "c48136865ebee6f8ec8dec3519522f22")
(setf (get drone_system::path_msg :datatype-) "drone_system/path_msg")
(setf (get drone_system::path_msg :request) drone_system::path_msgRequest)
(setf (get drone_system::path_msg :response) drone_system::path_msgResponse)

(defmethod drone_system::path_msgRequest
  (:response () (instance drone_system::path_msgResponse :init)))

(setf (get drone_system::path_msgRequest :md5sum-) "c48136865ebee6f8ec8dec3519522f22")
(setf (get drone_system::path_msgRequest :datatype-) "drone_system/path_msgRequest")
(setf (get drone_system::path_msgRequest :definition-)
      "string option
---
string path
")

(setf (get drone_system::path_msgResponse :md5sum-) "c48136865ebee6f8ec8dec3519522f22")
(setf (get drone_system::path_msgResponse :datatype-) "drone_system/path_msgResponse")
(setf (get drone_system::path_msgResponse :definition-)
      "string option
---
string path
")



(provide :drone_system/path_msg "c48136865ebee6f8ec8dec3519522f22")


