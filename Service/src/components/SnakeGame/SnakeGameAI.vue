
<template>
  <div>
    <body>
      <h3 v-if="this.difficulty == 2">AI-Easy</h3>
      <h3 v-if="this.difficulty == 3" v-bind:style="{ color: 'red' }">
        AI-Hard
      </h3>
      <p>Score: {{ score }}</p>
      <p>Combo: {{ combo }}</p>
      <canvas id="snakeboardAI" width="300" height="300"></canvas>
    </body>
  </div>
</template>




<script>
import axios from "axios";
import { AI } from "./AI.js";
import EventBus from "./EventBus";

export default {
  name: "SnakeGameAI",
  data() {
    return {
      difficulty: 2,
      speed: 30,
      testbool: null,
      testforfood: false,
      timer: 0,

      inputs: [],
      outputs: [],
      outputs2: [],
      bool1: false,
      tempdir1: -2,
      tempdir2: -2,

      life: 10,

      rank: -1,

      DIRECTIONS: [
        [0, -1],
        [1, 0],
        [0, 1],
        [-1, 0],
      ],
      // UDLR

      direction: 0,

      board_border: "black",
      board_background: "black",
      snake_col: "red",
      snake_border: "red",
      snake: [
        { x: 150, y: 260 },
        { x: 150, y: 270 },
        { x: 150, y: 280 },
        { x: 150, y: 290 },
      ],
      score: 0,
      combo: 0,
      // True if changing direction
      changing_direction: true,
      // Horizontal velocity
      food_x: -100,
      food_y: -100,
      dx: 0,
      // Vertical velocity
      dy: -10,
      // Get the canvas element
      snakeboard: "test",
      snakeboard_ctx: "test",
    };
  },
  methods: {
    Main() {
      if (this.life <= 0) {
        return;
      }

      // if (!this.Step(this.direction)) {
      //   console.log("collapsed");
      // }

      // if (this.Has_game_ended()) {
      //   console.log("has ended");
      //   this.ResetTile();
      //   clearInterval(this.testbool);
      // }

      // console.log(this.dx, this.dy);

      this.changing_direction = true;
      setTimeout(() => {
        if (this.Has_game_ended() || this.timer >= 500) {
          // console.log("has ended");
          this.combo = 0;
          this.life -= 1;
          this.timer = 0;
          this.ResetTile();
        }
        // console.log(this.snake[0], this.snake[1]);
        this.timer += 5;
        this.GetGenes();
        this.Move_Snake();
        this.Clear_board();
        this.DrawFood();
        this.DrawSnake();

        // console.log(this.inputs, this.outputs, this.outputs2, this.direction);

        // console.log(this.dx, this.dy);
        // Repeat
        this.Main();
      }, this.speed);
    },

    ResetTile() {
      this.snake = [
        { x: 150, y: 260 },
        { x: 150, y: 270 },
        { x: 150, y: 280 },
        { x: 150, y: 290 },
      ];
      this.dx = 0;
      this.dy = -10;
      this.Gen_Food();
    },

    SetWay(dir) {
      this.dx = this.DIRECTIONS[dir][0] * 10;
      this.dy = this.DIRECTIONS[dir][1] * 10;
    },

    GetGenes() {
      // console.log("getinputs");
      this.inputs = this.GetInputs();
      if (this.difficulty == 3) {
        this.outputs = this.ForwardHard(this.inputs);
      } else {
        this.outputs = this.Forward(this.inputs);
      }

      this.SetOutPuts(this.ArgMax(this.outputs));
      this.SetWay(this.direction);
    },

    SetOutPuts(num) {
      var resnum = num;

      if (num == 1) {
        resnum = (this.direction + 3) % 4;
        this.direction = resnum;
      }
      if (num == 2) {
        resnum = (this.direction + 1) % 4;
        this.direction = resnum;
      }
      // this.direction = resnum;
    },

    // draw a border around the canvas
    Clear_board() {
      //  Select the colour to fill the drawing
      this.snakeboard_ctx.fillStyle = this.board_background;
      //  Select the colour for the border of the canvas
      this.snakeboard_ctx.strokestyle = this.board_border;
      // Draw a "filled" rectangle to cover the entire canvas
      this.snakeboard_ctx.fillRect(
        0,
        0,
        this.snakeboard.width,
        this.snakeboard.height
      );
      // Draw a "border" around the entire canvas
      this.snakeboard_ctx.strokeRect(
        0,
        0,
        this.snakeboard.width,
        this.snakeboard.height
      );
      return {};
    },

    // Draw the snake on the canvas
    DrawSnake() {
      // Draw each part
      this.snake.forEach((snakePart) => {
        // Set the colour of the snake part
        this.snakeboard_ctx.fillStyle = this.snake_col;
        // Set the border colour of the snake part
        this.snakeboard_ctx.strokestyle = this.snake_border;
        // Draw a "filled" rectangle to represent the snake part at the coordinates
        // the part is located
        this.snakeboard_ctx.fillRect(snakePart.x, snakePart.y, 10, 10);
        // Draw a border around the snake part
        this.snakeboard_ctx.strokeRect(snakePart.x, snakePart.y, 10, 10);
      });
    },

    DrawFood() {
      this.snakeboard_ctx.fillStyle = "lightgreen";
      this.snakeboard_ctx.strokestyle = "darkgreen";
      this.snakeboard_ctx.fillRect(this.food_x, this.food_y, 10, 10);
      this.snakeboard_ctx.strokeRect(this.food_x, this.food_y, 10, 10);
    },

    Has_game_ended() {
      for (let i = 4; i < this.snake.length; i++) {
        if (
          this.snake[i].x === this.snake[0].x &&
          this.snake[i].y === this.snake[0].y
        )
          return true;
      }
      const hitLeftWall = this.snake[0].x < 0;
      const hitRightWall = this.snake[0].x > this.snakeboard.width - 10;
      const hitToptWall = this.snake[0].y < 0;
      const hitBottomWall = this.snake[0].y > this.snakeboard.height - 10;
      if (
        (hitLeftWall || hitRightWall || hitToptWall || hitBottomWall) == true
      ) {
        this.combo = 0;
        this.life -= 1;
      }
      return hitLeftWall || hitRightWall || hitToptWall || hitBottomWall;
    },

    Random_food(min, max) {
      return Math.round((Math.random() * (max - min) + min) / 10) * 10;
    },

    Gen_Food() {
      // Generate a random number the food x-coordinate
      if (this.testforfood) {
        this.food_x = 120;
        this.food_y = 120;
        this.testforfood = false;
      } else {
        this.food_x = this.Random_food(0, this.snakeboard.width - 10);
        // Generate a random number for the food y-coordinate
        this.food_y = this.Random_food(0, this.snakeboard.height - 10);
        // if the new food location is where the snake currently is, generate a new food location
      }
      this.snake.forEach((part) => {
        const has_eaten = part.x == this.food_x && part.y == this.food_y;
        if (has_eaten) this.Gen_Food();
      });
    },

    Relu(x) {
      var res = [];
      var xx = [];
      var xxx = 0;
      for (var i = 0; i < x[0].length; i++) {
        xxx = x[0][i] * (x[0][i] >= 0);

        xx.push(xxx);
      }
      res.push(xx);
      return res;
    },

    SoftMax(x) {
      var res = [];
      var xx = [];
      var xxx = 0;
      var exptmp = 0;
      for (var z = 0; z < x[0].length; z++) {
        var expfrag = Math.exp(x[0][z]);
        exptmp += expfrag;
      }

      for (var i = 0; i < x[0].length; i++) {
        xxx = Math.exp(x[0][i]) / exptmp;
        xx.push(xxx);
      }
      res.push(xx);
      return res;
    },

    MatSum(a) {
      var l = a[0].length;
      var arr1 = Array(l);

      for (var b = 0; b < l; b++) {
        arr1[b] = 0;
      }
      var sol = Array(1);
      sol[0] = arr1;
      // var sol = Array(l);

      for (var v = 0; v < a.length; v++) {
        for (var c = 0; c < l; c++) {
          sol[0][c] += a[v][c];
        }
      }

      return a;
    },

    ForwardHard(inputs) {
      var net1 = this.MatMul(inputs, AI.D1.N1.w1);

      var net11 = this.Relu(net1);

      var net2 = this.MatMul(net11, AI.D1.N1.w2);

      var net22 = this.Relu(net2);

      var net3 = this.MatMul(net22, AI.D1.N1.w3);

      var net33 = this.Relu(net3);

      var net4 = this.MatMul(net33, AI.D1.N1.w4);

      var net44 = this.SoftMax(net4);

      return net44;
    },
    Forward(inputs) {
      var net1 = this.MatMul(inputs, AI.D3.N2.w1);

      var net11 = this.Relu(net1);

      var net2 = this.MatMul(net11, AI.D3.N2.w2);

      var net22 = this.Relu(net2);

      var net3 = this.MatMul(net22, AI.D3.N2.w3);

      var net33 = this.Relu(net3);

      var net4 = this.MatMul(net33, AI.D3.N2.w4);

      var net44 = this.SoftMax(net4);

      return net44;
    },

    GuessHeadBool(inputarr) {
      for (var jj = 0; jj < this.snake.length; jj++) {
        if (
          inputarr[0] == this.snake[jj].x &&
          inputarr[1] == this.snake[jj].y
        ) {
          return true;
        }
      }
      return false;
    },

    GetInputs() {
      var head = [this.snake[0].x, this.snake[0].y];
      var result = [1, 1, 1, 0, 0, 0];

      var possible_dirs = [
        this.DIRECTIONS[this.direction],
        this.DIRECTIONS[(this.direction + 3) % 4],
        this.DIRECTIONS[(this.direction + 1) % 4],
      ];

      for (var i = 0; i < possible_dirs.length; i++) {
        for (var j = 0; j < 5; j++) {
          var guess_head = [
            head[0] + possible_dirs[i][0] * (j + 1) * 10,
            head[1] + possible_dirs[i][1] * (j + 1) * 10,
          ];

          if (
            guess_head[0] < 0 ||
            guess_head[0] >= 300 ||
            guess_head[1] < 0 ||
            guess_head[1] >= 300 ||
            this.GuessHeadBool(guess_head)
          ) {
            // console.log("if checked");
            result[i] = j * 0.2;
            break;
          }
        }
      }
      this.bool1 = false;
      // console.log(result);
      for (var pix = 0; pix < 4; pix++) {
        // console.log(this.snake[pix], this.food_x, this.food_y);
        if (
          this.snake[pix].x == this.food_x ||
          this.snake[pix].y == this.food_y
        ) {
          // console.log("getfind");
          this.bool1 = true;
          break;
        }
      }
      // console.log(head, possible_dirs);

      var test1 = this.NpSum(head, possible_dirs[1]);
      var test2 = this.NpSum([this.food_x, this.food_y], possible_dirs[1]);

      if (
        this.bool1 &&
        this.NpSum(head, possible_dirs[0]) <=
          this.NpSum([this.food_x, this.food_y], possible_dirs[0])
      ) {
        result[3] = 1;
      }
      if (test1 < test2) {
        // console.log("left");
        result[4] = 1;
      } else {
        // console.log("right");
        result[5] = 1;
      }
      var tmp_res = [];
      tmp_res.push(result);
      // console.log(tmp_res);
      return tmp_res;
    },

    NpSum(arr1, arr2) {
      var sum = 0;
      for (var cal = 0; cal < arr1.length; cal++) {
        sum += arr1[cal] * arr2[cal];
      }
      return sum;
    },

    MatMul(a, b) {
      var mul = [];
      var lena = a[0].length;
      var lenb = b[0].length;
      var row = [];
      for (var i = 0; i < lenb; i++) {
        var x = 0;
        for (var j = 0; j < lena; j++) {
          x += Math.round(a[0][j] * b[j][i] * 1e8) / 1e8;
        }

        row.push(x);
      }

      mul.push(row);

      return mul;
    },

    Move_Snake() {
      // Create the new Snake's head
      const head = {
        x: this.snake[0].x + this.dx,
        y: this.snake[0].y + this.dy,
      };
      // Add the new head to the beginning of snake body
      this.snake.unshift(head);
      const has_eaten_food =
        this.snake[0].x === this.food_x && this.snake[0].y === this.food_y;
      if (has_eaten_food) {
        // Increase score, combo
        this.combo += 1;
        this.score += 1 + this.combo;
        this.timer = 0;

        // Generate new food location
        this.Gen_Food();
      } else {
        // Remove the last part of snake body
        this.snake.pop();
      }
    },
    ArgMax(arr) {
      var max = -999;
      var ans = 0;
      for (var i = 0; i < arr[0].length; i++) {
        // console.log(i);
        if (arr[0][i] > max) {
          max = arr[0][i];
          ans = i;
        }
      }
      return ans;
    },
    ChangeDiff() {
      if (this.difficulty == 3) {
        this.difficulty = 2;
      } else {
        this.difficulty = 3;
      }
    },

    SnakeStart() {
      this.life = 9999;
      this.Main();

      this.Gen_Food();
    },
    SnakeStop() {
      this.life = 0;
      EventBus.$emit("ai-completed", [this.score]);
    },
    SubmitGameData() {
      // not using now
      axios
        .post("http://localhost:8080/Play/snake/", {
          gaId: 1,
          plLevel: 5,
          plscore: this.score,
          uiPk: 27,
        })
        .then((res) => {
          this.rank = res.data.rank;
          alert(`${this.rank}위에 랭크인 하셨습니다. 축하드립니다.`);
          // console.log(res);
        })
        .catch((err) => {
          console.error(err);
        });
    },
  },
  mounted() {
    // document.addEventListener("keydown", this.Change_Direction);

    this.snakeboard = document.getElementById("snakeboardAI");
    this.snakeboard_ctx = this.snakeboard.getContext("2d");
  },
};
</script>