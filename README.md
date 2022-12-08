# One Word Story - **CSC207** project

## General Description

- A “One Word Story” local game intends to emulate an online game. Due to following 
*Clean Architecture* conventions, transforming this game in an online app is easily achievable, 
but we have made a conscious decision to focus on building the best functionality. 
- The game is played in the following way. One user opens the game, submits the
number of players, their names, certain game parameters, and the game starts for each player 
in a separate window. Players take timed turns submitting words, which contribute to the story. 
After a fixed number of rounds, the game ends and the story is complete.


## Contributors

Early on, we defined 4 core roles which were distributed among the group members. 
*Quality managers* are in charge of keeping track of proper testing, 
*Documentation managers* ensure documentation conventions are satisfied, 
*Architects* work on design, and *Coordinator* organizes/runs meetings and maintains 
GitHub Project management.

| Name                    |            Role(s)            |
|-------------------------|:-----------------------------:|
| Aleksey Panas           |    Coordinator, Architect     | 
| Andrii Serdiuk          |           Architect           | 
| Patrick Fidler          |           Architect           | 
| Mariana Piz             |        Quality Manager        | 
| Alexander Ryabchenko    | Quality/Documentation Manager | 
| Daniel Honciuc Menendez |        Quality Manager        | 
| Sohail Sarkar           |     Documentation Manager     | 
| Joel Mathew             |     Documentation Manager     | 


## Design Patterns and Clean Architecture

In this project, we used the following design patterns. Many of them were implemented after scrupulous process of refactoring.

| Design Pattern |                    Examples in our project                    |
|----------------|:-------------------------------------------------------------:|
| Strategy       |           Validity Checkers for Titles and Comments           | 
| Facade         | ValidityChecker is a facade for Punctuation and Word checkers | 
| Observer       |                      PlayerPoolListener                       | 
| Simple Factory |                         Game, Player                          | 
| Factory Method |                   Response (from Exception)                   | 
| Builder        |                        GameDisplayData                        | 



Although, we aimed to satisfy Clean Architecture design as much as possible, in certain places we made a conscious decision not to adhere to certain rules.

* In Pull Game Ended (PGE) use-case, we save RecursiveSymboledIntegerHashMap (RSIH) and pass it to output to the adapters layer despite it being an entity. Although RSIH is an entity, it does not have any ties to actual fundamental entities; in fact, it is a utility data structure which is completely independent of entities, just as any of the java.util items. This being said, instead of pointlessly transferring it to a duplicate object by a different name, we will pass it directly. We do not believe that this crosses layers. In fact, we even store these classes in a separate "util" folder.
* In the new view model structure, we directly return use-case-layer objects - StoryRepoData, TitleRepoData, CommentRepoData, and the Response object - to the view. this means that the view crosses into the use case layer. This is not unfixable since alternatively, we could create a duplicate object with the same fields. Moreover, there is not much more manipulation needed for the view to display the situation.


## Progress

![100%](https://progress-bar.dev/100/?title=Backend)
![100%](https://progress-bar.dev/100/?title=Intended-Functionality)
![100%](https://progress-bar.dev/100/?title=GUI)

## Installation Instructions

TODO

## Software Specifications

TODO

## Functionality

TODO

