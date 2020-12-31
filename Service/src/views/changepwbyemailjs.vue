<template>
  <v-container>
    <v-card class="elevation-12">
      <v-bottom-navigation
        :value="activeBtn"
        color="yellow accent-2"
        horizontal
      >
        <v-btn>
          <span>PW</span>
          <v-icon>mdi-heart</v-icon>
        </v-btn>
      </v-bottom-navigation>

      <v-container :class="profileview">
        <v-row justify="center">
          <v-card-text>
            이메일에 써있던 코드를 입력해주세요.
            <v-text-field
              v-model="U_code"
              prepend-icon="mdi-lock"
              type="password"
            ></v-text-field>
            바꿀 비밀번호를 입력한 후 PW_변경 버튼을 눌러주세요.
            <v-text-field
              v-model="U_password"
              prepend-icon="mdi-lock"
              type="password"
            ></v-text-field>
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="yellow accent-4" @click="change_PW">PW_변경</v-btn>
          </v-card-actions>
        </v-row>
      </v-container>
    </v-card>
  </v-container>
</template>
<script>
import axios from "axios";
export default {
  name: "Changepwbyemailjs",
  data() {
    return {
      U_code: "",
      U_password: "",
    };
  },
  methods: {
    change_PW() {
      if (this.U_code != localStorage.getItem("code")) {
        alert("잘못된 코드를 입력하셨습니다.");
        window.location.href = "/changepwbyemailjs";
      } else {
        axios
          .post("https://j3b201.p.ssafy.io:8443/Userinfo/updatepw", {
            uiId: localStorage.getItem("email"),
            uiPw: this.U_password,
          })
          .then(({ data }) => {
            console.log(data);
            alert("변경한 비밀번호로 로그인해주시기 바랍니다!!");
            window.location.href = "/";
          })
          .catch(() => {
            alert("닉네임 변경 시 에러가 발생했습니다.");
            window.location.href = "/changepwbyemailjs";
          });
      }
    },
  },
};
</script>
