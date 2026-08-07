(defproject das-vokabular "0.1.0-SNAPSHOT"
  :description "CLI application to memorize German vocabulary"
  :url "https://github.com/bm3719/das-vokabular"
  :license {:name "GPL-3.0-only"
            :url "https://www.gnu.org/licenses/gpl-3.0.en.html"}
  :dependencies [[org.clojure/clojure "1.12.4"]
                 [org.clojure/data.csv "1.1.1"]]
  :repl-options {:init-ns das-vokabular.core}
  :main das-vokabular.core)
