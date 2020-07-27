# Java Monads

## Manual release ##
Make sure that both develop and master branches are up to date and clean.
1. `git checkout master && git clean -df && git pull`
2. `git checkout develop && git clean -df && git pull`
3. `git remote set-url origin https://github.com/cwdesautels/java-monads.git`
4. `mvn --batch-mode --update-snapshots jgitflow:release-start`
5. `mvn --batch-mode --update-snapshots help:evaluate -Dexpression=project.version -DforceStdout=true -Doutput=.tag`
6. `mvn --batch-mode --update-snapshots jgitflow:release-finish -DscmCommentPrefix="[skip ci] Generated commit - " -DskipTests -DskipITs`
7. `git checkout master`
8. `git commit --allow-empty -m "[v$(cat .tag | cut -d- -f1)] Master build"`
9. `git remote set-url origin git@github.com:cwdesautels/java-monads.git`
10. `git push --no-verify --tags`
11. `git push --no-verify origin develop master`
