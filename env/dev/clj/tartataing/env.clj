(ns tartataing.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [tartataing.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[tartataing started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[tartataing has shut down successfully]=-"))
   :middleware wrap-dev})
