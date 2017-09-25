(defproject mosql "0.1.0"
  :description "A monadic interface around clojure.java.jdbc using funcool/cats. "
  :url "https://github.com/jfigueroama/mosql"
  :license {:name "BSD 2-Clause License"
            :url "https://opensource.org/licenses/BSD-2-Clause"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [funcool/cats "2.1.0"]
                 [org.clojure/java.jdbc "0.7.1"]]


  :profiles {:dev {:dependencies [[jfigueroama/watch "0.1.0"]
                                  [org.xerial/sqlite-jdbc "3.20.0"]]
                   :plugins [[com.jakemccrary/lein-test-refresh "0.21.1"]]}})
