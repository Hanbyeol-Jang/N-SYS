<template>
  <v-container>
    <brain-wall v-if="tempvar == true"></brain-wall>
    <snake v-if="tempvar == false"></snake>

    <h1>Games</h1>

    <!-- 게임 모든 리스트  v-for 돌려서 출력 && axios-->
    <!--    <game-container v-for="game in gameList" :key="game.gid">-->

    <br />
    <div v-for="game in gameList" :key="game.gid">
      <game-container :game="game" />
      <!--    {{game.gtitle}}-->
    </div>
  </v-container>
</template>

<script>
import GameContainer from "@/components/Game/GameContainer";
import BrainWall from "@/components/BrainWall/BrainWall";
import Snake from "@/components/SnakeGame/Snake";

import axios from "axios";

export default {
  name: "Games",
  data() {
    return {
      tempvar: false,
      // tempvar는 임시로 만든 변수입니다. 차후 삭제요망
      gameList: [],
    };
  },
  methods: {
    getGames() {
      axios
        .get(this.$store.state.SERVER_URL + "/game/list")
        .then((res) => {
          this.gameList = res.data;
          console.log(res);
        })
        .catch((err) => {
          console.log(err);
        });
    },
  },
  components: {
    GameContainer,
    BrainWall,
    Snake,
  },
  actions: {},
};
</script>

<style scoped>
</style>