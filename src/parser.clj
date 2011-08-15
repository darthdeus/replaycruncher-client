(ns parser
  ; TODO - use clojure-contrib.duck-streams instead?
  (:import (java.io RandomAccessFile)))


; TODO - implement fn to check if file is SC2 replay
(defn is-replay? [] false)



; doesn't throw exception on c with negative value
(defn safe-char [c] (if (> c 0) (char c) c))

(with-open [file (RandomAccessFile. "replay.SC2Replay" "r")]
  (let [bytes (byte-array 512)]
    (.read file bytes 0 512)
    (println (apply str (map safe-char bytes)))))


