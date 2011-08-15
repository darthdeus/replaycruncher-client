(ns parser
  (:import (com.mundi4.mpq MpqFile MpqEntry)))

; still not sure how this works
; TODO - find API/usage docs
(with-open [file (MpqFile. "replay.SC2Replay")]
  (let [item (.. file iterator next)]
    (println (. item getName))))


