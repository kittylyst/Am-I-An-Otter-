(ns am-i-an-otter.test.core
  (:use [am-i-an-otter.core] :reload)
  (:use am-i-an-otter.analysis)
  (:use [clojure.test]))

(deftest replace-me ;; FIXME: write
  (is true (= 34 (count (scan-log-for-http-entries "test/resources/debug.log")))))
