(do
    (require '[clojure.test :refer [run-tests]])
    (require '[clojure.java.jdbc :as j])
    (require '[cats.core :as m])
    (require '[cats.monad.either :as e])
    (require '[watch.core :as w]))

(w/reload (require '[mosql.core :as mo])
          "src/mosql/core.clj"
          (run-tests 'mosql.core))

(w/watch "test/mosql/core_test.clj"
         (fn [ctx e] (run-tests 'mosql.core)))
