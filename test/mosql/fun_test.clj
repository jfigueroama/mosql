(ns mosql.fun-test
  (:require [clojure.test :refer :all]
            [mosql.fun :refer :all]
            [cats.monad.either :refer [right left]]))

(deftest efirst-test
  (testing "with elements"
    (is (= (right 1) (efirst [1 2 3]))))
  (testing "without elements"
    (is (= (left {:fn first :args [[]]}) (efirst [])))
    (is (= (left {:fn first :args [nil]}) (efirst nil)))))

(deftest eget-test
    (is (= (right 1) (eget {:a 1} :a)))
    (is (= (left {:fn get :args [{:b 1} :a]}) (eget {:b 1} :a)))
    (is (= (left {:fn get :args [{:a nil :b 2} :a]}) (eget {:a nil :b 2} :a))))
