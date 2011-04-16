(defproject am-i-an-otter "1.0.0-SNAPSHOT"
  :description "Am I an Otter or Not?"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [compojure "0.6.2"]
                 [log4j "1.2.15" :exclusions  [javax.mail/mail
                                              javax.jms/jms
                                              com.sun.jdmk/jmxtools
                                              com.sun.jmx/jmxri]]
                 [org.slf4j/slf4j-api "1.5.6"]
                 [org.slf4j/slf4j-log4j12 "1.5.6"]
                 [hiccup "0.3.4"]]
  :dev-dependencies [[lein-ring "0.4.0"]]
  ;:source-path "src"
  :repl-init am-i-an-otter.core
  :ring {:handler am-i-an-otter.core/app})
