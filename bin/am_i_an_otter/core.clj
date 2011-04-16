(ns am-i-an-otter.core
  (:use compojure.core)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.middleware.multipart-params :as mp]
            ))

(load "imports")

; Load the DB and filesystem manipulation functions 
(load "otters-db")

; Load the main page functions from the classpath
(load "otters")

; Set up the routes - ie URL -> function mappings for the main pages 
(defroutes main-routes
  (GET "/" [] (page-compare-otters))
  (GET ["/upvote/:id", :id #"[0-9]+" ] [id] (page-upvote-otter id))
  (GET "/upload" [] (page-start-upload-otter))
  ; This is the file uploader
  (mp/wrap-multipart-params
   (POST "/add_otter" req (str (upload-otter req) (page-start-upload-otter))))
  
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (handler/site main-routes)
)
