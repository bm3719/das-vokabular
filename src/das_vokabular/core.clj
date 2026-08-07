(ns das-vokabular.core
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.string :as s]))

(def dict "data.csv")

(defn csv->seq [f]
  (if-let [r (io/resource f)]
    (with-open [reader (io/reader r)]
      (doall (csv/read-csv reader)))
    (throw (ex-info "Dictionary file not found" {:file f}))))

(defn csv-data->maps [csv-data]
  (map zipmap
       (->> [:article :word]
            repeat)
       (rest csv-data)))

(defn read-file [f]
  (csv-data->maps (csv->seq f)))

(defn valid-file?
  "Check an input file to ensure that it has a consistent number of fields.  If
  false, there exists one or more rows that have too few or too many fields." [f]
  (->> (csv->seq f)
       doall
       (map count)
       distinct
       (every? #(= 2 %))))

(defn next-word-article [dict]
  (nth dict (rand-int (count dict))))

(defn -main [& _]
  ;; Before attempting to parse file, ensure it's in valid form.
  (if (valid-file? dict)
    ;; Main interaction loop.
    (let [data (read-file dict)]
      (println "Geben Sie den richtigen deutsche Wort ein (oder \"q\" zum Beenden)")
      (loop []
        (let [word-article (next-word-article data)]
          (println "» " (:word word-englisch))
          (print "> ")
          (flush)
          (let [ans (read-line)]
            (if (= (s/lower-case ans) "q")
              (println "Auf Wiedersehen.")
              (do
                (if (= (:article word-deutsch) ans)
                  (println "Richtig!")
                  (println (str "Falsch!  Die Antwort war \"" (:article word-deutsch) "\"!")))
                (recur)))))))
    (println "Dictionary file format invalid.")))
