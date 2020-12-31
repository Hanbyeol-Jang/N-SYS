<template>
  <v-container>
    <v-card class="elevation-12">
      <v-bottom-navigation
        :value="activeBtn"
        color="yellow accent-2"
        horizontal
      >
        <v-btn>
          <span>NickName</span>
          <v-icon>mdi-heart</v-icon>
        </v-btn>
      </v-bottom-navigation>

      <v-container :class="profileview">
        <v-row justify="center">
          <v-card-text>
            ◆ 현재 닉네임
            <v-text-field
              v-model="U_nickname"
              prepend-icon="mdi-account"
              type="text"
              readonly
            ></v-text-field>
            ◆ 바꿀 닉네임
            <v-text-field
              v-model="U_changenickname"
              prepend-icon="mdi-account"
              type="text"
            ></v-text-field>
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="yellow accent-4" @click="change_NickName"
              >Nickname_변경</v-btn
            >
          </v-card-actions>
        </v-row>
      </v-container>
    </v-card>
  </v-container>
</template>
<script>
import axios from "axios";
export default {
  name: "Changenickname",
  data() {
    return {
      U_nickname: "",
      U_changenickname: "",
    };
  },
  created() {
    this.U_nickname = localStorage.getItem("Now_Uname");
  },
  methods: {
    change_NickName() {
      axios
        .post("https://j3b201.p.ssafy.io:8443/Userinfo/updateName", {
          uiId: localStorage.getItem("Now_Uid"),
          uiName: this.U_changenickname,
        })
        .then(({ data }) => {
          console.log(data);
          console.log(data.Userinfo.uiName);
          if (data.Userinfo == "fail") {
            alert("이미 해당 닉네임으로 가입된 유저가 존재합니다.");
            window.location.href = "/changenickname";
          } else {
            alert("닉네임 변경 성공!!");
            localStorage.setItem("Now_Uname", data.Userinfo.uiName);
            window.location.href = "/myprofile";
          }
        })
        .catch(() => {
          alert("닉네임 변경 시 에러가 발생했습니다.");
          window.location.href = "/changenickname";
        });
    },
  },
};
</script>
