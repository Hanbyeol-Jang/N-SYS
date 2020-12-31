import pygame
import random
import numpy as np
from copy import deepcopy
from snake import Snake, SCREEN_SIZE, PIXEL_SIZE
from genome import Genome


N_POPULATION = 50
N_BEST = 5
N_CHILDREN = 5
PROB_MUTATION = 0.4

dif = -1


print('ai의 난이도를 설정하세요 (1~3)')
dif = int(input())
while (0 > dif or dif > 3):
    print('다시 입력하세요 (1~3)')
    dif = int(input())


pygame.init()
pygame.font.init()
s = pygame.display.set_mode(
    (SCREEN_SIZE * PIXEL_SIZE, SCREEN_SIZE * PIXEL_SIZE))
pygame.display.set_caption('Snake')

# generate 1st population
genomes = [Genome() for _ in range(N_POPULATION)]

# load genomes

if dif == 1:
    best_genomes = None
if dif == 2:
    genomes_old = np.load(
        './difficulty/snake_200.npy', allow_pickle=True)
    genomes = genomes_old
    best_genomes = deepcopy(genomes[0])
if dif == 3:
    genomes_old = np.load(
        './difficulty/snake_400.npy', allow_pickle=True)
    genomes = genomes_old
    best_genomes = deepcopy(genomes[0])


n_gen = 0
while True:
    n_gen += 1
    # for z in genomes:
    #     print(z.__init__)

    for i, genome in enumerate(genomes):
        snake = Snake(s, genome=genome)
        fitness, score = snake.run()

        genome.fitness = fitness
        genome.score = score

        # print('Generation #%s, Genome #%s, Fitness: %s, Score: %s' %
        #       (n_gen, i, fitness, score))

    if best_genomes is not None:
        # genomes.extend(best_genomes)
        temp2 = np.append(genomes, best_genomes)
        genomes = deepcopy(temp2)

        # np.concatenate((genomes, best_genomes), axis=0)
        # np.vstack((genomes, best_genomes))

    # genomes.sort(key=lambda x: x.fitness, reverse=True)
    genomes = sorted(genomes, key=lambda x: x.fitness, reverse=True)
    # if genomes[0].fitness <= 0 and n_gen >= 150:
    #     print('~~')
    #     sorted(genomes, key=lambda x: x.score, reverse=True)

    # print('===== Generaton #%s\tScore %s , Fitness %s =====' %
    #       (n_gen, genomes[0].score, genomes[0].fitness))

    print('===== Generaton #%s\tFitness %s =====' %
          (n_gen, genomes[0].fitness))
    # if genomes[0].fitness < 0:
    #     pygame.quit()

    # print(genomes[0].w1, genomes[0].w2)

    # if n_gen >= 1000:
    #     np.save('C:\SSAFY\data\snake_save_'+str(1000), genomes)
    #     break

    # best_genomes = deepcopy(genomes[:N_BEST])
    best_genomes = genomes[:N_BEST].copy()

    # crossover 다음 세대 제작
    for i in range(N_CHILDREN):
        new_genome = deepcopy(best_genomes[0])
        # new_genome = best_genomes[0].copy()
        a_genome = random.choice(best_genomes)
        b_genome = random.choice(best_genomes)

        cut = random.randint(0, new_genome.w1.shape[1])
        new_genome.w1[i, :cut] = a_genome.w1[i, :cut]
        new_genome.w1[i, cut:] = b_genome.w1[i, cut:]

        cut = random.randint(0, new_genome.w2.shape[1])
        new_genome.w2[i, :cut] = a_genome.w2[i, :cut]
        new_genome.w2[i, cut:] = b_genome.w2[i, cut:]

        cut = random.randint(0, new_genome.w3.shape[1])
        new_genome.w3[i, :cut] = a_genome.w3[i, :cut]
        new_genome.w3[i, cut:] = b_genome.w3[i, cut:]

        cut = random.randint(0, new_genome.w4.shape[1])
        new_genome.w4[i, :cut] = a_genome.w4[i, :cut]
        new_genome.w4[i, cut:] = b_genome.w4[i, cut:]

        # best_genomes.append(new_genome)
        temp = np.append(best_genomes, new_genome)
        best_genomes = deepcopy(temp)

    # mutation - 돌연변이 제작

    genomes = []
    for i in range(int(N_POPULATION / (N_BEST + N_CHILDREN))):
        for bg in best_genomes:
            new_genome = deepcopy(bg)
            # new_genome = bg.copy()

            mean = 20
            stddev = 10

            if random.uniform(0, 1) < PROB_MUTATION:
                new_genome.w1 += new_genome.w1 * \
                    np.random.normal(mean, stddev, size=(6, 10)) / \
                    100 * np.random.randint(-1, 2, (6, 10))
            if random.uniform(0, 1) < PROB_MUTATION:
                new_genome.w2 += new_genome.w2 * \
                    np.random.normal(mean, stddev, size=(10, 20)) / \
                    100 * np.random.randint(-1, 2, (10, 20))
            if random.uniform(0, 1) < PROB_MUTATION:
                new_genome.w3 += new_genome.w3 * \
                    np.random.normal(mean, stddev, size=(20, 10)) / \
                    100 * np.random.randint(-1, 2, (20, 10))
            if random.uniform(0, 1) < PROB_MUTATION:
                new_genome.w4 += new_genome.w4 * \
                    np.random.normal(mean, stddev, size=(10, 3)) / \
                    100 * np.random.randint(-1, 2, (10, 3))

            genomes.append(new_genome)
