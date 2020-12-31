<template>
  <v-container fill-height fluid >
      <v-img src="@/assets/images/backimgg.png"
             height="550px" width="100vw">
    <v-row align="center" style="margin-top: 5%;">

      <v-col cols="12">
        <v-container class="fill-height" :class="sheet1" @click="toggleSheet">
          <v-hover v-slot:default="{ hover }">
            <v-card
              :elevation="hover ? 24 : 2"
              class="mx-auto"
              height="400"
              max-width="350"
            >
              <v-img
                src="@/assets/images/insertcoin.png"
                height="400"
                max
                width="350"
              ></v-img>
            </v-card>
          </v-hover>
        </v-container>
        <v-container class="fill-height" :class="sheet2">
          <v-hover v-slot:default="{ hover }">
            <v-card
              :elevation="hover ? 24 : 2"
              class="mx-auto"
              height="400"
              max-width="350"
            >
              <v-img
                src="@/assets/images/block.png"
                height="400"
                max
                width="350"
              >
                <v-container fluid >
                  <v-row>
                    <v-hover v-slot:default="{ hover }" >
                      <v-card :elevation="hover ? 24 : 2" style=" background-color: transparent">
                        <v-img contain
                               src="@/assets/images/icon_brainwall.png"
                               @click="toggleSheet3" />
                      </v-card>
                    </v-hover>
                  </v-row>
                  <v-row>
                    <v-hover v-slot:default="{ hover }">
                      <v-card :elevation="hover ? 24 : 2" style="background-color: transparent">
                        <v-img src="@/assets/images/icon_snake2.png"
                               @click="$router.push('/game3')"/>
                      </v-card>
                    </v-hover>
                  </v-row>
                  <v-row>
                    <v-hover v-slot:default="{ hover }" >
                      <v-card :elevation="hover ? 24 : 2" style="background-color: transparent">
                        <v-img src="@/assets/images/icon_20482.png"
                               @click="$router.push('/2048')"/>
                      </v-card>
                    </v-hover>
                  </v-row>
                </v-container>
              </v-img>
            </v-card>
          </v-hover>
        </v-container>
        <v-container class="fill-height" :class="sheet3" >
          <v-hover v-slot:default="{ hover }">
            <v-card
                :elevation="hover ? 24 : 2"
                class="mx-auto"
                height="400"
                max-width="350"
            >
              <v-img
                  src="@/assets/images/block.png"
                  height="400"
                  max
                  width="350"
              >
                <v-container fluid class="pa-10">
                  <v-row>
                    <v-hover v-slot:default="{ hover }" >
                      <v-card :elevation="hover ? 24 : 2" style="background-color: transparent">
                        <v-img src="@/assets/images/icon_1play.png"
                               @click="$router.push('/game1')"/>
                      </v-card>
                    </v-hover>
                  </v-row>
                  <v-row>
                    <v-hover v-slot:default="{ hover }">
                      <v-card :elevation="hover ? 24 : 2" style="background-color: transparent" >
                        <v-img src="@/assets/images/icon_2play.png"
                               @click="$router.push('/game2')"/>
                      </v-card>
                    </v-hover>
                  </v-row>

                </v-container>
              </v-img>
            </v-card>
          </v-hover>
        </v-container>
      </v-col>
    </v-row>
      </v-img>
  </v-container>
</template>
<script>
export default {
  name: "MainPage",
  data: () => ({
    sheet1: "d-flex",
    sheet2: "d-none",
    sheet3: "d-none"
  }),
  created() {
    this.$store.commit(
      "changebar",
      "내가 승리한 것이지 인간이 승리한 것이 아니야"
    );
  },
  methods: {
    toggleSheet() {
      this.sheet1 = "d-none";
      this.sheet2 = "d-flex";
      this.sheet3 = "d-none";
    },
    toggleSheet3() {
      this.sheet1 = "d-none";
      this.sheet2 = "d-none";
      this.sheet3 = "d-flex";
    },
  },
};
</script>

