(ns situacao-metro-sp.core
  (:require [clj-http.client :as http]
            [cheshire.core :as json]
            [clojure.string :as string]
            [hickory.core :as html]))

;; TODO:
;; Cria util.clj
;; Separar em arquivos metro e CPTM
;; Juntar em um mapa só padronizado

(def VIAQUATRO-URL "https://www.viaquatro.com.br/generic/Main/LineStatus")
(def CPTM-URL      "https://www.cptm.sp.gov.br")
(def LINHAS-CPTM [{:nome "LILÁS"
                   :numero 5}
                  {:nome "RUBI"
                   :numero 7}
                  {:nome "DIAMANTE"
                   :numero 8}
                  {:nome "ESMERALDA"
                   :numero 9}
                  {:nome "TURQUESA"
                   :numero 10}
                  {:nome "CORAL"
                   :numero 11}
                  {:nome "SAFIRA"
                   :numero 12}
                  {:nome "JADE"
                   :numero 13}])

(def mapa-via-quatro
  (try
    (let [content (http/get VIAQUATRO-URL)
          body    (:body content)]
      (json/parse-string body true))
    (catch Exception e
      (str "Erro ao capturar dados do Metrô: " e))))

;; CPTM

(def vetor-html-cptm ;; TODO: try and catch
  (try
    (let [content (http/get CPTM-URL)
          body    (:body content)
          parse   (html/as-hiccup (html/parse body))]
      (flatten (vec parse)))
    (catch Exception e
      (str "Erro ao capturar dados da CPTM: " e))))

(defn situacao-linha-cptm [linha]
  (nth vetor-html-cptm (+ (.indexOf vetor-html-cptm linha) 3)))

(defn mapa-unica-linha-cptm [linha]
  (let [nome           (:nome linha)
        nome-lowercase (string/lower-case nome)]
    {(keyword nome-lowercase)
     {:nome     (str (first nome) (apply str (rest nome-lowercase)))
      :numero   (:numero linha)
      :situacao (situacao-linha-cptm nome)}}))

(def mapa-todas-linhas-cptm
  (merge (map mapa-unica-linha-cptm LINHAS-CPTM)))

(defn -main
  [& args]
  (prn "Via Quatro:")
  (prn mapa-via-quatro)

  (prn "CPTM:")
  (prn mapa-todas-linhas-cptm))

