
(cl:in-package :asdf)

(defsystem "drone_system-srv"
  :depends-on (:roslisp-msg-protocol :roslisp-utils )
  :components ((:file "_package")
    (:file "AddTwoInts" :depends-on ("_package_AddTwoInts"))
    (:file "_package_AddTwoInts" :depends-on ("_package"))
    (:file "PathMsg" :depends-on ("_package_PathMsg"))
    (:file "_package_PathMsg" :depends-on ("_package"))
    (:file "path_msg" :depends-on ("_package_path_msg"))
    (:file "_package_path_msg" :depends-on ("_package"))
  ))