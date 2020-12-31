import pygame
import os
import random
import numpy as np


# 게임 상수값, fps가 속도 조절치
FPS = 120
SCREEN_SIZE = 30
PIXEL_SIZE = 20
LINE_WIDTH = 1

SCORE = 0

DIRECTIONS = np.array([
    (0, -1),  # UP
    (1, 0),  # RIGHT
    (0, 1),  # DOWN
    (-1, 0)  # LEFT
])


class Snake():
    snake, fruit = None, None

    def __init__(self, s, genome):
        self.genome = genome

        self.s = s
        self.score = SCORE
        self.combo = 0
        self.snake = np.array([[15, 26], [15, 27], [15, 28], [15, 29]])
        self.direction = 0  # UP
        self.place_fruit()
        self.timer = 0
        self.last_fruit_time = 0

        # fitness
        self.fitness = 0.
        self.last_dist = np.inf

    def place_fruit(self, coord=None):
        if coord:
            self.fruit = np.array(coord)
            return

        while True:
            x = random.randint(0, SCREEN_SIZE-1)
            y = random.randint(0, SCREEN_SIZE-1)
            if list([x, y]) not in self.snake.tolist():
                break
        self.fruit = np.array([x, y])

    def step(self, direction):
        old_head = self.snake[0]
        movement = DIRECTIONS[direction]
        new_head = old_head + movement

        if (
            new_head[0] < 0 or
            new_head[0] >= SCREEN_SIZE or
            new_head[1] < 0 or
            new_head[1] >= SCREEN_SIZE or
            new_head.tolist() in self.snake.tolist()
        ):
            # self.fitness -= FPS/2
            return False

        # 목표물에 도달한 경우
        if all(new_head == self.fruit):
            self.last_fruit_time = self.timer
            self.combo += 1
            self.score += 1 + self.combo
            self.fitness += 10
            self.place_fruit()
        else:
            tail = self.snake[-1]
            self.snake = self.snake[:-1, :]

        self.snake = np.concatenate([[new_head], self.snake], axis=0)
        return True

    def get_inputs(self):
        head = self.snake[0]
        result = [1., 1., 1., 0., 0., 0.]

        # check forward, left, right
        possible_dirs = [
            DIRECTIONS[self.direction],  # straight forward
            DIRECTIONS[(self.direction + 3) % 4],  # left
            DIRECTIONS[(self.direction + 1) % 4]  # right
        ]

        # 0 - 1 ... danger - safe
        for i, p_dir in enumerate(possible_dirs):
            # sensor range = 5
            for j in range(5):
                guess_head = head + p_dir * (j + 1)

                if (
                    guess_head[0] < 0 or
                    guess_head[0] >= SCREEN_SIZE or
                    guess_head[1] < 0 or
                    guess_head[1] >= SCREEN_SIZE or
                    guess_head.tolist() in self.snake.tolist()
                ):
                    result[i] = j * 0.2
                    break

        # finding fruit
        # heading straight forward to fruit
        if np.any(head == self.fruit) and np.sum(head * possible_dirs[0]) <= np.sum(self.fruit * possible_dirs[0]):
            result[3] = 1
        # fruit is on the left side
        if np.sum(head * possible_dirs[1]) < np.sum(self.fruit * possible_dirs[1]):
            result[4] = 1
        # fruit is on the right side
        # if np.sum(head * possible_dirs[2]) < np.sum(self.fruit * possible_dirs[2]):
        else:
            result[5] = 1

        return np.array(result)

    def run(self):
        self.fitness = 0

        prev_key = pygame.K_UP

        font = pygame.font.Font('3270Medium.otf', 20)
        font.set_bold(True)
        appleimage = pygame.Surface((PIXEL_SIZE, PIXEL_SIZE))
        appleimage.fill((0, 255, 0))
        img = pygame.Surface((PIXEL_SIZE, PIXEL_SIZE))
        img.fill((255, 0, 0))
        clock = pygame.time.Clock()

        while True:
            self.timer += 0.1
            # 장기적으로 교착상황이 지속되는 경우에 대한 break문이며 유저들이 플레이할 코드에는 주석처리가 필요함
            if self.fitness < -FPS/2 or self.timer - self.last_fruit_time > 0.1 * FPS * 5:
                # if self.fitness < -5 or self.timer - self.last_fruit_time > 0.1 * 45 * 5:
                self.fitness -= FPS/2
                # print('Terminated by Suspecting Infinite Loop')
                break

            clock.tick(FPS)
            for e in pygame.event.get():
                if e.type == pygame.QUIT:
                    pygame.quit()
                elif e.type == pygame.KEYDOWN:

                    # q키로 종료
                    if e.key == pygame.K_q or self.score >= 20:
                        pygame.quit()
                        exit()

                    # 스페이스바로 일시 정지
                    if e.key == pygame.K_SPACE:
                        pause = True
                        while pause:
                            for ee in pygame.event.get():
                                if ee.type == pygame.QUIT:
                                    pygame.quit()
                                elif ee.type == pygame.KEYDOWN:
                                    if ee.key == pygame.K_SPACE:
                                        pause = False
                    if __name__ == '__main__':
                        # CONTROLLER
                        if prev_key != pygame.K_DOWN and e.key == pygame.K_UP:
                            self.direction = 0
                            prev_key = e.key
                        elif prev_key != pygame.K_LEFT and e.key == pygame.K_RIGHT:
                            self.direction = 1
                            prev_key = e.key
                        elif prev_key != pygame.K_UP and e.key == pygame.K_DOWN:
                            self.direction = 2
                            prev_key = e.key
                        elif prev_key != pygame.K_RIGHT and e.key == pygame.K_LEFT:
                            self.direction = 3
                            prev_key = e.key

            # action
            if __name__ != '__main__':
                inputs = self.get_inputs()
                outputs = self.genome.forward(inputs)
                outputs = np.argmax(outputs)

                if outputs == 0:  # straight
                    pass
                elif outputs == 1:  # left
                    self.direction = (self.direction + 3) % 4
                elif outputs == 2:  # right
                    self.direction = (self.direction + 1) % 4

            if not self.step(self.direction):
                break

            # compute fitness
            current_dist = np.linalg.norm(self.snake[0] - self.fruit)
            if self.last_dist > current_dist:
                self.fitness += 1.
            else:
                self.fitness -= 1.5
            self.last_dist = current_dist

            self.s.fill((0, 0, 0))
            pygame.draw.rect(self.s, (255, 255, 255), [
                             0, 0, SCREEN_SIZE*PIXEL_SIZE, LINE_WIDTH])
            pygame.draw.rect(self.s, (255, 255, 255), [
                             0, SCREEN_SIZE*PIXEL_SIZE-LINE_WIDTH, SCREEN_SIZE*PIXEL_SIZE, LINE_WIDTH])
            pygame.draw.rect(self.s, (255, 255, 255), [
                             0, 0, LINE_WIDTH, SCREEN_SIZE*PIXEL_SIZE])
            pygame.draw.rect(self.s, (255, 255, 255), [
                             SCREEN_SIZE*PIXEL_SIZE-LINE_WIDTH, 0, LINE_WIDTH, SCREEN_SIZE*PIXEL_SIZE+LINE_WIDTH])
            for bit in self.snake:
                self.s.blit(img, (bit[0] * PIXEL_SIZE, bit[1] * PIXEL_SIZE))
            self.s.blit(
                appleimage, (self.fruit[0] * PIXEL_SIZE, self.fruit[1] * PIXEL_SIZE))

            # 점수, 콤보 표시
            score_ts = font.render(
                'score : '+str(self.score), False, (255, 255, 255))
            combo_ts = font.render(
                'combo : '+str(self.combo), False, (255, 255, 255))
            self.s.blit(score_ts, (5, 5))
            self.s.blit(combo_ts, (5, 25))
            pygame.display.update()

        # 목숨 하나 종료 시 2개의 값을 리턴
        return self.fitness, self.score


if __name__ == '__main__':
    pygame.init()
    pygame.font.init()
    s = pygame.display.set_mode(
        (SCREEN_SIZE * PIXEL_SIZE, SCREEN_SIZE * PIXEL_SIZE))
    pygame.display.set_caption('Snake')

    while True:
        snake = Snake(s, genome=None)
        fitness, score = snake.run()
        SCORE = score

        print('Fitness: %s, Score: %s' % (fitness, score))
