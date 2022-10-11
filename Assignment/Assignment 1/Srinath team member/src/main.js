import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import vuetify from "./plugins/vuetify";
import BaseButton from "./UI/BaseButton.vue";
import BaseHeader from "./UI/BaseHeader.vue";
import BaseFooter from "./UI/BaseFooter.vue";
Vue.config.productionTip = false;
Vue.component("base-button", BaseButton);
Vue.component("base-header", BaseHeader);
Vue.component("base-footer", BaseFooter);
new Vue({
  router,
  store,
  vuetify,
  render: (h) => h(App),
}).$mount("#app");
