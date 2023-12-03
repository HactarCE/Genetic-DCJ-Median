# Genetic-DCJ-Median

Genetic DCJ median genome algorithm & visualization based on <https://journals.plos.org/plosone/article?id=10.1371/journal.pone.0062156>

Supplemental files related to the Drosophila dataset can be found [here](https://drive.google.com/file/d/1scNw5q8LGh3dizSQuIDnH5zLrhRu91p5/view?usp=sharing).

## Input format

- Each genome is one line
- Each line contains a signed permutation of the numbers 1 to n (inclusive)

Example:

```
1 -3 -2 4
1 2 -4 -3
```

## To-do

### DCJ distance (Aaron)

- [ ] generate adjacency graph from signed permutation (include telomeres)
- [ ] compute DCJ distance using $d_{DCJ}(G_1, G_2) = n - (C+I/2)$
  - count cycles + non-cyclic edge paths

### Genetic mutation

- [ ] genetic mixing/crossover function (see paper)
- [ ] apply mutations (see paper)
- [ ] high-level genetic algorithm
  - select individuals based on score

### Data generation (Ethan)

- [ ] make new permutations
- [ ] drosophila

### Visualization (Andrew)

- [ ] the thing in the project proposal
