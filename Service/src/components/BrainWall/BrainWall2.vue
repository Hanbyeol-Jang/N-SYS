<template>
  <div style="display: block; position: relative; margin: 10px; margin-top: 2%">
    <v-card class="item" style="float: left ;
     margin-left: 5%;  width: 40%; ">
      <div>
        <v-card style="height: 250px; margin-top: 20px; margin-left:10%; margin-right: 10% ">
          <div v-if="gameStartFlag" style="text-align: center; margin: 5%">
            <div v-if="!roundFinishFlag">
              <div><h1>{{ countDown }}</h1></div>
              <div><h2> round : {{ round }}/5</h2></div>
              <div><h2> score : {{ score }} </h2></div>
              <div style="font-size: 30px" id="label-container"></div>
              <v-btn :disabled="countFlag" color="green darken-3" @click="generateRandomNumber">
                포즈 바꾸기
              </v-btn>
              <v-btn color="green darken-4" @click="clickExit" >나가기</v-btn>
            </div>
          </div>
          <div v-if="!gameStartFlag" style=" text-align: center; align-content: center">
            <v-btn color="green darken-3" @click="clickStart" style="margin-top: 20%">START</v-btn>
            <v-btn color="green darken-4" @click="clickExit" style="margin-top: 20%;margin-left: 10px">나가기</v-btn>
          </div>
        </v-card>
        <div style="height: 370px;  margin: 10px">
          <div class="container">
            <v-card class="item" style="width: 47%; height: auto; margin: 5px;">
              <random-pose></random-pose>
            </v-card>
            <div class="item" style="float: left ; width: 3% ;background-color: transparent">
              <img>
            </div>
            <v-card class="item" style="width: 47%; height: auto; text-align: center">
              <video style="width: 100%; height: 100%; background-color: black " id='remoteVideo' ref="remoteVideo"
                     autoplay muted></video>
            </v-card>
          </div>
        </div>
      </div>
    </v-card>
    <div class="item" style="float: left ; width: 5% ;background-color: transparent">
      <img>
    </div>
    <v-card class="item" style="float: left ;background-color: black; margin-right:5% ;width: 35%; height: 650px">
      <div style="margin-top: 15% ;margin-bottom: 15%; ">
        <video style="width: 100%; height: auto" id='localVideo' ref="localVideo" autoplay
               muted></video>
      </div>
    </v-card>
  </div>

</template>

<style scoped>
.container {
  display: flex;
  justify-content: space-around;
  align-content: center;
}

</style>

<script>
import "@tensorflow/tfjs";
import RandomPose from "@/components/BrainWall/RandomPose";
import * as tmPose from "@teachablemachine/pose";
import {mapGetters} from "vuex";
import io from 'socket.io-client';
const socket = io('https://j3b201.p.ssafy.io:3000' ,
    { secure: true, reconnect: true, rejectUnauthorized : false });
// Vue.prototype.$socket= socket;
let model, labelContainer, maxPredictions;

const mediaOption = {
  // audio: true,
  video: {
    mandatory: {
      maxWidth: 300,
      maxHeight: 300,
      maxFrameRate: 5,
    },
    optional: [
      {googNoiseReduction: true}, // Likely removes the noise in the captured video stream at the expense of computational effort.
      {facingMode: 'user'}, // Select the front/user facing camera or the rear/environment facing camera if available (on Phone)
    ],
  },
};

export default {
  name: 'BrainWall',

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

      countFlag: false, //카운트 다운 버튼

      ///////////////////////webRTC////////////////////////////////
      startbtnvalue: 'START',
      room: 'foo',
      isInitiator: false,
      isStarted: false,
      isChannelReady: false,
      turnReady: false,
      localStream: {type: Object},
      remoteStream: {type: Object},
      pc: {type: Object},
      pc_config: {
        'iceServers': [{
          'url': 'stun:stun.l.google.com:19302'
        }]
      },
      sdpConstraints: {
        'mandatory': {
          // 'OfferToReceiveAudio': true,
          'OfferToReceiveVideo': true
        }
      },
      constraints: {
        video: true
      },
      makeroom: false

    }
  },
  components: {
    RandomPose,
  },
  computed: {
    ...mapGetters(['getCurrentPose'])
  },
  created() {

    this.$store.commit("changebar", "두뇌의벽");

    if (this.room !== '') {
      socket.emit('create or join', this.room);
      socket.emit
    }
    // else{
    //   window.room = prompt("방이름을 적어주세용:");
    //   this.room = window.room;
    //   this.$socket.emit('roommk', this.room);
    //   console.log('roomname',this.room);

    socket.on('created', function () {
      this.isInitiator = true;
    }.bind(this));

    socket.on('full', function () {
    });
    let _this = this;
    socket.on('join', function () {
      _this.isChannelReady = true;

    });

    socket.on('joined', function () {
      _this.isChannelReady = true;
    });

    socket.on('log', function () {
    });

    socket.on('message', function (message) {
      // console.log('message '+message);
      if (message === 'got user media') {
        this.maybeStart2();
      } else if (message.type === 'offer') {
        if (!this.isInitiator && !_this.isStarted) {
          this.maybeStart2();
        }
        this.pc.setRemoteDescription(new RTCSessionDescription(message));
        this.doAnswer();
      } else if (message.type === 'answer' && this.isStarted) {
        this.pc.setRemoteDescription(new RTCSessionDescription(message));
      } else if (message.type === 'candidate' && this.isStarted) {
        let candidate = new RTCIceCandidate({
          sdpMLineIndex: message.label,
          candidate: message.candidate
        });
        this.pc.addIceCandidate(candidate);
      } else if (message === 'bye' && this.isStarted) {
        // console.log('res bye');
        // console.log('he')
        // this.$refs.remoteVideo.classList.remove("remoteVideoInChatting");
        // this.$refs.localVideo.classList.remove("localVideoInChatting");
        this.isStarted = false;
        this.pc.close();
        this.pc = null;
        // stop();
        this.isInitiator = false;
        // console.log('handle')
        this.$router.push('/');
        // console.log('after bye ')
        // socket.emit('bye');
      }
    }.bind(this));


    navigator.mediaDevices.getUserMedia(mediaOption)
        .then(this.gotStream)
        .catch(function (e) {
          alert('getUserMedia() error: ' + e.name);
        });


    socket.on('changepose', (data) => {
      this.changeCurrentPoseM(data.tempRandomNumber);
      this.round = data.round;
      this.countDown=10;
    }).bind(this);

    if (location.hostname !== "localhost") {
      this.requestTurn('https://computeengineondemand.appspot.com/turn?username=41784574&key=4080218913');
    }
    window.onbeforeunload = function () {
      this.sendMessage('bye');
    }
  },

  methods: {
    clickExit(){
      this.sendMessage('bye');
      this.$router.push("/");
    },
    sendMessage(message) {
      // console.log(message);
      socket.emit('message', message);
    },
    maybeStart2() {
      if (!this.isStarted && this.localStream.active === true
          && this.isChannelReady) {
        this.createPeerConnection();
        this.pc.addStream(this.localStream);
        this.isStarted = true;
        if (this.isInitiator) {
          this.doCall();
        }
      }
    },
    gotStream(stream) {
      // console.log('logger - gotStream '+ stream)

      this.localStream = stream;
      // this.$refs.localVideo.src = window.URL.createObjectURL(stream);
      // console.log('event.stream--local ' +JSON.stringify(stream));
      this.$refs.localVideo.srcObject = stream;
      this.sendMessage('got user media');
      if (this.isInitiator) {
        this.maybeStart2();
      }
    },
    createPeerConnection() {
      try {
        this.pc = new RTCPeerConnection(null);
        this.pc.onicecandidate = this.handleIceCandidate;
        this.pc.onaddstream = this.handleRemoteStreamAdded;
        this.pc.onremovestream = this.handleRemoteStreamRemoved;
      } catch (e) {
        alert('Cannot create RTCPeerConnection object.');
        // return;
      }
    },
    handleIceCandidate(event) {
      if (event.candidate) {
        this.sendMessage({
          type: 'candidate',
          label: event.candidate.sdpMLineIndex,
          id: event.candidate.sdpMid,
          candidate: event.candidate.candidate
        });
      }
    },
    handleCreateOfferError() {
    },
    doCall() {
      this.pc.createOffer(this.setLocalAndSendMessage,
          this.handleCreateOfferError);
    },
    doAnswer() {
      this.pc.createAnswer().then(
          this.setLocalAndSendMessage,
          this.onCreateSessionDescriptionError
      );
    },
    setLocalAndSendMessage(sessionDescription) {
      this.pc.setLocalDescription(sessionDescription);
      this.sendMessage(sessionDescription);
    },
    onCreateSessionDescriptionError() {
    },
    requestTurn(turn_url) {
      let turnExists = false;
      for (let i in this.pc_config.iceServers) {
        if (this.pc_config.iceServers[i].url.substr(0, 5) === 'turn:') {
          turnExists = true;
          this.turnReady = true;
          break;
        }
      }
      if (!turnExists) {
        let xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function () {
          if (xhr.readyState === 4 && xhr.status === 200) {
            let turnServer = JSON.parse(xhr.responseText);
            this.pc_config.iceServers.push({
              'urls': 'turn:' + turnServer.username + '@' + turnServer.turn,
              'credential': turnServer.password
            });
            this.turnReady = true;
          }
        };
        xhr.open('GET', turn_url, true);
        xhr.send();
      }
    },
    handleRemoteStreamAdded(event) {
      // console.log('logger - handleRemoteStreamAdded[event] '+ event)
      // console.log('logger - handleRemoteStreamAdded[event.stream] '+ event.stream)
      this.remoteStream = event.stream;
      this.$refs.remoteVideo.srcObject = event.stream;
      // console.log('event.stream1 ' +JSON.stringify(event));
      // console.log('event.stream2 ' +JSON.stringify(event.stream));

      this.$refs.remoteVideo.classList.add("remoteVideoInChatting");
      this.$refs.localVideo.classList.add("localVideoInChatting");


    },
    handleRemoteStreamRemoved() {
    },
    handleRemoteHangup() {
      // console.log('he')
      this.$refs.remoteVideo.classList.remove("remoteVideoInChatting");
      this.$refs.localVideo.classList.remove("localVideoInChatting");

      stop();
      this.isInitiator = false;
      // console.log('handle')
      this.$router.push('/');
    },
    stop() {
      this.isStarted = false;
      this.pc.close();
      this.pc = null;
    },
    ///////////////////////webRTC////////////////////////////////

    clickStart() {
      this.init()
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
          alert('게임이 종료되었습니다.')
          this.roundFinishFlag = true;
          this.gameStartFlag = false;
          this.round = 0
          this.score = 0

        }
      }
    },
    generateRandomNumber() {
      let tempRandomNumber = Math.floor(Math.random() * 9 +1) // 숫자 바꾸면 됨
      this.changeCurrentPoseM(tempRandomNumber)
      this.round++


      this.countDown = 10
      this.countFlag = true
      this.countDownTimer()

      // if(this.round==5){
      //   this.roundFinishFlag = true
      //   this.round = 0
      //   this.score=0
      // }
      this.scoreFlag = false
      socket.emit('changepose',
          {tempRandomNumber: tempRandomNumber, round: this.round});

    },
    changeCurrentPoseM: function (x) {
      this.$store.commit("changeCurrentPose", x)
    },
    async init() {
      this.startBtn = false;

      const URL = "https://teachablemachine.withgoogle.com/models/1tX6vs5hQ/";
      const modelURL = URL + "model.json";
      const metadataURL = URL + "metadata.json";

      model = await tmPose.load(modelURL, metadataURL);
      maxPredictions = model.getTotalClasses();

      this.requestId = window.requestAnimationFrame(this.loop);


      labelContainer = document.getElementById("label-container");
      for (let i = 0; i < maxPredictions; i++) {
        // and class labels
        labelContainer.appendChild(document.createElement("div"));
      }
    },
    async loop() {
      await this.predict(); //this.predict();
      if (this.requestId) {

        window.requestAnimationFrame(this.loop);
      }
    },
    async predict() {
      const {posenetOutput} = await model.estimatePose(this.$refs.localVideo);
      const prediction = await model.predict(posenetOutput);

      for (let i = 0; i < maxPredictions; i++) {

        if (this.$store.state.currentPose == prediction[i].className) {
          // labelContainer.childNodes[0].innerHTML = prediction[i].className + ": " + prediction[i].probability.toFixed(2);
          labelContainer.childNodes[0].innerHTML = " 정확도 : " + prediction[i].probability.toFixed(2);
        }

        if (this.$store.state.currentPose == prediction[i].className &&
            !this.scoreFlag && prediction[i].probability.toFixed(2) >= 0.5) {
          if (this.countDown > 0) {
            this.score++
            this.scoreFlag = true
          }

          if (
              this.$store.state.currentPose == prediction[i].className &&
              !this.scoreFlag &&
              prediction[i].probability.toFixed(2) >= 0.9
          ) {
            if (this.countDown > 0) {
              this.score++;
              this.scoreFlag = true;
            }
          }
        }
      }
    }
  }
}
</script>
