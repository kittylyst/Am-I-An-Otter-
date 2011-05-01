(ns am-i-an-otter.otters-db
  (:use clojure.java.io
        compojure.core
        hiccup.core)
  (:require (clojure.contrib [duck-streams :as ds])
            (clojure.contrib [logging :as log]))
  (:import (java.io File)))

(def otter-img-dir "resources/public/img/")
(def otter-img-dir-fq (str (.getAbsolutePath (file ".")) "/" otter-img-dir))

;; Instead of starting with an empty map should scan the disk and see what's there at startup
;; The next few functions work towards that goal

;; Gets the next id for a map keyed on id numbers (eg the otters)
(defn next-map-id [map-with-id]
  (+ 1 (nth (max (let [map-ids (keys map-with-id)] (if (nil? map-ids) [0] map-ids))) 0 )))

(defn scan-for-otters []
  (let [files (map #(.getName %) (file-seq (file otter-img-dir-fq)))
           jpegs (filter #(re-matches #".*jpe?g$" %) files)
           jpeg-count (count jpegs)]
    (zipmap (range jpeg-count) jpegs)))

;; otter-pics maps integer ids to filenames
(def otter-pics (scan-for-otters))

;; otter-votes stores the votes
(def otter-votes-r (ref {}))

(defn otter-exists [id]
  (contains? (set (keys otter-pics)) id))

;; Votes up an otter
(defn alter-otter-upvote [vote-map id] 
  (assoc vote-map id (+ 1 (let [cur-votes (get vote-map id)]
                            (if (nil? cur-votes) 0 cur-votes)))))

;; Fn for upvoting an otter in the DB
(defn upvote-otter [id]
  (if (otter-exists id) 
    (let [my-id id]
      (log/info (str "Upvoted Otter " my-id)) 
      (dosync (alter otter-votes-r alter-otter-upvote my-id) otter-votes-r))
    (log/info  (str "Otter " id " Not Found " otter-pics))))

;; Function returns a random id for an otter
(defn random-otter []
  (rand-nth (keys otter-pics)))

;; FIXME - otter pics should be a ref or atom
(defn upload-otter [req]
  (let [new-id (next-map-id otter-pics)
        new-name (str (java.util.UUID/randomUUID) ".jpg")
        tmp-file (:tempfile (get (:multipart-params req) "file"))]
    (log/debug  (str (.toString req) " ; New name = " new-name " ; New id = " new-id))
    (ds/copy tmp-file (ds/file-str (str otter-img-dir new-name)))
    (def otter-pics (assoc otter-pics new-id new-name))
    (html [:h1 "Otter Uploaded!"])))
