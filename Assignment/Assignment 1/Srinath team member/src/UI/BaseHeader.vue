<template>
  <div>
    <v-app-bar color="primary lighten-1" dense elevation="4" rounded>
      <v-toolbar-title>Containment Zone</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-switch
        v-model="$vuetify.theme.dark"
        hint="For Dark Users"
        inset
        label="Dark Theme"
        persistent-hint
        color="black"
        prepend-icon="mdi-theme-light-dark"
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
