<template>
<div>
  <div>image:{{this.game.gid}}</div>
  <div>title:{{this.game.gtitle}}</div>
  game
</div>
</template>

<script>
export default {
  name: "Game.vue",
  props: {
    game:Object,
  }
}
</script>

<style scoped>

</style>