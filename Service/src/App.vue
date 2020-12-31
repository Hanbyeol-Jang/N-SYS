<template>
  <v-app id="inspire">
    <v-app-bar app clipped-left>
      <v-menu bottom  right offset-y >
        <template v-slot:activator="{ on, attrs }">
          <v-btn dark icon v-bind="attrs" v-on="on">
            <v-icon>mdi-dots-vertical</v-icon>
          </v-btn>
        </template>
        <mypageCard></mypageCard>
      </v-menu>
      <v-toolbar-title @click="$router.push('/')"> N SYS </v-toolbar-title>
      <!--      <v-toolbar-title @click="$router.push('/')" >{{ getBar }} </v-toolbar-title>-->
    </v-app-bar>
    <v-main>
      <router-view></router-view>
    </v-main>

    <v-footer app>
      <span>&copy; Copyright©2020 'Insert Coin' All rights reserved.</span>
    </v-footer>
  </v-app>
</template>
<script>
import mypageCard from '@/components/Users/mypage'
import {mapGetters} from "vuex";

export default {
  props: {
    source: String,
  },
  components: {
    mypageCard
  },
  data: () => ({
    drawer: null,
  }),
  created() {
    this.$vuetify.theme.dark = true;
  }
  ,computed: {
    ...mapGetters(["getBar"]),
  },
}
;
</script>
