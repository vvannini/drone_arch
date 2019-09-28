
(cl:in-package :asdf)

(defsystem "planners-srv"
  :depends-on (:roslisp-msg-protocol :roslisp-utils )
  :components ((:file "_package")
    (:file "JarPlanners" :depends-on ("_package_JarPlanners"))
    (:file "_package_JarPlanners" :depends-on ("_package"))
  ))