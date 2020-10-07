(ns tartataing.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[tartataing started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[tartataing has shut down successfully]=-"))
   :middleware identity})
