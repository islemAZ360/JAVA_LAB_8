# Graph Report - src  (2026-06-01)

## Corpus Check
- 67 files · ~15,403 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 442 nodes · 853 edges · 21 communities detected
- Extraction: 61% EXTRACTED · 39% INFERRED · 0% AMBIGUOUS · INFERRED: 330 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- [[_COMMUNITY_Community 0|Community 0]]
- [[_COMMUNITY_Community 1|Community 1]]
- [[_COMMUNITY_Community 2|Community 2]]
- [[_COMMUNITY_Community 3|Community 3]]
- [[_COMMUNITY_Community 4|Community 4]]
- [[_COMMUNITY_Community 5|Community 5]]
- [[_COMMUNITY_Community 6|Community 6]]
- [[_COMMUNITY_Community 7|Community 7]]
- [[_COMMUNITY_Community 8|Community 8]]
- [[_COMMUNITY_Community 9|Community 9]]
- [[_COMMUNITY_Community 10|Community 10]]
- [[_COMMUNITY_Community 11|Community 11]]
- [[_COMMUNITY_Community 12|Community 12]]
- [[_COMMUNITY_Community 13|Community 13]]
- [[_COMMUNITY_Community 14|Community 14]]
- [[_COMMUNITY_Community 15|Community 15]]
- [[_COMMUNITY_Community 16|Community 16]]
- [[_COMMUNITY_Community 17|Community 17]]
- [[_COMMUNITY_Community 18|Community 18]]
- [[_COMMUNITY_Community 19|Community 19]]
- [[_COMMUNITY_Community 20|Community 20]]

## God Nodes (most connected - your core abstractions)
1. `getMessage()` - 35 edges
2. `HumanBeing` - 33 edges
3. `CollectionManager` - 31 edges
4. `HumanBeingBuilder` - 13 edges
5. `Account` - 10 edges
6. `HumanBeingChecker` - 10 edges
7. `PostgresCollectionRepository` - 10 edges
8. `HumanBeingFileManager` - 8 edges
9. `ServerMain` - 8 edges
10. `PostgresAccountRepository` - 8 edges

## Surprising Connections (you probably didn't know these)
- `Account` --implements--> `Serializable`  [EXTRACTED]
  server\auth\Account.java →   _Bridges community 1 → community 7_
- `HumanBeing` --implements--> `Serializable`  [EXTRACTED]
  common\models\HumanBeing.java →   _Bridges community 7 → community 2_
- `PostgresAccountRepository` --implements--> `AccountRepository`  [EXTRACTED]
  server\auth\PostgresAccountRepository.java →   _Bridges community 1 → community 10_
- `AddIfMaxCommand` --implements--> `Command`  [EXTRACTED]
  server\commands\AddIfMaxCommand.java →   _Bridges community 0 → community 6_
- `HelpCommand` --implements--> `Command`  [EXTRACTED]
  server\commands\HelpCommand.java →   _Bridges community 0 → community 9_

## Communities

### Community 0 - "Community 0"
Cohesion: 0.04
Nodes (13): AddCommand, ClearCommand, Command, ExecuteScriptCommand, FilterGreaterThanCarCommand, FilterLessThanMinutesOfWaitingCommand, InfoCommand, RegisterCommand (+5 more)

### Community 1 - "Community 1"
Cohesion: 0.06
Nodes (8): Account, AccountService, ConnectionState, LoginCommand, LogoutCommand, PostgresAccountRepository, RequireAuthorization, UserSession

### Community 2 - "Community 2"
Cohesion: 0.19
Nodes (1): HumanBeing

### Community 3 - "Community 3"
Cohesion: 0.08
Nodes (7): AccountInputHandler, BooleanBuilder, ClientMain1, ClientMain2, InputManager, LongBuilder, ReconnectingEffectManager

### Community 4 - "Community 4"
Cohesion: 0.12
Nodes (4): CollectionManager, getLongFlag(), getShortFlag(), matches()

### Community 5 - "Community 5"
Cohesion: 0.13
Nodes (3): HumanBeingBuilder, HumanBeingChecker, getMessage()

### Community 6 - "Community 6"
Cohesion: 0.11
Nodes (3): AddIfMaxCommand, AddIfMinCommand, FilterContainsNameCommand

### Community 7 - "Community 7"
Cohesion: 0.09
Nodes (5): Car, Coordinates, Request, Response, Serializable

### Community 8 - "Community 8"
Cohesion: 0.1
Nodes (7): CommandFileManager, FileManager, FileManager, HandleCommandFile, HandleHumanBeingFile, HumanBeingFileManager, HumanBeingReader

### Community 9 - "Community 9"
Cohesion: 0.1
Nodes (3): HelpCommand, PostgresCollectionRepository, SessionManager

### Community 10 - "Community 10"
Cohesion: 0.14
Nodes (5): AccountRepository, AutoCloseable, RequestSender, Serializer, ServerMain

### Community 11 - "Community 11"
Cohesion: 0.12
Nodes (3): CommandManager, CommandSuggester, RequestHandler

### Community 12 - "Community 12"
Cohesion: 0.33
Nodes (2): ClientMain, Terminal

### Community 13 - "Community 13"
Cohesion: 0.25
Nodes (1): CollectionRepository

### Community 14 - "Community 14"
Cohesion: 0.29
Nodes (1): AccountRepository

### Community 15 - "Community 15"
Cohesion: 0.33
Nodes (1): HandleHumanBeingFile

### Community 16 - "Community 16"
Cohesion: 0.4
Nodes (1): Command

### Community 17 - "Community 17"
Cohesion: 0.5
Nodes (2): DatabaseException, RuntimeException

### Community 18 - "Community 18"
Cohesion: 0.67
Nodes (1): HandleCommandFile

### Community 19 - "Community 19"
Cohesion: 1.0
Nodes (1): RequireAuthorization

### Community 20 - "Community 20"
Cohesion: 1.0
Nodes (1): Const

## Knowledge Gaps
- **2 isolated node(s):** `RequireAuthorization`, `Const`
  These have ≤1 connection - possible missing edges or undocumented components.
- **Thin community `Community 2`** (38 nodes): `.execute()`, `.isCool()`, `.update()`, `.updateInDatabaseAndMemory()`, `.getX()`, `.getY()`, `HumanBeing`, `.getCar()`, `.getCoordinates()`, `.getCreationDate()`, `.getId()`, `.getImpactSpeed()`, `.getMinutesOfWaiting()`, `.getName()`, `.getSoundtrackName()`, `.getUserId()`, `.getWeaponType()`, `.HumanBeing()`, `.isHasToothpick()`, `.isRealHero()`, `.setCar()`, `.setCoordinates()`, `.setHasToothpick()`, `.setId()`, `.setImpactSpeed()`, `.setMinutesOfWaiting()`, `.setName()`, `.setRealHero()`, `.setSoundtrackName()`, `.setUserId()`, `.setValueCreationDate()`, `.setWeaponType()`, `.toString()`, `.extractInfo()`, `.add()`, `.update()`, `.getName()`, `.getUserId()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 12`** (12 nodes): `ClientMain.java`, `Terminal.java`, `ClientMain`, `.isSocketAlive()`, `.log()`, `.main()`, `.reconnect()`, `.triggerReconnect()`, `Terminal`, `.log()`, `.startAnimation()`, `.stopAnimation()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 13`** (8 nodes): `CollectionRepository`, `.add()`, `.clear()`, `.generateNextId()`, `.loadAll()`, `.remove()`, `.update()`, `CollectionRepository.java`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 14`** (7 nodes): `AccountRepository`, `.changePassword()`, `.existsByUsername()`, `.findByUsername()`, `.save()`, `.updateStatus()`, `AccountRepository.java`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 15`** (6 nodes): `HandleHumanBeingFile.java`, `HandleHumanBeingFile`, `.readFileAndLoadHumanBeing()`, `.save()`, `.saveAll()`, `.saveOne()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 16`** (5 nodes): `Command`, `.execute()`, `.getDescription()`, `.getName()`, `Command.java`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 17`** (4 nodes): `DatabaseException`, `.DatabaseException()`, `RuntimeException`, `DatabaseException.java`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 18`** (3 nodes): `HandleCommandFile.java`, `HandleCommandFile`, `.readFileAndRunScripts()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 19`** (2 nodes): `RequireAuthorization.java`, `RequireAuthorization`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 20`** (2 nodes): `Const.java`, `Const`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `getMessage()` connect `Community 5` to `Community 0`, `Community 1`, `Community 2`, `Community 3`, `Community 4`, `Community 6`, `Community 8`, `Community 10`, `Community 11`, `Community 12`?**
  _High betweenness centrality (0.245) - this node is a cross-community bridge._
- **Why does `HumanBeing` connect `Community 2` to `Community 8`, `Community 11`, `Community 4`, `Community 7`?**
  _High betweenness centrality (0.077) - this node is a cross-community bridge._
- **Why does `CollectionManager` connect `Community 4` to `Community 2`, `Community 3`, `Community 6`, `Community 8`, `Community 9`?**
  _High betweenness centrality (0.070) - this node is a cross-community bridge._
- **Are the 34 inferred relationships involving `getMessage()` (e.g. with `.main()` and `.reconnect()`) actually correct?**
  _`getMessage()` has 34 INFERRED edges - model-reasoned connections that need verification._
- **What connects `RequireAuthorization`, `Const` to the rest of the system?**
  _2 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 0` be split into smaller, more focused modules?**
  _Cohesion score 0.04 - nodes in this community are weakly interconnected._
- **Should `Community 1` be split into smaller, more focused modules?**
  _Cohesion score 0.06 - nodes in this community are weakly interconnected._