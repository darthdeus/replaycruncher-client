(ns parser
  ; TODO - use clojure-contrib.duck-streams instead?
  (:import (java.io RandomAccessFile)))


(defn
  is-replay?
  "Check if the given file is a SC2 replay by reading bytes 22 to 40,
    which should contain string 'StarCraft II replay'"
  [file]
  (= "StarCraft II replay" (apply str (drop 21 (take 40 file)))))


(defn
  safe-char
  "Converts int to char without throwing an exception if the integer
  has a negative value. Used for parsing the main binary file."
  [c] (if (> c 0) (char c) c))


(defn
  read-header
  "Read the whole header section of a SC2 replay file"
  [filename]
  (with-open [file (RandomAccessFile. filename "r")]
    (let [bytes (byte-array 1000)]
      (.read file bytes 0 1000)
      ))
  (apply str (map safe-char bytes))
  )


(def data (read-header "replay.SC2Replay"))

(println (is-replay? data))

;(with-open [file (RandomAccessFile. "replay.SC2Replay" "r")]
;  (let [bytes (byte-array 40)]
;    (.read file bytes 0 40)
;    (println (is-replay? (apply str (map safe-char bytes))))))
;

