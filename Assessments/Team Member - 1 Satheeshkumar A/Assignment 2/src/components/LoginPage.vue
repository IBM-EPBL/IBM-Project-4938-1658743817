<template>
  <div>
    <base-header></base-header>
    <v-main>
      <v-card elevation="2" class="loginCard">
        <v-form @submit.prevent="">
          <v-banner><h3>LOGIN</h3></v-banner>
          <v-row>
            <v-col>
              <v-row>
                <v-col>
                  <v-text-field
                    label="Email Id"
                    type="email"
                    v-model="inputName"
                  >
                  </v-text-field
                ></v-col>
              </v-row>
              <v-row>
                <v-col
                  ><v-text-field
                    :append-icon="passwordVisible ? 'mdi-eye' : 'mdi-eye-off'"
                    :rules="[rules.required]"
                    :type="passwordVisible ? 'text' : 'password'"
                    name="input-10-2"
                    v-model="inputPassword"
                    label="Password"
                    hint="At least 8 characters"
                    class="input-group--focused"
                    @click:append="passwordVisible = !passwordVisible"
                  >
                  </v-text-field>
                </v-col>
              </v-row>
            </v-col>
          </v-row>

          <v-row>
            <v-col class="">
              <base-button><h3>LOGIN</h3></base-button></v-col
            ><v-col
              ><router-link to="/signup" right>
                <base-button class="signup-btn"
                  ><h3>SIGNUP</h3></base-button
                ></router-link
              ></v-col
            ></v-row
          >
          <v-row class="text-center">
            <router-link to="/forgot">
              <p>did you forgot password?</p>
            </router-link>
          </v-row>
        </v-form>
      </v-card>
    </v-main>
    <base-footer></base-footer>
  </div>
</template>

<script>
import BaseFooter from "@/UI/BaseFooter.vue";
export default {
  components: { BaseFooter },
  data() {
    return {
      inputName: "",
      inputPassword: "",
      passwordVisible: false,
      rules: {
        required: (value) => !!value || "Required.",
      },
    };
  },
};
</script>

<style scoped>
header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.appBar {
  display: flex;
  align-content: space-between;
}

a {
  text-decoration: none;
}
.loginCard {
  margin: 10rem auto;
  max-width: 45rem;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.26);
  padding: 2rem;
  align-items: center;
  justify-content: center;
}
</style>
