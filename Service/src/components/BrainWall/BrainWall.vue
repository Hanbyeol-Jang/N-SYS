<template>
  <div>
    <h1 align="center">두뇌의 벽</h1>
    <v-card-actions class="justify-center">
      <v-btn
          v-if="!gameStartFlag"
          :class="`rounded-xl`"
          @click="clickStart()"
          color="light-green lighten-1"
          elevation="2"
          tile
          x-large
      >START</v-btn>
    </v-card-actions>


    <div v-if="gameStartFlag">

      <v-container class="dark lighten-5">
        <v-row>
          <v-col
              >
            <v-card
                class="pa-2"
                outlined
                tile

            >
              <h3 align="center">카운트 다운</h3>
              <h1 align="center" class="red--text" id="cd">{{ countDown }}</h1>
            </v-card>
          </v-col>
          <v-col>
            <v-card
                class="pa-2"
                outlined
                tile
            >
              <h2 align="center">round - {{ round }}/5</h2>
              <h2 align="center">score - {{ score }}</h2>
              <h1 align="center" id="label-container"></h1>
            </v-card>
          </v-col>
        </v-row>
      </v-container>

        <v-card-actions class="justify-center">
          <v-btn
              :disabled="countFlag" @click="generateRandomNumber()"
              :class="`rounded-lg`"
              color="light-green lighten-1"
              elevation="2"
              tile
              x-large
          >포즈 바꾸기 버튼</v-btn>
        </v-card-actions>

      <v-container class="dark lighten-5">
          <random-pose class="temp"></random-pose>
          <div class="temp"><canvas id="canvas"></canvas></div>
      </v-container>

      </div>
  </div>
</template>

<script>
import "@tensorflow/tfjs";
import RandomPose from "@/components/BrainWall/RandomPose";
import * as tmPose from "@teachablemachine/pose";
import { mapGetters } from "vuex";

let model, webcam, ctx, labelContainer, maxPredictions;

export default {
  name: "BrainWall",
  data() {
    return {
      startBtn: true,
      requestId: undefined,
      score: 0,
      round: 0,
      roundFinishFlag: false,
      scoreFlag: false,
      gameStartFlag: false,
      countDown: 10,
      countFlag: false, //카운트 다운 버튼 비활성화
    };
  },
  components: {
    RandomPose,
  },
  computed: {
    ...mapGetters(["getCurrentPose"]),
  },
  methods: {
    clickStart() {
      this.init();
      this.roundFinishFlag = false
      this.gameStartFlag = true
    },
    countDownTimer() {
      if (this.countDown > 0) {
        setTimeout(() => {
          this.countDown -= 1
          this.countDownTimer()
        }, 1000)
      } else if (this.countDown == 0) {
        this.countFlag = false
        if (this.round == 5) {
          alert("게임이 종료되었습니다.\n맞추신 개수는 "+this.score+"입니다.");
          this.gameStartFlag=false
          this.roundFinishFlag = true
          this.finishRoundSetReadyPoseM()
          this.round = 0
          this.score = 0
        }
      }
    },
    generateRandomNumber() {
      let tempRandomNumber = Math.floor(Math.random() * 9 + 1); // 숫자 바꾸면 됨
      this.changeCurrentPoseM(tempRandomNumber);
      this.round++;
      this.countDown = 10;
      this.countFlag = true;
      this.countDownTimer();
      this.scoreFlag = false;

    },
    changeCurrentPoseM: function(x) {
      this.$store.commit("changeCurrentPose", x);
    },
    finishRoundSetReadyPoseM: function(){
      this.$store.commit("finishRoundSetReadyPose","ready0")
    },
    async init() {
      this.startBtn = false;

      // const URL = "https://teachablemachine.withgoogle.com/models/sV2phcmJ-/";
      const URL = "https://teachablemachine.withgoogle.com/models/1tX6vs5hQ/";
      const modelURL = URL + "model.json";
      const metadataURL = URL + "metadata.json";

      model = await tmPose.load(modelURL, metadataURL);
      maxPredictions = model.getTotalClasses();

      // Convenience function to setup a webcam
      const size = 400;
      const flip = true; // whether to flip the webcam
      webcam = new tmPose.Webcam(size, size, flip); // width, height, flip
      await webcam.setup(); // request access to the webcam
      await webcam.play();
      this.requestId = window.requestAnimationFrame(this.loop);

      const canvas = document.getElementById("canvas");

      canvas.width = size;
      canvas.height = size;

      ctx = canvas.getContext("2d");
      labelContainer = document.getElementById("label-container");
      for (let i = 0; i < maxPredictions; i++) {
        // and class labels
        labelContainer.appendChild(document.createElement("div"));
      }
    },
    async loop() {
      //timestamp
      webcam.update(); // update the webcam frame
      await this.predict(); //this.predict();
      if (this.requestId) {
        window.requestAnimationFrame(this.loop);
      }
    },
    async predict() {
      const { pose, posenetOutput } = await model.estimatePose(webcam.canvas);
      const prediction = await model.predict(posenetOutput);

      for (let i = 0; i < maxPredictions; i++) {
        // console.log(prediction[i].className)

        if (this.$store.state.currentPose == prediction[i].className) {
          // labelContainer.childNodes[0].innerHTML = prediction[i].className + ": " + prediction[i].probability.toFixed(2);
          labelContainer.childNodes[0].innerHTML = "정확도: " + prediction[i].probability.toFixed(2);
        }

        if (
            this.$store.state.currentPose == prediction[i].className &&
            !this.scoreFlag &&
            prediction[i].probability.toFixed(2) >= 0.98
        ) {
          if (this.countDown > 0) {
            this.score++;
            this.scoreFlag = true;
          }
        }

      }

      // finally draw the poses
      this.drawPose(pose);
    },
    drawPose(pose) {
      if (webcam.canvas) {
        ctx.drawImage(webcam.canvas, 0, 0);
        // draw the keypoints and skeleton
        if (pose) {
          const minPartConfidence = 0.5;
          tmPose.drawKeypoints(pose.keypoints, minPartConfidence, ctx);
          tmPose.drawSkeleton(pose.keypoints, minPartConfidence, ctx);
        }
      }
    },
  },
};
</script>
<style>

.temp{
  float: left;
  width:50%;
}
#cd{
  font-size: 62px;
}

</style>
