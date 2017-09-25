(ns mosql.core-test
  (:require [clojure.test :refer :all]
            [mosql.core :refer :all]))

(deftest efirst-test
  (testing "with elements"
    (is (= (right 1) (efirst [1 2 3]))))
  (testing "without elements"
    (is (= (left) (efirst [])))
    (is (= (left) (efirts nil)))))

(deftest eget-test
    (is (= (right 1) (eget {:a 1} :a)))
    (is (= (left :a) (eget {:b 1} :a)))
    (is (= (left :a) (eget {:a nil :b 2}))))
