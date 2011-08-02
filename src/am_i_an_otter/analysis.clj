(ns am-i-an-otter.analysis 
  (:use [clojure.contrib.io :only [reader]]))

(use '(incanter core charts stats io excel))

(import '(java.text SimpleDateFormat))

; This file contains simple Incanter-based functions for data loading and analysis

; Define the Jetty log patterns and date format
(def tstamp-pattern #"\d+-\d+-\d+\s\d\d:\d\d:\d\d,\d\d\d")

(def line-pattern #"(\d+-\d+-\d+\s\d\d:\d\d:\d\d,\d\d\d)\s+DEBUG\s+\[.*\]\s+(REQUEST|RESPONSE)\s+([^\s]+)")

(def resp-pattern #"(\d+-\d+-\d+\s\d\d:\d\d:\d\d,\d\d\d)\s+DEBUG\s+\[.*\]\s+RESPONSE\s+([^\s]+)\s+(\d+)")

(def req-pattern #"(\d+-\d+-\d+\s\d\d:\d\d:\d\d,\d\d\d)\s+DEBUG\s+\[.*\]\s+REQUEST\s+([^\s]+)\s+on(.*)")

(def date-format (SimpleDateFormat. "yyyy-MM-dd HH:mm:ss,SSS"))

; Given a log line, return a map of the form: {:req t/f :resp t/f :timestamp :millis :uri :code (nil for reqs)}
(defn build-map-from-logline [line] 
  (let [ln-mtch (re-matcher line-pattern line)
        ts-mtch (re-matcher tstamp-pattern line)
        is-req (not (nil? (re-matches req-pattern line))) 
        is-resp (not (nil? (re-matches resp-pattern line)))
        millis (.getTime (.parse date-format (re-find ts-mtch)))
        uri (nth (re-find ln-mtch) 3)
        code (if is-resp
                 (nth (re-find resp-pattern line) 3)
                 nil)]
    {:timestamp (re-find ts-mtch) :req is-req :resp is-resp :millis millis :uri uri :code code}))

; Grab all relevant log lines, and convert to map entries
(defn scan-log-for-http-entries [log-file]
  (with-open []
    (map build-map-from-logline 
      (filter #(re-find line-pattern %) (line-seq (reader log-file))))))

; Function to scan a file and dump all request / responses as maps into a data structure
; { uri => [ (maps of req / resp, sorted by datetime inc) ] }
; The data structure will be sorted by dint of the temporal ordering of the loglines, which is preserved
(defn build-map-http-entries [log-file] 
  (group-by :uri (scan-log-for-http-entries log-file)))

; Build response times for a block of events on the same URL
(defn make-resp-times [ents]
  (loop [my-ents ents resp-times []]
    (if (nil? my-ents)
      ;{:uri 1}
      resp-times
      (let [req (first my-ents) resp (first (next my-ents)) 
            reqt (:millis req) respt (:millis resp) 
            ptime (if (or (nil? reqt) (nil? respt)) 0 (- respt reqt))]
        (recur (next my-ents) (conj resp-times {:uri (:uri req) :ptime ptime}))))))

; Function to calculate the time taken for each request, and save in an Incanter dataset
; [ :uri :ptime ]
(defn calc-response-times [my-map]
  (let [my-vals (vals my-map)]
    (to-dataset (flatten (map make-resp-times my-vals)))))

; just the ptime vals ($ :ptime my-ds)

(defn save-overall-resp [my-ds fname]
  (with-data ($rollup mean :ptime :uri my-ds) (save (bar-chart :uri :ptime) (str fname ".png"))))

(defn save-detail-resp [my-ds fname]
  (with-data ($order :ptime :asc ($rollup #(.count %1) :count :ptime my-ds) )  
             (save (bar-chart :ptime :count :x-label "Resp Time (ms)" :y-label "# Requests") (str fname ".png"))))

(defn save-detail-resp-cutoff [my-ds cutoff fname]
  (with-data (sel my-ds :filter  #(< (nth % 1) cutoff) )  
             (save (histogram :ptime :nbins 100 :title fname) (str fname "_" cutoff ".png"))))

; TEST defs in case live demo has problems
 (def test-ds (calc-response-times (build-map-http-entries "./test/resources/debug.log")))
 (def image-regex (re-pattern ".*\\.jpg"))
 (def vote-regex (re-pattern "/upvote/.*"))

(defn save-detail-resp-regex [my-ds regex fname]
  (let [my-reg (re-pattern regex)]
    (with-data (sel my-ds :filter  #(not (nil? (re-matches my-reg (nth % 0)))) )  
               (save (histogram :ptime :nbins 100 :title fname) fname))))
