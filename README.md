# One Word Story - **CSC207** project

## General Description

- One Word Story is a common creativity game where a group of participants take turns contributing a word
to a story with no communication on the story's heading. We thought this would be a perfect gmae 
to port online, where anyone can visit the website and contribute words to a story with random
people around the world.
- To play the game, you would navigate to the website, enter a display name, and hit play! You would be connected to other players and could begin contributing words to a story. When a game ends, the story is saved to the blog. Anyone can view the blog of all stories, like them, suggest titles, or add comments!


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

* In Pull Game Ended (PGE) use-case, we save RecursiveSymboledIntegerHashMap (RSIH) and pass it to output to the adapters layer despite it being an entity. Although RSIH is an entity, it does not have any ties to actual fundamental entities; in fact, it is a utility data structure which is effectively independent of entities. This being said, instead of pointlessly transferring it to a duplicate object by a different name, we will pass it directly. We do not believe that this crosses layers. In fact, we even store these classes in a separate "util" folder.
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

## Future updates

1. *Shutdown Messages:* Design a nice structure for use cases to send a message back to the shutdown server use case to display in the view’s terminal (e.g “Sw shutdown successfully”). This would require some cleanup and potentially putting shutdown on its own thread to not block the view
2. *Time and Round-based Games:* Game ends when either a timer runs out or a certain number of turns pass
3. *Kicking AFK players:* Keep integer count of how many turns a player missed. If player missed a certain number of turns, remove them
4. *Multiple Public Lobbies and Matchmaking Settings:* Settings can be selected and sent to the server when joining a public lobby so that you are sorted into a lobby with those desired settings. Weekly Topic. Join a public lobby where the topic is the “weekly topic”. These stories will be filed separately when complete and there will be a “best weekly topic story” vote.
5. *Lobby Restoration:* Constantly save lobby information to the repository so that if the server ever crashes, previously active lobbies could be restored. Obviously the players would all have been disconnected if the server crashed. We’d need to decide the exact protocol.
6. *Comment Replies:* As the title implies, comments may be replied to!


