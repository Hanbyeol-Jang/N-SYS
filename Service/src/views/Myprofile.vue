<template>
  <v-container>
    <v-card class="elevation-12">
      <v-bottom-navigation
        :value="activeBtn"
        color="yellow accent-2"
        horizontal
      >
        <v-btn>
          <span>My Profile</span>
          <v-icon>mdi-heart</v-icon>
        </v-btn>
      </v-bottom-navigation>

      <v-container :class="profileview">
        <v-row justify="center">
          <v-card-text>
            <v-card
              :elevation="hover ? 24 : 2"
              class="mx-auto"
              height="250"
              max-width="250"
            >
              <v-img :src="U_image" height="250" max-width="250"></v-img>
            </v-card>
            <v-text-field
              v-model="U_nickname"
              prepend-icon="mdi-account"
              type="text"
              readonly
            ></v-text-field>
            <v-text-field
              v-model="U_email"
              prepend-icon="mdi-account"
              type="text"
              readonly
            ></v-text-field>
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
              color="purple accent-1"
              router
              :to="{ name: 'changenickname' }"
              >닉네임변경</v-btn
            >
            <v-btn color="purple accent-2" router :to="{ name: 'changepw' }"
              >비밀번호변경</v-btn
            >

            <v-btn
              id="changeImage"
              color="purple accent-3"
              @click="$refs.userProfileImgChange.click()"
              >이미지변경</v-btn
            >

            <v-btn
              color="purple accent-3"
              @click="saveImage"
              v-if="Ischangeimage"
            >
              이미지저장
            </v-btn>

            <v-btn color="purple accent-4" @click="deleteuser">회원탈퇴</v-btn>
            <input
              style="display: none"
              ref="userProfileImgChange"
              type="file"
              accept="image/jpeg,image/gif,image/png"
              @change="onFileChange"
              enctype="multipart/form-data"
            />
          </v-card-actions>
        </v-row>
      </v-container>
    </v-card>
  </v-container>
</template>

<script>
import axios from "axios";
export default {
  name: "Myprofile",
  data() {
    return {
      U_nickname: "",
      U_email: "",
      U_password1: "",
      U_password2: "",
      U_image: "",
      item: {},
      // vediosrc: require("../../assets/images/2048_2분시연영상.mp4"),
      image: "",
      message: "",
      profileImage: {},
      responseimage: null,
      Ischangeimage: false,
    };
  },
  created() {
    this.U_nickname = localStorage.getItem("Now_Uname");
    this.U_email = localStorage.getItem("Now_Uid");
    this.U_image = localStorage.getItem("Now_srcImage");
  },
  methods: {
    //
    //여기서부터는 지우지 말아주세요.
    //
    onFileChange(e) {
      var files = e.target.files || e.dataTransfer.files;
      if (!files.length) return;
      console.log(files[0]);
      this.profileImage = files[0];
      // console.log(this.profileImage);
      // console.log(this.profileImage.name);
      // console.log(this.profileImage.lastModifiedDate);
      // console.log(this.profileImage.size);
      // console.log(this.profileImage.type);
      // console.log(this.profileImage.webkitRelativePath);
      var isImageOk = true;
      isImageOk = this.chk_file_type(files[0]);
      if (isImageOk == true) {
        this.createImage(files[0]);
      } else {
        window.location.href = "/myprofile";
      }
    },
    chk_file_type(obj) {
      // console.log("obj : " + obj);
      // console.log(/^image\/\w+/.test(obj.type));
      // console.log(!/^image\/\w+/.test(obj.type));
      if (/^image\/\w+/.test(obj.type)) {
        alert("이미지 파일 등록 완료");
        return true;
      } else {
        alert("이미지 파일만 등록이 가능합니다");
        return false;
      }
    },
    createImage(file) {
      // var image = new Image();
      var reader = new FileReader();
      var imgurl = "";
      reader.onload = (e) => {
        this.U_image = e.target.result;
        // console.log("this.image : " + this.image);
        // console.log("e.target.result : " + e.target.result);
        imgurl = "" + e.target.result;
      };
      reader.readAsDataURL(file);
      console.log("imgurl : " + imgurl);
      console.log("file : " + file);

      var formData = new FormData();
      formData.append("image", file);
      for (let key of formData.entries()) {
        console.log(`${key}`);
      }
      axios
        .post("https://j3b201.p.ssafy.io:8443/Userinfo/fileUpload", formData)
        .then((res) => {
          console.log("fileupload 결과 res : " + res.data);
          console.log("fileupload 결과 res : " + JSON.stringify(res));
          this.Ischangeimage = true;
        })
        .catch((error) => {
          console.log(error.response);
          alert("사진 변경 시 에러가 발생했습니다.");
        });
    },
    saveImage() {
      console.log(this.profileImage);
      var profilefile = this.profileImage;
      var reader = new FileReader();
      reader.readAsDataURL(profilefile);
      console.log("file : " + profilefile);

      var formData = new FormData();
      formData.append("image", profilefile);
      for (let key of formData.entries()) {
        console.log(`${key}`);
      }
      //Image 지정된 경로에 저장, 저장경로 반환.
      axios
        .post(
          "https://j3b201.p.ssafy.io:8443/Userinfo/profileimgsave",
          formData
        )
        .then((res) => {
          // console.log("profileimgsave 결과 res : " + res.data);
          // console.log("profileimgsave 결과 res : " + JSON.stringify(res));
          this.responseimage = res.data;
          console.log("경로 저장 후 res.data : " + this.responseimage);
          if (res != null) {
            axios
              .post("https://j3b201.p.ssafy.io:8443/Userinfo/updateProfile", {
                uiId: localStorage.getItem("Now_Uid"),
                uiImage: this.responseimage,
                uiImgtype: this.profileImage.type,
              })
              .then(({ data }) => {
                let msg = "image DB저장 실패.";
                if (data != null) {
                  msg = "image DB저장 완료.";
                  console.log("DB저장 후 data : " + data);
                  var finallysrc =
                    "data:" + this.profileImage.type + ";base64," + data;
                  localStorage.setItem("Now_srcImage", finallysrc);
                }
                alert(msg);
                window.location.href = "/myprofile";
              })
              .catch(() => {
                alert("image DB저장 시 에러가 발생했습니다.");
                window.location.href = "/myprofile";
              });
          }
        })
        .catch((error) => {
          console.log(error.response);
          alert("사진 저장 시 에러가 발생했습니다.");
        });
      //Image DB저장.
    },
    //
    //여기까지는 지우지 말아주세요.
    deleteuser() {
      axios
        .post("https://j3b201.p.ssafy.io:8443/Userinfo/deleteaccount", {
          uiId: localStorage.getItem("Now_Uid"),
        })
        .then(({ data }) => {
          console.log(data);
          if (data.Userinfo != null) {
            localStorage.clear();
            alert("회원탈퇴 성공!!");
            window.location.href = "/";
          }
        })
        .catch(() => {
          alert("회원탈퇴 시 에러가 발생했습니다.");
          window.location.href = "/";
        });
    },
  },
};
</script>
