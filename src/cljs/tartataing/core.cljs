(ns tartataing.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [goog.events :as events]
   [goog.history.EventType :as HistoryEventType]
   [markdown.core :refer [md->html]]
   [tartataing.ajax :as ajax]
   [ajax.core :refer [GET POST]]
   [reitit.core :as reitit]
   [clojure.string :as string])
  (:import goog.History))

(defonce session (r/atom {:page :home}))
(defonce cart (r/atom []))
(defn nav-link [uri title page]
  [:a.navbar-item
   {:href  uri
    :class (when (= page (:page @session)) "is-active")}
   title])

(defn navbar []
  (r/with-let [expanded? (r/atom false)
               ]
    [:nav.navbar>div.container
     [:div.navbar-brand
      [:a.navbar-item {:href "/" :style {:font-weight :bold}}
       [:img {:src "https://amplify-tartataing-dev-220411-deployment.s3.amazonaws.com/www/public/img/logo.svg"}]]
      [:span.cart-container
       [:i.fa.fa-shopping-cart.cart-icon]
       [:span.badge [:span (count @cart)] ]]
      [:span.navbar-burger.burger
       {:data-target :nav-menu
        :on-click    #(swap! expanded? not)
        :class       (when @expanded? :is-active)}
       [:span] [:span] [:span]]]]))

(defn about-page []
  [:section.section>div.container>div.content
   [:img {:src "/img/warning_clojure.png"}]])

(defn star-reviews [{:keys [rating reviews-count]}]
  [:p.star-reviews-container (for [i (range rating)] [:i.fa.fa-star.rating-star {:key i}])
   [:span.star-reviews-count (str " (" reviews-count " reviews)")]])

(defn add-button []
  [:div.add-button-box {:on-click #(swap! cart conj "oui")} [:i.fa.fa-plus]])

(defn product-label [{:keys [product]}]
  (let [rating        (:rating product)
        name          (:name product)
        reviews       (:reviews product)
        reviews-count (count (:reviews product))]
    [:div.product-label
     [:div
      [:p.product-name name]]
     [star-reviews {:rating rating :reviews-count reviews-count}]]))

(defn price-tag []
  [:div.price-tag
   [:p.price-tag-price "$45.99"
    [:span.price-tag-dimension "(12'/32 cm)"]]])

(def product-example {:rating  5
                      :name    "Signature Tartataing"
                      :reviews ["youpi" "super"]})

(defn home-page []
  [:div
   [:div.img_container
    [:img.img_home {:src "https://amplify-tartataing-dev-220411-deployment.s3.amazonaws.com/www/public/img/tatin.jpg"}]]
   [:div.label-container
    [price-tag]
    [product-label {:product product-example}]
    [:div.home-add-button [add-button]]]])

(def pages
  {:home  #'home-page
   :about #'about-page})

(defn page []
  [(pages (:page @session))])

;; -------------------------
;; Routes

(def router
  (reitit/router
   [["/" :home]
    ["/about" :about]]))

(defn match-route [uri]
  (->> (or (not-empty (string/replace uri #"^.*#" "")) "/")
       (reitit/match-by-path router)
       :data
       :name))
;; -------------------------
;; History
;; must be called after routes have been defined
;; (defn hook-browser-navigation! []
;;   (doto (History.)
;;     (events/listen
;;       HistoryEventType/NAVIGATE
;;       (fn [event]
;;         (swap! session assoc :page (match-route (.-token event)))))
;;     (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn fetch-docs! []
  (GET "/docs" {:handler #(swap! session assoc :docs %)}))

(defn ^:dev/after-load mount-components []
  (rdom/render [#'navbar] (.getElementById js/document "navbar"))
  (rdom/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (ajax/load-interceptors!)
  (fetch-docs!)
  ;; (hook-browser-navigation!)
  (mount-components))
