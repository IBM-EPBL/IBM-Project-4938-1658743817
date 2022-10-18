<template>
  <div>
    <base-header></base-header>
    <v-container>
      <v-form
        ><v-card elevation="2" class="loginCard">
          <v-banner><h3>Register to SignIn</h3></v-banner>
          <v-row
            ><v-col><v-text-field label="Name"></v-text-field></v-col
            ><v-col><v-text-field label="Email"></v-text-field></v-col
          ></v-row>
          <v-row
            ><v-col
              ><v-text-field
                label="Create New Password"
                type="password"
              ></v-text-field></v-col
            ><v-col
              ><v-text-field label="Confirm Password"></v-text-field></v-col
          ></v-row>
          <v-row>
            <v-col>
              <label for="">Give your hint code</label> <v-spacer></v-spacer>
              <textarea name="" id="" cols="30" rows="2"></textarea>
            </v-col>
          </v-row>

          <v-row>
            <router-link to="/login"
              ><base-button>SIGN UP</base-button></router-link
            ></v-row
          >
        </v-card>
      </v-form>
    </v-container>
    <base-footer></base-footer>
  </div>
</template>
