
<!-- Base header file for web -->

<template>
  <div>
    <v-app-bar
      color="primary lighten-1"
      dense
      elevation="4"
      class="fixed-header"
      rounded
    >
      <v-toolbar-title>Containment Zone</v-toolbar-title>
      <router-link to="/login"><base-button>Logout</base-button></router-link>
      <v-spacer></v-spacer>
      <v-switch
        v-model="$vuetify.theme.dark"
        hint="For Dark Users"
        inset
        label="Dark Theme"
        persistent-hint
        color="black"
      ></v-switch>
    </v-app-bar>
  </div>
</template>

<script>
export default {
  data() {
    return {};
  },
};
</script>
