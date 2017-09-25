(defproject mosql "0.1.0"
  :description "A simple wrapper around clojure.java.jdbc that provides a monadic interface. "
  :url ""
  :license {:name "BSD 2-Clause License"
            :url "https://opensource.org/licenses/BSD-2-Clause"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [funcool/cats "2.1.0"]
                 [org.clojure/java.jdbc "0.7.1"]]


  :profiles {:dev {:dependencies [[jfigueroama/watch "0.1.0"]
                                  [mount "0.1.11"]
                                  [org.xerial/sqlite-jdbc "3.20.0"]
                                  ;[org.clojure/tools.nrepl "0.2.8"]
                                  ]}
             ;:repl {:dependencies [[jfigueroama/watch "0.1.0"]
             ;                      [mount "0.1.11"]
             ;                      ;[org.clojure/tools.nrepl "0.2.8"]
             ;                      [org.xerial/sqlite-jdbc "3.20.0"]]}
             
             })
