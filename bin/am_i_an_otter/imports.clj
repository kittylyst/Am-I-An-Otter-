(ns am-i-an-otter.core)

; Imports and helper functions
(import '(org.slf4j LoggerFactory Logger)) 

(defn get-logger [] (LoggerFactory/getLogger (str *ns*))) 

