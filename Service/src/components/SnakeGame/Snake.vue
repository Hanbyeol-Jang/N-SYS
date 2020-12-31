<template>
  <div>
    <div class="aaaacenter">
      <div class="robottext">{{ robotstext }}</div>
      <div class="robot">
        <img :src="this.repos[this.face].src" class="robotimg" />
      </div>
    </div>
    <div class="aaaacenter">
      <h2 :class="{ deadline: time <= 10 }">남은 시간 : {{ time }}</h2>
    </div>
    <div
      class="aaaacenter"
      v-bind:style="{ width: `50%`, float: `left`, align: `center` }"
    >
      <SnakeGame ref="snakeuser" />
    </div>
    <div
      class="aaaacenter"
      v-bind:style="{ width: `50%`, float: `right`, textalign: `center` }"
    >
      <SnakeGameAI ref="snakeai" />
    </div>

    <div class="aaaacenter">
      <div v-bind:style="{ width: `50%`, float: `left`, textalign: `center` }">
        <button
          type="button"
          class="snakebutton"
          v-on:click="SnakeStart()"
          v-if="!this.GameStarted"
        >
          시작하기
        </button>
        <button
          v-if="this.GameHasEnded"
          v-on:click="Refresh()"
          type="button"
          class="refreshbutton"
        >
          다시하기
        </button>
      </div>
      <div v-bind:style="{ width: `50%`, float: `right`, textalign: `center` }">
        <button
          type="button"
          class="diff"
          v-if="!this.GameStarted"
          v-on:click="ChangeDifficulty()"
        >
          난이도 변경
        </button>
      </div>
    </div>
  </div>
</template>

<style>
div.aaaacenter {
  text-align: center;
}

div.robot {
  width: 15%;
  margin-right: 10%;
  margin-top: 10px;
  margin-bottom: 10px;
  height: 100px;
  float: right;
}

div.robottext {
  margin-left: 10%;
  margin-top: 10px;
  margin-bottom: 10px;
  width: 65%;
  height: 100px;
  line-height: 50px;
  float: left;
  font-size: 15px;
  color: white;
  word-break: break-all;
  background-color: grey;
  border: 2px solid black;
}

.deadline {
  color: red;
}

.robotimg {
  height: 90px;
}

button.snakebutton {
  background-color: #8edb62;
  color: black;
  padding: 15px 32px;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 12px;
  border-radius: 5px;
}
button.refreshbutton {
  background-color: rgb(136, 76, 216);
  color: white;
  padding: 15px 32px;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 12px;
  border-radius: 5px;
}

button.diff {
  background-color: rgb(189, 131, 24);
  color: white;
  padding: 15px 32px;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 12px;
  border-radius: 5px;
}
</style>
<script>
// import img1 from
import SnakeGame from "./SnakeGame";
import SnakeGameAI from "./SnakeGameAI";
import axios from "axios";
import EventBus from "./EventBus";

export default {
  name: "Snake",
  data() {
    return {
      repos: [
        { name: "robot1", src: require("../../assets/images/robot1.png") },
        { name: "robot2", src: require("../../assets/images/robot2.png") },
        { name: "robot3", src: require("../../assets/images/robot3.png") },
        { name: "robot4", src: require("../../assets/images/robot4.png") },
        { name: "robot5", src: require("../../assets/images/robot5.png") },
        { name: "robot6", src: require("../../assets/images/robot6.png") },
      ],
      hard: false,
      face: 0,
      robotstext:
        "60초 내로 더 많은 점수를 얻은 쪽이 승리! 로그인 후 하드모드 격파시 랭킹 도전 가능",
      time: 60,
      Uname: null,
      GameStarted: false,
      GameHasEnded: false,
      HumanScore: 0,
      AIScore: 0,
      deadline: {
        color: "red",
      },
    };
  },
  methods: {
    SnakeStart() {
      this.GameStarted = true;
      this.$refs.snakeuser.SnakeStart();
      this.$refs.snakeai.SnakeStart();
      this.SetTimer();
    },
    ChangeDifficulty() {
      this.hard = !this.hard;
      if (this.hard) {
        this.face = 3;
      } else {
        this.face = 0;
      }
      this.$refs.snakeai.ChangeDiff();
    },
    SubmitGameData() {
      if (this.uiPk != undefined) {
        axios
          .post("https://j3b201.p.ssafy.io:8443/Play/snake/", {
            gaId: 1,
            plLevel: 5,
            plScore: this.HumanScore,
            plVictory: 1,
            uiPk: this.uiPk,
          })
          .then((res) => {
            alert(`${res.data.rank}위에 랭크인 하셨습니다. 축하드립니다.`);
          });
      }

      // .catch((e) => {
      //   alert("랭킹 진입에 실패하였습니다.");
      //   //   console.error(err);
      // });
    },

    SetTimer() {
      setTimeout(() => {
        if (this.time <= 0) {
          this.EndGame();
          return;
        }
        this.time -= 1;
        this.SetTimer();
      }, 1000);
    },
    EndGame() {
      //   console.log("경기끝");
      this.$refs.snakeuser.SnakeStop();
      this.$refs.snakeai.SnakeStop();
      //   console.log(this.HumanScore, this.AIScore);
      if (this.HumanScore > this.AIScore) {
        this.robotstext = "AI의 패배. 인간의 승리.";
        if (this.hard) {
          this.SubmitGameData();
        }

        if (this.hard) {
          this.face = 5;
        } else {
          this.face = 2;
        }
      } else {
        if (this.hard) {
          this.face = 4;
        } else {
          this.face = 1;
        }
        this.robotstext = "인간의 패배. 다음 기회에..";
      }
      this.GameHasEnded = true;
    },
    Refresh() {
      this.$router.go(0);
    },
  },
  components: {
    SnakeGame,
    SnakeGameAI,
  },
  created() {
    this.uiPk = localStorage.getItem("Now_Upk");
    // console.log(this.uiPk);
    // console.log("pk test");
    EventBus.$on("human-completed", (payload) => {
      this.HumanScore = payload[0];
    });
    EventBus.$on("ai-completed", (payload) => {
      this.AIScore = payload[0];
    });
  },
};
</script>
