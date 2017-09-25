(ns mosql.generators
  (:require [mosql.fun :refer :all]
    [cats.monad.either :refer
             [right left right? try-either]] ))



(defn field-cast
  "Generates a susbstitute value for the parameter in the operation SQL.

  The substitution function is like:
  (fn [field value] ...) and must return a string.

  Ej.
  (gen-field-cast {} :name \"jose\") => \":name=?\"
  "
  [casts [field value]]
  (if (nil? (get casts field))
    "?"
    (apply (get casts field) [field value])))


(defn gen-field-update
  "Generates an sql field=? assignment for sql statements using
  and castings map."
  [casts [field value]]
  (str (name field) "=" (if (nil? (get casts field))
                          "?"
                          (apply (get casts field) [field value]))))

(defn gen-insert-attributes
  [data]
  (clojure.string/join ", " (map name (keys data))))


(defn gen-insert-params
  [casts data]
  (let [fcast (partial field-cast casts)]
    (clojure.string/join
      ", "
      (map fcast data))))

(defn gen-assignments
  [casts data]
  (let [fup (partial gen-field-update casts)]
    (clojure.string/join
      ", "
      (map fup data))))

(defn gen-where-ids
  [casts ids]
  (let [fup (partial gen-field-update casts)]
    (clojure.string/join
      " AND "
      (map fup ids))))

(defn gen-update-field
  [casts [field value]]
  (str (name field) "=" (if (nil? (get casts field))
                          "?"
                          (apply (get casts field) [field value]))))


(defn find-parameters
  [sql]
  (mapv (comp keyword #(subs % 1))
        (re-seq #":[-_\w]+" sql)))

(defn param2string
  [casts param value]
  (let [cfn (or (get casts param)
                (fn [k v] "?"))]
    (if (not (coll? value))
      (cfn param value)
      (str "("
           (clojure.string/join
             ", "
             (map #(cfn param %) value))
           ")"))))

; ; ; ; ; ; ; ; ; ; ; ;
; ; ; ; ; ; ; ; ; ; ; ; 


(defn gen-insert
  "Basic insert function.

  options can be
  {:new-id :id    ; tells where the generated key will be inserted if any.
  ; if omitted the generated key will not be inserted.
  :cast {:dob (fn [x] (str \"cast(? as date)\"))}
  "
  ([table data]
   (gen-insert table {} data))
  ([options table data]
   (let [rvalues (or (:cast options) {})
         skeys (keys data)
         fields (gen-insert-attributes data)
         substs (gen-insert-params rvalues data)]
     (vec
       (concat
         [(str "INSERT INTO " (name table)
               " (" fields ") VALUES(" substs ")")]
         (vals data))))))



(defn gen-update
  "Generates an update statement.
 
  (gen-update
    {:dob (fn [k v] \"cast(? as date)\")}
    :person
    [\"dob>cast(? as date)\" \"2000-01-01\"]
    {:dob \"1982-11-29\"}
  =>
  [\"UPDATE person SET dob=? WHERE dob>cast(? as date)\"
   \"1982-11-29\" \"2001-01-01\"]
  "
  ([table ids data]
   (gen-update {} table ids data))
  ([options table ids data]
   (let [rvalues (or (:cast options) {})
         fields (clojure.string/join
                  ", "
                  (map (partial gen-update-field rvalues) data))
         where (if (map? ids)
                 (str " WHERE "
                      (clojure.string/join
                        " AND "
                        (map (partial gen-update-field rvalues) ids)))
                 (if (sequential? ids)
                   (str " WHERE " (first ids))))
         sql (str "UPDATE " (name table) " SET " fields where)
         ]
     (vec (concat [sql] (vals data) (if (map? ids) (vals ids) (rest ids)))))))

;(gen-update :person "dob>cast(? as date)" {:fname "jose" :dob "1982-11-29"} )

(defn gen-select
  "Generates a generic select statement from the table name and the id's attributes."
  ([table]
   [(str "SELECT * FROM " (name table))])
  ([table ids]
   (gen-select {} table ids))
  ([options table ids]
   (let [casts (or (:cast options) {})
         where (if (map? ids)
                 (str " WHERE "
                      (clojure.string/join
                        " AND "
                        (map (partial gen-update-field casts) ids)))
                 (if (sequential? ids)
                   (str " WHERE " (first ids))))
         sql (str "SELECT * FROM " (name table)  where)]
     (vec (concat [sql] (if (map? ids) (vals ids) (rest ids)))))))


(defn gen-q
  "Generates a select statement from a parameterized query.
  Check the tests!.

  TODO: Documentar los parametros :-s
  "
  ([sql data]
   (gen-q {} sql data))
  ([options sql data]
   (let [casts (or (options :cast) {})
         params (find-parameters sql)]
     (println params)
     (loop [s sql ps params fparams []]
       (if (empty? ps)
         (vec (concat [s] fparams))
         (let [param (first ps)
               value (get data param)]
           (recur
             (clojure.string/replace
               s
               (str param)
               (param2string casts param value))
             (rest ps)
             (if (not (coll? value))
               (conj fparams value)
               (vec (concat fparams value))))))))))


