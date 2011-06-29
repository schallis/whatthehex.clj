(ns whatthehex.core
  (:use compojure.core
        hiccup.core
        hiccup.page-helpers
        hiccup.form-helpers)
  (:require (clojure [string :as str])
            [compojure.route :as route]
            [compojure.handler :as handler]))

;; (defn dec-to-hex
;;   "Convert a 0-255 decimal to 0-F hex bit"
;;   [dec]
;;   (let [digits "0123456789ABCDEF"]
;;     (get digits dec)))


(defn dec-to-hex
  "Convert a 0-255 decimal to 0-F hex string"
  [dec]
  (str/upper-case (Integer/toHexString dec)))

(defn component-to-hex
  [component]
  (map dec-to-hex component))

(defn triple-to-hex
  "Convert a decimal triple to a traditional hex representation"
  [triple]
  {:post [(= 7 (count %))]}
  (str "#"
       (reduce str (flatten (map component-to-hex triple)))))

;; (defn gen-hex-bit
;;   "Randomly generate a single hex bit"
;;   []
;;   (dec-to-hex (rand-int 16)))

;; (defn gen-hex-bits-recursive
;;   "Generate a sequence of hex digits"
;;   [bits]
;;   (dec bits)
;;   (cond (<= bits 1) (gen-hex-bit)
;;         (> bits 1) (str (gen-hex-bit)
;;                         (gen-hex-bits-recursive (dec bits)))))

;; (defn gen-hex-bits-iterative
;;   "Generate a sequence of hex digits"  
;;   [bits]
;;   (for [x (range bits)] (gen-hex-bit)))

(defn gen-dec-bit
  "Randomly generate a single colour bit"
  []
  (rand-int 16))

(defn warp-bit
  "Return a randomly warped bit that is within +- warp-max. The
  result is always within the range 0-16"
  [bit variation]
  (let [new (+ bit (- (rand-int (* variation 2)) variation))]
    (cond (< new 0) (- new)
          (> new 15) (- 15 (- new 15))
          :else new)))

(defn gen-component
  "Generate two close hex pairs"
  [variation]
  (let [x (gen-dec-bit)]
    (seq [(warp-bit x variation) x])))

(defn warp-component
  "Warp a component with specified variation"
  [component variation]
  (map #(warp-bit % variation) component))

(defn gen-triple
  "Generate a full color code. Variation specifies the amount of
  variation between the bits of each component."
  [variation]
  (for [x (range 3)] (gen-component variation)))

(defn warp-triple
  [triple variation]
  (map #(warp-component % variation) triple))

(defn gen-level
  "Generate a game level with the target code and n posibilities"
  [{:keys [possibilities comp-var warp-var]
    :or {possibilities 5 comp-var 0 warp-var 15}}]
  (let [code (gen-triple comp-var)]
    (map triple-to-hex
         (cons code
               (for [x (range possibilities)]
                 (warp-triple code warp-var))))))

(defroutes main-routes
  "Define the application routing"
  (GET "/" [] (html [:h1 "WhatTheHex?"]
        (let [level (gen-level {})]
          [:form {:method "post" :action "/"}
           ;; Show the target code
           [:h2 (first level)]
           (hidden-field :answer (first level))
           ;; Show the options as appropriately coloured radio buttons
           (ordered-list (for [x (shuffle level)]
                           [:span
                            {:style (str "width: 100px; "
                                         "display: block; "
                                         "margin: 10px 0; "
                                         "background-color: " x)}
                            (radio-button :selection false x)]))
           (submit-button "Guess")])))
  (POST "/" [answer selection] (html (cond (= answer selection) [:h1 "Correct!"]
                                           :else [:h1 "Wrong :("])
                                     (link-to "/" "Try again")))
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (handler/site main-routes))

;;(run-server {:port 8080}
;;   "/*" (servlet main-routes))
