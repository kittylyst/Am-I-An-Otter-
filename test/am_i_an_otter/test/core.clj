(ns am-i-an-otter.test.core
  (:use [am-i-an-otter.core] :reload)
  (:use am-i-an-otter.analysis)
  (:use [clojure.test]))

(deftest count-loglines
  (is true (= 34 (count (scan-log-for-http-entries "test/resources/debug.log")))))

(def req-line "2011-07-22 11:42:55,099 DEBUG [org.mortbay.log] REQUEST /upvote/1 on org.mortbay.jetty.HttpConnection@3f0731e7")

(def resp-line "2011-07-22 11:43:00,771 DEBUG [org.mortbay.log] RESPONSE /img/1e95bfd0-6118-476a-982c-dfad959f22c0.jpg  200")

(deftest check-is-req
  (is true  (not (nil? (re-matches req-pattern req-line)))))

(deftest check-is-resp
  (is true  (not (nil? (re-matches resp-pattern resp-line)))))

;(calc-response-times (build-map-http-entries "test/resources/debug.log"))