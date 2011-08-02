(ns am-i-an-otter.core
  (:use compojure.core
        am-i-an-otter.otters-db
        am-i-an-otter.otters
        am-i-an-otter.analysis)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.middleware.multipart-params :as mp]))

;; Set up the routes - ie URL -> function mappings for the main pages 
(defroutes main-routes
  (GET "/" [] (page-compare-otters))
  (GET ["/upvote/:id", :id #"[0-9]+" ] [id] (page-upvote-otter (Integer/parseInt id)))
  (GET "/upload" [] (page-start-upload-otter))
  (GET "/votes" [] (page-otter-votes))  

  ;; This is the file uploader
  (mp/wrap-multipart-params
   (POST "/add_otter" req (str (upload-otter! req) (page-start-upload-otter))))
  
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (handler/site main-routes))
