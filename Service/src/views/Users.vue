<template>
  <v-container>
    <v-row align="center" justify="center">
      <v-col cols="12" sm="8" md="4">
        <userLoginAJoin></userLoginAJoin>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import userLoginAJoin from "@/components/Users/newlog";
import { mapGetters } from "vuex";
export default {
  data() {
    return {
      useremail: "",
      userpwd: "",
      U_nickname: "",
      U_email: "",
      U_password1: "",
      U_password2: "",
      item1: {},
      item2: {},
    };
  },
  components: {
    userLoginAJoin,
  },
  computed: {
    ...mapGetters(["token"]),
  },
  methods: {},
};
</script>
