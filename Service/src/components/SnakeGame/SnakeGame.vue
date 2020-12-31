<template>
  <div>
    <body>
      <h3>Human</h3>
      <p>Score: {{ score }}</p>
      <p>Combo: {{ combo }}</p>
      <canvas
        id="snakeboard"
        v-on:keydown="Change_Direction(event)"
        width="300"
        height="300"
      ></canvas>
    </body>
  </div>
</template>
<script>
// import axios from "axios";
import EventBus from "./EventBus";

export default {
  name: "SnakeGame",
  data() {
    return {
      life: 9999,
      speed: 30,
      rank: -1,

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
      changing_direction: false,
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
      if (this.life == 0) {
        return;
      }

      if (this.Has_game_ended()) {
        this.snake = [
          { x: 150, y: 260 },
          { x: 150, y: 270 },
          { x: 150, y: 280 },
          { x: 150, y: 290 },
        ];
        this.dx = 0;
        this.dy = -10;
        this.Gen_Food();
      }
      this.changing_direction = false;
      setTimeout(() => {
        this.Clear_board();
        this.DrawFood();
        this.Move_Snake();
        this.DrawSnake();
        // Repeat
        this.Main();
      }, this.speed);
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
      if (hitLeftWall || hitRightWall || hitToptWall || hitBottomWall == true) {
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
      this.food_x = this.Random_food(0, this.snakeboard.width - 10);
      // Generate a random number for the food y-coordinate
      this.food_y = this.Random_food(0, this.snakeboard.height - 10);
      // if the new food location is where the snake currently is, generate a new food location

      //   this.snake.forEach(function Has_snake_eaten_food(part) {
      this.snake.forEach((part) => {
        const has_eaten = part.x == this.food_x && part.y == this.food_y;
        if (has_eaten) this.Gen_Food();
      });
    },

    Change_Direction(event) {
      const LEFT_KEY = 37;
      const RIGHT_KEY = 39;
      const UP_KEY = 38;
      const DOWN_KEY = 40;

      // Prevent the snake from reversing

      if (this.changing_direction) return;
      this.changing_direction = true;
      let keyPressed = 38;
      if (event != undefined) {
        keyPressed = event.keyCode;
      }

      const goingUp = this.dy === -10;
      const goingDown = this.dy === 10;
      const goingRight = this.dx === 10;
      const goingLeft = this.dx === -10;
      if (keyPressed === LEFT_KEY && !goingRight) {
        this.dx = -10;
        this.dy = 0;
      }
      if (keyPressed === UP_KEY && !goingDown) {
        this.dx = 0;
        this.dy = -10;
      }
      if (keyPressed === RIGHT_KEY && !goingLeft) {
        this.dx = 10;
        this.dy = 0;
      }
      if (keyPressed === DOWN_KEY && !goingUp) {
        this.dx = 0;
        this.dy = 10;
      }
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

        // Generate new food location
        this.Gen_Food();
      } else {
        // Remove the last part of snake body
        this.snake.pop();
      }
    },
    SnakeStart() {
      this.Main();

      this.Gen_Food();
    },
    SnakeStop() {
      this.life = 0;
      EventBus.$emit("human-completed", [this.score]);
    },
    HumanCompleted() {
      EventBus.$emit("human-completed", [this.score]);
    },
  },
  mounted() {
    document.addEventListener("keydown", this.Change_Direction);

    this.snakeboard = document.getElementById("snakeboard");
    this.snakeboard_ctx = this.snakeboard.getContext("2d");
  },
};
</script>
