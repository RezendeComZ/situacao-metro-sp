(ns situacao-metro-sp.core
  (:require [situacao-metro-sp.metro :as metro]
            [situacao-metro-sp.cptm  :as cptm]))

(def mapa-todas-as-linhas
  (let [cptm-metro (concat cptm/mapa-todas-linhas-cptm
                           metro/mapa-padronizado-metro)]
    (sort-by :numero cptm-metro)))

(defn -main
  [& args]
  (doall (map println mapa-todas-as-linhas)))

