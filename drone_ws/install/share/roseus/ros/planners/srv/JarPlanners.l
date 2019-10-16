;; Auto-generated. Do not edit!


(when (boundp 'planners::JarPlanners)
  (if (not (find-package "PLANNERS"))
    (make-package "PLANNERS"))
  (shadow 'JarPlanners (find-package "PLANNERS")))
(unless (find-package "PLANNERS::JARPLANNERS")
  (make-package "PLANNERS::JARPLANNERS"))
(unless (find-package "PLANNERS::JARPLANNERSREQUEST")
  (make-package "PLANNERS::JARPLANNERSREQUEST"))
(unless (find-package "PLANNERS::JARPLANNERSRESPONSE")
  (make-package "PLANNERS::JARPLANNERSRESPONSE"))

(in-package "ROS")





(defclass planners::JarPlannersRequest
  :super ros::object
  :slots (_pedido ))

(defmethod planners::JarPlannersRequest
  (:init
   (&key
    ((:pedido __pedido) 0)
    )
   (send-super :init)
   (setq _pedido (round __pedido))
   self)
  (:pedido
   (&optional __pedido)
   (if __pedido (setq _pedido __pedido)) _pedido)
  (:serialization-length
   ()
   (+
    ;; int64 _pedido
    8
    ))
  (:serialize
   (&optional strm)
   (let ((s (if strm strm
              (make-string-output-stream (send self :serialization-length)))))
     ;; int64 _pedido
#+(or :alpha :irix6 :x86_64)
       (progn (sys::poke _pedido (send s :buffer) (send s :count) :long) (incf (stream-count s) 8))
#-(or :alpha :irix6 :x86_64)
       (cond ((and (class _pedido) (= (length (_pedido . bv)) 2)) ;; bignum
              (write-long (ash (elt (_pedido . bv) 0) 0) s)
              (write-long (ash (elt (_pedido . bv) 1) -1) s))
             ((and (class _pedido) (= (length (_pedido . bv)) 1)) ;; big1
              (write-long (elt (_pedido . bv) 0) s)
              (write-long (if (>= _pedido 0) 0 #xffffffff) s))
             (t                                         ;; integer
              (write-long _pedido s)(write-long (if (>= _pedido 0) 0 #xffffffff) s)))
     ;;
     (if (null strm) (get-output-stream-string s))))
  (:deserialize
   (buf &optional (ptr- 0))
   ;; int64 _pedido
#+(or :alpha :irix6 :x86_64)
      (setf _pedido (prog1 (sys::peek buf ptr- :long) (incf ptr- 8)))
#-(or :alpha :irix6 :x86_64)
      (setf _pedido (let ((b0 (prog1 (sys::peek buf ptr- :integer) (incf ptr- 4)))
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

(defclass planners::JarPlannersResponse
  :super ros::object
  :slots (_result ))

(defmethod planners::JarPlannersResponse
  (:init
   (&key
    ((:result __result) "")
    )
   (send-super :init)
   (setq _result (string __result))
   self)
  (:result
   (&optional __result)
   (if __result (setq _result __result)) _result)
  (:serialization-length
   ()
   (+
    ;; string _result
    4 (length _result)
    ))
  (:serialize
   (&optional strm)
   (let ((s (if strm strm
              (make-string-output-stream (send self :serialization-length)))))
     ;; string _result
       (write-long (length _result) s) (princ _result s)
     ;;
     (if (null strm) (get-output-stream-string s))))
  (:deserialize
   (buf &optional (ptr- 0))
   ;; string _result
     (let (n) (setq n (sys::peek buf ptr- :integer)) (incf ptr- 4) (setq _result (subseq buf ptr- (+ ptr- n))) (incf ptr- n))
   ;;
   self)
  )

(defclass planners::JarPlanners
  :super ros::object
  :slots ())

(setf (get planners::JarPlanners :md5sum-) "4227327d0bce5bc51aba1221199a837b")
(setf (get planners::JarPlanners :datatype-) "planners/JarPlanners")
(setf (get planners::JarPlanners :request) planners::JarPlannersRequest)
(setf (get planners::JarPlanners :response) planners::JarPlannersResponse)

(defmethod planners::JarPlannersRequest
  (:response () (instance planners::JarPlannersResponse :init)))

(setf (get planners::JarPlannersRequest :md5sum-) "4227327d0bce5bc51aba1221199a837b")
(setf (get planners::JarPlannersRequest :datatype-) "planners/JarPlannersRequest")
(setf (get planners::JarPlannersRequest :definition-)
      "int64 pedido
---
string result
")

(setf (get planners::JarPlannersResponse :md5sum-) "4227327d0bce5bc51aba1221199a837b")
(setf (get planners::JarPlannersResponse :datatype-) "planners/JarPlannersResponse")
(setf (get planners::JarPlannersResponse :definition-)
      "int64 pedido
---
string result
")



(provide :planners/JarPlanners "4227327d0bce5bc51aba1221199a837b")


