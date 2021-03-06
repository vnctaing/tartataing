(ns tartataing.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [goog.events :as events]
   [goog.history.EventType :as HistoryEventType]
   ["aws-amplify-react" :refer (withAuthenticator)]
   ["aws-amplify" :default Amplify :as amp]
   ["/aws-exports.js" :default aws-exports]
   [markdown.core :refer [md->html]]
   [tartataing.ajax :as ajax]
   ;; [tartataing.home.authenticator :as auth]
   [ajax.core :refer [GET POST]]
   [reitit.core :as reitit]
   [clojure.string :as string])
  (:import goog.History))

(defonce session (r/atom {:page :home}))
(defonce bid-input (r/atom nil))

(defn nav-link [uri title page]
  [:a.navbar-item
   {:href  uri
    :class (when (= page (:page @session)) "is-active")}
   title])

(defn navbar []
  (r/with-let [expanded? (r/atom false)]
    [:nav>div.grid.gap-2.md:grid-cols-6.md:grid-rows:1
     [:a.font-bold.p-6.px-6 {:href "/"}
      [:img {:src "https://amplify-tartataing-dev-231139-deployment.s3.amazonaws.com/www/public/img/logo.svg"}]]]))

(defn about-page []
  [:section.section>div.container>div.content
   [:img {:src "/img/warning_clojure.png"}]])

(defn star-reviews [{:keys [rating reviews-count]}]
  [:p (for [i (range rating)] [:i.fa.fa-star.rating-star {:key i}])
   [:span.text-xs (str " (" reviews-count " reviews)")]])

(defn place-bid-button []
  [:div
   [:span "Place bid"]])

(defn bids-actions []
  [:div.bg-blue-400.hover:bg-blue-500.cursor-pointer.h-10.w-32.place-content-stretch.text-white.rounded-lg.flex.items-center.justify-center {:on-click #(reset! bid-input nil)}
   [place-bid-button]])

(defn product-label [{:keys [product]}]
  (let [rating        (:rating product)
        name          (:name product)
        reviews       (:reviews product)
        reviews-count (count (:reviews product))]
    [:div.justify-self-start
     [:div
      [:p.font-bold.text-l name
       [:span.text-xs.px-2 "(12'/32 cm)"]]]
     [star-reviews {:rating rating :reviews-count reviews-count}]]))
(defn price-tag []
  [:div.justify-self-start
   [:div
    [:p.font-bold "Currend bid: $5"
     [:span.text-xs.px-2 "[ 72 bids]"]]]
   [:div
    [:div {:class "mt-1 relative rounded-md shadow-sm"}
     [:div {:class "absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none"}]
     [:input {:type "text"
              :name "price"
              :id "price"
              :value @bid-input
              :on-change #(reset! bid-input (-> % .-target .-value))
              :class "border block focus:ring-indigo-500 focus:border-indigo-500 block w-full px-4 py-1 pl-7 pr-12 sm:text-sm border-gray-400 rounded-md"
              :placeholder "0.00"}]]]])

(def product-example {:rating  5
                      :name    "Signature Tartataing"
                      :reviews ["youpi" "super"]})

(defn card []
  [:div {:class "px-4 py-8"}
   [:div.bg-white.shadow-2xl.rounded-lg
    [:div
     [:img.rounded-t-lg {:src "https://amplify-tartataing-dev-231139-deployment.s3.amazonaws.com/www/public/img/banner.jpg"}]]
    [:div.p-6.py-8.bg-white.rounded-b-lg
     [:div.row-start-2.row-span-2.col-start-4.col-span-3.grid.grid-row-2.grid-cols-3.gap-x-4
      [product-label {:product product-example}]
      [price-tag]
      [bids-actions]]]]])

(defn login []
  [:div "hey"])

(def opts
  (clj->js {:signUpConfig {:hiddenDefaults ['email' 'username']
                           :signUpFields [{:label "Email"
                                           :key "username"
                                           :required true
                                           :displayOrder 1
                                           :type "email"
                                           :custom false}
                                          {:label "Password"
                                           :key "password"
                                           :required true
                                           :displayOrder 2
                                           :type "password"
                                           :custom false}
                                          {:label "Phone Number"
                                           :key "phone_number"
                                           :required true
                                           :displayOrder 3
                                           :type "tel"
                                           :custom false}]}}))

(defn amplify-authenticator []
  (r/adapt-react-class
   (withAuthenticator
    (r/reactify-component login) opts)))

(defn home []
  [:div {:class "md:grid md:grid-rows-3 md:grid-cols-6"}
   [:div.col-start-4.col-span-3.row-start-1
    [card]]
   ;; [:img {:class "rounded-l-md my-2" :src ""}]

   [:div.col-start-1.col-span-3.row-start-1.place-self-center
    [(amplify-authenticator)]
    [:h2.text-6xl.font-bold.p-8.text-gray-800 "Traditional french pastries delivery."]
    [:h2.text-2xl.px-8.py-1.text-gray-600 "Win the auction. Get a tartataing delivered to your door."]
    [:h3.text-l.p-8.italic.text-gray-600 "Same-day delivery only in San Francisco (SoMa, Mission, Downtown, Castro)"]]])

(def pages
  {:home  #'home
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
  (.configure Amplify aws-exports)
  (rdom/render [#'navbar] (.getElementById js/document "navbar"))
  (rdom/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (ajax/load-interceptors!)
  (fetch-docs!)
  ;; (hook-browser-navigation!)
  (mount-components))
