(ns smilefjes.search-index
  (:require [datomic-type-extensions.api :as d]
            [smilefjes.search :as search]))

(defn index-document
  "Index data in `doc` according to `schema` under `id` in `index`. Returns the
  updated index. At its simplest, the schema specifies which keys in `doc` to
  include in the index, and how to tokenize them:

  ```clj
  {:title {:tokenizers [tokenize-words]}
   :description {:tokenizers [tokenize-words]}}
  ```

  This schema will use the provided tokenizers to index `:title` and
  `:description` from `doc`.

  The following schema names what function `:f` to apply to `doc` to extract the
  data to index, and what `:tokenizers` to use. The keys of the schema name the
  resulting field indexes - when querying you can choose to query across all
  fields, or name individual fields to query:

  ```clj
  {:title
   {:f :title
    :tokenizers [tokenize-words]}

   :description
   {:f :description
    :tokenizers [tokenize-words]}}
  ```

  You can use schemas to index the same fields multiple times with different
  tokenizers:

  ```clj
  {:title
   {:f :title
    :tokenizers [tokenize-words]}

   :title.ngrams
   {:f :title
    :tokenizers [tokenize-words
                 (partial tokenize-ngrams 3)]}}
  ```"
  [index schema id doc]
  (->> schema
       (mapcat (fn [[field config]]
                 (let [f (:f config field)]
                   (->> (search/tokenize (f doc) (:tokenizers config))
                        (search/filter-tokens (:token-filters config))
                        (search/get-field-syms field)))))
       (reduce (fn [index {:keys [field sym weight]}]
                 (assoc-in index [field sym id] weight))
               index)))

(defn index-spisesteder [schema id->spisested & [index]]
  (->> id->spisested
       (reduce (fn [index [id spisested]]
                 (index-document index schema id spisested))
               index)))

(defn build-index [db]
  (index-spisesteder search/schema db))

(comment
  (def db (d/db (:datomic/conn (powerpack.dev/get-app))))

  (def spisested
    (->> (d/q '[:find [?e ...]
                :where
                [?e :tilsynsobjekt/id]]
              db)
         (map #(d/entity db %))
         first))

  (index-document {} search/schema 0 spisested)

  )
