
(cl:in-package :asdf)

(defsystem "decision_support-msg"
  :depends-on (:roslisp-msg-protocol :roslisp-utils :mavros_msgs-msg
)
  :components ((:file "_package")
    (:file "newMission" :depends-on ("_package_newMission"))
    (:file "_package_newMission" :depends-on ("_package"))
    (:file "newMission" :depends-on ("_package_newMission"))
    (:file "_package_newMission" :depends-on ("_package"))
  ))