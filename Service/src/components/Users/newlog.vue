<template>
  <v-container>
    <v-card class="elevation-12">
      <v-bottom-navigation
        :value="activeBtn"
        color="yellow accent-2"
        horizontal
      >
        <v-btn @click="clicklogin">
          <span>Login</span>
          <v-icon>mdi-heart</v-icon>
        </v-btn>
        <v-btn @click="clickjoin">
          <span>Join</span>
          <v-icon>mdi-heart</v-icon>
        </v-btn>
      </v-bottom-navigation>
      <v-container :class="loginview">
        <v-row justify="center">
          <v-card-text>
            <v-form>
              <v-text-field
                label="Email"
                v-model="email"
                name="email"
                prepend-icon="mdi-account"
                type="text"
                :error-messages="emailErrors"
                @input="$v.email.$touch()"
                @blur="$v.email.$touch()"
                required
              ></v-text-field>
              <v-text-field
                v-model="password"
                label="Password"
                name="password"
                prepend-icon="mdi-lock"
                type="password"
                :error-messages="passwordErrors"
                @input="$v.password.$touch()"
                @blur="$v.password.$touch()"
                required
              ></v-text-field>
            </v-form>
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="green darken-3" @click="submitlogin">Login</v-btn>
            <!--            <v-btn color="yellow accent-4" @click="clickfindpw">FindPassword</v-btn>-->
            <v-btn color="lime" dark @click.stop="dialog = true">
              FindPassword
            </v-btn>
            <v-dialog v-model="dialog" max-width="290">
              <v-card>
                <v-card-title class="headline"> Find Password</v-card-title>
                <v-card-text>
                  If you want to find your password. Insert your email.
                </v-card-text>
                <v-card-text>
                  <v-form>
                    <v-text-field
                      label="Email"
                      v-model="templateParams.target_email"
                      name="email"
                      prepend-icon="mdi-account"
                      type="text"
                      :error-messages="emailErrors"
                      @input="$v.email.$touch()"
                      @blur="$v.email.$touch()"
                      required
                    ></v-text-field>
                  </v-form>
                </v-card-text>

                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn color="green darken-1" text @click="dialog = false">
                    CANCEL
                  </v-btn>
                  <v-btn color="green darken-1" text @click="submitfindpw">
                    FIND
                  </v-btn>
                </v-card-actions>
              </v-card>
            </v-dialog>
          </v-card-actions>
        </v-row>
      </v-container>

      <v-container :class="joinview">
        <v-row justify="center">
          <v-card-text>
            <v-form>
              <v-text-field
                label="Nickname"
                v-model="nickname"
                name="nickname"
                prepend-icon="mdi-account"
                type="text"
              ></v-text-field>
<!--                @input="$v.nickname.$touch()"-->
<!--                @blur="$v.nickname.$touch()"-->
<!--                required-->
<!--                :error-messages="nicknameErrors"-->

              <v-text-field
                label="Email"
                v-model="email"
                name="email"
                prepend-icon="mdi-account"
                type="text"
                :error-messages="emailErrors"
                @input="$v.email.$touch()"
                @blur="$v.email.$touch()"
                required
              ></v-text-field>
              <v-text-field
                v-model="password"
                label="Password"
                name="password"
                prepend-icon="mdi-lock"
                type="password"
                :error-messages="passwordErrors"
                @input="$v.password.$touch()"
                @blur="$v.password.$touch()"
                required
              ></v-text-field>
              <v-text-field
                v-model="repeatPassword"
                label="repeatPassword"
                name="repeatPassword"
                prepend-icon="mdi-lock"
                type="password"
                :error-messages="repeatPasswordErrors"
                @input="$v.repeatPassword.$touch()"
                @blur="$v.repeatPassword.$touch()"
              ></v-text-field>
            </v-form>
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="lime darken-3" @click="submitjoin">Join</v-btn>
          </v-card-actions>
        </v-row>
      </v-container>
    </v-card>
  </v-container>
</template>
<script>
import { mapActions } from "vuex";
import { required, sameAs, email, minLength } from "vuelidate/lib/validators";
export default {
  validations: {
    email: { required, email },
    password: { required, minLength: minLength(4) },
    repeatPassword: { sameAsPassword: sameAs("password") },
  },

  data() {
    return {
      activeBtn: 0,
      loginview: "d-flex",
      joinview: "d-none",
      findview: "d-none",
      nickname: "",
      email: "",
      password: "",
      dialog: false,
      repeatPassword: "",
      avatarImage: require("../../assets/images/person.png"),
      templateParams: {
        from_name: "SSAFY_B201_InsertCoin",
        to_name: localStorage.getItem("Now_Uname"),
        message_html: "http://localhost:8081/changepwbyemailjs" + "▶▶▶ Code : ",
        company_email: "http://localhost:8081/",
        target_email: "",
      },
    };
  },
  methods: {
    clicklogin() {
      this.clear();
      this.loginview = "d-flex";
      this.joinview = "d-none";
      this.findview = "d-none";
    },
    clickjoin() {
      this.clear();
      this.loginview = "d-none";
      this.joinview = "d-flex";
      this.findview = "d-none";
    },
    ...mapActions(["login", "join", "findpw"]),
    submitlogin() {
      this.repeatPassword = this.password;
      this.$v.$touch();
      if (this.$v.$invalid) {
        alert("모든 값을 입력해 주세요!");
      } else {
        let loginData = {
          uid: this.email,
          upw: this.password,
        };
        this.$store.dispatch("login", loginData);
      }
    },
    submitjoin() {
      this.$v.$touch();
      if (this.$v.$invalid) {
        alert("모든 값을 입력해 주세요!");
      } else {
        let joinData = {
          uid: this.email,
          upw: this.password,
          uname: this.nickname,
          uimage: this.avatarImage,
        };
        this.$store.dispatch("join", joinData);
      }
    },
    submitfindpw() {
      // this.$v.$touch(); 이거 왜 오류나는거지
      // if (this.$v.$invalid) {
      if (this.templateParams.target_email == null) {
        alert("모든 값을 입력해 주세요!");
      } else {
        let findpwData = {
          from_name: this.templateParams.from_name,
          to_name: this.templateParams.to_name,
          message_html: this.templateParams.message_html,
          company_email: this.templateParams.company_email,
          target_email: this.templateParams.target_email,
        };
        this.$store.dispatch("findpw", findpwData);
      }
    },
    clear() {
      this.$v.$reset();
      this.email = "";
      this.password = "";
      this.repeatPassword = "";
    },
  },
  computed: {
    // nicknameErrors() {
    //   const errors = [];
    //   if (!this.$v.email.$dirty) return errors;
    //   !this.$v.nickname.required && errors.push("닉네임을 입력해주세요.");
    //   return errors;
    // },
    emailErrors() {
      const errors = [];
      if (!this.$v.email.$dirty) return errors;
      !this.$v.email.email && errors.push("이메일 형식을 바르게 입력해주세요.");
      !this.$v.email.required && errors.push("이메일을 입력해주세요.");
      return errors;
    },
    passwordErrors() {
      const errors = [];
      if (!this.$v.password.$dirty) return errors;
      !this.$v.password.minLength &&
        errors.push(`비밀번호는 최소 4자 이상 입력해야 합니다.`);
      !this.$v.password.required && errors.push("비밀번호를 입력해주세요.");
      return errors;
    },
    repeatPasswordErrors() {
      const errors = [];
      if (!this.$v.repeatPassword.$dirty) return errors;
      !this.$v.repeatPassword.sameAsPassword &&
        errors.push("비밀번호가 다릅니다");
      return errors;
    },
  },
};
</script>
