(do
    (require '[clojure.test :refer [run-tests]])
    (require '[clojure.java.jdbc :as j])
    (require '[cats.core :as m])
    (require '[cats.monad.either :as e])
    (require '[watch.core :as w]))

(w/reload (require '[mosql.fun :as fun])
          "src/mosql/fun.clj")

(w/reload (require '[mosql.generators :as ge])
          "src/mosql/generators.clj")


(w/reload (require '[mosql.core :as mo])
          "src/mosql/core.clj")



;(w/watch "test/mosql/generators_test.clj"
;         (fn [ctx e] (run-tests 'mosql.generators)))



