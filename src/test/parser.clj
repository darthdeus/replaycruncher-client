; TODO - namespace prefix?
(ns parser
  ; TODO - :use vs :require?
  (:use [clojure.contrib.test-is :as is]))

; test playground
(is (= 1 1))
(is (= "AB" (mapcat (fn [n] (char n)) [65 66])))
(is (= true false))

