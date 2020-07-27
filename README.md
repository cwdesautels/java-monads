# Java Monads

## Manual release ##
Make sure that both develop and master branches are up to date and clean.
1. `git checkout master && git clean -df && git pull`
2. `git checkout develop && git clean -df && git pull`
3. `mvn --batch-mode --update-snapshots jgitflow:release-start -DenableSshAgent`
4. `mvn --batch-mode --update-snapshots help:evaluate -Dexpression=project.version -DforceStdout=true -Doutput=.tag`
5. `mvn --batch-mode --update-snapshots jgitflow:release-finish -DenableSshAgent -DscmCommentPrefix="[skip ci] Generated commit - " -DskipTests -DskipITs`
6. `git checkout master`
7. `git commit --allow-empty -m "[v$(cat .tag | cut -d- -f1)] Master build"`
8. `git push --no-verify --tags`
9. `git push --no-verify origin develop master`
