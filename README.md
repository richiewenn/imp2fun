[![CircleCI](https://circleci.com/gh/richiewenn/imp2fun.svg?style=svg)](https://circleci.com/gh/richiewenn/imp2fun)


java code => Ast2Cfg => CfgInEdgesFiller => CfgJumpOptimizer => dominator tree => Dominance frontiers 
=> finding where phi functions are needed => minimal SSA form => AST => haskell

## TODO
- [ ] Get rid of nullable type ```Node?``` in Edge class
- [ ] Start loading testing java code from files and not from multiline string
- [ ] Start working on transformation to SSA form 