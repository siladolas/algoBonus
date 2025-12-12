# Our Journey

After all what happened during the lab exam, that exam everybody nearly failed, we
decided to study those String matching algorithms to not fail during the midterm of
the lecture. So, knowing Boyer-Moore theoretically was our advantage. We together
implemented the algorithm together.

## GoCrazy Algorithm – First Design

About GoCrazy algorithm, we decided to create a hybrid algorithm which uses features
of Boyer-Moore’s Bad Character rule and KMP’s prefix logic. But after some tests on
different computers, we realised that the algorithm was extremely costly by both space
and time because we used 2 HashMap, 1 ArrayList and 1 HashSet. This led us finding
another approach to the algorithm.

## Optimization Phase

So we designed an hybrid algorithm of Horspool, Boyer-Moore and some optimization
techniques some from Sunday algorithm and we used int[256] array structure with
modulo instead of HashMap and other structures, this improved our time performance
especially for bulky old processors. While developing GoCrazy algorithm, we used
Gemini model to make our algorithm effectively.

Also, first version of GoCrazy algorithm that we implemented helped us to realise that
we should not use pre-defined data structures that much, we also decided to update the
Boyer-Moore algorithm again.

## Pre-analysis Phase

In Pre-analysis part, we did not really understand what to do because in many test cases
the winner was Naive algorithm, which we thought there must be something wrong about
our pre-analysis.

After communicating with Mr. Öztürk, we understood that we must not think about
only the test cases in the project file, we realised that we must think about different
algorithms with different length, patterns and other features.

## Final Pre-analysis Strategy

So, we designed our Pre-analysis strategy to work in many different conditions. With
the help of Claude Sonnet 4.5, we designed a pretty good pre-analysis technique.
