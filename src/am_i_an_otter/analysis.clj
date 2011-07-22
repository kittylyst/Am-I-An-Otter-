(ns am-i-an-otter.analysis 
  (:use [clojure.contrib.io :only [reader]]))

(use '(incanter core charts io excel))

; This file should contain simple Incanter-based functions for data loading and analysis


; Function to scan a file and dump all request / responses as maps into a data structure
; { uri => [ (maps of req / resp, sorted by datetime inc) ] }

; Use a ref to keep track of the map as we build it out
(def line-store (ref []))

(defn scan-log-for-http-entries [log-file]
  (with-open []
    (filter #(re-find #"REQUEST|RESPONSE" %) (line-seq (reader log-file)))))



; (send msg-store conj msg-str)