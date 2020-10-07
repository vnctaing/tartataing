(ns tartataing.app
  (:require [tartataing.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
