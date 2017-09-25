(ns mosql.generators-test
  (:require [clojure.test :refer :all]
            [mosql.generators :refer :all]))

(def data1
  {:fname "Jose" :lname "Figueroa" :dob "1982-11-29"})

(def options1
  {:cast
   {:dob (fn [k v] (str "cast(? as date)"))}})

(deftest gen-insert-attributes-test
  (is (= (gen-insert-attributes data1)
         "fname, lname, dob")))


(deftest gen-insert-params-test
  (is (= "?, ?, cast(? as date)"
         (gen-insert-params (:cast options1) data1))))

(deftest gen-assignments-test
  (is (= (gen-assignments (:cast options1) data1)
         "fname=?, lname=?, dob=cast(? as date)")))


(deftest gen-where-ids-test
  (testing 'with-casts
    (is (= "fname=? AND lname=? AND dob=cast(? as date)"
           (gen-where-ids (:cast options1) data1))))
  (testing 'without-casts
    (is (= "fname=? AND lname=? AND dob=?"
           (gen-where-ids {} data1)))))

(deftest gen-insert-test
  (is (= ["INSERT INTO person (fname, lname, dob) VALUES(?, ?, cast(? as date))"
          "jose" "figueroa" "1982-11-20"]
         (gen-insert options1
                     :person
                     {:fname "jose" :lname "figueroa" :dob "1982-11-20"}))))

(deftest gen-update-test
  (testing 'simple
    (is (= (gen-update options1
                       :person
                       {:fname "jose"}
                       {:phone "12345"})
           ["UPDATE person SET phone=? WHERE fname=?" "12345" "jose"])))
  (testing 'complex

    (is (= (gen-update options1
                       :person
                       {:fname "jose" :lname "figueroa" :dob "1982-11-20"}
                       {:phone "12345", :addr "New addr"})
           ["UPDATE person SET phone=?, addr=? WHERE fname=? AND lname=? AND dob=cast(? as date)" "12345" "New addr" "jose" "figueroa" "1982-11-20"])))
  (testing 'vector
    (is (= ["UPDATE person SET fname=?, dob=? WHERE dob>cast(? as date)"
            "jose" "1982-11-29" "1982-11-10"]
           (gen-update :person
                       ["dob>cast(? as date)" "1982-11-10"]
                       {:fname "jose" :dob "1982-11-29"})
           ))))




#_(deftest gen-select-test
  (testing 'simple
    (is (= ["SELECT * FROM person WHERE fname=? AND lname=? AND dob=cast(? as date)" "jose" "figueroa" "1982-11-29"]
           (gen-select
             options1
             :person
             {:fname "jose" :lname "figueroa" :dob "1982-11-29"}))))

  )

(deftest find-parameters-test
  (is (= [:_a_ :234b_ :___x___234 :a]
         (find-parameters "SELECT * FROM person where a=:_a_ and b=:234b_ and c=:___x___234 and x=:a"))))


(deftest gen-q-test
  (testing 'simple
    (is (= ["SELECT * FROM person WHERE fname=? AND lname=? AND dob=cast(? as date)" "jose" "figueroa" "1982-11-29"]
           (gen-q
             options1
             "SELECT * FROM person WHERE fname=:fname AND lname=:lname AND dob=:dob"
             {:dob "1982-11-29" :fname "jose" :lname "figueroa"}))))
  (testing 'in
    (is (= ["SELECT * FROM person WHERE id IN (?, ?, ?)" 1 2 3]
           (gen-q
             options1
             "SELECT * FROM person WHERE id IN :ids"
             {:ids [1 2 3]}))))
  (testing 'in-cast
    (is (= ["dob IN (cast(? as date), cast(? as date))" "2001-01-01" "2001-12-31"]
           (gen-q
             options1
             "dob IN :dob"
             {:dob ["2001-01-01" "2001-12-31"]})))))


(deftest gen-select-test
  (is (= ["SELECT * FROM person"]
         (gen-select :person)))
  (is (= ["SELECT * FROM person WHERE dob=cast(? as date)" "1982-11-29"]
         (gen-select options1 :person {:dob "1982-11-29"})))
  (is (= ["SELECT * FROM person WHERE fname=? AND dob=cast(? as date)" "jose" "1982-11-29"]
         (gen-select options1 :person {:fname "jose" :dob "1982-11-29"}))))
