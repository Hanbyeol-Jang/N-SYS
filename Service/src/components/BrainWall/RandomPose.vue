<template>
  <div id="container">

    <v-img :src="require(`@/assets/images/pose/${getCurrentPose}.png`)"
           id="cenimg"
           contain
    max-height="400px"
    max-width="400px"></v-img>

  </div>

</template>

<script>

import {mapGetters} from 'vuex'

export default {
  name: 'RandomPose',

  computed:{
    ...mapGetters(['getCurrentPose'])
  },
  methods:{

  },

}
</script>

<style>
#cenimg {
  display: block; margin: 0px auto;
}
</style>
