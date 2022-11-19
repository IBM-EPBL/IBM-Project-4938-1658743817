
<!-- Signup page to join as a new user  -->

<template>
  <div>
    <base-header></base-header>
    <v-container>
      <v-form
        ><v-card elevation="2" class="loginCard">
          <v-banner><h3>Register to SignIn</h3></v-banner>
          <v-row no-gutters class="ma-2"
            ><v-col><v-text-field label="Name" outlined></v-text-field></v-col
            ><v-col><v-text-field label="Email" outlined></v-text-field></v-col
          ></v-row>
          <v-row no-gutters class="ma-2"
            ><v-col
              ><v-text-field
                label="Create New Password"
                type="password"
                outlined
              ></v-text-field></v-col
            ><v-col
              ><v-text-field
                label="Confirm Password"
                type="password"
                outlined
              ></v-text-field></v-col
          ></v-row>

          <v-row no-gutters class="ma-2">
            <router-link to="/login"
              ><base-button @click="newUser()"
                >SIGN UP</base-button
              ></router-link
            ></v-row
          >
        </v-card>
      </v-form>
    </v-container>
    <base-footer></base-footer>
  </div>
</template>
<script>
import { newUser } from "../../server";
export default {
  newUser() {
    Vue.use(newUser);
  },
};
</script>

<style scoped>
textarea {
  border: 1px;
  border-color: black;
}
header {
  display: flex;
  justify-content: space-around;
  align-items: center;
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
