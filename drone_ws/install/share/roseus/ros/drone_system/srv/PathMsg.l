;; Auto-generated. Do not edit!


(when (boundp 'drone_system::PathMsg)
  (if (not (find-package "DRONE_SYSTEM"))
    (make-package "DRONE_SYSTEM"))
  (shadow 'PathMsg (find-package "DRONE_SYSTEM")))
(unless (find-package "DRONE_SYSTEM::PATHMSG")
  (make-package "DRONE_SYSTEM::PATHMSG"))
(unless (find-package "DRONE_SYSTEM::PATHMSGREQUEST")
  (make-package "DRONE_SYSTEM::PATHMSGREQUEST"))
(unless (find-package "DRONE_SYSTEM::PATHMSGRESPONSE")
  (make-package "DRONE_SYSTEM::PATHMSGRESPONSE"))

(in-package "ROS")





(defclass drone_system::PathMsgRequest
  :super ros::object
  :slots (_a ))

(defmethod drone_system::PathMsgRequest
  (:init
   (&key
    ((:a __a) 0)
    )
   (send-super :init)
   (setq _a (round __a))
   self)
  (:a
   (&optional __a)
   (if __a (setq _a __a)) _a)
  (:serialization-length
   ()
   (+
    ;; int64 _a
    8
    ))
  (:serialize
   (&optional strm)
   (let ((s (if strm strm
              (make-string-output-stream (send self :serialization-length)))))
     ;; int64 _a
#+(or :alpha :irix6 :x86_64)
       (progn (sys::poke _a (send s :buffer) (send s :count) :long) (incf (stream-count s) 8))
#-(or :alpha :irix6 :x86_64)
       (cond ((and (class _a) (= (length (_a . bv)) 2)) ;; bignum
              (write-long (ash (elt (_a . bv) 0) 0) s)
              (write-long (ash (elt (_a . bv) 1) -1) s))
             ((and (class _a) (= (length (_a . bv)) 1)) ;; big1
              (write-long (elt (_a . bv) 0) s)
              (write-long (if (>= _a 0) 0 #xffffffff) s))
             (t                                         ;; integer
              (write-long _a s)(write-long (if (>= _a 0) 0 #xffffffff) s)))
     ;;
     (if (null strm) (get-output-stream-string s))))
  (:deserialize
   (buf &optional (ptr- 0))
   ;; int64 _a
#+(or :alpha :irix6 :x86_64)
      (setf _a (prog1 (sys::peek buf ptr- :long) (incf ptr- 8)))
#-(or :alpha :irix6 :x86_64)
      (setf _a (let ((b0 (prog1 (sys::peek buf ptr- :integer) (incf ptr- 4)))
                  (b1 (prog1 (sys::peek buf ptr- :integer) (incf ptr- 4))))
              (cond ((= b1 -1) b0)
                     ((and (= b1  0)
                           (<= lisp::most-negative-fixnum b0 lisp::most-positive-fixnum))
                      b0)
                    ((= b1  0) (make-instance bignum :size 1 :bv (integer-vector b0)))
                    (t (make-instance bignum :size 2 :bv (integer-vector b0 (ash b1 1)))))))
   ;;
   self)
  )

(defclass drone_system::PathMsgResponse
  :super ros::object
  :slots (_b ))

(defmethod drone_system::PathMsgResponse
  (:init
   (&key
    ((:b __b) 0)
    )
   (send-super :init)
   (setq _b (round __b))
   self)
  (:b
   (&optional __b)
   (if __b (setq _b __b)) _b)
  (:serialization-length
   ()
   (+
    ;; int64 _b
    8
    ))
  (:serialize
   (&optional strm)
   (let ((s (if strm strm
              (make-string-output-stream (send self :serialization-length)))))
     ;; int64 _b
#+(or :alpha :irix6 :x86_64)
       (progn (sys::poke _b (send s :buffer) (send s :count) :long) (incf (stream-count s) 8))
#-(or :alpha :irix6 :x86_64)
       (cond ((and (class _b) (= (length (_b . bv)) 2)) ;; bignum
              (write-long (ash (elt (_b . bv) 0) 0) s)
              (write-long (ash (elt (_b . bv) 1) -1) s))
             ((and (class _b) (= (length (_b . bv)) 1)) ;; big1
              (write-long (elt (_b . bv) 0) s)
              (write-long (if (>= _b 0) 0 #xffffffff) s))
             (t                                         ;; integer
              (write-long _b s)(write-long (if (>= _b 0) 0 #xffffffff) s)))
     ;;
     (if (null strm) (get-output-stream-string s))))
  (:deserialize
   (buf &optional (ptr- 0))
   ;; int64 _b
#+(or :alpha :irix6 :x86_64)
      (setf _b (prog1 (sys::peek buf ptr- :long) (incf ptr- 8)))
#-(or :alpha :irix6 :x86_64)
      (setf _b (let ((b0 (prog1 (sys::peek buf ptr- :integer) (incf ptr- 4)))
                  (b1 (prog1 (sys::peek buf ptr- :integer) (incf ptr- 4))))
              (cond ((= b1 -1) b0)
                     ((and (= b1  0)
                           (<= lisp::most-negative-fixnum b0 lisp::most-positive-fixnum))
                      b0)
                    ((= b1  0) (make-instance bignum :size 1 :bv (integer-vector b0)))
                    (t (make-instance bignum :size 2 :bv (integer-vector b0 (ash b1 1)))))))
   ;;
   self)
  )

(defclass drone_system::PathMsg
  :super ros::object
  :slots ())

(setf (get drone_system::PathMsg :md5sum-) "f16097f93022db785b2cc9436c158893")
(setf (get drone_system::PathMsg :datatype-) "drone_system/PathMsg")
(setf (get drone_system::PathMsg :request) drone_system::PathMsgRequest)
(setf (get drone_system::PathMsg :response) drone_system::PathMsgResponse)

(defmethod drone_system::PathMsgRequest
  (:response () (instance drone_system::PathMsgResponse :init)))

(setf (get drone_system::PathMsgRequest :md5sum-) "f16097f93022db785b2cc9436c158893")
(setf (get drone_system::PathMsgRequest :datatype-) "drone_system/PathMsgRequest")
(setf (get drone_system::PathMsgRequest :definition-)
      "int64 a
---
int64 b
")

(setf (get drone_system::PathMsgResponse :md5sum-) "f16097f93022db785b2cc9436c158893")
(setf (get drone_system::PathMsgResponse :datatype-) "drone_system/PathMsgResponse")
(setf (get drone_system::PathMsgResponse :definition-)
      "int64 a
---
int64 b
")



(provide :drone_system/PathMsg "f16097f93022db785b2cc9436c158893")


