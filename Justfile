set shell := ["bash", "-lc"]

default:
  @just --list

lint:
  @./gradlew lint

test:
  @./gradlew test

detekt:
  @if ./gradlew -q help --task detekt >/dev/null 2>&1; then ./gradlew detekt; else echo 'skip: detekt'; fi

ktlint:
  @if ./gradlew -q help --task ktlintCheck >/dev/null 2>&1; then ./gradlew ktlintCheck; elif ./gradlew -q help --task ktlint >/dev/null 2>&1; then ./gradlew ktlint; else echo 'skip: ktlint'; fi

assemble:
  @./gradlew assembleDebug

verify: lint test detekt ktlint
