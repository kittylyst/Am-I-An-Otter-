(ns am-i-an-otter.core
  (:use compojure.core)
  (:use hiccup.core))

; Main page for comparing otters
(defn page-compare-otters [] (let [otter1 (random-otter), otter2 (random-otter)]
  (.info (get-logger) (str "Otter1 = " otter1 " ; Otter2 = " otter2 " ; " otter-pics))
  (html [:h1 "Otters say 'Hello Compojure!'"] 
        [:p [:a {:href (str "/upvote/" otter1)} [:img {:src (str "/img/" (get otter-pics otter1))} ]]]
        [:p [:a {:href (str "/upvote/" otter2)} [:img {:src (str "/img/" (get otter-pics otter2))} ]]]
        [:p "Click " [:a {:href "/upload"} "here"] " to upload a brand new otter"])))
  
; Page for upvoting an otter
(defn page-upvote-otter [id] (let [my-id id]
  (upvote-otter id)                               
  (str (html [:h1 "Upvoted otter id=" my-id]) (page-compare-otters))))

; Page for uploading an otter
(defn page-start-upload-otter []                                
  (html [:h1 "Upload a new otter"]
        [:p [:form {:action "/add_otter" :method "POST" :enctype "multipart/form-data"} 
                 [:input {:name "file" :type "file" :size "20"}] 
                 [:input {:name "submit" :type "submit" :value "submit"}]]]
        [:p "Or click " [:a {:href "/"} "here" ] "to vote on some otters"]))

; Page for showing otter votes
(defn page-otter-votes [] 
  (let []
    (.info (get-logger) (str "Otters: " @otter-votes-r))
    (html [:h1 "Otter Votes" ]
          [:div#votes.otter-votes 
            (for [x (keys @otter-votes-r)] 
              [:p [:img {:src (str "/img/" (get otter-pics x))} ]  (get @otter-votes-r x)  ]
            ) ])))
