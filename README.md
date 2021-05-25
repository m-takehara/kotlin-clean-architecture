# kotlin-clean-architecture

## 概要

Clean Architecture を意識しながら、以下のライブラリや F/W を使って適当な Web アプリを作るリポジトリ

* Ktor: Web アプリケーション構築用 F/W
* Koin: Dependency Injection 用 F/W
* Exposed: OR Mapper
* kotest: Testing F/W

## 動作環境

```text
$ java -version
openjdk version "11.0.10" 2021-01-19 LTS
OpenJDK Runtime Environment Corretto-11.0.10.9.1 (build 11.0.10+9-LTS)
OpenJDK 64-Bit Server VM Corretto-11.0.10.9.1 (build 11.0.10+9-LTS, mixed mode)

$ mvn -version
Apache Maven 3.6.3

$ docker --version
Docker version 20.10.6, build 370c289

$ gauge version 
Gauge version: 1.1.8
Plugins
-------
html-report (4.0.12)
java (0.7.15)
screenshot (0.0.1)
```

## 環境構築

```shell
export SOURCES_ROOT_DIR=$(pwd)

# リポジトリを Clone
git clone git@github.com:m-takehara/kotlin-clean-architecture.git
git clone git@github.com:m-takehara/kotlin-clean-architecture-environment.git
git clone git@github.com:m-takehara/kotlin-clean-architecture-e2e.git

# データベースを起動
cd "$SOURCES_ROOT_DIR/kotlin-clean-architecture-environment/postgres"
docker compose up -d

# アプリケーションを起動
cd "$SOURCES_ROOT_DIR/kotlin-clean-architecture"
mvn install
mvn exec:java -pl rest -Dexec.mainClass=me.takehara.rest.MainKt

# アプリケーションの単体テストを実行
cd "$SOURCES_ROOT_DIR/kotlin-clean-architecture"
mvn test

# E2E テストを実行
cd "$SOURCES_ROOT_DIR/kotlin-clean-architecture-e2e"
mvn gauge:execute
```

## アプリケーションへのリクエスト送信

```http request
POST http://localhost:8080/users
Content-Type: application/json

{
  "name": "m-takehara",
  "password": "password",
  "mailAddress": "m-takehara@gmail.com"
}

###

GET http://localhost:8080/users/811eb3ab-24fa-4a0f-80a2-f1ac44f274f3
```
